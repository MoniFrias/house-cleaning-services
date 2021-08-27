package com.example.houseCleaning.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.validation.Valid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.example.houseCleaning.entity.BookService;
import com.example.houseCleaning.entity.Customer;
import com.example.houseCleaning.entity.Employee;
import com.example.houseCleaning.entity.TypeService;
import com.example.houseCleaning.oauth.WebSecurityConfiguration;
import com.example.houseCleaning.services.Services;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@WebMvcTest(WebSecurityConfiguration.class)
class ControllerTest {

	@InjectMocks
	Controller controller;
	@Mock
	Services services;
	
	@Test
	void AdminTest() {
		assertEquals("Pagina Administrador", controller.admin());
	}
	
	@Test
	void createAccountCustomerTest() {
		assertEquals(HttpStatus.OK, controller.createAccountCustomer(new Customer()).getStatusCode());
	}
	
	@Test
	void createAccountEmployeeTest() {
		assertEquals(HttpStatus.OK, controller.createAccountEmployee(new Employee()).getStatusCode());
	}
	
	@Test
	void createTypeServiceTest() {
		assertEquals(HttpStatus.OK, controller.createTypeService(new TypeService()).getStatusCode());
	}
	
	@Test
	void loginTest() throws JsonProcessingException {
		assertEquals(HttpStatus.OK, controller.login("").getStatusCode());
	}
	
	@Test
	void bookServiceTest() throws JsonMappingException, JsonProcessingException {
		BookService bookService =  new BookService();		
		BindingResult bindingResult = new BeanPropertyBindingResult(bookService, "");
		assertEquals(HttpStatus.OK, controller.bookService(bookService, bindingResult).getStatusCode());
	}

	@Test
	void validatePayTest() throws JsonMappingException, JsonProcessingException {
		assertEquals(HttpStatus.OK, controller.validatePay(1234L, 12222L).getStatusCode());
	}
	
	@Test
	void findAllTest() {
		assertEquals(HttpStatus.OK, controller.findAll().getStatusCode());
	}
	
	@Test
	void findByCustomerIdTest() {
		assertEquals(HttpStatus.OK, controller.findByCustomerId(1L).getStatusCode());
	}
	
	@Test
	void findCustomerInfoByIdTest() throws JsonMappingException, JsonProcessingException {
		assertEquals(HttpStatus.OK, controller.findCustomerInfoById(1L).getStatusCode());
	}
	
	@Test
	void findCustomerPaymentsByIdTest() throws JsonMappingException, JsonProcessingException {
		assertEquals(HttpStatus.OK, controller.findCustomerPaymentsById(1L).getStatusCode());
	}
	
	@Test
	void findByBookNumberTest() {
		assertEquals(HttpStatus.OK, controller.findByBookNumber(1223L).getStatusCode());
	}
	
	@Test
	void findServicesPerMonthTest() {
		assertEquals(HttpStatus.OK, controller.findServicesPerMonth("","").getStatusCode());
	}
	
	@Test
	void updateTest() throws JsonProcessingException {
		BookService bookService = new BookService();
		BindingResult validResultUpdate = new BeanPropertyBindingResult(bookService, "");
		assertEquals(HttpStatus.OK, controller.update(bookService,1L,validResultUpdate).getStatusCode());
	}
	
	@Test
	void updateStatusServiceTest() throws JsonMappingException, JsonProcessingException {
		assertEquals(HttpStatus.OK, controller.updateStatusService(111L,"").getStatusCode());
	}
	
	@Test
	void deleteByBookNumberTest() {
		assertEquals(HttpStatus.OK, controller.deleteByBookNumber(111L).getStatusCode());
	}
	
	@Autowired
    private MockMvc mvc;

	
    @Test
    public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
        mvc.perform(get("/houseCleaning/login").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }
	
	
}
