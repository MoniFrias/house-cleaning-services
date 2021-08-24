package com.example.employee.services;

import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.example.employee.entity.Appointment;
import com.example.employee.entity.Employee;
import com.example.employee.entity.Response;
import com.example.employee.entity.TypeService;
import com.example.employee.entity.ValidationException;
import com.example.employee.repository.RepositoryAppointment;
import com.example.employee.repository.RepositoryEmployee;
import com.fasterxml.jackson.core.JsonProcessingException;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ServicesTest {
	
	@InjectMocks
	Services services;
	@Mock
	RepositoryEmployee repositoryEmployee;
	@Mock
	RepositoryAppointment repositoryAppointment;
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
		ReflectionTestUtils.setField(services, "typeServiceFindByType", "");
	}

	@Test
	void saveValidationElseTest() {
		Employee employee = new Employee(1L, "name", "lastName", 1L, "email@gmail", "Monterrey", "NLL", 1234599L, 123456789699L, new ArrayList<>());
		BindingResult validResult= new BeanPropertyBindingResult(employee, "");
		assertThrows(ValidationException.class, () -> services.save(employee, validResult));
	}
	
	@Test
	void saveValidResultElseTest() {
		Employee employee = new Employee(1L, "na", "las", 12345678L, "emailgmail.com", "Mon9", "NLLL", 1234599L, 123456789699L, new ArrayList<>());
		BindingResult validResult= new BeanPropertyBindingResult(employee, "");
		assertThrows(ValidationException.class, () -> services.save(employee, validResult));
	}
	
	@Test
	void saveEmployeeFoundElseTest() {
		Employee employee = new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, new ArrayList<>());
		BindingResult validResult= new BeanPropertyBindingResult(employee, "");
		when(repositoryEmployee.findEmployeeByEmail(Mockito.anyString())).thenReturn(employee);
		assertThrows(ValidationException.class, () -> services.save(employee, validResult));
	}
	
	@Test
	void saveTest() {
		Employee employee = new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, new ArrayList<>());
		BindingResult validResult= new BeanPropertyBindingResult(employee, "");
		when(repositoryEmployee.findEmployeeByEmail(Mockito.anyString())).thenReturn(null);
		assertTrue(services.save(employee, validResult).isResult());
	}
	
	@Test
	void saveAppointmentGetIDNullTest() throws JsonProcessingException {
		Appointment appointment = new Appointment(1L, null, 1L, 1L, 12345L, "House", LocalDate.now().plusDays(1), LocalTime.now(), LocalTime.now().plusHours(3), "Paid", "Done");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		assertTrue(services.saveAppointment(appointment, validResultApp).isResult());
	}
	
	@Test
	void saveAppointmentGetBookServicesNullTest() throws JsonProcessingException {
		Appointment appointment = new Appointment(null, 20218161439591L, 1L, 1L, 12345L, "House", LocalDate.now().plusDays(1), LocalTime.now(), LocalTime.now().plusHours(3), "Paid", "Done");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		assertTrue(services.saveAppointment(appointment, validResultApp).isResult());
	}
	
	@Test
	void saveAppointmentValidationAppointmentElseTest() {
		Appointment appointment = new Appointment(null, null, 1L, 123456L, 1234599L, "House", LocalDate.now().plusDays(1), LocalTime.now(), LocalTime.now().plusHours(3), "Paid", "Done");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		assertThrows(ValidationException.class, () -> services.saveAppointment(appointment, validResultApp));
	}
	
	@Test
	void saveAppointmentValidResultAppElseTest() {
		Appointment appointment = new Appointment(null, null, 1L, 1L, 12345L, "Ho", LocalDate.now().plusDays(1), LocalTime.now(), LocalTime.now().plusHours(3), "Paid", "Done");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		assertThrows(ValidationException.class, () -> services.saveAppointment(appointment, validResultApp));
	}
	
	@Test
	void saveAppointmentlistEmployeeElseTest() {
		Appointment appointment = new Appointment(null, null, 1L, 1L, 12345L, "House", LocalDate.now().plusDays(1), LocalTime.now(), LocalTime.now().plusHours(3), "", "");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		List<Employee> listEmployee = new ArrayList<>();
		when(repositoryEmployee.findEmployeeByPostalCode(Mockito.anyLong())).thenReturn(listEmployee);
		assertThrows(ValidationException.class, () -> services.saveAppointment(appointment, validResultApp));
	}
	
	@Test
	void saveAppointmentValidateDateTimeElseTest() {
		Appointment appointment = new Appointment(null, null, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.parse("06:00"), LocalTime.parse("23:00"), "", "");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		List<Employee> listEmployee = new ArrayList<>();
		listEmployee.add(new Employee());
		when(repositoryEmployee.findEmployeeByPostalCode(Mockito.anyLong())).thenReturn(listEmployee);
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		TypeService typeService = new TypeService(1L, "House", 500L, 5L);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", typeService));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertThrows(ValidationException.class, () -> services.saveAppointment(appointment, validResultApp));
	}
	
	@Test
	void saveAppointmentTypeServiceFindByTypeCathTest() {
		Appointment appointment = new Appointment(null, null, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.parse("06:00"), LocalTime.parse("23:00"), "", "");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		List<Employee> listEmployee = new ArrayList<>();
		listEmployee.add(new Employee());
		when(repositoryEmployee.findEmployeeByPostalCode(Mockito.anyLong())).thenReturn(listEmployee);
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString(),Mockito.anyString())).thenReturn(requestHeaderSpec); //send URI wrong
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", new TypeService()));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertThrows(ValidationException.class, () -> services.saveAppointment(appointment, validResultApp));
	}

	@Test
	void saveAppointmentEmployeeFirstElseTest() {
		Appointment appointment = new Appointment(null, null, 1L, 1L, 12345L, "House", LocalDate.now().plusDays(1), LocalTime.parse("08:00"), LocalTime.parse("13:00"), "", "");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		List<Employee> listEmployee = new ArrayList<>();
		List<Appointment> listAppointment = new ArrayList<>();
		listAppointment.add(new Appointment(null, null, 1L, 1L, 12345L, "House", LocalDate.now().plusDays(1), LocalTime.parse("08:00"), LocalTime.parse("13:00"), "", ""));
		listEmployee.add(new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, listAppointment ));
		when(repositoryEmployee.findEmployeeByPostalCode(Mockito.anyLong())).thenReturn(listEmployee);
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		TypeService typeService = new TypeService(1L, "House", 500L, 5L);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", typeService));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertThrows(ValidationException.class, () -> services.saveAppointment(appointment, validResultApp));
	}
	
	@Test
	void saveAppointmentTest() throws JsonProcessingException {
		Appointment appointment = new Appointment(null, null, 1L, 1L, 12345L, "House", LocalDate.now().plusDays(1), LocalTime.parse("08:00"), LocalTime.parse("13:00"), "", "");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		List<Employee> listEmployee = new ArrayList<>();
		listEmployee.add(new Employee());
		when(repositoryEmployee.findEmployeeByPostalCode(Mockito.anyLong())).thenReturn(listEmployee);
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		TypeService typeService = new TypeService(1L, "House", 500L, 5L);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", typeService));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertTrue(services.saveAppointment(appointment, validResultApp).isResult());
	}
	
	@Test
	void findAllListEmployeeEmptyTest() {
		List<Employee> listEmployee = new ArrayList<>();
		when(repositoryEmployee.findAll()).thenReturn(listEmployee);
		assertThrows(ValidationException.class, () -> services.findAll());
	}
	
	@Test
	void findAllAddAppointmentTest() {
		List<Employee> listEmployee = new ArrayList<>();
		listEmployee.add(new Employee(1L, "name", "lastName", 1L, "email@gmail", "Monterrey", "NLL", 1234599L, 123456789699L, new ArrayList<>()));
		List<Appointment> listApp = new ArrayList<>();
		listApp.add(new Appointment());
		when(repositoryEmployee.findAll()).thenReturn(listEmployee);		
		when(repositoryAppointment.findAppointmentByIdEmployee(Mockito.anyLong())).thenReturn(listApp);
		assertTrue(services.findAll().isResult());
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
	void findByIdEmployeeElseTest() {
		when(repositoryEmployee.findEmployeeById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.findById(1L));
	}
	
	@Test
	void findByIdTest() {
		Employee employee = new Employee();
		when(repositoryEmployee.findEmployeeById(Mockito.anyLong())).thenReturn(employee);
		assertTrue(services.findById(1L).isResult());
	}
	
	@Test
	void findByCityMatchElseTest() {
		assertThrows(ValidationException.class, () -> services.findByCity("Mon"));
	}
	
	@Test
	void findByCitylistEmployeeElseTest() {
		List<Employee> listEmployee = new ArrayList<>();
		when(repositoryEmployee.findEmployeeByCity(Mockito.anyString())).thenReturn(listEmployee);
		assertThrows(ValidationException.class, () -> services.findByCity("Monterrey"));
	}
	
	@Test
	void findByCityTest() {
		List<Employee> listEmployee = new ArrayList<>();
		listEmployee.add(new Employee());
		when(repositoryEmployee.findEmployeeByCity(Mockito.anyString())).thenReturn(listEmployee);
		assertTrue(services.findByCity("Monterrey").isResult());
	}

	@Test
	void findByStateMatchElseTest() {
		assertThrows(ValidationException.class, () -> services.findByState("NLLL"));
	}
	
	@Test
	void findByStateListEmployeeElseTest() {
		List<Employee> listEmployee = new ArrayList<>();
		when(repositoryEmployee.findEmployeeByState(Mockito.anyString())).thenReturn(listEmployee);
		assertThrows(ValidationException.class, () -> services.findByState("NLL"));
	}
	
	@Test
	void findByStateTest() {
		List<Employee> listEmployee = new ArrayList<>();
		listEmployee.add(new Employee(1L, "name", "lastName", 1L, "email@gmail", "Monterrey", "NLL", 1234599L, 123456789699L, new ArrayList<>()));
		List<Appointment> liAppointments = new ArrayList<>();
		when(repositoryEmployee.findEmployeeByState(Mockito.anyString())).thenReturn(listEmployee);
		when(repositoryAppointment.findAppointmentByIdEmployee(Mockito.anyLong())).thenReturn(liAppointments);
		assertTrue(services.findByState("NLL").isResult());
	}
	
	@Test
	void findByEmailContainsElseTest() {
		assertThrows(ValidationException.class, () -> services.findByEmail("email"));
	}
	
	@Test
	void findByEmailEmployeeFoundElseTest() {
		when(repositoryEmployee.findEmployeeByEmail(Mockito.anyString())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.findByEmail("email@.com"));
	}
	
	@Test
	void findByEmailTest() {
		Employee employee = new Employee();
		when(repositoryEmployee.findEmployeeByEmail(Mockito.anyString())).thenReturn(employee);
		assertTrue(services.findByEmail("email@.com").isResult());
	}
	
	@Test
	void findByPostalCodeMatchElseTest() {
		assertThrows(ValidationException.class, () -> services.findByPostalCode(123456L));
	}
	
	@Test
	void findByPostalCodeListEmployeeEmptyTest() {
		List<Employee> listEmployee = new ArrayList<>();
		when(repositoryEmployee.findEmployeeByPostalCode(Mockito.anyLong())).thenReturn(listEmployee);
		assertThrows(ValidationException.class, () -> services.findByPostalCode(12345L));
	}
	
	@Test
	void findByPostalCodeTest() {
		List<Employee> listEmployee = new ArrayList<>();
		listEmployee.add(new Employee());
		when(repositoryEmployee.findEmployeeByPostalCode(Mockito.anyLong())).thenReturn(listEmployee);
		assertTrue(services.findByPostalCode(12345L).isResult());
	}
	
	@Test
	void findByBookNumberMatcherElseTest() {
		assertThrows(ValidationException.class, () -> services.findByBookNumber(12345L));
	}
	
	@Test
	void findByBookNumberAppointmentFoundElseTest() {
		when(repositoryAppointment.findAppointmentByBookNumber(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.findByBookNumber(123451234567L));
	}
	
	@Test
	void findByBookNumberTest() {
		Appointment appointment = new Appointment();
		when(repositoryAppointment.findAppointmentByBookNumber(Mockito.anyLong())).thenReturn(appointment);
		assertTrue(services.findByBookNumber(123451234567L).isResult());
	}
	
	@Test
	void updateIDNullTest() {
		Employee employee = new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(employee, "");
		assertThrows(ValidationException.class, () -> services.update(employee, null, validResult));
	}
	
	@Test
	void updateIDZeroTest() {
		Employee employee = new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(employee, "");
		assertThrows(ValidationException.class, () -> services.update(employee, 0L, validResult));
	}
	
	@Test
	void updateValidationResultElseTest() {
		Employee employee = new Employee(1L, "name", "lastName", 1L, "email@gmail.com", "Monterrey", "NLL", 1L, 1L, new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(employee, "");
		assertThrows(ValidationException.class, () -> services.update(employee, 1L, validResult));
	}
	
	@Test
	void updateValidResultElseTest() {
		Employee employee = new Employee(1L, "na", "la", 12345678L, "emailgmail.com", "Mo", "NLLL", 12345L, 1234567891L, new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(employee, "");
		assertThrows(ValidationException.class, () -> services.update(employee, 1L, validResult));
	}
	
	@Test
	void updateEmployeeFoundByEmailElseTest() {
		Employee employee = new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(employee, "");
		when(repositoryEmployee.findEmployeeByEmail(Mockito.anyString())).thenReturn(employee);		
		assertThrows(ValidationException.class, () -> services.update(employee, 1L, validResult));
	}
	
	@Test
	void updateEmployeeFoundByEmailElseEmployeeFoundTest() {
		Employee employee = new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(employee, "");
		when(repositoryEmployee.findEmployeeByEmail(Mockito.anyString())).thenReturn(employee);		
		when(repositoryEmployee.findEmployeeById(Mockito.anyLong())).thenReturn(employee);
		assertTrue(services.update(employee, 1L, validResult).isResult());
	}
	

	
	@Test
	void updateEmployeeFoundElseTest() {
		Employee employee = new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(employee, "");
		when(repositoryEmployee.findEmployeeByEmail(Mockito.anyString())).thenReturn(null);
		when(repositoryEmployee.findEmployeeById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.update(employee, 1L, validResult));
	}
	
	@Test
	void updateTest() {
		Employee employee = new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, new ArrayList<>());
		BindingResult validResult = new BeanPropertyBindingResult(employee, "");
		when(repositoryEmployee.findEmployeeByEmail(Mockito.anyString())).thenReturn(null);
		when(repositoryEmployee.findEmployeeById(Mockito.anyLong())).thenReturn(employee);
		assertTrue(services.update(employee, 1L, validResult).isResult());
	}
	
	@Test 
	void updateAppointmentValidationAppointmentElseTest() {
		Appointment appointment = new Appointment(1L, 2021810170461L, 1L, 123456L, 1L, "House", LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(3), "Paid", "");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		assertThrows(ValidationException.class, () -> services.updateAppointment(appointment, validResultApp));
	}
	
	@Test 
	void updateAppointmentValidResultAppTest() {
		Appointment appointment = new Appointment(1L, 2021810170461L, 1L, 1L, 12345L, "Ho", LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(3), "Paid", "");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		assertThrows(ValidationException.class, () -> services.updateAppointment(appointment, validResultApp));
	}
	
	@Test 
	void updateAppointmentListEmployeeElseTest() {
		Appointment appointment = new Appointment(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(3), "Paid", "");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		List<Employee> listEmployee = new ArrayList<>();
		when(repositoryEmployee.findEmployeeByPostalCode(Mockito.anyLong())).thenReturn(listEmployee );
		assertThrows(ValidationException.class, () -> services.updateAppointment(appointment, validResultApp));
	}
	
	@Test 
	void updateAppointmentAppointmentFoundElseTest() {
		Appointment appointment = new Appointment(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(3), "Paid", "");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		when(repositoryAppointment.findAppointmentById(Mockito.anyLong())).thenReturn(null );
		assertThrows(ValidationException.class, () -> services.updateAppointment(appointment, validResultApp));
	}
	
	@Test 
	void updateAppointmentAppointmentFoundGetStatusElseTest() {
		Appointment appointment = new Appointment(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(3), "Paid", "done");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		when(repositoryAppointment.findAppointmentById(Mockito.anyLong())).thenReturn(appointment);
		assertThrows(ValidationException.class, () -> services.updateAppointment(appointment, validResultApp));
	}
	
	@Test 
	void updateAppointmentValidateDateTimeElseTest() {
		Appointment appointment = new Appointment(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.parse("04:00"), LocalTime.parse("23:30"), "", "pendient");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		List<Employee> listEmployee = new ArrayList<>();
		listEmployee.add(new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, new ArrayList<>()));
		when(repositoryEmployee.findEmployeeByPostalCode(Mockito.anyLong())).thenReturn(listEmployee);
		when(repositoryAppointment.findAppointmentById(Mockito.anyLong())).thenReturn(appointment);
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", new TypeService(1L, "House", 500L, 3L)));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse );
		assertThrows(ValidationException.class, () -> services.updateAppointment(appointment, validResultApp));
	}
	
	@Test ///parte de throw
	void updateAppointmentEmployeeFirstElseTest() throws JsonProcessingException {
		Appointment appointment = new Appointment(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now().plusDays(1), LocalTime.parse("09:00"), LocalTime.parse("13:30"), "", "pendient");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		List<Employee> listEmployee = new ArrayList<>();
		listEmployee.add(new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, new ArrayList<>()));
		when(repositoryEmployee.findEmployeeByPostalCode(Mockito.anyLong())).thenReturn(listEmployee);
		when(repositoryAppointment.findAppointmentById(Mockito.anyLong())).thenReturn(appointment);
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", new TypeService(1L, "House", 500L, 3L)));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse );
		assertThrows(ValidationException.class, () -> services.updateAppointment(appointment, validResultApp));
	}
	
	@Test 
	void updateAppointmentTest() throws JsonProcessingException {
		Appointment appointment = new Appointment(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now().plusDays(1), LocalTime.parse("09:00"), LocalTime.parse("13:30"), "", "pendient");
		BindingResult validResultApp = new BeanPropertyBindingResult(appointment, "");
		List<Employee> listEmployee = new ArrayList<>();
		listEmployee.add(new Employee(1L, "name", "lastName", 12345678L, "email@gmail.com", "Monterrey", "NLL", 12345L, 1234567896L, new ArrayList<>()));
		when(repositoryEmployee.findEmployeeByPostalCode(Mockito.anyLong())).thenReturn(listEmployee);
		when(repositoryAppointment.findAppointmentById(Mockito.anyLong())).thenReturn(appointment);
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri(Mockito.anyString())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", new TypeService(1L, "House", 500L, 3L)));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse );
		assertTrue(services.updateAppointment(appointment, validResultApp).isResult());
	}
	
	@Test
	void updateStatusPaymentElseGetStatusTest() {
		when(repositoryAppointment.findAppointmentByBookNumber(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.updateStatusPayment(123455L, "Pendient"));
	}
	
	@Test
	void updateStatusPaymentElseAppointmentFoundTest() {
		assertThrows(ValidationException.class, () ->services.updateStatusPayment(122333L, "Paid"));
	}
	
	@Test
	void updateStatusPaymentTest() {
		Appointment appointment = new Appointment();
		when(repositoryAppointment.findAppointmentByBookNumber(Mockito.anyLong())).thenReturn(appointment);
		assertTrue(services.updateStatusPayment(123455L, "Paid").isResult());
	}
	
	@Test
	void updateStatusAppointmentElseAppointmentFoundTest() {
		when(repositoryAppointment.findAppointmentByBookNumber(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () ->services.updateStatusAppointment(122333L, "Pendient"));
	}
	
	@Test
	void updateStatusAppointmentElseGetStatusTest() {
		Appointment appointment = new Appointment(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(3), "Pendient", "");
		when(repositoryAppointment.findAppointmentByBookNumber(Mockito.anyLong())).thenReturn(appointment);
		assertThrows(ValidationException.class, () -> services.updateStatusAppointment(123455L, "Done"));
	}
	
	@Test
	void updateStatusAppointmentTest() {
		Appointment appointment = new Appointment(1L, 2021810170461L, 1L, 1L, 12345L, "House", LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(3), "Paid", "");
		when(repositoryAppointment.findAppointmentByBookNumber(Mockito.anyLong())).thenReturn(appointment);
		assertTrue(services.updateStatusAppointment(111L, "Done").isResult());
	}
	
	@Test
	void deleteByIdNullTest() {
		assertThrows(ValidationException.class, () -> services.deleteById(null));
	}
	
	@Test
	void deleteByIZeroTest() {
		assertThrows(ValidationException.class, () -> services.deleteById(0L));
	}	
	
	@Test
	void deleteByIEmployeeFoundNullTest() {
		when(repositoryEmployee.findEmployeeById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.deleteById(1L));
	}
	
	@Test
	void deleteByITest() {
		Employee employee = new Employee();
		when(repositoryEmployee.findEmployeeById(Mockito.anyLong())).thenReturn(employee);
		assertTrue(services.deleteById(1L).isResult());
	}
}










