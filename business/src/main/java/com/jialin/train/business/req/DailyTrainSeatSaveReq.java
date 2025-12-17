package com.jialin.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DailyTrainSeatSaveReq {

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
     * Carriage index
     */
    @NotNull(message = "[Carriage index] cannot be empty")
    private Integer carriageIndex;

    /**
     * Row number | 01, 02
     */
    @NotBlank(message = "[Row number ] cannot be empty")
    private String row;

    /**
     * Column number | Enum[SeatColEnum]
     */
    @NotBlank(message = "[Column number ] cannot be empty")
    private String col;

    /**
     * Seat type | Enum[SeatTypeEnum]
     */
    @NotBlank(message = "[Seat type ] cannot be empty")
    private String seatType;

    /**
     * Carriage seat index
     */
    @NotNull(message = "[Carriage seat index] cannot be empty")
    private Integer carriageSeatIndex;

    /**
     * Sales status | Generate a segment availability string using 0s and 1s, where 0 indicates "available for sale" and 1 indicates "not available for sale."
     */
    @NotBlank(message = "[Sales status ] cannot be empty")
    private String sell;

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

    public Integer getCarriageIndex() {
        return carriageIndex;
    }

    public void setCarriageIndex(Integer carriageIndex) {
        this.carriageIndex = carriageIndex;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public Integer getCarriageSeatIndex() {
        return carriageSeatIndex;
    }

    public void setCarriageSeatIndex(Integer carriageSeatIndex) {
        this.carriageSeatIndex = carriageSeatIndex;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
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
        sb.append(", carriageIndex=").append(carriageIndex);
        sb.append(", row=").append(row);
        sb.append(", col=").append(col);
        sb.append(", seatType=").append(seatType);
        sb.append(", carriageSeatIndex=").append(carriageSeatIndex);
        sb.append(", sell=").append(sell);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
