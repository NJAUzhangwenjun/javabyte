package cn.wjhub.bytebuddy.annotationService.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * rpc网关方法
 *
 * @author zhangwenjun
 * @date 2022/12/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RpcGatewayMethod {
    String methodName() default "";

    String methodDesc() default "";

}