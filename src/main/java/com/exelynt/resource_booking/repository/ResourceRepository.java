package com.exelynt.resource_booking.repository;

import com.exelynt.resource_booking.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}