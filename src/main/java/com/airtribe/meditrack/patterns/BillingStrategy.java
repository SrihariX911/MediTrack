package com.airtribe.meditrack.patterns;

/**
 * Strategy interface for billing calculations.
 * Demonstrates: Strategy design pattern.
 */
public interface BillingStrategy {

    /**
     * Calculates the pre-tax base amount from the doctor's consultation fee.
     * Implementations may apply discounts here.
     */
    double calculateBase(double consultationFee);

    /**
     * Calculates tax on the base amount.
     */
    double calculateTax(double baseAmount);

    /** Human-readable name shown on receipts. */
    String getName();
}
