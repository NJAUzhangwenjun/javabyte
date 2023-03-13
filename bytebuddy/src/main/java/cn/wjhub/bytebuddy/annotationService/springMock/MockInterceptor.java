package cn.wjhub.bytebuddy.annotationService.springMock;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.concurrent.Callable;

@Slf4j
public class MockInterceptor {
    @RuntimeType
    public static Object intercept(@SuperCall Callable<?> superMethod) throws Exception {
        log.info("s={}", JSON.toJSONString(superMethod));
        return superMethod.call();
    }
}
