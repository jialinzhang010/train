package com.jialin.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jialin.train.business.domain.*;
import com.jialin.train.business.mapper.DailyTrainSeatMapper;
import com.jialin.train.business.req.DailyTrainSeatQueryReq;
import com.jialin.train.business.req.DailyTrainSeatSaveReq;
import com.jialin.train.business.resp.DailyTrainSeatQueryResp;
import com.jialin.train.common.resp.PageResp;
import com.jialin.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainSeatService.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private TrainSeatService trainSeatService;

    @Resource
    private TrainStationService trainStationService;

    public void save(DailyTrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);
        if (ObjectUtil.isNull(dailyTrainSeat.getId())) {
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        } else {
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.updateByPrimaryKey(dailyTrainSeat);
        }
    }

    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req) {
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.setOrderByClause("date desc, train_code asc, carriage_index asc, carriage_seat_index asc");
        DailyTrainSeatExample.Criteria criteria = dailyTrainSeatExample.createCriteria();
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        LOG.info("Requested pages: {}", req.getPage());
        LOG.info("Requested page size: {}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainSeat> dailyTrainSeatList = dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);

        PageInfo<DailyTrainSeat> pageInfo = new PageInfo<>(dailyTrainSeatList);
        LOG.info("Total number of lines: {}", pageInfo.getTotal());
        LOG.info("Total number of pages: {}", pageInfo.getPages());

        List<DailyTrainSeatQueryResp> list = BeanUtil.copyToList(dailyTrainSeatList, DailyTrainSeatQueryResp.class);

        PageResp<DailyTrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainSeatMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("Generate daily train seat for train code: {} on {}", trainCode, DateUtil.formatDate(date));

        // Delete seat data on date xxx
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainSeatMapper.deleteByExample(dailyTrainSeatExample);

        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        String sell = StrUtil.fillBefore("", '0', stationList.size() - 1);

        List<TrainSeat> seatList = trainSeatService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(seatList)) {
            LOG.info("No seats found for train code: {}", trainCode);
            return;
        }
        for (TrainSeat trainSeat : seatList) {
            DateTime now = DateTime.now();
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }
    }

    public int countSeat(Date date, String trainCode, String seatType) {
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andSeatTypeEqualTo(seatType);
        long l = dailyTrainSeatMapper.countByExample(dailyTrainSeatExample);
        if (l == 0L) {
            return -1;
        }
        return (int) l;
    }

    public List<DailyTrainSeat> selectByCarriage(Date date, String trainCode, Integer carriageIndex) {
        DailyTrainSeatExample example = new DailyTrainSeatExample();
        example.setOrderByClause("carriage_index asc");
        example.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andCarriageIndexEqualTo(carriageIndex);
        return dailyTrainSeatMapper.selectByExample(example);
    }
}
