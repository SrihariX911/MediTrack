package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;
import com.airtribe.meditrack.util.Validator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CRUD and search operations for Doctor entities.
 * Demonstrates: Searchable interface, Java Streams, and Lambdas (Bonus D).
 */
public class DoctorService implements Searchable<Doctor> {

    private final DataStore<Doctor> store = new DataStore<>();

    // --- CRUD ---

    public Doctor addDoctor(String name, String phone, String email,
                            Specialization specialization, double consultationFee) {
        String id = IdGenerator.getInstance().generateDoctorId();
        Doctor doctor = new Doctor(id, name, phone, email, specialization, consultationFee);
        store.save(id, doctor);
        return doctor;
    }

    /**
     * Saves an already-constructed Doctor (used by CSV loader).
     */
    public void saveDoctor(Doctor doctor) {
        Validator.requireNonNull(doctor, "doctor");
        store.save(doctor.getId(), doctor);
    }

    public void updateDoctor(String id, String name, String phone, String email,
                             Specialization specialization, double consultationFee) {
        Doctor d = requireDoctor(id);
        if (name         != null) d.setName(name);
        if (phone        != null) d.setPhone(phone);
        if (email        != null) d.setEmail(email);
        if (specialization != null) d.setSpecialization(specialization);
        if (consultationFee > 0)  d.setConsultationFee(consultationFee);
    }

    public void removeDoctor(String id) {
        if (!store.delete(id)) {
            throw new InvalidDataException("No doctor found with ID: " + id);
        }
    }

    public List<Doctor> getAllDoctors() {
        return store.findAll();
    }

    // --- Searchable ---

    @Override
    public List<Doctor> search(String query) {
        String q = query.toLowerCase();
        return store.findAll().stream()
                .filter(d -> d.getName().toLowerCase().contains(q)
                          || d.getId().equalsIgnoreCase(q)
                          || d.getSpecialization().getDisplayName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    @Override
    public Doctor findById(String id) {
        return store.findById(id).orElse(null);
    }

    // --- Specialization search (overloaded) ---

    public List<Doctor> searchBySpecialization(Specialization spec) {
        return store.findAll().stream()
                .filter(d -> d.getSpecialization() == spec)
                .sorted(Comparator.comparing(Doctor::getName))
                .collect(Collectors.toList());
    }

    public List<Doctor> searchAvailable() {
        return store.findAll().stream()
                .filter(Doctor::isAvailable)
                .sorted(Comparator.comparing(Doctor::getName))
                .collect(Collectors.toList());
    }

    // --- Stream analytics (Bonus D) ---

    public double getAverageConsultationFee() {
        return store.findAll().stream()
                .mapToDouble(Doctor::getConsultationFee)
                .average()
                .orElse(0.0);
    }

    public Optional<Doctor> getCheapestDoctor() {
        return store.findAll().stream()
                .min(Comparator.comparingDouble(Doctor::getConsultationFee));
    }

    public Map<Specialization, Long> countBySpecialization() {
        return store.findAll().stream()
                .collect(Collectors.groupingBy(Doctor::getSpecialization, Collectors.counting()));
    }

    public List<Doctor> getDoctorsSortedByFee() {
        return store.findAll().stream()
                .sorted(Comparator.comparingDouble(Doctor::getConsultationFee))
                .collect(Collectors.toList());
    }

    public int totalDoctors() { return store.count(); }

    // --- Internal helper ---

    private Doctor requireDoctor(String id) {
        return store.findById(id).orElseThrow(
                () -> new InvalidDataException("No doctor found with ID: " + id));
    }
}
