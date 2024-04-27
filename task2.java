import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class task2 extends Thread {
    public static void main(String[] args) {
        task2 task2 = new task2();
        task2.start();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        // Prompt for input
        System.out.print("Enter the number of people participating in the simulation: ");
        int N = scanner.nextInt();

        System.out.print("Enter the size of each person's mailbox: ");
        int S = scanner.nextInt();

        System.out.print("Enter the total number of messages to send: ");
        int M = scanner.nextInt();

        scanner.close();

        // Input validation
        if (N <= 0 || S <= 0 || M <= 0) {
            System.out.println("Invalid input. Please enter positive values for all parameters.");
            return;
        }

        // Start the simulation
        PostOfficeSimulation simulation = new PostOfficeSimulation(N, S, M);
        simulation.startSimulation();
    }
}

class PostOfficeSimulation {
    private final int N; // Number of people
    private final int S; // Size of each mailbox
    private final int M; // Total number of messages to send
    private final Semaphore[] mailboxes;
    private final Random random;

    public PostOfficeSimulation(int N, int S, int M) {
        this.N = N;
        this.S = S;
        this.M = M;
        this.mailboxes = new Semaphore[N];
        this.random = new Random();
        for (int i = 0; i < N; i++) {
            this.mailboxes[i] = new Semaphore(S, true); // Initialize mailboxes
        }
    }

    public void startSimulation() {
        System.out.println("Post Office Simulation Started");

        for (int i = 0; i < N; i++) {
            Thread person = new Person(i);
            person.start();
        }
    }

    class Person extends Thread {
        private final int id;

        public Person(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            for (int i = 1; i <= M; i++) {
                enterPostOffice();
                readMail();
                composeMail();
                sendMail();
                leavePostOffice();
            }
        }

        private void enterPostOffice() {
            System.out.println("Person " + id + " enters the post office.");
        }

        private void readMail() {
            System.out.println("Person " + id + " starts reading mail.");

            for (int i = 1; i <= S; i++) {
                try {
                    mailboxes[id].acquire();
                    System.out.println("Person " + id + " reads message " + i);
                    Thread.sleep(random.nextInt(100)); // Yield between reading each message
                    mailboxes[id].release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void composeMail() {
            System.out.println("Person " + id + " composes a message.");
        }

        private void sendMail() {
            int recipient = random.nextInt(N);
            while (recipient == id) {
                recipient = random.nextInt(N); // Ensure recipient is different from sender
            }
            try {
                mailboxes[recipient].acquire();
                System.out.println("Person " + id + " sends a message to Person " + recipient);
                Thread.sleep(random.nextInt(100)); // Simulate sending mail
                mailboxes[recipient].release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void leavePostOffice() {
            System.out.println("Person " + id + " leaves the post office.");
        }
    }
}
