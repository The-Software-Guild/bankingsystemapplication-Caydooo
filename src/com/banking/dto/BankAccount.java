package com.banking.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.banking.dto.Customer;
import com.banking.exceptions.ExistingAccountException;
import com.banking.exceptions.InsufficientFundsException;

public abstract class BankAccount implements Serializable {
	private long accountNum;
	private long bsbCode;
	private String bankName;
	private transient double balance; // transient keyword ignores balance during serialization
	private String openingDate; // needs to match dd/MM/yyyy
	public static Scanner scanner = new Scanner(System.in);
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	public BankAccount(long accountNum, long bsbCode, String bankName, double balance, String openingDate) {
		this.accountNum = accountNum;
		this.bsbCode = bsbCode;
		this.bankName = bankName;
		this.balance = balance;
		this.openingDate = openingDate;
	}

	public BankAccount() {} //default constructor

	public long getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(long accountNum) {
		this.accountNum = accountNum;
	}

	public long getBsbCode() {
		return bsbCode;
	}

	public void setBsbCode(long bsbCode) {
		this.bsbCode = bsbCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(String openingDate) {
		this.openingDate = openingDate;
	}
	
	public long randomValueGenerator(int length) {
		long result;
		String randomStr;
		
		Random rand = new Random();
		result = Math.abs(rand.nextLong());
		
		randomStr = Long.toString(result);
		String lengthOfLong = randomStr.substring(0, length);
		result = Long.parseLong(lengthOfLong);		
		
		return result;
	}
	
	public void createBankAccount() throws ExistingAccountException, InsufficientFundsException {
		BankAccount ba = null;
		long accountNumber;
		long bsbCode;
		String bankName = "";
		double balance = 0;
		String openingDate;
		Customer chosen = null;
		List<Customer> customers = null;
		boolean accountSuccess = false;
		boolean savingsSuccess = false;
		int length = 0;
		
		System.out.println("-------------------------");
		System.out.print("Please enter the customer ID in which this bank account will be associated with: ");
		int assignedId = Integer.parseInt(scanner.nextLine());
		chosen = new Customer();
		customers = chosen.getCustomers();
		try {
			for(Customer c : customers) {
				if(assignedId == c.getCustomerId()) {
					if(c.getBankAcc() != null) {
						ExistingAccountException eae = new ExistingAccountException("A bank account already exists with this Customer! Exiting to main menu...");
						throw eae;
					}
					chosen = c;
					break;
				}
			}
			
			System.out.println("Successfully found Customer with ID: " + assignedId);
			
			while(!accountSuccess) {						
				System.out.println("Which type of account would you like to create?");
				System.out.println("Enter '1' for a Savings Account.");
				System.out.println("Enter '2' for a Fixed Deposit Account.");
				int accountType = Integer.parseInt(scanner.nextLine());
				
				if(accountType == 1) {
					accountSuccess = true;
					System.out.println("Great! Creating new savings account...");
					boolean isSalaryAccount = false;
					ba = new SavingsAccount();
					
					//Generated values for bank account
					// Account Number
					length = 9;
					accountNumber = ba.randomValueGenerator(length);

					// BSB Code
					length = 6;
					bsbCode = ba.randomValueGenerator(length);
								
					// Opening Date
					LocalDate ld = LocalDate.now();
					openingDate = ld.format(formatter);
					//System.out.println(openingDate);
					
					while(!savingsSuccess) {
						System.out.print("Would you like this to be your salary account? (Yes/No): ");
						String savingsResponse = scanner.nextLine();
						if(savingsResponse.equalsIgnoreCase("Yes")) {
							savingsSuccess = true;
							isSalaryAccount = true;
							bankName = chosen.getCustomerName().concat("'s Savings/Salary Account");
						} 
						else if(savingsResponse.equalsIgnoreCase("No")) {
							savingsSuccess = true;
							bankName = chosen.getCustomerName().concat("'s Savings Account");
						} 
						else {
							System.out.println("You haven't specified a correct answer! Please use 'Yes' or 'No'.");
						}
					}

					while(true) {
						try {
							if(!isSalaryAccount) {
								System.out.print("What would you like to deposit as a starting balance? (0.00): ");
								balance = Double.parseDouble(scanner.nextLine());
								if(ba instanceof SavingsAccount) {
									SavingsAccount sa = (SavingsAccount)ba;
									if(balance < sa.getMinBalance()) {
										InsufficientFundsException ife = 
												new InsufficientFundsException("Insufficient balance for Savings "
												+ "Account creation. The minimum balance required is $100.");
										throw ife;
									}
								}
							}														
							break;
						} catch (InsufficientFundsException ife) {
							String message = ife.getMessage();
							System.out.println("Error: " + message);
							
						} catch (NumberFormatException nfe) {
							System.out.println("This isn't a valid value, please try again.");
						}
					}
					
					ba = new SavingsAccount(accountNumber, bsbCode, bankName, balance, openingDate, isSalaryAccount);
					if(ba instanceof SavingsAccount) {
						SavingsAccount sa = (SavingsAccount)ba;
						sa.calculateInterest();
					}
					
					System.out.println("Bank Account details:");
					System.out.println("Account Number: " + ba.getAccountNum());
					System.out.println("BSB Code: " + ba.getBsbCode());
					System.out.println("Bank Name: " + ba.getBankName());
					System.out.println("Starting Balance: $" + ba.getBalance());
					System.out.println("Opening Date: " + ba.getOpeningDate());
					if(ba instanceof SavingsAccount) {
						SavingsAccount sa = (SavingsAccount)ba;
						System.out.println("Is a Salary Account?: " + sa.isSalaryAccount());
					}
				}
				else if (accountType == 2) {
					accountSuccess = true;
					System.out.println("Great! Creating new fixed deposit account...");
					double depositAmount = 0;
					ba = new FixedDepositAccount();
					
					//Generated values for bank account
					// Account Number
					length = 9;
					accountNumber = ba.randomValueGenerator(length);

					// BSB Code
					length = 6;
					bsbCode = ba.randomValueGenerator(length);
								
					// Opening Date
					LocalDate ld = LocalDate.now();
					openingDate = ld.format(formatter);
					//System.out.println(openingDate);
					
					while(true) {
						try {
							System.out.print("Please enter your starting deposit amount (minimum is $1000): ");
							depositAmount = Double.parseDouble(scanner.nextLine());
							if(depositAmount >= 1000) {
								break;
							}
							System.out.println("This amount did not succeed $1000, please try again.");
							
						} catch (NumberFormatException nfe) {
							System.out.println("This isn't a valid value, please try again.");
						}
					}
					
					int tenure = 0;
					boolean tenureSuccess = false;
					
					while(!tenureSuccess) {
						try {
							System.out.println("Please enter a chosen tenure (integer) for which the account will be locked for.");
							System.out.print("Minimum 1 year, Maximum 7 years: ");
							tenure = Integer.parseInt(scanner.nextLine());
							
							switch (tenure) {
								case 1:
								case 2:
								case 3:
								case 4:
								case 5:
								case 6:
								case 7: {
									System.out.println("Great! Your tenure is confirmed.");
									tenureSuccess = true;
									break;
								}
								default: {
									System.out.println("That number of years isn't allowed, please try again.");
								}
							}
						} catch (NumberFormatException nfe) {
							System.out.println("This isn't a valid value, please try again.");
						}
					}
					
					bankName = chosen.getCustomerName().concat("'s Fixed Deposit Account");
					ba = new FixedDepositAccount(accountNumber, bsbCode, bankName, balance, openingDate, depositAmount, tenure);
					if(ba instanceof FixedDepositAccount) {
						FixedDepositAccount fda = (FixedDepositAccount)ba;
						fda.addToBalance(depositAmount);
						fda.calculateInterest();
					}
					
					System.out.println("\nBank Account details:");
					System.out.println("Account Number: " + ba.getAccountNum());
					System.out.println("BSB Code: " + ba.getBsbCode());
					System.out.println("Bank Name: " + ba.getBankName());
					System.out.println("Starting Balance: $" + ba.getBalance());
					System.out.println("Opening Date: " + ba.getOpeningDate());
					if(ba instanceof FixedDepositAccount) {
						FixedDepositAccount fda = (FixedDepositAccount)ba;
						System.out.println("Initial Deposit Amount: " + fda.getDepositAmount());
						System.out.println("Tenure: " + fda.getTenure());
					}
					
				}
				else {
					System.out.println("You haven't specified a correct answer! Please select either option 1, or option 2.");
				}
			}
			
			chosen.setBankAcc(ba);
			
			System.out.println("\nA new bank account has been created for Customer " + assignedId + " successfully! Returning to main menu...");
			System.out.print("-------------------------");
			
		} catch (ExistingAccountException eae) {
			String message = eae.getMessage();
			System.out.println("Error: " + message);

		} catch(NullPointerException e) {
			System.out.println("Error: Our system wasn't able to find a Customer with that ID. Exiting to main menu...");
			e.printStackTrace();
		}
	}

	public void displayBalance() {
		Customer chosen = new Customer();
		List<Customer> customers = null;
		customers = chosen.getCustomers();
		
		System.out.print("Great! Please enter your Customer ID: ");
		int cusId = Integer.parseInt(scanner.nextLine());
		try {
			for(Customer c : customers) {
				if(cusId == c.getCustomerId()) {
					if(c.getBankAcc() != null) {
						if(c.getBankAcc() instanceof SavingsAccount) {
							SavingsAccount sa = (SavingsAccount)c.getBankAcc();
							sa.SavingsMenu(c);
						} else {
							System.out.println("Account found. Your current balance is $" + c.getBankAcc().getBalance());
						}
					} else {
						ExistingAccountException eae = new ExistingAccountException("No bank account exists for this customer! Exiting to menu...");
						throw eae;
					}
					break;
				}
			}
		} catch (ExistingAccountException eae) {
			String message = eae.getMessage();
			System.out.println("Error: " + message);

		} catch(NullPointerException e) {
			System.out.println("Error: Our system wasn't able to find a Customer with that ID. Exiting to main menu...");
			e.printStackTrace();
		}
	}
	
	public abstract void calculateInterest();

	@Override
	public String toString() {
		return "BankAccount [accountNum=" + accountNum + ", bsbCode=" + bsbCode + ", bankName=" + bankName
				+ ", balance=" + balance + ", openingDate=" + openingDate + "]";
	}
}
