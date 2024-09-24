package Banking_Simulator;

import java.util.Random;

public class transferAgent implements Runnable {

    private ABankAccount sourceAccount;
    private ABankAccount destinationAccount;
    private TheBank bank;
    private int agentId;
    private final int MAXSLEEP = 700;

    public transferAgent(ABankAccount sourceAccount, ABankAccount destinationAccount, TheBank bank, int agentId) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.bank = bank;
        this.agentId = agentId;
    }

    @Override
    public void run() {
        while (true) {  // Infinite loop as per project specification
            try {
                int transferAmount = new Random().nextInt(99) + 1;  // Random transfer amount between 1 and 99

                // Perform the transfer
                boolean success = transfer(transferAmount);

                if (success) {
                    int transactionNumber = bank.getNextTransactionNumber();
                    System.out.printf("TRANSFER --> Agent TR%-2d transferring $%-4d from JA-2 to JA-1 | Transaction#: %-2d\n", 
                                      agentId, transferAmount, transactionNumber);
                }

                // Sleep for a random time between 0ms and MAXSLEEP
                Thread.sleep(new Random().nextInt(MAXSLEEP));
            } catch (InterruptedException e) {
                System.out.println("Transfer Agent " + agentId + " interrupted.");
            }
        }
    }

    // Transfer method
    private boolean transfer(int amount) {
        if (sourceAccount.getBalance() < amount) {
            System.out.println("Transfer Agent " + agentId + " failed due to insufficient funds.");
            return false; 
        }

        sourceAccount.withdraw(amount, agentId);
        destinationAccount.deposit(amount);
        return true; 
    }
}