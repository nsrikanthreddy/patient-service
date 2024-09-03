package com.hackerearth.fullstack.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.hackerearth.fullstack.backend.dto.DoctorDTO;
import com.hackerearth.fullstack.backend.model.Patient;
import com.hackerearth.fullstack.backend.repository.PatientRepository;

import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.servlet.http.HttpServletRequest;



@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = {"Access-Control-Allow-Origin"}, methods  = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class PatientController {

    Logger logger= LoggerFactory.getLogger(PatientController.class);
	
    private static final String DOCTOR_SERVICE_URL = "http://localhost:9090/api/doctors/";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PatientRepository patientRepository;

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        
    	
    	Long doctorId = patient.getDoctorId();
    	try {

		
		/*String SystemName = InetAddress.getLocalHost().getHostName();
		logger.info("in createPatient & System Name : ------>"+ SystemName);*/
		 
    		DoctorDTO doctor = restTemplate.getForObject(DOCTOR_SERVICE_URL+doctorId,DoctorDTO.class);
        	
        	Patient savedPatient = null;
		
        	if(doctor != null) {
        		savedPatient = patientRepository.save(patient);
            	
                return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
        	}else {
        		throw new IllegalArgumentException("Doctor with ID "+doctorId+" not found.");
        	}
		} catch (HttpClientErrorException.NotFound e) {
			throw new IllegalArgumentException("Doctor with ID "+doctorId+" not found.");
			
		}
    		
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
       
       
    	Optional<Patient> patient = patientRepository.findById(id);
    	if(patient.isPresent())
    		return new ResponseEntity<>(patient.get(), HttpStatus.OK);
    	else
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient updatedPatient) {
        
    	
    	Optional<Patient> patient = patientRepository.findById(id);
    	if(patient.isEmpty())
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	
    	Long doctorId = updatedPatient.getDoctorId();
    	try {
    		DoctorDTO doctor = restTemplate.getForObject(DOCTOR_SERVICE_URL+doctorId, DoctorDTO.class);
        	if(doctor!=null && doctor.getId() == updatedPatient.getDoctorId()){
        		Patient existedPatientObj=patient.get();
        		existedPatientObj.setName(updatedPatient.getName());
        		existedPatientObj.setAge(updatedPatient.getAge());
        		existedPatientObj.setDoctorId(updatedPatient.getDoctorId());
        		existedPatientObj.setEmergencyContactPhone(updatedPatient.getEmergencyContactPhone());
        		Patient modifiedPatient = patientRepository.save(existedPatientObj);
        		
        		return new ResponseEntity<>(modifiedPatient, HttpStatus.OK);
        	
        	}else {
        		throw new IllegalArgumentException("Doctor with ID "+doctorId+" not found.");
        	}
        	
		} catch (HttpClientErrorException.NotFound e) {
			throw new IllegalArgumentException("Doctor with ID "+doctorId+" not found.");
		}
    	
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
       
        
    	Optional<Patient> patient = patientRepository.findById(id);
    	if(!patient.isPresent())
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	
    	patientRepository.deleteById(id);
    	
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
       
    	return new ResponseEntity<>(patientRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/doctor/id/{doctorId}")
    public ResponseEntity<List<Patient>> getPatientsByDoctorId(@PathVariable Long doctorId) {
        
    	List<Patient> patientsList = patientRepository.findByDoctorId(doctorId);
        return new ResponseEntity<>(patientsList, HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorName}")
    public ResponseEntity<List<Patient>> getPatientsByDoctorName(@PathVariable String doctorName) {
	try {
		String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String SystemName = InetAddress.getLocalHost().getHostName();
		logger.info("in getPatientsByDoctorName & System Name : ------>"+ SystemName);
		logger.info("in getPatientsByDoctorName & baseUrl : ------>"+ baseUrl);
	 }
        catch (Exception E) {
            System.err.println(E.getMessage());
        }
    	
    	DoctorDTO[] doctors = restTemplate.getForObject(DOCTOR_SERVICE_URL+"searchByName?name="+doctorName, DoctorDTO[].class);
    	
    	if(doctors ==null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}else {
    		List<Patient> patientsList= new ArrayList<>();
    		Stream.of(doctors).forEach(doctor ->{
    			patientsList.addAll(patientRepository.findByDoctorId(doctor.getId()));
    		});
    		return new ResponseEntity<>(patientsList, HttpStatus.OK);
    	}
    	
    	
    }
    
    @GetMapping("/searchByName/{patientName}")
    public ResponseEntity<List<Patient>> getPatientsByName(@PathVariable String patientName) {
        
    	
    		List<Patient> patientsList= patientRepository.findByNameContaining(patientName);
    		
    		return new ResponseEntity<>(patientsList, HttpStatus.OK);
    	
    	
    }

}
