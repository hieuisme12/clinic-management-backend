package com.example.phongkhambackend.controller;

import com.example.phongkhambackend.dto.ApiResponse;
import com.example.phongkhambackend.model.*;
import com.example.phongkhambackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClinicController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private PrescriptionDetailRepository prescriptionDetailRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Authen
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(@RequestBody User loginReq) {
        try {
            if (loginReq.getUsername() == null || loginReq.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Tên đăng nhập và mật khẩu không được để trống", null));
            }

            Optional<User> userOpt = userRepository.findByUsername(loginReq.getUsername());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (passwordEncoder.matches(loginReq.getPassword(), user.getPassword())) {
                    return ResponseEntity.ok(new ApiResponse<>(200, "Đăng nhập thành công", user));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(401, "Tài khoản hoặc mật khẩu không chính xác", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Lỗi máy chủ khi đăng nhập: " + e.getMessage(), null));
        }
    }
    // 2. Patients
    @GetMapping("/patients")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Patient>>> getAllPatients() {
        try {
            List<Patient> patients = patientRepository.findAll();
            return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách bệnh nhân thành công", patients));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Lỗi khi lấy danh sách bệnh nhân: " + e.getMessage(), null));
        }
    }

    @GetMapping("/patients/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Patient>> getPatientById(@PathVariable Long id) {
        try {
            Optional<Patient> patientOpt = patientRepository.findById(id);
            if (patientOpt.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(200, "Tìm thấy thông tin bệnh nhân", patientOpt.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Không tìm thấy bệnh nhân với ID: " + id, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Lỗi server: " + e.getMessage(), null));
        }
    }

    @PostMapping("/patients")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Patient>> createPatient(@RequestBody Patient patient) {
        try {
            if (patient.getFullName() == null || patient.getFullName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Họ tên bệnh nhân không được để trống", null));
            }
            Patient savedPatient = patientRepository.save(patient);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(201, "Thêm bệnh nhân mới thành công", savedPatient));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Lỗi khi lưu bệnh nhân: " + e.getMessage(), null));
        }
    }

    // ==========================================
    // 3. QUẢN LÝ THUỐC (MEDICINES)
    // ==========================================
    @GetMapping("/medicines")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Medicine>>> getMedicines(@RequestParam(required = false) String search) {
        try {
            List<Medicine> medicines;
            if (search != null && !search.trim().isEmpty()) {
                medicines = medicineRepository.findByNameContainingIgnoreCase(search.trim());
            } else {
                medicines = medicineRepository.findAll();
            }
            return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách thuốc thành công", medicines));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Lỗi khi tra cứu thuốc: " + e.getMessage(), null));
        }
    }

    @GetMapping("/medicine-units")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MedicineUnit>>> getMedicineUnits() {
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh mục đơn vị thuốc thành công",
                Arrays.asList(MedicineUnit.values())));
    }

    // ==========================================
    // 4. HỒ SƠ KHÁM BỆNH & LỊCH SỬ (MEDICAL RECORDS)
    // ==========================================
    @GetMapping("/patients/{patientId}/medical-records")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getMedicalRecordsByPatient(@PathVariable Long patientId) {
        try {
            boolean exists = patientRepository.existsById(patientId);
            if (!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "Bệnh nhân không tồn tại trên hệ thống", null));
            }
            List<MedicalRecord> records = medicalRecordRepository.findByPatientId(patientId);
            return ResponseEntity.ok(new ApiResponse<>(200, "Lấy lịch sử khám bệnh thành công", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Lỗi khi lấy lịch sử khám bệnh: " + e.getMessage(), null));
        }
    }

    @PostMapping("/medical-records")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<MedicalRecord>> createMedicalRecord(@RequestBody MedicalRecord record) {
        try {
            if (record.getPatient() == null || record.getPatient().getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Thông tin bệnh nhân không hợp lệ", null));
            }
            MedicalRecord savedRecord = medicalRecordRepository.save(record);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(201, "Lưu hồ sơ khám bệnh thành công", savedRecord));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Lỗi khi lưu hồ sơ khám: " + e.getMessage(), null));
        }
    }
    // 5. Prescriptions
    @PostMapping("/prescriptions")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PrescriptionDetail>> createPrescription(
            @RequestBody PrescriptionDetail prescription) {
        try {
            if (prescription.getMedicalRecord() == null || prescription.getMedicalRecord().getId() == null
                    || prescription.getMedicine() == null || prescription.getMedicine().getId() == null
                    || prescription.getQuantity() == null || prescription.getQuantity() <= 0
                    || prescription.getDosage() == null || prescription.getDosage().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Thông tin đơn thuốc không hợp lệ", null));
            }
            if (!medicalRecordRepository.existsById(prescription.getMedicalRecord().getId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "Không tìm thấy hồ sơ khám bệnh", null));
            }
            if (!medicineRepository.existsById(prescription.getMedicine().getId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "Không tìm thấy thuốc", null));
            }
            PrescriptionDetail savedPrescription = prescriptionDetailRepository.save(prescription);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(201, "Lưu đơn thuốc thành công", savedPrescription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Lỗi khi lưu đơn thuốc: " + e.getMessage(), null));
        }
    }

    @GetMapping("/patients/{patientId}/prescriptions")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PrescriptionDetail>>> getPatientPrescriptions(
            @PathVariable Long patientId) {
        try {
            if (!patientRepository.existsById(patientId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "Bệnh nhân không tồn tại trên hệ thống", null));
            }
            List<PrescriptionDetail> prescriptions =
                    prescriptionDetailRepository.findByMedicalRecordPatientIdOrderByMedicalRecordExamDateDesc(patientId);
            return ResponseEntity.ok(new ApiResponse<>(200, "Lấy lịch sử thuốc đã cấp thành công", prescriptions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Lỗi khi lấy lịch sử thuốc đã cấp: " + e.getMessage(), null));
        }
    }

    // 6. Admin
    @DeleteMapping("/admin/patients/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Long id) {
        if (!patientRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Không tìm thấy bệnh nhân với ID: " + id, null));
        }
        patientRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Xóa bệnh nhân thành công", null));
    }

    @PostMapping("/admin/medicines")
    public ResponseEntity<ApiResponse<Medicine>> createMedicine(@RequestBody Medicine medicine) {
        if (medicine.getName() == null || medicine.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Tên thuốc không được để trống", null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Thêm thuốc thành công", medicineRepository.save(medicine)));
    }

    @PutMapping("/admin/medicines/{id}")
    public ResponseEntity<ApiResponse<Medicine>> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicine) {
        Optional<Medicine> existing = medicineRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Không tìm thấy thuốc với ID: " + id, null));
        }
        medicine.setId(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật thuốc thành công", medicineRepository.save(medicine)));
    }

    @DeleteMapping("/admin/medicines/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMedicine(@PathVariable Long id) {
        if (!medicineRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Không tìm thấy thuốc với ID: " + id, null));
        }
        medicineRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Xóa thuốc thành công", null));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách người dùng thành công", userRepository.findAll()));
    }

    @PostMapping("/admin/users")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()
                || user.getPassword() == null || user.getPassword().isEmpty()
                || user.getRole() == null || user.getRole().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, "Username, password và role không được để trống", null));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole().trim().toUpperCase());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Tạo người dùng thành công", userRepository.save(user)));
    }
}