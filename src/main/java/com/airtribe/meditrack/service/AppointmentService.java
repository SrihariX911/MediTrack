package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.patterns.AppointmentObserver;
import com.airtribe.meditrack.patterns.BillFactory;
import com.airtribe.meditrack.patterns.BillingStrategy;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;
import com.airtribe.meditrack.util.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Appointment lifecycle management with Observer notifications and billing integration.
 * Demonstrates: Observer pattern, Streams, exception chaining.
 */
public class AppointmentService {

    private final DataStore<Appointment> appointmentStore = new DataStore<>();
    private final DataStore<Bill>        billStore        = new DataStore<>();
    private final List<AppointmentObserver> observers     = new ArrayList<>();

    // --- Observer registration ---

    public void addObserver(AppointmentObserver observer) {
        Validator.requireNonNull(observer, "observer");
        observers.add(observer);
    }

    private void notifyCreated(Appointment a)   { observers.forEach(o -> o.onAppointmentCreated(a)); }
    private void notifyCancelled(Appointment a) { observers.forEach(o -> o.onAppointmentCancelled(a)); }
    private void notifyCompleted(Appointment a) { observers.forEach(o -> o.onAppointmentCompleted(a)); }

    // --- CRUD ---

    public Appointment createAppointment(Patient patient, Doctor doctor,
                                         LocalDateTime dateTime, String notes) {
        Validator.requireNonNull(patient, "patient");
        Validator.requireNonNull(doctor,  "doctor");
        Validator.requireNonNull(dateTime, "dateTime");
        Validator.requireTrue(doctor.isAvailable(), "Doctor " + doctor.getName() + " is not available");

        String id = IdGenerator.getInstance().generateAppointmentId();
        Appointment appt = new Appointment(id, patient, doctor, dateTime, notes);
        appointmentStore.save(id, appt);
        notifyCreated(appt);
        return appt;
    }

    /**
     * Saves an already-constructed Appointment (used by CSV loader).
     */
    public void saveAppointment(Appointment appointment) {
        Validator.requireNonNull(appointment, "appointment");
        appointmentStore.save(appointment.getId(), appointment);
    }

    public void cancelAppointment(String id) {
        Appointment appt = requireAppointment(id);
        Validator.requireTrue(appt.getStatus() != AppointmentStatus.CANCELLED,
                "Appointment " + id + " is already cancelled");
        appt.setStatus(AppointmentStatus.CANCELLED);
        notifyCancelled(appt);
    }

    public void completeAppointment(String id) {
        Appointment appt = requireAppointment(id);
        Validator.requireTrue(appt.getStatus() == AppointmentStatus.CONFIRMED,
                "Only CONFIRMED appointments can be completed");
        appt.setStatus(AppointmentStatus.COMPLETED);
        notifyCompleted(appt);
    }

    public void reschedule(String id, LocalDateTime newDateTime) {
        Appointment appt = requireAppointment(id);
        Validator.requireTrue(appt.getStatus() != AppointmentStatus.CANCELLED,
                "Cannot reschedule a cancelled appointment");
        appt.setAppointmentDateTime(newDateTime);
        appt.setStatus(AppointmentStatus.RESCHEDULED);
    }

    // --- Billing ---

    public Bill generateBill(String appointmentId, BillingStrategy strategy) {
        Appointment appt = requireAppointment(appointmentId);
        Validator.requireTrue(appt.getStatus() == AppointmentStatus.COMPLETED,
                "Bills can only be generated for COMPLETED appointments");
        Bill bill = BillFactory.getInstance().createBill(appt, strategy);
        billStore.save(bill.getId(), bill);
        return bill;
    }

    public void payBill(String billId) {
        Bill bill = billStore.findById(billId)
                .orElseThrow(() -> new InvalidDataException("No bill found with ID: " + billId));
        bill.pay();
    }

    // --- Queries ---

    public Appointment getAppointmentById(String id) {
        return appointmentStore.findById(id).orElse(null);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentStore.findAll();
    }

    public List<Appointment> getAppointmentsByPatient(String patientId) {
        return appointmentStore.findAll().stream()
                .filter(a -> a.getPatient().getId().equals(patientId))
                .sorted(Comparator.comparing(Appointment::getAppointmentDateTime))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        return appointmentStore.findAll().stream()
                .filter(a -> a.getDoctor().getId().equals(doctorId))
                .sorted(Comparator.comparing(Appointment::getAppointmentDateTime))
                .collect(Collectors.toList());
    }

    public List<Appointment> getByStatus(AppointmentStatus status) {
        return appointmentStore.findAll().stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Bill> getAllBills() {
        return billStore.findAll();
    }

    public List<Bill> getUnpaidBills() {
        return billStore.findAll().stream()
                .filter(b -> !b.isPaid())
                .collect(Collectors.toList());
    }

    // --- Stream analytics (Bonus D) ---

    public Map<String, Long> appointmentsPerDoctor() {
        return appointmentStore.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDoctor().getName(),
                        Collectors.counting()));
    }

    public double totalRevenue() {
        return billStore.findAll().stream()
                .filter(Bill::isPaid)
                .mapToDouble(Bill::getAmount)
                .sum();
    }

    public int totalAppointments() { return appointmentStore.count(); }

    // --- Internal helper ---

    private Appointment requireAppointment(String id) {
        return appointmentStore.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }
}
