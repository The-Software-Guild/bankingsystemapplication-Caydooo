package com.banking.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.banking.dto.Customer;

public class FileStorageDao implements Idao {

	@Override
	public void saveAllCustomers(List<Customer> customers) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ObjectOutputStream oos = null;
		
		File f = new File("C://C353/customers.txt");

		try {
			fos = new FileOutputStream(f); 			//  f  -> fos
			bos = new BufferedOutputStream(fos);	// fos -> bos
			oos = new ObjectOutputStream(bos);		// bos -> oos
			
			oos.writeObject(customers);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<Customer> retrieveAllCustomers() {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ObjectInputStream ois = null;
		
		File f = new File("C://C353/customers.txt");
		
		try {
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			ois = new ObjectInputStream(bis);
			
			Object obj = ois.readObject();
			if(obj instanceof List) {
				List<Customer> imported_customers = (List<Customer>) obj;
				//Student s = (Student)obj;
				//System.out.println(imported_customers);
				return imported_customers;
			}			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Customer retrieveCustomer(String name) {
		return null;
	}
	
}
