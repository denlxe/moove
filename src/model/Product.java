package model;

// an item the shop sells
public abstract class Product {

    private final String id;
    private String name;
    private String description;
    private double price;
    private int shelfLifeDays;
    private boolean active;

    protected Product(String id, String name, String description,
                      double price, int shelfLifeDays, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.shelfLifeDays = shelfLifeDays;
        this.active = active;
    }

    public abstract ProductCategory getCategory();

    public abstract String getUnitLabel();

    public abstract int getDefaultShelfLifeDays();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getShelfLifeDays() {
        return shelfLifeDays;
    }

    public void setShelfLifeDays(int shelfLifeDays) {
        this.shelfLifeDays = shelfLifeDays;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name;
    }
}
