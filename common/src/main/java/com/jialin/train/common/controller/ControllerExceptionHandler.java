package com.jialin.train.common.controller;

import cn.hutool.core.util.StrUtil;
import com.jialin.train.common.exception.BusinessException;
import com.jialin.train.common.resp.CommonResp;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    /**
     * All exception handler
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResp exceptionHandler(Exception e) throws Exception {
        LOG.info("seata全局事务ID：{}", RootContext.getXID());
        if (StrUtil.isNotBlank(RootContext.getXID())) {
            throw e;
        }
        CommonResp commonResp = new CommonResp();
        LOG.error("Exception: ", e);
        commonResp.setSuccess(false);
        commonResp.setMessage("Exception, please contact admin");
//        commonResp.setMessage(e.getMessage());
        return commonResp;

    }

    /**
     * Business exception handler
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp exceptionHandler(BusinessException e) {
        CommonResp commonResp = new CommonResp();
        LOG.error("Business Exception: {}", e.getE().getDesc());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getE().getDesc());
        return commonResp;
    }

    /**
     * Business exception handler
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResp exceptionHandler(BindException e) {
        CommonResp commonResp = new CommonResp();
        LOG.error("Validation Exception: {}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return commonResp;

    }
}
