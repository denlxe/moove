package model;

/**
 * A person who can sign in.
 * Subclass:
 * + Manager
 * + Clerk
 */
public abstract class User {

    private final String username;
    private final String displayName;
    private final String password;

    protected User(String username, String displayName, String password) {
        this.username = username;
        this.displayName = displayName;
        this.password = password;
    }


    public static User create(Role role, String username, String displayName, String password) {
        switch (role) {
            case MANAGER:
                return new Manager(username, displayName, password);
            case CLERK:
                return new Clerk(username, displayName, password);
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

    public abstract Role getRole();

    public abstract boolean canManageProducts();

    public abstract boolean canManageInventory();

    public abstract boolean canViewAllClerks();

    public abstract boolean canWithdrawCash();


    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return displayName;
    }
}