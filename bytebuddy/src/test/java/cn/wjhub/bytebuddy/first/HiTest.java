// package cn.wjhub.bytebuddy.first;
//
// import lombok.ToString;
// import net.bytebuddy.ByteBuddy;
// import net.bytebuddy.NamingStrategy;
// import net.bytebuddy.agent.builder.AgentBuilder;
// import net.bytebuddy.description.type.TypeDescription;
// import net.bytebuddy.dynamic.ClassFileLocator;
// import net.bytebuddy.dynamic.DynamicType;
// import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
// import net.bytebuddy.implementation.FixedValue;
// import net.bytebuddy.implementation.bind.annotation.SuperCall;
// import net.bytebuddy.matcher.ElementMatchers;
// import net.bytebuddy.pool.TypePool;
// import net.bytebuddy.utility.JavaModule;
// import org.junit.Assert;
// import org.junit.Test;
//
// import java.io.File;
// import java.io.IOException;
// import java.lang.instrument.Instrumentation;
// import java.security.ProtectionDomain;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Objects;
// import java.util.concurrent.Callable;
//
// import static net.bytebuddy.matcher.ElementMatchers.*;
// import static org.hamcrest.CoreMatchers.notNullValue;
// import static org.junit.Assert.assertThat;
//
// public class HiTest {
//
//     @Test
//     public void main() throws InstantiationException, IllegalAccessException, IOException {
//
//
//         DynamicType.Loaded<Object> dynamicType = new ByteBuddy()
//                 .subclass(Object.class)
//                 .name("NewSubObject")
//                 .method(named("toString"))
//                 .intercept(FixedValue.value("hello byte buddy !"))
//                 .make()
//                 .load(Hi.class.getClassLoader());
//
//         String s = dynamicType
//                 .getLoaded()
//                 .newInstance()
//                 .toString();
//
//
//         dynamicType.saveIn(new File(Objects.requireNonNull(Hi.class.getResource("/")).getPath()));
//
//         System.out.println("s = " + s);
//     }
//
//     @Test
//     public void testRebaseObjectToString() throws InstantiationException, IllegalAccessException, IOException {
//         DynamicType.Loaded<Object> dynamicType = new ByteBuddy()
//                 .rebase(Object.class)
//                 .name("RebaseNewSubObject")
//                 .method(named("toString"))
//                 .intercept(FixedValue.value("hello byte buddy !"))
//                 .make()
//                 .load(Object.class.getClassLoader());
//
//         String s = dynamicType
//                 .getLoaded()
//                 .newInstance()
//                 .toString();
//
//
//         dynamicType.saveIn(new File(Objects.requireNonNull(Hi.class.getResource("/")).getPath()));
//
//         System.out.println("s = " + s);
//     }
//
//     @Test
//     public void testByteBuddyQuickStart() {
//
//         DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
//                 .subclass(Object.class) // 通过创建子类的方式创建增强类
//                 .name("example.Type")   // 如果不输入类名,则会给一个 默认类名
//                 .make();                //
//     }
//
//     @Test
//     public void testByteBuddyQuickStart1() {
//
//         DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
//                 .with(new NamingStrategy.SuffixingRandom("cn.wjhub."))
//                 .subclass(Object.class)
//                 .make();
//
//     }
//
//     @Test
//     public void testByteBuddyQuickStart2() {
//
//         Class<?> type = new ByteBuddy()
//                 .subclass(Object.class)
//                 .make()
//                 .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
//                 .getLoaded();
//
//     }
//
//     @Test
//     public void testByteBuddyQuickStart3() throws Exception {
//         TypePool typePool = TypePool.Default.ofSystemLoader();
//         DynamicType.Unloaded<Object> dynamicType = new ByteBuddy()
//                 .redefine(typePool.describe("cn.wjhub.bytebuddy.first.Bar").resolve(), // do not use 'Bar.class'
//                         ClassFileLocator.ForClassLoader.ofSystemLoader())
//                 .defineField("qux", String.class) // we learn more about defining fields later
//                 .make();
//
//         Class<?> bar = dynamicType
//                 .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION)
//                 .getLoaded();
//         dynamicType.saveIn(new File(Objects.requireNonNull(Hi.class.getResource("/")).getPath()));
//         assertThat(bar.getDeclaredField("qux"), notNullValue());
//     }
//
//     public static void premain(String arguments, Instrumentation instrumentation) {
//         new AgentBuilder.Default()
//                 .type(ElementMatchers.isAnnotatedWith(ToString.class))
//                 .transform((builder, typeDescription, classLoader, module, protectionDomain) -> builder.method(named("toString"))
//                         .intercept(FixedValue.value("transformed"))).installOn(instrumentation);
//     }
//
//     @Test
//     public void testMethod1() throws InstantiationException, IllegalAccessException {
//
//
//         Foo dynamicFoo = new ByteBuddy()
//                 .subclass(Foo.class)
//                 .method(isDeclaredBy(Foo.class)).intercept(FixedValue.value("One!"))
//                 .method(named("foo")).intercept(FixedValue.value("Two!"))
//                 .method(named("foo").and(takesArguments(1))).intercept(FixedValue.value("Three!"))
//                 .make()
//                 .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
//                 .getLoaded()
//                 .newInstance();
//         Assert.assertEquals(dynamicFoo.bar(), "One!");
//         Assert.assertEquals(dynamicFoo.foo(), "Two!");
//         Assert.assertEquals(dynamicFoo.foo("s"), "Three!");
//
//     }
// }
//
// class MemoryDatabase {
//     public List<String> load(String info) {
//         return Arrays.asList(info + ": foo", info + ": bar");
//     }
// }
//
// class LoggerInterceptor {
//     public static List<String> log(@SuperCall Callable<List<String>> zuper)
//             throws Exception {
//         System.out.println("Calling database");
//         try {
//             return zuper.call();
//         } finally {
//             System.out.println("Returned from database");
//         }
//     }
// }
//
// class Bar {
// }
//
// class Foo {
//     public String bar() {
//         return null;
//     }
//
//     public String foo() {
//         return null;
//     }
//
//     public String foo(Object o) {
//         return null;
//     }
// }