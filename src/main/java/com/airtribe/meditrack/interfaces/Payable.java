package com.airtribe.meditrack.interfaces;

/**
 * Contract for any entity that can be paid for.
 */
public interface Payable {

    /** Returns the total amount due. */
    double getAmount();

    /** Marks the entity as paid. */
    void pay();

    /** Returns true if payment has been settled. */
    boolean isPaid();

    /** Default convenience method for human-readable payment status. */
    default String getPaymentStatus() {
        return isPaid() ? "PAID" : "PENDING";
    }
}
