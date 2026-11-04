package model;

/*
 * The Manager has full read and write access: products, inventory, transactions,
 * and every clerk's sales history and dashboard.
 */
public class Manager extends User {

    public Manager(String username, String displayName, String password) {
        super(username, displayName, password);
    }

    @Override
    public Role getRole() {
        return Role.MANAGER;
    }

    @Override
    public boolean canManageProducts() {
        return true;
    }

    @Override
    public boolean canManageInventory() {
        return true;
    }

    @Override
    public boolean canViewAllClerks() {
        return true;
    }

    @Override
    public boolean canWithdrawCash() {
        return true;
    }
}