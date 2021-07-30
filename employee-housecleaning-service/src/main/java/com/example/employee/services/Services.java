package com.example.employee.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
		long count = 0;
		boolean validationAppoitment = validationAppointment(appointment);
		LocalDate appointmentDate = appointment.getDate();
		LocalTime appointmentstartTime = appointment.getStarTime();
		Employee employee = repository.findEmployeeById(appointment.getIdEmployee());
		if (validationAppoitment && !validResultApp.hasErrors()) {
			if (employee != null) {
				TypeService typeServiceFound = typeServiceFound(appointment.getTypeService());
				LocalTime appoitmentEndTime = appointment.getStarTime().plusHours(typeServiceFound.getTimeSuggested());
				boolean validateDateTime = validateLocalDateTime(appointmentDate, appointmentstartTime, appoitmentEndTime);
				if (validateDateTime) {
					if (typeServiceFound != null) {
						Employee employeeNew = addAppointment(employee);
						if (employeeNew.getAppointments() != null) {
							count = employee.getAppointments().parallelStream()
									.filter(appointmentFilter -> appointmentDate.isEqual(appointmentFilter.getDate())
											&& !appointmentstartTime.isBefore(appointmentFilter.getStarTime())
											&& !appointmentstartTime.isAfter(appointmentFilter.getEndTime())
											|| appointmentDate.isEqual(appointmentFilter.getDate())
													&& !appoitmentEndTime.isBefore(appointmentFilter.getStarTime())
													&& !appoitmentEndTime.isAfter(appointmentFilter.getEndTime()))
									.count();
							if (count == 0) {
								appointment.setEndTime(appoitmentEndTime);
								response.setData(repositoryAppointment.save(appointment));
								return response;
							} else {
								throw new ValidationException("That schedule is not available, there is already an appointment");
							}
						} else {
							appointment.setEndTime(appoitmentEndTime);
							response.setData(repositoryAppointment.save(appointment));
							return response;
						}
					}
				} else {
					throw new ValidationException("Out of schedule");
				}
			}
			throw new ValidationException("No employee with that ID");
		} else {
			throw new ValidationException("Some values are wrong");
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
		


	public Response update(Employee employee, Long id, BindingResult validResult) {
		Response response = new Response();
		boolean validationResult = validation(employee.getPhoneNumber(), employee.getPostalCode(), employee.getEmail(), employee.getNss());
		Employee employeeFound = repository.findEmployeeById(id);
		if (id != null && id > 0) {
			if (validationResult && !validResult.hasErrors()) {
				if (employeeFound != null) {
					response.setData(repository.save(updateEmployee(employee, employeeFound)));
					return response;
				}else {
					throw new ValidationException("No employee with that Id");
				}
			}else {
				throw new ValidationException("Some values are wrong");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
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
		patternIdEmployee = Pattern.compile("[0-9]{1,5}");
		matcherIdEmployee = patternIdEmployee.matcher(Long.toString(appoitment.getIdEmployee()));
		if (matcherIdCustomer.matches() && matcherIdEmployee.matches()) {
			return true;
		}else {
			return false;
		}
	}
	
	private TypeService typeServiceFound(String type) throws JsonProcessingException {
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
	
	
}


