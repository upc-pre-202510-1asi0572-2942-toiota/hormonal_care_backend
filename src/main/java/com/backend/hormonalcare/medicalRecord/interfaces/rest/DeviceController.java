package com.backend.hormonalcare.medicalRecord.interfaces.rest;

import com.backend.hormonalcare.medicalRecord.domain.model.aggregates.Patient;
import com.backend.hormonalcare.medicalRecord.domain.services.*;
import com.backend.hormonalcare.medicalRecord.infrastructure.persistence.jpa.repositories.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/patient", produces = "application/json")
public class DeviceController {

    @Autowired
    private GlucoseService glucoseService;

    @Autowired
    private InsulinService insulinService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private BlynkClient blynkClient;
    // Endpoint para registrar los niveles de glucosa de un paciente
    @PostMapping("/{patientId}/fetch-glucose-from-blynk")
    public ResponseEntity<Integer> fetchAndRecordGlucoseFromBlynk(@PathVariable Long patientId) {
        try {
            // Obtener nivel de glucosa desde Blynk
            int glucoseLevel = blynkClient.fetchGlucoseLevel();

            // Verificar existencia del paciente
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            // Crear GlucoseData con paciente y hora actual
            GlucoseData data = new GlucoseData();
            data.setPatient(patient);
            data.setGlucoseLevel(glucoseLevel);
            data.setTime(LocalTime.now());

            // Guardar en base de datos
            glucoseService.saveSingle(data);

            return new ResponseEntity<>( glucoseLevel , HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
    }


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
    @PostMapping("/{patientId}/auto-insulin")
    public ResponseEntity<Long> recordSingleInsulinEntry(
            @PathVariable Long patientId,
            @RequestBody Map<String, Object> body) {
        try {
            if (body == null || !body.containsKey("units")) {
                return new ResponseEntity<>(-1L, HttpStatus.BAD_REQUEST);
            }

            int units = Integer.parseInt(body.get("units").toString());

            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            InsulinData data = new InsulinData();
            data.setPatient(patient);
            data.setUnits(units);
            data.setTime(LocalTime.now());

            insulinService.saveSingle(data);

            return new ResponseEntity<>((long) units, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(-1L, HttpStatus.BAD_REQUEST);
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
