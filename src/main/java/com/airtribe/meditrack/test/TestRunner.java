package com.airtribe.meditrack.test;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.patterns.*;
import com.airtribe.meditrack.service.*;
import com.airtribe.meditrack.util.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Manual test runner — no JUnit dependency required.
 * Run with: java -cp target/meditrack.jar com.airtribe.meditrack.test.TestRunner
 */
public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println(Constants.SEPARATOR);
        System.out.println("  MediTrack Manual Test Suite");
        System.out.println(Constants.SEPARATOR);

        testIdGenerator();
        testValidation();
        testPatientCRUD();
        testDoctorCRUD();
        testDeepClone();
        testBillSummaryImmutability();
        testEnums();
        testAppointmentLifecycle();
        testBillingStrategies();
        testStreams();
        testExceptions();
        testAIHelper();
        testDataStore();

        System.out.println(Constants.SEPARATOR);
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println(Constants.SEPARATOR);
        System.exit(failed > 0 ? 1 : 0);
    }

    // =====================================================================
    // Test groups
    // =====================================================================

    private static void testIdGenerator() {
        section("IdGenerator (Singleton)");

        IdGenerator g1 = IdGenerator.getInstance();
        IdGenerator g2 = IdGenerator.getInstance();
        assertTrue("Same singleton instance", g1 == g2);

        String p1 = g1.generatePatientId();
        String p2 = g1.generatePatientId();
        assertTrue("Patient IDs are unique", !p1.equals(p2));
        assertTrue("Patient ID has correct prefix", p1.startsWith("PAT-"));

        String d = g1.generateDoctorId();
        assertTrue("Doctor ID has DOC prefix", d.startsWith("DOC-"));
    }

    private static void testValidation() {
        section("Validator");

        assertTrue("Valid email", Validator.isValidEmail("test@example.com"));
        assertFalse("Invalid email", Validator.isValidEmail("not-an-email"));
        assertTrue("Valid phone", Validator.isValidPhone("9876543210"));
        assertFalse("Short phone", Validator.isValidPhone("123"));
        assertTrue("Valid name", Validator.isValidName("Alice"));
        assertFalse("Null name", Validator.isValidName(null));

        assertThrows("requirePositive throws on zero", InvalidDataException.class,
                () -> Validator.requirePositive(0, "fee"));
        assertThrows("requireNonEmpty throws on blank", InvalidDataException.class,
                () -> Validator.requireNonEmpty("   ", "field"));
    }

    private static void testPatientCRUD() {
        section("PatientService CRUD");

        PatientService svc = new PatientService();
        Patient p = svc.addPatient("Bob Smith", "9876543210", "bob@test.com",
                LocalDate.of(1990, 5, 20));
        assertNotNull("Patient created", p);
        assertTrue("Stored by ID", svc.findById(p.getId()) != null);

        svc.updatePatient(p.getId(), "Robert Smith", null, null, null);
        assertTrue("Name updated", svc.findById(p.getId()).getName().equals("Robert Smith"));

        svc.removePatient(p.getId());
        assertTrue("Patient removed", svc.findById(p.getId()) == null);

        assertThrows("Remove non-existent throws", InvalidDataException.class,
                () -> svc.removePatient("PAT-0"));
    }

    private static void testDoctorCRUD() {
        section("DoctorService CRUD");

        DoctorService svc = new DoctorService();
        Doctor d = svc.addDoctor("Dr. Jane", "8765432109", "jane@clinic.com",
                Specialization.CARDIOLOGY, 1500.0);
        assertNotNull("Doctor created", d);
        assertTrue("Doctor has correct specialization",
                d.getSpecialization() == Specialization.CARDIOLOGY);

        List<Doctor> bySpec = svc.searchBySpecialization(Specialization.CARDIOLOGY);
        assertTrue("Search by specialization works", bySpec.size() == 1);

        double avg = svc.getAverageConsultationFee();
        assertTrue("Average fee calculated", avg == 1500.0);
    }

    private static void testDeepClone() {
        section("Deep Clone (Patient & Appointment)");

        Patient original = new Patient("PAT-T1", "Clone Test", "9000000000",
                "clone@test.com", LocalDate.of(1985, 1, 1));
        original.addMedicalHistory("Hypertension");

        Patient cloned = original.clone();
        assertTrue("Clone is a different object", original != cloned);
        assertTrue("Clone has same name", cloned.getName().equals(original.getName()));

        // Mutate original — clone must not be affected (deep copy)
        original.addMedicalHistory("Diabetes");
        assertTrue("Deep copy: clone history unchanged",
                cloned.getMedicalHistory().size() == 1);

        // Appointment deep clone
        Doctor doc = new Doctor("DOC-T1", "Dr. Test", "8000000000",
                "doc@test.com", Specialization.GENERAL_MEDICINE, 500.0);
        Appointment appt = new Appointment("APT-T1", original, doc,
                LocalDateTime.now().plusDays(1), "Checkup");
        Appointment apptClone = appt.clone();
        assertTrue("Appointment clone is new object", appt != apptClone);
        assertTrue("Appointment clone has independent patient",
                appt.getPatient() != apptClone.getPatient());
    }

    private static void testBillSummaryImmutability() {
        section("BillSummary (Immutable)");

        BillSummary summary = new BillSummary("Alice", "Dr. Bob", 1000.0, 180.0, 1180.0,
                "Standard", false);
        assertTrue("Patient name correct", "Alice".equals(summary.getPatientName()));
        assertTrue("Total correct", summary.getTotalAmount() == 1180.0);
        assertTrue("Not settled", !summary.isSettled());
        assertNotNull("Generated timestamp set", summary.getGeneratedAt());
        // No setters — immutability is enforced at compile time (final fields)
    }

    private static void testEnums() {
        section("Enums (Specialization & AppointmentStatus)");

        assertTrue("Cardiology display name",
                Specialization.CARDIOLOGY.getDisplayName().equals("Cardiology"));
        assertTrue("CONFIRMED display name",
                AppointmentStatus.CONFIRMED.getDisplayName().equals("Confirmed"));
        assertTrue("Enum ordinal stable", Specialization.NEUROLOGY.ordinal() == 1);
    }

    private static void testAppointmentLifecycle() {
        section("Appointment Lifecycle");

        PatientService  ps = new PatientService();
        DoctorService   ds = new DoctorService();
        AppointmentService as = new AppointmentService();
        as.addObserver(new com.airtribe.meditrack.patterns.NotificationService());

        Patient  p = ps.addPatient("Lifecycle Patient", "9111111111",
                "life@test.com", LocalDate.of(2000, 3, 15));
        Doctor   d = ds.addDoctor("Dr. Lifecycle", "9222222222",
                "lifedoc@test.com", Specialization.NEUROLOGY, 2000.0);

        Appointment appt = as.createAppointment(p, d, LocalDateTime.now().plusDays(2), "Test");
        assertTrue("Status CONFIRMED", appt.getStatus() == AppointmentStatus.CONFIRMED);

        as.completeAppointment(appt.getId());
        assertTrue("Status COMPLETED",
                as.getAppointmentById(appt.getId()).getStatus() == AppointmentStatus.COMPLETED);

        Bill bill = as.generateBill(appt.getId(), new StandardBillingStrategy());
        assertNotNull("Bill generated", bill);
        assertTrue("Bill not paid yet", !bill.isPaid());
        as.payBill(bill.getId());
        assertTrue("Bill paid", bill.isPaid());
    }

    private static void testBillingStrategies() {
        section("Billing Strategies (Strategy Pattern)");

        double fee = 1000.0;

        StandardBillingStrategy std = new StandardBillingStrategy();
        double base = std.calculateBase(fee);
        double tax  = std.calculateTax(base);
        assertTrue("Standard base is full fee", base == 1000.0);
        assertTrue("Standard tax is 18%", Math.abs(tax - 180.0) < 0.01);

        DiscountedBillingStrategy senior = DiscountedBillingStrategy.senior();
        double sBase = senior.calculateBase(fee);
        assertTrue("Senior base discounted", sBase < fee);

        BillFactory factory = BillFactory.getInstance();
        assertNotNull("BillFactory singleton not null", factory);
        // Eagerly loaded — always same instance
        assertTrue("Factory is singleton", factory == BillFactory.getInstance());
    }

    private static void testStreams() {
        section("Streams & Lambdas (Bonus D)");

        DoctorService ds = new DoctorService();
        ds.addDoctor("Dr. A", "9000000001", "a@test.com", Specialization.CARDIOLOGY, 1200.0);
        ds.addDoctor("Dr. B", "9000000002", "b@test.com", Specialization.NEUROLOGY, 1800.0);
        ds.addDoctor("Dr. C", "9000000003", "c@test.com", Specialization.CARDIOLOGY, 900.0);

        double avg = ds.getAverageConsultationFee();
        assertTrue("Average fee", Math.abs(avg - 1300.0) < 0.01);

        List<Doctor> cardiologists = ds.searchBySpecialization(Specialization.CARDIOLOGY);
        assertTrue("Filter by specialization count", cardiologists.size() == 2);

        List<Doctor> sorted = ds.getDoctorsSortedByFee();
        assertTrue("Sorted ascending — first cheapest",
                sorted.get(0).getConsultationFee() <= sorted.get(1).getConsultationFee());
    }

    private static void testExceptions() {
        section("Custom Exceptions");

        assertThrows("AppointmentNotFoundException",
                AppointmentNotFoundException.class,
                () -> { throw new AppointmentNotFoundException("APT-999"); });

        try {
            throw new AppointmentNotFoundException("APT-999");
        } catch (AppointmentNotFoundException e) {
            assertTrue("getId() returns ID", "APT-999".equals(e.getAppointmentId()));
        }

        assertThrows("InvalidDataException chaining",
                InvalidDataException.class,
                () -> { throw new InvalidDataException("Wrapped", new RuntimeException("cause")); });
    }

    private static void testAIHelper() {
        section("AIHelper (Bonus C)");

        Specialization s = AIHelper.recommendSpecialization("I have severe chest pain");
        assertTrue("Chest pain → CARDIOLOGY", s == Specialization.CARDIOLOGY);

        Specialization s2 = AIHelper.recommendSpecialization("my eye hurts");
        assertTrue("Eye → OPHTHALMOLOGY", s2 == Specialization.OPHTHALMOLOGY);

        Specialization s3 = AIHelper.recommendSpecialization("random unknown symptom xyz");
        assertTrue("Unknown → GENERAL_MEDICINE", s3 == Specialization.GENERAL_MEDICINE);
    }

    private static void testDataStore() {
        section("DataStore<T> (Generics)");

        DataStore<Patient> ds = new DataStore<>();
        Patient p = new Patient("P-1", "Store Test", "9000000099", "store@test.com",
                LocalDate.of(1995, 6, 10));
        ds.save("P-1", p);
        assertTrue("Count is 1", ds.count() == 1);
        assertTrue("Exists", ds.exists("P-1"));
        assertTrue("FindById", ds.findById("P-1").isPresent());
        ds.delete("P-1");
        assertTrue("Deleted", !ds.exists("P-1"));
    }

    // =====================================================================
    // Assertion helpers
    // =====================================================================

    private static void section(String name) {
        System.out.println("\n  [TEST] " + name);
    }

    private static void assertTrue(String label, boolean condition) {
        if (condition) {
            System.out.println("    PASS  " + label);
            passed++;
        } else {
            System.err.println("    FAIL  " + label);
            failed++;
        }
    }

    private static void assertFalse(String label, boolean condition) {
        assertTrue(label, !condition);
    }

    private static void assertNotNull(String label, Object obj) {
        assertTrue(label + " (not null)", obj != null);
    }

    private static <T extends Throwable> void assertThrows(String label,
                                                            Class<T> type,
                                                            Runnable action) {
        try {
            action.run();
            System.err.println("    FAIL  " + label + " (no exception thrown)");
            failed++;
        } catch (Throwable t) {
            if (type.isInstance(t)) {
                System.out.println("    PASS  " + label);
                passed++;
            } else {
                System.err.println("    FAIL  " + label + " (wrong exception: " + t.getClass().getSimpleName() + ")");
                failed++;
            }
        }
    }
}
