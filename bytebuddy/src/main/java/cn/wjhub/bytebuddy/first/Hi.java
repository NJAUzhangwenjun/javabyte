package cn.wjhub.bytebuddy.first;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Hi {


    public static void main() throws InstantiationException, IllegalAccessException, IOException {


        DynamicType.Unloaded<Object> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .name("NewSubObject")
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("hello byte buddy !"))
                .make();


        String s = dynamicType.load(Hi.class.getClassLoader())
                .getLoaded()
                .newInstance()
                .toString();

        dynamicType.saveIn(new File(Objects.requireNonNull(Hi.class.getResource("/")).getPath()));

        System.out.println("s = " + s);
    }
}
