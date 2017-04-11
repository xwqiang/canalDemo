package com.alibaba.otter.canal.sample;

import com.alibaba.otter.canal.common.utils.AddressUtils;

/**
 * Created by xuwuqiang on 2017/2/12.
 */
public class Test {
    public static void main(String[] args) {
        String ip = AddressUtils.getHostIp();
        System.out.println(ip);
    }
}
