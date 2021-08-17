package com.example.customer.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.example.customer.entity.Customer;
import com.example.customer.entity.Payment;
import com.example.customer.entity.ValidationException;
import com.example.customer.repository.RepositoryCustomers;
import com.example.customer.repository.RepositoryPayment;

@ExtendWith(MockitoExtension.class)
class ServicesTest {
	
	@InjectMocks
	Services services;
	@Mock
	RepositoryCustomers repositoryCustomer;
	@Mock
	RepositoryPayment repositoryPayment;


	@Test
	void saveTest() {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		assertTrue(services.save(customer, validResult).isResult());
	}
	
	@Test
	void saveElseMatchTest() {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12L , "address9", 577777L, 1234L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		assertThrows(ValidationException.class, () -> services.save(customer, validResult));
	}
	
	@Test
	void saveCustomerNotFoundTest() {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		when(repositoryCustomer.findCustomerByEmail(Mockito.anyString())).thenReturn(customer);
		assertThrows(ValidationException.class, () -> services.save(customer, validResult));
	}
	
	@Test
	void savePaymentElseTest() {
		Payment payment = new Payment(1L, null, "cardType", 1234567890123457L);
		BindingResult validResultPayment = new BeanPropertyBindingResult(payment, "");
		assertThrows(ValidationException.class, () -> services.savePayment(payment, validResultPayment));
	}
	
	@Test
	void savePaymentElseIDZeroTest() {
		Payment payment = new Payment(1L, 0L, "cardType", 1234567890123457L);
		BindingResult validResultPayment = new BeanPropertyBindingResult(payment, "");
		assertThrows(ValidationException.class, () -> services.savePayment(payment, validResultPayment));
	}

	@Test
	void savePaymentMatchElseTest() {
		Payment payment = new Payment(1L, 1L, "cardType", 123454L);
		BindingResult validResultPayment = new BeanPropertyBindingResult(payment, "");
		assertThrows(ValidationException.class, () -> services.savePayment(payment, validResultPayment));
	}
	
	@Test
	void savePaymentCustomerFoundElseTest() {
		Payment payment = new Payment(1L, 1L, "cardType", 1234567890123457L);
		BindingResult validResultPayment = new BeanPropertyBindingResult(payment, "");
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.savePayment(payment, validResultPayment));
	}
	
	@Test
	void savePaymentFoundElseTest() {
		Payment payment = new Payment(1L, 1L, "cardType", 1234567890123457L);
		BindingResult validResultPayment = new BeanPropertyBindingResult(payment, "");
		Customer customer = new Customer();
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(customer);
		when(repositoryPayment.findPaymentByIdCustomerAndCardNumber(Mockito.anyLong(), Mockito.anyLong())).thenReturn(payment);
		assertThrows(ValidationException.class, () -> services.savePayment(payment, validResultPayment));
	}
	
	@Test
	void savePaymentTest() {
		Payment payment = new Payment(1L, 1L, "cardType", 1234567890123457L);
		BindingResult validResultPayment = new BeanPropertyBindingResult(payment, "");
		Customer customer = new Customer();
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(customer);
		when(repositoryPayment.findPaymentByIdCustomerAndCardNumber(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
		assertTrue(services.savePayment(payment, validResultPayment).isResult());
	}
	
	@Test
	void findAllTest() {
		List<Customer> listCustomer = new ArrayList<>();
		listCustomer.add( new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>()));

		when(repositoryCustomer.findAll()).thenReturn(listCustomer );
		assertTrue(services.findAll().isResult());
	}	
	
	@Test
	void findAllElseTest() {
		List<Payment> listPayment = new ArrayList<>();
		when(repositoryPayment.findAll()).thenReturn(listPayment);
		assertThrows(ValidationException.class, () -> services.findAll());
	}
	
	

}
