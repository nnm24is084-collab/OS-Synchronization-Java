# Operating Systems Programming Assignment

## AI-Assisted Synchronization Algorithms with Program-Generated HTML Simulation

**Student Name:** Anushka Garthe  
**USN:** NNM24IS084  
**Course:** Operating Systems  
**Programming Language:** Java

---

## 1. Problem Statement and Synchronization Objective

When multiple threads access shared data or resources concurrently, the result can depend on the order in which the threads execute. This situation can lead to race conditions, inconsistent data, and resource conflicts.

Synchronization mechanisms coordinate thread execution and control access to shared resources. This project implements two classic operating system synchronization problems: the Readers-Writers problem and the Dining Philosophers problem. Both problems are demonstrated using semaphore-based and monitor-based approaches.

The project also includes HTML simulations to visualize the behavior of the synchronization algorithms.

## 2. Objectives

- Understand synchronization in concurrent programming.
- Implement the Readers-Writers problem using semaphores and monitors.
- Implement the Dining Philosophers problem using semaphores and monitors.
- Demonstrate synchronization behavior through HTML simulations.
- Identify race conditions and resource conflicts.
- Document the implementation, execution, and observations.

## 3. Tools and Technologies

- Java Development Kit (JDK)
- Java threads and synchronization mechanisms
- Semaphores and monitors
- HTML for generated simulations
- Visual Studio Code
- Git and GitHub

## 4. Readers-Writers Problem

### 4.1 Problem Description

The Readers-Writers problem involves multiple threads accessing a shared resource. Readers only examine the resource, while writers modify it. Synchronization is necessary to prevent readers from accessing the resource while a writer is modifying it.

### 4.2 Semaphore Approach

The semaphore solution coordinates access to the shared resource and protects the shared reader state. Multiple readers may access the resource concurrently when no writer is writing. A writer must obtain exclusive access before modifying the resource.

### 4.3 Monitor Approach

The monitor solution coordinates access through shared state and controlled thread execution. Mutual exclusion and appropriate waiting conditions ensure that readers and writers follow the synchronization rules.

### 4.4 Correctness Requirements

- Multiple readers may read concurrently.
- Only one writer may write at a time.
- Readers must not read while a writer is writing.
- Shared state must be protected against race conditions.

## 5. Dining Philosophers Problem

### 5.1 Problem Description

The Dining Philosophers problem represents several philosophers sitting around a table, with a shared fork between neighboring philosophers. Each philosopher alternates between thinking and eating and requires two forks to eat.

Without synchronization, philosophers may compete for the same forks or become deadlocked while waiting for resources.

### 5.2 Semaphore Approach

Semaphores coordinate access to forks and control when philosophers may proceed. The implementation must ensure that a fork is not used by two neighboring philosophers simultaneously.

### 5.3 Monitor Approach

The monitor approach coordinates philosopher states and fork access through shared state and synchronization conditions. Philosophers may eat only when the required forks are available under the implementation's rules.

### 5.4 Correctness Requirements

- A fork cannot be used by two neighboring philosophers simultaneously.
- A philosopher must obtain the required forks before eating.
- Synchronization should prevent invalid resource access.
- Deadlock prevention depends on the implemented resource-allocation and waiting rules.

## 6. HTML Simulations

The project contains four generated HTML simulation files:

| Problem | Synchronization | HTML File |
|---|---|---|
| Readers-Writers | Semaphore | `readers_writers_semaphore.html` |
| Readers-Writers | Monitor | `readers_writers_monitor.html` |
| Dining Philosophers | Semaphore | `dining_philosophers_semaphore.html` |
| Dining Philosophers | Monitor | `dining_philosophers_monitor.html` |

The HTML files can be opened in a web browser to inspect the generated visualizations.

## 7. Project Directory Structure

```text
OS-Synchronization-Java/
├── README.md
├── run.bat
├── run.sh
├── readers_writers/
│   ├── semaphore/
│   └── monitor/
├── dining_philosophers/
│   ├── semaphore/
│   └── monitor/
├── generated_html/
├── screenshots/
├── ai_prompts/
├── references/
└── report/
    ├── Assignment_Report_Content.md
    └── Assignment_Report_Anushka_Garthe_NNM24IS084.docx
```

## 8. Execution Instructions

### Windows

Open PowerShell in the project root directory and run:

```powershell
.\run.bat
```

### Linux or macOS

Make the script executable and run it:

```bash
chmod +x run.sh
./run.sh
```

These scripts compile the Java source files included in the project. Open the existing HTML files in the `generated_html` directory separately to view the simulations.

## 9. Screenshots

The `screenshots` directory contains images of the four generated simulations:

1. Readers-Writers using semaphores.
2. Readers-Writers using monitors.
3. Dining Philosophers using semaphores.
4. Dining Philosophers using monitors.

These screenshots provide visual documentation of the simulation outputs.

## 10. Testing and Observations

The Java implementations and generated HTML simulations provide a way to examine the synchronization behavior of each solution.

The implementations should be checked against the correctness requirements described in Sections 4 and 5. Testing should verify that shared resources are accessed according to the synchronization rules and that threads do not enter invalid states.

Actual test results and any observed limitations should be recorded after executing and inspecting each implementation.

## 11. Conclusion

This project demonstrates the importance of synchronization in concurrent programming through the Readers-Writers and Dining Philosophers problems. The semaphore and monitor approaches illustrate different ways to coordinate threads and control access to shared resources.

The Java implementations, HTML simulations, screenshots, and documentation provide a basis for studying race conditions, mutual exclusion, resource coordination, and deadlock prevention.

## 12. References

Supporting references used during development are documented in `references/references.md`. AI prompts used during development are documented in `ai_prompts/prompts.md`.

## 13. Author

**Name:** Anushka Garthe  
**USN:** NNM24IS084  
**Course:** Operating Systems
