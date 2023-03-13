package cn.wjhub.bytebuddy.annotationService.springMock;

import groovy.mock.interceptor.MockInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class MockProcessor implements BeanFactoryPostProcessor {
    private static List<Class<?>> mockClassList = new ArrayList<>();

    public static List<Class<?>> getMockClassList() {
        return mockClassList;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            String className = beanDefinition.getBeanClassName();
            if (className == null) {
                continue;
            }
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(MockClass.class)) {
                    mockClassList.add(clazz);
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(MockMethod.class)) {
                            String mockMethodName = method.getAnnotation(MockMethod.class).value();
                            new ByteBuddy().redefine(clazz)
                                    .method(named(mockMethodName))
                                    .intercept(MethodDelegation.to(MockInterceptor.class))
                                    .make()
                                    .load(MockProcessor.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}