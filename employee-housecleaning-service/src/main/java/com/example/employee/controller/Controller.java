package com.example.employee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.services.Services;

@RestController
@RequestMapping(path = "/employee")
public class Controller {

	@Autowired
	Services services;

}
