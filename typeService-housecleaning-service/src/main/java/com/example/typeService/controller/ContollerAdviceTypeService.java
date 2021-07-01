package com.example.typeService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.typeService.entity.Response;
import com.example.typeService.entity.ValidationException;

@ControllerAdvice
public class ContollerAdviceTypeService {

	@ExceptionHandler(value = ValidationException.class)
	public ResponseEntity<Response> validationTypeService(final ValidationException validation){
		Response response = new Response(false, validation.getMessage(), null);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
