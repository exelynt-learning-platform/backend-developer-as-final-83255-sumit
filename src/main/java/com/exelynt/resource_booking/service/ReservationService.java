package com.exelynt.resource_booking.service;

import com.exelynt.resource_booking.dto.ReservationRequest;
import com.exelynt.resource_booking.dto.ReservationResponse;
import com.exelynt.resource_booking.entity.Reservation;
import com.exelynt.resource_booking.entity.ReservationStatus;
import com.exelynt.resource_booking.entity.Resource;
import com.exelynt.resource_booking.entity.User;
import com.exelynt.resource_booking.exception.ResourceNotFoundException;
import com.exelynt.resource_booking.repository.ReservationRepository;
import com.exelynt.resource_booking.repository.ResourceRepository;
import com.exelynt.resource_booking.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ResourceRepository resourceRepository,
            UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    public ReservationResponse createReservation(ReservationRequest request) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + username));

        Resource resource = resourceRepository.findById(
                        request.getResourceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found with id: "
                                        + request.getResourceId()));

        if (request.getStartTime() == null
                || request.getEndTime() == null
                || !request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        long days = ChronoUnit.DAYS.between(
                request.getStartTime().toLocalDate(),
                request.getEndTime().toLocalDate());

        if (days <= 0) {
            days = 1;
        }

        BigDecimal price = BigDecimal.valueOf(100)
                .multiply(BigDecimal.valueOf(days));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPrice(price);
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation saved = reservationRepository.save(reservation);

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReservationResponse> getAllReservations(
            Pageable pageable) {

        return reservationRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReservationResponse> getReservations(
            Long resourceId,
            Long userId,
            String status,
            Pageable pageable) {

        if (resourceId == null && userId == null && status == null) {
            return getAllReservations(pageable);
        }

        Specification<Reservation> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        if (resourceId != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(
                                    root.get("resource").get("id"),
                                    resourceId));
        }

        if (userId != null) {
            specification = specification.and(
                    ReservationRepository.belongsToUser(userId));
        }

        if (status != null && !status.isBlank()) {
            ReservationStatus reservationStatus;

            try {
                reservationStatus =
                        ReservationStatus.valueOf(
                                status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Invalid reservation status: " + status);
            }

            specification = specification.and(
                    ReservationRepository.hasStatus(
                            reservationStatus));
        }

        return reservationRepository.findAll(
                        specification, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reservation not found with id: " + id));

        return mapToResponse(reservation);
    }

    public ReservationResponse updateStatus(Long id, String status) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reservation not found with id: " + id));

        try {
            reservation.setStatus(
                    ReservationStatus.valueOf(
                            status.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status: " + status);
        }

        Reservation updated = reservationRepository.save(reservation);

        return mapToResponse(updated);
    }

    public void deleteReservation(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reservation not found with id: " + id));

        reservationRepository.delete(reservation);
    }

    private ReservationResponse mapToResponse(
            Reservation reservation) {

        ReservationResponse response = new ReservationResponse();

        response.setId(reservation.getId());

        if (reservation.getResource() != null) {
            response.setResourceId(
                    reservation.getResource().getId());
        }

        if (reservation.getUser() != null) {
            response.setUserId(
                    reservation.getUser().getId());
        }

        response.setStartTime(reservation.getStartTime());
        response.setEndTime(reservation.getEndTime());
        response.setPrice(reservation.getPrice());
        response.setStatus(reservation.getStatus());

        return response;
    }
}