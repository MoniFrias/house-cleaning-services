package com.example.customer.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.example.customer.entity.BookService;
import com.example.customer.entity.Customer;
import com.example.customer.entity.Payment;
import com.example.customer.entity.Response;
import com.example.customer.entity.ValidationException;
import com.example.customer.repository.RepositoryCustomers;
import com.example.customer.repository.RepositoryPayment;
import com.fasterxml.jackson.core.JsonProcessingException;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ServicesTest {
	
	@InjectMocks
	Services services;
	@Mock
	RepositoryCustomers repositoryCustomer;
	@Mock
	RepositoryPayment repositoryPayment;
	@Mock
	WebClient webClient;
	@Mock
	RequestHeadersUriSpec requestHeaderUriSpec;
	@Mock
	RequestHeadersSpec requestHeaderSpec;
	@Mock 
	ResponseSpec responseSpec;

	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(services, "bookServiceFindByIdCustomer", "");
	}

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
	
	@Test
	void findByIdNullTest() {
		assertThrows(ValidationException.class, () -> services.findById(null));
	}
	
	@Test
	void findByIdZeroTest() {
		assertThrows(ValidationException.class, () -> services.findById(0L));
	}
	
	@Test
	void findByIdCustomerFoundElseTest() {
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.findById(1L));
	}
	
	@Test
	void findByIdTest() throws JsonProcessingException {
		Customer customer = new Customer();
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(customer);
		assertTrue(services.findById(1L).isResult());
	}
	
	@Test
	void findInfoByIdNullTest() {
		assertThrows(ValidationException.class, () -> services.findInfoById(null));
	}
	
	@Test
	void findInfoByIdZeroTest() {
		assertThrows(ValidationException.class, () -> services.findInfoById(0L));
	}
	
	@Test
	void findInfoByIdInfoCustomerElseTest() {
		when(repositoryCustomer.findInfoByIdCustomer(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.findInfoById(1L));
	}
	
	@Test
	void findInfoByIdTest() {
		Object customer = new Customer();
		when(repositoryCustomer.findInfoByIdCustomer(Mockito.anyLong())).thenReturn(customer);
		assertEquals("success", services.findInfoById(1L).getMessage());
	}
	
	@Test
	void findPaymentsByIdNullTest() {
		assertThrows(ValidationException.class, () -> services.findPaymentsById(null));
	}
	
	@Test
	void findPaymentsByIdZeroTest() {
		assertThrows(ValidationException.class, () -> services.findPaymentsById(0L));
	}
	
	@Test
	void findPaymentsByIdPaymentElseTest() {
		List<Payment> listpayment = new ArrayList<>();
		when(repositoryPayment.findPaymentByIdCustomer(Mockito.anyLong())).thenReturn(listpayment);
		assertThrows(ValidationException.class, () -> services.findPaymentsById(1L));
	}
	
	@Test
	void findPaymentsByIdTest() {
		List<Payment> listpayment = new ArrayList<>();
		listpayment.add(new Payment());
		when(repositoryPayment.findPaymentByIdCustomer(Mockito.anyLong())).thenReturn(listpayment);
		assertEquals("success", services.findPaymentsById(1L).getMessage());
	}
	
	
	

	@Test
	void findBookServiceByIdCustomerNullTest() {
		assertThrows(ValidationException.class, () -> services.findBookServiceByIdCustomer(null));
	}
	
	@Test
	void findBookServiceByIdCustomerZeroTest() {
		assertThrows(ValidationException.class, () -> services.findBookServiceByIdCustomer(0L));
	}
	
	@Test
	void findBookServiceByIdCustomerElseTest() {
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.findBookServiceByIdCustomer(1L));
	}

	@Test
	void findBookServiceByIdCustomerlistBookServiceFoundCATCHTest() throws JsonProcessingException {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		List<Payment> listPayment = new ArrayList<>();
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(customer);
		when(repositoryPayment.findPaymentByIdCustomer(Mockito.anyLong())).thenReturn(listPayment);
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString(),Mockito.anyLong())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Mono<Response> monoResponse=Mono.just(new Response(true, "msg", true));//value wrong
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertThrows(ValidationException.class, () -> services.findBookServiceByIdCustomer(1L));
	}
	
	
	@Test
	void findBookServiceByIdCustomerTest() throws JsonProcessingException {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		List<Payment> listPayment = new ArrayList<>();
		listPayment.add(new Payment(1L, 1L, "cardType", 1234567890123457L));
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(customer);
		when(repositoryPayment.findPaymentByIdCustomer(Mockito.anyLong())).thenReturn(listPayment);
		
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		List<BookService> listBookService = new ArrayList<>();
		listBookService.add(new BookService(1L, 20218161439591L, 1L, 1L, 12345L, "House", LocalDate.now().plusDays(1), LocalTime.now(), LocalTime.now().plusHours(3), 500L, "Pendient", 1234567890123457L));
		Mono<Response> monoResponse=Mono.just(new Response(true, "msg", listBookService));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertTrue(services.findBookServiceByIdCustomer(1L).isResult());
	}
	
	@Test
	void findByCityMatchElseTest() {
		assertThrows(ValidationException.class, () -> services.findByCity("Mon"));
	}
	
	@Test
	void findByCityListCustomerElseTest() {
		List<Customer> listCustomer = new ArrayList<>();
		when(repositoryCustomer.findCustomerByCity(Mockito.anyString())).thenReturn(listCustomer);
		assertThrows(ValidationException.class, () -> services.findByCity("Monterrey"));
	}
	
	@Test
	void findByCityTest() {
		List<Customer> listCustomer = new ArrayList<>();
		listCustomer.add(new Customer());
		when(repositoryCustomer.findCustomerByCity(Mockito.anyString())).thenReturn(listCustomer);
		assertTrue(services.findByCity("Monterrey").isResult());
	}
	
	@Test
	void findByStateMatchElseTest() {
		assertThrows(ValidationException.class, () -> services.findByState("Monterrey"));
	}
	
	@Test
	void findByStateListCustomerElseTest() {
		List<Customer> listCustomer = new ArrayList<>();
		when(repositoryCustomer.findCustomerByState(Mockito.anyString())).thenReturn(listCustomer);
		assertThrows(ValidationException.class, () -> services.findByState("NLL"));
	}
	
	@Test
	void findByStateTest() {
		List<Customer> listCustomer = new ArrayList<>();
		listCustomer.add(new Customer());
		when(repositoryCustomer.findCustomerByState(Mockito.anyString())).thenReturn(listCustomer);
		assertTrue(services.findByState("NLL").isResult());
	}
	
	@Test
	void findByState2MatchElseTest() {
		assertThrows(ValidationException.class, () -> services.findByState2("Monterrey"));
	}
	
	@Test
	void findByState2ListCustomerElseTest() {
		List<Object> listCustomer = new ArrayList<>();
		when(repositoryCustomer.findCustomerByStates(Mockito.anyString())).thenReturn(listCustomer);
		assertThrows(ValidationException.class, () -> services.findByState2("NLL"));
	}
	
	@Test
	void findByState2Test() {
		List<Object> listCustomer = new ArrayList<>();
		listCustomer.add(new Customer());
		when(repositoryCustomer.findCustomerByStates(Mockito.anyString())).thenReturn(listCustomer);
		assertTrue(services.findByState2("NLL").isResult());
	}
	
	@Test
	void findByPostalCodeMatchElseTest() {
		assertThrows(ValidationException.class, () -> services.findByPostalCode(1L));
	}
	
	@Test
	void findByPostalCodeListCustomerElseTest() {
		List<Customer> listCustomer = new ArrayList<>();
		when(repositoryCustomer.findCustomerByPostalCode(Mockito.anyLong())).thenReturn(listCustomer);
		assertThrows(ValidationException.class, () -> services.findByPostalCode(12345L));
	}
	
	@Test
	void findByPostalCodeTest() {
		List<Customer> listCustomer = new ArrayList<>();
		listCustomer.add(new Customer());
		when(repositoryCustomer.findCustomerByPostalCode(Mockito.anyLong())).thenReturn(listCustomer);
		assertTrue(services.findByPostalCode(12345L).isResult());
	}
	
	@Test
	void findByEmailContainsElseTest() {
		assertThrows(ValidationException.class, () -> services.findByEmail("1234@gmail"));
	}
	
	@Test
	void findByEmailCustomerElseTest() {
		when(repositoryCustomer.findCustomerByEmail(Mockito.anyString())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.findByEmail("1234@gmail.com"));
	}
	
	@Test
	void findByEmailTest() {
		Customer Customer = new Customer();
		when(repositoryCustomer.findCustomerByEmail(Mockito.anyString())).thenReturn(Customer);
		assertTrue(services.findByEmail("1234@gmail.com").isResult());
	}

	
	@Test
	void updateIDNullTest() {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		assertThrows(ValidationException.class, () -> services.update(customer, validResult, null));
	}
	
	@Test
	void updateIDZeroTest() {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		assertThrows(ValidationException.class, () -> services.update(customer, validResult, 0L));
	}
	
	@Test
	void updateMatchElseTest() {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12L , "address9", 577777L, 1234L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		assertThrows(ValidationException.class, () -> services.update(customer, validResult, 1L));
	}

	@Test
	void updateValidResultElseTest() {
		Customer customer = new Customer(1L, "Nam", "ame", "emailgma.com", "Mon", "NLLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		assertThrows(ValidationException.class, () -> services.update(customer, validResult, 1L));
	}
	
	@Test
	void updateCustomerFoundByEmailElseTest() {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		when(repositoryCustomer.findCustomerByEmail(Mockito.anyString())).thenReturn(customer);
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(customer);
		assertTrue(services.update(customer, validResult, 1L).isResult());
	}
	
	@Test
	void updateCustomerFoundByEmailElseCustomerFoundElseTest() {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		when(repositoryCustomer.findCustomerByEmail(Mockito.anyString())).thenReturn(customer);
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.update(customer, validResult, 1L));
	}
	
	@Test
	void updateCustomerFoundByEmailElseCustomerFoundByEmailElseTest() {
		Customer customer = new Customer(2L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		when(repositoryCustomer.findCustomerByEmail(Mockito.anyString())).thenReturn(customer);
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.update(customer, validResult, 1L));
	}
	
	@Test
	void updateCustomerFoundElseTest() {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		when(repositoryCustomer.findCustomerByEmail(Mockito.anyString())).thenReturn(customer);
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.update(customer, validResult, 2L));
	}
	
	@Test
	void updateTest() {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(customer, "");
		when(repositoryCustomer.findCustomerByEmail(Mockito.anyString())).thenReturn(null);
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(customer);
		assertTrue(services.update(customer, validResult, 1L).isResult());
	}
	
	@Test
	void updateCountServiceNULLTest() {
		assertThrows(ValidationException.class, () -> services.updateCountService(null, null));
	}
	
	@Test
	void updateCountServiceZeroTest() {
		assertThrows(ValidationException.class, () -> services.updateCountService(0L, 0L));
	}
	
	@Test
	void updateCountServiceCustomerFoundElseTest() {
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.updateCountService(1L, 1L));
	}
	
	@Test
	void updateCountServiceTest() {
		Customer customer = new Customer();
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(customer);
	    assertTrue(services.updateCountService(1L, 1L).isResult());
	}
	
	@Test 
	void deleteByIdNullTest() {
		assertThrows(ValidationException.class, () -> services.deleteById(null));
	}
	
	@Test 
	void deleteByIdZeroTest() {
		assertThrows(ValidationException.class, () -> services.deleteById(0L));
	}
	
	@Test 
	void deleteByIdCustomerFoundElseTest() {
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.deleteById(1L));
	}
	
	@Test 
	void deleteByIdCustomerFoundTest() {
		Customer customer = new Customer(1L, "firstName", "lastName", "email@gma.com", "Monterrey", "NLL", 12345L , "address9", 555L, 1234567894L, 0L, new ArrayList<>(), new ArrayList<>());
		when(repositoryCustomer.findCustomerById(Mockito.anyLong())).thenReturn(customer);
		List<Payment> listPayment = new ArrayList<>();
		listPayment.add(new Payment(1L, 1L, "cardType", 1234567890123457L));
		when(repositoryPayment.findPaymentByIdCustomer(Mockito.anyLong())).thenReturn(listPayment);
		assertTrue(services.deleteById(1L).isResult());
	}
	
}
