package com.example.customer.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import com.example.customer.entity.Customer;
import com.example.customer.entity.Response;
import com.example.customer.entity.ValidationException;
import com.example.customer.repository.RepositoryCustomers;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class Services {

	@Autowired
	RepositoryCustomers repository;

	private Pattern pattern, patternPhone, patternCodeP;
	private Matcher matcher, matcherPhone, matcherCodeP;


	public Response save(Customer customer, BindingResult validResult) {
		Response response = new Response();
		customer.setCountService(0L);
		Customer customerFound = repository.findCustomerByEmail(customer.getEmail());
		boolean matcherPhoneNumber = validatePhoneNumberAndEmail(customer.getPhoneNumber(), customer.getPostalCode(),customer.getEmail());
		if (matcherPhoneNumber && !validResult.hasErrors()) {
			if (customerFound == null) {
				response.setData(repository.save(customer));
				return response;
			} else {
				throw new ValidationException("Already there a customer with that Email");
			}
		} else {
			throw new ValidationException("Some values are wrong");
		}
	}

	public Response findAll() {
		Response response = new Response();
		List<Customer> listCustomer = repository.findAll();
		if (!listCustomer.isEmpty()) {
			response.setData(listCustomer);
			return response;
		} else {
			throw new ValidationException("Is empty");
		}
	}

	public Response findById(Long id) throws JsonProcessingException {
		Response response = new Response();
		Customer customer = repository.findCustomerById(id);
		if (id != null && id > 0) {
			if (customer != null) {
				response.setData(customer);
				return response;
			} else {
				throw new ValidationException("No customers with that ID");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}

	public Response findByCity(String city) {
		Response response = new Response();
		pattern = Pattern.compile("[a-zA-Z]{4,30}");
		matcher = pattern.matcher(city);
		List<Customer> listCustomer = repository.findCustomerByCity(city);
		if (matcher.matches()) {
			if (!listCustomer.isEmpty()) {
				response.setData(listCustomer);
				return response;
			} else {
				throw new ValidationException("Is empty");
			}
		}else {
			throw new ValidationException("Value is wrong");
		}
	}

	public Response findByState(String state) {
		Response response = new Response();
		pattern = Pattern.compile("[A-Z]{2,3}");
		matcher = pattern.matcher(state);
		List<Customer> listCustomer = repository.findCustomerByState(state);
		if (matcher.matches()) {
			if (!listCustomer.isEmpty()) {
				response.setData(listCustomer);
				return response;
			} else {
				throw new ValidationException("No customers in that State");
			}
		}else {
			throw new ValidationException("Value is wrong");
		}
	}
	
	public Response findByState2(String state) {
		Response response = new Response();
		pattern = Pattern.compile("[A-Z]{2,3}");
		matcher = pattern.matcher(state);
		List<Object> listCustomer = repository.findCustomerByStates(state);
		if (matcher.matches()) {
			if (!listCustomer.isEmpty()) {
				response.setData(listCustomer);
				return response;
			} else {
				throw new ValidationException("No customers in that State");
			}
		}else {
			throw new ValidationException("Value is wrong");
		}
	}

	public Response findByPostalCode(Long code) {
		Response response = new Response();
		pattern = Pattern.compile("[0-9]{5}");
		matcher = pattern.matcher(Long.toString(code));
		List<Customer> listCustomer = repository.findCustomerByPostalCode(code);
		if (matcher.matches()) {
			if (!listCustomer.isEmpty()) {
				response.setData(listCustomer);
				return response;
			} else {
				throw new ValidationException("No customers in that Postal code");
			}
		}else {
			throw new ValidationException("Value is wrong");
		}
	}

	public Response findByEmail(String email) {
		Response response = new Response();
		Customer customer = repository.findCustomerByEmail(email);
		if (email.contains(".com")) {
			if (customer != null) {
				response.setData(customer);
				return response;
			} else {
				throw new ValidationException("No customer with that Email");
			}
		}else {
			throw new ValidationException("Value is wrong");
		}
	}	

	public Response update(Customer customer, Long id, BindingResult validResult) {
		Response response = new Response();
		boolean matcherPhoneNumber = validatePhoneNumberAndEmail(customer.getPhoneNumber(), customer.getPostalCode(), customer.getEmail());
		Customer customerFound = repository.findCustomerById(id);
		if (id != null && id > 0) {
			if (matcherPhoneNumber && !validResult.hasErrors()) {
				if (customerFound != null) {
					response.setData(repository.save(updateCustomer(customer, customerFound)));
					return response;
				} else {
					throw new ValidationException("No customer with that Id");
				}
			} else {
				throw new ValidationException("Some values are wrong");
			}
		} else {
			throw new ValidationException("Id can't be null or zero");
		}
	}

	public Response updateCountService(Long id, Long count) {
		Response response = new Response();
		Customer customerFound = repository.findCustomerById(id);
		if (id != null && id > 0) {
			if (customerFound != null) {
				customerFound.setCountService(count);
				response.setData(repository.save(customerFound));
				return response;
			} else {
				throw new ValidationException("No customer with that Id");
			}
		} else {
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
			} else {
				throw new ValidationException("No customer with that Id");
			}
		} else {
			throw new ValidationException("Id can't be null or zero");
		}
	}

	private boolean validatePhoneNumberAndEmail(Long phoneNumber, Long codeP, String email) {
		patternPhone = Pattern.compile("[0-9]{10}");
		matcherPhone = patternPhone.matcher(Long.toString(phoneNumber));
		patternCodeP = Pattern.compile("[0-9]{5}");
		matcherCodeP = patternCodeP.matcher(Long.toString(codeP));
		if (matcherPhone.matches() && matcherCodeP.matches() && email.contains(".com")) {
			return true;
		} else {
			return false;
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

}
