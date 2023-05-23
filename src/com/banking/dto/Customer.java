package com.banking.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.banking.dto.BankAccount;
import com.banking.dto.FixedDepositAccount;
import com.banking.dto.SavingsAccount;
import com.banking.exceptions.CustomerNotFoundException;
import com.banking.exceptions.ExistingAccountException;
import com.banking.exceptions.InvalidDateException;

public class Customer implements Serializable {

	private int customerId;
	private String customerName;
	private String dob;
	private int age;
	private String mobileNum;
	private String passportNum;
	private BankAccount bankAcc = null;
	private static List<Customer> customers = null;
	public static Scanner scanner = new Scanner(System.in);
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final int MIN_YEAR = 1900;
	
	public Customer(int customerId, String customerName, String dob, int age, String mobileNum, String passportNum) {
		this.customerId = customerId;
		this.customerName = customerName;
		this.dob = dob;
		this.age = age;
		this.mobileNum = mobileNum;
		this.passportNum = passportNum;
	}

	public Customer() {} //default constructor

	public int getCustomerId() {
		return customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public String getPassportNum() {
		return passportNum;
	}

	public void setPassportNum(String passportNum) {
		this.passportNum = passportNum;
	}
	
	public void initializeCollection(List<Customer> cust) {
		customers = cust;
	}
	
	public List<Customer> getCustomers() {
		return customers;
	}
	
	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}
	
	public static boolean isLeapYear(int year) {
		if(((year % 4 == 0) && (year % 100 == 0)) || (year % 400 == 0)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isValidDate(String date) {
		int day = Integer.parseInt(date.substring(0,2));
		int month = Integer.parseInt(date.substring(3,5));
		int year = Integer.parseInt(date.substring(6,10));
		LocalDate ld = LocalDate.parse(date, formatter);
		final int MAX_YEAR = ld.getYear();
		
		if(year < MIN_YEAR || year > MAX_YEAR) {
			return false;
		}
		if(month < 1 || month > 12) {
			return false;
		}
		if(day < 1 || day > 31) {
			return false;
		}
		
		//check for Feburary
		if(month == 2) {
			if(isLeapYear(year)) {
				return (day <= 29);
			} else {
				return (day <= 28);
			}
		}
		
		//check for months with 30 days
		if(month == 4 || month == 6 || month == 9 || month == 11) {
			return (day <= 30);
		}

		return true;
	}
	
	public BankAccount getBankAcc() {
		return bankAcc;
	}
	
	public void setBankAcc(BankAccount bankAcc) {
		this.bankAcc = bankAcc;
	}
	
	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", customerName=" + customerName + ", dob=" + dob + ", age=" + age
				+ ", mobileNum=" + mobileNum + ", passportNum=" + passportNum + ", bankAcc=" + bankAcc + "]";
	}
	
	
	public void generateCustomers() {
		customers.add(new Customer(100, "Bilal Johns", "23/06/1979", 43, "6147398601", "923E"));
		customers.add(new Customer(101, "Saoirse Cross", "02/03/1999", 24, "9749927510", "188H"));
		customers.add(new Customer(102, "Allan Church", "29/11/1958", 64, "1789429802", "918S"));
		customers.add(new Customer(103, "Husna Goodman", "14/05/2003", 20, "9483343134", "690B"));
		customers.add(new Customer(104, "Hayley Gray", "16/01/1977", 46, "8162544026", "955T"));
		customers.add(new Customer(105, "Cyrus Cordova", "11/12/1973", 49, "1443810143", "472B"));
		customers.add(new Customer(106, "Casey Flores", "09/04/1985", 38, "7348311063", "802E"));
		customers.add(new Customer(107, "Lorraine Alvarez", "11/09/1967", 55, "0153524225", "849E"));
		customers.add(new Customer(108, "Aleena Oconnell", "22/08/1969", 53, "2947095147", "775S"));
		customers.add(new Customer(109, "Yusuf Norman", "07/07/1959", 63, "0699138378", "387C"));
		
	}
	
	public void createNewCustomer() throws RuntimeException {
		int customerIdGeneration = 100;
		boolean success = false;
		String customerName = "";
		String dob = "";
		int age = 0;
		String mobileNum = "";
		String passportNum = "";
		
		System.out.println("-------------------------");
		System.out.println("Please enter these details to create a new customer.");
		
		while(!success) {
			try {
				System.out.print("Customer Name: ");
				customerName = scanner.nextLine();
				
				System.out.print("Date of Birth (DD/MM/YYYY): ");
				dob = scanner.nextLine();
				
				if(!isValidDate(dob)) {
					InvalidDateException dtpe = new InvalidDateException("");
					throw dtpe;
				}
				
				// calculate age based on dob
				LocalDate ldCurrent = LocalDate.now();
				String ldStr = ldCurrent.format(formatter);
				LocalDate ldDob = LocalDate.parse(dob, formatter);
				ldCurrent = LocalDate.parse(ldStr, formatter);
				
				age = Period.between(ldDob, ldCurrent).getYears();
				System.out.println("Age of Customer: " + age);
				
				while(true) {
					System.out.print("Mobile Number: ");
					mobileNum = scanner.nextLine();
					String temp = mobileNum;
					try {  
						Integer.parseInt(temp); 
						break;
					} catch(NumberFormatException e){  
						System.out.println("This isn't a valid mobile number!");  
					}
				}
				
				System.out.print("Passport Number: ");
				passportNum = scanner.nextLine();
				
				success = true;
			} catch (InvalidDateException | DateTimeParseException ee) {
				System.out.println("The date of birth entered does not exist, or it isn't possible! Please try again.");
				ee.printStackTrace();
				success = false;
			} catch (RuntimeException rte) {
				System.out.println("You've entered an incorrect format! Please try again.");
				success = false;
			}		
		}
		
		if(success) {
			int id = customerIdGeneration + customers.size();
			//System.out.println("id: " + id + ", customerIdCounter: " + customerIdCounter);
			customers.add(new Customer(id, customerName, dob, age, mobileNum, passportNum));
		}
		
		System.out.println("\nThank you, a new customer has been created. Exiting to main menu...");
		System.out.print("-------------------------");
	}

	public void showAllCustomers() {
		System.out.println("-------------------------");
		System.out.println("Here is the current list of customers with the Java Banking System:\n");

		
		
		customers.forEach((cust) -> System.out.println(cust.toString()));
		
		/*
		Iterator<Customer> iter = customers.iterator();
		
		while(iter.hasNext()) {
			Customer cust = iter.next();
			System.out.println(cust.toString());
		}
		*/
		System.out.print("-------------------------");
	}

	public void createBankAccount() {
		SavingsAccount sa = new SavingsAccount();
		if(sa instanceof BankAccount) {
			BankAccount ba = (BankAccount)sa;
			ba.createBankAccount();
		}
	}
	
	public void displayBalance() {
		SavingsAccount sa = new SavingsAccount();
		if(sa instanceof BankAccount) {
			BankAccount ba = (BankAccount)sa;
			ba.displayBalance();
		}
	}

	public void displayInterest() {
		System.out.print("Great! Please enter your Customer ID: ");
		int cusId = Integer.parseInt(scanner.nextLine());
		try {
			for(Customer c : customers) {
				if(cusId == c.getCustomerId()) {
					if(c.getBankAcc() != null) {
						if(c.getBankAcc() instanceof SavingsAccount) {
							System.out.println("Calculating interest based on your savings account...");
							SavingsAccount sa = (SavingsAccount) c.getBankAcc();
							sa.calculateInterest();
							System.out.println("Your total interest is: $" + sa.getInterestEarned() + ".");
							break;
						} else if(c.getBankAcc() instanceof FixedDepositAccount) {
							System.out.println("Calculating interest based on your fixed deposit account...");
							FixedDepositAccount fda = (FixedDepositAccount) c.getBankAcc();
							fda.calculateInterest();
							System.out.println("Your total interest (based on " + fda.getTenure() + " year/s tenure is: $" + fda.getInterestEarned() + ".");
							break;
						} else {
							ExistingAccountException eae = new ExistingAccountException("No bank account exists for this customer! Exiting to menu...");
							throw eae;
						}
					}
				}
			}
		} catch (ExistingAccountException eae) {
			String message = eae.getMessage();
			System.out.println("Error: " + message);

		} catch(NullPointerException | CustomerNotFoundException e) {
			System.out.println("Error: Our system wasn't able to find a Customer with that ID. Exiting to main menu...");
		}
	}

	public void sortCustomerData() {
		System.out.println("-------------------------");
		boolean sortComplete = false;
		
		while(!sortComplete) {
			System.out.println("What would you like to sort the customers by?");
			System.out.println("1. Sort by customer ID");
			System.out.println("2. Sort by customer name");
			System.out.println("3. Sort by customer's bank balance");
			System.out.println("4. Cancel request");
			
			try {
				int option = Integer.parseInt(scanner.nextLine());
				switch(option) {
					case 1: {
						customers.sort(new Comparator<Customer>() {
							@Override
							public int compare(Customer c1, Customer c2) {
								if(c1.getCustomerId() < c2.getCustomerId()) {
									return -1;
								} else if (c1.getCustomerId() > c2.getCustomerId()) {
									return 1;
								} else {
									return 0;
								}
							}
						});
						System.out.println("Sort by customer ID completed.\n");
						break;
					}
					case 2: {
						customers.sort((c1, c2) -> c1.getCustomerName().compareTo(c2.getCustomerName()));
						System.out.println("Sort by customer name completed.\n");
						break;
					}
					case 3: {
						customers.sort(new Comparator<Customer>() {
							@Override
							public int compare(Customer c1, Customer c2) {
								double c1Balance = 0;
								double c2Balance = 0;
								
								if(c1.getBankAcc() == null) {
									c1Balance = -1;
								} else {
									c1Balance = c1.getBankAcc().getBalance();
								}
								
								if(c2.getBankAcc() == null) {
									c2Balance = -1;
								} else {
									c2Balance = c2.getBankAcc().getBalance();
								}

								if(c1Balance < c2Balance) {
									return 1;
								} else if (c1Balance > c2Balance) {
									return -1;
								} else {
									return 0;
								}
							}
						});
						System.out.println("Sort by customer's bank balance completed.\n");
						break;
					}
					case 4: {
						System.out.println("Exiting to main menu...");
						sortComplete = true;
						break;
					}
					default: {
						System.out.println("This isn't a valid value, please choose option 1, option 2, option 3, or option 4.");
					}
				}	
			} catch(NumberFormatException nfe) {
				System.out.println("This isn't a valid value, please try again.");
			}
				
		}
		System.out.println("-------------------------");
	}
}
