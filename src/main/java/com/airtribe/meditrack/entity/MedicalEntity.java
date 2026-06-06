package com.airtribe.meditrack.entity;

/**
 * Root abstraction for all identifiable clinical entities.
 * Demonstrates: abstract classes and the template-method pattern shape.
 */
public abstract class MedicalEntity {

    /**
     * Returns the unique identifier of this entity.
     */
    public abstract String getId();

    /**
     * Returns a human-readable summary of this entity's key attributes.
     */
    public abstract String getDetails();

    @Override
    public String toString() {
        return getDetails();
    }
}
