package com.example.houseCleaning.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.houseCleaning.entity.Customer;
import com.example.houseCleaning.entity.Employee;
import com.example.houseCleaning.entity.Response;
import com.example.houseCleaning.entity.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Services {

	@Autowired
	WebClient webClient;
	@Value("${customerSave}")
	private String customerSave;
	@Value("${employeeSave}")
	private String employeeSave;
	
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
	
	public Response login(String user, String pass) {
		Response response = new Response();
		return response;
	}

	

	

}
