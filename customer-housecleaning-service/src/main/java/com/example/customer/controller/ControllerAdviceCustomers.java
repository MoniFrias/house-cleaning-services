package com.example.customer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.customer.entity.Response;
import com.example.customer.entity.ValidationException;

@RestControllerAdvice
public class ControllerAdviceCustomers {

	@ExceptionHandler(value = ValidationException.class)
	public ResponseEntity<Response> validationCustomer(final ValidationException validation){
		Response response = new Response(false, validation.getMessage(), null);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}

}
