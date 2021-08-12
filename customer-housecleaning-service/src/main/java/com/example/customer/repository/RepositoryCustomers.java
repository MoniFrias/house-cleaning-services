package com.example.customer.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.customer.entity.Customer;

@Repository
public interface RepositoryCustomers extends JpaRepository<Customer, Long>{

	Customer findCustomerByEmail(String email);

	List<Customer> findCustomerByCity(String city);

	Customer findCustomerById(Long id);

	void deleteCustomerById(Long id);

	List<Customer> findCustomerByState(String state);

	List<Customer> findCustomerByPostalCode(Long code);
	
	@Query(nativeQuery = true, value = "SELECT id,name,state FROM Customer WHERE state =:state")
	List<Object> findCustomerByStates(String state);
	
	@Query(nativeQuery = true, value = "SELECT id,name,email,state,address FROM Customer WHERE id =:id")
	Object findInfoByIdCustomer(Long id);

}
