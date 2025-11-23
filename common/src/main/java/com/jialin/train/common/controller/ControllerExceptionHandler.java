package com.jialin.train.common.controller;

import com.jialin.train.common.exception.BusinessException;
import com.jialin.train.common.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public CommonResp exceptionHandler(Exception e) {
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

}
