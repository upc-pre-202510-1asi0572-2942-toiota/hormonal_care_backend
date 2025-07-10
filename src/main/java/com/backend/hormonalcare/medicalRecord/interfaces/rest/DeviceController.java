package com.backend.hormonalcare.medicalRecord.interfaces.rest;

import com.backend.hormonalcare.medicalRecord.domain.services.GlucoseData;
import com.backend.hormonalcare.medicalRecord.domain.services.GlucoseService;
import com.backend.hormonalcare.medicalRecord.domain.services.InsulinData;
import com.backend.hormonalcare.medicalRecord.domain.services.InsulinService;
import com.backend.hormonalcare.medicalRecord.infrastructure.persistence.jpa.repositories.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/patient", produces = "application/json")
public class DeviceController {

    @Autowired
    private GlucoseService glucoseService;

    @Autowired
    private InsulinService insulinService;

    @Autowired
    private PatientRepository patientRepository;

    // Endpoint para registrar los niveles de glucosa de un paciente
    @PostMapping("/{patientId}/glucose")
    public ResponseEntity<String> recordGlucoseData(
            @PathVariable Long patientId,
            @RequestBody List<GlucoseData> glucoseDataList) {

        try {
            glucoseService.saveGlucoseData(patientId, glucoseDataList);  // Llamada al servicio para guardar los datos de glucosa
            return new ResponseEntity<>("Glucose data saved successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error saving glucose data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para registrar los datos de insulina de un paciente
    @PostMapping("/{patientId}/insulin")
    public ResponseEntity<String> recordInsulinData(
            @PathVariable Long patientId,
            @RequestBody List<InsulinData> insulinDataList) {

        try {
            insulinService.saveInsulinData(patientId, insulinDataList);  // Llamada al servicio para guardar los datos de insulina
            return new ResponseEntity<>("Insulin data saved successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error saving insulin data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para obtener los datos de glucosa de un paciente para una fecha específica
    @GetMapping("/{patientId}/glucose/{date}")
    public ResponseEntity<List<GlucoseData>> getGlucoseDataForDate(
            @PathVariable Long patientId,
            @PathVariable String date) {

        try {
            LocalDate localDate = LocalDate.parse(date);  // Convertir la fecha de String a LocalDate
            List<GlucoseData> glucoseDataList = glucoseService.getGlucoseDataForPatientAndDate(patientId, localDate);
            return new ResponseEntity<>(glucoseDataList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para monitorizar el nivel de glucosa y activar insulina de emergencia si es necesario
    @GetMapping("/{patientId}/monitor-glucose")
    public ResponseEntity<String> monitorGlucoseLevels(@PathVariable Long patientId) {
        try {
            int latestGlucoseLevel = glucoseService.getLatestGlucoseLevel(patientId);

            // Umbral crítico de glucosa (por ejemplo, 200 mg/dL)
            if (latestGlucoseLevel > 200) {
                insulinService.triggerEmergencyInsulin(patientId);  // Disparar la insulina de emergencia
                return new ResponseEntity<>("Emergency insulin triggered. Glucose level is too high!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Glucose levels are normal. No action needed.", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error monitoring glucose levels: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
