package com.jialin.train.business.controller.admin;

import com.jialin.train.business.req.TrainCarriageQueryReq;
import com.jialin.train.business.req.TrainCarriageSaveReq;
import com.jialin.train.business.resp.TrainCarriageQueryResp;
import com.jialin.train.business.service.TrainCarriageService;
import com.jialin.train.common.resp.CommonResp;
import com.jialin.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-carriage")
public class TrainCarriageAdminController {
    @Resource
    private TrainCarriageService trainCarriageService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainCarriageSaveReq req) {
        trainCarriageService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainCarriageQueryResp>> queryList(@Valid TrainCarriageQueryReq req) {
        PageResp<TrainCarriageQueryResp> list = trainCarriageService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id) {
        trainCarriageService.delete(id);
        return new CommonResp<>();
    }

}
