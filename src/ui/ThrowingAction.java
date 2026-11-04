package ui;

import exception.AgriException;

// dialogs and buttons run these and show the message instead of crashing
@FunctionalInterface
public interface ThrowingAction {
    /* Runs the action.
     * throws AgriException if the action is not allowed or the input is invalid
     */
    void run() throws AgriException;
}
