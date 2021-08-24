package com.example.employee.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.employee.entity.ValidationException;

@ExtendWith(MockitoExtension.class)
class ControllerAdviceEmployeeTest {

	@InjectMocks
	ControllerAdviceEmployee controlAdvice;
	
	
	@Test
	void test() {
		ValidationException validation = new ValidationException("");
		assertEquals(HttpStatus.BAD_REQUEST, controlAdvice.validationEmployee(validation).getStatusCode());
	}

}
