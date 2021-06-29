package com.example.employee.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.employee.repository.RepositoryEmployee;

@Service
public class Services {

	@Autowired
	RepositoryEmployee repository;

}
