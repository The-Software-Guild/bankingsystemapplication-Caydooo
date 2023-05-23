package com.banking.service;

import java.util.List;

import com.banking.dto.Customer;
import com.banking.dao.DatabaseStorageDao;
import com.banking.exceptions.DuplicateIDException;
import com.banking.exceptions.PersistenceException;
import com.banking.exceptions.ValidationException;

public class CustomerServiceImplementation implements CustomerService {

	private DatabaseStorageDao dao;
	
	public CustomerServiceImplementation() {
		dao = new DatabaseStorageDao();
	}
	
	@Override
	public void saveAllCusts(List<Customer> customers) 
			throws DuplicateIDException, ValidationException, PersistenceException {
		dao.saveAllCustomers(customers);
	}

	@Override
	public List<Customer> retrieveAllCusts() {
		List<Customer> customers = dao.retrieveAllCustomers();
		return customers;
	}

	@Override
	public Customer retrieveCust(String name) {
		Customer customer = dao.retrieveCustomer(name);
		return customer;
	}

}
