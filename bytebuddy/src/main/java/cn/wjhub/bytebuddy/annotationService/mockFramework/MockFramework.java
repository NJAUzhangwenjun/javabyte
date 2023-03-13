package cn.wjhub.bytebuddy.annotationService.mockFramework;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;

public class MockFramework {

    private final Map<Class<?>, Object> mocks = new HashMap<>();

    public void registerMock(Class<?> clazz, Object mock) {
        mocks.put(clazz, mock);
    }

    public <T> T createMock(Class<T> clazz) {
        return (T) createMockInstance(clazz);
    }

    private Object createMockInstance(Class<?> clazz) {
        if (mocks.containsKey(clazz)) {
            return mocks.get(clazz);
        }
        return createMockProxy(clazz);
    }

    private Object createMockProxy(Class<?> clazz) {
        try {
            return new ByteBuddy()
                    .subclass(clazz)
                    .method(isAnnotatedWith(Mocked.class))
                    .intercept(MethodDelegation.to(MockProxy.class))
                    .make()
                    .load(clazz.getClassLoader())
                    .getLoaded()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class MockProxy {
        @RuntimeType
        public static Object mock(@This Object obj,
                                  @Origin Method method,
                                  @AllArguments Object[] args)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

            System.out.print("Mocked method called: ");
            System.out.println(method.getName());

            Mocked mocked = method.getAnnotation(Mocked.class);
            Method targetMethod = obj.getClass().getMethod(mocked.value(), method.getParameterTypes());
            return targetMethod.invoke(obj, args);
        }
    }
}