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

@EnableResourceServer
@RestController
@RequestMapping(path = "houseCleaning")
public class Controller extends ResourceServerConfigurerAdapter{

	@Autowired
	Services services;

	@PostMapping(path = "/privada")
	public String privada() {
		return "Pagina Privada";
	}

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
	public ResponseEntity<Response> login(@RequestParam(name = "user") String user,
			@RequestParam(name = "pass") String pass) {
		Response response = services.login(user, pass);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(path = "/bookService")
	public ResponseEntity<Response> bookService(@RequestBody BookService bookService) {
		Response response = services.bookService(bookService);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// http.authorizeRequests().antMatchers("/oauth/token", "/oauth/authorize**",
		// "/houseCleaning/login").permitAll();

		http.requestMatchers().antMatchers("/houseCleaning/privada").and().authorizeRequests()
				.antMatchers("/houseCleaning/privada").access("hasRole('USER')")

				.and().requestMatchers().antMatchers("/houseCleaning/admin").and().authorizeRequests()
				.antMatchers("/houseCleaning/admin").access("hasRole('ADMIN')");
	}
}
