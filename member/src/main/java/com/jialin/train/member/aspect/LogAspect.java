package com.jialin.train.member.aspect;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
public class LogAspect {
    public LogAspect() {
        System.out.println("LogAspect");
    }

    private final static Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    /**
     * Define a pointcut
     */
    // * (first asterisk): Represents any return type.
    // *Controller: All classes whose names end with 'Controller'.
    // *(..): Any method (name is '*'), with any number and type of parameters ('..').
    @Pointcut("execution(public * com.jialin..*Controller.*(..))")
    public void controllerPointcut() {
    }
    // Before the hello() method is executed.
    @Before("controllerPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        // Start logging the request.
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Signature signature = joinPoint.getSignature();
        String name = signature.getName();

        // Log request message.
        LOG.info("------------- Begin -------------");
        LOG.info("Request URL: {} {}", request.getRequestURL().toString(), request.getMethod());
        LOG.info("Class and Method Name: {}.{}", signature.getDeclaringTypeName(), name);
        LOG.info("Remote Address: {}", request.getRemoteAddr());

        // Log request parameters
        Object[] args = joinPoint.getArgs();
//        LOG.info("Request parameters: {}", JSONObject.toJSONString(args));

        // Excluding special types of parameters, such as file types.
        Object[] arguments = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest
                    || args[i] instanceof ServletResponse
                    || args[i] instanceof MultipartFile) {
                continue;
            }
            arguments[i] = args[i];
        }

        String[] excludeProperties = {};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludeFilter = filters.addFilter();
        excludeFilter.addExcludes(excludeProperties);
        LOG.info("Request Parameters: {}", JSONObject.toJSONString(arguments, excludeFilter));
    }

    @Around("controllerPointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        // Exclude fields: sensitive or overly long fields will not be displayed (e.g., ID card number, phone number
        // , email, password, etc.).
        String[] excludeProperties = {};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludeFilter = filters.addFilter();
        excludeFilter.addExcludes(excludeProperties);
        LOG.info("Returned Result: {}", JSONObject.toJSONString(result, excludeFilter));
        LOG.info("Duration: {} ms", System.currentTimeMillis() - startTime);
        LOG.info("------------- End -------------");
        return result;
    }

}
