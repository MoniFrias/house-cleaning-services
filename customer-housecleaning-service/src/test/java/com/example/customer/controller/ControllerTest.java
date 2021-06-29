package com.example.customer.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import com.example.customer.entity.Customer;
import com.example.customer.services.Services;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

	@InjectMocks
	Controller controller;
	@Mock
	Services services;
	
	Customer customer = new Customer();
	BindingResult validResult = null;
	
	@Test
	public void saveTest() {
		assertEquals(HttpStatus.OK, controller.save(customer, validResult).getStatusCode());
	}

	@Test
	public void findAllTest() {
		assertEquals(HttpStatus.FOUND, controller.findAll().getStatusCode());
	}
	
	@Test
	public void findByCityTest() {
		assertEquals(HttpStatus.FOUND, controller.findByCity("GDL").getStatusCode());
	}
	
	@Test
	public void findByEmailTest() {
		assertEquals(HttpStatus.FOUND, controller.findByEmail("monifrias").getStatusCode());
	}
	
	@Test
	public void updateTest() {
		assertEquals(HttpStatus.OK, controller.update(customer, 1L, validResult).getStatusCode());
	}
	
	@Test
	public void deleteByIdTest() {
		assertEquals(HttpStatus.OK, controller.deleteById(1L).getStatusCode());
	}
	
}