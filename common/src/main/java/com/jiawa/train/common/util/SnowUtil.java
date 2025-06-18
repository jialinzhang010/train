package com.jiawa.train.common.util;

import cn.hutool.core.util.IdUtil;

public class SnowUtil {
    private static long datacenterId = 1;
    private static long workerId = 1;

    public static long getSnowflakeNextId() {
        return IdUtil.getSnowflake(workerId, datacenterId).nextId();
    }
    public static String getSnowflakeIdStr() {
        return IdUtil.getSnowflake(workerId, datacenterId).nextIdStr();
    }
}
