package model;

import java.time.LocalDateTime;

/**
 * something that can be cancelled after it was recorded
 * a voided record is kept in the history (marked as voided)
 */
public interface Voidable {

    // return true if this record has already been voided
    boolean isVoided();

    // true if this kind of record is allowed to be voided at all 
    boolean isVoidable();

    void markVoided(String voidedBy, LocalDateTime at, String reason);
}
