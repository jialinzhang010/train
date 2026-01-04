package com.jialin.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jialin.train.business.domain.*;
import com.jialin.train.business.enums.ConfirmOrderStatusEnum;
import com.jialin.train.business.enums.RedisKeyPreEnum;
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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private SkTokenService skTokenService;


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

    @SentinelResource(value="doConfirm", blockHandler = "doConfirmBlock")
    public void doConfirm(ConfirmOrderDoReq req) {
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), req.getMemberId());
        if (validSkToken) {
            LOG.info("Do confirm by sk token");
        } else {
            LOG.info("Do confirm by sk token fail");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
        }

        // 锁同一天同一车次
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(req.getDate()) + "-" + req.getTrainCode();
//        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
//        if (Boolean.TRUE.equals(setIfAbsent)) {
//            LOG.info("Lock obtained!");
//        } else {
//            LOG.info("Lose lock!");
//            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
//        }
        RLock lock = null;
        try {
            // 使用redisson，自带看门狗
            lock = redissonClient.getLock(lockKey);
            // waitTime：等待时长设置为0，如果没拿到锁就不等待，直接返回false。
            // leaseTime 不写的话会有看门狗机制，默认释放时间30秒。
            // 只要业务逻辑还没执行完，看门狗就会每隔 10 秒（即 lockWatchdogTimeout / 3）检查一次。如果业务还在运行，它就会自动将锁的过期时间重置回 30 秒。
            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS);
            if (tryLock) {
                LOG.info("Confirm order lock acquired");
                for (int i = 0; i < 30; ++i) {
                    Long expire = redisTemplate.opsForValue().getOperations().getExpire(lockKey);
                    LOG.info("{} seconds till expire!", expire);
                    Thread.sleep(1000);
                }
            } else {
                LOG.info("Confirm order lock acquisition fail");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
            }

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

            // 查处余票记录，因为需要得到真实的库存。
            // 会导致超卖。
            DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
            LOG.info("dailyTrainTicket: {}", dailyTrainTicket);

            // 预扣余票数量，并判断余票是否足够。
            reduceTickets(req, dailyTrainTicket);

            List<DailyTrainSeat> finalSeatList = new ArrayList<>();

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

                getSeat(finalSeatList,
                        date,
                        trainCode,
                        ticketReq0.getSeatTypeCode(),
                        ticketReq0.getSeat().split("")[0],
                        offsetList,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex());
            } else {
                LOG.info("This order has no seat.");
                for (ConfirmOrderTicketReq ticketReq : tickets) {
                    getSeat(finalSeatList,
                            date,
                            trainCode,
                            ticketReq0.getSeatTypeCode(),
                            null,
                            null,
                            dailyTrainTicket.getStartIndex(),
                            dailyTrainTicket.getEndIndex());
                }
            }
            LOG.info("Final chosen seats: {}", finalSeatList);

            try {
                afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, tickets, confirmOrder);
            } catch (Exception e) {
                LOG.error("Save order failed", e);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
            }
            LOG.info("Ticket purchasing process complete, releasing key!");
            redisTemplate.delete(lockKey);
        } catch (Exception e) {
            LOG.error("Save order failed", e);
        } finally {
            LOG.info("Order finished, releasing key!");
            // 只有当前线程能释放锁
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /*
    column: 第一个座位对应的列
     */
    private void getSeat(List<DailyTrainSeat> finalSeatList, Date date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
        List<DailyTrainSeat> getSeatList = new ArrayList<>();
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService
                .selectBySeatType(date, trainCode, seatType);
        LOG.info("There are {} eligible carriages.", carriageList.size());

        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            LOG.info("Start choose seat from carriage {}", dailyTrainCarriage.getIndex());
            getSeatList = new ArrayList<>();
            List<DailyTrainSeat> seatList = dailyTrainSeatService
                    .selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("There are {} eligible seats.", seatList.size());
            for (int i = 0; i < seatList.size(); ++i) {
                DailyTrainSeat seat = seatList.get(i);
                String col = seat.getCol();
                Integer seatIndex = seat.getCarriageSeatIndex();

                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat finalSeat : finalSeatList) {
                    if (finalSeat.getId().equals(seat.getId())) {
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    LOG.info("This seat {} has already been chosen.", seatIndex);
                    continue;
                }

                if (StrUtil.isBlank(column)) {
                    LOG.info("Did not choose seat");
                } else {
                    if (!col.equals(column)) {
                        LOG.info("Seat {} does not match column, target: {}", seat, seatIndex);
                        continue;
                    }
                }

                boolean isChoose = canSell(seat, startIndex, endIndex);
                if (isChoose) {
                    getSeatList.add(seat);
                    LOG.info("Choose seat.");
                } else {
                    continue;
                }

                boolean isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)) {
                    LOG.info("There is offset: {}, verifying if offset seat is choosable", offsetList);
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(j);
                        // 座位在库的索引从1开始
//                        int nextIndex = seatIndex + offset - 1;
                        int nextIndex = i + offset;
                        if (nextIndex >= seatList.size()) {
                            LOG.info("There is no next index of {}", nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        boolean isChooseNext = canSell(nextDailyTrainSeat, startIndex, endIndex);
                        if (isChooseNext) {
                            LOG.info("Choose seat {}.", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            LOG.info("Cannot choose seat {}.", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }
                if (!isGetAllOffsetSeat) {
                    getSeatList = new ArrayList<>();
                    continue;
                }

                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    private boolean canSell(DailyTrainSeat seat, Integer startIndex, Integer endIndex) {
        String sell = seat.getSell();
        sell.substring(startIndex, endIndex);
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            LOG.info("Seat has sell.");
            return false;
        } else {
            LOG.info("Seat has not sell.");
            String curSell = sellPart.replace('0', '1');
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());
            int newSellInt = NumberUtil.binaryToInt(curSell) | cn.hutool.core.util.NumberUtil.binaryToInt(sell);
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            LOG.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}"
                    , seat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
            seat.setSell(newSell);
            return true;
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
    public void doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOG.info("Do confirm block: {}.", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}
