package com.jialin.train.business.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConfirmOrderTicketReq {

    /**
     * Passenger ID
     */
    @NotNull(message = "Passenger ID is required")
    private Long passengerId;

    /**
     * Ticket type (e.g., Adult, Child)
     */
    @NotBlank(message = "Passenger ticket type is required")
    private String passengerType;

    /**
     * Passenger name
     */
    @NotBlank(message = "Passenger name is required")
    private String passengerName;

    /**
     * Passenger ID Card / Passport Number
     */
    @NotBlank(message = "Passenger ID card is required")
    private String passengerIdCard;

    /**
     * Seat type code (e.g., YDZ, EDZ)
     */
    @NotBlank(message = "Seat type code is required")
    private String seatTypeCode;

    /**
     * 选座，可空，值示例：A1
     */
    private String seat;

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerIdCard() {
        return passengerIdCard;
    }

    public void setPassengerIdCard(String passengerIdCard) {
        this.passengerIdCard = passengerIdCard;
    }

    public String getSeatTypeCode() {
        return seatTypeCode;
    }

    public void setSeatTypeCode(String seatTypeCode) {
        this.seatTypeCode = seatTypeCode;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConfirmOrderTicketReq{");
        sb.append("passengerId=").append(passengerId);
        sb.append(", passengerType='").append(passengerType).append('\'');
        sb.append(", passengerName='").append(passengerName).append('\'');
        sb.append(", passengerIdCard='").append(passengerIdCard).append('\'');
        sb.append(", seatTypeCode='").append(seatTypeCode).append('\'');
        sb.append(", seat='").append(seat).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
