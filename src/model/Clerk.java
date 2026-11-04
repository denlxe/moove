package model;

/*
 * The Clerk records sales and cash donations, checks stock levels and may void
 * any recorded transaction. A clerk cannot change products or add stock.
 */
public class Clerk extends User {

    public Clerk(String username, String displayName, String password) {
        super(username, displayName, password);
    }

    @Override
    public Role getRole() {
        return Role.CLERK;
    }

    @Override
    public boolean canManageProducts() {
        return false;
    }

    @Override
    public boolean canManageInventory() {
        return false;
    }

    @Override
    public boolean canViewAllClerks() {
        return false;
    }

    @Override
    public boolean canWithdrawCash() {
        return false;
    }
}