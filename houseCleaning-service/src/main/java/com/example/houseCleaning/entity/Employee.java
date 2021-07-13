package com.example.houseCleaning.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

	private Long id;
	private String name;
	private String lastName;
	private Long nss;
	private String email;
	private String city;
	private String state;
	private String postalCode;
	private Long phoneNumber;

}