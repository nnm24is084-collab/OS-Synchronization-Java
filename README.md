\# Operating Systems Programming Assignment

\## AI-Assisted Synchronization Algorithms with Program-Generated HTML Simulation



\*\*Student Name:\*\* Anushka Garthe  

\*\*USN:\*\* \[Enter your USN]  

\*\*Course:\*\* Operating Systems  

\*\*Programming Language:\*\* Java  

\*\*GitHub Repository:\*\* \[Add your repository URL]



\## 1. Project Overview



This project implements two classical operating system synchronization problems using semaphore-based and monitor-based approaches.



The four implementations are:



1\. Readers–Writers using Semaphores

2\. Readers–Writers using Monitors

3\. Dining Philosophers using Semaphores

4\. Dining Philosophers using Monitors



Each implementation generates an HTML visualization based on execution and state data produced by the Java program.



\## 2. Technologies Used



\- Java and JDK

\- Java concurrency and synchronization utilities

\- HTML, CSS and JavaScript for browser-based visualizations

\- Visual Studio Code and PowerShell

\- Git and GitHub for version control



\## 3. Project Structure



```text

OS-Synchronization-Java/

├── readers\_writers/

│   ├── semaphore/

│   └── monitor/

├── dining\_philosophers/

│   ├── semaphore/

│   └── monitor/

├── generated\_html/

│   ├── readers\_writers\_semaphore.html

│   ├── readers\_writers\_monitor.html

│   ├── dining\_philosophers\_semaphore.html

│   └── dining\_philosophers\_monitor.html

├── screenshots/

├── ai\_prompts/

│   └── prompts.md

├── references/

│   └── references.md

├── report/

└── README.md

```



\## 4. Compilation and Execution



Open PowerShell in the project root directory.



\### Readers–Writers: Semaphore



```powershell

javac .\\readers\_writers\\semaphore\\ReadersWritersSemaphore.java

java -cp .\\readers\_writers\\semaphore ReadersWritersSemaphore

```



\### Readers–Writers: Monitor



```powershell

javac .\\readers\_writers\\monitor\\ReadersWritersMonitor.java

java -cp .\\readers\_writers\\monitor ReadersWritersMonitor

```



\### Dining Philosophers: Semaphore



```powershell

javac .\\dining\_philosophers\\semaphore\\DiningPhilosophersSemaphore.java

java -cp .\\dining\_philosophers\\semaphore DiningPhilosophersSemaphore

```



\### Dining Philosophers: Monitor



```powershell

javac .\\dining\_philosophers\\monitor\\DiningPhilosophersMonitor.java

java -cp .\\dining\_philosophers\\monitor DiningPhilosophersMonitor

```



If a compilation command reports a missing class or dependency, follow the instructions for the actual source files in that implementation.



\## 5. Viewing HTML Simulations



After executing an implementation, open the generated HTML file in a browser.



To open the folder containing all simulations:



```powershell

explorer .\\generated\_html

```



The four generated HTML files are:



\- `readers\_writers\_semaphore.html`

\- `readers\_writers\_monitor.html`

\- `dining\_philosophers\_semaphore.html`

\- `dining\_philosophers\_monitor.html`



The simulations should reflect the execution data recorded by their corresponding Java programs.



\## 6. Synchronization Concepts



\### Readers–Writers Problem



Multiple readers may access a shared resource simultaneously, provided no writer is modifying it. Writers require exclusive access to prevent conflicting operations.



\### Dining Philosophers Problem



Four philosophers share four forks. Each philosopher needs the two adjacent forks to eat. The synchronization algorithm must prevent adjacent philosophers from eating simultaneously and address deadlock.



\### Race Conditions



A race condition occurs when concurrent operations access shared data without sufficient coordination, potentially producing inconsistent results.



\### Mutual Exclusion



Mutual exclusion ensures that conflicting operations do not enter a protected critical section simultaneously.



\### Deadlock



Deadlock occurs when processes or threads remain blocked indefinitely while waiting for resources held by one another.



\### Starvation



Starvation occurs when a thread waits indefinitely because other threads repeatedly obtain the resources it needs.



\## 7. Screenshots and Testing Evidence



The `screenshots/` directory contains screenshots of program execution and HTML simulations.



Screenshots should demonstrate the actual outputs of the four implementations. The source code and HTML visualizations should be tested before submission.



\## 8. AI Assistance



AI tools were used as programming and debugging assistants during development. Their assistance included explaining synchronization concepts, helping diagnose compilation issues, improving visualization behavior, and preparing documentation.



The specific tools, prompts, changes made, and limitations identified are documented in `ai\_prompts/prompts.md`. This disclosure should be updated to accurately reflect the tools and assistance actually used.



\## 9. References



Technical references and documentation are listed in `references/references.md`.



\## 10. Limitations



The behavior of each solution depends on its synchronization policy and implementation. Starvation prevention should not be claimed unless it is supported by the actual algorithm. HTML visualizations should be regenerated from the corresponding program execution.



\## 11. Academic Integrity



I am responsible for testing, understanding, and explaining the submitted implementations. AI assistance and external references must be disclosed, and the final report must accurately describe the actual code and observed results.



