package com.jialin.train.${module}.controller.admin;

import com.jialin.train.common.context.LoginMemberContext;
import com.jialin.train.common.resp.CommonResp;
import com.jialin.train.common.resp.PageResp;
import com.jialin.train.${module}.req.${Domain}QueryReq;
import com.jialin.train.${module}.req.${Domain}SaveReq;
import com.jialin.train.${module}.resp.${Domain}QueryResp;
import com.jialin.train.${module}.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/${domain}")
public class ${Domain}AdminController {
    @Resource
    private ${Domain}Service ${domain}Service;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ${Domain}SaveReq req) {
        ${domain}Service.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> queryList(@Valid ${Domain}QueryReq req) {
        PageResp<${Domain}QueryResp> list = ${domain}Service.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id) {
        ${domain}Service.delete(id);
        return new CommonResp<>();
    }

}
