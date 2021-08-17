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
import com.example.customer.entity.Payment;
import com.example.customer.services.Services;
import com.fasterxml.jackson.core.JsonProcessingException;

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
	public void savePaymentTest() {
		Payment payment = null;
		assertEquals(HttpStatus.OK, controller.savePayment(payment, validResult).getStatusCode());
	}

	@Test
	public void findAllTest() {
		assertEquals(HttpStatus.FOUND, controller.findAll().getStatusCode());
	}
	
	@Test
	public void findByIdTest() throws JsonProcessingException {
		assertEquals(HttpStatus.FOUND, controller.findById(1L).getStatusCode());
	}
	
	@Test
	public void findInfoByIdTest() {
		assertEquals(HttpStatus.FOUND, controller.findInfoById(1L).getStatusCode());
	}
	
	@Test
	public void findPaymentsByIdTest() {
		assertEquals(HttpStatus.FOUND, controller.findPaymentsById(1L).getStatusCode());
	}

	@Test
	public void findBookServiceByIdCustomerTest() throws JsonProcessingException {
		assertEquals(HttpStatus.FOUND, controller.findBookServiceByIdCustomer(1L).getStatusCode());
	}
	
	@Test
	public void findByCityTest() {
		assertEquals(HttpStatus.FOUND, controller.findByCity("GDL").getStatusCode());
	}
	
	@Test
	public void findByStateTest() {
		assertEquals(HttpStatus.FOUND, controller.findByState("JALISCO").getStatusCode());
	}
	
	@Test
	public void findByState2Test() {
		assertEquals(HttpStatus.FOUND, controller.findByState2("JALISCO").getStatusCode());
	}
	
	@Test
	public void findByPostalCodeTest() {
		assertEquals(HttpStatus.FOUND, controller.findByPostalCode(1L).getStatusCode());
	}
	
	@Test
	public void findByEmailTest() {
		assertEquals(HttpStatus.FOUND, controller.findByEmail("monifrias").getStatusCode());
	}
	
	@Test
	public void updateTest() {
		assertEquals(HttpStatus.OK, controller.update(customer, validResult, 1L).getStatusCode());
	}
	
	@Test
	public void updateCountServiceTest() {
		assertEquals(HttpStatus.OK, controller.updateCountService(1L, 1L).getStatusCode());
	}
	
	@Test
	public void deleteByIdTest() {
		assertEquals(HttpStatus.OK, controller.deleteById(1L).getStatusCode());
	}
	
}