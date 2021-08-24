package com.example.employee.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import com.example.employee.entity.Appointment;
import com.example.employee.entity.Employee;
import com.example.employee.services.Services;
import com.fasterxml.jackson.core.JsonProcessingException;

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
		assertEquals(HttpStatus.OK, controller.save(employee, validResult).getStatusCode());
	}
	
	@Test
	void saveAppointmentTest() throws JsonProcessingException {
		Appointment appointment = new Appointment();
		BindingResult validResult = new BeanPropertyBindingResult(appointment, "");		
		assertEquals(HttpStatus.OK, controller.saveAppointment(appointment, validResult).getStatusCode());
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
	void findByCityTest() {		
		assertEquals(HttpStatus.FOUND, controller.findByCity("Monterrey").getStatusCode());
	}
	
	@Test
	void findByStateTest(){		
		assertEquals(HttpStatus.FOUND, controller.findByState("NLL").getStatusCode());
	}
	
	@Test
	void findByEmailTest(){		
		assertEquals(HttpStatus.FOUND, controller.findByEmail("123@gmail.com").getStatusCode());
	}
	
	@Test
	void findByPostalCodeTest(){		
		assertEquals(HttpStatus.FOUND, controller.findByPostalCode(12345L).getStatusCode());
	}
	
	@Test
	void findByBookNumberTest(){		
		assertEquals(HttpStatus.FOUND, controller.findByBookNumber(20218161439591L).getStatusCode());
	}
	
	@Test
	void updateTest() {
		Employee employee = new Employee();
		BindingResult validResult = new BeanPropertyBindingResult(employee, "");		
		assertEquals(HttpStatus.OK, controller.update(employee, 1L, validResult).getStatusCode());
	}
	
	@Test
	void updateAppointmentTest() throws JsonProcessingException {
		Appointment appointment = new Appointment();
		BindingResult validResult = new BeanPropertyBindingResult(appointment, "");		
		assertEquals(HttpStatus.OK, controller.updateAppointment(appointment,  validResult).getStatusCode());
	}
	
	@Test
	void updateStatusPaymentTest() {
		assertEquals(HttpStatus.OK, controller.updateStatusPayment(1L, "Paid").getStatusCode());
	}
	
	@Test
	void updateStatusAppointmentTest() {
		assertEquals(HttpStatus.OK, controller.updateStatusAppointment(1L, "Done").getStatusCode());
	}
	
	@Test
	void deleteByIdTest() {
		assertEquals(HttpStatus.OK, controller.deleteById(1L).getStatusCode());
	}
}
