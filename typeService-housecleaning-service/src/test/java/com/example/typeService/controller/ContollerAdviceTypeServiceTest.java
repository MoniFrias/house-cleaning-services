package com.example.typeService.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.typeService.entity.ValidationException;


@ExtendWith(MockitoExtension.class)
class ContollerAdviceTypeServiceTest {

	@InjectMocks
	ContollerAdviceTypeService controllerAdvice;
	
	@Test
	void test() {
		ValidationException validation = new ValidationException("");
		assertEquals(HttpStatus.BAD_REQUEST, controllerAdvice.validationTypeService(validation).getStatusCode());
	}

}
