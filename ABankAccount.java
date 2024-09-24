package Banking_Simulator;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ABankAccount {

    private int balance;
    private final ReentrantLock lock = new ReentrantLock();  
    private final Condition sufficientFunds = lock.newCondition(); 

    public ABankAccount() {
        this.balance = 0;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    // Withdraw method with blocking on insufficient funds
    public boolean withdraw(int amount, int agentId) {
    	// Lock the account for thread safety
        lock.lock();  
        try {
            // Block if insufficient funds
            while (balance < amount) {
                System.out.println("Withdrawal Agent " + agentId + " is blocked. Insufficient funds. Balance is $" + balance);
                sufficientFunds.await();  // Wait until notified that a deposit has been made
            }

            // If sufficient funds, proceed with withdrawal
            System.out.println("Withdrawal Agent " + agentId + " withdrawing $" + amount + ". Previous balance: $" + balance);
            balance -= amount;
            System.out.println("Withdrawal Agent " + agentId + " new balance: $" + balance);
            return true;
        } catch (InterruptedException e) {
            System.out.println("Withdrawal interrupted for agent " + agentId);
            return false;
        } finally {
            lock.unlock();  // Unlock the account
        }
    }

    // Deposit method to notify waiting withdrawal agents
    public void deposit(int amount) {
    	// Lock the account for thread safety
        lock.lock();  
        try {
            System.out.println("Depositing $" + amount + ". Previous balance: $" + balance);
            balance += amount;
            System.out.println("New balance after deposit: $" + balance);
            // Notify all waiting withdrawal agents
            sufficientFunds.signalAll();  
        } finally {
        	// Unlock the account
            lock.unlock();  
        }
    }

    public int getBalance() {
        return balance;
    }
}