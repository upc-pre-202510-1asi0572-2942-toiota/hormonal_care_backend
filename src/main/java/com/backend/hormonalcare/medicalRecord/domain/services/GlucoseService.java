package com.backend.hormonalcare.medicalRecord.domain.services;

import com.backend.hormonalcare.medicalRecord.domain.model.aggregates.Patient;
import com.backend.hormonalcare.medicalRecord.infrastructure.persistence.jpa.repositories.GlucoseDataRepository;
import com.backend.hormonalcare.medicalRecord.infrastructure.persistence.jpa.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GlucoseService {

    @Autowired
    private GlucoseDataRepository glucoseDataRepository;

    @Autowired
    private PatientRepository patientRepository;  // Inyectamos el repositorio del paciente

    // Guardar los datos de glucosa para un paciente
    public void saveGlucoseData(Long patientId, List<GlucoseData> glucoseDataList) {
        // Buscamos al paciente en la base de datos por su ID
        Optional<Patient> patient = patientRepository.findById(patientId);

        if (patient.isPresent()) {
            // Asociamos el paciente con cada uno de los registros de glucosa
            for (GlucoseData glucoseData : glucoseDataList) {
                glucoseData.setPatient(patient.get());  // Seteamos el paciente en los datos de glucosa
            }
            glucoseDataRepository.saveAll(glucoseDataList);  // Guardamos los datos
        } else {
            throw new RuntimeException("Paciente no encontrado con el ID: " + patientId);
        }
    }

    // Obtener datos de glucosa por paciente y fecha
    public List<GlucoseData> getGlucoseDataForPatientAndDate(Long patientId, LocalDate date) {
        // Buscamos al paciente por ID
        Optional<Patient> patient = patientRepository.findById(patientId);

        if (patient.isPresent()) {
            // Aquí puedes filtrar por fecha si es necesario
            return glucoseDataRepository.findByPatientId(patientId);  // Devolver los datos de glucosa de ese paciente
        } else {
            throw new RuntimeException("Paciente no encontrado con el ID: " + patientId);
        }
    }

    // Obtener el último nivel de glucosa de un paciente
    public int getLatestGlucoseLevel(Long patientId) {
        // Buscamos al paciente por ID
        Optional<Patient> patient = patientRepository.findById(patientId);

        if (patient.isPresent()) {
            // Aquí obtenemos el último nivel de glucosa
            List<GlucoseData> data = glucoseDataRepository.findByPatientId(patientId);
            if (!data.isEmpty()) {
                return data.get(data.size() - 1).getGlucoseLevel();
            }
            return -1;  // Si no hay datos, se devuelve -1
        } else {
            throw new RuntimeException("Paciente no encontrado con el ID: " + patientId);
        }
    }

    public void saveSingle(GlucoseData data) {
        glucoseDataRepository.save(data);
    }
}