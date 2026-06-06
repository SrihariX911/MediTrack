# Final MediTrack — Clinic & Appointment Management System

A modular, object-oriented Clinic & Appointment Management System implemented in **Core Java 17**.

## Features

| Category | Implemented |
|---|---|
| Core OOP | Encapsulation, Inheritance (`Person → Doctor / Patient`), Polymorphism (overloading + overriding + dynamic dispatch), Abstract classes (`MedicalEntity`), Interfaces (`Searchable<T>`, `Payable`) |
| Advanced OOP | Deep clone (`Patient`, `Appointment`), Immutable class (`BillSummary`), Enums (`Specialization`, `AppointmentStatus`), Static init blocks |
| Collections | `ArrayList`, `HashMap` via generic `DataStore<T>`, `Comparator` |
| Exception handling | Custom exceptions with chaining (`AppointmentNotFoundException`, `InvalidDataException`) |
| Design Patterns | Singleton (`IdGenerator`, `BillFactory`), Factory (`BillFactory`), Strategy (`BillingStrategy`), Observer (`AppointmentObserver`) |
| Java 8+ | Streams, Lambdas, `Optional`, method references |
| File I/O (Bonus A) | CSV save/load via `CSVUtil` with `try-with-resources` |
| AI Feature (Bonus C) | Symptom-based doctor recommendation (`AIHelper`) |
| Testing | Manual `TestRunner` — 56 tests, 0 failures |

## Quick Start

```bash
# Build
mvn package

# Run with sample data
java -jar target/meditrack.jar

# Run with persisted CSV data
java -jar target/meditrack.jar --loadData

# Run tests
java -cp target/meditrack.jar com.airtribe.meditrack.test.TestRunner
```

## Sample Output

```
============================================================
  Welcome to MediTrack v1.0
  Clinic & Appointment Management System
============================================================

  MediTrack v1.0 — Main Menu
  1. Patient Management
  2. Doctor Management
  3. Appointment Management
  4. Billing
  5. Analytics & Reports
  6. AI Doctor Recommendation
  7. Save Data to CSV
  0. Exit
```

```
--- Analytics & Reports (Streams + Lambdas) ---
  Total Patients  : 3
  Total Doctors   : 5
  Total Appointments: 3
  Avg Patient Age : 46.7
  Avg Doctor Fee  : 1300.00
  Total Revenue   : 2360.00

  Doctors by Specialization:
    Cardiology           : 1
    Neurology            : 1
    Dermatology          : 1
    General Medicine     : 1
    Pediatrics           : 1
```

## Project Structure

```
src/main/java/com/airtribe/meditrack/
├── Main.java
├── constants/Constants.java
├── entity/          MedicalEntity, Person, Doctor, Patient, Appointment, Bill, BillSummary
├── enums/           Specialization, AppointmentStatus
├── exception/       AppointmentNotFoundException, InvalidDataException
├── interfaces/      Searchable<T>, Payable
├── patterns/        BillingStrategy, StandardBillingStrategy, DiscountedBillingStrategy,
│                    AppointmentObserver, NotificationService, BillFactory
├── service/         DoctorService, PatientService, AppointmentService
├── test/            TestRunner
└── util/            Validator, DateUtil, IdGenerator, DataStore<T>, CSVUtil, AIHelper
docs/
├── JVM_Report.md
├── Setup_Instructions.md
└── Design_Decisions.md
```

## Documentation

- [Setup Instructions](docs/Setup_Instructions.md)
- [JVM Report](docs/JVM_Report.md)
- [Design Decisions](docs/Design_Decisions.md)
