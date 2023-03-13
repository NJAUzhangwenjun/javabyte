package cn.wjhub.bytebuddy.first;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.named;

@Slf4j
public class ThreeTest {
    @Test
    public void testAnnotationSuperCall() throws Exception {
        DynamicType.Unloaded<MemoryDatabase> dynamicType = new ByteBuddy()
                .subclass(MemoryDatabase.class)
                .name("cn.wjhub.bytebuddy.first.LoggerMemoryDatabase")
                .method(named("load"))
                .intercept(MethodDelegation.to(LoggerInterceptor.class))
                .make();

        // 保存生成后的代码到指定位置
        dynamicType.saveIn(new File(Objects.requireNonNull(getClass().getResource("/")).getPath()));

        Class<? extends MemoryDatabase> dbClass = dynamicType
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
        MemoryDatabase loggingDatabase = dbClass
                .newInstance();
        Object invoke = dbClass.getDeclaredMethod("load", String.class).invoke(loggingDatabase, "user");

        List<String> list = (List) invoke;
        log.info(list.toString());


    }


    @Test
    public void testAnnotationMorph() throws Exception {
        List<String> list = new ByteBuddy()
                .subclass(MemoryDatabase.class)
                .name("cn.wjhub.bytebuddy.first.LoggerMemoryDatabase")
                .method(named("load"))
                .intercept(MethodDelegation.withDefaultConfiguration()
                        .withBinders(
                                // 要用Morph之前,需要通过Morph.Binder告诉Byte Buddy要注入的参数类型是什么
                                Morph.Binder.install(List.class)
                        )
                        .to(LoggerInterceptor.class)
                ).make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .newInstance()
                .load("zhngsan");

        log.info(list.toString());

    }

    /**
     * 内存数据库操作
     *
     * @author zhangwenjun
     * @date 2022/12/24
     */
    static class MemoryDatabase {
        public List<String> load(String info) {
            return Arrays.asList(info + ": foo", info + ": bar");
        }
    }

    /**
     * 记录器拦截器
     *
     * @author zhangwenjun
     * @date 2022/12/24
     */
    static class LoggerInterceptor {
        public static List<String> log(
                @This Object obj, // 目标对象
                @AllArguments Object[] allArguments, // 注入目标方法的全部参数
                @SuperCall Callable<?> zuper, // 调用目标方法，必不可少
                @Origin Method method, // 目标方法
                @Super MemoryDatabase delegeteFoo) // 目标对象
                throws Exception {
            log.info("Calling database");
            try {
                return (List<String>) zuper.call();
            } finally {
                log.info("Returned from database");
            }
        }
    }

}
