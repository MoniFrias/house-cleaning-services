package com.example.houseCleaning.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.houseCleaning.entity.Appointment;
import com.example.houseCleaning.entity.BookService;
import com.example.houseCleaning.entity.Customer;
import com.example.houseCleaning.entity.Employee;
import com.example.houseCleaning.entity.Response;
import com.example.houseCleaning.entity.TypeService;
import com.example.houseCleaning.entity.ValidationException;
import com.example.houseCleaning.repository.RepositoryBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Services {

	@Autowired
	WebClient webClient;
	@Autowired
	RepositoryBook repository;
	
	@Value("${customerSave}")
	private String customerSave;
	@Value("${customerFindById}")
	private String customerFindById;
	@Value("${customerFindByEmail}")
	private String customerFindByEmail;
	@Value("${customerUpdateCountService}")
	private String customerUpdateCountService;
	@Value("${employeeSave}")
	private String employeeSave;
	@Value("${employeeFindByPostalCode}")
	private String employeeFindByPostalCode;
	@Value("${employeeSaveAppointment}")
	private String employeeSaveAppointment;
	@Value("${typeServiceFindByType}")	
	private String typeServiceFindByType;
	
	
	
	private Customer saveCustomer(Customer customer) throws JsonMappingException, JsonProcessingException {
		MediaType contentType = null;
		Response objectResponse = null;
		try {
			objectResponse = webClient.post().uri(customerSave).contentType(contentType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(customer)).retrieve().bodyToMono(Response.class).block();
		} catch (Exception e) {
			throw new ValidationException("Some data is wrong or already there a customer with that Email");
		}

		Object objectCustomer = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectCustomer);
		Customer responseCustomer = objectMapper.readValue(stringResponse, Customer.class);
		return responseCustomer;
	}
	
	private Employee saveEmployee(Employee employee) throws JsonMappingException, JsonProcessingException {
		MediaType contentType = null;
		Response objectResponse = null;
		try {
			objectResponse = webClient.post().uri(employeeSave).contentType(contentType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(employee)).retrieve().bodyToMono(Response.class).block();
		} catch (Exception e) {
			throw new ValidationException("Some data is wrong or already there a employee with that Email");
		}

		Object objectEmployee = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectEmployee);
		Employee responseEmployee = objectMapper.readValue(stringResponse, Employee.class);
		return responseEmployee;
	}
	
	private List<Employee> employeeFindByPostalCode(Long codeP) throws JsonMappingException, JsonProcessingException{
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(employeeFindByPostalCode+codeP).retrieve().bodyToMono(Response.class).block();
		}catch (Exception e) {
			throw new ValidationException("There aren't employees in that Postal code");
		}
		
		Object objectEmployee = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		String stringResponse = objectMapper.writeValueAsString(objectEmployee);
		List<Employee> responseEmployee = objectMapper.readValue(stringResponse, new TypeReference<List<Employee>>() {});
		return responseEmployee;
	}
	
	private Customer customerFindById(Long id) throws JsonMappingException, JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(customerFindById+id).retrieve().bodyToMono(Response.class).block();
		}catch (Exception e) {
			throw new ValidationException("There aren't customer with that ID");
		}				
		Object objectCustomer = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectCustomer);
		Customer responseCustomer = objectMapper.readValue(stringResponse, Customer.class);
		return responseCustomer;
	}
	
	private TypeService typeServiceFindByType(String type) throws JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(typeServiceFindByType+type).retrieve().bodyToMono(Response.class).block();
		}catch (Exception e) {
			throw new ValidationException("There aren't type services with that name");
		}				
		Object objectType = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectType);
		TypeService responseType = objectMapper.readValue(stringResponse, TypeService.class);
		return responseType;

	}
	
	private Customer customerFindByEmail(String email) throws JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(customerFindByEmail+email).retrieve().bodyToMono(Response.class).block();
		}catch (Exception e) {
			throw new ValidationException("There isn't a customer with that Email");
		}				
		Object objectCustomer = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectCustomer);
		Customer responseCustomer = objectMapper.readValue(stringResponse, Customer.class);
		return responseCustomer;
	}
	
	private void employeeSaveAppointment(Appointment appointment) {
		MediaType contentType = null;
		webClient.post().uri(employeeSaveAppointment).contentType(contentType.APPLICATION_JSON)
		.body(BodyInserters.fromValue(appointment)).retrieve().bodyToMono(Response.class).block();
	}
	
	private void customerUpdateCountService(Long id, Long count) {
		webClient.put().uri(customerUpdateCountService, uri -> uri.queryParam("id", id).queryParam("count", count).build()) 
		.retrieve().bodyToMono(Response.class).block();
	}
	
	public Response createAccountCustomer(Customer customer) {
		Response response = new Response();
		Customer customerFound = null;
		try {
			customer.setCountService(0L);
			customerFound = saveCustomer(customer);
		} catch (JsonProcessingException e) {
			log.error("error {}",e);
			throw new ValidationException(e.getMessage());
		}
		response.setData(customerFound);
		return response;		
	}
	
	public Response createAccountEmployee(Employee employee) {
		Response response = new Response();
		Employee employeeFound = null;
		try {
			employeeFound = saveEmployee(employee);
		} catch (JsonProcessingException e) {
			log.error("error {}",e);
			throw new ValidationException(e.getMessage());
		}
		response.setData(employeeFound);
		return response;
	}
	
	public Response login(String email) throws JsonProcessingException {
		Response response = new Response();	
		Customer customerFound = customerFindByEmail(email);
		if(customerFound != null) {
			response.setMessage("Welcome "+ customerFound.getName());
			return response;
		}else {
			throw new ValidationException("There aren't customers with that email");
		}
		
	}
		
	public Response bookService(BookService bookService) throws JsonMappingException, JsonProcessingException {
		Response response = new Response();
		List<Employee> listEmployee;
		final LocalDate dateBook = bookService.getDate();
		final LocalTime timeBook = bookService.getStarTime();
		try {
			listEmployee = employeeFindByPostalCode(bookService.getCodeP());
		} catch (JsonProcessingException e) {
			log.error("error {}", e);
			throw new ValidationException(e.getMessage());
		}

		Optional<Employee> employeeA = listEmployee.stream().map(employee -> {
			long count = 0;
			if (employee.getAppointments() != null) {
				count = employee.getAppointments().parallelStream()
						.filter(appointment -> dateBook.isEqual(appointment.getDate())
								&& !timeBook.isBefore(appointment.getStarTime()) && !timeBook.isAfter(appointment.getEndTime()))
						.count();
			}
			return (count > 0) ? null : employee;
		}).filter(Objects::nonNull).findFirst();

		if (employeeA.isPresent()) {
			Employee employee = employeeA.get();
			Customer customerStatus = customerFindById(bookService.getIdCustomer());
			TypeService typeServiceFound = typeServiceFindByType(bookService.getTypeService());

			if (customerStatus.getCountService() == 0) {
				double descount = typeServiceFound.getCost() * (0.2);
				Long costTotal = typeServiceFound.getCost() - (new Double(descount)).longValue();
				customerUpdateCountService(bookService.getIdCustomer(), 1L);
				bookService.setCost(costTotal);
				bookService.setIdEmployee(employee.getId());
				Appointment appoitmentValues = setValuesAppointment(bookService);
				employeeSaveAppointment(appoitmentValues);
				response.setData(repository.save(bookService));
				return response;
			} else {
				Long countNew = customerStatus.getCountService() + 1L;
				customerUpdateCountService(bookService.getIdCustomer(), countNew);	
				bookService.setIdEmployee(employee.getId());
				bookService.setCost(typeServiceFound.getCost());
				Appointment appoitmentValues = setValuesAppointment(bookService);
				employeeSaveAppointment(appoitmentValues);
				response.setData(repository.save(bookService));
				return response;
			}
		} else {
			throw new ValidationException("There aren't employees");
		}
	}
	
	private Appointment setValuesAppointment(BookService bookService) {
		Appointment appointment = new Appointment();
		appointment.setIdEmployee(bookService.getIdEmployee());
		appointment.setTypeService(bookService.getTypeService());
		appointment.setDate(bookService.getDate());
		appointment.setStarTime(bookService.getStarTime());
		return appointment;
		
	}

	public Response validatePay(BookService bookService) {
		// TODO Auto-generated method stub
		return null;
	}
	
	


	

}
