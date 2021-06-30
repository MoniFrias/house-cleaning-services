package com.example.services.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.services.entity.TypeServices;

@Repository
public interface RepositoryServices extends JpaRepository<TypeServices, Long>{

}
