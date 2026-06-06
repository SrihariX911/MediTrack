package com.airtribe.meditrack.util;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CSV persistence utilities.
 * Demonstrates: File I/O, try-with-resources, serialization via CSV (Bonus A).
 */
public final class CSVUtil {

    private static final String PATIENT_HEADER =
            "id,name,phone,email,dateOfBirth,medicalHistory";
    private static final String DOCTOR_HEADER =
            "id,name,phone,email,specialization,consultationFee,available";
    private static final String APPOINTMENT_HEADER =
            "id,patientId,doctorId,dateTime,status,notes";

    private CSVUtil() {}

    // =====================================================================
    // SAVE
    // =====================================================================

    public static void savePatients(List<Patient> patients) {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(Constants.PATIENTS_CSV))) {
            pw.println(PATIENT_HEADER);
            for (Patient p : patients) {
                String history = String.join("|", p.getMedicalHistory());
                pw.printf("%s,%s,%s,%s,%s,%s%n",
                        escape(p.getId()), escape(p.getName()),
                        escape(p.getPhone()), escape(p.getEmail()),
                        DateUtil.formatDate(p.getDateOfBirth()),
                        escape(history));
            }
            System.out.println("[CSVUtil] Saved " + patients.size() + " patients to " + Constants.PATIENTS_CSV);
        } catch (IOException e) {
            System.err.println("[CSVUtil] Failed to save patients: " + e.getMessage());
        }
    }

    public static void saveDoctors(List<Doctor> doctors) {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(Constants.DOCTORS_CSV))) {
            pw.println(DOCTOR_HEADER);
            for (Doctor d : doctors) {
                pw.printf("%s,%s,%s,%s,%s,%.2f,%b%n",
                        escape(d.getId()), escape(d.getName()),
                        escape(d.getPhone()), escape(d.getEmail()),
                        d.getSpecialization().name(),
                        d.getConsultationFee(), d.isAvailable());
            }
            System.out.println("[CSVUtil] Saved " + doctors.size() + " doctors to " + Constants.DOCTORS_CSV);
        } catch (IOException e) {
            System.err.println("[CSVUtil] Failed to save doctors: " + e.getMessage());
        }
    }

    public static void saveAppointments(List<Appointment> appointments) {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(Constants.APPOINTMENTS_CSV))) {
            pw.println(APPOINTMENT_HEADER);
            for (Appointment a : appointments) {
                pw.printf("%s,%s,%s,%s,%s,%s%n",
                        escape(a.getId()),
                        escape(a.getPatient().getId()),
                        escape(a.getDoctor().getId()),
                        DateUtil.formatDateTime(a.getAppointmentDateTime()),
                        a.getStatus().name(),
                        escape(a.getNotes()));
            }
            System.out.println("[CSVUtil] Saved " + appointments.size() + " appointments to " + Constants.APPOINTMENTS_CSV);
        } catch (IOException e) {
            System.err.println("[CSVUtil] Failed to save appointments: " + e.getMessage());
        }
    }

    // =====================================================================
    // LOAD
    // =====================================================================

    public static void loadPatients(PatientService patientService) {
        Path path = Paths.get(Constants.PATIENTS_CSV);
        if (!Files.exists(path)) {
            System.out.println("[CSVUtil] No patient CSV found — skipping.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.PATIENTS_CSV))) {
            String line = br.readLine(); // skip header
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", 6);
                if (parts.length < 5) continue;
                Patient patient = new Patient(
                        parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(),
                        LocalDate.parse(parts[4].trim()));
                if (parts.length == 6 && !parts[5].isBlank()) {
                    for (String entry : parts[5].split("\\|")) {
                        if (!entry.isBlank()) patient.addMedicalHistory(entry);
                    }
                }
                patientService.savePatient(patient);
                count++;
            }
            System.out.println("[CSVUtil] Loaded " + count + " patients.");
        } catch (IOException e) {
            System.err.println("[CSVUtil] Failed to load patients: " + e.getMessage());
        }
    }

    public static void loadDoctors(DoctorService doctorService) {
        Path path = Paths.get(Constants.DOCTORS_CSV);
        if (!Files.exists(path)) {
            System.out.println("[CSVUtil] No doctor CSV found — skipping.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.DOCTORS_CSV))) {
            br.readLine(); // skip header
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", 7);
                if (parts.length < 6) continue;
                Doctor doctor = new Doctor(
                        parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(),
                        Specialization.valueOf(parts[4].trim()),
                        Double.parseDouble(parts[5].trim()));
                if (parts.length == 7) {
                    doctor.setAvailable(Boolean.parseBoolean(parts[6].trim()));
                }
                doctorService.saveDoctor(doctor);
                count++;
            }
            System.out.println("[CSVUtil] Loaded " + count + " doctors.");
        } catch (IOException e) {
            System.err.println("[CSVUtil] Failed to load doctors: " + e.getMessage());
        }
    }

    public static void loadAppointments(AppointmentService appointmentService,
                                        PatientService patientService,
                                        DoctorService doctorService) {
        Path path = Paths.get(Constants.APPOINTMENTS_CSV);
        if (!Files.exists(path)) {
            System.out.println("[CSVUtil] No appointment CSV found — skipping.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.APPOINTMENTS_CSV))) {
            br.readLine(); // skip header
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", 6);
                if (parts.length < 5) continue;
                Patient patient = patientService.findById(parts[1].trim());
                Doctor  doctor  = doctorService.findById(parts[2].trim());
                if (patient == null || doctor == null) continue;
                LocalDateTime dt = DateUtil.parseDateTime(parts[3].trim());
                String notes = parts.length == 6 ? parts[5].trim() : "";
                Appointment appt = new Appointment(parts[0].trim(), patient, doctor, dt, notes);
                appt.setStatus(AppointmentStatus.valueOf(parts[4].trim()));
                appointmentService.saveAppointment(appt);
                count++;
            }
            System.out.println("[CSVUtil] Loaded " + count + " appointments.");
        } catch (IOException e) {
            System.err.println("[CSVUtil] Failed to load appointments: " + e.getMessage());
        }
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private static void ensureDataDir() {
        File dir = new File(Constants.DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
