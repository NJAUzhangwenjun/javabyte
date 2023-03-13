package cn.wjhub.bytebuddy.monitorMethod.service;

import cn.wjhub.bytebuddy.monitorMethod.intercept.MonitorDemo;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.named;

@Slf4j
public class BizMethodTest {

    @Test
    public void test_byteBuddy() throws Exception {
        DynamicType.Unloaded<BizMethod> queryUserInfo = new ByteBuddy()
                .subclass(BizMethod.class)
                .method(named("queryUserInfo"))
                .intercept(MethodDelegation.to(new MonitorDemo()))
                .make();

        String userInfo = queryUserInfo
                // 加载
                .load(BizMethodTest.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded()
                // 调用
                .newInstance()
                .queryUserInfo("3231", "wjhubb3231");

        queryUserInfo.saveIn(new File(Objects.requireNonNull(getClass().getResource("/")).getPath()));

        log.info("userInfo={}", JSON.toJSONString(userInfo));
    }

    @Test
    public void queryUserInfo() {

    }
}