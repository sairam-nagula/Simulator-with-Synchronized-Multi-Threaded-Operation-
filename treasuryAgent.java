package Banking_Simulator;

import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class treasuryAgent implements Runnable {

    private ABankAccount account1;
    private ABankAccount account2;
    private int treasuryAuditCount = 0;  // Keeps track of how many Treasury audits have been completed
    private final int MAXSLEEP = 1500;   // Sleep time for treasury agent
    private final Random random = new Random();
    private TheBank bank;  // Reference to the bank to get transaction counts

    public treasuryAgent(ABankAccount account1, ABankAccount account2, TheBank bank) {
        this.account1 = account1;
        this.account2 = account2;
        this.bank = bank;
    }

    @Override
    public void run() {
        while (true) {  // Infinite loop as per project specification
            try {
                performTreasuryAudit();

                // Sleep for a random time (longer sleep for treasury agent)
                Thread.sleep(random.nextInt(MAXSLEEP));

            } catch (InterruptedException e) {
                System.out.println("Treasury Agent interrupted.");
            }
        }
    }

    private void performTreasuryAudit() {
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
                System.out.println("Treasury Agent failed to lock both accounts. Retrying later.");
                return;
            }

            // Perform audit
            int totalBalance = account1.getBalance() + account2.getBalance();
            int transactionsSinceLastAudit = bank.getTransactionsSinceLastTreasuryAudit();  // Get the number of transactions since the last treasury audit

            treasuryAuditCount++;  // Increment treasury audit count here

            // Structured output with decorative borders
            System.out.println("\n" + "*".repeat(60));
            System.out.printf("US DEPT of treasury: Audit %d Beginning...\n", treasuryAuditCount);
            System.out.printf("The total number of transactions since the last Treasury audit is: %d\n", transactionsSinceLastAudit);
            System.out.printf("TREASURY AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-1 TO BE: $%d\n", account1.getBalance());
            System.out.printf("TREASURY AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-2 TO BE: $%d\n", account2.getBalance());
            System.out.println("Treasury Bank Audit Complete.");
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