package hw_3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyCustomThreadPool {
    List<Thread> workers = new ArrayList<>();
    final LinkedList<Runnable> taskQueue = new LinkedList<>();
    private volatile boolean isRunning = true;

    public MyCustomThreadPool(int capacity) {
        for (int i = 0; i <= capacity; i++) {
            var name = "Worker N" + i;

            Thread worker = new Thread(() -> {
                while (isRunning || !taskQueue.isEmpty()) {
                    Runnable task;

                    synchronized (taskQueue) {
                        while (taskQueue.isEmpty()) {
                            if (!isRunning) return;

                            try {
                                taskQueue.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        task = taskQueue.removeFirst();
                    }

                    try {
                        task.run();
                    } catch (Exception e) {
                        System.out.println(name + " error: " + e.getMessage());
                    }
                }
            }, name);

            workers.add(worker);
            worker.start();
            System.out.println(name + " started");
        }
    }

    public void execute(Runnable task) {
        if (!isRunning) {
            awaitTermination();
            throw new IllegalStateException("Thread pull disabled");
        }

        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notify();
        }
    }

    public void shutdown() {
        isRunning = false;

        synchronized (taskQueue) {
            taskQueue.notifyAll();
        }
    }

    public void awaitTermination() {
        for (Thread thread : workers) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        runTest(5);
    }

    private static void runTest(int poolSize) throws InterruptedException {
        MyCustomThreadPool pool = new MyCustomThreadPool(poolSize);

        System.out.println("\n" + "Thread pool is initialized" + "\n");

        for (int i = 1; i <= 30; i++) {
            int taskId = i;

            pool.execute(() -> {
                System.out.println("Task " + taskId + " started" + Thread.currentThread().getName());

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {}

                System.out.println("Task " + taskId + " finished");
            });
        }

        pool.shutdown();
        pool.awaitTermination();
    }
}
