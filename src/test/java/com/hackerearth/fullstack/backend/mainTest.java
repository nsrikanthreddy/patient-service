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
class mainTest {

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
    void createPatient_InValidPatient_ThrowsIllegalArgumentException() {
        // Arrange
        Patient patient = new Patient("Jane Smith", 22, "8881112223", 1L);
        when(restTemplate.getForObject(anyString(), eq(DoctorDTO.class))).thenReturn(null);
        when(patientRepository.save(any())).thenReturn(patient);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> patientController.createPatient(patient));
        verify(patientRepository, never()).save(any(Patient.class));
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

    @Test
    void getPatientById_ValidPatientId_ReturnsPatient() {
        // Arrange
        DoctorDTO doctorDTO = new DoctorDTO(1L, "John Doe", "Cardiologist", "Apollo Hospitals", "8885551112");
        Patient patient = new Patient("John Doe", 30, "1234567890", doctorDTO.getId());
        Long patientId = patient.getId();
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        // Act
        ResponseEntity<Patient> response = patientController.getPatientById(patientId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patient, response.getBody());
    }

    @Test
    void getPatientById_InvalidPatientId_ReturnsNotFound() {
        // Arrange
        Long patientId = -999L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Patient> response = patientController.getPatientById(patientId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updatePatient_ValidPatient_ReturnsUpdatedPatient() throws ParseException {
        // Arrange
        DoctorDTO doctorDTO = new DoctorDTO(1L, "John Doe", "Cardiologist", "Apollo Hospitals", "8885551112");
        Patient patient = new Patient("John Doe", 30, "1234567890", doctorDTO.getId());
        Patient updatedPatient = new Patient("Jane Doe", 15, "0987654321", doctorDTO.getId());
        Long patientId = patient.getId();

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(restTemplate.getForObject(anyString(), eq(DoctorDTO.class))).thenReturn(doctorDTO);
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        // Act
        ResponseEntity<Patient> response = patientController.updatePatient(patientId, updatedPatient);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedPatient.getName(), response.getBody().getName());
        assertEquals(updatedPatient.getAge(), response.getBody().getAge());
        assertEquals(updatedPatient.getEmergencyContactPhone(), response.getBody().getEmergencyContactPhone());
        assertEquals(updatedPatient.getDoctorId(), response.getBody().getDoctorId());

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void updatePatient_ValidPatient_ThrowsIllegalArgumentException() throws ParseException {
        // Arrange
        DoctorDTO doctorDTO = new DoctorDTO(1L, "John Doe", "Cardiologist", "Apollo Hospitals", "8885551112");
        Patient patient = new Patient("John Doe", 30, "1234567890", doctorDTO.getId());
        Patient updatedPatient = new Patient("Jane Doe", 15, "0987654321", 2L);

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

        assertThrows(IllegalArgumentException.class,
                () -> patientController.updatePatient(patient.getId(), updatedPatient));
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void deletePatientById_ValidPatientId_DeletesPatient() {
        // Arrange
        DoctorDTO doctorDTO = new DoctorDTO(1L, "John Doe", "Cardiologist", "Apollo Hospitals", "8885551112");
        Patient patient = new Patient("John Doe", 30, "1234567890", doctorDTO.getId());
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        doNothing().when(patientRepository).deleteById(patient.getId());
        Long patientId = patient.getId();

        // Act
        ResponseEntity<Void> response = patientController.deletePatient(patientId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(patientRepository, times(1)).deleteById(patientId);
    }

    @Test
    void getPatientsByDoctorId_ValidDoctorId_ReturnsPatients() {
        // Arrange
        DoctorDTO doctorDTO = new DoctorDTO(1L, "John Doe", "Cardiologist", "Apollo Hospitals", "8885551112");
        Long doctorId = doctorDTO.getId();
        List<Patient> patients = new ArrayList<>();
        patients.add(new Patient("John Doe", 30, "1234567890", doctorId));
        patients.add(new Patient("Jane Doe", 25, "0987654321", doctorId));
        when(patientRepository.findByDoctorId(doctorId)).thenReturn(patients);

        // Act
        ResponseEntity<List<Patient>> response = patientController.getPatientsByDoctorId(doctorId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patients.size(), response.getBody().size());
        assertEquals(patients.get(0).getName(), response.getBody().get(0).getName());
    }

    @SuppressWarnings("unchecked")
    @Test
    void getPatientsByDoctorName_ValidDoctorName_ReturnsPatients() {
        // Arrange
        String doctorName = "John";
        DoctorDTO[] doctorDTOs = { new DoctorDTO(1L, "John Doe", "Cardiology", "Hospital A", "1234567890") };
        when(restTemplate.getForObject(any(String.class), any(Class.class))).thenReturn(doctorDTOs);
        List<Patient> patients = new ArrayList<>();
        Long doctorId = doctorDTOs[0].getId();
        patients.add(new Patient("John Doe", 30, "1234567890", doctorId));
        patients.add(new Patient("Jane Doe", 25, "0987654321", doctorId));
        when(patientRepository.findByDoctorId(doctorId)).thenReturn(patients);

        // Act
        ResponseEntity<List<Patient>> response = patientController.getPatientsByDoctorName(doctorName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patients.size(), response.getBody().size());
    }
}