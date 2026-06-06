package com.airtribe.meditrack.entity;

import java.time.LocalDateTime;

/**
 * Immutable snapshot of a bill at the time it was generated.
 * Demonstrates: immutability (final class, final fields, no setters, defensive copy-free because
 * all fields are primitives, Strings, or the immutable LocalDateTime).
 */
public final class BillSummary {

    private final String patientName;
    private final String doctorName;
    private final double baseAmount;
    private final double taxAmount;
    private final double totalAmount;
    private final String strategyApplied;
    private final boolean settled;
    private final LocalDateTime generatedAt;

    public BillSummary(String patientName, String doctorName,
                       double baseAmount, double taxAmount, double totalAmount,
                       String strategyApplied, boolean settled) {
        this.patientName     = patientName;
        this.doctorName      = doctorName;
        this.baseAmount      = baseAmount;
        this.taxAmount       = taxAmount;
        this.totalAmount     = totalAmount;
        this.strategyApplied = strategyApplied;
        this.settled         = settled;
        this.generatedAt     = LocalDateTime.now();
    }

    // --- Only getters — no setters (immutability guarantee) ---

    public String getPatientName()    { return patientName; }
    public String getDoctorName()     { return doctorName; }
    public double getBaseAmount()     { return baseAmount; }
    public double getTaxAmount()      { return taxAmount; }
    public double getTotalAmount()    { return totalAmount; }
    public String getStrategyApplied(){ return strategyApplied; }
    public boolean isSettled()        { return settled; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }

    @Override
    public String toString() {
        return String.format(
                "--- Bill Summary ---\n" +
                "Patient      : %s\n" +
                "Doctor       : %s\n" +
                "Base Amount  : ₹%.2f\n" +
                "Tax          : ₹%.2f\n" +
                "Total Amount : ₹%.2f\n" +
                "Strategy     : %s\n" +
                "Status       : %s\n" +
                "Generated At : %s",
                patientName, doctorName, baseAmount, taxAmount, totalAmount,
                strategyApplied, settled ? "PAID" : "PENDING",
                generatedAt);
    }
}
