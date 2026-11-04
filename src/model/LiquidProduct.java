package model;

// drinkable dairy such as fresh milk and chocolate milk, sold by the bottle
public class LiquidProduct extends Product {

    public LiquidProduct(String id, String name, String description,
            double price, int shelfLifeDays, boolean active) {
        super(id, name, description, price, shelfLifeDays, active);
    }

    @Override
    public ProductCategory getCategory() {
        return ProductCategory.LIQUID;
    }

    @Override
    public String getUnitLabel() {
        return "bottle";
    }

    @Override
    public int getDefaultShelfLifeDays() {
        return 7;
    }
}
