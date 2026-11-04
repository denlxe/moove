package model;

public class Cheese extends Product {

    public Cheese(String id, String name, String description,
            double price, int shelfLifeDays, boolean active) {
        super(id, name, description, price, shelfLifeDays, active);
    }

    @Override
    public ProductCategory getCategory() {
        return ProductCategory.CHEESE;
    }

    @Override
    public String getUnitLabel() {
        return "pack";
    }

    @Override
    public int getDefaultShelfLifeDays() {
        return 10;
    }
}
