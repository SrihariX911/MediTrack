package com.airtribe.meditrack.patterns;

import com.airtribe.meditrack.constants.Constants;

/**
 * Standard billing — full fee + configured tax rate.
 */
public class StandardBillingStrategy implements BillingStrategy {

    @Override
    public double calculateBase(double consultationFee) {
        return consultationFee;
    }

    @Override
    public double calculateTax(double baseAmount) {
        return baseAmount * Constants.TAX_RATE;
    }

    @Override
    public String getName() {
        return "Standard (18% GST)";
    }
}
