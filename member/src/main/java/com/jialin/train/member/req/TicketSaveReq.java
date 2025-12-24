package com.jialin.train.member.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketSaveReq {

    /**
     * ID
     */
    private Long id;

    /**
     * Member ID
     */
    @NotNull(message = "[Member ID] cannot be empty")
    private Long memberId;

    /**
     * Passenger ID
     */
    @NotNull(message = "[Passenger ID] cannot be empty")
    private Long passengerId;

    /**
     * Passenger Name
     */
    private String passengerName;

    /**
     * Travel Date
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "[Travel Date] cannot be empty")
    private Date trainDate;

    /**
     * Train Number
     */
    @NotBlank(message = "[Train Number] cannot be empty")
    private String trainCode;

    /**
     * Carriage Number
     */
    @NotNull(message = "[Carriage Number] cannot be empty")
    private Integer carriageIndex;

    /**
     * Row Number | 01, 02
     */
    @NotBlank(message = "[Row Number ] cannot be empty")
    private String seatRow;

    /**
     * Column | Enum [SeatColEnum]
     */
    @NotBlank(message = "[Column ] cannot be empty")
    private String seatCol;

    /**
     * Departure Station
     */
    @NotBlank(message = "[Departure Station] cannot be empty")
    private String startStation;

    /**
     * Departure Time
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "[Departure Time] cannot be empty")
    private Date startTime;

    /**
     * Arrival Station
     */
    @NotBlank(message = "[Arrival Station] cannot be empty")
    private String endStation;

    /**
     * Arrival Time
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "[Arrival Time] cannot be empty")
    private Date endTime;

    /**
     * Seat Type | Enum [SeatTypeEnum]
     */
    @NotBlank(message = "[Seat Type ] cannot be empty")
    private String seatType;

    /**
     * Creation Time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * Last Update Time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberId=").append(memberId);
        sb.append(", passengerId=").append(passengerId);
        sb.append(", passengerName=").append(passengerName);
        sb.append(", trainDate=").append(trainDate);
        sb.append(", trainCode=").append(trainCode);
        sb.append(", carriageIndex=").append(carriageIndex);
        sb.append(", seatRow=").append(seatRow);
        sb.append(", seatCol=").append(seatCol);
        sb.append(", startStation=").append(startStation);
        sb.append(", startTime=").append(startTime);
        sb.append(", endStation=").append(endStation);
        sb.append(", endTime=").append(endTime);
        sb.append(", seatType=").append(seatType);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
