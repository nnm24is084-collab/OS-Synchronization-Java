
@echo off
setlocal
cd /d "%~dp0"

echo Compiling Readers-Writers Semaphore...
javac readers_writers\semaphore\ReadersWritersSemaphore.java
if errorlevel 1 exit /b 1

echo Compiling Readers-Writers Monitor...
javac readers_writers\monitor\ReadersWritersMonitor.java
if errorlevel 1 exit /b 1

echo Compiling Dining Philosophers Semaphore...
javac dining_philosophers\semaphore\DiningPhilosophersSemaphore.java
if errorlevel 1 exit /b 1

echo Compiling Dining Philosophers Monitor...
javac dining_philosophers\monitor\DiningPhilosophersMonitor.java
if errorlevel 1 exit /b 1

echo.
echo Compilation complete.
echo Open the generated HTML simulations in the generated_html folder.
pause
