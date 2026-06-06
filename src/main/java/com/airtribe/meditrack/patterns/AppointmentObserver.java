package com.airtribe.meditrack.patterns;

import com.airtribe.meditrack.entity.Appointment;

/**
 * Observer contract for appointment lifecycle events.
 * Demonstrates: Observer design pattern.
 */
public interface AppointmentObserver {

    void onAppointmentCreated(Appointment appointment);

    void onAppointmentCancelled(Appointment appointment);

    void onAppointmentCompleted(Appointment appointment);
}
