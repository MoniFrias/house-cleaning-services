package com.example.services.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.services.entity.TypeServices;

@Repository
public interface RepositoryServices extends JpaRepository<TypeServices, Long>{

	TypeServices findTypeServiceByType(String type);

	List<TypeServices> findTypeServicesByType(String type);

	TypeServices findTypeServiceById(Long id);


}
