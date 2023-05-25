package com.banking.service;

import java.util.List;
import com.banking.dto.Customer;
import com.banking.exceptions.DuplicateIDException;
import com.banking.exceptions.PersistenceException;
import com.banking.exceptions.ValidationException;

public interface StorageService {

	void saveAllCusts(List<Customer> customers) throws DuplicateIDException, ValidationException, PersistenceException;
	List<Customer> retrieveAllCusts();
	Customer retrieveCust(String name);
}
