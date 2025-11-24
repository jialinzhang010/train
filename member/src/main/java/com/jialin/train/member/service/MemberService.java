package com.jialin.train.member.service;

import cn.hutool.core.collection.CollUtil;
import com.jialin.train.common.exception.BusinessException;
import com.jialin.train.common.exception.BusinessExceptionEnum;
import com.jialin.train.common.util.SnowUtil;
import com.jialin.train.member.domain.Member;
import com.jialin.train.member.domain.MemberExample;
import com.jialin.train.member.mapper.MemberMapper;
import com.jialin.train.member.req.MemberRegisterReq;
import com.jialin.train.member.req.MemberSendCodeReq;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberService.class);
    @Resource
    private MemberMapper memberMapper;

    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    public long register(MemberRegisterReq req) {
        String mobile = req.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        if (CollUtil.isNotEmpty(list)) {
//            return list.get(0).getId();
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);

        memberMapper.insert(member);
        return member.getId();
    }


    public void sendCode(MemberSendCodeReq req) {
        String mobile = req.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);

        // If phone number does not exist, then insert a record
        if (CollUtil.isEmpty(list)) {
            LOG.info("Phone number does not exist, insert a new one");
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        } else {
            LOG.info("Phone number exists, do not insert a new one");
        }
        // Generate verification code
//        String code = RandomUtil.randomString(4);
        String code = "8888";
        LOG.info("Generating verification code: {}", code);

        // Save message table: phone number, verification code, expiration time, whether used, business type, sent time, used time
        LOG.info("Save message table");

        // Connect to SMS gateway
        LOG.info("Connect to SMS gateway");
    }


}

