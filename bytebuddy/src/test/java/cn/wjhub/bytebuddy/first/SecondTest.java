package cn.wjhub.bytebuddy.first;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.named;

@Slf4j
public class SecondTest {
    @Test
    public void dynamicTypeSubclass() throws IOException {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                .subclass(Object.class) // 生成 Object的子类
                .name("cn.wjhub.Type")   // 生成类的名称为"cn.wjhub.Type"
                .make();
        // 保存生成后的代码到指定位置
        dynamicType.saveIn(new File(getClass().getResource("/").getPath()));
    }

    @Test
    public void dynamicTypeCrateMethodType() throws IOException {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                .subclass(Object.class) // 生成 Object的子类
                .name("cn.wjhub.NewType")   // 生成类的名称为"cn.wjhub.NewType"
                // 定义sayHello方:名称、返回类型、属性public static void
                .defineMethod("sayHello", String.class, Modifier.PUBLIC + Modifier.STATIC)
                // 定义参数:参数类型、参数名称
                .withParameter(String[].class, "args")
                .intercept(FixedValue.value("hello byteBuddy  main !"))
                // 拦截父类 toString 方法
                .method(named("toString"))
                .intercept(FixedValue.value("hello byteBuddy  toString !"))
                .make();
        // 保存生成后的代码到指定位置
        dynamicType.saveIn(new File(Objects.requireNonNull(getClass().getResource("/")).getPath()));
    }

    @Test
    public void dynamicTypeCrateMethodTypeAndImplementAndField() throws IOException {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                .subclass(Object.class) // 生成 Object的子类
                .name("cn.wjhub.bytebuddy.first.ImplementAndFieldObject")   // 生成类的名称为"cn.wjhub.bytebuddy.first.ImplementAndFieldObject"
                // 定义sayHello方:名称、返回类型、属性public static void
                .defineMethod("sayHello", String.class, Modifier.PUBLIC + Modifier.STATIC)
                // 定义参数:参数类型、参数名称
                .withParameter(String[].class, "args")
                .intercept(FixedValue.value("hello byteBuddy  main !"))
                // 拦截父类 toString 方法
                .method(named("toString"))
                .intercept(FixedValue.value("hello byteBuddy  toString !"))

                // 定义属性
                .defineField("name", String.class, Modifier.PUBLIC)
                // 实现接口
                .implement(User.class)
                // 实现接口的方式是读写 name字段
                .intercept(FieldAccessor.ofField("name"))
                .make();
        // 保存生成后的代码到指定位置
        dynamicType.saveIn(new File(Objects.requireNonNull(getClass().getResource("/")).getPath()));
    }

    interface User {
        public String getUserName();

        public void setUserName(String name);
    }


    @Test
    public void dynamicTypeMethodDelegationTOStaticMethod() throws IOException, InstantiationException, IllegalAccessException {

        DynamicType.Unloaded<Object> user = new ByteBuddy()
                .subclass(Object.class)
                .name("cn.wjhub.bytebuddy.first.StaticMethodObject")
                //            自定义方法
                .defineMethod("getUserAge", Integer.class, Modifier.PUBLIC)
                .withParameter(String.class, "username")
                .intercept(MethodDelegation.to(StaticMethodUser.class))
                .make();
        // 保存生成后的代码到指定位置
        user.saveIn(new File(Objects.requireNonNull(getClass().getResource("/")).getPath()));

    }

    static class StaticMethodUser {
        public static Integer getAge(String username) {
            return username.length();
        }
    }

    @Test
    public void dynamicTypeMethodDelegationTOStaticMethodInvoke() throws Exception {

        Class<?> userClass = new ByteBuddy()
                .subclass(Object.class)
                .name("cn.wjhub.bytebuddy.first.MethodInvokeObject")
                //            自定义方法
                .defineMethod("getUserAge", Integer.class, Modifier.PUBLIC)
                .withParameter(String.class, "username")
                .intercept(MethodDelegation.to(StaticMethodUser.class))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        Object user = userClass.newInstance();

        String username = "zhangSan";
        Integer age = (Integer) userClass.getDeclaredMethod("getUserAge", String.class).invoke(user, username);
        log.info("userAge={}", age);
        Assert.assertEquals(age, ((Integer) username.length()));
    }
}
