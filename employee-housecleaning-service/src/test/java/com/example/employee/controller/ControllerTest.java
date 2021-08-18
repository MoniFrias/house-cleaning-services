package com.example.employee.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.example.employee.entity.Employee;
import com.example.employee.services.Services;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

	@InjectMocks
	Controller controller;
	@Mock
	Services services;
	
	@Test
	void saveTest() {
		Employee employee = new Employee();
		BindingResult validResult = new BeanPropertyBindingResult(employee, "");		
		assertEquals(HttpStatus.OK, controller.save(employee, validResult));
	}

}
