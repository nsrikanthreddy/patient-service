package com.hackerearth.fullstack.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hackerearth.fullstack.backend.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByDoctorId(Long id);

    List<Patient> findByDoctorId(Long doctorId);
    
    List<Patient> findByNameContaining(String name);
}