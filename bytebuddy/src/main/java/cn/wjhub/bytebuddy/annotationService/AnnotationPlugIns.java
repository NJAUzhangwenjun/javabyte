package cn.wjhub.bytebuddy.annotationService;

import cn.wjhub.bytebuddy.annotationService.annotation.RpcGatewayClazz;
import cn.wjhub.bytebuddy.annotationService.annotation.RpcGatewayMethod;

public class AnnotationPlugIns {

    @RpcGatewayClazz(
            clazzDesc = "查询数据信息",
            timeOut = 350L,
            alias = "dataApi"
    )
    public static class UserRepository {
        @RpcGatewayMethod(
                methodDesc = "查询数据",
                methodName = "queryData"
        )
        public static String queryData(int var1) {
            return "我是代理信息";
        }
    }
}
