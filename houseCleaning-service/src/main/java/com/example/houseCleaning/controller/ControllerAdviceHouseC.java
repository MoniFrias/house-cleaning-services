package com.example.houseCleaning.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.houseCleaning.entity.Response;
import com.example.houseCleaning.entity.ValidationException;

@RestControllerAdvice
public class ControllerAdviceHouseC {

	@ExceptionHandler(value = ValidationException.class)
	public ResponseEntity<Response> validationHouseC(final ValidationException validation) {
		Response response = new Response(false, validation.getMessage(), null);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
