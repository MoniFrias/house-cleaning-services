package com.example.employee.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name = "idEmployee")
	private Long idEmployee;
	
	@Column(name = "typeService")
	private String typeService;
	
	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "starTime")
	private LocalTime starTime;
	
	@Column(name = "endTime")
	private LocalTime endTime;

}
