package com.airtribe.meditrack.patterns;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.util.DateUtil;

/**
 * Console-based notification observer for appointment events.
 */
public class NotificationService implements AppointmentObserver {

    private static final String PREFIX = "[Notification] ";

    @Override
    public void onAppointmentCreated(Appointment appointment) {
        System.out.printf("%s BOOKED  — %s has an appointment with Dr. %s on %s%n",
                PREFIX,
                appointment.getPatient().getName(),
                appointment.getDoctor().getName(),
                DateUtil.formatDateTime(appointment.getAppointmentDateTime()));
    }

    @Override
    public void onAppointmentCancelled(Appointment appointment) {
        System.out.printf("%s CANCELLED — Appointment %s for %s has been cancelled.%n",
                PREFIX,
                appointment.getId(),
                appointment.getPatient().getName());
    }

    @Override
    public void onAppointmentCompleted(Appointment appointment) {
        System.out.printf("%s COMPLETED — Appointment %s with Dr. %s is now completed. Please proceed to billing.%n",
                PREFIX,
                appointment.getId(),
                appointment.getDoctor().getName());
    }
}
