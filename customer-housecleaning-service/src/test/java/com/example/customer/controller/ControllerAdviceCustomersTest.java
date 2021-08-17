package com.example.customer.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.customer.entity.ValidationException;

@ExtendWith(MockitoExtension.class)
class ControllerAdviceCustomersTest {
	
	@InjectMocks
	ControllerAdviceCustomers controllerAdvice;

	@Test
	void test() {
		ValidationException exception = new ValidationException("");
		assertEquals(HttpStatus.BAD_REQUEST, controllerAdvice.validationCustomer(exception).getStatusCode());
		}

}
