# Setup Instructions

## Prerequisites

| Tool | Version | Download |
|---|---|---|
| JDK | 17 or later | https://adoptium.net |
| Maven | 3.8+ | https://maven.apache.org/download.cgi |
| Git | Any | https://git-scm.com |

### Verify installation

```bash
java -version
# Expected: openjdk 17.x.x ...

mvn -version
# Expected: Apache Maven 3.x.x ...
```

---

## Clone & Build

```bash
git clone <your-repo-url>
cd MediTrack

# Compile and package
mvn package

# Verify the JAR was produced
ls target/meditrack.jar
```

---

## Run the Application

### Option A — with sample (seeded) data
```bash
java -jar target/meditrack.jar
```
Five sample doctors, three patients, and three appointments are created automatically.

### Option B — load persisted CSV data
```bash
java -jar target/meditrack.jar --loadData
```
Reads `data/patients.csv`, `data/doctors.csv`, and `data/appointments.csv`.  
Use **Save Data to CSV** from the main menu to create these files first.

---

## Run the Manual Test Suite

```bash
java -cp target/meditrack.jar com.airtribe.meditrack.test.TestRunner
```

Expected output ends with:
```
Results: 56 passed, 0 failed
```

---

## Project Structure (after build)

```
MediTrack/
├── pom.xml
├── data/                        # CSV persistence (auto-created on save)
├── docs/
│   ├── Setup_Instructions.md
│   ├── JVM_Report.md
│   └── Design_Decisions.md
├── src/
│   └── main/java/com/airtribe/meditrack/
│       ├── Main.java
│       ├── constants/Constants.java
│       ├── entity/              # MedicalEntity, Person, Doctor, Patient, Appointment, Bill, BillSummary
│       ├── enums/               # Specialization, AppointmentStatus
│       ├── exception/           # AppointmentNotFoundException, InvalidDataException
│       ├── interfaces/          # Searchable<T>, Payable
│       ├── patterns/            # BillingStrategy, BillFactory, AppointmentObserver, NotificationService
│       ├── service/             # DoctorService, PatientService, AppointmentService
│       ├── test/                # TestRunner
│       └── util/                # Validator, DateUtil, IdGenerator, DataStore, CSVUtil, AIHelper
└── target/
    └── meditrack.jar
```

---

## Sample Menu Navigation

```
Main Menu
  1 → Patient Management
  2 → Doctor Management
  3 → Appointment Management
  4 → Billing
  5 → Analytics & Reports
  6 → AI Doctor Recommendation
  7 → Save Data to CSV
  0 → Exit
```

**Typical workflow:**
1. Add a doctor (menu 2 → 1)
2. Add a patient (menu 1 → 1)
3. Book an appointment (menu 3 → 1) — enter the patient and doctor IDs printed above
4. Complete the appointment (menu 3 → 6)
5. Generate a bill (menu 4 → 1)
6. Pay the bill (menu 4 → 5)
7. View analytics (menu 5)
8. Save to CSV (menu 7)

---

## Generate JavaDoc

```bash
mvn javadoc:javadoc
# HTML output: target/site/apidocs/index.html
```
