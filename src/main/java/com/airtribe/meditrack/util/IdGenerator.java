package com.airtribe.meditrack.util;

import com.airtribe.meditrack.constants.Constants;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe ID generator implemented as a lazy-initialised Singleton.
 * Demonstrates: Singleton pattern (double-checked locking) + AtomicInteger.
 */
public class IdGenerator {

    private static volatile IdGenerator instance;

    private final AtomicInteger patientCounter;
    private final AtomicInteger doctorCounter;
    private final AtomicInteger appointmentCounter;
    private final AtomicInteger billCounter;

    // Static initialisation block — runs once when the class is loaded
    static {
        System.out.println("[IdGenerator] ID generator class initialised.");
    }

    private IdGenerator() {
        patientCounter     = new AtomicInteger(1000);
        doctorCounter      = new AtomicInteger(2000);
        appointmentCounter = new AtomicInteger(3000);
        billCounter        = new AtomicInteger(4000);
    }

    /** Lazy double-checked locking singleton. */
    public static IdGenerator getInstance() {
        if (instance == null) {
            synchronized (IdGenerator.class) {
                if (instance == null) {
                    instance = new IdGenerator();
                }
            }
        }
        return instance;
    }

    public String generatePatientId() {
        return Constants.PATIENT_ID_PREFIX + "-" + patientCounter.getAndIncrement();
    }

    public String generateDoctorId() {
        return Constants.DOCTOR_ID_PREFIX + "-" + doctorCounter.getAndIncrement();
    }

    public String generateAppointmentId() {
        return Constants.APPOINTMENT_ID_PREFIX + "-" + appointmentCounter.getAndIncrement();
    }

    public String generateBillId() {
        return Constants.BILL_ID_PREFIX + "-" + billCounter.getAndIncrement();
    }

    /** Resets all counters — useful for test isolation. */
    public void reset(int patientStart, int doctorStart, int appointmentStart, int billStart) {
        patientCounter.set(patientStart);
        doctorCounter.set(doctorStart);
        appointmentCounter.set(appointmentStart);
        billCounter.set(billStart);
    }
}
