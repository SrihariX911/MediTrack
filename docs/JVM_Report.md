# JVM Report — How Java Runs MediTrack

## 1. Class Loader Subsystem

When you run `java -cp target/meditrack.jar com.airtribe.meditrack.Main`, the **Class Loader** subsystem loads bytecode (`.class` files) from the JAR into the JVM. It follows a **delegation model**:

1. **Bootstrap ClassLoader** — loads core Java classes (`java.lang`, `java.util`, etc.) from the JDK's `rt.jar` / modules.
2. **Extension/Platform ClassLoader** — loads classes from `$JAVA_HOME/lib/ext`.
3. **Application ClassLoader** — loads MediTrack classes from `target/meditrack.jar` on the classpath.

Each loader delegates upward before loading itself — this prevents user code from accidentally replacing core classes.

In MediTrack, `static` blocks in `Person` and `IdGenerator` run exactly once, at the moment the JVM loads each class. You can see the log lines `[System] Person entity class loaded into JVM.` and `[IdGenerator] ID generator class initialised.` in the output.

---

## 2. Runtime Data Areas

```
┌─────────────────────────────────────────┐
│              JVM Memory                 │
│                                         │
│  ┌─────────┐   ┌──────────────────────┐ │
│  │  Method │   │         Heap         │ │
│  │  Area   │   │  (Objects live here) │ │
│  │ (static │   │  Patient, Doctor,    │ │
│  │ fields, │   │  Appointment, Bill…  │ │
│  │ bytecode│   └──────────────────────┘ │
│  └─────────┘                            │
│                                         │
│  Per-thread:                            │
│  ┌──────────────┐  ┌─────────────────┐  │
│  │  JVM Stack   │  │   PC Register   │  │
│  │  (frames /   │  │  (current instr)│  │
│  │  local vars) │  └─────────────────┘  │
│  └──────────────┘                       │
│  ┌─────────────────────────────────────┐ │
│  │  Native Method Stack (JNI calls)    │ │
│  └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

| Area | What lives there | MediTrack example |
|---|---|---|
| **Heap** | All object instances | `new Patient(...)`, `new Appointment(...)` |
| **Method Area** (Metaspace) | Class metadata, static fields, bytecode | `IdGenerator.instance`, `AIHelper.SYMPTOM_MAP` |
| **JVM Stack** | One frame per active method call; holds local variables and operands | `addPatient()` stack frame with `id`, `name` locals |
| **PC Register** | Address of the JVM instruction currently executing | Points to the current bytecode opcode |
| **Native Method Stack** | Used when Java calls native (C/C++) code via JNI | Used internally by `System.out.println` |

### Garbage Collection on the Heap
When a `Patient` is removed from `DataStore` (`store.remove(key)`), the object becomes unreachable. The GC (G1 by default in Java 17) reclaims the memory at the next collection cycle. MediTrack makes no manual memory management calls — the GC handles everything.

---

## 3. Execution Engine

The Execution Engine interprets or compiles bytecode to native machine instructions:

1. **Interpreter** — executes bytecode one instruction at a time. Fast startup but slow for hot loops.
2. **JIT Compiler** (Just-In-Time) — detects "hot" code paths (methods called frequently) and compiles them to native machine code at runtime. Subsequent calls execute native code directly — far faster than interpretation.
3. **HotSpot Profiler** — counts invocations and back-edges; feeds data to the JIT.

In MediTrack, the `mainMenu()` loop and the stream operations in `DoctorService.getAverageConsultationFee()` would become JIT candidates under sustained load.

---

## 4. JIT Compiler vs Interpreter

| Aspect | Interpreter | JIT Compiler |
|---|---|---|
| When it runs | Always (for un-compiled code) | After a method becomes "hot" |
| Speed | Slower (per-instruction overhead) | Faster (native CPU instructions) |
| Startup | Faster (no compilation step) | Slower (compilation cost on first calls) |
| Memory | Low | Higher (stores native code) |
| Optimisations | None | Inlining, loop unrolling, escape analysis, dead code elimination |

Java 17 uses **tiered compilation**: C1 (client) compiler produces lightly optimised code quickly; C2 (server) compiler produces heavily optimised native code for truly hot paths.

---

## 5. "Write Once, Run Anywhere"

Java source code is compiled by `javac` to platform-neutral **bytecode** (`.class` files). The bytecode specification is fixed — it is not tied to any CPU instruction set.

```
MediTrack source (.java)
        │  javac
        ▼
    Bytecode (.class / .jar)  ←── platform independent
        │
   ┌────┴─────┐
   │  JVM     │  ← platform specific (Windows / Linux / macOS / ARM / x86)
   └──────────┘
        │
  Native machine code
```

`target/meditrack.jar` built on Windows runs identically on Linux or macOS — provided a compatible JRE (Java 17+) is installed. The JVM translates the same bytecode to the appropriate native instructions for each platform.

This is possible because:
- The bytecode format and JVM specification are standardised (Java SE 17 spec).
- The JVM is ported to each platform separately by vendors (Oracle, Amazon Corretto, Eclipse Temurin, etc.).
- The standard library (`java.util`, `java.io`, etc.) abstracts OS differences.
