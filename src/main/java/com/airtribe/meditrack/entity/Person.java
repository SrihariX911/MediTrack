package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.util.Validator;

import java.util.Objects;

/**
 * Abstract representation of a person (patient or doctor) in the system.
 * Demonstrates: encapsulation, constructor chaining, and inheritance from MedicalEntity.
 */
public abstract class Person extends MedicalEntity {

    // Static initialisation block — runs once when Person class is loaded
    static {
        System.out.println("[System] Person entity class loaded into JVM.");
    }

    private String id;
    private String name;
    private String phone;
    private String email;

    /**
     * Primary constructor — validates all fields before assignment.
     */
    protected Person(String id, String name, String phone, String email) {
        Validator.requireNonEmpty(id, "id");
        Validator.requireValidName(name);
        Validator.requireValidPhone(phone);
        Validator.requireValidEmail(email);
        this.id    = id;
        this.name  = name.trim();
        this.phone = phone.trim();
        this.email = email.trim();
    }

    /** Copy constructor used by subclass clone implementations. */
    protected Person(Person other) {
        this.id    = other.id;
        this.name  = other.name;
        this.phone = other.phone;
        this.email = other.email;
    }

    // --- Getters ---

    @Override
    public String getId() { return id; }

    public String getName()  { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    // --- Validated setters ---

    public void setName(String name) {
        Validator.requireValidName(name);
        this.name = name.trim();
    }

    public void setPhone(String phone) {
        Validator.requireValidPhone(phone);
        this.phone = phone.trim();
    }

    public void setEmail(String email) {
        Validator.requireValidEmail(email);
        this.email = email.trim();
    }

    protected void setId(String id) {
        Validator.requireNonEmpty(id, "id");
        this.id = id;
    }

    // --- equals / hashCode based on business identity (ID) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
