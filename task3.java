import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class task3 extends Thread {
    private static Semaphore mutex = new Semaphore(1);
    private static Semaphore rw_mutex = new Semaphore(1);
    private static Semaphore readTry = new Semaphore(1);
    private static int readCount = 0;
    private static int writeCount = 0;
    private static int maxReaders;
    private static int readers;
    private static int writers;

    public task3(int maxReaders, int readers, int writers) {
        this.maxReaders = maxReaders;
        this.readers = readers;
        this.writers = writers;
    }

    public static void main(String[] args) {
        // User input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Readers count between 1 and 2147483647: ");
        int readers = scanner.nextInt();

        System.out.print("Enter Writers count between 1 and 2147483647: ");
        int writers = scanner.nextInt();

        System.out.print("Enter Max_Readers count between 1 and 10: ");
        int maxReaders = scanner.nextInt();
        scanner.close();

        // Validate input
        if (readers <= 0 || writers <= 0 || maxReaders <= 0) {
            System.out.println("Invalid input. Readers, Writers, and Max_Readers should be greater than 0.");
            return;
        }

        // Start readers and writers
        for (int i = 0; i < readers + writers; i++) {
            if (i < readers) {
                new Reader(i).start();
            } else {
                new Writer(i).start();
            }
        }
    }

    static class Reader extends Thread {
        private int id;

        public Reader(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    readTry.acquire();
                    mutex.acquire();
                    if (readCount == 0) {
                        rw_mutex.acquire();
                    }
                    readCount++;
                    readTry.release();
                    mutex.release();

                    // Reading the file
                    System.out.println("R" + id + " started reading.");

                    Thread.sleep(1000); // Simulate reading time

                    // Reading finished
                    System.out.println("-R" + id + " finished reading. Total Reads:" + readCount);

                    mutex.acquire();
                    readCount--;
                    if (readCount == 0) {
                        rw_mutex.release();
                    }
                    mutex.release();

                    // Terminate after one read
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Writer extends Thread {
        private int id;

        public Writer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    readTry.acquire(); // Wait for all readers to finish
                    rw_mutex.acquire();
                    readTry.release(); // Allow readers to start reading again

                    // Writing to the file
                    System.out.println("--W" + id + " started writing");

                    Thread.sleep(2000); // Simulate writing time

                    // Writing finished
                    System.out.println("---W" + id + " finished writing");

                    rw_mutex.release();

                    // Terminate after one write
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
