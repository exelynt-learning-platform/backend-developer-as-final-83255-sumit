package com.exelynt.resource_booking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private boolean available = true;

    public Resource() {}

    public Resource(String name, String type, boolean available) {
        this.name = name;
        this.type = type;
        this.available = available;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}