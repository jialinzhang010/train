package com.jialin.train.business.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

public class ConfirmOrderDoReq {

    /**
     * Member id
     */
    @NotNull(message = "[Member id] cannot be empty")
    private Long memberId;

    /**
     * Date
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "[Date] cannot be empty")
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
     * Arrival station
     */
    @NotBlank(message = "[Arrival station] cannot be empty")
    private String end;

    /**
     * Daily train ticket id
     */
    @NotNull(message = "[Daily train ticket id] cannot be empty")
    private Long dailyTrainTicketId;

    /**
     * tickets
     */
    @NotBlank(message = "[tickets] cannot be empty")
    private List<ConfirmOrderTicketReq> tickets;


    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Long getDailyTrainTicketId() {
        return dailyTrainTicketId;
    }

    public void setDailyTrainTicketId(Long dailyTrainTicketId) {
        this.dailyTrainTicketId = dailyTrainTicketId;
    }

    public List<ConfirmOrderTicketReq> getTickets() {
        return tickets;
    }

    public void setTickets(List<ConfirmOrderTicketReq> tickets) {
        this.tickets = tickets;
    }

    @Override
    public String toString() {
        return "ConfirmOrderDoReq{" +
                "memberId=" + memberId +
                ", date=" + date +
                ", trainCode='" + trainCode + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", dailyTrainTicketId=" + dailyTrainTicketId +
                ", tickets=" + tickets +
                '}';
    }
}
