package com.example.houseCleaning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.houseCleaning.entity.BookService;

@Repository
public interface RespositoryBook extends JpaRepository<BookService, Long>{

}
