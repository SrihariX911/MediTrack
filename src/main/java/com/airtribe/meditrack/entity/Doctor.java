package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.util.Validator;

/**
 * Represents a doctor in the clinic.
 * Demonstrates: inheritance from Person, encapsulation, and getDetails() override.
 */
public class Doctor extends Person {

    private Specialization specialization;
    private double consultationFee;
    private boolean available;

    /**
     * Full constructor — chains to Person via super().
     */
    public Doctor(String id, String name, String phone, String email,
                  Specialization specialization, double consultationFee) {
        super(id, name, phone, email);
        Validator.requireNonNull(specialization, "specialization");
        Validator.requirePositive(consultationFee, "consultationFee");
        this.specialization  = specialization;
        this.consultationFee = consultationFee;
        this.available       = true;
    }

    // --- Getters ---

    public Specialization getSpecialization()  { return specialization; }
    public double getConsultationFee()          { return consultationFee; }
    public boolean isAvailable()               { return available; }

    // --- Setters ---

    public void setSpecialization(Specialization specialization) {
        Validator.requireNonNull(specialization, "specialization");
        this.specialization = specialization;
    }

    public void setConsultationFee(double fee) {
        Validator.requirePositive(fee, "consultationFee");
        this.consultationFee = fee;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Overrides MedicalEntity.getDetails() — dynamic dispatch demonstration.
     */
    @Override
    public String getDetails() {
        return String.format("Doctor[%s] %s | %s | Fee: ₹%.2f | %s",
                getId(), getName(), specialization.getDisplayName(),
                consultationFee, available ? "Available" : "Unavailable");
    }
}
