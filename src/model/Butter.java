package model;

// sold by the pack
public class Butter extends Product {

    public Butter(String id, String name, String description,
            double price, int shelfLifeDays, boolean active) {
        super(id, name, description, price, shelfLifeDays, active);
    }

    @Override
    public ProductCategory getCategory() {
        return ProductCategory.BUTTER;
    }

    @Override
    public String getUnitLabel() {
        return "pack";
    }

    @Override
    public int getDefaultShelfLifeDays() {
        return 60;
    }
}
