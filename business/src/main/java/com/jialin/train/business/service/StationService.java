package com.jialin.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jialin.train.common.resp.PageResp;
import com.jialin.train.common.util.SnowUtil;
import com.jialin.train.business.domain.Station;
import com.jialin.train.business.domain.StationExample;
import com.jialin.train.business.mapper.StationMapper;
import com.jialin.train.business.req.StationQueryReq;
import com.jialin.train.business.req.StationSaveReq;
import com.jialin.train.business.resp.StationQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    private static final Logger LOG = LoggerFactory.getLogger(StationService.class);

    @Resource
    private StationMapper stationMapper;

    public void save(StationSaveReq req) {
        DateTime now = DateTime.now();
        Station station = BeanUtil.copyProperties(req, Station.class);
        if (ObjectUtil.isNull(station.getId())) {
            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        } else {
            station.setUpdateTime(now);
            stationMapper.updateByPrimaryKey(station);
        }
    }

    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        StationExample stationExample = new StationExample();
        stationExample.setOrderByClause("id desc");
        StationExample.Criteria criteria = stationExample.createCriteria();

        LOG.info("Requested pages: {}", req.getPage());
        LOG.info("Requested page size: {}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Station> stationList = stationMapper.selectByExample(stationExample);

        PageInfo<Station> pageInfo = new PageInfo<>(stationList);
        LOG.info("Total number of lines: {}", pageInfo.getTotal());
        LOG.info("Total number of pages: {}", pageInfo.getPages());

        List<StationQueryResp> list = BeanUtil.copyToList(stationList, StationQueryResp.class);

        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        stationMapper.deleteByPrimaryKey(id);
    }
}
