package com.example.houseCleaning.entity;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee{

	
	//private static final long serialVersionUID = 6255196506819628318L;
	private Long id;
	private String name;
	private String lastName;
	private Long nss;
	private String email;
	private String city;
	private String state;
	private String postalCode;
	private Long phoneNumber;
	private List<Appointment> appointments;
}
