package com.airtribe.meditrack.enums;

/**
 * Medical specializations available at the clinic.
 */
public enum Specialization {
    CARDIOLOGY("Cardiology", "Heart and cardiovascular system"),
    NEUROLOGY("Neurology", "Brain and nervous system"),
    ORTHOPEDICS("Orthopedics", "Bones, joints, and muscles"),
    DERMATOLOGY("Dermatology", "Skin conditions and disorders"),
    PEDIATRICS("Pediatrics", "Children's health care"),
    GENERAL_MEDICINE("General Medicine", "General health care and diagnostics"),
    ONCOLOGY("Oncology", "Cancer diagnosis and treatment"),
    OPHTHALMOLOGY("Ophthalmology", "Eye care and vision"),
    ENT("ENT", "Ear, Nose, and Throat"),
    PSYCHIATRY("Psychiatry", "Mental health and behavioral disorders");

    private final String displayName;
    private final String description;

    Specialization(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    @Override
    public String toString() { return displayName; }
}
