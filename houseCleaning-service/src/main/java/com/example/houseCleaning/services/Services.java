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
			log.error("error {}",e);
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
			log.error("error {}",e);
			throw new ValidationException(e.getMessage());
		}
		response.setData(employeeFound);
		return response;
	}
	
	public Response createTypeService(TypeService typeService) {
		Response response = new Response();
		TypeService typeServiceSave  = null;
		try {
			typeServiceSave = saveTypeService(typeService);
		} catch (JsonProcessingException e) {
			log.error("error {}",e);
			throw new ValidationException(e.getMessage());
		}
		response.setData(typeServiceSave);
		return response;
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
	
	public Response login(String email) throws JsonProcessingException {
		Response response = new Response();	
		Customer customerFound = customerFindByEmail(email);
		if(customerFound != null) {
			response.setMessage("Welcome "+ customerFound.getName());
			return response;
		}else {
			throw new ValidationException("There aren't customers with that email");
		}		
	}
		
	public Response bookService(BookService bookService, BindingResult bindingResult) throws JsonMappingException, JsonProcessingException {
		Response response = new Response();
		List<Employee> listEmployee;
		final LocalDate dateBook = bookService.getDate();
		LocalTime appointmentstartTime = bookService.getStarTime();
		boolean validationResult = validateData(bookService.getIdCustomer(), bookService.getCodeP(), dateBook, appointmentstartTime);
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

				Optional<Employee> employeeValidation = validateAvaliable(listEmployee, dateBook, appointmentstartTime,appoitmentEndTime);

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
				} else {
					throw new ValidationException("No employees available");
				}
			}else {
				throw new ValidationException("Out of schedule");
			}
			
		}else {
			throw new ValidationException("Some data is wrong or the day and time are out of schedule");
		}
	}
		
	public Response validatePay(BookService bookService) throws JsonMappingException, JsonProcessingException {
		Response response = new Response();
		patternCard = Pattern.compile("[0-9]{16}");
		matcherCard = patternCard.matcher(Long.toString(bookService.getCreditCard()));
		if (matcherCard.matches()) {
			BookService bookServiceFound = repository.findBookServiceBybookNumber(bookService.getBookNumber());
			if (bookServiceFound != null) {
				bookServiceFound.setStatusPay("Paid");
				response.setData(repository.save(bookServiceFound));
				return response;
			}else {
				throw new ValidationException("There aren't bookService with that bookNumber");
			}
		}else {
			throw new ValidationException("Number introduce is wrong");
		}
	}
	
	public Response findAll() {
		Response response = new Response();
		List<BookService> listBookService = repository.findAll();
		if (!listBookService.isEmpty()) {
			response.setData(listBookService);
			return response;
		}else {
			throw new ValidationException("There aren't bookService");
		}
	}

	public Response findByCustomerId(Long id) {
		Response response = new Response();
		BookService bookSeviceFound  =repository.findBookServiceByIdCustomer(id);
		if (id != null && id > 0) {
			if (bookSeviceFound != null) {
				response.setData(bookSeviceFound);
				return response;
			}else {
				throw new ValidationException("There aren't bookService for that Customer");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}

	public Response findByBookNumber(Long number) {
		Response response = new Response();
		BookService bookSeviceFound  =repository.findBookServiceBybookNumber(number);
		boolean validationNumber = validateBookNumber(number);
		if (validationNumber) {
			if (bookSeviceFound != null) {
				response.setData(bookSeviceFound);
				return response;
			}else {
				throw new ValidationException("There aren't bookService for that Customer");
			}
		}else {
			throw new ValidationException("Some data is wrong");
		}
	}
	
	public Response update(BookService bookService, Long number, BindingResult validResultUpdate) throws JsonProcessingException {
		Response response = new Response();
		List<Employee> listEmployee;
		boolean validation = validateData(bookService.getIdCustomer(), bookService.getCodeP(), bookService.getDate(), bookService.getStarTime());
		BookService bookSeviceFound = repository.findBookServiceBybookNumber(number);
		final LocalDate dateBook = bookService.getDate();
		LocalTime appointmentstartTime = bookService.getStarTime();
		if (validation && !validResultUpdate.hasErrors()) {
			if (bookSeviceFound != null) {
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

					if (employeeValidation.isPresent()) {
						Employee employee = employeeValidation.get();
						Customer customerStatus = customerFindById(bookService.getIdCustomer());

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
					} else {
						throw new ValidationException("No employees available");
					}

				} else {
					throw new ValidationException("Out of schedule");
				}

			} else {
				throw new ValidationException("There aren't bookService for that bookNumber");
			}
		} else {
			throw new ValidationException("Some data is wrong");
		}

	}

	public Response deleteByBookNumber(Long number) {
		Response response = new Response();
		BookService bookSeviceFound  =repository.findBookServiceBybookNumber(number);
		boolean validationNumber = validateBookNumber(number);
		if (validationNumber) {
			if (bookSeviceFound != null) {
				repository.deleteById(bookSeviceFound.getId());
				return response;
			}else {
				throw new ValidationException("There aren't bookService for that bookNumber");
			}
		}else {
			throw new ValidationException("Some data is wrong");
		}
	}
	
	private boolean validateLocalDateTime(LocalDate date, LocalTime startTime, LocalTime endTime) {
		LocalTime isbeforeTime = LocalTime.parse("07:00");
		LocalTime isAfterTime = LocalTime.parse("19:59");
		if(date.isAfter(LocalDate.now()) && !startTime.isBefore(isbeforeTime) && !startTime.isAfter(isAfterTime) &&
				!endTime.isBefore(isbeforeTime) && !endTime.isAfter(isAfterTime)) {
			return true;
		}else {
			return false;
		}
	}
	
	private boolean validateData(Long idCustomer, Long codeP, LocalDate date, LocalTime time) {
		patternIdCustomer = Pattern.compile("[0-9]{1,5}");
		matcherIdCustomer = patternIdCustomer.matcher(Long.toString(idCustomer));
		patternCodeP = Pattern.compile("[0-9]{5}");
		matcherCodeP = patternCodeP.matcher(Long.toString(codeP));		
		if(matcherCodeP.matches() && matcherIdCustomer.matches()) {
			return true;
		}else {
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
						&& !appointmentTime.isAfter(appointment.getEndTime())
						|| dateBook.isEqual(appointment.getDate())
								&& !appoitmentEndTime.isBefore(appointment.getStarTime())
								&& !appoitmentEndTime.isAfter(appointment.getEndTime()))
						.count();
			}
			return (count > 0) ? null : employee;
		}).filter(Objects::nonNull).findFirst();
		
		return employeeA;
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
		Appointment appoitmentValues = setValuesAppointment(bookService);
		appoitmentValues.setIdCustomer(bookService.getIdCustomer());
		appoitmentValues.setEndTime(appoitmentEndTime);		
		appoitmentValues.setPostalCode(bookService.getCodeP());
		employeeSaveAppointment(appoitmentValues);
		return bookService;
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
	
	private List<Employee> employeeFindByPostalCode(Long codeP) throws JsonMappingException, JsonProcessingException{
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(employeeFindByPostalCode+codeP).retrieve().bodyToMono(Response.class).block();
		}catch (Exception e) {
			throw new ValidationException("There aren't employees in that Postal code");
		}
		
		Object objectEmployee = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		String stringResponse = objectMapper.writeValueAsString(objectEmployee);
		List<Employee> responseEmployee = objectMapper.readValue(stringResponse, new TypeReference<List<Employee>>() {});
		return responseEmployee;
	}
	
	private Customer customerFindById(Long id) throws JsonMappingException, JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(customerFindById+id).retrieve().bodyToMono(Response.class).block();
		}catch (Exception e) {
			throw new ValidationException("There aren't customer with that ID");
		}				
		Object objectCustomer = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectCustomer);
		Customer responseCustomer = objectMapper.readValue(stringResponse, Customer.class);
		return responseCustomer;
	}
	
	private TypeService typeServiceFindByType(String type) throws JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(typeServiceFindByType+type).retrieve().bodyToMono(Response.class).block();
		}catch (Exception e) {
			throw new ValidationException("There aren't type services with that name");
		}				
		Object objectType = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectType);
		TypeService responseType = objectMapper.readValue(stringResponse, TypeService.class);
		return responseType;

	}
	
	private Customer customerFindByEmail(String email) throws JsonProcessingException {
		Response objectResponse = null;
		try {
			objectResponse = webClient.get().uri(customerFindByEmail+email).retrieve().bodyToMono(Response.class).block();
		}catch (Exception e) {
			throw new ValidationException("There isn't a customer with that Email");
		}				
		Object objectCustomer = objectResponse.getData();
		ObjectMapper objectMapper = new ObjectMapper();
		String stringResponse = objectMapper.writeValueAsString(objectCustomer);
		Customer responseCustomer = objectMapper.readValue(stringResponse, Customer.class);
		return responseCustomer;
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
	
	private void customerUpdateCountService(Long id, Long count) {
		webClient.put().uri(customerUpdateCountService, uri -> uri.queryParam("id", id).queryParam("count", count).build()) 
		.retrieve().bodyToMono(Response.class).block();
	}
	
	private Appointment setValuesAppointment(BookService bookService) {
		Appointment appointment = new Appointment();
		appointment.setIdEmployee(bookService.getIdEmployee());
		appointment.setTypeService(bookService.getTypeService());
		appointment.setDate(bookService.getDate());
		appointment.setStarTime(bookService.getStarTime());
		appointment.setBookNumber(bookService.getBookNumber());
		return appointment;		
	}
	
	private boolean validateBookNumber(Long number) {
		patternBookNumber = Pattern.compile("[0-9]{12,14}");
		matcherBookNumber = patternBookNumber.matcher(Long.toString(number));
		if (matcherBookNumber.matches()) {
			return true;
		}else {
			return false;
		}		
	}
	
	private BookService setValuesUpdate(BookService bookSeviceFound, BookService bookSevice) {
		bookSeviceFound.setCodeP(bookSevice.getCodeP());
		bookSeviceFound.setTypeService(bookSevice.getTypeService());
		bookSeviceFound.setDate(bookSevice.getDate());
		bookSeviceFound.setStarTime(bookSevice.getStarTime());
		return bookSeviceFound;
	}

}
