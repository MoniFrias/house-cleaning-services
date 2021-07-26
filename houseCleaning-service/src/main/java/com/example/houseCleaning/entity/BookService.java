package com.example.houseCleaning.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
	
	@NotNull
	@Column(name = "idCustomer")
	private Long idCustomer;
	
	
	@Column(name = "idEmployee")
	private Long idEmployee;
	
	@NotNull
	@Column(name = "codeP")
	private Long codeP;
	
	@NotNull
	@Pattern(regexp = "[a-zA-Z]{5,20}")
	@Column(name = "typeService")
	private String typeService;
	
	@NotNull
	@Column(name = "date")
	private LocalDate date;
	
	@NotNull
	@Column(name = "starTime")
	private LocalTime starTime;
	
	@Column(name = "endTime")
	private LocalTime endTime;
		
	@Column(name = "cost")
	private Long cost;
	
	@Column(name = "statusPay")
	private String statusPay;
	
	

}
