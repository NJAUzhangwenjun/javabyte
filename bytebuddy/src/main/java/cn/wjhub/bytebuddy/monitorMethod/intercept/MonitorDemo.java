package cn.wjhub.bytebuddy.monitorMethod.intercept;

import cn.wjhub.bytebuddy.monitorMethod.service.BizMethod;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 监控演示
 *
 * @author zhangwenjun
 * @date 2022/12/25
 */
@Slf4j
public  class MonitorDemo {
    public static String queryUserInfo(
            @This Object obj, // 目标对象
            @AllArguments Object[] allArguments, // 注入目标方法的全部参数
            @SuperCall Callable<?> zuper, // 用于调用父类版本的方法
            @Origin Method targetMethod, // 目标方法
            @Super BizMethod targetObject // 目标父类对象
    ) throws Exception {
        log.info("-------------------MonitorDemo log start");
        long start = System.currentTimeMillis();
        try {
            return (String) zuper.call();
        } finally {
            long end = System.currentTimeMillis();

            log.info("----------------MonitorDemo log end");

            log.info("耗时:{}", end - start);
        }
    }
}
