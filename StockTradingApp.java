import java.io.*;
import java.util.*;

class Stock {
    String symbol;
    double price;

    Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }
}

class Transaction {
    String type; // BUY or SELL
    String stockSymbol;
    int quantity;
    double price;

    Transaction(String type, String stockSymbol, int quantity, double price) {
        this.type = type;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
    }
}

class Portfolio {
    Map<String, Integer> holdings = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();
    double cash = 10000.0;

    void buyStock(Stock stock, int quantity) {
        double totalCost = stock.price * quantity;
        if (cash >= totalCost) {
            holdings.put(stock.symbol, holdings.getOrDefault(stock.symbol, 0) + quantity);
            cash -= totalCost;
            transactions.add(new Transaction("BUY", stock.symbol, quantity, stock.price));
            System.out.println("✅ Bought " + quantity + " of " + stock.symbol);
        } else {
            System.out.println("❌ Not enough cash.");
        }
    }

    void sellStock(Stock stock, int quantity) {
        int currentQty = holdings.getOrDefault(stock.symbol, 0);
        if (currentQty >= quantity) {
            holdings.put(stock.symbol, currentQty - quantity);
            cash += stock.price * quantity;
            transactions.add(new Transaction("SELL", stock.symbol, quantity, stock.price));
            System.out.println("✅ Sold " + quantity + " of " + stock.symbol);
        } else {
            System.out.println("❌ Not enough stock to sell.");
        }
    }

    void displayPortfolio(Map<String, Stock> market) {
        System.out.println("---- Portfolio Summary ----");
        for (String symbol : holdings.keySet()) {
            int qty = holdings.get(symbol);
            double price = market.get(symbol).price;
            System.out.println(symbol + " - Qty: " + qty + ", Current Price: " + price + ", Value: " + (qty * price));
        }
        System.out.println("Cash: $" + cash);
    }

    void savePortfolio(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(cash);
            for (String symbol : holdings.keySet()) {
                writer.println(symbol + "," + holdings.get(symbol));
            }
            System.out.println("✅ Portfolio saved.");
        } catch (IOException e) {
            System.out.println("❌ Error saving file.");
        }
    }

    void loadPortfolio(String filename) {
        try (Scanner file = new Scanner(new File(filename))) {
            cash = Double.parseDouble(file.nextLine());
            while (file.hasNextLine()) {
                String[] parts = file.nextLine().split(",");
                holdings.put(parts[0], Integer.parseInt(parts[1]));
            }
            System.out.println("✅ Portfolio loaded.");
        } catch (IOException e) {
            System.out.println("❌ Error loading file.");
        }
    }
}

public class StockTradingApp {
    static Map<String, Stock> market = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Portfolio portfolio = new Portfolio();

        // Sample market data
        market.put("AAPL", new Stock("AAPL", 180.0));
        market.put("GOOGL", new Stock("GOOGL", 2700.0));
        market.put("MSFT", new Stock("MSFT", 320.0));

        while (true) {
            System.out.println("\n--- Stock Trading Platform ---");
            System.out.println("1. View Market");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. Save Portfolio");
            System.out.println("6. Load Portfolio");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    for (Stock s : market.values()) {
                        System.out.println(s.symbol + " - $" + s.price);
                    }
                    break;

                case 2:
                    System.out.print("Enter Stock Symbol: ");
                    String buySymbol = sc.next().toUpperCase();
                    System.out.print("Enter Quantity: ");
                    int buyQty = sc.nextInt();
                    if (market.containsKey(buySymbol)) {
                        portfolio.buyStock(market.get(buySymbol), buyQty);
                    } else {
                        System.out.println("❌ Stock not found.");
                    }
                    break;

                case 3:
                    System.out.print("Enter Stock Symbol: ");
                    String sellSymbol = sc.next().toUpperCase();
                    System.out.print("Enter Quantity: ");
                    int sellQty = sc.nextInt();
                    if (market.containsKey(sellSymbol)) {
                        portfolio.sellStock(market.get(sellSymbol), sellQty);
                    } else {
                        System.out.println("❌ Stock not found.");
                    }
                    break;

                case 4:
                    portfolio.displayPortfolio(market);
                    break;

                case 5:
                    portfolio.savePortfolio("portfolio.txt");
                    break;

                case 6:
                    portfolio.loadPortfolio("portfolio.txt");
                    break;

                case 7:
                    System.out.println("🚪 Exiting...");
                    return;

                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }
}
