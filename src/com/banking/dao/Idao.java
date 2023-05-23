package com.banking.dao;

import java.util.List;
import com.banking.dto.Customer;

public abstract interface Idao {
	
	abstract void saveAllCustomers(List<Customer> customers); // C of CRUD
	abstract List<Customer> retrieveAllCustomers(); // R of CRUD
	Customer retrieveCustomer(String name); // R of CRUD
}
