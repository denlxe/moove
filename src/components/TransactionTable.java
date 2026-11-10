package components;
/* TODO:
 * The transaction history table. 
 * - It is reused by the Manager overview (clerk sales history), the clerk summary, and the wallet and transaction screen, 
 * - Voided rows are greyed out, and the "Void" button column only appears when a void handler is supplied.
 */

import java.util.function.Consumer;
import java.util.list;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import model.Transaction;
import service.AgriSystem;

public class TransactionTable extends TableView<Transaction> {


}
