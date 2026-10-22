package com.example.phongkhambackend.model;

public enum MedicineUnit {
    VIEN("Viên"),
    VI("Vỉ"),
    HOP("Hộp"),
    CHAI("Chai"),
    GOI("Gói"),
    LO("Lọ"),
    TUYP("Tuýp"),
    ONG("Ống");

    private final String label;

    MedicineUnit(String label) {
        this.label = label;
    }

    public String getCode() {
        return name();
    }

    public String getLabel() {
        return label;
    }
}
