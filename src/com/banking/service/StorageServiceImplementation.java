package com.banking.service;

import java.util.List;

import com.banking.dto.Customer;
import com.banking.dao.DatabaseStorageDao;
import com.banking.dao.StorageDao;
import com.banking.exceptions.DuplicateIDException;
import com.banking.exceptions.PersistenceException;
import com.banking.exceptions.ValidationException;

public class StorageServiceImplementation implements StorageService {

	private DatabaseStorageDao dsdao;
	
	public StorageServiceImplementation() {
		dsdao = new DatabaseStorageDao();
	}
	
	@Override
	public void saveAllCusts(List<Customer> customers) 
			throws DuplicateIDException, ValidationException, PersistenceException {
		dsdao.saveAllCustomers(customers);
	}

	@Override
	public List<Customer> retrieveAllCusts() {
		List<Customer> customers = dsdao.retrieveAllCustomers();
		return customers;
	}

	@Override
	public Customer retrieveCust(String name) {
		Customer customer = dsdao.retrieveCustomer(name);
		return customer;
	}

}
