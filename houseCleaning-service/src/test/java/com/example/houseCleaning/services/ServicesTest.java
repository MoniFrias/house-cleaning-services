package com.example.houseCleaning.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.example.houseCleaning.entity.Customer;
import com.example.houseCleaning.entity.Response;
import com.example.houseCleaning.entity.ValidationException;
import com.example.houseCleaning.repository.RepositoryBook;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ServicesTest {

	@InjectMocks
	Services services;
	@Mock
	RepositoryBook repository;
	@Mock
	WebClient webClient;
	@Mock
	RequestBodyUriSpec requestBodyUriSpec;
	@Mock
	RequestBodySpec requestBodySpec;
	@Mock
	RequestHeadersSpec requestHeaderSpec;
	@Mock
	ResponseSpec responseSpec;
	
	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(services, "customerSave", "");
	}
	
	
	@Test
	public void createAccountCustomerSaveCustomerTest() {
		Customer customer =  new Customer(1L, "name", "lastName", "email@gmail.com", "city", "NLL", 12345L, "address", 123L, 1234567896L, 0L, new ArrayList<>(), new ArrayList<>());
		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		MediaType contentType=null;
		when(requestBodySpec.contentType(contentType.APPLICATION_JSON)).thenReturn(requestBodySpec);
		when(requestBodySpec.body(Mockito.any())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", customer));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertTrue(services.createAccountCustomer(customer).isResult());
	}
	
	@Test
	public void createAccountCustomerSaveCustomerCATCHTest() {
		Customer customer =  new Customer(1L, "name", "lastName", "email@gmail.com", "city", "NLL", 12345L, "address", 123L, 1234567896L, 0L, new ArrayList<>(), new ArrayList<>());
		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
		MediaType contentType=null;
		when(requestBodySpec.contentType(contentType.APPLICATION_JSON)).thenReturn(requestBodySpec);
		when(requestBodySpec.body(Mockito.anyObject())).thenReturn(requestHeaderSpec);
		when(requestHeaderSpec.retrieve()).thenReturn(responseSpec);
		Mono<Response> monoResponse = Mono.just(new Response(true, "", true));
		when(responseSpec.bodyToMono(Response.class)).thenReturn(monoResponse);
		assertThrows(ValidationException.class, () -> services.createAccountCustomer(customer));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
