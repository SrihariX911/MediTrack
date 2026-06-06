# MediTrack — Design Decisions

## Package Structure

| Package | Purpose |
|---|---|
| `entity` | Domain objects (Person hierarchy, Appointment, Bill, BillSummary) |
| `service` | Business logic and CRUD (DoctorService, PatientService, AppointmentService) |
| `util` | Stateless helpers (Validator, DateUtil, IdGenerator, DataStore, AIHelper, CSVUtil) |
| `exception` | Checked/unchecked domain exceptions |
| `interfaces` | Searchable<T> and Payable contracts |
| `enums` | Specialization and AppointmentStatus |
| `patterns` | Design pattern implementations (Strategy, Factory, Observer) |
| `test` | Manual TestRunner (no JUnit dependency) |

> **Note**: Java reserves the keyword `interface`, so the directory/package is named `interfaces`.

---

## OOP Design Decisions

### Inheritance hierarchy
```
MedicalEntity (abstract)
  └── Person (abstract)
        ├── Doctor
        └── Patient (implements Cloneable)
Appointment  (implements Cloneable)
Bill         (implements Payable)
BillSummary  (immutable final class)
```
`MedicalEntity` provides the `getId()` / `getDetails()` contract. `Person` adds identity fields and validation. Concrete subclasses override `getDetails()` — this is the **dynamic dispatch** demonstration.

### Why Person is abstract
Patients and Doctors are the only concrete people in the system. Making `Person` abstract prevents accidental instantiation and forces subclasses to provide `getDetails()`.

### Deep vs Shallow Copy
- **Patient.clone()** — uses a copy constructor that `new ArrayList<>(medicalHistory)`. A shallow `super.clone()` would share the list reference, letting the original's mutations silently affect the clone.
- **Appointment.clone()** — calls `patient.clone()` explicitly so the appointment snapshot owns an independent patient record. `Doctor` is _intentionally_ shared (a clinic-wide entity; cloning it would create orphaned duplicates).

### Immutable BillSummary
All fields are `final`. There are no setters. `LocalDateTime` is itself immutable, so no defensive copying is needed. The class is `final` to prevent subclasses from adding mutable state.

---

## Design Patterns

| Pattern | Where | Why |
|---|---|---|
| **Singleton** | `IdGenerator` (lazy, double-checked locking) | One counter sequence across the JVM |
| **Singleton** (eager) | `BillFactory` | Stateless factory — eager init is safe and simpler |
| **Factory** | `BillFactory` | Centralises Bill construction; callers only choose a strategy |
| **Strategy** | `BillingStrategy` / `StandardBillingStrategy` / `DiscountedBillingStrategy` | Swappable fee calculation without if/else chains |
| **Observer** | `AppointmentObserver` / `NotificationService` | Decouples lifecycle events from business logic |

---

## Generic DataStore<T>
`DataStore<T>` wraps a `HashMap<String, T>` and returns `Optional<T>` from `findById`. Services own a private store instance — no global state. This makes each service independently testable.

---

## Exception Hierarchy
- `AppointmentNotFoundException` (unchecked) — thrown when a lookup by ID fails; carries `appointmentId` for diagnostics.
- `InvalidDataException` (unchecked) — thrown by validators; carries `fieldName` and `invalidValue`.

Both extend `RuntimeException` so callers are not forced to handle them at every call site, but the context is preserved for logging or user-facing messages.

---

## Streams & Lambdas (Bonus D)
Used throughout services for:
- Filtering (`filter`)
- Sorting (`sorted` + `Comparator`)
- Aggregation (`average`, `sum`, `count`)
- Grouping (`Collectors.groupingBy`)

No mutable state is introduced inside stream pipelines.

---

## CSV Persistence (Bonus A)
- `CSVUtil.save*` — writes `PrintWriter` inside `try-with-resources`.
- `CSVUtil.load*` — reads `BufferedReader` inside `try-with-resources`.
- Commas inside field values are escaped to semicolons.
- Medical history entries within a patient row are pipe-delimited (`|`).
- Pass `--loadData` at startup to load from `data/` instead of seeding sample data.

---

## AI Helper (Bonus C)
A `static` keyword map initialised in a `static {}` block maps symptom keywords to `Specialization` enums. `recommendSpecialization` uses a stream to find the first matching entry. Falls back to `GENERAL_MEDICINE`.
