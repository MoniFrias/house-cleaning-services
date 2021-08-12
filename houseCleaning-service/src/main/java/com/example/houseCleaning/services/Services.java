package com.example.houseCleaning.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.houseCleaning.entity.Appointment;
import com.example.houseCleaning.entity.BookService;
import com.example.houseCleaning.entity.Customer;
import com.example.houseCleaning.entity.Employee;
import com.example.houseCleaning.entity.Payment;
import com.example.houseCleaning.entity.Response;
import com.example.houseCleaning.entity.TypeService;
import com.example.houseCleaning.entity.ValidationException;
import com.example.houseCleaning.repository.RepositoryBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Services {

	@Autowired
	WebClient webClient;
	@Autowired
	RepositoryBook repository;

	@Value("${customerSave}")
	private String customerSave;
	@Value("${customerFindById}")
	private String customerFindById;	
	@Value("${customerFindInfoById}")
	private String customerFindInfoById;
	@Value("${customerFindPaymentsById}")
	private String customerFindPaymentsById;
	@Value("${customerFindByEmail}")
	private String customerFindByEmail;
	@Value("${customerUpdateCountService}")
	private String customerUpdateCountService;
	@Value("${employeeSave}")
	private String employeeSave;
	@Value("${employeeFindByPostalCode}")
	private String employeeFindByPostalCode;
	@Value("${employeeSaveAppointment}")
	private String employeeSaveAppointment;
	@Value("${employeeUpdateAppointment}")
	private String employeeUpdateAppointment;
	@Value("${employeeFindByBookNumber}")
	private String employeeFindByBookNumber;
	@Value("${typeServiceFindByType}")
	private String typeServiceFindByType;
	@Value("${typeServiceSave}")
	private String typeServiceSave;

	private Pattern patternCodeP, patternIdCustomer, patternCard, patternBookNumber;
	private Matcher matcherCodeP, matcherIdCustomer, matcherCard, matcherBookNumber;

	public Response createAccountCustomer(Customer customer) {
		Response response = new Response();
		Customer customerFound = null;
		try {
			customer.setCountService(0L);
			customerFound = saveCustomer(customer);
		} catch (JsonProcessingException e) {
			log.error("error {}", e);
			throw new ValidationException(e.getMessage());
		}
		response.setData(customerFound);
		return response;
	}

	public Response createAccountEmployee(Employee employee) {
		Response response = new Response();
		Employee employeeFound = null;
		try {
			employeeFound = saveEmployee(employee);
		} catch (JsonProcessingException e) {
			log.error("error {}", e);
			throw new ValidationException(e.getMessage());
		}
		response.setData(employeeFound);
		return response;
	}

	public Response createTypeService(TypeService typeService) {
		Response response = new Response();
		TypeService typeServiceSave = null;
		try {
			typeServiceSave = saveTypeService(typeService);
		} catch (JsonProcessingException e) {
			log.error("error {}", e);
			throw new ValidationException(e.getMessage());
		}
		response.setData(typeServiceSave);
		return response;
	}

	public Response login(String email) throws JsonProcessingException {
		Response response = new Response();
		Customer customerFound = customerFindByEmail(email);
		if (customerFound != null) {
			response.setMessage("Welcome " + customerFound.getName());
			return response;
		} else {
			throw new ValidationException("No customers with that email");
		}
	}

	public Response bookService(BookService bookService, BindingResult bindingResult)
			throws JsonMappingException, JsonProcessingException {
		Response response = new Response();
		List<Employee> listEmployee;
		final LocalDate dateBook = bookService.getDate();
		LocalTime appointmentstartTime = bookService.getStarTime();
		boolean validationResult = validateData(bookService.getIdCustomer(), bookService.getCodeP(), dateBook,
				appointmentstartTime);
		Customer customerStatus = customerFindById(bookService.getIdCustomer());
		if (validationResult && !bindingResult.hasErrors()) {
			TypeService typeServiceFound = typeServiceFindByType(bookService.getTypeService());
			LocalTime appoitmentEndTime = bookService.getStarTime().plusHours(typeServiceFound.getTimeSuggested());
			boolean validateDateTime = validateLocalDateTime(dateBook, appointmentstartTime, appoitmentEndTime);
			if (validateDateTime) {

				try {
					listEmployee = employeeFindByPostalCode(bookService.getCodeP());
				} catch (JsonProcessingException e) {
					log.error("error {}", e);
					throw new ValidationException(e.getMessage());
				}

				Optional<Employee> employeeValidation = validateAvaliable(listEmployee, dateBook, appointmentstartTime,
						appoitmentEndTime);
				List<BookService> listBookServiceByCustomer = repository.findBookServiceByIdCustomer(bookService.getIdCustomer());
				Long count = listBookServiceByCustomer.parallelStream().filter(date -> date.getDate().equals(dateBook)).count();
				if (count == 0) {					
					if (employeeValidation.isPresent()) {
						Employee employee = employeeValidation.get();

						if (customerStatus.getCountService() == 0) {
							double descount = typeServiceFound.getCost() * (0.2);
							Long costTotal = typeServiceFound.getCost() - (new Double(descount)).longValue();
							customerUpdateCountService(bookService.getIdCustomer(), 1L);
							bookService.setCost(costTotal);
							BookService bookServiceNew = setValuesBookService(bookService, employee, appoitmentEndTime);
							response.setData(repository.save(bookServiceNew));
							return response;
						} else {
							Long countNew = customerStatus.getCountService() + 1L;
							customerUpdateCountService(bookService.getIdCustomer(), countNew);
							bookService.setCost(typeServiceFound.getCost());
							BookService bookServiceNew = setValuesBookService(bookService, employee, appoitmentEndTime);
							response.setData(repository.save(bookServiceNew));
							return response;
						}
					}else {
						throw new ValidationException("No employees available");
					}
				} else {
					throw new ValidationException("Already have a book for that day, only you can update that book service");
				}
			} else {
				throw new ValidationException("Out of schedule");
			}

		} else {
			throw new ValidationException("Some data is wrong or the day and time are out of schedule");
		}
	}

	
	@SuppressWarnings("unused")
	public Response validatePay(BookService bookService) throws JsonMappingException, JsonProcessingException {
		Response response = new Response();
		patternCard = Pattern.compile("[0-9]{16}");
		matcherCard = patternCard.matcher(Long.toString(bookService.getCreditCard()));
		
		if (matcherCard.matches()) {
			BookService bookServiceFound = repository.findBookServiceBybookNumber(bookService.getBookNumber());
			if (bookServiceFound != null) {	
				List<Payment> listPaymentsFound = (List<Payment>) findCustomerPaymentsById(bookServiceFound.getIdCustomer()).getData();
				Long findPayment = listPaymentsFound.parallelStream().filter(cardnumber -> cardnumber.getCardNumber().equals(bookService.getCreditCard())).count();
				
				if (findPayment > 0) {
					bookServiceFound.setStatusPay("Paid");
					response.setData(repository.save(bookServiceFound));
					return response;
				}else {
					throw new ValidationException("Payment method doesn't saved");
				}
			} else {
				throw new ValidationException("No bookService with that bookNumber");
			}
		} else {
			throw new ValidationException("Card Number is wrong");
		}
	}

	public Response findAll() {
		Response response = new Response();
		List<BookService> listBookService = repository.findAll();
		if (!listBookService.isEmpty()) {
			response.setData(listBookService);
			return response;
		} else {
			throw new ValidationException("No bookService");
		}
	}

	public Response findByCustomerId(Long id) {
		Response response = new Response();
		List<BookService> bookSeviceFound = repository.findBookServiceByIdCustomer(id);
		if (id != null && id > 0) {
			if (!bookSeviceFound.isEmpty()) {
				response.setData(bookSeviceFound);
				return response;
			} else {
				throw new ValidationException("No bookService for that Customer");
			}
		} else {
			throw new ValidationException("Id can't be null or zero");
		}
	}
	
	public Response findCustomerInfoById(Long id) throws JsonMappingException, JsonProcessingException {
		Response response = new Response();
		if (id != null && id > 0) {
			Object infoFound = findCustomerInfo(id);
			response.setData(infoFound);
			return response;
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}
	
	public Response findCustomerPaymentsById(Long id) throws JsonMappingException, JsonProcessingException {
		Response response = new Response();
		if (id != null && id > 0) {
			List<Payment> listPaymentsFound = findCustomerPayments(id);
			response.setData(listPaymentsFound);
			return response;
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}

	public Response findByBookNumber(Long number) {
		Response response = new Response();
		BookService bookSeviceFound = repository.findBookServiceBybookNumber(number);
		boolean validationNumber = validateBookNumber(number);
		if (validationNumber) {
			if (bookSeviceFound != null) {
				response.setData(bookSeviceFound);
				return response;
			} else {
				throw new ValidationException("No bookService for that Customer");
			}
		} else {
			throw new ValidationException("Some data is wrong");
		}
	}
	
	public Response findServicesPerMonth(String fromDate, String toDate) {
		Response response = new Response();
		Pattern pattern = Pattern.compile("^(((19|2[0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01]))$"  + 
				  						  "|^(((19|2[0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30))$");
		Matcher matcherFromDate = pattern.matcher(fromDate);
		Matcher matcherToDate = pattern.matcher(toDate);
		if (matcherFromDate.matches() && matcherToDate.matches()) {
			List<BookService> listServicesPerMonth = repository.findBookServiceByDateGreaterThanEqualAndDateLessThanEqual(LocalDate.parse(fromDate),LocalDate.parse(toDate));
			if (!listServicesPerMonth.isEmpty()) {
				response.setData(listServicesPerMonth);
				return response;
			}else {
				throw new ValidationException("No bookServices between that dates");
			}
		}else {
			throw new ValidationException("Some values are wrong");
		}
		
	}

	public Response update(BookService bookService, Long id, BindingResult validResultUpdate) throws JsonProcessingException {
		Response response = new Response();
		List<Employee> listEmployee;
		boolean validation = validateData(bookService.getIdCustomer(), bookService.getCodeP(), bookService.getDate(),bookService.getStarTime());
		BookService bookSeviceFound = repository.findBookServiceById(id);
		LocalDate dateBook = bookService.getDate();
		LocalTime appointmentstartTime = bookService.getStarTime();
		if (validation && !validResultUpdate.hasErrors()) {
			List<BookService> listBookServiceByCustomer = repository.findBookServiceByIdCustomer(bookService.getIdCustomer());
			Long count = listBookServiceByCustomer.parallelStream().filter(date -> date.getDate().equals(dateBook)).count();
			if (bookSeviceFound != null && !bookSeviceFound.getStatusService().equals("done")) {
				if (count == 0) {
					TypeService typeServiceFound = typeServiceFindByType(bookService.getTypeService());
					LocalTime appoitmentEndTime = bookService.getStarTime().plusHours(typeServiceFound.getTimeSuggested());
					boolean validateDateTime = validateLocalDateTime(dateBook, appointmentstartTime, appoitmentEndTime);
					if (validateDateTime) {
						try {
							listEmployee = employeeFindByPostalCode(bookService.getCodeP());
						} catch (JsonProcessingException e) {
							log.error("error {}", e);
							throw new ValidationException(e.getMessage());
						}

						Optional<Employee> employeeValidation = validateAvaliable(listEmployee, dateBook,
								appointmentstartTime, appoitmentEndTime);

						if (employeeValidation.isPresent()) {
							Employee employee = employeeValidation.get();
							bookSeviceFound.setCost(typeServiceFound.getCost());
							bookSeviceFound = setValuesUpdateBookService(bookSeviceFound, bookService, employee,
									appoitmentEndTime);
							response.setData(repository.save(bookSeviceFound));
							return response;

						} else {
							throw new ValidationException("No employees available");
						}

					} else {
						throw new ValidationException("Out of schedule");
					}

				} else {
					throw new ValidationException("Already have a book service for that day");
				}
			}else {
				throw new ValidationException("No bookService for that ID or is already done");
			}
		
		} else {
			throw new ValidationException("Some data is wrong");
		}
	}
	
	public Response updateStatusService(Long bookService, String status) {
		Response response = new Response();
		BookService bookSeviceFound = repository.findBookServiceBybookNumber(bookService);
		boolean validationNumber = validateBookNumber(bookService);
		if (validationNumber) {
			if (bookSeviceFound != null) {
				bookSeviceFound.setStatusService(status);
				response.setData(repository.save(bookSeviceFound));
				return response;
			} else {
				throw new ValidationException("No bookService for that bookNumber");
			}
		} else {
			throw new ValidationException("Some data is wrong");
		}
	}

	public Response deleteByBookNumber(Long number) {
		Response response = new Response();
		BookService bookSeviceFound = repository.findBookServiceBybookNumber(number);
		boolean validationNumber = validateBookNumber(number);
		if (validationNumber) {
			if (bookSeviceFound != null) {
				repository.deleteById(bookSeviceFound.getId());
				return response;
			} else {
				throw new ValidationException("No bookService for that bookNumber");
			}
		} else {
			throw new ValidationException("Some data is wrong");
		}
	}

	private BookService setValuesBookService(BookService bookService, Employee employee, LocalTime appoitmentEndTime) {
		int month = LocalDate.now().getMonthValue();
		int year = LocalDate.now().getYear();
		int day = LocalDate.now().getDayOfMonth();
		int hour = LocalTime.now().getHour();
		int minute = LocalTime.now().getMinute();
		int second = LocalTime.now().getSecond();
		Long number = Long.valueOf(String.valueOf(year) + String.valueOf(month) + String.valueOf(day)
				+ String.valueOf(hour) + String.valueOf(minute) + String.valueOf(second)
				+ String.valueOf(bookService.getIdCustomer()));
		bookService.setIdEmployee(employee.getId());
		bookService.setStatusPay("In process");
		bookService.setEndTime(appoitmentEndTime);
		bookService.setBookNumber(number);
		Appointment appoitmentValues = new Appointment();
		appoitmentValues = setValuesAppointment(appoitmentValues, bookService);
		appoitmentValues.setEndTime(appoitmentEndTime);
		appoitmentValues.setBookNumber(number);
		employeeSaveAppointment(appoitmentValues);
		return bookService;
	}

	private BookService setValuesUpdateBookService(BookService bookServiceFound, BookService bookService,
			Employee employee, LocalTime appoitmentEndTime) throws JsonMappingException, JsonProcessingException {
		bookServiceFound.setIdCustomer(bookService.getIdCustomer());
		bookServiceFound.setIdEmployee(employee.getId());
		bookServiceFound.setCodeP(bookService.getCodeP());
		bookServiceFound.setTypeService(bookService.getTypeService());
		bookServiceFound.setDate(bookService.getDate());
		bookServiceFound.setStarTime(bookService.getStarTime());
		bookServiceFound.setEndTime(appoitmentEndTime);

		Appointment appointmentFound = appointmentFindByBookNumber(bookServiceFound.getBookNumber());		
		appointmentFound = setValuesAppointment(appointmentFound, bookServiceFound);
		appointmentFound.setEndTime(appoitmentEndTime);
		employeeUpdateAppointment(appointmentFound);
		return bookServiceFound;
	}

	private Appointment setValuesAppointment(Appointment appointment, BookService bookService) {
		appointment.setIdCustomer(bookService.getIdCustomer());
		appointment.setIdEmployee(bookService.getIdEmployee());
		appointment.setPostalCode(bookService.getCodeP());
		appointment.setTypeService(bookService.getTypeService());
		appointment.setDate(bookService.getDate());
		appointment.setStarTime(bookService.getStarTime());
		return appointment;
	}

	private boolean validateData(Long idCustomer, Long codeP, LocalDate date, LocalTime time) {
		patternIdCustomer = Pattern.compile("[0-9]{1,5}");
		matcherIdCustomer = patternIdCustomer.matcher(Long.toString(idCustomer));
		patternCodeP = Pattern.compile("[0-9]{5}");
		matcherCodeP = patternCodeP.matcher(Long.toString(codeP));
		if (matcherCodeP.matches() && matcherIdCustomer.matches()) {
			return true;
		} else {
			return false;
		}
	}

	private Optional<Employee> validateAvaliable(List<Employee> listEmployee, LocalDate dateBook,
			LocalTime appointmentTime, LocalTime appoitmentEndTime) {
		Optional<Employee> employeeA = listEmployee.stream().map(employee -> {
			long count = 0;
			if (employee.getAppointments() != null) {
				count = employee.getAppointments().parallelStream()
						.filter(appointment -> dateBook.isEqual(appointment.getDate())
								&& !appointmentTime.isBefore(appointment.getStarTime())
								&& !appointmentTime.isAfter(appointment.getEndTime().plusHours(1L))// give an hour to
																									// move
								|| dateBook.isEqual(appointment.getDate())
										&& !appoitmentEndTime.isBefore(appointment.getStarTime())
										&& !appoitmentEndTime.isAfter(appointment.getEndTime().plusHours(1L)))
						.count();
			}
			return (count > 0) ? null : employee;
		}).filter(Objects::nonNull).findFirst();

		return employeeA;
	}

	private boolean validateLocalDateTime(LocalDate date, LocalTime startTime, LocalTime endTime) {
		LocalTime isbeforeTime = LocalTime.parse("07:00");
		LocalTime isAfterTime = LocalTime.parse("19:59");
		if (date.isAfter(LocalDate.now()) && !startTime.isBefore(isbeforeTime) && !startTime.isAfter(isAfterTime)
				&& !endTime.isBefore(isbeforeTime) && !endTime.isAfter(isAfterTime)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean validateBookNumber(Long number) {
		patternBookNumber = Pattern.compile("[0-9]{12,14}");
		matcherBookNumber = patternBookNumber.matcher(Long.toString(number));
		if (matcherBookNumber.matches()) {
			return true;
		} else {
			return false;
		}
	}

	private Customer saveCustomer(Customer customer) throws JsonMappingException, JsonProcessingException {
		MediaType contentType = null;
		Response objectResponse = null;
		try {
			objectResponse = webClient.post().uri(customerSave).contentType(contentType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(customer)).retrieve().bodyToMono(Response.class).block();
		} catch (Exception e) {
			throw new ValidationException("Some data is wrong or already there a customer with that Email");
		}

		Object objectCustomer = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectCustomer);
		Customer responseCustomer = objectMapper.readValue(stringResponse, Customer.class);
		return responseCustomer;
	}

	private Employee saveEmployee(Employee employee) throws JsonMappingException, JsonProcessingException {
		MediaType contentType = null;
		Response objectResponse = null;
		try {
			objectResponse = webClient.post().uri(employeeSave).contentType(contentType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(employee)).retrieve().bodyToMono(Response.class).block();
		} catch (Exception e) {
			throw new ValidationException("Some data is wrong or already there a employee with that Email");
		}

		Object objectEmployee = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectEmployee);
		Employee responseEmployee = objectMapper.readValue(stringResponse, Employee.class);
		return responseEmployee;
	}

	private TypeService saveTypeService(TypeService typeService) throws JsonMappingException, JsonProcessingException {
		MediaType contentType = null;
		Response objectResponse = null;
		try {
			objectResponse = webClient.post().uri(typeServiceSave).contentType(contentType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(typeService)).retrieve().bodyToMono(Response.class).block();
		} catch (Exception e) {
			throw new ValidationException("Some data is wrong or already there a Type service with that name");
		}
		Object objectTypeService = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectTypeService);
		TypeService responseTypeService = objectMapper.readValue(stringResponse, TypeService.class);
		return responseTypeService;
	}

	private Customer customerFindByEmail(String email) throws JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(customerFindByEmail + email).retrieve().bodyToMono(Response.class)
					.block();
		} catch (Exception e) {
			throw new ValidationException("There isn't a customer with that Email");
		}
		Object objectCustomer = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectCustomer);
		Customer responseCustomer = objectMapper.readValue(stringResponse, Customer.class);
		return responseCustomer;
	}

	private Customer customerFindById(Long id) throws JsonMappingException, JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(customerFindById + id).retrieve().bodyToMono(Response.class).block();
		} catch (Exception e) {
			throw new ValidationException("There aren't customer with that ID");
		}
		Object objectCustomer = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectCustomer);
		Customer responseCustomer = objectMapper.readValue(stringResponse, Customer.class);
		return responseCustomer;
	}
	
	private Object findCustomerInfo(Long id) throws JsonMappingException, JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(customerFindInfoById + id).retrieve().bodyToMono(Response.class)
					.block();
		} catch (Exception e) {
			throw new ValidationException("There isn't info for that customer ID");
		}
		Object objectCustomer = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectCustomer);
		Object responseCustomer = objectMapper.readValue(stringResponse, Object.class);
		return responseCustomer;
	}
	
	private List<Payment> findCustomerPayments(Long id) throws JsonMappingException, JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient
					.get()
					.uri(customerFindPaymentsById + id)
					.retrieve()
					.bodyToMono(Response.class)
					.block();
		} catch (Exception e) {
			throw new ValidationException("There isn't payments for that customer");
		}
		Object objectCustomer = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		String stringResponse = objectMapper.writeValueAsString(objectCustomer);
		List<Payment> responseCustomer = objectMapper.readValue(stringResponse, new TypeReference<List<Payment>>() {});
		return responseCustomer;
	}

	private TypeService typeServiceFindByType(String type) throws JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(typeServiceFindByType + type).retrieve().bodyToMono(Response.class)
					.block();
		} catch (Exception e) {
			throw new ValidationException("There aren't type services with that name");
		}
		Object objectType = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectType);
		TypeService responseType = objectMapper.readValue(stringResponse, TypeService.class);
		return responseType;

	}

	private List<Employee> employeeFindByPostalCode(Long codeP) throws JsonMappingException, JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(employeeFindByPostalCode + codeP).retrieve().bodyToMono(Response.class)
					.block();
		} catch (Exception e) {
			throw new ValidationException("There aren't employees in that Postal code");
		}

		Object objectEmployee = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		String stringResponse = objectMapper.writeValueAsString(objectEmployee);
		List<Employee> responseEmployee = objectMapper.readValue(stringResponse, new TypeReference<List<Employee>>() {});
		return responseEmployee;
	}

	private void customerUpdateCountService(Long id, Long count) {
		webClient.put()
				.uri(customerUpdateCountService, uri -> uri.queryParam("id", id).queryParam("count", count).build())
				.retrieve().bodyToMono(Response.class).block();
	}

	private void employeeSaveAppointment(Appointment appointment) {
		MediaType contentType = null;
		try {
			webClient.post().uri(employeeSaveAppointment).contentType(contentType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(appointment)).retrieve().bodyToMono(Response.class).block();
		} catch (Exception e) {
			throw new ValidationException("Something is wrong");
		}
	}

	private void employeeUpdateAppointment(Appointment appointment) {
		MediaType contentType = null;
		try {
			webClient.put().uri(employeeUpdateAppointment).contentType(contentType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(appointment)).retrieve().bodyToMono(Response.class).block();
		} catch (Exception e) {
			throw new ValidationException("Something is wrong");
		}
	}

	private Appointment appointmentFindByBookNumber(Long bookNumber)
			throws JsonMappingException, JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(employeeFindByBookNumber + bookNumber).retrieve()
					.bodyToMono(Response.class).block();
		} catch (Exception e) {
			throw new ValidationException("There aren't appointment with that book number");
		}

		Object objectAppointment = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		String stringResponse = objectMapper.writeValueAsString(objectAppointment);
		Appointment responseAppointment = objectMapper.readValue(stringResponse, Appointment.class);
		return responseAppointment;
	}
}
