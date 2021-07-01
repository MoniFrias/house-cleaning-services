package com.example.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.employee.entity.Employee;

@Repository
public interface RepositoryEmployee extends JpaRepository<Employee, Long>{

	Employee findEmployeeByEmail(String email);

	List<Employee> findEmployeeByCity(String city);

	Employee findEmployeeById(Long id);

	List<Employee> findEmployeeByState(String state);

	List<Employee> findEmployeeByPostalCode(String code);

}
