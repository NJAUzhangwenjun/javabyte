package cn.wjhub.asm.first.asm;

import cn.wjhub.asm.first.service.ApiTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PreMainTest {

    public static void main(String[] args) {
        log.info("PreMainTest.main");

        ApiTest apiTest = new ApiTest();
        String s = apiTest.queryUserInfo();
        System.out.println("s = " + s);
    }
}