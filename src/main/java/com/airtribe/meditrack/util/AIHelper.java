package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.enums.Specialization;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Rule-based AI helper that recommends doctors from patient symptoms.
 * Demonstrates: static initialisation blocks, streams, and lambdas.
 */
public final class AIHelper {

    // Symptom → Specialization mapping initialised once at class load
    private static final Map<String, Specialization> SYMPTOM_MAP = new LinkedHashMap<>();

    static {
        SYMPTOM_MAP.put("chest pain",    Specialization.CARDIOLOGY);
        SYMPTOM_MAP.put("heart",         Specialization.CARDIOLOGY);
        SYMPTOM_MAP.put("palpitation",   Specialization.CARDIOLOGY);
        SYMPTOM_MAP.put("headache",      Specialization.NEUROLOGY);
        SYMPTOM_MAP.put("migraine",      Specialization.NEUROLOGY);
        SYMPTOM_MAP.put("seizure",       Specialization.NEUROLOGY);
        SYMPTOM_MAP.put("brain",         Specialization.NEUROLOGY);
        SYMPTOM_MAP.put("bone",          Specialization.ORTHOPEDICS);
        SYMPTOM_MAP.put("fracture",      Specialization.ORTHOPEDICS);
        SYMPTOM_MAP.put("joint",         Specialization.ORTHOPEDICS);
        SYMPTOM_MAP.put("arthritis",     Specialization.ORTHOPEDICS);
        SYMPTOM_MAP.put("skin",          Specialization.DERMATOLOGY);
        SYMPTOM_MAP.put("rash",          Specialization.DERMATOLOGY);
        SYMPTOM_MAP.put("acne",          Specialization.DERMATOLOGY);
        SYMPTOM_MAP.put("eczema",        Specialization.DERMATOLOGY);
        SYMPTOM_MAP.put("child",         Specialization.PEDIATRICS);
        SYMPTOM_MAP.put("infant",        Specialization.PEDIATRICS);
        SYMPTOM_MAP.put("cancer",        Specialization.ONCOLOGY);
        SYMPTOM_MAP.put("tumor",         Specialization.ONCOLOGY);
        SYMPTOM_MAP.put("eye",           Specialization.OPHTHALMOLOGY);
        SYMPTOM_MAP.put("vision",        Specialization.OPHTHALMOLOGY);
        SYMPTOM_MAP.put("ear",           Specialization.ENT);
        SYMPTOM_MAP.put("nose",          Specialization.ENT);
        SYMPTOM_MAP.put("throat",        Specialization.ENT);
        SYMPTOM_MAP.put("tonsil",        Specialization.ENT);
        SYMPTOM_MAP.put("mental",        Specialization.PSYCHIATRY);
        SYMPTOM_MAP.put("anxiety",       Specialization.PSYCHIATRY);
        SYMPTOM_MAP.put("depression",    Specialization.PSYCHIATRY);
        SYMPTOM_MAP.put("stress",        Specialization.PSYCHIATRY);
    }

    private AIHelper() {}

    /**
     * Maps a symptom description to a recommended specialization.
     * Falls back to GENERAL_MEDICINE when no keyword matches.
     */
    public static Specialization recommendSpecialization(String symptoms) {
        String lower = symptoms.toLowerCase();
        return SYMPTOM_MAP.entrySet().stream()
                .filter(e -> lower.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(Specialization.GENERAL_MEDICINE);
    }

    /**
     * Returns available doctors whose specialization matches the symptoms,
     * sorted by consultation fee (ascending).
     */
    public static List<Doctor> recommendDoctors(String symptoms, List<Doctor> allDoctors) {
        Specialization target = recommendSpecialization(symptoms);
        return allDoctors.stream()
                .filter(Doctor::isAvailable)
                .filter(d -> d.getSpecialization() == target)
                .sorted(java.util.Comparator.comparingDouble(Doctor::getConsultationFee))
                .collect(Collectors.toList());
    }

    public static String getSymptomHints() {
        return "Keywords recognised: " + String.join(", ", SYMPTOM_MAP.keySet());
    }
}
