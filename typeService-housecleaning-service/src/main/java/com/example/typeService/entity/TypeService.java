package com.example.typeService.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TypeService {

	@Id
	@GeneratedValue
	private Long id;
	
	@Pattern(regexp = "[a-zA-Z]{4,10}")
	@Column(name = "name")
	private String name;
	
	@Column(name = "cost")
	private Long cost;
	
	@Column(name = "timeSuggested")
	private Float timeSuggested;

}
