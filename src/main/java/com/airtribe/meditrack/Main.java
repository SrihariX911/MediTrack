package com.airtribe.meditrack;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.patterns.*;
import com.airtribe.meditrack.service.*;
import com.airtribe.meditrack.util.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Entry point for MediTrack — menu-driven console application.
 * Pass {@code --loadData} as a command-line argument to load persisted CSV data on startup.
 */
public class Main {

    private static final PatientService     patientService     = new PatientService();
    private static final DoctorService      doctorService      = new DoctorService();
    private static final AppointmentService appointmentService = new AppointmentService();
    private static final Scanner            scanner            = new Scanner(System.in);

    static {
        // Register notification observer — demonstrates Observer pattern
        appointmentService.addObserver(new NotificationService());
    }

    public static void main(String[] args) {
        // Handle --loadData CLI argument (Bonus A)
        if (Arrays.asList(args).contains("--loadData")) {
            System.out.println("[Startup] Loading data from CSV files...");
            CSVUtil.loadDoctors(doctorService);
            CSVUtil.loadPatients(patientService);
            CSVUtil.loadAppointments(appointmentService, patientService, doctorService);
        } else {
            seedSampleData();
        }

        printBanner();
        mainMenu();
    }

    // =====================================================================
    // Main menu
    // =====================================================================

    private static void mainMenu() {
        while (true) {
            System.out.println("\n" + Constants.SEPARATOR);
            System.out.println("  " + Constants.APP_NAME + " v" + Constants.APP_VERSION + " — Main Menu");
            System.out.println(Constants.SEPARATOR);
            System.out.println("  1. Patient Management");
            System.out.println("  2. Doctor Management");
            System.out.println("  3. Appointment Management");
            System.out.println("  4. Billing");
            System.out.println("  5. Analytics & Reports");
            System.out.println("  6. AI Doctor Recommendation");
            System.out.println("  7. Save Data to CSV");
            System.out.println("  0. Exit");
            System.out.println(Constants.SEPARATOR);
            System.out.print("  Choice: ");

            switch (readLine()) {
                case "1" -> patientMenu();
                case "2" -> doctorMenu();
                case "3" -> appointmentMenu();
                case "4" -> billingMenu();
                case "5" -> analyticsMenu();
                case "6" -> aiRecommendation();
                case "7" -> saveData();
                case "0" -> { System.out.println("  Goodbye!"); return; }
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    // =====================================================================
    // Patient menu
    // =====================================================================

    private static void patientMenu() {
        while (true) {
            System.out.println("\n--- Patient Management ---");
            System.out.println("  1. Add Patient");
            System.out.println("  2. View All Patients");
            System.out.println("  3. Search Patient (by name/ID)");
            System.out.println("  4. Search Patient (by age)");
            System.out.println("  5. Update Patient");
            System.out.println("  6. Remove Patient");
            System.out.println("  7. Add Medical History Entry");
            System.out.println("  8. Demonstrate Deep Clone");
            System.out.println("  0. Back");
            System.out.print("  Choice: ");

            switch (readLine()) {
                case "1" -> addPatient();
                case "2" -> listPatients();
                case "3" -> searchPatientByName();
                case "4" -> searchPatientByAge();
                case "5" -> updatePatient();
                case "6" -> removePatient();
                case "7" -> addMedicalHistory();
                case "8" -> demonstrateClone();
                case "0" -> { return; }
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    private static void addPatient() {
        try {
            System.out.print("  Name        : "); String name = readLine();
            System.out.print("  Phone       : "); String phone = readLine();
            System.out.print("  Email       : "); String email = readLine();
            System.out.print("  Date of Birth (yyyy-MM-dd): "); LocalDate dob = DateUtil.parseDate(readLine());
            Patient p = patientService.addPatient(name, phone, email, dob);
            System.out.println("  Patient added: " + p.getDetails());
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void listPatients() {
        List<Patient> patients = patientService.getPatientsSortedByName();
        if (patients.isEmpty()) { System.out.println("  No patients registered."); return; }
        patients.forEach(p -> System.out.println("  " + p.getDetails()));
    }

    private static void searchPatientByName() {
        System.out.print("  Enter name or ID: ");
        List<Patient> results = patientService.searchPatient(readLine());
        if (results.isEmpty()) { System.out.println("  No results."); return; }
        results.forEach(p -> System.out.println("  " + p.getDetails()));
    }

    private static void searchPatientByAge() {
        try {
            System.out.print("  Enter age: "); int age = Integer.parseInt(readLine());
            List<Patient> results = patientService.searchPatient(age);
            if (results.isEmpty()) { System.out.println("  No results."); return; }
            results.forEach(p -> System.out.println("  " + p.getDetails()));
        } catch (NumberFormatException e) {
            System.out.println("  Invalid age.");
        }
    }

    private static void updatePatient() {
        System.out.print("  Patient ID: "); String id = readLine();
        Patient p = patientService.findById(id);
        if (p == null) { System.out.println("  Patient not found."); return; }
        System.out.print("  New name (enter to skip): ");  String name = readLine();
        System.out.print("  New phone (enter to skip): "); String phone = readLine();
        System.out.print("  New email (enter to skip): "); String email = readLine();
        try {
            patientService.updatePatient(id,
                    name.isBlank()  ? null : name,
                    phone.isBlank() ? null : phone,
                    email.isBlank() ? null : email,
                    null);
            System.out.println("  Updated: " + patientService.findById(id).getDetails());
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void removePatient() {
        System.out.print("  Patient ID: ");
        try {
            patientService.removePatient(readLine());
            System.out.println("  Patient removed.");
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void addMedicalHistory() {
        System.out.print("  Patient ID: "); String id = readLine();
        Patient p = patientService.findById(id);
        if (p == null) { System.out.println("  Patient not found."); return; }
        System.out.print("  History entry: "); p.addMedicalHistory(readLine());
        System.out.println("  Entry added. Total records: " + p.getMedicalHistory().size());
    }

    private static void demonstrateClone() {
        System.out.print("  Patient ID to clone: "); String id = readLine();
        Patient original = patientService.findById(id);
        if (original == null) { System.out.println("  Patient not found."); return; }
        Patient clone = original.clone();
        System.out.println("  Original: " + original.getDetails());
        System.out.println("  Clone   : " + clone.getDetails());
        System.out.println("  Same object? " + (original == clone));
        System.out.println("  (Adding history to original won't affect clone — deep copy)");
        original.addMedicalHistory("Test entry for clone demo");
        System.out.println("  Original history size: " + original.getMedicalHistory().size());
        System.out.println("  Clone    history size: " + clone.getMedicalHistory().size());
    }

    // =====================================================================
    // Doctor menu
    // =====================================================================

    private static void doctorMenu() {
        while (true) {
            System.out.println("\n--- Doctor Management ---");
            System.out.println("  1. Add Doctor");
            System.out.println("  2. View All Doctors");
            System.out.println("  3. Search Doctor (by name/specialization)");
            System.out.println("  4. Search by Specialization");
            System.out.println("  5. Update Doctor");
            System.out.println("  6. Remove Doctor");
            System.out.println("  7. Toggle Availability");
            System.out.println("  0. Back");
            System.out.print("  Choice: ");

            switch (readLine()) {
                case "1" -> addDoctor();
                case "2" -> listDoctors();
                case "3" -> searchDoctor();
                case "4" -> searchDoctorBySpec();
                case "5" -> updateDoctor();
                case "6" -> removeDoctor();
                case "7" -> toggleDoctorAvailability();
                case "0" -> { return; }
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    private static void addDoctor() {
        try {
            System.out.print("  Name        : "); String name = readLine();
            System.out.print("  Phone       : "); String phone = readLine();
            System.out.print("  Email       : "); String email = readLine();
            System.out.println("  Specializations:");
            Specialization[] specs = Specialization.values();
            for (int i = 0; i < specs.length; i++) {
                System.out.printf("    %d. %s%n", i + 1, specs[i].getDisplayName());
            }
            System.out.print("  Choice: ");
            int idx = Integer.parseInt(readLine()) - 1;
            System.out.print("  Consultation Fee: "); double fee = Double.parseDouble(readLine());
            Doctor d = doctorService.addDoctor(name, phone, email, specs[idx], fee);
            System.out.println("  Doctor added: " + d.getDetails());
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void listDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        if (doctors.isEmpty()) { System.out.println("  No doctors registered."); return; }
        doctors.forEach(d -> System.out.println("  " + d.getDetails()));
    }

    private static void searchDoctor() {
        System.out.print("  Query: ");
        List<Doctor> results = doctorService.search(readLine());
        if (results.isEmpty()) { System.out.println("  No results."); return; }
        results.forEach(d -> System.out.println("  " + d.getDetails()));
    }

    private static void searchDoctorBySpec() {
        Specialization[] specs = Specialization.values();
        for (int i = 0; i < specs.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, specs[i].getDisplayName());
        }
        System.out.print("  Choice: ");
        try {
            int idx = Integer.parseInt(readLine()) - 1;
            doctorService.searchBySpecialization(specs[idx])
                    .forEach(d -> System.out.println("  " + d.getDetails()));
        } catch (Exception e) {
            System.out.println("  Invalid selection.");
        }
    }

    private static void updateDoctor() {
        System.out.print("  Doctor ID: "); String id = readLine();
        Doctor d = doctorService.findById(id);
        if (d == null) { System.out.println("  Doctor not found."); return; }
        System.out.print("  New name (enter to skip): ");  String name = readLine();
        System.out.print("  New fee (0 to skip): ");
        double fee = 0;
        try { fee = Double.parseDouble(readLine()); } catch (Exception ignored) {}
        try {
            doctorService.updateDoctor(id, name.isBlank() ? null : name,
                    null, null, null, fee);
            System.out.println("  Updated.");
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void removeDoctor() {
        System.out.print("  Doctor ID: ");
        try {
            doctorService.removeDoctor(readLine());
            System.out.println("  Doctor removed.");
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void toggleDoctorAvailability() {
        System.out.print("  Doctor ID: "); String id = readLine();
        Doctor d = doctorService.findById(id);
        if (d == null) { System.out.println("  Doctor not found."); return; }
        d.setAvailable(!d.isAvailable());
        System.out.println("  Availability set to: " + d.isAvailable());
    }

    // =====================================================================
    // Appointment menu
    // =====================================================================

    private static void appointmentMenu() {
        while (true) {
            System.out.println("\n--- Appointment Management ---");
            System.out.println("  1. Book Appointment");
            System.out.println("  2. View All Appointments");
            System.out.println("  3. View by Patient");
            System.out.println("  4. View by Doctor");
            System.out.println("  5. Cancel Appointment");
            System.out.println("  6. Complete Appointment");
            System.out.println("  7. Reschedule Appointment");
            System.out.println("  8. Demonstrate Appointment Clone");
            System.out.println("  0. Back");
            System.out.print("  Choice: ");

            switch (readLine()) {
                case "1" -> bookAppointment();
                case "2" -> listAppointments();
                case "3" -> appointmentsByPatient();
                case "4" -> appointmentsByDoctor();
                case "5" -> cancelAppointment();
                case "6" -> completeAppointment();
                case "7" -> reschedule();
                case "8" -> demonstrateAppointmentClone();
                case "0" -> { return; }
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    private static void bookAppointment() {
        try {
            System.out.print("  Patient ID: "); String pid = readLine();
            Patient patient = patientService.findById(pid);
            if (patient == null) { System.out.println("  Patient not found."); return; }

            System.out.print("  Doctor ID: "); String did = readLine();
            Doctor doctor = doctorService.findById(did);
            if (doctor == null) { System.out.println("  Doctor not found."); return; }

            System.out.print("  Date/Time (yyyy-MM-dd HH:mm): ");
            LocalDateTime dt = DateUtil.parseDateTime(readLine());
            System.out.print("  Notes (optional): "); String notes = readLine();

            Appointment appt = appointmentService.createAppointment(patient, doctor, dt, notes);
            System.out.println("  Booked: " + appt.getDetails());
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void listAppointments() {
        List<Appointment> list = appointmentService.getAllAppointments();
        if (list.isEmpty()) { System.out.println("  No appointments."); return; }
        list.forEach(a -> System.out.println("  " + a.getDetails()));
    }

    private static void appointmentsByPatient() {
        System.out.print("  Patient ID: ");
        appointmentService.getAppointmentsByPatient(readLine())
                .forEach(a -> System.out.println("  " + a.getDetails()));
    }

    private static void appointmentsByDoctor() {
        System.out.print("  Doctor ID: ");
        appointmentService.getAppointmentsByDoctor(readLine())
                .forEach(a -> System.out.println("  " + a.getDetails()));
    }

    private static void cancelAppointment() {
        System.out.print("  Appointment ID: ");
        try {
            appointmentService.cancelAppointment(readLine());
            System.out.println("  Appointment cancelled.");
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void completeAppointment() {
        System.out.print("  Appointment ID: ");
        try {
            appointmentService.completeAppointment(readLine());
            System.out.println("  Appointment marked complete.");
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void reschedule() {
        System.out.print("  Appointment ID: "); String id = readLine();
        System.out.print("  New Date/Time (yyyy-MM-dd HH:mm): ");
        try {
            LocalDateTime dt = DateUtil.parseDateTime(readLine());
            appointmentService.reschedule(id, dt);
            System.out.println("  Rescheduled.");
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void demonstrateAppointmentClone() {
        System.out.print("  Appointment ID to clone: "); String id = readLine();
        Appointment original = appointmentService.getAppointmentById(id);
        if (original == null) { System.out.println("  Appointment not found."); return; }
        Appointment clone = original.clone();
        System.out.println("  Original patient ref == Clone patient ref? "
                + (original.getPatient() == clone.getPatient())
                + " (false = deep copy)");
        System.out.println("  Original: " + original.getDetails());
        System.out.println("  Clone   : " + clone.getDetails());
    }

    // =====================================================================
    // Billing menu
    // =====================================================================

    private static void billingMenu() {
        while (true) {
            System.out.println("\n--- Billing ---");
            System.out.println("  1. Generate Bill (Standard)");
            System.out.println("  2. Generate Bill (Senior Discount)");
            System.out.println("  3. Generate Bill (General Discount)");
            System.out.println("  4. View All Bills");
            System.out.println("  5. Pay a Bill");
            System.out.println("  0. Back");
            System.out.print("  Choice: ");

            switch (readLine()) {
                case "1" -> generateBill(new StandardBillingStrategy());
                case "2" -> generateBill(DiscountedBillingStrategy.senior());
                case "3" -> generateBill(DiscountedBillingStrategy.general());
                case "4" -> listBills();
                case "5" -> payBill();
                case "0" -> { return; }
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    private static void generateBill(BillingStrategy strategy) {
        System.out.print("  Appointment ID: ");
        try {
            Bill bill = appointmentService.generateBill(readLine(), strategy);
            System.out.println("  " + bill.getDetails());
            System.out.println("  " + bill.generateSummary());
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void listBills() {
        List<Bill> bills = appointmentService.getAllBills();
        if (bills.isEmpty()) { System.out.println("  No bills."); return; }
        bills.forEach(b -> System.out.println("  " + b.getDetails()));
    }

    private static void payBill() {
        System.out.print("  Bill ID: ");
        try {
            appointmentService.payBill(readLine());
            System.out.println("  Payment recorded.");
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // =====================================================================
    // Analytics menu
    // =====================================================================

    private static void analyticsMenu() {
        System.out.println("\n--- Analytics & Reports (Streams + Lambdas) ---");
        System.out.printf("  Total Patients  : %d%n", patientService.totalPatients());
        System.out.printf("  Total Doctors   : %d%n", doctorService.totalDoctors());
        System.out.printf("  Total Appointments: %d%n", appointmentService.totalAppointments());
        System.out.printf("  Avg Patient Age : %.1f%n",
                patientService.getAverageAge().orElse(0.0));
        System.out.printf("  Avg Doctor Fee  : ₹%.2f%n", doctorService.getAverageConsultationFee());
        System.out.printf("  Total Revenue   : ₹%.2f%n", appointmentService.totalRevenue());

        System.out.println("\n  Doctors by Specialization:");
        doctorService.countBySpecialization()
                .forEach((spec, count) ->
                        System.out.printf("    %-20s : %d%n", spec.getDisplayName(), count));

        System.out.println("\n  Appointments per Doctor:");
        appointmentService.appointmentsPerDoctor()
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.printf("    %-25s : %d%n", e.getKey(), e.getValue()));

        long seniors = patientService.countSeniorPatients(Constants.SENIOR_AGE_THRESHOLD);
        System.out.printf("%n  Senior Patients (age >= %d): %d%n",
                Constants.SENIOR_AGE_THRESHOLD, seniors);
    }

    // =====================================================================
    // AI Recommendation
    // =====================================================================

    private static void aiRecommendation() {
        System.out.println("\n--- AI Doctor Recommendation ---");
        System.out.println("  Hint: " + AIHelper.getSymptomHints());
        System.out.print("  Describe your symptoms: ");
        String symptoms = readLine();
        com.airtribe.meditrack.enums.Specialization rec = AIHelper.recommendSpecialization(symptoms);
        System.out.println("  Recommended Specialization: " + rec.getDisplayName());
        System.out.println("  Description: " + rec.getDescription());

        List<Doctor> recommended = AIHelper.recommendDoctors(symptoms, doctorService.getAllDoctors());
        if (recommended.isEmpty()) {
            System.out.println("  No available doctors found for this specialization.");
        } else {
            System.out.println("  Available doctors (sorted by fee):");
            recommended.forEach(d -> System.out.println("    " + d.getDetails()));
        }
    }

    // =====================================================================
    // Save data
    // =====================================================================

    private static void saveData() {
        CSVUtil.savePatients(patientService.getAllPatients());
        CSVUtil.saveDoctors(doctorService.getAllDoctors());
        CSVUtil.saveAppointments(appointmentService.getAllAppointments());
        System.out.println("  All data saved to CSV files.");
    }

    // =====================================================================
    // Sample data seed (used when --loadData is NOT passed)
    // =====================================================================

    private static void seedSampleData() {
        System.out.println("[Startup] Seeding sample data...");

        // Doctors
        Doctor d1 = doctorService.addDoctor("Dr. Priya Sharma",   "9876500001", "priya@clinic.com",
                Specialization.CARDIOLOGY,    1500.0);
        Doctor d2 = doctorService.addDoctor("Dr. Arjun Mehta",    "9876500002", "arjun@clinic.com",
                Specialization.NEUROLOGY,     2000.0);
        Doctor d3 = doctorService.addDoctor("Dr. Sunita Rao",     "9876500003", "sunita@clinic.com",
                Specialization.DERMATOLOGY,   1200.0);
        Doctor d4 = doctorService.addDoctor("Dr. Kiran Patel",    "9876500004", "kiran@clinic.com",
                Specialization.GENERAL_MEDICINE, 800.0);
        Doctor d5 = doctorService.addDoctor("Dr. Meena Joshi",    "9876500005", "meena@clinic.com",
                Specialization.PEDIATRICS,    1000.0);

        // Patients
        Patient p1 = patientService.addPatient("Ravi Kumar",   "9988776655", "ravi@mail.com",
                LocalDate.of(1985, 3, 12));
        p1.addMedicalHistory("Hypertension diagnosed 2020");

        Patient p2 = patientService.addPatient("Anita Desai",  "9988776644", "anita@mail.com",
                LocalDate.of(1995, 8, 22));

        Patient p3 = patientService.addPatient("Mohan Singh",  "9988776633", "mohan@mail.com",
                LocalDate.of(1955, 1, 5));
        p3.addMedicalHistory("Diabetes Type 2");
        p3.addMedicalHistory("Knee replacement 2019");

        // Appointments
        Appointment a1 = appointmentService.createAppointment(p1, d1,
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0), "Routine checkup");
        Appointment a2 = appointmentService.createAppointment(p2, d3,
                LocalDateTime.now().plusDays(2).withHour(11).withMinute(30), "Skin rash");
        Appointment a3 = appointmentService.createAppointment(p3, d2,
                LocalDateTime.now().minusDays(1).withHour(9).withMinute(0), "Headaches");

        // Complete one and generate a bill
        appointmentService.completeAppointment(a3.getId());
        Bill bill = appointmentService.generateBill(a3.getId(), new StandardBillingStrategy());

        System.out.println("[Startup] Sample data ready. " +
                patientService.totalPatients() + " patients, " +
                doctorService.totalDoctors() + " doctors, " +
                appointmentService.totalAppointments() + " appointments.");
    }

    // =====================================================================
    // Utilities
    // =====================================================================

    private static void printBanner() {
        System.out.println(Constants.SEPARATOR);
        System.out.println("  Welcome to " + Constants.APP_NAME + " v" + Constants.APP_VERSION);
        System.out.println("  Clinic & Appointment Management System");
        System.out.println(Constants.SEPARATOR);
    }

    private static String readLine() {
        return scanner.nextLine().trim();
    }
}
