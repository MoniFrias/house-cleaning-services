package com.example.houseCleaning.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.example.houseCleaning.entity.BookService;
import com.example.houseCleaning.entity.Customer;
import com.example.houseCleaning.entity.Employee;
import com.example.houseCleaning.entity.Payment;
import com.example.houseCleaning.entity.Response;
import com.example.houseCleaning.entity.TypeService;
import com.example.houseCleaning.entity.ValidationException;
import com.example.houseCleaning.repository.RepositoryBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ServicesTest {

	@InjectMocks
	Services services;
	@Mock
	RepositoryBook repository;
	@Mock
	WebClient webClient;
	@Mock
	RequestBodyUriSpec requestBodyUriSpec;
	@Mock
	RequestBodySpec requestBodySpec;
	@Mock
	RequestHeadersSpec requestHeaderSpec;
	@Mock
	ResponseSpec responseSpec;
	@Mock
	RequestHeadersUriSpec requestHeaderUriSpec;
	
	
	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(services, "customerSave", "");
		ReflectionTestUtils.setField(services, "employeeSave", "");
		ReflectionTestUtils.setField(services, "typeServiceSave", "");
		ReflectionTestUtils.setField(services, "employeeFindByPostalCode", "");
		ReflectionTestUtils.setField(services, "customerFindPaymentsById", "");
		ReflectionTestUtils.setField(services, "employeeUpdatePaymentAppointment", "");

	}
	
	
	@Test
	public void createAccountCustomerSaveCustomerTest() {
		Customer customer =  new Customer(1L, "name", "lastName", "email@gmail.com", "city", "NLL", 12345L, "address", 123L, 1234567896L, 0L, new ArrayList<>(), new ArrayList<>());
		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		MediaType contentType=null;
		when(requestBodySpec.contentType(contentType.APPLICATION_JSON)).thenReturn(requestBodySpec);
		when(requestBodySpec.body(Mockito.any())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", customer));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertTrue(services.createAccountCustomer(customer).isResult());
	}
	
	@Test
	public void createAccountCustomerSaveCustomerCatchTest() {
		Customer customer =  new Customer(1L, "name", "lastName", "email@gmail.com", "city", "NLL", 12345L, "address", 123L, 1234567896L, 0L, new ArrayList<>(), new ArrayList<>());
		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		MediaType contenType = null;
		when(requestBodySpec.contentType(contenType.APPLICATION_JSON)).thenReturn(requestBodySpec);
		when(requestBodySpec.body(Mockito.any())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Response.class)).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.createAccountCustomer(customer));
	}
	

	@Test
	public void createAccountEmployeeTest() {
		Employee employee = new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "city", "NLL", 12345L, 1234567898L, new ArrayList<>());
		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		MediaType contenType = null;
		when(requestBodySpec.contentType(contenType.APPLICATION_JSON)).thenReturn(requestBodySpec);
		when(requestBodySpec.body(Mockito.any())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", employee));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertTrue(services.createAccountEmployee(employee).isResult());
	}
	
	@Test
	public void createAccountEmployeeCatchTest() {
		Employee employee = new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "city", "NLL", 12345L, 1234567898L, new ArrayList<>());
		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		MediaType contenType = null;
		when(requestBodySpec.contentType(contenType.APPLICATION_JSON)).thenReturn(requestBodySpec);
		when(requestBodySpec.body(Mockito.any())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Response.class)).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.createAccountEmployee(employee));
	}
	
	@Test
	public void createTypeServiceTest() {
		TypeService typeServices = new TypeService(1L, "House", 500L, 3L);
		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		MediaType contenType = null;
		when(requestBodySpec.contentType(contenType.APPLICATION_JSON)).thenReturn(requestBodySpec);
		when(requestBodySpec.body(Mockito.any())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", typeServices));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertTrue(services.createTypeService(typeServices).isResult());
	}
	
	@Test
	public void createTypeServiceCatchTest() {
		TypeService typeServices = new TypeService(1L, "House", 500L, 3L);
		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		MediaType contenType = null;
		when(requestBodySpec.contentType(contenType.APPLICATION_JSON)).thenReturn(requestBodySpec);
		when(requestBodySpec.body(Mockito.any())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Response.class)).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.createTypeService(typeServices));
	}
	
	@Test
	public void loginTest() throws JsonProcessingException {
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Customer customer =  new Customer(1L, "name", "lastName", "email@gmail.com", "city", "NLL", 12345L, "address", 123L, 1234567896L, 0L, new ArrayList<>(), new ArrayList<>());
		Mono<Response> monoResponse = Mono.just(new Response(true, "", customer));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertTrue(services.login("email@gmail.com").isResult());
	}
	
	@Test
	public void loginCustumerFoundElseTest() throws JsonProcessingException {
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
	    Mono<Response> monoResponse = Mono.just(new Response(true, "", null));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertThrows(ValidationException.class, () -> services.login("email@gmail.com"));
	}
	
	@Test
	public void loginCustumerFoundCustomerFindByEmailCatchTest() {
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Response.class)).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.login("email@gmail.com"));
	}
	
	@Test
	public void bookServicesValidationResultElseTest() {
		BookService bookService = new BookService(1L, 2021810170461L, 123456L, 1L, 1L, "House", LocalDate.now(), LocalTime.parse("09:00"), LocalTime.parse("13:00"), 500L, "", "");
		BindingResult bindingResult = new BeanPropertyBindingResult(bookService, "");
		assertThrows(ValidationException.class, () -> services.bookService(bookService, bindingResult));
	}
	
	@Test
	public void bookServicesBindingResultElseTest() {
		BookService bookService = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "Hou", LocalDate.now(), LocalTime.parse("09:00"), LocalTime.parse("13:00"), 500L, "", "");
		BindingResult bindingResult = new BeanPropertyBindingResult(bookService, "");
		assertThrows(ValidationException.class, () -> services.bookService(bookService, bindingResult));
	}
	
	@Test
	public void bookServicesValidateDateTimeElseTest() {
		BookService bookService = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.parse("06:00"), LocalTime.parse("23:00"), 500L, "", "");
		BindingResult bindingResult = new BeanPropertyBindingResult(bookService, "");
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		TypeService typeService = new TypeService(1L, "House", 500L, 3L);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", typeService));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertThrows(ValidationException.class, () -> services.bookService(bookService, bindingResult));
	}
	
	@Test
	public void bookServicestypeServiceFindByTypeCatchTest() {
		BookService bookService = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.parse("06:00"), LocalTime.parse("23:00"), 500L, "", "");
		BindingResult bindingResult = new BeanPropertyBindingResult(bookService, "");
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Response.class)).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.bookService(bookService, bindingResult));
	}
	
	@Test
	public void bookServicesEmployeeFindByPostalCodeTest() {
		BookService bookService = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now().plusDays(1), LocalTime.parse("09:00"), LocalTime.parse("13:00"), 500L, "", "");
		BindingResult bindingResult = new BeanPropertyBindingResult(bookService, "");
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		TypeService typeService = new TypeService(1L, "House", 500L, 3L);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", typeService));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
	
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);		
		List<Employee> listEmployee = new ArrayList<>();
		listEmployee.add(new Employee(1L, "name", "lastName", 12345678L, "email.gmail.com", "city", "NLL", 12345L, 1234567891L, new ArrayList<>()));
		Mono<Response> monoResponse2 = Mono.just(new Response(true, "", listEmployee));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse2);

		
		assertThrows(ValidationException.class, () -> services.bookService(bookService, bindingResult));
	}
	
	
	@Test
	public void bookServiceslistEmployeeCATCHTest() {
		BookService bookService = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now().plusDays(1), LocalTime.parse("09:00"), LocalTime.parse("13:00"), 500L, "", "");
		BindingResult bindingResult = new BeanPropertyBindingResult(bookService, "");
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		TypeService typeService = new TypeService(1L, "House", 500L, 3L);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", typeService));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertThrows(ValidationException.class, () -> services.bookService(bookService, bindingResult));
	}
	
	@Test
	public void validatePayMatchElseTest() {
		assertThrows(ValidationException.class, () -> services.validatePay(2021810170461L, 1L));
	}
	
	@Test
	public void validatePayBookServiceFoundElseTest() {
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.validatePay(2021810170461L, 1234567890123457L));
	}
	
	@Test
	public void validatePayFindPaymentELSEfindCustomerPaymentsTest() {
		BookService bookServices = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.parse("06:00"), LocalTime.parse("23:00"), 500L, "", "");
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(bookServices);
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		List<Payment> listPayments = new ArrayList<>();
		Mono<Response> monoResponse = Mono.just(new Response(true, "", listPayments));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertThrows(ValidationException.class, () -> services.validatePay(2021810170461L, 1234567890123457L));
	}
	
	@Test
	public void validatePayFindPaymentElsefindCustomerPaymentsCATCHTest() {
		BookService bookServices = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.parse("06:00"), LocalTime.parse("23:00"), 500L, "", "");
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(bookServices);
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Response.class)).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.validatePay(2021810170461L, 1234567890123457L));
	}
	
	@Test
	public void validatePayFindPaymentfindCustomerPaymentsTest() {
		BookService bookServices = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.parse("06:00"), LocalTime.parse("23:00"), 500L, "", "");
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(bookServices);
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		List<Payment> listPayments = new ArrayList<>();
		listPayments.add(new Payment(1L, 1L, "cardType", 1234567890123457L));
		Mono<Response> monoResponse = Mono.just(new Response(true, "", listPayments));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		when(webClient.put()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(requestBodySpec);
		assertThrows(ValidationException.class, () -> services.validatePay(2021810170461L, 1234567890123457L));
	}
	
	
	
	@Test
	public void findAllEmptyTest() {
		when(repository.findAll()).thenReturn(new ArrayList<>());
		assertThrows(ValidationException.class, () -> services.findAll());
	}
	
	@Test
	public void findAllTest() {
		List<BookService> listBookServices = new ArrayList<>();
		listBookServices.add(new BookService());
		when(repository.findAll()).thenReturn(listBookServices);
		assertTrue(services.findAll().isResult());
	}
	
	@Test
	public void findByCustomerIdNullTest() {
		assertThrows(ValidationException.class, () -> services.findByCustomerId(null));
	}
	
	@Test
	public void findByCustomerIdZeroTest() {
		assertThrows(ValidationException.class, () -> services.findByCustomerId(0L));
	}
	
	@Test
	public void findByCustomerIdBookSeviceFoundEmptyTest() {
		when(repository.findBookServiceByIdCustomer(Mockito.anyLong())).thenReturn(new ArrayList<>());
		assertThrows(ValidationException.class, () -> services.findByCustomerId(1L));
	}
	
	@Test
	public void findByCustomerIdBookSeviceFoundTest() {
		List<BookService> listBookServices = new ArrayList<>();
		listBookServices.add(new BookService());
		when(repository.findBookServiceByIdCustomer(Mockito.anyLong())).thenReturn(listBookServices);
		assertTrue(services.findByCustomerId(1L).isResult());
	}
	
	@Test
	public void findCustomerInfoByIdNullTest() {
		assertThrows(ValidationException.class, () -> services.findCustomerInfoById(null));
	}
	
	@Test
	public void findCustomerInfoByIdZeroTest() {
		assertThrows(ValidationException.class, () -> services.findCustomerInfoById(0L));
	}
	

	@Test
	public void findCustomerInfoByIdTest() throws JsonMappingException, JsonProcessingException {
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Object customerIn = new Object(); 
		Mono<Response> monoResponse = Mono.just(new Response(true, "", customerIn));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertTrue(services.findCustomerInfoById(1L).isResult());
	}
	
	@Test
	public void findCustomerPaymentsByIdNullTest() {
		assertThrows(ValidationException.class, () -> services.findCustomerPaymentsById(null));
	}
	
	@Test
	public void findCustomerPaymentsByIdZeroTest() {
		assertThrows(ValidationException.class, () -> services.findCustomerPaymentsById(0L));
	}
	
	@Test
	public void findCustomerPaymentsByIdTest() throws JsonMappingException, JsonProcessingException {
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		List<Payment> listPayments = new ArrayList<>();
		listPayments.add(new Payment(1L, 1L, "cardType", 1234567890123457L));
		Mono<Response> monoResponse = Mono.just(new Response(true, "", listPayments));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		when(webClient.put()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(requestBodySpec);
		assertTrue(services.findCustomerPaymentsById(1L).isResult());
	}
	
	@Test
	public void findByBookNumberValidationElseTest() {
		assertThrows(ValidationException.class, () -> services.findByBookNumber(1L));
	}

	@Test
	public void findByBookNumberBookServiceFoundElseTest() {
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.findByBookNumber(20218161439591L));
	}
	
	@Test
	public void findByBookNumberTest() {
		BookService bookServices = new BookService();
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(bookServices);
		assertTrue(services.findByBookNumber(20218161439591L).isResult());
	}
	
	@Test
	public void findServicesPerMonthMatcherFromDateElseTest() {
		assertThrows(ValidationException.class, () -> services.findServicesPerMonth("2021/08/25", "2021-09-25"));
	}
	
	@Test
	public void findServicesPerMonthMatcherToDateElseTest() {
		assertThrows(ValidationException.class, () -> services.findServicesPerMonth("2021-08-25", "2021/09/25"));

	}
	
	@Test
	public void findServicesPerMonthListServicesPerMonthElseTest() {
		when(repository.findBookServiceByDateGreaterThanEqualAndDateLessThanEqual(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
		assertThrows(ValidationException.class, () -> services.findServicesPerMonth("2021-08-25", "2021-09-25"));

	}
	
	@Test
	public void findServicesPerMonthTest() {
		List<BookService> listBookServices = new ArrayList<>();
		listBookServices.add(new BookService());
		when(repository.findBookServiceByDateGreaterThanEqualAndDateLessThanEqual(Mockito.any(), Mockito.any())).thenReturn(listBookServices);
		assertTrue(services.findServicesPerMonth("2021-08-25", "2021-09-25").isResult());
	}
	
	
	
	
	
	//update
	
	
	@Test
	public void updateStatusServiceValidationNumberElseTest() {
		assertThrows(ValidationException.class, () -> services.updateStatusService(1L, "Done"));
	}
	
	@Test
	public void updateStatusServiceBookSeviceFoundElseTest() {
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.updateStatusService(20218161439591L, "Done"));
	}
	
	@Test
	public void updateStatusServiceBookSeviceFoundGetStatusPayElseTest() {
		BookService bookServices = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "Hou", LocalDate.now(), LocalTime.parse("09:00"), LocalTime.parse("13:00"), 500L, "Pendient", "Pendient");
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(bookServices);
		assertThrows(ValidationException.class, () -> services.updateStatusService(20218161439591L, "Done"));
	}
	
	@Test
	public void updateStatusServiceBookSeviceFoundGetStatusServiceElseTest() {
		BookService bookServices = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "Hou", LocalDate.now(), LocalTime.parse("09:00"), LocalTime.parse("13:00"), 500L, "Paid", "Done");
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(bookServices);
		assertThrows(ValidationException.class, () -> services.updateStatusService(20218161439591L, "Done"));
	}
	
	@Test
	public void updateStatusServiceTest() {
		BookService bookServices = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "Hou", LocalDate.now(), LocalTime.parse("09:00"), LocalTime.parse("13:00"), 500L, "Paid", "Pendient");
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(bookServices);
		assertThrows(ValidationException.class, () -> services.updateStatusService(20218161439591L, "Done"));
	}
	
	@Test
	public void deleteByBookNumberValidationNumberElseTest() {
		assertThrows(ValidationException.class, () -> services.deleteByBookNumber(1L));
	}
	
	@Test
	public void deleteByBookNumberBookSeviceFoundElseTest() {
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.deleteByBookNumber(20218161439591L));
	}
	
	@Test
	public void deleteByBookNumberTest() {
		BookService bookServices = new BookService(1L, 2021810170461L, 1L, 1L, 12345L, "Hou", LocalDate.now(), LocalTime.parse("09:00"), LocalTime.parse("13:00"), 500L, "Paid", "Pendient");
		when(repository.findBookServiceBybookNumber(Mockito.anyLong())).thenReturn(bookServices);
		assertThrows(ValidationException.class, () -> services.deleteByBookNumber(20218161439591L));
	}
	
	
	
	
	
	
	
	
	
	
	
}
