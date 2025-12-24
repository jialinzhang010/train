package com.jialin.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jialin.train.common.req.MemberTicketReq;
import com.jialin.train.common.resp.PageResp;
import com.jialin.train.common.util.SnowUtil;
import com.jialin.train.member.domain.Ticket;
import com.jialin.train.member.domain.TicketExample;
import com.jialin.train.member.mapper.TicketMapper;
import com.jialin.train.member.req.TicketQueryReq;
import com.jialin.train.member.resp.TicketQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    @Resource
    private TicketMapper ticketMapper;

    public void save(MemberTicketReq req) {
        DateTime now = DateTime.now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
        if (ObjectUtil.isNull(ticket.getId())) {
            ticket.setId(SnowUtil.getSnowflakeNextId());
            ticket.setCreateTime(now);
            ticket.setUpdateTime(now);
            ticketMapper.insert(ticket);
        } else {
            ticket.setUpdateTime(now);
            ticketMapper.updateByPrimaryKey(ticket);
        }
    }

    public PageResp<TicketQueryResp> queryList(TicketQueryReq req) {
        TicketExample ticketExample = new TicketExample();
        ticketExample.setOrderByClause("id desc");
        TicketExample.Criteria criteria = ticketExample.createCriteria();

        LOG.info("Requested pages: {}", req.getPage());
        LOG.info("Requested page size: {}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ticket> ticketList = ticketMapper.selectByExample(ticketExample);

        PageInfo<Ticket> pageInfo = new PageInfo<>(ticketList);
        LOG.info("Total number of lines: {}", pageInfo.getTotal());
        LOG.info("Total number of pages: {}", pageInfo.getPages());

        List<TicketQueryResp> list = BeanUtil.copyToList(ticketList, TicketQueryResp.class);

        PageResp<TicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        ticketMapper.deleteByPrimaryKey(id);
    }
}
