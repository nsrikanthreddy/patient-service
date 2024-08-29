package com.hackerearth.fullstack.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.hackerearth.fullstack.backend.controller.PatientController;
import com.hackerearth.fullstack.backend.dto.DoctorDTO;
import com.hackerearth.fullstack.backend.model.Patient;
import com.hackerearth.fullstack.backend.repository.PatientRepository;

@SpringBootTest
class sampleTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPatient_ValidPatient_ReturnsCreated() {
        // Arrange
        DoctorDTO doctorDTO = new DoctorDTO(1L, "John Doe", "Cardiologist", "Apollo Hospitals", "8885551112");
        Patient patient = new Patient("Jane Smith", 22, "8881112223", doctorDTO.getId());
        when(restTemplate.getForObject(anyString(), eq(DoctorDTO.class))).thenReturn(doctorDTO);
        when(patientRepository.save(any())).thenReturn(patient);

        // Act
        ResponseEntity<Patient> response = patientController.createPatient(patient);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(patient, response.getBody());
        assertEquals(patient.getName(), response.getBody().getName());
        assertEquals(patient.getEmergencyContactPhone(), response.getBody().getEmergencyContactPhone());
        assertEquals(patient.getDoctorId(), response.getBody().getDoctorId());
    }

    @Test
    void getAllPatients_ReturnsListOfPatients() {
        // Arrange
        DoctorDTO doctorDTO1 = new DoctorDTO(1L, "John Doe", "Cardiologist", "Apollo Hospitals", "8885551112");
        DoctorDTO doctorDTO2 = new DoctorDTO(2L, "John Doe", "Cardiologist", "Apollo Hospitals", "8885551112");
        List<Patient> patientList = new ArrayList<>();
        patientList.add(new Patient("John Doe", 30, "1234567890", doctorDTO1.getId()));
        patientList.add(new Patient("Jane Smith", 25, "9876543210", doctorDTO2.getId()));
        when(patientRepository.findAll()).thenReturn(patientList);

        // Act
        ResponseEntity<List<Patient>> response = patientController.getAllPatients();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patientList, response.getBody());
        assertEquals(patientList.size(), response.getBody().size());
    }
}