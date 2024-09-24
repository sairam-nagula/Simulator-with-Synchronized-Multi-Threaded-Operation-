package Banking_Simulator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TheBank {

    private ABankAccount account1;
    private ABankAccount account2;
    private ExecutorService executorService;
    private int transactionCounter = 1;  // Overall transaction counter

    // Separate counters for tracking transactions since last audit
    private int lastInternalAuditTransactionCount = 0;
    private int lastTreasuryAuditTransactionCount = 0;

    public TheBank() {
        // Initialize two bank accounts with an initial balance of 0
        account1 = new ABankAccount();
        account2 = new ABankAccount();

        // Create a thread pool to manage the agents
        executorService = Executors.newFixedThreadPool(19);  // 5 Depositors + 10 Withdrawals + 2 Transfers + 1 Internal Audit + 1 Treasury

        // Redirect console output to RedirectedOutput.txt
        try {
            String filePath = "/Users/zai/CNT4714_Project2/RedirectedOutput.txt";  // The same directory as transactions.csv
            PrintStream out = new PrintStream(new FileOutputStream(filePath));
            System.setOut(out);  // Redirect all console output to the file
            System.out.println("Console output is redirected to: " + filePath);  // Initial message to verify redirection
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print out the working directory for debugging (this will now be written to RedirectedOutput.txt)
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
    }

    // Method to start the simulation
    public void startSimulation() {
        // Create and execute 5 depositor agents
        for (int i = 0; i < 5; i++) {
            executorService.execute(new depositorAgent(account1, account2, this, i));
        }

        // Create and execute 10 withdrawal agents
        for (int i = 0; i < 10; i++) {
            executorService.execute(new withdrawalAgent(account1, account2, this, i));
        }

        // Create and execute 2 transfer agents
        for (int i = 0; i < 2; i++) {
            executorService.execute(new transferAgent(account1, account2, this, i));
        }

        // Create and execute 1 internal audit agent
        executorService.execute(new internalAuditAgent(account1, account2, this));

        // Create and execute 1 treasury agent
        executorService.execute(new treasuryAgent(account1, account2, this));
    }

    // Method to get the next transaction number and increment the counter
    public synchronized int getNextTransactionNumber() {
        return transactionCounter++;  // Increment and return the transaction number
    }

    // Method to get the number of transactions since the last Internal Audit
    public synchronized int getTransactionsSinceLastInternalAudit() {
        int transactionsSinceLastAudit = transactionCounter - lastInternalAuditTransactionCount;
        lastInternalAuditTransactionCount = transactionCounter;  // Reset for next internal audit
        return transactionsSinceLastAudit;
    }

    // Method to get the number of transactions since the last Treasury Audit
    public synchronized int getTransactionsSinceLastTreasuryAudit() {
        int transactionsSinceLastAudit = transactionCounter - lastTreasuryAuditTransactionCount;
        lastTreasuryAuditTransactionCount = transactionCounter;  // Reset for next treasury audit
        return transactionsSinceLastAudit;
    }

    // Method to log flagged transactions to the CSV file
    public synchronized void flagTransaction(String agentType, int agentId, int amount, int transactionNumber) {
        // Get the current timestamp for the transaction
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // Log message to the console (redirected to RedirectedOutput.txt)
        String logMessage = "Flagged Transaction: " + agentType + " " + agentId + " | Amount: $" + amount + " | Transaction#: " + transactionNumber + " | Timestamp: " + timeStamp;
        System.out.println(logMessage);

        // Use an absolute or relative file path for the transactions.csv file
        String filePath = "/Users/zai/CNT4714_Project2/transactions.csv";  // You can change this to an absolute path if needed.

        // Append the flagged transaction to the CSV file
        try (FileWriter fw = new FileWriter(filePath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.printf("%s,%d,%d,%d,%s\n", agentType, agentId, amount, transactionNumber, timeStamp);
            // Add a log to confirm writing to the file
            System.out.println("Transaction written to file: " + filePath);
        } catch (IOException e) {
            // Print more details about the exception
            System.err.println("Error writing to CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TheBank bank = new TheBank();  // Create the bank
        bank.startSimulation();        // Start the simulation
    }
}