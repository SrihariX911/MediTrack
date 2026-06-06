package com.airtribe.meditrack.exception;

/**
 * Thrown when an appointment cannot be located by its ID.
 */
public class AppointmentNotFoundException extends RuntimeException {

    private final String appointmentId;

    public AppointmentNotFoundException(String appointmentId) {
        super("Appointment not found with ID: " + appointmentId);
        this.appointmentId = appointmentId;
    }

    public AppointmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.appointmentId = null;
    }

    public String getAppointmentId() { return appointmentId; }
}
