package com.airtribe.meditrack.patterns;

import com.airtribe.meditrack.constants.Constants;

/**
 * Discounted billing — reduces the base fee before applying tax.
 * Used for senior citizens or special-category patients.
 */
public class DiscountedBillingStrategy implements BillingStrategy {

    private final double discountRate;
    private final String label;

    public DiscountedBillingStrategy(double discountRate, String label) {
        if (discountRate < 0 || discountRate >= 1) {
            throw new IllegalArgumentException("Discount rate must be between 0 and 1");
        }
        this.discountRate = discountRate;
        this.label        = label;
    }

    /** Convenience factory for senior-citizen discount. */
    public static DiscountedBillingStrategy senior() {
        return new DiscountedBillingStrategy(Constants.SENIOR_DISCOUNT_RATE, "Senior Citizen");
    }

    /** Convenience factory for general discount. */
    public static DiscountedBillingStrategy general() {
        return new DiscountedBillingStrategy(Constants.GENERAL_DISCOUNT_RATE, "General");
    }

    @Override
    public double calculateBase(double consultationFee) {
        return consultationFee * (1 - discountRate);
    }

    @Override
    public double calculateTax(double baseAmount) {
        return baseAmount * Constants.TAX_RATE;
    }

    @Override
    public String getName() {
        return label + " Discount (" + (int) (discountRate * 100) + "% off + 18% GST)";
    }
}
