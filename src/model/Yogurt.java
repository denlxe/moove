package model;

// yogurt products, sold by the bottle.
public class Yogurt extends Product {

    public Yogurt(String id, String name, String description,
            double price, int shelfLifeDays, boolean active) {
        super(id, name, description, price, shelfLifeDays, active);
    }

    @Override
    public ProductCategory getCategory() {
        return ProductCategory.YOGURT;
    }

    @Override
    public String getUnitLabel() {
        return "bottle";
    }

    @Override
    public int getDefaultShelfLifeDays() {
        return 14;
    }
}
