package com.example.typeService.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.example.typeService.entity.TypeService;
import com.example.typeService.entity.ValidationException;
import com.example.typeService.repository.RepositoryTypeService;

@ExtendWith(MockitoExtension.class)
class ServicesTest {
	
	@InjectMocks
	Services services;
	@Mock
	RepositoryTypeService repository;

	@Test
	void saveValidationResultElseTest() {
		TypeService typeService = new TypeService(1L, "House", 2L, 322L);
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		assertThrows(ValidationException.class, () -> services.save(typeService , validResult));
	}
	
	@Test
	void saveValidResultElseTest() {
		TypeService typeService = new TypeService(1L, "Ho", 500L, 3L);
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		assertThrows(ValidationException.class, () -> services.save(typeService , validResult));
	}
	  
	@Test
	void saveTypeServiceFoundElseTest() {
		TypeService typeService = new TypeService(1L, "House", 500L, 3L);
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		when(repository.findTypeServiceByName(Mockito.anyString())).thenReturn(typeService);
		assertThrows(ValidationException.class, () -> services.save(typeService , validResult));
	}
	
	@Test
	void saveTest() {
		TypeService typeService = new TypeService(1L, "House", 500L, 3L);
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		when(repository.findTypeServiceByName(Mockito.anyString())).thenReturn(null);
		assertTrue(services.save(typeService , validResult).isResult());
	}
	
	@Test
	void findAllListTypeServicesElseTest() {
		List<TypeService> listTypeServices = new ArrayList<>();
		when(repository.findAll()).thenReturn(listTypeServices);
		assertThrows(ValidationException.class, () -> services.findAll());
	}
	
	@Test
	void findAllTest() {
		List<TypeService> listTypeServices = new ArrayList<>();
		listTypeServices.add(new TypeService());
		when(repository.findAll()).thenReturn(listTypeServices);
		assertTrue(services.findAll().isResult());
	}
	
	@Test
	void findByIdNullTest() {
		assertThrows(ValidationException.class, () -> services.findById(null));
	}
	
	@Test
	void findByIdZeroTest() {
		assertThrows(ValidationException.class, () -> services.findById(0L));
	}
	
	@Test
	void findByIdTypeServiceFoundElseTest() {
		when(repository.findTypeServiceById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.findById(1L));
	}
	
	@Test
	void findByIdTest() {
		when(repository.findTypeServiceById(Mockito.anyLong())).thenReturn(new TypeService());
		assertTrue(services.findById(1L).isResult());
	}
	
	@Test
	void findByTypeMatchElseTest() {
		assertThrows(ValidationException.class, () -> services.findByType("Hou"));
	}
	
	@Test
	void findByTypeServiceFoundElseTest() {
		when(repository.findTypeServiceByName(Mockito.anyString())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.findByType("House"));
	}
	
	@Test
	void findByTypeTest() {
		when(repository.findTypeServiceByName(Mockito.anyString())).thenReturn(new TypeService());
		assertTrue(services.findByType("House").isResult());
	}
	
	@Test
	void udateIDNullTest() {
		TypeService typeService = new TypeService(1L, "name", 5L, 3222L);		
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		assertThrows(ValidationException.class, () -> services.update(typeService, validResult, null));
	}
	
	@Test
	void udateIDZeroTest() {
		TypeService typeService = new TypeService(1L, "name", 5L, 3222L);		
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		assertThrows(ValidationException.class, () -> services.update(typeService, validResult, 0L));
	}
	
	@Test
	void udateValidationResultElseTest() {
		TypeService typeService = new TypeService(1L, "name", 5L, 3222L);		
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		assertThrows(ValidationException.class, () -> services.update(typeService, validResult, 1L));
	}
	
	@Test
	void udateValidResultElseTest() {
		TypeService typeService = new TypeService(1L, "Ho", 500L, 3L);		
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		assertThrows(ValidationException.class, () -> services.update(typeService, validResult, 1L));
	}
	
	@Test
	void udateTypeServiceNameFoundElseTest() {
		TypeService typeService = new TypeService(1L, "House", 500L, 3L);		
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		when(repository.findTypeServiceByName(Mockito.anyString())).thenReturn(typeService);
		when(repository.findTypeServiceById(Mockito.anyLong())).thenReturn(typeService);
		assertThrows(ValidationException.class, () -> services.update(typeService, validResult, 1L));
	}
	
	@Test
	void udateTypeServiceNameFoundElse2Test() {
		TypeService typeService = new TypeService(1L, "House", 500L, 3L);		
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		when(repository.findTypeServiceByName(Mockito.anyString())).thenReturn(typeService);
		when(repository.findTypeServiceById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.update(typeService, validResult, 1L));
	}
	
	@Test
	void udateTypeServiceFoundElseTest() {
		TypeService typeService = new TypeService(1L, "House", 500L, 3L);		
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		when(repository.findTypeServiceById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.update(typeService, validResult, 1L));
	}
	
	@Test
	void udateTest() {
		TypeService typeService = new TypeService(1L, "House", 500L, 3L);		
		BindingResult validResult = new BeanPropertyBindingResult(typeService, "");
		when(repository.findTypeServiceByName(Mockito.anyString())).thenReturn(null);
		when(repository.findTypeServiceById(Mockito.anyLong())).thenReturn(typeService);
		assertTrue(services.update(typeService, validResult, 1L).isResult());
	}
	
	@Test
	void deleteByIdNulTest() {
		assertThrows(ValidationException.class, () -> services.deleteById(null));
	}
	
	@Test
	void deleteByIdZeroTest() {
		assertThrows(ValidationException.class, () -> services.deleteById(0L));
	}
	
	@Test
	void deleteByIdTypeServiceFoundElseTest() {
		when(repository.findTypeServiceById(Mockito.anyLong())).thenReturn(null);
		assertThrows(ValidationException.class, () -> services.deleteById(1L));
	}
	
	@Test
	void deleteByITest() {
		when(repository.findTypeServiceById(Mockito.anyLong())).thenReturn(new TypeService());
		assertTrue(services.deleteById(1L).isResult());
	}
	

}
