# QueueManagementSystem
## Overview

This project simulates a queue management system where multiple clients are distributed among several servers (queues) based on selected strategies. It includes both a Graphical User Interface (GUI) built with Java Swing and backend simulation logic following design patterns like Singleton.

Users can:
- Input the number of clients, queues, simulation time, arrival times, service times
- Select the dispatching strategy: `Shortest Queue` or `Shortest Time`
- View the simulation in real time
- Save the simulation log to a `.txt` file

## Technologies Used
- Java 17+
- Java Swing(for GUI)
- Multithreading (`Runnable`)
- AtomicInteger (for concurrency-safe counters)
- Singleton Design Pattern (for `SimulationManager`)
- UML Diagrams: Class diagram, package diagram, use case diagram

## Features
- Dynamic Client Generation: Random clients with random arrival and service times within user-defined bounds.
- Queue Dispatching Strategies:
    - Shortest Queue: Assigns to the server with the least number of total clients (including current processing).
    - Shortest Time: Assigns to the server with the shortest total waiting time.
- Real-Time Simulation Log:
  - Displays clients in each queue
  - Shows active processing
  - Records waiting clients
- File Saving:
  - Save the full log output to a `.txt` file.
- Multithreaded Simulation:
  - Each server processes clients independently.
- Thread-Safe Singleton:
  - `SimulationManager` ensures only one simulation instance at a time.
