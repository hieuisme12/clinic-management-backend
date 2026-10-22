package com.example.phongkhambackend.repository;

import com.example.phongkhambackend.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    // Lấy lịch sử khám bệnh của một bệnh nhân cụ thể
    List<MedicalRecord> findByPatientId(Long patientId);
}