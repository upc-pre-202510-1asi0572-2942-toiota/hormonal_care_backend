package com.backend.hormonalcare.medicalRecord.infrastructure.persistence.jpa.repositories;

import com.backend.hormonalcare.medicalRecord.domain.services.GlucoseData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlucoseDataRepository extends JpaRepository<GlucoseData, Long> {
    List<GlucoseData> findByPatientId(Long patientId);
}