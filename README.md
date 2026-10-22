# OS Synchronization in Java

## Operating Systems Programming Assignment

Project Title: AI-Assisted Synchronization Algorithms with Program-Generated HTML Simulation

**Problems Covered:**
- Readers-Writers Problem
- Dining Philosophers Problem

**Synchronization Techniques:**
- Semaphore
- Monitor

 1. Overview

This project demonstrates two classic operating system synchronization problems using Java. It explores how threads coordinate access to shared resources and how synchronization mechanisms help prevent race conditions and resource conflicts.

The project includes Java implementations, HTML simulations, screenshots, assignment documentation, AI prompts, and references.

 2. Objectives

- Understand thread synchronization and shared resources.
- Implement the Readers-Writers problem using semaphores and monitors.
- Implement the Dining Philosophers problem using semaphores and monitors.
- Demonstrate synchronization behavior through HTML simulations.
- Document implementation decisions, observations, and references.

 3. Project Structure


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
│   ├── readers_writers_semaphore.html
│   ├── readers_writers_monitor.html
│   ├── dining_philosophers_semaphore.html
│   └── dining_philosophers_monitor.html
├── screenshots/
│   ├── readers_writers_semaphore.png
│   ├── readers_writers_monitor.png
│   ├── dining_philosophers_semaphore.png
│   └── dining_philosophers_monitor.png
├── ai_prompts/
│   └── prompts.md
├── references/
│   └── references.md
└── report/
    └── Assignment_Report_Anushka_Garthe_NNM24IS084.docx

 4. Problem Descriptions

4.1 Readers-Writers Problem

The Readers-Writers problem involves multiple threads accessing a shared resource.

- Multiple readers may read the shared resource simultaneously.
- A writer requires exclusive access.
- Readers must not access the resource while a writer is writing.
- Synchronization must prevent conflicting access.

 4.2 Dining Philosophers Problem

The Dining Philosophers problem models multiple philosophers sharing a limited number of forks.

- Each philosopher alternates between thinking and eating.
- A philosopher needs two forks to eat.
- Fork access must be synchronized.
- The implementation should prevent conflicting fork usage and avoid deadlock under its synchronization rules.

 5. Synchronization Approaches

Semaphore-Based Solutions

Semaphores coordinate access to shared resources through acquire and release operations. They can limit concurrent access and enforce resource availability.

 Monitor-Based Solutions

Monitors coordinate threads through shared state, mutual exclusion, and condition-based waiting or notification. They provide a structured way to control access to shared resources.

The exact behavior and guarantees depend on the implementation of each solution.

6. Technologies Used

- **Programming language:** Java
- **Synchronization:** Semaphores and monitors
- **Visualization:** HTML
- **Development environment:** Visual Studio Code
- **Version control:** Git
- **Repository hosting:** GitHub

 7. Requirements

Install the Java Development Kit (JDK) and ensure the Java compiler is available in your terminal.

Check the installation:

```bash
java -version
javac -version


Git is required to clone or manage the repository.

 8. How to Run



Open PowerShell in the project root directory and run:

```powershell
.\run.bat
```

The script compiles the four main Java source files. After successful compilation, open the corresponding HTML files in the `generated_html` folder in a web browser to view the existing simulations.

 Linux or macOS

Make the shell script executable:

```bash
chmod +x run.sh
```

Then run:

```bash
./run.sh
```

The shell script compiles the four main Java source files. Open the HTML files in `generated_html` in a browser to view the existing simulations.

**Note:** These scripts compile the Java source files; they do not regenerate the HTML simulations. If compilation fails because a source file depends on additional Java files, compile the required source files together.

 9. HTML Simulations

The project includes four HTML simulation files:

| Simulation | File |
|---|---|
| Readers-Writers using Semaphore | `generated_html/readers_writers_semaphore.html` |
| Readers-Writers using Monitor | `generated_html/readers_writers_monitor.html` |
| Dining Philosophers using Semaphore | `generated_html/dining_philosophers_semaphore.html` |
| Dining Philosophers using Monitor | `generated_html/dining_philosophers_monitor.html` |

Open any of these files in a web browser to inspect the generated visualization.

 10. Screenshots

The `screenshots/` directory contains screenshots of the four simulations. These images document the visual output for the assignment.

 11. Documentation

- **Assignment report:** `report/`
- **AI prompts used during development:** `ai_prompts/prompts.md`
- **References:** `references/references.md`

Refer to these files for the report, development prompts, and supporting references.

 12. Expected Learning Outcomes

After completing this project, students should be able to:

- Explain the need for synchronization in concurrent programs.
- Distinguish semaphore-based and monitor-based synchronization.
- Identify race conditions and resource conflicts.
- Explain the Readers-Writers and Dining Philosophers problems.
- Run Java implementations and inspect HTML simulations.
- Use Git and GitHub to maintain and share project files.

 13. Author

**Name:** Anushka Garthe  
**USN:** NNM24IS084  
**Course:** Operating Systems
 

GitHub: https://github.com/nnm24is084-collab/OS-Synchronization-Java

---

*Academic project created for the Operating Systems Programming Assignment.*
