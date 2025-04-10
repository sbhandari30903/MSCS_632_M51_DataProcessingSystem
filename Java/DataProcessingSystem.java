import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataProcessingSystem {
    static class TaskQueue {
        private final Queue<String> tasks = new LinkedList<>();
        private final Lock lock = new ReentrantLock();
        private boolean isDone = false;

        public void addTask(String task) {
            lock.lock();
            try {
                tasks.add(task);
            } finally {
                lock.unlock();
            }
        }

        public Result getTask() {
            lock.lock();
            try {
                if (tasks.isEmpty()) {
                    return new Result("", isDone);
                }
                return new Result(tasks.poll(), false);
            } finally {
                lock.unlock();
            }
        }

        public void markDone() {
            lock.lock();
            try {
                isDone = true;
            } finally {
                lock.unlock();
            }
        }
    }

    static class Result {
        final String task;
        final boolean isDone;

        Result(String task, boolean isDone) {
            this.task = task;
            this.isDone = isDone;
        }
    }

    static class Worker implements Runnable {
        private final int id;
        private final TaskQueue queue;

        public Worker(int id, TaskQueue queue) {
            this.id = id;
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                Result result = queue.getTask();
                if (result.isDone && result.task.isEmpty()) {
                    log("Worker " + id + " shutting down");
                    return;
                }
                if (result.task.isEmpty()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    continue;
                }
                System.out.printf("Worker %d processing %s%n", id, result.task);
                try {
                    Thread.sleep(500); // Simulate work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private static void log(String message) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.printf("[%s] %s%n", timestamp, message);
    }

    public static void main(String[] args) throws InterruptedException {
        TaskQueue taskQueue = new TaskQueue();
        int numWorkers = 5;
        CountDownLatch latch = new CountDownLatch(numWorkers);

        // Start worker threads
        for (int i = 0; i < numWorkers; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    new Worker(workerId, taskQueue).run();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Add tasks
        for (int i = 0; i < 10; i++) {
            taskQueue.addTask("Task " + i);
            log("Added Task " + i + " to the queue");
            Thread.sleep(100);
        }

        // Signal that no more tasks will be added
        taskQueue.markDone();
        
        // Wait for all workers to finish
        latch.await();
        log("All tasks have been processed.");
    }
}