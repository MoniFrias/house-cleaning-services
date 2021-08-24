package com.example.typeService.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.example.typeService.entity.TypeService;
import com.example.typeService.services.Services;

@ExtendWith(MockitoExtension.class)
class ControllerTest {
	
	@InjectMocks
	Controller controller;
	@Mock
	Services services;

	@Test
	void saveTest() {
		TypeService typeService = new TypeService();
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		assertEquals(HttpStatus.OK, controller.save(typeService, validResult).getStatusCode());
	}
	
	@Test
	void findAllTest() {
		assertEquals(HttpStatus.FOUND, controller.findAll().getStatusCode());
	}
	
	@Test
	void findByIdTest() {
		assertEquals(HttpStatus.FOUND, controller.findById(1L).getStatusCode());
	}
	
	@Test
	void findByTypeTest() {
		assertEquals(HttpStatus.FOUND, controller.findByType("").getStatusCode());
	}
	
	@Test
	void updateTest() {
		TypeService typeService = new TypeService();
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		assertEquals(HttpStatus.OK, controller.update(typeService, validResult, 1L).getStatusCode());
	}
	
	@Test
	void deleteByIdTest() {
		assertEquals(HttpStatus.OK, controller.deleteById(1L).getStatusCode());
	}

}
