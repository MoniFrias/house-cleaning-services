package com.example.customer.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.customer.entity.Customer;
import com.example.customer.entity.Payment;
import com.example.customer.entity.Response;
import com.example.customer.services.Services;
import com.fasterxml.jackson.core.JsonProcessingException;


@RestController
@RequestMapping(path = "/customer")
public class Controller {

	@Autowired
	Services services;
	
	@PostMapping(path = "/save")
	public ResponseEntity<Response> save(@Valid @RequestBody Customer customer,BindingResult validResult){
		Response response = services.save(customer,validResult);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@PostMapping(path = "/savePayment")
	public ResponseEntity<Response> savePayment(@Valid @RequestBody Payment payment,BindingResult validResultPayment){
		Response response = services.savePayment(payment,validResultPayment);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}	
	
	@GetMapping(path = "/findAll")
	public ResponseEntity<Response> findAll(){
		Response response = services.findAll();
		return new ResponseEntity<>(response,HttpStatus.FOUND);
	}
	
	@GetMapping(path = "/findById")
	public ResponseEntity<Response> findById(@RequestParam(name = "id") Long id) throws JsonProcessingException{
		Response response = services.findById(id);
		return new ResponseEntity<>(response,HttpStatus.FOUND);
	}
	
	@GetMapping(path = "/findBookServiceByIdCustomer")
	public ResponseEntity<Response> findBookServiceByIdCustomer(@RequestParam(name = "id") Long id) throws JsonProcessingException{
		Response response = services.findBookServiceByIdCustomer(id);
		return new ResponseEntity<>(response,HttpStatus.FOUND);
	}
		
	@GetMapping(path = "/findByCity")
	public ResponseEntity<Response> findByCity(@RequestParam(name = "city") String city){
		Response response = services.findByCity(city);
		return new ResponseEntity<>(response,HttpStatus.FOUND);
	}
	
	@GetMapping(path = "/findByState")
	public ResponseEntity<Response> findByState(@RequestParam(name = "state") String state){
		Response response = services.findByState(state);
		return new ResponseEntity<>(response,HttpStatus.FOUND);
	}

	@GetMapping(path = "/findByState2")
	public ResponseEntity<Response> findByState2(@RequestParam(name = "state") String state){
		Response response = services.findByState2(state);
		return new ResponseEntity<>(response,HttpStatus.FOUND);
	}
	
	@GetMapping(path = "/findByPostalCode")
	public ResponseEntity<Response> findByPostalCode(@RequestParam(name = "code") Long code){
		Response response = services.findByPostalCode(code);
		return new ResponseEntity<>(response,HttpStatus.FOUND);
	}
	
	@GetMapping(path = "/findByEmail")
	public ResponseEntity<Response> findByEmail(@RequestParam(name = "email") String email){
		Response response = services.findByEmail(email);
		return new ResponseEntity<>(response,HttpStatus.FOUND);
	}
	
	@PutMapping(path = "/update/{id}")
	public ResponseEntity<Response> update(@Valid @RequestBody Customer customer, @PathVariable Long id, BindingResult validResult){
		Response response = services.update(customer, id, validResult);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@PutMapping(path = "/updateCountService")
	public ResponseEntity<Response> updateCountService(@RequestParam Long id, @RequestParam Long count){
		Response response = services.updateCountService(id,count);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@DeleteMapping(path = "/deleteById")
	public ResponseEntity<Response> deleteById(@RequestHeader(name = "id") Long id){
		Response response = services.deleteById(id);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
}
