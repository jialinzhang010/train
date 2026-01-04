package com.jialin.train.business.controller.admin;

import com.jialin.train.common.resp.CommonResp;
import com.jialin.train.common.resp.PageResp;
import com.jialin.train.business.req.SkTokenQueryReq;
import com.jialin.train.business.req.SkTokenSaveReq;
import com.jialin.train.business.resp.SkTokenQueryResp;
import com.jialin.train.business.service.SkTokenService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sk-token")
public class SkTokenAdminController {
    @Resource
    private SkTokenService skTokenService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody SkTokenSaveReq req) {
        skTokenService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<SkTokenQueryResp>> queryList(@Valid SkTokenQueryReq req) {
        PageResp<SkTokenQueryResp> list = skTokenService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id) {
        skTokenService.delete(id);
        return new CommonResp<>();
    }

}
