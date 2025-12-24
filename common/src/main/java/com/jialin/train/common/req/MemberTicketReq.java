package com.jialin.train.common.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class MemberTicketReq {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    /**
     * Passenger ID
     */
    @NotNull(message = "Passenger ID is required")
    private Long passengerId;

    /**
     * Passenger Name
     */
    @NotNull(message = "Passenger name is required")
    private String passengerName;

    /**
     * Date of Travel
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "Date is required")
    private Date trainDate;

    /**
     * Train Number (e.g., G123)
     */
    @NotBlank(message = "Train code is required")
    private String trainCode;

    /**
     * Carriage Number
     */
    @NotNull(message = "Carriage index is required")
    private Integer carriageIndex;

    /**
     * Row Number | 01, 02
     */
    @NotBlank(message = "Seat row is required")
    private String seatRow;

    /**
     * Column Number | Enum [SeatColumnEnum]
     */
    @NotBlank(message = "Seat column is required")
    private String seatCol;

    /**
     * Departure Station
     */
    @NotBlank(message = "Departure station is required")
    private String startStation;

    /**
     * Departure Time
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "Departure time is required")
    private Date startTime;

    /**
     * Destination Station
     */
    @NotBlank(message = "Arrival station is required")
    private String endStation;

    /**
     * Arrival Time
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "Arrival time is required")
    private Date endTime;

    /**
     * Seat Type | Enum [SeatTypeEnum]
     */
    @NotBlank(message = "Seat type is required")
    private String seatType;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Date getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(Date trainDate) {
        this.trainDate = trainDate;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public Integer getCarriageIndex() {
        return carriageIndex;
    }

    public void setCarriageIndex(Integer carriageIndex) {
        this.carriageIndex = carriageIndex;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    public String getSeatCol() {
        return seatCol;
    }

    public void setSeatCol(String seatCol) {
        this.seatCol = seatCol;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    @Override
    public String toString() {
        return "MemberTicketReq{" +
                "memberId=" + memberId +
                ", passengerId=" + passengerId +
                ", passengerName='" + passengerName + '\'' +
                ", trainDate=" + trainDate +
                ", trainCode='" + trainCode + '\'' +
                ", carriageIndex=" + carriageIndex +
                ", seatRow='" + seatRow + '\'' +
                ", seatCol='" + seatCol + '\'' +
                ", startStation='" + startStation + '\'' +
                ", startTime=" + startTime +
                ", endStation='" + endStation + '\'' +
                ", endTime=" + endTime +
                ", seatType='" + seatType + '\'' +
                '}';
    }
}