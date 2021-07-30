package com.example.typeService.controller;

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

import com.example.typeService.entity.Response;
import com.example.typeService.entity.TypeService;
import com.example.typeService.services.Services;

@RestController
@RequestMapping(path = "/typeService")
public class Controller {

	@Autowired
	Services services;
	
	@PostMapping(path = "/save")
	public ResponseEntity<Response> save(@Valid @RequestBody TypeService typeService, BindingResult validResult){
		Response response = services.save(typeService,validResult);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(path = "/findAll")
	public ResponseEntity<Response> findAll(){
		Response response = services.findAll();
		return new ResponseEntity<>(response, HttpStatus.FOUND);
	}
	
	@GetMapping(path = "/findById")
	public ResponseEntity<Response> findById(@RequestParam(name = "id") Long id){
		Response response = services.findById(id);
		return new ResponseEntity<>(response, HttpStatus.FOUND);
	}
	
	@GetMapping(path = "/findByType")
	public ResponseEntity<Response> findByType(@RequestParam(name = "type") String type){
		Response response = services.findByType(type);
		return new ResponseEntity<>(response, HttpStatus.FOUND);
	}
	
	
	@PutMapping(path = "/update/{id}")
	public ResponseEntity<Response> update(@Valid @RequestBody TypeService typeService , BindingResult validResult, @PathVariable Long id){
		Response response = services.update(typeService, validResult, id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping(path = "/deleteById")
	public ResponseEntity<Response> deleteById(@RequestHeader(name = "id") Long id){
		Response response = services.deleteById(id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
