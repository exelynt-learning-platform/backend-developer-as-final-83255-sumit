package com.exelynt.resource_booking.service;

import com.exelynt.resource_booking.dto.ReservationRequest;
import com.exelynt.resource_booking.dto.ReservationResponse;
import com.exelynt.resource_booking.entity.*;
import com.exelynt.resource_booking.exception.ResourceNotFoundException;
import com.exelynt.resource_booking.exception.UnauthorizedAccessException;
import com.exelynt.resource_booking.repository.ReservationRepository;
import com.exelynt.resource_booking.repository.ResourceRepository;
import com.exelynt.resource_booking.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ResourceRepository resourceRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        Reservation reservation = new Reservation();
        reservation.setUser(currentUser);
        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPrice(request.getPrice());
        reservation.setStatus(ReservationStatus.PENDING);

        return mapToResponse(reservationRepository.save(reservation));
    }

    public Page<ReservationResponse> getReservations(ReservationStatus status,
                                                     BigDecimal minPrice,
                                                     BigDecimal maxPrice,
                                                     Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Specification<Reservation> spec = Specification.where(ReservationRepository.hasStatus(status))
                .and(ReservationRepository.hasPriceGte(minPrice))
                .and(ReservationRepository.hasPriceLte(maxPrice));

        if (!isAdmin) {
            User currentUser = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
            spec = spec.and(ReservationRepository.belongsToUser(currentUser.getId()));
        }

        return reservationRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        validateOwnershipOrAdmin(reservation);
        return mapToResponse(reservation);
    }

    @Transactional
    public ReservationResponse updateReservation(Long id, ReservationRequest request) {
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        validateOwnershipOrAdmin(reservation);

        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPrice(request.getPrice());

        return mapToResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse updateStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Regular users can only cancel their own reservations; admins can set any status
        if (!isAdmin) {
            validateOwnershipOrAdmin(reservation);
            if (status != ReservationStatus.CANCELLED) {
                throw new UnauthorizedAccessException("Users can only update reservation status to CANCELLED");
            }
        }

        reservation.setStatus(status);
        return mapToResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        validateOwnershipOrAdmin(reservation);
        reservationRepository.delete(reservation);
    }

    private void validateOwnershipOrAdmin(Reservation reservation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !reservation.getUser().getUsername().equals(auth.getName())) {
            throw new UnauthorizedAccessException("You do not have permission to access or modify this reservation");
        }
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        ReservationResponse res = new ReservationResponse();
        res.setId(reservation.getId());
        res.setResourceId(reservation.getResource().getId());
        res.setUserId(reservation.getUser().getId());
        res.setStartTime(reservation.getStartTime());
        res.setEndTime(reservation.getEndTime());
        res.setPrice(reservation.getPrice());
        res.setStatus(reservation.getStatus());
        return res;
    }
}