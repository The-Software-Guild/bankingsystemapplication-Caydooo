package com.banking.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.banking.dto.BankAccount;
import com.banking.dto.FixedDepositAccount;
import com.banking.dto.SavingsAccount;
import com.banking.dto.Customer;

public class DatabaseStorageDao implements StorageDao {

	private Connection openConnection() {
		Connection con = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//System.out.println("MySQL driver registered with DriverManager");
			
			con = DriverManager.getConnection("jdbc:mysql://localhost:3307/bankingsystem", "root", "root");
			//System.out.println(con);
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL suitable driver not found");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return con;
	}
	
	private void closeConnection(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveAllCustomers(List<Customer> customers) {
		Connection con = openConnection();
		int customerCounter = 0;
		int bankAccountCounter = 0;
		int savingsCounter = 0;
		int fixedDepositCounter = 0;
		
		for(Customer customer : customers) {
			//DELETE customer where customerId is equal//
			//(This allows for clean saving, and also update of current customer)//
			try {
				if(customer.getBankAcc() != null) {
					long accountNum = customer.getBankAcc().getAccountNum();
					if(customer.getBankAcc() instanceof SavingsAccount) {
						String deleteSavingsSql = "DELETE FROM savingsaccount WHERE accountNum=?";
						PreparedStatement pStatement = con.prepareStatement(deleteSavingsSql);
						pStatement.setLong(1, accountNum);
						
						int del = pStatement.executeUpdate();
					    
					} else {
						String deleteFixedDepositSql = "DELETE FROM fixeddepositaccount WHERE accountNum=?";
						PreparedStatement pStatement = con.prepareStatement(deleteFixedDepositSql);
						pStatement.setLong(1, accountNum);
						
						int del = pStatement.executeUpdate();
					}
					
					String deleteBankAccountSql = "DELETE FROM bankaccount WHERE accountNum=?";
					PreparedStatement pStatement = con.prepareStatement(deleteBankAccountSql);
					pStatement.setLong(1, accountNum);
					
					int del = pStatement.executeUpdate();
				}
				
				/*if(customer.getCustomerId() == 103) {
					String updateCustomersql = "DELETE FROM fixeddepositaccount WHERE accountNum=364710488";
					PreparedStatement pState = con.prepareStatement(updateCustomersql);
					int upd = pState.executeUpdate();
					System.out.println("Updated 103 successfully");
					
					updateCustomersql = "DELETE FROM bankaccount WHERE accountNum=364710488";
					pState = con.prepareStatement(updateCustomersql);
					upd = pState.executeUpdate();
					System.out.println("Updated 103 successfully");
				}*/
				
				String deleteCustomerSql = "DELETE FROM customer WHERE customerId=?";
				PreparedStatement pStatement = con.prepareStatement(deleteCustomerSql);
				
				int customerId = customer.getCustomerId();
				pStatement.setInt(1, customerId);
				
				int del = pStatement.executeUpdate();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			boolean hasBankAccount = false;
			
			//Customer//
			int customerId = customer.getCustomerId();
			String customerName = customer.getCustomerName();
			String dob = customer.getDob();
			int age = customer.getAge();
			String mobileNum = customer.getMobileNum();
			String passportNum = customer.getPassportNum();
			long accountNum = 0;
			
			long bsbCode = 0;
			String bankName = "";
			double balance = 0;
			String openingDate = "";
			
			boolean isSalaryAccount = false;
			double interestEarned = 0;
			double depositAmount = 0;
			int tenure = 0;
			
			if(customer.getBankAcc() == null) {
				//set accountNum to null?
				hasBankAccount = false;
			} else {
				hasBankAccount = true;
				accountNum = customer.getBankAcc().getAccountNum();
				
				//BankAccount//
				accountNum = customer.getBankAcc().getAccountNum();
				bsbCode = customer.getBankAcc().getBsbCode();
				bankName = customer.getBankAcc().getBankName();
				balance = customer.getBankAcc().getBalance();
				openingDate = customer.getBankAcc().getOpeningDate();
				
				//SavingsAccount//
				if(customer.getBankAcc() instanceof SavingsAccount) {
					SavingsAccount sa = (SavingsAccount) customer.getBankAcc();
					accountNum = customer.getBankAcc().getAccountNum();
					isSalaryAccount = sa.isSalaryAccount();
					interestEarned = sa.getInterestEarned();
				}
				
				//FixedDepositAccount//
				if(customer.getBankAcc() instanceof FixedDepositAccount) {
					FixedDepositAccount fda = (FixedDepositAccount) customer.getBankAcc();
					accountNum = customer.getBankAcc().getAccountNum();
					depositAmount = fda.getDepositAmount();
					tenure = fda.getTenure();
					interestEarned = fda.getInterestEarned();
				}
			}
			
			try {
				//First INSERT - Customer
				
				String customerSql = "INSERT INTO customer (customerId, customerName, dob, age, mobileNum, "
						+ "passportNum) VALUES (?, ?, ?, ?, ?, ?);";
				PreparedStatement pStatement = con.prepareStatement(customerSql);
				
				pStatement.setInt(1, customerId);
				pStatement.setString(2, customerName);
				pStatement.setString(3, dob);
				pStatement.setInt(4, age);
				pStatement.setString(5, mobileNum);
				pStatement.setString(6, passportNum);
				
				int n = pStatement.executeUpdate();

				//System.out.println(customerSql);
				customerCounter++;
				
				if(hasBankAccount) {
					//Second INSERT - BankAccount
					
					String bankAccountSql = "INSERT INTO bankaccount (accountNum, bsbCode, bankName, balance, openingDate, customerId)"
							+ " VALUES (?, ?, ?, ?, ?, ?);";
					pStatement = con.prepareStatement(bankAccountSql);
					
					pStatement.setLong(1, accountNum);
					pStatement.setLong(2, bsbCode);
					pStatement.setString(3, bankName);
					pStatement.setDouble(4, balance);
					pStatement.setString(5, openingDate);
					pStatement.setInt(6, customerId);
					
					n = pStatement.executeUpdate();

					//System.out.println(bankAccountSql);
					bankAccountCounter++;
					
					//Third INSERT - SavingsAccount OR FixedDepositAccount
					
					if(customer.getBankAcc() instanceof SavingsAccount) {
						String savingsAccountSql = "INSERT INTO savingsaccount (accountNum, isSalaryAccount, interestEarned)"
								+ " VALUES (?, ?, ?);";
						pStatement = con.prepareStatement(savingsAccountSql);
						
						pStatement.setLong(1, accountNum);
						pStatement.setBoolean(2, isSalaryAccount);
						pStatement.setDouble(3, interestEarned);
						
						n = pStatement.executeUpdate();

						//System.out.println(savingsAccountSql);
						savingsCounter++;
					} else {
						
						// is a FixedDepositAccount //
						String fixedDepositAccountSql = "INSERT INTO fixeddepositaccount (accountNum, depositAmount, tenure, interestEarned)"
								+ " VALUES (?, ?, ?, ?);";
						
						pStatement = con.prepareStatement(fixedDepositAccountSql);
						
						pStatement.setLong(1, accountNum);
						pStatement.setDouble(2, depositAmount);
						pStatement.setInt(3, tenure);
						pStatement.setDouble(4, interestEarned);
						
						n = pStatement.executeUpdate();

						//System.out.println(fixedDepositAccountSql);
						fixedDepositCounter++;

					}
					
				}

				pStatement.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}		
		}
		
		System.out.println("Number of rows inserted for Customer: " + customerCounter);
		System.out.println("Number of rows inserted for BankAccount: " + bankAccountCounter);
		System.out.println("Number of rows inserted for SavingsAccount: " + savingsCounter);
		System.out.println("Number of rows inserted for FixedDepositAccount: " + fixedDepositCounter);	
		
		closeConnection(con);
	}

	@Override
	public List<Customer> retrieveAllCustomers() {
		Connection con = openConnection();
		
		String sql = "SELECT * FROM customer;";
		List<Customer> customers = new ArrayList<>();;
		try {
			PreparedStatement pStatement = con.prepareStatement(sql);
			ResultSet rSet = pStatement.executeQuery();

			while(rSet.next()) {
				//Customer//
				int customerId = rSet.getInt("customerId");
				String customerName = rSet.getString("customerName");
				String dob = rSet.getString("dob");
				int age = rSet.getInt("age");
				String mobileNum = rSet.getString("mobileNum");
				String passportNum = rSet.getString("passportNum");
				BankAccount bankAccount = null;
				
				//BankAccount//	
				String bankAccountSql = "SELECT * FROM bankaccount WHERE customerId=?;";
				pStatement = con.prepareStatement(bankAccountSql);
					
				pStatement.setLong(1, customerId);
				ResultSet rs = pStatement.executeQuery();
					
				while(rs.next()) {
					long accountNum = rs.getLong("accountNum");
					long bsbCode = rs.getLong("bsbCode");
					String bankName = rs.getString("bankName");
					double balance = rs.getDouble("balance");
					String openingDate = rs.getString("openingDate");
						
					//SavingsAccount or FixedDepositAccount//		
					//testing for SavingsAccount, if return 0 then it is a FixedDepositAccount
					String savingsAccountCounter = "SELECT COUNT(*) FROM savingsaccount WHERE accountNum=?;";
					boolean isSavingsAccount = true;
					pStatement = con.prepareStatement(savingsAccountCounter);
						
					pStatement.setLong(1, accountNum);
					rs = pStatement.executeQuery();
						
					while(rs.next()) {
						if(rs.getInt(1) == 0) {
							System.out.println("Customer has FixedDepositAccount");
							isSavingsAccount = false;
							break;
						} else {
							System.out.println("Customer has SavingsAccount");
							isSavingsAccount = true;
							break;
						}	
					}
						
					if(isSavingsAccount) {
						//SavingsAccount//
						String savingsAccountSql = "SELECT * FROM savingsaccount WHERE accountNum=?;";
						pStatement = con.prepareStatement(savingsAccountSql);
							
						pStatement.setLong(1, accountNum);
						rs = pStatement.executeQuery();
							
						while(rs.next()) {
							accountNum = rs.getLong("accountNum");
							boolean isSalaryAccount = rs.getBoolean("isSalaryAccount");
							double interestEarned = rs.getDouble("interestEarned");
							
							bankAccount = new SavingsAccount(accountNum, bsbCode, bankName, balance, openingDate, isSalaryAccount, interestEarned);
						}
							
					} else {
						//FixedDepositAccount//
						String fixedDepositSql = "SELECT * FROM fixeddepositaccount WHERE accountNum=?;";
						pStatement = con.prepareStatement(fixedDepositSql);
							
						pStatement.setLong(1, accountNum);
						rs = pStatement.executeQuery();
							
						while(rs.next()) {
							accountNum = rs.getLong("accountNum");
							double depositAmount = rs.getDouble("depositAmount");
							int tenure = rs.getInt("tenure");
							double interestEarned = rs.getDouble("interestEarned");
								
							bankAccount = new FixedDepositAccount(accountNum, bsbCode, bankName, balance, openingDate, depositAmount, tenure, interestEarned);
						}
					}
				}
				Customer customer = new Customer(customerId, customerName, dob, age, mobileNum, passportNum);
				customer.setBankAcc(bankAccount);
				customers.add(customer);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		closeConnection(con);
		return customers;
	}

	public Customer retrieveCustomer(String name) {
		Connection con = openConnection();
		
		Customer customer = null;
		String sql = "SELECT COUNT(*) FROM customer WHERE customerName=?;";
		
		try {
			PreparedStatement pState = con.prepareStatement(sql);
			pState.setString(1, name);
			ResultSet rSet = pState.executeQuery();
			
			while(rSet.next()) {
				if(rSet.getInt(1) == 0) {
					return customer;
				} else {
					System.out.println("Customer found");
					
					String findCustomerSql = "SELECT * FROM customer WHERE customerName=?;";
					PreparedStatement pStatement = con.prepareStatement(findCustomerSql);
					pStatement.setString(1, name);
					ResultSet rs = pStatement.executeQuery();	
					
					while(rs.next()) {
						//Customer//
						int customerId = rs.getInt("customerId");
						System.out.println("customerId: " + customerId);
						String customerName = rs.getString("customerName");
						String dob = rs.getString("dob");
						int age = rs.getInt("age");
						String mobileNum = rs.getString("mobileNum");
						String passportNum = rs.getString("passportNum");
						BankAccount bankAccount = null;
							
						//BankAccount//	
						String bankAccountSql = "SELECT * FROM bankaccount WHERE customerId=?;";
						pStatement = con.prepareStatement(bankAccountSql);
							
						pStatement.setLong(1, customerId);
						rs = pStatement.executeQuery();
							
						while(rs.next()) {
							long accountNum = rs.getLong("accountNum");
							long bsbCode = rs.getLong("bsbCode");
							String bankName = rs.getString("bankName");
							double balance = rs.getDouble("balance");
							String openingDate = rs.getString("openingDate");
								
							//SavingsAccount or FixedDepositAccount//		
							//testing for SavingsAccount, if return 0 then it is a FixedDepositAccount
							String savingsAccountCounter = "SELECT COUNT(*) FROM savingsaccount WHERE accountNum=?;";
							boolean isSavingsAccount = true;
							pStatement = con.prepareStatement(savingsAccountCounter);
								
							pStatement.setLong(1, accountNum);
							rs = pStatement.executeQuery();
								
							while(rs.next()) {
								if(rs.getInt(1) == 0) {
									System.out.println("Customer has FixedDepositAccount");
									isSavingsAccount = false;
									break;
								} else {
									System.out.println("Customer has SavingsAccount");
									isSavingsAccount = true;
									break;
								}	
							}
								
							if(isSavingsAccount) {
								//SavingsAccount//
								String savingsAccountSql = "SELECT * FROM savingsaccount WHERE accountNum=?;";
								pStatement = con.prepareStatement(savingsAccountSql);
									
								pStatement.setLong(1, accountNum);
								rs = pStatement.executeQuery();
									
								while(rs.next()) {
									accountNum = rs.getLong("accountNum");
									boolean isSalaryAccount = rs.getBoolean("isSalaryAccount");
									double interestEarned = rs.getDouble("interestEarned");
									
									bankAccount = new SavingsAccount(accountNum, bsbCode, bankName, balance, openingDate, isSalaryAccount, interestEarned);
								}
									
							} else {
								//FixedDepositAccount//
								String fixedDepositSql = "SELECT * FROM fixeddepositaccount WHERE accountNum=?;";
								pStatement = con.prepareStatement(fixedDepositSql);
									
								pStatement.setLong(1, accountNum);
								rs = pStatement.executeQuery();
									
								while(rs.next()) {
									accountNum = rs.getLong("accountNum");
									double depositAmount = rs.getDouble("depositAmount");
									int tenure = rs.getInt("tenure");
									double interestEarned = rs.getDouble("interestEarned");
										
									bankAccount = new FixedDepositAccount(accountNum, bsbCode, bankName, balance, openingDate, depositAmount, tenure, interestEarned);
								}
							}
						}
						customer = new Customer(customerId, customerName, dob, age, mobileNum, passportNum);
						customer.setBankAcc(bankAccount);
					}
					rs.close();
					pStatement.close();
					break;
				}	
			}
			pState.close();
			rSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		closeConnection(con);
		return customer;
	}
}