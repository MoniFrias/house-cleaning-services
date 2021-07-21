package com.example.houseCleaning.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

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
	
	@Column(name = "codeP")
	private Long codeP;
	
	@Column(name = "typeService")
	private String typeService;
	
	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "starTime")
	private LocalTime starTime;
		
	@Column(name = "cost")
	private Long cost;
	
	@Column(name = "statusPay")
	private String statusPay;
	
	

}
