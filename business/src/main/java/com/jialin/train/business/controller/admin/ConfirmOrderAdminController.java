package com.jialin.train.business.controller.admin;

import com.jialin.train.common.resp.CommonResp;
import com.jialin.train.common.resp.PageResp;
import com.jialin.train.business.req.ConfirmOrderQueryReq;
import com.jialin.train.business.req.ConfirmOrderDoReq;
import com.jialin.train.business.resp.ConfirmOrderQueryResp;
import com.jialin.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {
    @Resource
    private ConfirmOrderService confirmOrderService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderDoReq req) {
        confirmOrderService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> queryList(@Valid ConfirmOrderQueryReq req) {
        PageResp<ConfirmOrderQueryResp> list = confirmOrderService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id) {
        confirmOrderService.delete(id);
        return new CommonResp<>();
    }

}
