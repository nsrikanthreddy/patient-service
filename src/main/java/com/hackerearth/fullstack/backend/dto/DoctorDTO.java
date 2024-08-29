package com.hackerearth.fullstack.backend.dto;

public class DoctorDTO {

    private Long id;
    private String name;
    private String specialty;
    private String hospitalAffiliation;
    private String contactPhone;

    public DoctorDTO() {
    }

    public DoctorDTO(Long id, String name, String specialty, String hospitalAffiliation, String contactPhone) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.hospitalAffiliation = hospitalAffiliation;
        this.contactPhone = contactPhone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getHospitalAffiliation() {
        return hospitalAffiliation;
    }

    public void setHospitalAffiliation(String hospitalAffiliation) {
        this.hospitalAffiliation = hospitalAffiliation;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

}
