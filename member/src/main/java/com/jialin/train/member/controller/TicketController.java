package com.jialin.train.member.controller;

import com.jialin.train.common.context.LoginMemberContext;
import com.jialin.train.common.resp.CommonResp;
import com.jialin.train.common.resp.PageResp;
import com.jialin.train.member.req.TicketQueryReq;
import com.jialin.train.member.resp.TicketQueryResp;
import com.jialin.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Resource
    private TicketService ticketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> query(@Valid TicketQueryReq req) {
        CommonResp<PageResp<TicketQueryResp>> commonResp = new CommonResp<>();
        req.setMemberId(LoginMemberContext.getId());
        PageResp<TicketQueryResp> pageResp = ticketService.queryList(req);
        commonResp.setContent(pageResp);
        return commonResp;
    }

}
