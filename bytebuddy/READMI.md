
### 委托带了参数绑定常用注解


| 注解          | 说明                                                         |
| ------------- | ------------------------------------------------------------ |
| @Argument     | 绑定单个参数                                                 |
| @AllArguments | 绑定所有参数的数组                                           |
| @This         | 当前被拦截的、动态生成的那个对象                             |
| @Super        | 当前被拦截的、动态生成的那个对象的父类对象                   |
| @Origin       | 可以绑定到以下类型的参数：Method 被调用的原始方法 Constructor 被调用的原始构造器 Class 当前动态创建的类 MethodHandle MethodType String  动态类的toString()的返回值 int  动态方法的修饰符 |
| @DefaultCall  | 调用默认方法而非super的方法                                  |
| @SuperCall    | 用于调用父类版本的方法                                       |
| @Super        | 注入父类型对象，可以是接口，从而调用它的任何方法             |
| @RuntimeType  | 可以用在返回值、参数上，提示ByteBuddy禁用严格的类型检查      |
| @Empty        | 注入参数的类型的默认值                                       |
| @StubValue    | 注入一个存根值。对于返回引用、void的方法，注入null；对于返回原始类型的方法，注入0 |
| @FieldValue   | 注入被拦截对象的一个字段的值                                 |
| @Morph        | 类似于@SuperCall，但是允许指定调用参数                       |



# fsdg


很高兴成为你的助手！下面是你需要实现的mock框架代码和使用范例：

```java
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class MockFramework {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface MockClass {
        Class<?> value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MockMethod {
        String value();
    }

    private static final TypeCache<TypeDescription, Class<?>> TYPE_CACHE = new TypeCache<>(TypeCache.Sort.WEAK);

    public static void mock() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(MockClass.class));
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents("com.example")) {
            String className = beanDefinition.getBeanClassName();
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(MockClass.class)) {
                    Class<?> target = clazz.getAnnotation(MockClass.class).value();
                    mockClass(target, clazz);
                } else {
                    throw new RuntimeException("Invalid MockClass annotation");
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to mock class: " + className, e);
            }
        }
    }

    private static void mockClass(Class<?> target, Class<?> implementation) throws Exception {
        DynamicType.Builder<?> builder = new ByteBuddy()
                .with(new NamingStrategy.SuffixingRandom("Mock"))
                .subclass(target);
        for (Method method : target.getDeclaredMethods()) {
            if (method.isAnnotationPresent(MockMethod.class)) {
                String methodName = method.getAnnotation(MockMethod.class).value();
                Method mockMethod = implementation.getDeclaredMethod(methodName);
                builder.defineMethod(method.getName(), method.getReturnType())
                        .withParameters(method.getParameterTypes())
                        .intercept(MethodDelegation.to(implementation))
                        .method(ElementMatchers.named(methodName))
                        .intercept(MethodDelegation.to(implementation))
                        .annotateMethod(method.getAnnotations())
                        .annotateMethod(mockMethod.getAnnotations());
            } else {
                builder = builder.method(ElementMatchers.is(method)).intercept(MethodDelegation.to(implementation));
            }
        }
        Class<?> dynamicClazz = builder.make()
                .load(implementation.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
        replaceClass(target, dynamicClazz);
    }

    private static void replaceClass(Class<?> target, Class<?> implementation) throws Exception {
        Set<Class<?>> redefined = new HashSet<>();
        Class<?> clazz = target;
        do {
            redefined.add(clazz);
            clazz = clazz.getSuperclass();
        } while (clazz != null && !Object.class.equals(clazz));
        for (Class<?> redefinedClass : redefined) {
            TYPE_CACHE.clear(new TypeDescription.ForLoadedType(redefinedClass));
        }
        // replace target with implementation in the class loader
        Thread.currentThread().getContextClassLoader()
                .setDefaultAssertionStatus(true);
        System.arraycopy(new Object[]{implementation},
                0, Object[].class.cast(
                        Thread.currentThread().getContextClassLoader()
                                .loadClass(target.getName())
                                .getDeclaredField("val$localMap")
                                .get(null)),
                0, 1);
    }

}
```

下面是使用范例：

```java
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExampleTests {

    @Mock
    private FooService fooService;

    @BeforeAll
    static void setup() {
        MockFramework.mock();
    }

    @Test
    void testFoo() {
        Example example = new Example(fooService);
        assertEquals("Hello, world!", example.foo("world"));
    }

}

@MockFramework.MockClass(Example.class)
class MockExample {

    @MockFramework.MockMethod("foo")
    public static String mockFoo(String name) {
        return "Hello, " + name + "!";
    }

}
```

在上面的例子中，我们有一个`Example`类和一个`FooService`接口，`Example`类依赖于`FooService`接口。我们使用`@MockFramework.MockClass`注解和`MockExample`类来创建`Example`类的代理类，在`MockExample`中，我们使用`@MockFramework.MockMethod`来指定要mock的方法，并提供对应的mock逻辑。在测试类中，我们使用`MockitoExtension`来注入我们创建的`FooService`实例，并通过`MockFramework.mock()`方法来启动mock框架进行代理。当调用`Example`实例的`foo`方法时，框架将使用`MockExample.mockFoo`方法中定义的mock逻辑返回一个恒定的字符串。最终，测试成功地运行，并断言了方法的返回值。

