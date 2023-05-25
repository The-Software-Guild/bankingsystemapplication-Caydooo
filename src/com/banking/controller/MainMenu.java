package com.banking.controller;

import java.util.List;
import java.util.Scanner;

import com.banking.dto.Customer;
import com.banking.service.StorageService;
import com.banking.service.StorageServiceImplementation;
import com.banking.dao.FileStorageDao;
import com.banking.exceptions.CustomerNotFoundException;
import com.banking.exceptions.DuplicateIDException;
import com.banking.exceptions.PersistenceException;
import com.banking.exceptions.ValidationException;

public class MainMenu {
	
	public static Scanner scanner = new Scanner(System.in);
	
	public static void optionStatements() {
		System.out.println("1. Create New Customer Data");
		System.out.println("2. Assign a Bank Account to a Customer");
		System.out.println("3. Display balance or interest earned of a Customer");
		System.out.println("4. Sort Customer Data");
		System.out.println("5. Persist Customer Data");
		System.out.println("6. Show All Customers");
		System.out.println("7. Search Customers by Name");
		System.out.println("8. Exit");
	}
	
	public static void main(String[] args) {
		boolean cont = true;
		Customer c = new Customer();
		StorageService service = new StorageServiceImplementation();
		List<Customer> customers = service.retrieveAllCusts();
		c.initializeCollection(customers);
		
		System.out.print("Welcome to the Java Banking System.");
		
		while(cont) {
			System.out.println("\nPlease select an option from the menu below:\n");
			optionStatements();
			System.out.print("\nEnter the corresponding number to the option: ");
			cont = false;

			String response = scanner.nextLine();
			System.out.print("You have selected: " + response + ". ");
					
			if(response.contentEquals("8")) {
				System.out.println("Exit");
				System.out.println("Have a great day!");
				scanner.close();
				break;
			}
					
			switch (response) {
				case "1": {
					System.out.println("Create New Customer Data");
					c.createNewCustomer();
					break;
				}
				case "2": {
					System.out.println("Assign a Bank Account to a Customer");
					c.createBankAccount();
					break;
				}
				case "3": {
					System.out.println("Display balance or interest earned of a Customer");
					while(true) {
						System.out.println("\nWould you like to display your balance, or your interest earned from your account?");
						System.out.println("1. Display balance (includes deposit/withdraw for savings accounts)");
						System.out.println("2. Display interest earned");
						System.out.println("3. Cancel request");
						int option = Integer.parseInt(scanner.nextLine());
						
						if(option == 1) {
							c.displayBalance();
						} else if (option == 2) {
							c.displayInterest();
						} else if (option == 3) {
							System.out.println("Cancelling your request. Returning to menu...");
							break;
						} else {
							System.out.println("That wasn't a mentioned value, please enter option 1, option 2, or option 3.");
						}
					}
					break;
				}
				case "4": {
					System.out.println("Sort Customer Data");
					c.sortCustomerData();
					break;
				}
				case "5": {
					System.out.println("Persist Customer Data");
					
					while(true) {
						System.out.println("\nWould you like to persist using File System or RDBMS?");
						System.out.println("1. File System");
						System.out.println("2. RDBMS");
						System.out.println("3. Cancel request");
						int option = Integer.parseInt(scanner.nextLine());
						
						if(option == 1) {
							System.out.println("Using File System...");
							customers = c.getCustomers();
							
							System.out.println("\nSavings customers...");
							FileStorageDao dao = new FileStorageDao();
							dao.saveAllCustomers(customers);	
							System.out.println("Successfully saved.");
							
							System.out.println("\nRetrieving customers...");
							List<Customer> imported_customers = null;
							imported_customers = dao.retrieveAllCustomers();
							
							System.out.println("Displaying 'imported_customers':");
							for(Customer cust : imported_customers) {
								System.out.println(cust.toString());
							}							
							
						} else if (option == 2) {
							System.out.println("Using RDBMS...");
							
							customers = c.getCustomers();
							
							try {
								service.saveAllCusts(customers);
							} catch (DuplicateIDException e) {
								e.printStackTrace();
							} catch (ValidationException e) {
								e.printStackTrace();
							} catch (PersistenceException e) {
								e.printStackTrace();
							}
							
						} else if (option == 3) {
							System.out.println("Cancelling your request. Returning to menu...");
							break;
						} else {
							System.out.println("That wasn't a mentioned value, please enter option 1, option 2, or option 3.");
						}
					}
					break;
				}
				case "6": {
					System.out.println("Show All Customers");
					c.showAllCustomers();
					break;
				}
				case "7": {
					System.out.println("Search Customers by Name");
					try {
						System.out.println("(Please make sure to include capitals where suitable.)");
						System.out.print("What name would you like to search by?: ");
						String searchName = scanner.nextLine();
						
						Customer customer = service.retrieveCust(searchName);
						if(customer == null) {
							CustomerNotFoundException cnfe = new CustomerNotFoundException("Unable to find a record of that customer's name, please try again!");
							throw cnfe;
						}
						
						System.out.println("Customer found: " + customer.getCustomerName());
						System.out.println(customer.toString());
					} catch (CustomerNotFoundException cnfe) {
						String message = cnfe.getMessage();
						System.out.println("Error: " + message);	
					}
					
					break;
				}
				default: {
					System.out.println("\nThat value is not in our system, please enter a value between 1 and 8, inclusive.\n");
					cont = true;
				}
			}
			cont = true;
		}
	}
}
