package com.example.customer.entity;

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
public class Payment {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@NotNull
	@Column(name = "ID_CUSTOMER")
	private Long idCustomer;
	
	@NotNull
	@Pattern(regexp = "[a-zA-Z]{5,6}")
	@Column(name = "CARD_TYPE")
	private String cardType;
	
	@NotNull
	@Column(name = "CARD_NUMBER")
	private Long cardNumber;
	
	
	
	
}
