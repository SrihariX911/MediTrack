package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.interfaces.Payable;
import com.airtribe.meditrack.util.Validator;

/**
 * Represents a billing record tied to an appointment.
 * Demonstrates: interface implementation (Payable) and encapsulation.
 */
public class Bill extends MedicalEntity implements Payable {

    private final String id;
    private final Appointment appointment;
    private final double baseAmount;
    private final double taxAmount;
    private final double totalAmount;
    private boolean paid;
    private String billingStrategyName;

    public Bill(String id, Appointment appointment, double baseAmount,
                double taxAmount, String billingStrategyName) {
        Validator.requireNonEmpty(id, "billId");
        Validator.requireNonNull(appointment, "appointment");
        Validator.requirePositive(baseAmount, "baseAmount");
        this.id                  = id;
        this.appointment         = appointment;
        this.baseAmount          = baseAmount;
        this.taxAmount           = taxAmount;
        this.totalAmount         = baseAmount + taxAmount;
        this.paid                = false;
        this.billingStrategyName = billingStrategyName;
    }

    // --- Getters ---

    @Override public String getId()               { return id; }
    public Appointment getAppointment()           { return appointment; }
    public double getBaseAmount()                 { return baseAmount; }
    public double getTaxAmount()                  { return taxAmount; }
    public String getBillingStrategyName()        { return billingStrategyName; }

    // --- Payable ---

    @Override public double getAmount()           { return totalAmount; }
    @Override public boolean isPaid()             { return paid; }

    @Override
    public void pay() {
        Validator.requireTrue(!paid, "Bill " + id + " is already paid");
        this.paid = true;
    }

    /**
     * Generates an immutable summary snapshot of this bill.
     * Demonstrates creation of a BillSummary (immutable class).
     */
    public BillSummary generateSummary() {
        return new BillSummary(
                appointment.getPatient().getName(),
                appointment.getDoctor().getName(),
                baseAmount, taxAmount, totalAmount,
                billingStrategyName, paid);
    }

    @Override
    public String getDetails() {
        return String.format(
                "Bill[%s] Patient: %s | Doctor: %s | Base: ₹%.2f | Tax: ₹%.2f | Total: ₹%.2f | %s",
                id, appointment.getPatient().getName(),
                appointment.getDoctor().getName(),
                baseAmount, taxAmount, totalAmount,
                getPaymentStatus());
    }
}
