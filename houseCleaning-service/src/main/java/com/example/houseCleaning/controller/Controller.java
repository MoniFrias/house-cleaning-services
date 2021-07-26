package com.example.houseCleaning.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.houseCleaning.entity.BookService;
import com.example.houseCleaning.entity.Customer;
import com.example.houseCleaning.entity.Employee;
import com.example.houseCleaning.entity.Response;
import com.example.houseCleaning.services.Services;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@EnableResourceServer
@RestController
@RequestMapping(path = "houseCleaning")
public class Controller extends ResourceServerConfigurerAdapter{

	@Autowired
	Services services;

	@PostMapping(path = "/admin")
	public String admin() {
		return "Pagina Administrador";
	}

	@PostMapping(path = "/createAccountCustomer")
	public ResponseEntity<Response> createAccountCustomer(@RequestBody Customer customer) {
		Response response = services.createAccountCustomer(customer);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(path = "/createAccountEmployee")
	public ResponseEntity<Response> createAccountEmployee(@RequestBody Employee employee) {
		Response response = services.createAccountEmployee(employee);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(path = "/login")
	public ResponseEntity<Response> login(@RequestParam(name = "email") String email) throws JsonProcessingException {
		Response response = services.login(email);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(path = "/bookService")
	public ResponseEntity<Response> bookService(@Valid @RequestBody BookService bookService, BindingResult bindingResult) throws JsonMappingException, JsonProcessingException {
		Response response = services.bookService(bookService,bindingResult);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(path = "/validatePay")
	public ResponseEntity<Response> validatePay(@RequestBody BookService bookService) throws JsonMappingException, JsonProcessingException {
		Response response = services.validatePay(bookService);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http.requestMatchers().antMatchers("/houseCleaning/login").and().authorizeRequests()
				.antMatchers("/houseCleaning/login").access("hasRole('LOGIN')")
		
				.and().requestMatchers().antMatchers("/houseCleaning/admin").and().authorizeRequests()
				.antMatchers("/houseCleaning/admin").access("hasRole('ADMIN')");
	}
}
