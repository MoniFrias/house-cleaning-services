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
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "BOOK_NUMBER")
	private Long bookNumber;
	
	@NotNull
	@Column(name = "ID_CUSTOMER")
	private Long idCustomer;
	
	
	@Column(name = "ID_EMPLOYEE")
	private Long idEmployee;
	
	@NotNull
	@Column(name = "CODE_POSTAL")
	private Long codeP;
	
	@NotNull
	@Pattern(regexp = "[a-zA-Z]{5,20}")
	@Column(name = "TYPE_SERVICE")
	private String typeService;
	
	@NotNull
	@Column(name = "DATE")
	private LocalDate date;
	
	@NotNull
	@Column(name = "STAR_TIME")
	private LocalTime starTime;
	
	@Column(name = "END_TIME")
	private LocalTime endTime;
		
	@Column(name = "COST")
	private Long cost;
	
	@Column(name = "STATUS_PAY")
	private String statusPay;
	
	@Column(name = "CREDIT_CARD")
	private Long creditCard;
	
	@Column(name = "STATUS_SERVICE")
	private String statusService;

}
