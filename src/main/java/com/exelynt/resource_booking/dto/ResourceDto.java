package com.exelynt.resource_booking.dto;

import jakarta.validation.constraints.NotBlank;

public class ResourceDto {
    private Long id;

    @NotBlank(message = "Resource name is required")
    private String name;

    private String description;

    @NotBlank(message = "Resource type is required")
    private String type;

    public ResourceDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}