package com.example.phongkhambackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "prescription_details")
@Data
public class PrescriptionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private Integer quantity; // Số lượng thuốc kê đơn

    @Column(nullable = false)
    private String dosage; // Liều dùng (Sáng 1 viên, tối 1 viên...)
}