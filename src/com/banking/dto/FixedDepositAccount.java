package com.banking.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

public class FixedDepositAccount extends BankAccount implements Serializable {
	private double depositAmount; // minimum is $1000
	private int tenure; // when the account is opened, specify between 1 and 7 years
	private double interestEarned; // calculate when the account has been opened with depositAmount and tenure
	
	public FixedDepositAccount(long accountNum, long bsbCode, String bankName, double balance, String openingDate, double depositAmount, int tenure) {
		super(accountNum, bsbCode, bankName, balance, openingDate); // from BankAccount
		this.depositAmount = depositAmount;
		this.tenure = tenure;
	}
	
	public FixedDepositAccount(long accountNum, long bsbCode, String bankName, double balance, String openingDate, double depositAmount, int tenure, double interestEarned) {
		super(accountNum, bsbCode, bankName, balance, openingDate); // from BankAccount
		this.depositAmount = depositAmount;
		this.tenure = tenure;
		this.interestEarned = interestEarned;
	}

	public FixedDepositAccount() {} // default constructor

	public double getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(double depositAmount) {
		this.depositAmount = depositAmount;
	}

	public void addToBalance(double depositAmount) {
		double balance = super.getBalance();
		balance += this.depositAmount;
		super.setBalance(balance);
	}

	public int getTenure() {
		return tenure;
	}

	public void setTenure(int tenure) {
		this.tenure = tenure;
	}

	public double getInterestEarned() {
		return interestEarned;
	}

	public void setInterestEarned(double interestEarned) {
		this.interestEarned = interestEarned;
	}

	@Override
	public String toString() {
		return super.toString() + " FixedDepositAccount [depositAmount=" + depositAmount + ", tenure=" + tenure + ", interestEarned="
				+ interestEarned + "]";
	}

	@Override
	public void calculateInterest() {
		LocalDate ldCurrent = LocalDate.now();
		String ldStr = ldCurrent.format(formatter);
		LocalDate ldOpeningDate = LocalDate.parse(this.getOpeningDate(), formatter);
		ldCurrent = LocalDate.parse(ldStr, formatter);
		
		int currentAccountAge = Period.between(ldOpeningDate, ldCurrent).getYears();
		if(currentAccountAge < 1) {
			setInterestEarned(this.getBalance() * 0.08 * this.tenure);
		} else {
			setInterestEarned(this.getBalance() * (0.08 * currentAccountAge) * this.tenure);
		}
		
	}
}
