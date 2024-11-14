package com.backend.hormonalcare.medicalRecord.domain.model.queries;

import com.backend.hormonalcare.medicalRecord.domain.model.aggregates.MedicalRecord;

public record GetPrescriptionByMedicalRecordIdQuery(Long medicalRecordId) {
}
