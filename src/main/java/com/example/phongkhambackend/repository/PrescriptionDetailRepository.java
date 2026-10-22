package com.example.phongkhambackend.repository;

import com.example.phongkhambackend.model.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, Long> {
    List<PrescriptionDetail> findByMedicalRecordPatientIdOrderByMedicalRecordExamDateDesc(Long patientId);
}