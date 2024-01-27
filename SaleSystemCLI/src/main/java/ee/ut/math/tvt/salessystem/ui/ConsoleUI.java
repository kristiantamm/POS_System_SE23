package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * A simple CLI (limited functionality).
 */
public class ConsoleUI {
    private static final Logger log = LogManager.getLogger(ConsoleUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart cart;

    public ConsoleUI(SalesSystemDAO dao) {
        this.dao = dao;
        cart = new ShoppingCart(dao);
    }

    public static void main(String[] args) throws Exception {
        SalesSystemDAO dao = new InMemorySalesSystemDAO();
        ConsoleUI console = new ConsoleUI(dao);
        console.runCLI();

    }

    /**
     * Run the sales system CLI.
     */
    public void runCLI() throws IOException {
        log.info("ConsoleUI started");
        System.out.println("===========================");
        System.out.println("=       Sales System      =");
        System.out.println("===========================");
        printUsage();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            processCommand(in.readLine().trim().toLowerCase());
            System.out.println("Done. ");
        }
    }

    private void showStock() {
        List<StockItem> stockItems = dao.findStockItems();
        System.out.println("-------------------------");
        for (StockItem si : stockItems) {
            System.out.println(si.getStockItemId() + " " + si.getStockItemName() + " " + si.getStockItemPrice() + "Euro (" + si.getStockItemQuantity() + " items)");
        }
        if (stockItems.size() == 0) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void showCart() {
        System.out.println("-------------------------");
        for (SoldItem si : cart.getAllItems()) {
            System.out.println(si.getSoldItemName() + " " + si.getSoldItemPrice() + "Euro (" + si.getQuantityOfSoldItem() + " items)");
        }
        if (cart.getAllItems().size() == 0) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void showTeam() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("-------------------------");
        System.out.print("Team: ");
        System.out.println(properties.getProperty("teamName"));
        System.out.print("Team contact person: ");
        System.out.println(properties.getProperty("teamContactPerson"));
        String[] members = properties.getProperty("teamMembers").split(",");
        System.out.println("Team members:");
        for (String member : members) {
            System.out.println(member);
        }
        System.out.println("-------------------------");
    }

    private void printUsage() {
        System.out.println("-------------------------");
        System.out.println("Usage:");
        System.out.println("?\t\tShow this help");
        System.out.println("w\t\tShow the contents of the warehouse");
        System.out.println("t\t\tShow team information");
        System.out.println("c\t\tShow cart contents");
        System.out.println("a IDX NR \t\tAdd NR of stock item with index IDX to the cart");
		System.out.println("iw IDX NR \t\tIncrease the amount of stock item with index IDX by NR");
        System.out.println("aw *IDX NR name PR \tAdd NR of stock item with index IDX (optional), price PR and name to the warehouse");
        System.out.println("p\t\tPurchase the shopping cart");
        System.out.println("r\t\tReset the shopping cart");
        System.out.println("h\t\tShow purchase history");
        System.out.println("q\t\tQuit the application");
        System.out.println("-------------------------");
    }

    private void processCommand(String command) {
        String[] c = command.split(" ");

        if (c[0].equals("?"))
            printUsage();
        else if (c[0].equals("q"))
            System.exit(0);
        else if (c[0].equals("w"))
            showStock();
        else if (c[0].equals("t"))
            showTeam();
        else if (c[0].equals("c"))
            showCart();
        else if (c[0].equals("p"))
            cart.submitCurrentPurchase();
        else if (c[0].equals("r"))
            cart.cancelCurrentPurchase();
        else if (c[0].equals("h")) {
            List<Purchase> purchases = dao.findPurchases();
            int i = 1;
            for (Purchase purchase : purchases) {
                System.out.println(i + ": date and time: "+ purchase.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + ", total: " + purchase.getTotalItemsPrice());
                i++;
            }
        }
        else if (c[0].equals("a") && c.length == 3) {
            try {
                long idx = Long.parseLong(c[1]);
                int amount = Integer.parseInt(c[2]);
                StockItem item = dao.findStockItem(idx);
                if (item != null) {
                    cart.addItemToShoppingCart(new SoldItem(item, Math.min(amount, item.getStockItemQuantity())));
                } else {
                    System.out.println("no stock item with id " + idx);
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        } else if (c[0].equals("aw") && (c.length == 4 || c.length == 5)) {
            addItemToStock(c);
        } else if (c[0].equals("iw") && (c.length == 3)) {
            increaseItemStock(c);
        } else {
            System.out.println("unknown command");
        }
    }

    private void addItemToStock(String[] c) {
        try {
            long idx = -1;
            int offset = 1;
            if (c.length == 5) {
                offset = 0;
                idx = Long.parseLong(c[1]);
                if (idx < 1) {
                    System.out.println("Invalid index");
                    return;
                }
            }
            int amount = Integer.parseInt(c[2-offset]);
            String name = c[3-offset];
            double price = Double.parseDouble(c[4-offset]);

            StockItem item = dao.findStockItem(idx);
            //StockItem item = null;
            if (item != null) {
                //If an item exists, throw an exception
                throw new SalesSystemException("Item with the provided index" + idx + " already exists");
            } else {
                //Add a new item if the information is correct
                if (amount < 1 || price < 0 || name.equals("") ) {
                    System.out.println("Invalid item information");
                    return;
                }
                //Find a suitable index if it wasn't provided
                if (idx == -1) {
                    idx = 1;
                    while (true) {
                        item = dao.findStockItem(idx);
                        if (item == null) break;
                        idx++;
                    }
                }
                dao.beginTransaction();
                try {
                    dao.saveStockItem(new StockItem(idx, name, "", price, amount));
                    dao.commitTransaction();
                } catch (Exception e) {
                    dao.rollbackTransaction();
                }
            }
        } catch (SalesSystemException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void increaseItemStock(String[] c) {
        try {
            long idx = Long.parseLong(c[1]);
            if (idx < 1) {
                System.out.println("Invalid index");
                return;
            }
            int amount = Integer.parseInt(c[2]);
            if (amount < 1) {
                System.out.println("Invalid amount: " + amount);
                return;
            }
            StockItem item = dao.findStockItem(idx);
            if (item != null) {
                //Increase the item amount if it exists
                dao.beginTransaction();
                try {
                    dao.increaseStockItem(idx, amount);
                    dao.commitTransaction();
                } catch (Exception e) {
                    dao.rollbackTransaction();
                    throw e;
                }
            } else {
                System.out.println("No item with the provided index exists");
            }
        } catch (SalesSystemException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
        }
    }
}
