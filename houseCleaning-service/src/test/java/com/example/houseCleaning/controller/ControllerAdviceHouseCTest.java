package com.example.houseCleaning.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.houseCleaning.entity.ValidationException;

@ExtendWith(MockitoExtension.class)
class ControllerAdviceHouseCTest {

	@InjectMocks
	ControllerAdviceHouseC controllerAdvice;
	
	@Test
	void test() {
		ValidationException validation = new ValidationException("");
		assertEquals(HttpStatus.BAD_REQUEST, controllerAdvice.validationHouseC(validation).getStatusCode());
	}

}
