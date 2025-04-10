package main

import (
    "fmt"
    "sync"
    "time"
)

// Task represents a unit of work
type Task string

// Queue represents a thread-safe task queue
type Queue struct {
    tasks []Task
    mu    sync.Mutex
    done  bool
}

// NewQueue creates a new Queue instance
func NewQueue() *Queue {
    return &Queue{
        tasks: make([]Task, 0),
        done:  false,
    }
}

// AddTask adds a task to the queue
func (q *Queue) AddTask(task Task) {
    q.mu.Lock()
    defer q.mu.Unlock()
    q.tasks = append(q.tasks, task)
}

// GetTask retrieves and removes a task from the queue
func (q *Queue) GetTask() (Task, bool) {
    q.mu.Lock()
    defer q.mu.Unlock()
    if len(q.tasks) == 0 {
        return "", q.done
    }
    task := q.tasks[0]
    q.tasks = q.tasks[1:]
    return task, false
}

// MarkDone signals that no more tasks will be added
func (q *Queue) MarkDone() {
    q.mu.Lock()
    defer q.mu.Unlock()
    q.done = true
}

// Worker represents a worker that processes tasks
type Worker struct {
    id    int
    queue *Queue
}

// NewWorker creates a new Worker instance
func NewWorker(id int, q *Queue) *Worker {
    return &Worker{
        id:    id,
        queue: q,
    }
}

// Start begins the worker's processing loop
func (w *Worker) Start() {
    for {
        task, done := w.queue.GetTask()
        if done && task == "" {
            Log(fmt.Sprintf("Worker %d shutting down", w.id))
            return
        }
        if task == "" {
            time.Sleep(100 * time.Millisecond)
            continue
        }
        fmt.Printf("Worker %d processing %s\n", w.id, task)
        time.Sleep(500 * time.Millisecond) // Simulate work
    }
}

// Log prints a timestamped message
func Log(message string) {
    fmt.Printf("[%s] %s\n", time.Now().Format("15:04:05"), message)
}

func main() {
    taskQueue := NewQueue()
    numWorkers := 5
    var wg sync.WaitGroup

    for i := 0; i < numWorkers; i++ {
        wg.Add(1)
        go func(workerID int) {
            defer wg.Done()
            w := NewWorker(workerID, taskQueue)
            w.Start()
        }(i)
    }

    for i := 0; i < 10; i++ {
        taskQueue.AddTask(Task(fmt.Sprintf("Task %d", i)))
        Log(fmt.Sprintf("Added Task %d to the queue", i))
        time.Sleep(100 * time.Millisecond)
    }

    // Signal that no more tasks will be added
    taskQueue.MarkDone()
    
    wg.Wait()
    Log("All tasks have been processed.")
}

