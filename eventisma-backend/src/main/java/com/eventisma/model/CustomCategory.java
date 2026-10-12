package com.eventisma.model;

import jakarta.persistence.*;

@Entity
@Table(name = "custom_categories")
public class CustomCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    public CustomCategory() {
    }

    public CustomCategory(String name) {
        this.name = name;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
