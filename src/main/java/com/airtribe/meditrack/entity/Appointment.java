package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.Validator;

import java.time.LocalDateTime;

/**
 * Represents a scheduled appointment between a patient and a doctor.
 * Demonstrates: Cloneable with deep copy of nested Patient, and enum usage.
 */
public class Appointment extends MedicalEntity implements Cloneable {

    private final String id;
    private Patient patient;             // mutable ref — deep-copied in clone
    private final Doctor doctor;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String notes;

    public Appointment(String id, Patient patient, Doctor doctor,
                       LocalDateTime appointmentDateTime, String notes) {
        Validator.requireNonEmpty(id, "appointmentId");
        Validator.requireNonNull(patient, "patient");
        Validator.requireNonNull(doctor, "doctor");
        Validator.requireNonNull(appointmentDateTime, "appointmentDateTime");
        this.id                  = id;
        this.patient             = patient;
        this.doctor              = doctor;
        this.appointmentDateTime = appointmentDateTime;
        this.notes               = notes != null ? notes : "";
        this.status              = AppointmentStatus.CONFIRMED;
    }

    // --- Getters ---

    @Override
    public String getId() { return id; }

    public Patient getPatient()                   { return patient; }
    public Doctor getDoctor()                     { return doctor; }
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public AppointmentStatus getStatus()          { return status; }
    public String getNotes()                      { return notes; }

    // --- Setters ---

    public void setStatus(AppointmentStatus status) {
        Validator.requireNonNull(status, "status");
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes : "";
    }

    public void setAppointmentDateTime(LocalDateTime dt) {
        Validator.requireNonNull(dt, "appointmentDateTime");
        this.appointmentDateTime = dt;
    }

    /**
     * Deep clone — Patient is cloned so the copy owns a fully independent
     * snapshot of the patient data at the time of cloning.
     * Doctor is shared (clinic-wide singleton-ish entity; not cloned by design).
     */
    @Override
    public Appointment clone() {
        try {
            Appointment copy = (Appointment) super.clone();
            // Deep copy patient — Patient.clone() itself deep-copies medicalHistory
            copy.patient = this.patient.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            // Cannot happen: Appointment and Patient both implement Cloneable
            throw new AssertionError("Clone failed unexpectedly", e);
        }
    }

    @Override
    public String getDetails() {
        return String.format("Appointment[%s] %s with Dr. %s on %s | Status: %s",
                id, patient.getName(), doctor.getName(),
                DateUtil.formatDateTime(appointmentDateTime),
                status.getDisplayName());
    }
}
