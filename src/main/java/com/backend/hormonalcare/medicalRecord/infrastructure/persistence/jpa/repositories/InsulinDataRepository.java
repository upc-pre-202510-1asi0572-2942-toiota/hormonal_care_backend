package com.backend.hormonalcare.medicalRecord.infrastructure.persistence.jpa.repositories;
import com.backend.hormonalcare.medicalRecord.domain.services.InsulinData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsulinDataRepository extends JpaRepository<InsulinData, Long> {
    List<InsulinData> findByPatientId(Long patientId);
}