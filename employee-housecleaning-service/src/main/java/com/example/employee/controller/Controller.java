package com.example.employee.controller;

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

import com.example.employee.entity.Employee;
import com.example.employee.entity.Response;
import com.example.employee.services.Services;

@RestController
@RequestMapping(path = "/employee")
public class Controller {

	@Autowired
	Services services;

	@PostMapping(path = "/save")
	public ResponseEntity<Response> save(@Valid @RequestBody Employee employee,BindingResult validResult){
		Response response = services.save(employee,validResult);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@GetMapping(path = "/findAll")
	public ResponseEntity<Response> findAll(){
		Response response = services.findAll();
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
	
	@GetMapping(path = "/findByPostalCode")
	public ResponseEntity<Response> findByPostalCode(@RequestParam(name = "code") String code){
		Response response = services.findByPostalCode(code);
		return new ResponseEntity<>(response,HttpStatus.FOUND);
	}
	
	
	@PutMapping(path = "/update/{id}")
	public ResponseEntity<Response> update(@Valid @RequestBody Employee employee, @PathVariable Long id, BindingResult validResult){
		Response response = services.update(employee, id, validResult);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@DeleteMapping(path = "/deleteById")
	public ResponseEntity<Response> deleteById(@RequestHeader(name = "id") Long id){
		Response response = services.deleteById(id);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
}
