package com.example.customer.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.text.MaskFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.customer.entity.BookService;
import com.example.customer.entity.Customer;
import com.example.customer.entity.Payment;
import com.example.customer.entity.Response;
import com.example.customer.entity.ValidationException;
import com.example.customer.repository.RepositoryCustomers;
import com.example.customer.repository.RepositoryPayment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class Services {

	@Autowired
	RepositoryCustomers repository;
	@Autowired
	RepositoryPayment repositoryPayment;
	@Autowired
	WebClient webClient;
	@Value("${bookServiceFindByIdCustomer}")
	private String bookServiceFindByIdCustomer;


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
	
	public static String formatCardNumber(String cardNumber){        
        StringBuilder sbMaskString = new StringBuilder(12);        
        for(int i = 0; i < 12; i++){        
        	sbMaskString.append('*');
        }        
        return sbMaskString.toString() 
            + cardNumber.substring(0 + 12);
	}
	
	public Response savePayment(Payment payment, BindingResult validResultPayment){
		Response response = new Response();
		pattern = Pattern.compile("[0-9]{16}");
		matcher = pattern.matcher(Long.toString(payment.getCardNumber()));
	    Customer customerFound;			
		if (payment.getIdCustomer() != null && payment.getIdCustomer() > 0) {
			if (matcher.matches() && !validResultPayment.hasErrors()) {
				try {
					customerFound = (Customer) findById(payment.getIdCustomer()).getData();
				} catch (JsonProcessingException e) {
					throw new ValidationException("No customers with that ID");
				}
				if (customerFound != null) {
					Payment paymentFound = repositoryPayment.findPaymentByIdCustomerAndCardNumber(payment.getIdCustomer(),payment.getCardNumber());
					if (paymentFound==null) {
						repositoryPayment.save(payment);
						String cardNumber = formatCardNumber(Long.toString(payment.getCardNumber()));
						response.setData(cardNumber);
						return response;
					}else {
						throw new ValidationException("Already saved that car number");
					}
				}else {
					throw new ValidationException("No customers saved");
				}
			}else {
				throw new ValidationException("Some values are wrong");
			}
		}else {
			throw new ValidationException("Id Customer can't be null or zero");
		}		
	}
	
	private Customer setPaymentsMethods(Customer customer){
		List<Payment> listPaymenteFound =  repositoryPayment.findPaymentByIdCustomer(customer.getId());
		if (listPaymenteFound.isEmpty()) {
			customer.setListPayment(new ArrayList<>());
			return customer;
		}else {
			customer.setListPayment(listPaymenteFound);
			return customer;
		}
	}

	public Response findAll() {
		Response response = new Response();
		List<Customer> listCustomer = repository.findAll();
		if (!listCustomer.isEmpty()) {
			
			listCustomer.stream().map(customer ->{
				Customer customerNew = setPaymentsMethods(customer);
				return customerNew;
			}).filter(Objects::nonNull).collect(Collectors.toList());
			
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
				Customer customerNew = setPaymentsMethods(customer);
				response.setData(customerNew);
				return response;
			} else {
				throw new ValidationException("No customers with that ID");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}
	
	public Response findBookServiceByIdCustomer(Long id) throws JsonProcessingException {
		Response response = new Response();
		Customer customer = repository.findCustomerById(id);
		if (id != null && id > 0) {
			if (customer != null) {
				Customer customerNew = setPaymentsMethods(customer);
				customerNew.setListBookService(listBookServiceFound(customerNew.getId()));
				response.setData(customerNew);
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
				listCustomer.stream().map(customer ->{
					Customer customerNew = setPaymentsMethods(customer);
					return customerNew;
				}).filter(Objects::nonNull).collect(Collectors.toList());
				
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
				listCustomer.stream().map(customer ->{
					Customer customerNew = setPaymentsMethods(customer);
					return customerNew;
				}).filter(Objects::nonNull).collect(Collectors.toList());
								
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
//				listCustomer.stream().map(customer ->{
//					Customer customerNew = setPaymentsMethods((Customer) customer);
//					return customerNew;
//				}).filter(Objects::nonNull).collect(Collectors.toList());				
				
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
				listCustomer.stream().map(customer ->{
					Customer customerNew = setPaymentsMethods(customer);
					return customerNew;
				}).filter(Objects::nonNull).collect(Collectors.toList());				
				
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
				Customer customerNew = setPaymentsMethods(customer);
				response.setData(customerNew);
				return response;
			} else {
				throw new ValidationException("No customer with that Email");
			}
		}else {
			throw new ValidationException("Value is wrong");
		}
	}	

	public Response update(Customer customer,BindingResult validResultUpdate, Long id) {
		Response response = new Response();
		customer.setCountService(0L);
		Customer customerFound = repository.findCustomerById(id);
		Customer customerFoundByEmail = repository.findCustomerByEmail(customer.getEmail()); 
		boolean matcherPhoneNumber = validatePhoneNumberAndEmail(customer.getPhoneNumber(), customer.getPostalCode(),customer.getEmail());
		if (id != null && id > 0) {
			if (matcherPhoneNumber && !validResultUpdate.hasErrors()) {
				if (customerFoundByEmail == null) {
					if (customerFound != null) {
						response.setData(repository.save(updateCustomer(customer, customerFound)));
						return response;
					} else {
						throw new ValidationException("No customer with that Id");
					}
				}else {
					if (customerFound != null && customerFoundByEmail.getId() == id) {
						response.setData(repository.save(updateCustomer(customer, customerFound)));
						return response;
					} else {
						throw new ValidationException("No customer with that Id or already exits with that Email");
					}
				}
			} else {
				throw new ValidationException("Some values are wrong");
			}
		}else {
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
				List<Payment> listPaymentByCustomer = repositoryPayment.findPaymentByIdCustomer(customerFound.getId());
				listPaymentByCustomer.stream().forEach(customer->{
					repositoryPayment.deletePaymentByIdCustomer(customer.getId());
				});				
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
	
	private List<BookService> listBookServiceFound(Long id) throws JsonProcessingException{
		Response objectResponse = null;
		try {
			objectResponse = webClient.get()
					.uri(bookServiceFindByIdCustomer+id)
					.retrieve()
					.bodyToMono(Response.class)
					.block();
		}catch (Exception e) {
			throw new ValidationException("No Book services for that Customer");
		}
		
		Object objectBookService = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		String stringResponse = objectMapper.writeValueAsString(objectBookService);
		List<BookService> responseBookService = objectMapper.readValue(stringResponse, new TypeReference<List<BookService>>() {});
		return responseBookService;
	}


}
