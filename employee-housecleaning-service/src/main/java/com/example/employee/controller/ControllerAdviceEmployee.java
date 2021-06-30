package com.example.employee.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.employee.entity.Response;
import com.example.employee.entity.ValidationException;

@RestControllerAdvice
public class ControllerAdviceEmployee {

	@ExceptionHandler(value = ValidationException.class)
	public ResponseEntity<Response> validationEmployee(final ValidationException validation){
		Response response = new Response(false, validation.getMessage(), null);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}

}
