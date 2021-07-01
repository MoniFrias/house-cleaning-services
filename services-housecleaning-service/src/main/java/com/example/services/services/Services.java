package com.example.services.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.example.services.entity.Response;
import com.example.services.entity.TypeServices;
import com.example.services.entity.ValidationException;
import com.example.services.repository.RepositoryServices;

@Service
public class Services {

	@Autowired
	RepositoryServices repository;
	
	Pattern patternCost, patternTime;
	Matcher matcherCost, matcherTime;
	
	private boolean validationValues(Long cost, Long time) {
		patternCost = Pattern.compile("[0-9]{3,4}");
		matcherCost = patternCost.matcher(Long.toString(cost));
		patternTime = Pattern.compile("[0-9]{1,3}");
		matcherTime = patternTime.matcher(Float.toString(time));
		if (matcherCost.matches() && matcherTime.matches()) {
			return true;
		}else {
			return false;
		}
	}
	
	public Response save(TypeServices typeServices, BindingResult validResult) {
		Response response = new Response();
		boolean validationResult = validationValues(typeServices.getCost(), typeServices.getSuggestedTime());
		TypeServices typeServiceFound = repository.findTypeServiceByType(typeServices.getType());
		if (validationResult && !validResult.hasErrors()) {
			if (typeServiceFound == null) {
				return response;
			}else {
				throw new ValidationException("");
			}
		}else {
			throw new ValidationException("");
		}
		
	}

	public Response findAll() {
		Response response = new Response();
		List<TypeServices> listTypeServices = repository.findAll();
		if (!listTypeServices.isEmpty()) {
			response.setData(listTypeServices);
			return response;
		}else {
			throw new ValidationException("Is empty");
		}	
	}

	public Response findByType(String type) {
		Response response = new Response();
		List<TypeServices> listTypeServices = repository.findTypeServicesByType(type);
		if (!listTypeServices.isEmpty()) {
			response.setData(listTypeServices);
			return response;
		}else {
			throw new ValidationException("Is empty");
		}
	}
	
	private TypeServices updateTypeServices(TypeServices TypeServices, TypeServices TypeServicesFound) {
		TypeServicesFound.setType(TypeServices.getType());
		TypeServicesFound.setCost(TypeServices.getCost());
		TypeServicesFound.setSuggestedTime(TypeServices.getSuggestedTime());
		return TypeServicesFound;
	}

	public Response update(TypeServices typeServices, Long id, BindingResult validResult) {
		Response response = new Response();
		boolean validation = validationValues(typeServices.getCost(), typeServices.getSuggestedTime());
		TypeServices typeServicesFound = repository.findTypeServiceById(id);
		if (id != null && id > 0) {
			if (validation && !validResult.hasErrors()) {
				if (typeServicesFound != null) {
					response.setData(repository.save(updateTypeServices(typeServices, typeServicesFound)));
					return response;
				}else {
					throw new ValidationException("There isn't a typeServices with that Id");
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
		TypeServices typeServicesFound = repository.findTypeServiceById(id);
		if (id != null && id > 0) {
			if (typeServicesFound != null) {
				repository.deleteById(id);
				return response;
			}else {
				throw new ValidationException("There isn't a typeServices with that Id");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}

	

}
