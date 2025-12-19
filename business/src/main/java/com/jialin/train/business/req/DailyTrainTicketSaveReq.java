package com.jialin.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DailyTrainTicketSaveReq {

    /**
     * id
     */
    private Long id;

    /**
     * date
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "[date] cannot be empty")
    private Date date;

    /**
     * Train code
     */
    @NotBlank(message = "[Train code] cannot be empty")
    private String trainCode;

    /**
     * Departure station
     */
    @NotBlank(message = "[Departure station] cannot be empty")
    private String start;

    /**
     * Departure time
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "[Departure time] cannot be empty")
    private Date startTime;

    /**
     * Departure station index | The sequence number of this station in the train route.
     */
    @NotNull(message = "[Departure station index ] cannot be empty")
    private Integer startIndex;

    /**
     * Arrival station
     */
    @NotBlank(message = "[Arrival station] cannot be empty")
    private String end;

    /**
     * Arrival time
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "[Arrival time] cannot be empty")
    private Date endTime;

    /**
     * Arrival station index | The sequence number of this station in the train route.
     */
    @NotNull(message = "[Arrival station index ] cannot be empty")
    private Integer endIndex;

    /**
     * First class remaining tickets
     */
    @NotNull(message = "[First class remaining tickets] cannot be empty")
    private Integer ydz;

    /**
     * First class ticket price
     */
    @NotNull(message = "[First class ticket price] cannot be empty")
    private BigDecimal ydzPrice;

    /**
     * Second class remaining tickets
     */
    @NotNull(message = "[Second class remaining tickets] cannot be empty")
    private Integer edz;

    /**
     * Second class ticket price
     */
    @NotNull(message = "[Second class ticket price] cannot be empty")
    private BigDecimal edzPrice;

    /**
     * Soft sleeper remaining tickets
     */
    @NotNull(message = "[Soft sleeper remaining tickets] cannot be empty")
    private Integer rw;

    /**
     * Soft sleeper ticket price
     */
    @NotNull(message = "[Soft sleeper ticket price] cannot be empty")
    private BigDecimal rwPrice;

    /**
     * Hard sleeper remaining tickets
     */
    @NotNull(message = "[Hard sleeper remaining tickets] cannot be empty")
    private Integer yw;

    /**
     * Hard sleeper ticket price
     */
    @NotNull(message = "[Hard sleeper ticket price] cannot be empty")
    private BigDecimal ywPrice;

    /**
     * Create time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * Update time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public Integer getYdz() {
        return ydz;
    }

    public void setYdz(Integer ydz) {
        this.ydz = ydz;
    }

    public BigDecimal getYdzPrice() {
        return ydzPrice;
    }

    public void setYdzPrice(BigDecimal ydzPrice) {
        this.ydzPrice = ydzPrice;
    }

    public Integer getEdz() {
        return edz;
    }

    public void setEdz(Integer edz) {
        this.edz = edz;
    }

    public BigDecimal getEdzPrice() {
        return edzPrice;
    }

    public void setEdzPrice(BigDecimal edzPrice) {
        this.edzPrice = edzPrice;
    }

    public Integer getRw() {
        return rw;
    }

    public void setRw(Integer rw) {
        this.rw = rw;
    }

    public BigDecimal getRwPrice() {
        return rwPrice;
    }

    public void setRwPrice(BigDecimal rwPrice) {
        this.rwPrice = rwPrice;
    }

    public Integer getYw() {
        return yw;
    }

    public void setYw(Integer yw) {
        this.yw = yw;
    }

    public BigDecimal getYwPrice() {
        return ywPrice;
    }

    public void setYwPrice(BigDecimal ywPrice) {
        this.ywPrice = ywPrice;
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
        sb.append(", date=").append(date);
        sb.append(", trainCode=").append(trainCode);
        sb.append(", start=").append(start);
        sb.append(", startTime=").append(startTime);
        sb.append(", startIndex=").append(startIndex);
        sb.append(", end=").append(end);
        sb.append(", endTime=").append(endTime);
        sb.append(", endIndex=").append(endIndex);
        sb.append(", ydz=").append(ydz);
        sb.append(", ydzPrice=").append(ydzPrice);
        sb.append(", edz=").append(edz);
        sb.append(", edzPrice=").append(edzPrice);
        sb.append(", rw=").append(rw);
        sb.append(", rwPrice=").append(rwPrice);
        sb.append(", yw=").append(yw);
        sb.append(", ywPrice=").append(ywPrice);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
