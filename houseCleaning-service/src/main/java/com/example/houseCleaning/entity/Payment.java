package com.example.houseCleaning.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

	private Long id;
	private Long idCustomer;
	private String cardType;
	private Long cardNumber;

}
