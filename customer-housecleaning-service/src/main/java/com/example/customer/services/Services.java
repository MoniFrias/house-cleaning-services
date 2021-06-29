package com.example.customer.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.example.customer.entity.Customer;
import com.example.customer.entity.Response;
import com.example.customer.entity.ValidationException;
import com.example.customer.repository.RepositoryCustomers;


@Service
public class Services {
	
	@Autowired
	RepositoryCustomers repository;
	
	private Pattern pattern;
	private Matcher matcher;
	
	private boolean validatePhoneNumber(Long phoneNumber) {
		pattern = Pattern.compile("[0-9]{10}");
		matcher = pattern.matcher(Long.toString(phoneNumber));
		if(matcher.matches()) {
			return true;
		}else {
			return false;
		}
	}

	public Response save(@Valid Customer customer, BindingResult validResult) {
		Response response = new Response();
		Customer customerFound = repository.findCustomerByEmail(customer.getEmail());
		boolean matcherPhoneNumber = validatePhoneNumber(customer.getPhoneNumber());
		if(matcherPhoneNumber && !validResult.hasErrors()) {
			if (customerFound == null) {
				response.setData(repository.save(customer));
				return response;
			}else {
				throw new ValidationException("Already there a customer with that Email");
			}			
		}else {
			throw new ValidationException("Some values are wrong");
		}		
	}

	public Response findAll() {
		Response response = new Response();
		List<Customer> listCustomer = repository.findAll();
		if (!listCustomer.isEmpty()) {
			response.setData(listCustomer);
			return response;
		}else {
			throw new ValidationException("Is empty");
		}	
	}
	
	public Response findByCity(String city) {
		Response response = new Response();
		List<Customer> listCustomer = repository.findCustomerByCity(city);
		if (!listCustomer.isEmpty()) {
			response.setData(listCustomer);
			return response;
		}else {
			throw new ValidationException("Is empty");
		}	
	}

	public Response findByEmail(String email) {
		Response response = new Response();
		Customer customer = repository.findCustomerByEmail(email);
		if (customer != null) {
			response.setData(customer);
			return response;
		}else {
			throw new ValidationException("There isn't a customer with that Email");
		}
	}
	
	private Customer updateCustomer(Customer customer, Customer customerFound) {
		customerFound.setName(customer.getName());
		customerFound.setLastName(customer.getLastName());
		customerFound.setEmail(customer.getEmail());
		customerFound.setCity(customer.getCity());
		customerFound.setAddress(customer.getAddress());
		customerFound.setPhoneNumber(customer.getPhoneNumber());
		return customerFound;
	}

	public Response update(Customer customer, Long id, BindingResult validResult) {
		Response response = new Response();
		boolean matcherPhoneNumber = validatePhoneNumber(customer.getPhoneNumber());
		Customer customerFound = repository.findCustomerById(id);
		if (id != null && id > 0) {
			if (matcherPhoneNumber && !validResult.hasErrors()) {
				if (customerFound != null) {
					response.setData(repository.save(updateCustomer(customer, customerFound)));
					return response;
				}else {
					throw new ValidationException("There isn't a customer with that Id");
				}
			}else {
				throw new ValidationException("Some values are wrong");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}

	public Response deleteById(Long id) {
		Response response = new Response();
		Customer customerFound = repository.findCustomerById(id);
		if (id != null && id > 0) {
			if (customerFound != null) {
				repository.deleteById(id);
				return response;
			}else {
				throw new ValidationException("There isn't a customer with that Id");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}
}
