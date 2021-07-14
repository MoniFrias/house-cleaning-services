package com.example.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.employee.entity.Appointment;

@Repository
public interface RepositoryAppointment extends JpaRepository<Appointment, Long>{

	List<Appointment> findAppointmentByIdEmployee(Long id);

}
