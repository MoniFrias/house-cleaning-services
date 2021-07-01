package com.example.typeService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.typeService.entity.TypeService;

@Repository
public interface RepositoryTypeService extends JpaRepository<TypeService, Long>{

	TypeService findTypeServiceByName(String name);

	TypeService findTypeServiceById(Long id);

	
}
