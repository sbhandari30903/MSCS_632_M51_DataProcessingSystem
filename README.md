# Data Processing System

A parallel processing system implementation that demonstrates concurrent task processing using worker threads and thread-safe queues. The project includes implementations in both Go and Java.

## Project Structure

```
MSCS_632_M51_DataProcessingSystem/
├── Go/
│   ├── main.go
│   └── go.mod
├── Java/
│   └── DataProcessingSystem.java
└── README.md
```

## Features

- Thread-safe task queue implementation
- Multiple worker threads processing tasks concurrently
- Graceful shutdown mechanism
- Task processing simulation
- Timestamped logging
- Proper synchronization using mutex/locks

## Implementation Details

### Go Version
- Uses goroutines for concurrent processing
- Implements sync.Mutex for thread safety
- Uses WaitGroup for synchronization
- Channels for communication

### Java Version
- Uses Java Thread class for concurrent processing
- Implements ReentrantLock for thread safety
- Uses CountDownLatch for synchronization
- LinkedList-based queue implementation

## How to Run

### Go Implementation
```bash
cd Go
go run main.go
```

### Java Implementation
```bash
cd Java
javac DataProcessingSystem.java
java DataProcessingSystem
```

## System Components

1. **Task Queue**
   - Thread-safe implementation
   - Supports adding and retrieving tasks
   - Signals completion state

2. **Worker**
   - Processes tasks from the queue
   - Implements graceful shutdown
   - Simulates work with delays

3. **Main Program**
   - Creates worker threads
   - Generates sample tasks
   - Manages program lifecycle
   - Handles synchronization

## Output Example

```
[15:04:05] Added Task 0 to the queue
Worker 1 processing Task 0
[15:04:05] Added Task 1 to the queue
Worker 2 processing Task 1
...
[15:04:07] Worker 1 shutting down
[15:04:07] Worker 2 shutting down
[15:04:07] All tasks have been processed.
```

## Requirements

### Go Version
- Go 1.16 or higher

### Java Version
- Java 8 or higher
- JDK for compilation
