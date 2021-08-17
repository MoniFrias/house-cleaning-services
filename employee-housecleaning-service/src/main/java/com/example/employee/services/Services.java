package com.example.employee.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.employee.entity.Appointment;
import com.example.employee.entity.Employee;
import com.example.employee.entity.Response;
import com.example.employee.entity.TypeService;
import com.example.employee.entity.ValidationException;
import com.example.employee.repository.RepositoryAppointment;
import com.example.employee.repository.RepositoryEmployee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class Services {

	@Autowired
	RepositoryEmployee repository;
	@Autowired
	RepositoryAppointment repositoryAppointment;
	@Autowired
	WebClient webClient;
	@Value("${typeServiceFindByType}")	
	private String typeServiceFindByType;
	Pattern pattern, patternPhone, patternNss, patternIdCustomer, patternIdEmployee;
	Matcher matcher, matcherPhone, matcherNss, matcherIdCustomer, matcherIdEmployee;
		
	
	public Response save(Employee employee, BindingResult validResult) {
		Response response = new Response();
		boolean validation = validation(employee.getPhoneNumber(), employee.getPostalCode(), employee.getEmail(), employee.getNss());
		Employee employeeFound  =repository.findEmployeeByEmail(employee.getEmail());
		if (validation && !validResult.hasErrors()) {
			if (employeeFound == null) {
				response.setData(repository.save(employee));
				return response;
			}else {
				throw new ValidationException("Already there a employee with that Email");
			}			
		}else {
			throw new ValidationException("Some values are wrong");
		}
	}
	
	public Response saveAppointment(Appointment appointment, BindingResult validResultApp) throws JsonProcessingException {
		Response response = new Response();
		Boolean validationAppointment = validationAppointment(appointment);
		LocalDate dateBook = appointment.getDate();
		LocalTime AppointmentStarTime = appointment.getStarTime();
		
		List<Employee> listEmployee = repository.findEmployeeByPostalCode(appointment.getPostalCode());
		if (appointment.getId() == null && appointment.getBookNumber() == null) {
			if (validationAppointment && !validResultApp.hasErrors()) {
				if (!listEmployee.isEmpty()) {
					TypeService typeServiceFound = typeServiceFindByType(appointment.getTypeService());			
					LocalTime appoitmentEndTime = appointment.getStarTime().plusHours(typeServiceFound.getTimeSuggested());
					boolean validateDateTime = validateLocalDateTime(dateBook, AppointmentStarTime, appoitmentEndTime);
					if (validateDateTime) {
						Optional<Employee> employeeFirst = validateAvailable(listEmployee, dateBook, AppointmentStarTime, appoitmentEndTime);
					
						if (employeeFirst.isPresent()) {
							Employee employee = employeeFirst.get();
							Long bookNumber = createBookNumber(appointment);
							appointment.setBookNumber(bookNumber);
							appointment.setIdEmployee(employee.getId());
							appointment.setEndTime(appoitmentEndTime);
							appointment.setStatusService("Pendient");
							response.setData(repositoryAppointment.save(appointment));
							return response;
						}else {
							throw new ValidationException("No employees available");
						}					
					}else {
						throw new ValidationException("Out of schedule");
					}
				}else {
					throw new ValidationException("No employees saved");
				}
			}else {
				throw new ValidationException("Some values are wrong");
			}
		}else {
			response.setData(repositoryAppointment.save(appointment));
			return response;
		}
	}	
	
	public Response findAll() {
		Response response = new Response();
		List<Employee> listEmployee = repository.findAll();
		if (!listEmployee.isEmpty()) {
			listEmployee.stream().map(employee -> {
				Employee employeeNew = addAppointment(employee);
				return employeeNew;
			}).filter(Objects::nonNull).collect(Collectors.toList());

			response.setData(listEmployee);
			return response;
		} else {
			throw new ValidationException("Is empty");
		}
	}	
	
	public Response findById(Long id) {
		Response response = new Response();
		Employee employee = repository.findEmployeeById(id);
		if (id != null & id > 0) {
			if (employee != null) {
				Employee employeeNew = addAppointment(employee);		
				response.setData(employeeNew);
				return response;
			}else {
				throw new ValidationException("No employees with that ID");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}
	
	public Response findByCity(String city) {
		Response response = new Response();
		pattern = Pattern.compile("[a-zA-Z]{4,30}");
		matcher = pattern.matcher(city);
		List<Employee> listEmployee = repository.findEmployeeByCity(city);
		if (matcher.matches()) {
			if (!listEmployee.isEmpty()) {
				List<Employee> listEmployeeNew = addAppointmentListEmployee(listEmployee);			
				response.setData(listEmployeeNew);
				return response;
			}else {
				throw new ValidationException("No employees in that city");
			}
		}else {
			throw new ValidationException("Some values are wrong");
		}
	}
	
	public Response findByState(String state) {
		Response response = new Response();
		pattern = Pattern.compile("[A-Z]{2,3}");
		matcher = pattern.matcher(state);
		List<Employee> listEmployee = repository.findEmployeeByState(state);
		if (matcher.matches()) {
			if (!listEmployee.isEmpty()) {
				List<Employee> listEmployeeNew = addAppointmentListEmployee(listEmployee);			
				response.setData(listEmployeeNew);
				return response;
			}else {
				throw new ValidationException("No employees in that State");
			}	
		}else {
			throw new ValidationException("Some values are wrong");
		}
	}
	
	public Response findByEmail(String email) {
		Response response = new Response();
		Employee employeeFound = repository.findEmployeeByEmail(email);
		if (email.contains(".com")) {
			if (employeeFound != null) {
				employeeFound = addAppointment(employeeFound);
				response.setData(employeeFound);
				return response;
			} else {
				throw new ValidationException("No customer with that Email");
			}
		}else {
			throw new ValidationException("Value is wrong");
		}
	}
	
	public Response findByPostalCode(Long code) {
		Response response = new Response();
		pattern = Pattern.compile("[0-9]{5}");
		matcher = pattern.matcher(Long.toString(code));
		List<Employee> listEmployee = repository.findEmployeeByPostalCode(code);
		if (matcher.matches()) {
			if (!listEmployee.isEmpty()) {	
				List<Employee> listEmployeeNew = addAppointmentListEmployee(listEmployee);			
				response.setData(listEmployeeNew);
				return response;
			}else {
				throw new ValidationException("No employees in that Postal code");
			}
		}else {
			throw new ValidationException("Some values are wrong");
		}
	}
	
	public Response findByBookNumber(Long bookNumber) {
		Response response = new Response();
		pattern = Pattern.compile("[0-9]{10,15}");
		matcher = pattern.matcher(Long.toString(bookNumber));
		Appointment appointmentFound = repositoryAppointment.findAppointmentByBookNumber(bookNumber);
		if (matcher.matches()) {
			if (appointmentFound != null) {			
				response.setData(appointmentFound);
				return response;
			}else {
				throw new ValidationException("No appointment with that book number");
			}
		}else {
			throw new ValidationException("Some values are wrong");
		}
	}

	public Response update(Employee employee, Long id, BindingResult validResult) {
		Response response = new Response();
		boolean validationResult = validation(employee.getPhoneNumber(), employee.getPostalCode(), employee.getEmail(), employee.getNss());
		Employee employeeFound = repository.findEmployeeById(id);
		Employee employeeFoundByEmail = repository.findEmployeeByEmail(employee.getEmail());
		if (id != null && id > 0) {
			if (validationResult && !validResult.hasErrors()) {
				if (employeeFoundByEmail == null) {
					if (employeeFound != null) {
						response.setData(repository.save(updateEmployee(employee, employeeFound)));
						return response;
					}else {
						throw new ValidationException("No employee with that Id");
					}
				}else{
					if (employeeFound != null && employeeFoundByEmail.getId() == id) {
						response.setData(repository.save(updateEmployee(employee, employeeFound)));
						return response;
					}else {
						throw new ValidationException("No employee with that Id or already save employee with that email");
					}
					
				}
			}else {
				throw new ValidationException("Some values are wrong");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}
	
	public Response updateAppointment(@Valid Appointment appointment, BindingResult validResultApp) throws JsonProcessingException {
		Response response = new Response();
		boolean validationAppointment = validationAppointment(appointment);
		Appointment appointmentFound = repositoryAppointment.findAppointmentById(appointment.getId());
		LocalDate dateBook = appointment.getDate();
		LocalTime AppointmentStarTime = appointment.getStarTime();
		
		List<Employee> listEmployee = repository.findEmployeeByPostalCode(appointment.getPostalCode());
		if (validationAppointment && !validResultApp.hasErrors()) {
			if (!listEmployee.isEmpty() && appointmentFound != null && !appointmentFound.getStatusService().equals("done")) {
				TypeService typeServiceFound = typeServiceFindByType(appointment.getTypeService());			
				LocalTime appoitmentEndTime = appointment.getStarTime().plusHours(typeServiceFound.getTimeSuggested());
				boolean validateDateTime = validateLocalDateTime(dateBook, AppointmentStarTime, appoitmentEndTime);
				if (validateDateTime) {
					Optional<Employee> employeeFirst = validateAvailable(listEmployee, dateBook, AppointmentStarTime, appoitmentEndTime);
				
					if (employeeFirst.isPresent()) {
						Employee employee = employeeFirst.get();
						appointmentFound.setIdCustomer(appointment.getIdCustomer());
						appointmentFound.setIdEmployee(employee.getId());
						appointmentFound.setPostalCode(appointment.getPostalCode());
						appointmentFound.setTypeService(typeServiceFound.getName());
						appointmentFound.setDate(appointment.getDate());
						appointmentFound.setStarTime(appointment.getStarTime());
						appointmentFound.setEndTime(appoitmentEndTime);
						response.setData(repositoryAppointment.save(appointmentFound));
						return response;
					}else {
						throw new ValidationException("No employees available");
					}					
				}else {
					throw new ValidationException("Out of schedule");
				}
			}else {
				throw new ValidationException("No employees saved or no appointment with that ID");
			}
		}else {
			throw new ValidationException("Some values are wrong");
		}
	}
	

	public Response updateStatusPayment(Long bookService, String status) {
		Response response = new Response();
		Appointment appointmentFound = repositoryAppointment.findAppointmentByBookNumber(bookService);
		if (appointmentFound != null && appointmentFound.getStatusPay() != "Paid") {
			appointmentFound.setStatusPay(status);
			response.setData(repositoryAppointment.save(appointmentFound));
			return response;
		}else {
			throw new ValidationException("No Appointment with that bookService or is already paid");
		}
	}
	
	public Response updateStatusAppointment(Long bookService, String statusAppoin) {
		Response response = new Response();
		Appointment appointmentFound = repositoryAppointment.findAppointmentByBookNumber(bookService);
		if (appointmentFound != null && appointmentFound.getStatusPay() == "Paid") {
			appointmentFound.setStatusService(statusAppoin);
			response.setData(repositoryAppointment.save(appointmentFound));
			return response;
		}else {
			throw new ValidationException("No Appointment with that bookService");
		}
	}

	public Response deleteById(Long id) {
		Response response = new Response();
		Employee employeeFound = repository.findEmployeeById(id);
		if (id != null && id > 0) {
			if (employeeFound != null) {
				repository.deleteById(id);
				return response;
			}else {
				throw new ValidationException("There isn't a employee with that Id");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}
	
	private Employee updateEmployee(Employee employee, Employee employeeFound) {
		employeeFound.setName(employee.getName());
		employeeFound.setLastName(employee.getLastName());
		employeeFound.setNss(employee.getNss());
		employeeFound.setEmail(employee.getEmail());
		employeeFound.setCity(employee.getCity());
		employeeFound.setPhoneNumber(employee.getPhoneNumber());
		return employeeFound;
	}
	
	private Optional<Employee> validateAvailable(List<Employee> listEmployee, LocalDate dateBook, LocalTime AppointmentStarTime, LocalTime appoitmentEndTime) {
		Optional<Employee> employeeA = listEmployee.stream().map(employee -> {
			Employee employeeNew = addAppointment(employee);
			long count = 0;
			if (employeeNew.getAppointments() != null) {
				count = employee.getAppointments().parallelStream()
						.filter(appointmentFilter -> dateBook.isEqual(appointmentFilter.getDate())
						&& !AppointmentStarTime.isBefore(appointmentFilter.getStarTime())
						&& !AppointmentStarTime.isAfter(appointmentFilter.getEndTime().plusHours(1L))
						|| dateBook.isEqual(appointmentFilter.getDate())
								&& !appoitmentEndTime.isBefore(appointmentFilter.getStarTime())
								&& !appoitmentEndTime.isAfter(appointmentFilter.getEndTime().plusHours(1L)))
						.count();
			}
			return (count > 0) ? null : employeeNew;
		}).filter(Objects::nonNull).findFirst();
		
		return employeeA;
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
	
	private boolean validation(Long phoneNumber, Long code, String email, Long nss) {
		patternPhone = Pattern.compile("[0-9]{10}");
		matcherPhone = patternPhone.matcher(Long.toString(phoneNumber));
		patternNss = Pattern.compile("[0-9]{8}");
		matcherNss = patternNss.matcher(Long.toString(nss));
		pattern = Pattern.compile("[0-9]{5}");
		matcher = pattern.matcher(Long.toString(code));
		if(matcherPhone.matches() && matcherNss.matches() && matcher.matches() && email.contains(".com")) {
			return true;
		}else {
			return false;
		}		
	} 
	
	private boolean validationAppointment(Appointment appoitment) {
		patternIdCustomer = Pattern.compile("[0-9]{1,5}");
		matcherIdCustomer = patternIdCustomer.matcher(Long.toString(appoitment.getIdCustomer()));
		pattern = Pattern.compile("[0-9]{5}");
		matcher = pattern.matcher(Long.toString(appoitment.getPostalCode()));
		if (matcherIdCustomer.matches() & matcher.matches()) {
			return true;
		}else {
			return false;
		}
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

	private Employee addAppointment(Employee employee) {
		List<Appointment> appointments = repositoryAppointment.findAppointmentByIdEmployee(employee.getId());
		if (appointments.isEmpty()) {
			employee.setAppointments(new ArrayList<>());
			return employee;
		} else {
			employee.setAppointments(appointments);
			return employee;
		}
	}
	
	private List<Employee> addAppointmentListEmployee(List<Employee> listEmployee){
		listEmployee.stream().map(employee -> {
			Employee employeeNew = addAppointment(employee);
			return employeeNew;
		}).filter(Objects::nonNull).collect(Collectors.toList());
		return listEmployee;
	}

	private Long createBookNumber(Appointment appointment) {
		int month = LocalDate.now().getMonthValue();
		int year = LocalDate.now().getYear();
		int day = LocalDate.now().getDayOfMonth();
		int hour = LocalTime.now().getHour();
		int minute = LocalTime.now().getMinute();
		int second = LocalTime.now().getSecond();
		Long number = Long.valueOf(String.valueOf(year) + String.valueOf(month) + String.valueOf(day)
				+ String.valueOf(hour) + String.valueOf(minute) + String.valueOf(second)
				+ String.valueOf(appointment.getIdCustomer()));
		return number;
	}


	

	
	
	
	
}


