package com.exelynt.resource_booking.service;

import com.exelynt.resource_booking.dto.ReservationResponse;
import com.exelynt.resource_booking.repository.ReservationRepository;
import com.exelynt.resource_booking.repository.ResourceRepository;
import com.exelynt.resource_booking.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetReservations() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<com.exelynt.resource_booking.entity.Reservation> page =
                new PageImpl<>(Collections.emptyList());

        when(reservationRepository.findAll(pageable))
                .thenReturn(page);

        Page<ReservationResponse> result =
                reservationService.getReservations(
                        null, null, null, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());

        verify(reservationRepository).findAll(pageable);
    }
}