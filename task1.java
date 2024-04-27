import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class task1 extends Thread {
    public static Semaphore startSemaphore;
    public static Semaphore entrySemaphore;
    public static int numPhilosophers;
    public static int philosophersFinishedEating; // Track the number of philosophers that have finished eating

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);

        System.out.println("Dining Philosophers Started");

        // Prompt the user for the number of philosophers
        System.out.print("How many philosophers should be created? (integer from 1-10000): ");
        numPhilosophers = scanner.nextInt();

        // Prompt the user for the number of meals
        System.out.print("How many meals should the philosophers eat? (integer from 1-10000): ");
        int numMeals = scanner.nextInt();

        scanner.close();

        // Validate input
        if (numPhilosophers < 1 || numPhilosophers > 10000 || numMeals < 1 || numMeals > 10000) {
            System.out.println("Invalid input. Number of philosophers and meals must be between 1 and 10000.");
            return;
        }

        // Initialize the start semaphore with 0 permits
        startSemaphore = new Semaphore(0);
        // Initialize the entry semaphore with 0 permits
        entrySemaphore = new Semaphore(0);
        // Initialize philosophersFinishedEating
        philosophersFinishedEating = 0;

        // Start the dining process with the given number of philosophers and meals
        startDining(numMeals);
    }

    private static void startDining(int numMeals) {
        // Create and start philosopher threads
        for (int i = 1; i <= numPhilosophers; i++) {
            Thread philosopher = new Philosopher(i, numMeals);
            philosopher.start();
        }

        // Wait for all philosophers to enter the room together
        try {
            entrySemaphore.acquire(numPhilosophers);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Release the start semaphore to allow philosophers to start
        startSemaphore.release(numPhilosophers);
    }

    static class Philosopher extends Thread {
        private final int id;
        private final int numMeals;
        private final Semaphore[] chopsticks; // Use an array to represent each philosopher's chopsticks
        private final Random random;

        public Philosopher(int id, int numMeals) {
            this.id = id;
            this.numMeals = numMeals;
            this.chopsticks = new Semaphore[2];
            this.chopsticks[0] = new Semaphore(1); // Each philosopher's left chopstick
            this.chopsticks[1] = new Semaphore(1); // Each philosopher's right chopstick
            this.random = new Random();
        }

        @Override
        public void run() {
            try {
                System.out.println("-Philosopher " + id + " starting.");
                entrySemaphore.release(); // Signal that philosopher has entered the room
                task1.startSemaphore.acquire(); // Wait for all philosophers to start together
                if (id == numPhilosophers) {
                    System.out.println("--All Philosophers have arrived.");
                }

                for (int meal = 1; meal <= numMeals; meal++) {
                    // Acquire left chopstick
                    System.out.println("---Philosopher " + id + " is waiting for left chopstick.");
                    chopsticks[0].acquire();
                    System.out.println("---Philosopher " + id + "'s left chopstick is acquired.");

                    // Simulate yield
                    Thread.sleep(random.nextInt(500)); // Random duration between 0-500 ms

                    // Acquire right chopstick
                    System.out.println("---Philosopher " + id + " is waiting for right chopstick.");
                    chopsticks[1].acquire();
                    System.out.println("---Philosopher " + id + "'s right chopstick is acquired.");

                    System.out.println("----Philosopher " + id + " grabs both chopsticks.");
                    System.out.println("----Philosopher " + id + " has a pair of chopsticks.");

                    System.out.println("-----Philosopher " + id + " is eating.");
                    System.out.println("Meals ate: " + meal);
                    Thread.sleep(random.nextInt(4000) + 3000); // Random duration between 3-6 cycles (3000-6000 ms)

                    // Release left chopstick
                    chopsticks[0].release();
                    System.out.println("-------Philosopher " + id + " dropped his left chopstick.");

                    // Release right chopstick
                    chopsticks[1].release();
                    System.out.println("-------Philosopher " + id + " dropped his right chopstick.");

                    System.out.println("--------Philosopher " + id + " is thinking.");
                    Thread.sleep(random.nextInt(4000) + 3000); // Random duration between 3-6 cycles (3000-6000 ms)
                }

                // Update philosophersFinishedEating
                synchronized (task1.class) {
                    philosophersFinishedEating++;
                    if (philosophersFinishedEating == numPhilosophers) {
                        System.out.println("---------All Philosophers have finished eating.");
                        // Iterate through philosophers in reverse order to show them leaving the table
                        for (int i = numPhilosophers; i >= 1; i--) {
                            System.out.println("----------Philosopher " + i + " has left the table.");
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
