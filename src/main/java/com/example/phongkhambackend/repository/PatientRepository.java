package com.example.phongkhambackend.repository;

import com.example.phongkhambackend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Tìm kiếm bệnh nhân theo tên (không phân biệt hoa thường)
    List<Patient> findByFullNameContainingIgnoreCase(String name);
}