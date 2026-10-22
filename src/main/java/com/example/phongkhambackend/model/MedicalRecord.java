package com.example.phongkhambackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "medical_records")
@Data
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String diagnosis; // Chuẩn đoán bệnh

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Column(columnDefinition = "TEXT")
    private String note;
}