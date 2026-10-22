package com.example.phongkhambackend.repository;

import com.example.phongkhambackend.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    // Tìm kiếm thuốc theo tên (dùng cho thanh Search của bác sĩ)
    List<Medicine> findByNameContainingIgnoreCase(String name);
}