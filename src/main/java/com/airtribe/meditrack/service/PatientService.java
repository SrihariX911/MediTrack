package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.IdGenerator;
import com.airtribe.meditrack.util.Validator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * CRUD and search operations for Patient entities.
 * Demonstrates: method overloading (searchPatient), Streams, Lambdas.
 */
public class PatientService implements Searchable<Patient> {

    private final DataStore<Patient> store = new DataStore<>();

    // --- CRUD ---

    public Patient addPatient(String name, String phone, String email, LocalDate dateOfBirth) {
        String id = IdGenerator.getInstance().generatePatientId();
        Patient patient = new Patient(id, name, phone, email, dateOfBirth);
        store.save(id, patient);
        return patient;
    }

    /**
     * Saves an already-constructed Patient (used by CSV loader).
     */
    public void savePatient(Patient patient) {
        Validator.requireNonNull(patient, "patient");
        store.save(patient.getId(), patient);
    }

    public void updatePatient(String id, String name, String phone, String email,
                              LocalDate dateOfBirth) {
        Patient p = requirePatient(id);
        if (name        != null) p.setName(name);
        if (phone       != null) p.setPhone(phone);
        if (email       != null) p.setEmail(email);
        if (dateOfBirth != null) p.setDateOfBirth(dateOfBirth);
    }

    public void removePatient(String id) {
        if (!store.delete(id)) {
            throw new InvalidDataException("No patient found with ID: " + id);
        }
    }

    public List<Patient> getAllPatients() {
        return store.findAll();
    }

    // --- Searchable ---

    /** Searches by name or ID (method from interface). */
    @Override
    public List<Patient> search(String query) {
        return searchPatient(query);
    }

    @Override
    public Patient findById(String id) {
        return store.findById(id).orElse(null);
    }

    // --- Overloaded searchPatient — demonstrates polymorphism (overloading) ---

    /** Search by name or ID. */
    public List<Patient> searchPatient(String nameOrId) {
        String q = nameOrId.toLowerCase();
        return store.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(q)
                          || p.getId().equalsIgnoreCase(q))
                .collect(Collectors.toList());
    }

    /** Search by exact age. */
    public List<Patient> searchPatient(int age) {
        return store.findAll().stream()
                .filter(p -> p.getAge() == age)
                .sorted(Comparator.comparing(Patient::getName))
                .collect(Collectors.toList());
    }

    /** Search by age range. */
    public List<Patient> searchPatient(int minAge, int maxAge) {
        return store.findAll().stream()
                .filter(p -> p.getAge() >= minAge && p.getAge() <= maxAge)
                .sorted(Comparator.comparingInt(Patient::getAge))
                .collect(Collectors.toList());
    }

    // --- Stream analytics (Bonus D) ---

    public OptionalDouble getAverageAge() {
        return store.findAll().stream()
                .mapToInt(Patient::getAge)
                .average();
    }

    public long countSeniorPatients(int ageThreshold) {
        return store.findAll().stream()
                .filter(p -> p.getAge() >= ageThreshold)
                .count();
    }

    public List<Patient> getPatientsSortedByName() {
        return store.findAll().stream()
                .sorted(Comparator.comparing(Patient::getName))
                .collect(Collectors.toList());
    }

    public int totalPatients() { return store.count(); }

    // --- Internal helper ---

    public Patient requirePatient(String id) {
        return store.findById(id).orElseThrow(
                () -> new InvalidDataException("No patient found with ID: " + id));
    }
}
