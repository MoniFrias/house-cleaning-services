package com.example.services.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.services.entity.Response;
import com.example.services.entity.ValidationException;

@RestControllerAdvice
public class ControllerAdviceServices {

	@ExceptionHandler(value = ValidationException.class)
	public ResponseEntity<Response> validationServices(final ValidationException validation){
		Response response =  new Response(false, validation.getMessage(), null);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}

}
