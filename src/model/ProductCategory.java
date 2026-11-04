package model;

public enum ProductCategory {
    LIQUID("Fresh milk"),
    YOGURT("Yogurt"),
    CHEESE("Cheese"),
    BUTTER("Butter");

    private final String label;

    ProductCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // returns the usual shelf life in days for this kind of product (for prefilling forms)
    public int defaultShelfLifeDays() {
        return create("sample", "", "", 1, 1, false).getDefaultShelfLifeDays();
    }

    public Product create(String id, String name, String description,
                          double price, int shelfLifeDays, boolean active) {
        switch (this) {
            case LIQUID:
                return new LiquidProduct(id, name, description, price, shelfLifeDays, active);
            case YOGURT:
                return new Yogurt(id, name, description, price, shelfLifeDays, active);
            case CHEESE:
                return new Cheese(id, name, description, price, shelfLifeDays, active);
            case BUTTER:
                return new Butter(id, name, description, price, shelfLifeDays, active);
            default:
                throw new IllegalStateException("Unknown category: " + this);
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
