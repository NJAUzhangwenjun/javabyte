# mock
下面是一个简单的 Java Mock 框架的代码示例，它使用了 ByteBuddy 和 Java 的反射机制：

```java
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import net.bytebuddy.*;
import net.bytebuddy.dynamic.*;
import net.bytebuddy.implementation.*;
import net.bytebuddy.implementation.bind.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Mocked {}

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
            throws IllegalAccessException, InvocationTargetException {

            System.out.print("Mocked method called: ");
            System.out.println(method.getName());

            Mocked mocked = method.getAnnotation(Mocked.class);
            Method targetMethod = obj.getClass().getMethod(mocked.value(), method.getParameterTypes());
            return targetMethod.invoke(obj, args);
        }
    }
}
```

MockFramework 类是整个框架的主要类，它负责注册和创建模拟对象，以及通过 ByteBuddy 和 Java 反射机制创建模拟代理。

MockFramework.registerMock（）方法允许用户自己创建模拟对象并注册，MockFramework.createMock（）方法用于创建模拟代理。

MockFramework.createMockProxy（）方法使用 ByteBuddy 创建模拟代理类。只有被 Mocked 注解所标注的方法才会被代理，它将被重定向到 MockProxy 类的 mock 方法中，MockProxy.mock 方法实现了该方法调用的流程。它首先打印出被调用的方法的名称，然后获取 Mocked 注解中指定的目标方法并调用该方法。

使用示例：

```java
public interface UserService {
    void login(String username, String password);
}

public class UserServiceImpl implements UserService {
    @Override
    public void login(String username, String password) {
        System.out.println("login succeeded");
    }
}

public class Test {
    public static void main(String[] args) {
        MockFramework mockFramework = new MockFramework();
        UserService userServiceMock = new UserService() {
            @Mocked("login")
            @Override
            public void login(String username, String password) {
                System.out.println("login failed");
            }
        };
        mockFramework.registerMock(UserService.class, userServiceMock);

        UserService userService = mockFramework.createMock(UserService.class);
        userService.login("test", "111111");
    }
}
```

此示例的输出应该是：

```
Mocked method called: login
login failed
```

该示例中创建了一个 UserService 的模拟对象，其 login 方法被 Mocked 注解所标注以便被代理，它将被替换为另一个方法，该方法将打印出信息并调用原始目标方法进行工作。然后，这个模拟对象被注册到 MockFramework 类中，并通过 MockFramework 来创建一个模拟代理。最后，模拟对象的 login 方法被调用，并输出打印出模拟代理方法被调用的信息。