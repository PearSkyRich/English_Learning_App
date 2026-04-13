package com.example.english_learning_app;

public class CertificateModel {
    private String id;
    private String Name; // Để trùng với tên field "Name" trên Firestore

    public CertificateModel() {}

    public CertificateModel(String id, String name) {
        this.id = id;
        this.Name = name;
    }

    public String getId() { return id; }
    public String getName() { return Name; }
}