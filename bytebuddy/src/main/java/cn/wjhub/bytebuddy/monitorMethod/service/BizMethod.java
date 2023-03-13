package cn.wjhub.bytebuddy.monitorMethod.service;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 业务方法
 *
 * @author zhangwenjun
 * @date 2022/12/25
 */
@Slf4j
public class BizMethod {
    /**
     * 查询用户信息
     *
     * @param uid   uid
     * @param token 令牌
     * @return {@link String}
     * @throws InterruptedException 中断异常
     */
    public String queryUserInfo(String uid, String token) throws InterruptedException {
        Thread.sleep(new Random().nextInt(500));
        System.out.println("BizMethod.queryUserInfo");
        return String.format("hello wjhub.cn !,uid=%s,token=%s", uid, token).toString();
    }
}