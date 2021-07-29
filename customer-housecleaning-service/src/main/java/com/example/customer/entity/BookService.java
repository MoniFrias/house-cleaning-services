package com.example.customer.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookService {

	private Long id;
	private Long bookNumber;
	private Long idCustomer;
	private Long idEmployee;
	private Long codeP;
	private String typeService;
	private LocalDate date;
	private LocalTime starTime;
	private LocalTime endTime;
	private Long cost;
	private String statusPay;
	private Long creditCard;

}
