package com.example.houseCleaning.entity;

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
public class BookService {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name = "bookNumber")
	private Long bookNumber;
	
	@Column(name = "idCustomer")
	private Long idCustomer;
	
	@Column(name = "idEmployee")
	private Long idEmployee;
	
	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "time")
	private LocalTime time;
	
	@Column(name = "cost")
	private Long cost;
	
	@Column(name = "statusPay")
	private String statusPay;

}
