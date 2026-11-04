package service;

import exception.AgriException;
import model.Inventory;
import model.Product;
import model.ProductCategory;
import model.Role;
import model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Starting data so app is usable on first launch: 
 * - 3 accounts
 * - product list
 * - some opening stock
 */
final class SeedData {

    private SeedData() {
    }

    // returns the default accounts (manager/manager123, clerk1/clerk123, clerk2/clerk123)
    static List<User> users() {
        List<User> users = new ArrayList<>();
        users.add(User.create(Role.MANAGER, "manager", "Store Manager",
                "manager123"));
        users.add(User.create(Role.CLERK, "clerk1", "Clerk One",
                "clerk123"));
        users.add(User.create(Role.CLERK, "clerk2", "Clerk Two",
                "clerk123"));
        return users;
    }

    // returns the starting product list (ids P001 to P035)
    // the expiry dates are guesses
    static List<Product> products() {
        List<Product> list = new ArrayList<>();
        int n = 1;
        // Fresh milks: 7 days
        String fresh = "Pasteurized fresh milk from Laguna, Batangas and Bulacan cooperatives";
        n = add(list, n, ProductCategory.LIQUID, "Fresh Milk", fresh, 7, "200mL", 40, "500mL", 75, "1L", 140, "2L", 280);
        String choco = "Pasteurized milk blended with Davao cacao";
        n = add(list, n, ProductCategory.LIQUID, "Choco Milk", choco, 7, "200mL", 45, "500mL", 85, "1L", 165, "2L", 325);
        String coffee = "Creamy milk with Benguet and Cordillera coffee extract";
        n = add(list, n, ProductCategory.LIQUID, "Coffee Milk", coffee, 7, "200mL", 45, "500mL", 90, "1L", 175, "2L", 350);
        n = add(list, n, ProductCategory.LIQUID, "Pandan Milk", "Milk infused with fresh pandan leaves", 7, "500mL", 85, "1L", 165);
        n = add(list, n, ProductCategory.LIQUID, "Melon Milk", "Milk blended with cantaloupe and honeydew", 7, "500mL", 85, "1L", 165);
        // Yogurt drinks: 14 days
        n = add(list, n, ProductCategory.YOGURT, "Yogurt Plain", "Live-culture drinking yogurt, pleasantly tart", 14, "500mL", 85, "1L", 165);
        n = add(list, n, ProductCategory.YOGURT, "Yogurt Strawberry", "Drinking yogurt with La Trinidad strawberries", 14, "500mL", 125, "1L", 250);
        n = add(list, n, ProductCategory.YOGURT, "Yogurt Blueberry", "Drinking yogurt with Benguet and Bukidnon blueberries", 14, "500mL", 140, "1L", 280);
        n = add(list, n, ProductCategory.YOGURT, "Yogurt Peach Mango", "Drinking yogurt with Carabao mango and peach", 14, "500mL", 100, "1L", 200);
        // Cheeses and butter: one size each
        n = add(list, n, ProductCategory.CHEESE, "Kesong Puti", "Fresh, soft, lightly salted white cheese", 7, "200g", 90);
        n = add(list, n, ProductCategory.CHEESE, "Mozzarella Cheese", "Hand-stretched cheese with a perfect melt", 21, "200g", 160);
        n = add(list, n, ProductCategory.CHEESE, "Gouda Cheese", "Mild, buttery, semi-hard aged cheese", 60, "200g", 200);
        n = add(list, n, ProductCategory.CHEESE, "Processed Cheese Spread", "Smooth, creamy, easy-to-spread cheese", 30, "200g", 90);
        n = add(list, n, ProductCategory.CHEESE, "Feta Cheese without Oil", "Crumbly, savory feta aged in brine", 30, "250g", 150);
        n = add(list, n, ProductCategory.CHEESE, "Feta Cheese in a Jar", "Feta cubes in herb and garlic infused oil", 60, "180g", 180);
        n = add(list, n, ProductCategory.CHEESE, "Ricotta", "Light, fluffy cheese made from whey", 7, "200g", 180);
        n = add(list, n, ProductCategory.CHEESE, "Cream Cheese", "Soft, rich, spreadable cheese", 21, "200g", 200);
        n = add(list, n, ProductCategory.CHEESE, "Cottage Cheese", "Mild curds in a light salted cream dressing", 10, "200g", 180);
        n = add(list, n, ProductCategory.CHEESE, "Cheddar Cheese", "Aged, sharp, firm cheese", 60, "200g", 200);
        add(list, n, ProductCategory.BUTTER, "Butter", "Cultured butter from full-cream local milk", 60, "200g", 160);
        return list;
    }

    /* + adds one product per size
     * + sizesAndPrices alternates a size label and its price
     * + returns the next free product number
     */
    private static int add(List<Product> list, int number, ProductCategory category, String name,
                           String description, int shelfLifeDays, Object... sizesAndPrices) {
        for (int i = 0; i < sizesAndPrices.length; i += 2) {
            String size = (String) sizesAndPrices[i];
            double price = ((Number) sizesAndPrices[i + 1]).doubleValue();
            list.add(category.create(String.format("P%03d", number++), name + " " + size,
                    description, price, shelfLifeDays, true));
        }
        return number;
    }

    /* adds raw milk and opening batches
     * expiry dates are relative to today
     * few sizes are deliberately left out of stock.
     */
    static void openingStock(Inventory inventory, List<Product> products, LocalDate today) throws AgriException {
        inventory.receiveMilk(120, "Opening balance", today, "system");
        // Fresh milk
        batch(inventory, products, "P001", 20, today, 5);
        batch(inventory, products, "P002", 24, today, 3);
        batch(inventory, products, "P002", 30, today, 7);
        batch(inventory, products, "P003", 12, today, 5);
        batch(inventory, products, "P004", 8, today, 6);
        // Choco milk: 6 units expire tomorrow on purpose (near expiry demo)
        batch(inventory, products, "P005", 15, today, 6);
        batch(inventory, products, "P006", 6, today, 1);
        batch(inventory, products, "P006", 20, today, 6);
        batch(inventory, products, "P007", 10, today, 6);
        batch(inventory, products, "P008", 6, today, 6);
        // Coffee, pandan, melon
        batch(inventory, products, "P009", 15, today, 6);
        batch(inventory, products, "P010", 12, today, 6);
        batch(inventory, products, "P011", 10, today, 6);
        batch(inventory, products, "P013", 10, today, 6);
        batch(inventory, products, "P015", 10, today, 6);
        // Yogurt
        batch(inventory, products, "P017", 18, today, 12);
        batch(inventory, products, "P018", 10, today, 12);
        batch(inventory, products, "P019", 10, today, 12);
        batch(inventory, products, "P021", 10, today, 12);
        batch(inventory, products, "P023", 10, today, 12);
        // Cheese and butter
        batch(inventory, products, "P025", 15, today, 6);
        batch(inventory, products, "P026", 10, today, 20);
        batch(inventory, products, "P027", 8, today, 55);
        batch(inventory, products, "P028", 10, today, 28);
        batch(inventory, products, "P029", 8, today, 28);
        batch(inventory, products, "P030", 6, today, 55);
        batch(inventory, products, "P031", 6, today, 6);
        batch(inventory, products, "P032", 8, today, 20);
        batch(inventory, products, "P033", 8, today, 9);
        batch(inventory, products, "P034", 8, today, 55);
        batch(inventory, products, "P035", 12, today, 55);
    }

    private static void batch(Inventory inventory, List<Product> products, String productId,
                              int units, LocalDate today, int daysUntilExpiry) throws AgriException {
        for (Product p : products) {
            if (p.getId().equals(productId)) {
                inventory.addBatch(p, units, 0, today, today.plusDays(daysUntilExpiry));
                return;
            }
        }
    }
}