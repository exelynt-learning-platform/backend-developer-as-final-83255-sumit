package com.exelynt.resource_booking.service;

import com.exelynt.resource_booking.dto.ResourceDto;
import com.exelynt.resource_booking.entity.Resource;
import com.exelynt.resource_booking.exception.ResourceNotFoundException;
import com.exelynt.resource_booking.repository.ResourceRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Transactional(readOnly = true)
    public Page<ResourceDto> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public ResourceDto getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource not found with id: " + id));

        return mapToDto(resource);
    }

    public ResourceDto createResource(ResourceDto resourceDto) {
        Resource resource = new Resource(
                resourceDto.getName(),
                resourceDto.getDescription(),
                resourceDto.getType()
        );

        Resource savedResource = resourceRepository.save(resource);

        return mapToDto(savedResource);
    }

    public ResourceDto updateResource(
            Long id,
            ResourceDto resourceDto) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource not found with id: " + id));

        resource.setName(resourceDto.getName());
        resource.setDescription(resourceDto.getDescription());
        resource.setType(resourceDto.getType());

        Resource updatedResource = resourceRepository.save(resource);

        return mapToDto(updatedResource);
    }

    public void deleteResource(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource not found with id: " + id));

        resourceRepository.delete(resource);
    }

    private ResourceDto mapToDto(Resource resource) {
        ResourceDto dto = new ResourceDto();

        dto.setId(resource.getId());
        dto.setName(resource.getName());
        dto.setDescription(resource.getDescription());
        dto.setType(resource.getType());

        return dto;
    }
}