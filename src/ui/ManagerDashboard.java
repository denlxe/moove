package ui;

import model.User;
import service.AgriSystem;

import java.util.Arrays;
import java.util.List;

/* 
 * + overview: stock alerts and each clerk's dashboard
 * + products: add, edit, hide, restock
 * + inventory: batches, stock per product, raw milk intake
 */
public class ManagerDashboard extends Dashboard {
	
    public ManagerDashboard(SceneManager sceneManager, AgriSystem system, User manager) {
        super(sceneManager, system, manager);
    }

    @Override
    protected String getTitle() {
        return "Manager dashboard";
    }

    @Override
    protected List<Screen> createScreens() {
        return Arrays.asList(
                new OverviewScreen(sceneManager, system),
                new ProductScreen(system),
                new InventoryScreen(system, true));
    }
}
