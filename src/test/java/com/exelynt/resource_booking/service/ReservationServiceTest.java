package com.exelynt.resource_booking.service;

import com.exelynt.resource_booking.dto.ReservationResponse;
import com.exelynt.resource_booking.entity.User;
import com.exelynt.resource_booking.repository.ReservationRepository;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    // 1. Missing UserRepository Mock Add Kela
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetReservations() {
        // 2. Mock Security Context with Username
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        SecurityContextHolder.setContext(securityContext);

        // 3. Mock UserRepository behavior (Je NPE det hota)
        User mockUser = new User();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        // 4. Mock ReservationRepository behavior
        Pageable pageable = PageRequest.of(0, 10);
        when(reservationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Call target method
        Page<ReservationResponse> result = reservationService.getReservations(null, null, null, pageable);

        // Assert
        assertNotNull(result);
    }
}