package com.airtribe.meditrack.constants;

/**
 * Application-wide constants.
 */
public final class Constants {

    private Constants() {}

    // Tax and billing
    public static final double TAX_RATE = 0.18;
    public static final double SENIOR_DISCOUNT_RATE = 0.15;
    public static final double GENERAL_DISCOUNT_RATE = 0.10;
    public static final int SENIOR_AGE_THRESHOLD = 60;

    // File paths
    public static final String DATA_DIR = "data/";
    public static final String PATIENTS_CSV = DATA_DIR + "patients.csv";
    public static final String DOCTORS_CSV = DATA_DIR + "doctors.csv";
    public static final String APPOINTMENTS_CSV = DATA_DIR + "appointments.csv";

    // Date/time formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";

    // ID prefixes
    public static final String PATIENT_ID_PREFIX = "PAT";
    public static final String DOCTOR_ID_PREFIX = "DOC";
    public static final String APPOINTMENT_ID_PREFIX = "APT";
    public static final String BILL_ID_PREFIX = "BILL";

    // Application info
    public static final String APP_NAME = "MediTrack";
    public static final String APP_VERSION = "1.0";
    public static final String SEPARATOR = "=".repeat(60);
    public static final String THIN_SEPARATOR = "-".repeat(60);

    // Constraints
    public static final int MAX_APPOINTMENTS_PER_SLOT = 1;
}
