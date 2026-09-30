package com.exelynt.resource_booking.controller;

import com.exelynt.resource_booking.dto.ResourceDto;
import com.exelynt.resource_booking.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public ResponseEntity<Page<ResourceDto>> getAllResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        return ResponseEntity.ok(resourceService.getAllResources(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDto> getResourceById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.getResourceById(id));
    }

    @PostMapping
    public ResponseEntity<ResourceDto> createResource(@Valid @RequestBody ResourceDto resourceDto) {
        return new ResponseEntity<>(resourceService.createResource(resourceDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceDto> updateResource(@PathVariable Long id, @Valid @RequestBody ResourceDto resourceDto) {
        return ResponseEntity.ok(resourceService.updateResource(id, resourceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}