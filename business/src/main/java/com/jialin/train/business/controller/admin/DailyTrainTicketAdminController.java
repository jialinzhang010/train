package com.jialin.train.business.controller.admin;

import com.jialin.train.business.req.DailyTrainTicketQueryReq;
import com.jialin.train.business.req.DailyTrainTicketSaveReq;
import com.jialin.train.business.resp.DailyTrainTicketQueryResp;
import com.jialin.train.business.service.DailyTrainTicketService;
import com.jialin.train.common.resp.CommonResp;
import com.jialin.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {
    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainTicketSaveReq req) {
        dailyTrainTicketService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id) {
        dailyTrainTicketService.delete(id);
        return new CommonResp<>();
    }

}
