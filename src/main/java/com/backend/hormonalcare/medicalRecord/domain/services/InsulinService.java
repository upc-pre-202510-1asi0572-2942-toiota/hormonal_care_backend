package com.backend.hormonalcare.medicalRecord.domain.services;


import com.backend.hormonalcare.medicalRecord.domain.model.aggregates.Patient;
import com.backend.hormonalcare.medicalRecord.infrastructure.persistence.jpa.repositories.InsulinDataRepository;
import com.backend.hormonalcare.medicalRecord.infrastructure.persistence.jpa.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InsulinService {

    @Autowired
    private InsulinDataRepository insulinDataRepository;

    @Autowired
    private PatientRepository patientRepository;  // Inyectamos el repositorio del paciente

    // Guardar datos de insulina para un paciente
    public void saveInsulinData(Long patientId, List<InsulinData> insulinDataList) {
        // Buscamos al paciente en la base de datos por su ID
        Optional<Patient> patient = patientRepository.findById(patientId);

        if (patient.isPresent()) {
            // Asociamos el paciente con cada uno de los registros de insulina
            for (InsulinData insulinData : insulinDataList) {
                insulinData.setPatient(patient.get());  // Seteamos el paciente en los datos de insulina
            }
            insulinDataRepository.saveAll(insulinDataList);  // Guardamos los datos
        } else {
            throw new RuntimeException("Paciente no encontrado con el ID: " + patientId);
        }
    }

    // Trigger de insulina de emergencia
    public void triggerEmergencyInsulin(Long patientId) {
        System.out.println("Emergency insulin triggered for patient with ID: " + patientId);
        // Aquí implementas la lógica para el manejo de emergencia
    }
}