package com.example.typeService.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import com.example.typeService.entity.Response;
import com.example.typeService.entity.TypeService;
import com.example.typeService.entity.ValidationException;
import com.example.typeService.repository.RepositoryTypeService;

@Service
public class Services {

	@Autowired
	RepositoryTypeService repository;
	
	Pattern pattern, patternCost, patternTime;
	Matcher matcher, matcherCost, matcherTime;
	
	
	public Response save(TypeService typeService, BindingResult validResult) {
		Response response = new Response();
		boolean validationResult = validation(typeService.getCost(), typeService.getTimeSuggested());
		TypeService typeServiceFound = repository.findTypeServiceByName(typeService.getName());
		if(validationResult && !validResult.hasErrors()) {
			if (typeServiceFound == null) {
				response.setData(repository.save(typeService));
				return response;
			}else {
				throw new ValidationException("Already there is a Type service with that name");
			}
		}else {
			throw new ValidationException("Some values are wrong");
		}
	}

	public Response findAll() {
		Response response = new Response();
		List<TypeService> listTypeService = repository.findAll();
		if (!listTypeService.isEmpty()) {
			response.setData(listTypeService);
			return response;
		}else {
			throw new ValidationException("No type services, is empty");
		}
	}
	
	public Response findById(Long id) {
		Response response = new Response();
		TypeService typeServiceFound = repository.findTypeServiceById(id);
		if (id != null && id > 0) {
			if (typeServiceFound != null) {
				response.setData(typeServiceFound);
				return response;
			}else {
				throw new ValidationException("No type services with that name");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}

	public Response findByType(String type) {
		Response response = new Response();
		pattern  = Pattern.compile("[a-zA-Z]{4,10}");
		matcher = pattern.matcher(type);
		TypeService typeServiceFound = repository.findTypeServiceByName(type);
		if (matcher.matches()) {
			if (typeServiceFound != null) {
				response.setData(typeServiceFound);
				return response;
			}else {
				throw new ValidationException("No type services with that name");
			}
		}else {
			throw new ValidationException("value is wrong");
		}
	}	
	
	public Response update(TypeService typeService, BindingResult validResult, Long id) {
		Response response = new Response();
		TypeService typeServiceFound = repository.findTypeServiceById(id);
		boolean validationResult = validation(typeService.getCost(), typeService.getTimeSuggested());
		if (id != null && id > 0) {
			if(validationResult && !validResult.hasErrors()) {
				if (typeServiceFound != null) {
					response.setData(repository.save(updateTypeService(typeService, typeServiceFound)));
					return response;
				}else {
					throw new ValidationException("No type services with that name");
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
		TypeService typeServiceFound = repository.findTypeServiceById(id);
		if (id != null && id > 0) {
			if (typeServiceFound != null) {
				repository.deleteById(id);
				return response;
			}else {
				throw new ValidationException("There aren't type services with that Id");
			}
		}else {
			throw new ValidationException("Id can't be null or zero");
		}
	}	
	
	private boolean validation(Long cost, Long timeSuggest) {
		patternCost = Pattern.compile("[0-9]{3,4}");
		patternTime = Pattern.compile("[0-9]{1,2}");
		matcherCost = patternCost.matcher(Long.toString(cost));
		matcherTime = patternTime.matcher(Long.toString(timeSuggest));
		if (matcherCost.matches() && matcherTime.matches()) {
			return true;
		}else {
			return false;
		}
	}
	
	private TypeService updateTypeService(TypeService typeService, TypeService typeServiceFound) {
		typeServiceFound.setName(typeService.getName());
		typeServiceFound.setCost(typeService.getCost());
		typeServiceFound.setTimeSuggested(typeService.getTimeSuggested());
		return typeServiceFound;
	}

}
