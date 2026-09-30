package com.exelynt.resource_booking.dto;

import com.exelynt.resource_booking.entity.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationResponse {
    private Long id;
    private Long resourceId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private ReservationStatus status;

    public ReservationResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}