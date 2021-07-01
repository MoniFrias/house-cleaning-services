package com.example.employee.services;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.example.employee.entity.Employee;
import com.example.employee.entity.Response;
import com.example.employee.entity.ValidationException;
import com.example.employee.repository.RepositoryEmployee;

@Service
public class Services {

	@Autowired
	RepositoryEmployee repository;
	
	
	Pattern patternPhone, patternNss;
	Matcher matcherPhone, matcherNss;
	
	private boolean validation(Long phoneNumber, String email, Long nss) {
		patternPhone = Pattern.compile("[0-9]{10}");
		matcherPhone = patternPhone.matcher(Long.toString(phoneNumber));
		patternNss = Pattern.compile("[0-9]{8}");
		matcherNss = patternNss.matcher(Long.toString(nss));
		if(matcherPhone.matches() && matcherNss.matches() && email.contains(".com")) {
			return true;
		}else {
			return false;
		}		
	}

	public Response save(Employee employee, BindingResult validResult) {
		Response response = new Response();
		boolean validation = validation(employee.getPhoneNumber(), employee.getEmail(), employee.getNss());
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

	public Response findAll() {
		Response response = new Response();
		List<Employee> listEmployee = repository.findAll();
		if (!listEmployee.isEmpty()) {
			response.setData(listEmployee);
			return response;
		}else {
			throw new ValidationException("Is empty");
		}	
	}

	public Response findByCity(String city) {
		Response response = new Response();
		List<Employee> listEmployee = repository.findEmployeeByCity(city);
		if (!listEmployee.isEmpty()) {
			response.setData(listEmployee);
			return response;
		}else {
			throw new ValidationException("Is empty");
		}
	}
	
	public Response findByState(String state) {
		Response response = new Response();
		List<Employee> listEmployee = repository.findEmployeeByState(state);
		if (!listEmployee.isEmpty()) {
			response.setData(listEmployee);
			return response;
		}else {
			throw new ValidationException("There aren't Customers in that State");
		}	
	}
	
	public Response findByPostalCode(String code) {
		Response response = new Response();
		List<Employee> listEmployee = repository.findEmployeeByPostalCode(code);
		if (!listEmployee.isEmpty()) {
			response.setData(listEmployee);
			return response;
		}else {
			throw new ValidationException("There aren't Customers in that Postal code");
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

	public Response update(Employee employee, Long id, BindingResult validResult) {
		Response response = new Response();
		boolean matcherPhoneNumber = validation(employee.getPhoneNumber(), employee.getEmail(), employee.getNss());
		Employee employeeFound = repository.findEmployeeById(id);
		if (id != null && id > 0) {
			if (matcherPhoneNumber && !validResult.hasErrors()) {
				if (employeeFound != null) {
					response.setData(repository.save(updateEmployee(employee, employeeFound)));
					return response;
				}else {
					throw new ValidationException("There isn't a employee with that Id");
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

}


