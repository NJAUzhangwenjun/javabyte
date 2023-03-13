package cn.wjhub.bytebuddy.annotationService.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * rpc网关 模拟网关类注解
 *
 * @author zhangwenjun
 * @date 2022/12/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcGatewayClazz {
    String clazzDesc() default "";

    String alias() default "";

    long timeOut() default 350;
}