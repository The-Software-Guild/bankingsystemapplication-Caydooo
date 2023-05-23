package com.banking.dto;

import java.io.Serializable;
import java.util.InputMismatchException;

import com.banking.dto.Customer;
import com.banking.exceptions.InsufficientFundsException;

public class SavingsAccount extends BankAccount implements Serializable {
	private boolean isSalaryAccount;
	private static final double MIN_BALANCE = 100;
	private double interestEarned; // calculate as 4% of balance
	
	public SavingsAccount(long accountNum, long bsbCode, String bankName, double balance, String openingDate, boolean isSalaryAccount) {
		super(accountNum, bsbCode, bankName, balance, openingDate);
		this.isSalaryAccount = isSalaryAccount;
	}
	
	public SavingsAccount(long accountNum, long bsbCode, String bankName, double balance, String openingDate, boolean isSalaryAccount, double interestEarned) {
		super(accountNum, bsbCode, bankName, balance, openingDate);
		this.isSalaryAccount = isSalaryAccount;
		this.interestEarned = interestEarned;
	}
	
	public SavingsAccount() {} // default constructor

	public boolean isSalaryAccount() {
		return isSalaryAccount;
	}

	public void setSalaryAccount(boolean isSalaryAccount) {
		this.isSalaryAccount = isSalaryAccount;
	}
	
	public double getMinBalance() {
		return MIN_BALANCE;
	}	
	
	public double getInterestEarned() {
		return interestEarned;
	}

	public void setInterestEarned(double interestEarned) {
		this.interestEarned = interestEarned;
	}

	@Override
	public String toString() {
		return super.toString() + " SavingsAccount [isSalaryAccount=" + isSalaryAccount + ", interestEarned=" + interestEarned + "]";
	}

	@Override
	public void calculateInterest() {
		this.interestEarned = this.getBalance() * 0.04;
	}

	public void SavingsMenu(Customer c) {
		System.out.println("-------------------------");
		System.out.println("Welcome to the Savings Menu");
		while(true) {
			System.out.println("For security purposes, please enter your Customer ID, or enter '1' to exit.");
			System.out.print("\nPlease confirm your Customer ID: ");
			try {
				int assignedId = Integer.parseInt(scanner.nextLine());
				if(assignedId == 1) {
					System.out.println("Exiting to main menu...");
					return;
				} else if(c.getCustomerId() != assignedId) {
					System.out.println("This Customer ID doesn't match the previous Customer ID, please try again.");
					continue;
				} else {
					System.out.println("Successfully found Customer with ID: " + assignedId + '\n');
					break;
				}
			} catch (NumberFormatException nfe) {
				System.out.println("This isn't a valid value, please try again.");
			}	
		}
		
		boolean savingsMenu = true;
		while(savingsMenu) {
			System.out.println("-------------------------");
			System.out.println("\nWhat would you like to do?");
			System.out.println("1. Deposit");
			System.out.println("2. Withdraw"); // cannot withdraw money to make savings account $100 or less, or throw exception.
			System.out.println("3. Display balance");
			System.out.println("4. Exit to menu");
			
			int option = Integer.parseInt(scanner.nextLine());			
			switch(option) {
				case 1: {
					try {
						System.out.print("How much would you like to deposit?: ");
						double amount = Double.parseDouble(scanner.nextLine());
						if(amount >= 0) {
							String transaction = deposit(c, amount);
							if(transaction.equals("successful")) {
								System.out.println("Transaction " + transaction + "!");
							} else {
								InsufficientFundsException ife = new InsufficientFundsException("Transaction " + transaction + "!");
								throw ife;
							}
						} else {
							System.out.println("This isn't a valid amount! Exiting to savings menu...");
						}	
					} catch (InsufficientFundsException ife) {
						String message = ife.getMessage();
						System.out.println(message);
					}catch (NumberFormatException nfe) {
						System.out.println("This isn't a valid amount! Exiting to savings menu...");
					}
					break;
				}
				case 2: {
					try {
						System.out.print("How much would you like to withdraw?: ");
						double amount = Double.parseDouble(scanner.nextLine());
						if(amount >= 0) {
							String transaction = withdraw(c, amount);
							if(transaction.equals("successful")) {
								System.out.println("Transaction " + transaction + "!");
							} else {
								InsufficientFundsException ife = new InsufficientFundsException("Transaction " + transaction + "!");
								throw ife;
							}
						} else {
							System.out.println("This isn't a valid amount! Exiting to savings menu...");
						}	
					} catch (InsufficientFundsException ife) {
						String message = ife.getMessage();
						System.out.println(message);
					} catch (NumberFormatException nfe) {
						System.out.println("This isn't a valid amount! Exiting to savings menu...");
					}
					break;
				}
				case 3: {
					System.out.println("Your current balance is: $" + c.getBankAcc().getBalance());
					break;
				}
				case 4: {
					System.out.println("Exiting the Savings menu back to main menu...");
					savingsMenu = false;
					break;
				}
				default: {
					System.out.println("This isn't an option above, please specify either values 1, 2, 3 or 4.");
				}
			}
		}
	}

	private static String deposit(Customer c, double amount) {
		double balance = c.getBankAcc().getBalance();
		
		balance += amount;
		c.getBankAcc().setBalance(balance);
		
		return "successful";
	}
	
	private static String withdraw(Customer c, double amount) {
		double balance = c.getBankAcc().getBalance();
		
		balance -= amount;
		if(c.getBankAcc() instanceof SavingsAccount) {
			SavingsAccount sa = (SavingsAccount)c.getBankAcc();
			if(sa.isSalaryAccount && balance < 0) {
				return "failed. You don't have enough money in your account";
			} else if(!(sa.isSalaryAccount) && balance < MIN_BALANCE) {
				return "failed. Your savings account must always have minimum $100 in there";
			} else {
				c.getBankAcc().setBalance(balance);
				return "successful";
			}
		}
		return "failed, bank account not a savings account";
	}
}
