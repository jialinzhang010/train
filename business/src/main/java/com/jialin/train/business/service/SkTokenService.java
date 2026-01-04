package com.jialin.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jialin.train.business.domain.SkToken;
import com.jialin.train.business.domain.SkTokenExample;
import com.jialin.train.business.enums.RedisKeyPreEnum;
import com.jialin.train.business.mapper.SkTokenMapper;
import com.jialin.train.business.mapper.cust.SkTokenMapperCust;
import com.jialin.train.business.req.SkTokenQueryReq;
import com.jialin.train.business.req.SkTokenSaveReq;
import com.jialin.train.business.resp.SkTokenQueryResp;
import com.jialin.train.common.resp.PageResp;
import com.jialin.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SkTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);

    @Resource
    private SkTokenMapper skTokenMapper;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @Resource
    private SkTokenMapperCust skTokenMapperCust;
    @Autowired
    private RedisTemplate redisTemplate;

    public void genDaily(Date date, String trainCode) {
        LOG.info("Deleting date {} train code {}'s tokens", DateUtil.formatDate(date), trainCode);
        SkTokenExample example = new SkTokenExample();
        example.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(example);

        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
        LOG.info("Train code {} seat count {}", trainCode, seatCount);
        long stationCount = dailyTrainStationService.countByTrainCode(date, trainCode);
        LOG.info("Train code {} stationCount {}", trainCode, stationCount);

        int count = (int) (seatCount * stationCount);
        LOG.info("Train code {} generated {} tokens", trainCode, count);
        skToken.setCount(count);

        skTokenMapper.insert(skToken);
    }

    public void save(SkTokenSaveReq req) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        if (ObjectUtil.isNull(skToken.getId())) {
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }
    }

    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req) {
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.setOrderByClause("id desc");
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();

        LOG.info("Requested pages: {}", req.getPage());
        LOG.info("Requested page size: {}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);

        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);
        LOG.info("Total number of lines: {}", pageInfo.getTotal());
        LOG.info("Total number of pages: {}", pageInfo.getPages());

        List<SkTokenQueryResp> list = BeanUtil.copyToList(skTokenList, SkTokenQueryResp.class);

        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }

    public boolean validSkToken(Date date, String trainCode, Long memberId) {
        String lockKey = RedisKeyPreEnum.SK_TOKEN + "_" + DateUtil.formatDate(date) + "_" + trainCode + "_" + memberId;
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(setIfAbsent)) {
            LOG.info("Token {} valid", lockKey);
        } else {
            LOG.info("Token {} not valid", lockKey);
            return false;
        }

        // 在缓存中读取
        // Redis key
        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "_" + DateUtil.formatDate(date) + "_" + trainCode + "_" + memberId;
        Object skTokenCount = redisTemplate.opsForValue().get(skTokenCountKey);
        // 如果缓存中存在
        if (ObjectUtil.isNull(skTokenCount)) {
            LOG.info("Token {} valid", skTokenCountKey);
            Long count = redisTemplate.opsForValue().decrement(skTokenCountKey, 1);
            if (count < 0L) {
                LOG.error("Token {} not valid", skTokenCountKey);
                return false;
            } else {
                LOG.info("Remaining {} token", count);
                // 取到令牌后重置缓存过期时间为60秒，防止缓存过期导致大量请求访问数据库，缓存穿透
                redisTemplate.expire(skTokenCountKey, 60, TimeUnit.SECONDS);
                // 每获取5个令牌更新一次数据库，避免实时访问数据库
                if (count % 5 == 0) {
                    skTokenMapperCust.decrease(date, trainCode, 5);
                }
                return true;
            }
        } else {
            LOG.info("Token {} valid", skTokenCountKey);
            // 缓存中没有，去数据库查询
            SkTokenExample skTokenExample = new SkTokenExample();
            skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
            List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);
            if (CollUtil.isEmpty(skTokenList)) {
                LOG.info("Cannot find {} {}'s token", DateUtil.formatDate(date), trainCode);
                return false;
            }

            SkToken skToken = skTokenList.get(0);
            if (skToken.getCount() <= 0) {
                return false;
            }

            Integer count = skToken.getCount() - 1;
            // 将数据库结果放到缓存
            redisTemplate.opsForValue().set(skTokenCountKey, String.valueOf(count), 60, TimeUnit.SECONDS);
        }

        int updateCount = skTokenMapperCust.decrease(date, trainCode, 0);
        if (updateCount > 0) {
            return true;
        } else {
            return false;
        }
    }
}
