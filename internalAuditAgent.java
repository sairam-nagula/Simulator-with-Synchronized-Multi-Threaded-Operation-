package Banking_Simulator;

import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class internalAuditAgent implements Runnable {

    private ABankAccount account1;
    private ABankAccount account2;
    private int auditCount = 0;  // Keeps track of how many audits have been completed
    private final int MAXSLEEP = 1000;  // Maximum sleep time for audit agent
    private final Random random = new Random();
    private TheBank bank;  // Reference to the bank to get transaction counts

    public internalAuditAgent(ABankAccount account1, ABankAccount account2, TheBank bank) {
        this.account1 = account1;
        this.account2 = account2;
        this.bank = bank;
    }

    @Override
    public void run() {
        while (true) {  // Infinite loop as per project specification
            try {
                performAudit();

                // Sleep for a random time (longer sleep for audit agents, e.g., 1000ms)
                Thread.sleep(random.nextInt(MAXSLEEP));

            } catch (InterruptedException e) {
                System.out.println("Internal Audit Agent interrupted.");
            }
        }
    }

    private void performAudit() {
        boolean bothAccountsLocked = true;

        ReentrantLock lock1 = account1.getLock();
        ReentrantLock lock2 = account2.getLock();

        try {
            // Try to lock both accounts for auditing
            if (!lock1.tryLock()) {
                bothAccountsLocked = false;
            } else if (!lock2.tryLock()) {
                bothAccountsLocked = false;
                lock1.unlock();  // Unlock account1 if account2 can't be locked
            }

            // If not both accounts were locked, retry later
            if (!bothAccountsLocked) {
                System.out.println("Internal Audit Agent failed to lock both accounts. Retrying later.");
                return;
            }

            // Perform audit
  
            int transactionsSinceLastAudit = bank.getTransactionsSinceLastInternalAudit();  // Get the number of transactions since the last internal audit

            auditCount++;

            // Structured output with decorative borders
            System.out.println("\n" + "*".repeat(60));
            System.out.printf("Internal Bank Audit #%d Beginning...\n", auditCount);
            System.out.printf("The total number of transactions since the last Internal audit is: %d\n", transactionsSinceLastAudit);
            System.out.printf("INTERNAL BANK AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-1 TO BE: $%d\n", account1.getBalance());
            System.out.printf("INTERNAL BANK AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-2 TO BE: $%d\n", account2.getBalance());
            System.out.println("Internal Bank Audit Complete.");
            System.out.println("*".repeat(60) + "\n");

        } finally {
            // Unlock both accounts after audit
            if (bothAccountsLocked) {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }
}