package model;

// The two kinds of account the system supports.
public enum Role {
    MANAGER("Manager"),
    CLERK("Clerk");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
