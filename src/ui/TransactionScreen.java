package ui;

import model.Donation;
import model.Sale;
import model.Transaction;
import model.User;
import service.AgriSystem;
import components.FormDialog;
import components.StatCard;
import components.TransactionTable;
import util.Fmt;
import util.Input;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* TODO:
 * - Wallet ballence
 * - Filterable transaction history (donor, type, amount or item, date for donations)
 * - Buttons to record donations, processing fees, and cash-outs, and to void a transaction
 * - readOnly is true (when a manager is looking at clerk's dashboard), false otherwise
 * 		- buttons and void column are hidden
 * 
 * Record Donation Button
 * - Donor name
 * - type (cash, item)
 * - amount (unwritable if type item)
 * - item name (unwritable if type cash)
 * - item quantity (unwritable if type cash)
 * 
 * Toll Processing Button
 * - shows a dialog:
 * 		- Farmer's cooperative name (input)
 * 		- Liter's processed (input)
 * 		- fee per liter (input)
 * 		- Fee (dynamically update)
 * 
 * Transaction History
 * Filter Selector:
 * - Sale
 * - Donation
 * - Processing Fee
 * - Cash out
 * - Write off
 * Search button
 * Show voided checkbox
 * 
 * Transaction History Table
 * Columns: number, date, type, details, amount, recorded by, status, button to void that transaction
 * */

public class TransactionScreen extends Screen {
	
	private final AgriSystem system;
	private final String onlyUsername;
	private final boolean readOnly;
	
    private final ComboBox<String> typeFilter = new ComboBox<>();
    private final TextField search = new TextField();
    private final CheckBox showVoided = new CheckBox("Show voided");
    private TransactionTable table;
    private List<Transaction> allTransactions = new ArrayList<>();
	
	public TransactionScreen(AgriSystem system, String onlyUsername, boolean readOnly) {
		this.system  = system;
		this.onlyUsername = onlyUsername;
		this.readOnly = readOnly;
	}
	
	@Override
	public String getName() {
		return "Transactions";
	}
	
	@Override
	protected Parent build() {
		
		
		return page("Transactions", "Subtitle", );
		
	}
	
	@Override
	public void refresh() {
		
	}
	
}
