package com.jialin.train.business.controller.admin;

import com.jialin.train.business.req.DailyTrainQueryReq;
import com.jialin.train.business.req.DailyTrainSaveReq;
import com.jialin.train.business.resp.DailyTrainQueryResp;
import com.jialin.train.business.service.DailyTrainService;
import com.jialin.train.common.resp.CommonResp;
import com.jialin.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {
    @Resource
    private DailyTrainService dailyTrainService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSaveReq req) {
        dailyTrainService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainQueryResp>> queryList(@Valid DailyTrainQueryReq req) {
        PageResp<DailyTrainQueryResp> list = dailyTrainService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id) {
        dailyTrainService.delete(id);
        return new CommonResp<>();
    }

}
