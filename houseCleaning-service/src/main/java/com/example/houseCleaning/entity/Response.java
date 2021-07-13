package com.example.houseCleaning.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

	private boolean result=true;
	private String message="success";
	private Object data;

}
