package com.example.houseCleaning.entity;


import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment{

	private Long id;
	private Long bookNumber;
	private Long idEmployee;
	private Long idCustomer;
	private Long postalCode;
	private String typeService;
	private LocalDate date;
	private LocalTime starTime;
	private LocalTime endTime;
}
