
#!/bin/bash
set -e
cd "$(dirname "$0")"

echo "Compiling Readers-Writers Semaphore..."
javac readers_writers/semaphore/ReadersWritersSemaphore.java

echo "Compiling Readers-Writers Monitor..."
javac readers_writers/monitor/ReadersWritersMonitor.java

echo "Compiling Dining Philosophers Semaphore..."
javac dining_philosophers/semaphore/DiningPhilosophersSemaphore.java

echo "Compiling Dining Philosophers Monitor..."
javac dining_philosophers/monitor/DiningPhilosophersMonitor.java

echo
echo "Compilation complete."
echo "Open the generated HTML simulations in the generated_html folder."
