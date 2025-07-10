package com.backend.hormonalcare.medicalRecord.domain.services;

import com.backend.hormonalcare.medicalRecord.domain.model.aggregates.Patient;
import jakarta.persistence.*;

import java.time.LocalTime;


@Entity
@Table(name = "glucose_data")
public class GlucoseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;  // Relación con paciente

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "glucose_level", nullable = false)
    private int glucoseLevel;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getGlucoseLevel() {
        return glucoseLevel;
    }

    public void setGlucoseLevel(int glucoseLevel) {
        this.glucoseLevel = glucoseLevel;
    }
}