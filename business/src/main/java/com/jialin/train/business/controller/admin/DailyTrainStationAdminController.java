package com.jialin.train.business.controller.admin;

import com.jialin.train.common.resp.CommonResp;
import com.jialin.train.common.resp.PageResp;
import com.jialin.train.business.req.DailyTrainStationQueryReq;
import com.jialin.train.business.req.DailyTrainStationSaveReq;
import com.jialin.train.business.resp.DailyTrainStationQueryResp;
import com.jialin.train.business.service.DailyTrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-station")
public class DailyTrainStationAdminController {
    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainStationSaveReq req) {
        dailyTrainStationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainStationQueryResp>> queryList(@Valid DailyTrainStationQueryReq req) {
        PageResp<DailyTrainStationQueryResp> list = dailyTrainStationService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id) {
        dailyTrainStationService.delete(id);
        return new CommonResp<>();
    }

}
