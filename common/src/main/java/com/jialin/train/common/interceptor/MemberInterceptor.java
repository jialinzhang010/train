package com.jialin.train.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jialin.train.common.util.JwtUtil;
import com.jialin.train.common.context.LoginMemberContext;
import com.jialin.train.common.resp.MemberLoginResp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MemberInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MemberInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info("MemberInterceptor begin.");

        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            LOG.info("Getting member token: {}", token);
            JSONObject loginMember = JwtUtil.getJSONObject(token);
            LOG.info("Current login member: {}", loginMember);
            MemberLoginResp member = JSONUtil.toBean(loginMember, MemberLoginResp.class);
            LoginMemberContext.setMember(member);
        }
        LOG.info("MemberInterceptor end.");
        return true;
    }

}
