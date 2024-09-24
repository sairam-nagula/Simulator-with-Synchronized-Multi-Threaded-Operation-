package Banking_Simulator;

import java.util.Random;

public class depositorAgent implements Runnable {

    private ABankAccount account1;
    private ABankAccount account2;
    private TheBank bank;
    private int agentId;
    private final int MAXSLEEP = 500; 

    public depositorAgent(ABankAccount account1, ABankAccount account2, TheBank bank, int agentId) {
        this.account1 = account1;
        this.account2 = account2;
        this.bank = bank;
        this.agentId = agentId;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ABankAccount selectedAccount = randomlySelectAccount();  // Randomly select an account
                int depositAmount = new Random().nextInt(600) + 1;  // Random deposit amount between 1 and 600

                // Perform deposit
                selectedAccount.deposit(depositAmount);

                // Get next transaction number from TheBank
                int transactionNumber = bank.getNextTransactionNumber();

                // Print and format the output
                String accountLabel = selectedAccount == account1 ? "JA-1" : "JA-2";
                System.out.printf("Agent DT%-2d deposits $%-4d into %-3s | (+) %-3s balance is $%-5d | Transaction#: %-2d\n",
                                  agentId, depositAmount, accountLabel, accountLabel, selectedAccount.getBalance(), transactionNumber);

                // Flag transactions if the deposit exceeds $450
                if (depositAmount > 450) {
                    bank.flagTransaction("Depositor Agent", agentId, depositAmount, transactionNumber);
                }

                // Sleep for a random time between 0 and MAXSLEEP
                Thread.sleep(new Random().nextInt(MAXSLEEP));

            } catch (InterruptedException e) {
                System.out.println("Depositor Agent interrupted.");
            }
        }
    }

    // Randomly selects between account1 and account2
    private ABankAccount randomlySelectAccount() {
        return new Random().nextBoolean() ? account1 : account2;  // 50% chance to select either account
    }
}