package com.exelynt.resource_booking.controller;

import com.exelynt.resource_booking.dto.ReservationRequest;
import com.exelynt.resource_booking.entity.*;
import com.exelynt.resource_booking.repository.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;

    public ReservationController(ReservationRepository reservationRepository, ResourceRepository resourceRepository) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest request, @AuthenticationPrincipal User currentUser) {
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        if (!resource.isAvailable()) {
            return ResponseEntity.badRequest().body("Resource is not available");
        }

        Reservation reservation = new Reservation(
                currentUser,
                resource,
                request.getStartTime(),
                request.getEndTime(),
                request.getPrice(),
                ReservationStatus.PENDING
        );

        return ResponseEntity.ok(reservationRepository.save(reservation));
    }

    @GetMapping
    public ResponseEntity<Page<Reservation>> getReservations(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @AuthenticationPrincipal User currentUser) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Specification<Reservation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (currentUser.getRole() == Role.ROLE_USER) {
                predicates.add(cb.equal(root.get("user").get("id"), currentUser.getId()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return ResponseEntity.ok(reservationRepository.findAll(spec, pageable));
    }
}