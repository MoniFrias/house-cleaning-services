package com.example.customer.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {

	@Id
	@GeneratedValue
	private Long id;
	
	@Pattern(regexp = "[a-zA-Z]{4,10}")
	@Column(name = "name")
	private String name;
	@Pattern(regexp = "[a-zA-Z]{4,10}")
	@Column(name = "lastName")
	private String lastName;
	
	@Column(name = "email")
	private String email;
	
	@Pattern(regexp = "[a-zA-Z]{4,30}")
	@Column(name = "city")
	private String city;
	@Pattern(regexp = "[a-zA-Z0-9]{5,50}")
	@Column(name = "address")
	private String address;
	
	@Column(name = "phoneNumber")
	private Long phoneNumber;
	
	
}