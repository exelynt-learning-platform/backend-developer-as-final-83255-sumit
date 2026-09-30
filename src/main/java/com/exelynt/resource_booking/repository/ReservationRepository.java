package com.exelynt.resource_booking.repository;

import com.exelynt.resource_booking.entity.Reservation;
import com.exelynt.resource_booking.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    static Specification<Reservation> hasStatus(ReservationStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    static Specification<Reservation> hasPriceGte(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    static Specification<Reservation> hasPriceLte(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    static Specification<Reservation> belongsToUser(Long userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }
}