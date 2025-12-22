package com.jialin.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jialin.train.business.domain.*;
import com.jialin.train.business.enums.ConfirmOrderStatusEnum;
import com.jialin.train.business.enums.SeatColEnum;
import com.jialin.train.business.enums.SeatTypeEnum;
import com.jialin.train.business.mapper.ConfirmOrderMapper;
import com.jialin.train.business.req.ConfirmOrderDoReq;
import com.jialin.train.business.req.ConfirmOrderQueryReq;
import com.jialin.train.business.req.ConfirmOrderTicketReq;
import com.jialin.train.business.resp.ConfirmOrderQueryResp;
import com.jialin.train.common.context.LoginMemberContext;
import com.jialin.train.common.exception.BusinessException;
import com.jialin.train.common.exception.BusinessExceptionEnum;
import com.jialin.train.common.resp.PageResp;
import com.jialin.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    public void save(ConfirmOrderDoReq req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.setOrderByClause("id desc");
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        LOG.info("Requested pages: {}", req.getPage());
        LOG.info("Requested page size: {}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);

        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
        LOG.info("Total number of lines: {}", pageInfo.getTotal());
        LOG.info("Total number of pages: {}", pageInfo.getPages());

        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);

        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    public void doConfirm(ConfirmOrderDoReq req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setUpdateTime(now);
        confirmOrder.setCreateTime(now);
        confirmOrder.setMemberId(LoginMemberContext.getId());

        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();
        List<ConfirmOrderTicketReq> tickets = req.getTickets();

        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());

        confirmOrder.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(confirmOrder);
        LOG.info("日期: {}", date);
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("dailyTrainTicket: {}", dailyTrainTicket);

        reduceTickets(req, dailyTrainTicket);

        //判断有无选座：seat属性是否为空
        ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
        LOG.info("ticketReq0: {}", ticketReq0);
        // isBlank对空格也生效
        if (StrUtil.isNotBlank(ticketReq0.getSeat())) {
            LOG.info("This order has seat.");
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
            LOG.info("The cols in this order: {}", colEnumList);
            // e.g. {A1, C1, D1, F1, A2, C2, D2, F2}
            List<String> referSeatList = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                for (SeatColEnum seatColEnum : colEnumList) {
                    referSeatList.add(seatColEnum.getCode() + i);
                }
            }
            LOG.info("ReferSeatList: {}", referSeatList);

            List<Integer> absoluteOffsetList = new ArrayList<>();
            List<Integer> offsetList = new ArrayList<>();
            for (ConfirmOrderTicketReq ticketReq : tickets) {
                int index = referSeatList.indexOf(ticketReq.getSeat());
                absoluteOffsetList.add(index);
            }
            LOG.info("AbsoluteOffsetList: {}", absoluteOffsetList);
            for (Integer index : absoluteOffsetList) {
                int offset = index - absoluteOffsetList.get(0);
                offsetList.add(offset);
            }
            LOG.info("OffsetList: {}", offsetList);

            getSeat(date,
                    trainCode,
                    ticketReq0.getSeatTypeCode(),
                    ticketReq0.getSeat().split("")[0],
                    offsetList);
        } else {
            LOG.info("This order has no seat.");
            for (ConfirmOrderTicketReq ticketReq : tickets) {
                getSeat(date,
                        trainCode,
                        ticketReq0.getSeatTypeCode(),
                        null,
                        null);
            }
        }
    }
    /*
    column: 第一个座位对应的列
     */
    private void getSeat(Date date, String trainCode, String seatType, String column, List<Integer> offsetList) {
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("There are {} eligible carriages.", carriageList.size());

        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            LOG.info("Start choose seat from carriage {}", dailyTrainCarriage);
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("There are {} eligible seats.", seatList.size());

        }
    }

    private static void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }
        }
    }
}
