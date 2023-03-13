// package cn.wjhub.bytebuddy.review;
//
// import cn.wjhub.bytebuddy.annotationService.annotation.RpcGatewayClazz;
// import cn.wjhub.bytebuddy.annotationService.annotation.RpcGatewayMethod;
// import lombok.extern.slf4j.Slf4j;
// import net.bytebuddy.ByteBuddy;
// import net.bytebuddy.description.annotation.AnnotationDescription;
// import net.bytebuddy.dynamic.DynamicType;
// import net.bytebuddy.matcher.ElementMatchers;
// import org.junit.Test;
//
// @Slf4j
// public class ByteBuddyTest {
//     @Test
//     public void testLoadClass() {
//         DynamicType.Builder<Object> builder = new ByteBuddy()
//                 .subclass(Object.class)
//                 .annotateType(AnnotationDescription.Builder.ofType(RpcGatewayClazz.class).build());
//         Class<? extends DynamicType.Loaded> sClass = (Class<? extends DynamicType.Loaded>) builder
//
//         log.info("sClass:{}",sClass.toString());
//         RpcGatewayClazz gatewayClazz = sClass.getDeclaredAnnotation(RpcGatewayClazz.class);
//         log.info("clazzDesc:{}",gatewayClazz.clazzDesc());
//         log.info("alias:{}",gatewayClazz.alias());
//         log.info("timeOut:{}",gatewayClazz.timeOut());
//         log.info("simpleName:{}",sClass.getSimpleName());
//     }
//
//
// }
