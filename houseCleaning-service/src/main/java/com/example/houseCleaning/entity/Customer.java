package com.example.houseCleaning.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
	
	private Long id;
	private String name;
	private String lastName;
	private String email;
	private String city;	
	private String state;
	private Long postalCode;
	private String address;
	private Long outdoorNumber;
	private Long phoneNumber;
	private Long countService;
	private List<Payment> listPayment;
	private List<BookService> listBookService;
}
