package com.exelynt.resource_booking.controller;

import com.exelynt.resource_booking.entity.Resource;
import com.exelynt.resource_booking.repository.ResourceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceRepository resourceRepository;

    public ResourceController(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @GetMapping
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @PostMapping
    public Resource createResource(@RequestBody Resource resource) {
        return resourceRepository.save(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Long id) {
        return resourceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        if (resourceRepository.existsById(id)) {
            resourceRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}