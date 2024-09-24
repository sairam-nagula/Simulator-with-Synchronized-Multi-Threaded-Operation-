package Banking_Simulator;

import java.util.Random;

public class withdrawalAgent implements Runnable {

    private ABankAccount account1;
    private ABankAccount account2;
    private TheBank bank;
    private int agentId;
    private final int MAXSLEEP = 300; 

    public withdrawalAgent(ABankAccount account1, ABankAccount account2, TheBank bank, int agentId) {
        this.account1 = account1;
        this.account2 = account2;
        this.bank = bank;
        this.agentId = agentId;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ABankAccount selectedAccount = randomlySelectAccount(); 
                int withdrawalAmount = new Random().nextInt(99) + 1; 
          
                int transactionNumber = bank.getNextTransactionNumber();

                // Perform withdrawal
                boolean success = selectedAccount.withdraw(withdrawalAmount, agentId);
                String accountLabel = selectedAccount == account1 ? "JA-1" : "JA-2";
                if (success) {
                    
                    System.out.printf("Agent WT%-2d withdraws $%-4d from %-3s | (-) %-3s balance is $%-5d | Transaction#: %-2d\n",
                                      agentId, withdrawalAmount, accountLabel, accountLabel, selectedAccount.getBalance(), transactionNumber);
                } else {
                    System.out.printf("Agent WT%-2d attempts to withdraw $%-4d from %-3s | (******) WITHDRAWAL BLOCKED - INSUFFICIENT FUNDS!!! Balance only $%-3d\n",
                                      agentId, withdrawalAmount, accountLabel, selectedAccount.getBalance());
                }
                if (withdrawalAmount > 90) {
                    bank.flagTransaction("Withdrawal Agent", agentId, withdrawalAmount, transactionNumber);
                }

                // Sleep for a random time between 0 and MAXSLEEP
                Thread.sleep(new Random().nextInt(MAXSLEEP));

            } catch (InterruptedException e) {
                System.out.println("Withdrawal Agent interrupted.");
            }
        }
    }

    // Randomly selects between account1 and account2
    private ABankAccount randomlySelectAccount() {
        return new Random().nextBoolean() ? account1 : account2;  // 50% chance to select either account
    }
}