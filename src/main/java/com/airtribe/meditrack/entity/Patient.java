package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a patient in the clinic.
 * Demonstrates: inheritance, Cloneable (deep copy), and encapsulation.
 */
public class Patient extends Person implements Cloneable {

    private LocalDate dateOfBirth;
    private List<String> medicalHistory;   // mutable — requires deep copy in clone()

    /**
     * Primary constructor.
     */
    public Patient(String id, String name, String phone, String email, LocalDate dateOfBirth) {
        super(id, name, phone, email);
        Validator.requireNonNull(dateOfBirth, "dateOfBirth");
        Validator.requireTrue(!dateOfBirth.isAfter(LocalDate.now()), "dateOfBirth cannot be in the future");
        this.dateOfBirth    = dateOfBirth;
        this.medicalHistory = new ArrayList<>();
    }

    /** Copy constructor — performs a deep copy of medicalHistory. */
    private Patient(Patient other) {
        super(other);
        this.dateOfBirth    = other.dateOfBirth;                  // LocalDate is immutable — safe to share
        this.medicalHistory = new ArrayList<>(other.medicalHistory); // deep copy
    }

    // --- Getters ---

    public LocalDate getDateOfBirth() { return dateOfBirth; }

    public int getAge() {
        return DateUtil.calculateAge(dateOfBirth);
    }

    /** Returns an unmodifiable view — callers cannot mutate the list directly. */
    public List<String> getMedicalHistory() {
        return Collections.unmodifiableList(medicalHistory);
    }

    // --- Setters ---

    public void setDateOfBirth(LocalDate dateOfBirth) {
        Validator.requireNonNull(dateOfBirth, "dateOfBirth");
        Validator.requireTrue(!dateOfBirth.isAfter(LocalDate.now()), "dateOfBirth cannot be in the future");
        this.dateOfBirth = dateOfBirth;
    }

    public void addMedicalHistory(String entry) {
        Validator.requireNonEmpty(entry, "medicalHistory entry");
        medicalHistory.add(entry.trim());
    }

    public void removeMedicalHistory(String entry) {
        medicalHistory.remove(entry);
    }

    /**
     * Deep clone — medicalHistory list is duplicated so the clone
     * and the original are fully independent.
     * Shallow clone would share the list reference, causing hidden mutations.
     */
    @Override
    public Patient clone() {
        return new Patient(this);
    }

    @Override
    public String getDetails() {
        return String.format("Patient[%s] %s | DOB: %s | Age: %d | History: %d record(s)",
                getId(), getName(), DateUtil.formatDate(dateOfBirth),
                getAge(), medicalHistory.size());
    }
}
