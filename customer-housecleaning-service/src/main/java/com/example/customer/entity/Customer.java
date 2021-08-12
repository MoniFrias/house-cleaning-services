package com.example.customer.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	@Pattern(regexp = "[a-zA-Z]{4,10}")
	@Column(name = "name")
	private String name;
	
	@NotNull
	@Pattern(regexp = "[a-zA-Z]{4,10}")
	@Column(name = "lastName")
	private String lastName;
	
	@NotNull
	@Email
	@Column(name = "email")
	private String email;
	
	@NotNull
	@Pattern(regexp = "[a-zA-Z]{4,30}")
	@Column(name = "city")
	private String city;	

	@NotNull
	@Pattern(regexp = "[A-Z]{2,3}")
	@Column(name = "state")
	private String state;
	
	@NotNull
	@Column(name = "postalCode")
	private Long postalCode;
	
	@NotNull
	@Pattern(regexp = "[a-zA-Z0-9]{5,50}")
	@Column(name = "address")
	private String address;
	
	@NotNull
	@Column(name = "outdoorNumber")
	private Long outdoorNumber;
	
	@NotNull
	@Column(name = "phoneNumber")
	private Long phoneNumber;
	
	@Column(name = "countService")
	private Long countService;
	
	@Transient
	private List<Payment> listPayment;
	
	@Transient
	private List<BookService> listBookService;
	
}