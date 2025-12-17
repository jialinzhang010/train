package com.jialin.train.business.controller.admin;

import com.jialin.train.business.req.DailyTrainSeatQueryReq;
import com.jialin.train.business.req.DailyTrainSeatSaveReq;
import com.jialin.train.business.resp.DailyTrainSeatQueryResp;
import com.jialin.train.business.service.DailyTrainSeatService;
import com.jialin.train.common.resp.CommonResp;
import com.jialin.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-seat")
public class DailyTrainSeatAdminController {
    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSeatSaveReq req) {
        dailyTrainSeatService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainSeatQueryResp>> queryList(@Valid DailyTrainSeatQueryReq req) {
        PageResp<DailyTrainSeatQueryResp> list = dailyTrainSeatService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id) {
        dailyTrainSeatService.delete(id);
        return new CommonResp<>();
    }

}
