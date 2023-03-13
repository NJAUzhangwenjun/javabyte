package cn.wjhub;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.ProtectionDomain;
import java.sql.Statement;

public class Main {

    private static final String mybatisPath = "org.apache.ibatis.executor.statement.PreparedStatementHandler";
    private static final String mybatisMethod = "parameterize";
    private static final String sqlPath = "java.sql.Statement";


    public static void premain(String arg, Instrumentation instrumentation) {

        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(
                    ClassLoader loader,
                    String className,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer) {

                if (!mybatisPath.replaceAll("\\.", "/").equals(className)) {
                    return null;
                }
                ClassPool pool = new ClassPool();
                pool.appendClassPath(new LoaderClassPath(loader));
                pool.appendSystemPath();

                try {
                    CtClass ctClass = pool.get(mybatisPath);
                    CtMethod method = ctClass.getDeclaredMethod(mybatisMethod, new CtClass[]{pool.get(sqlPath)});
                    method.insertAfter("cn.wjhub.Main#printSQL($1)");

                    return ctClass.toBytecode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    /**
     * printSQL
     *
     * @param statement statement
     */
    private void printSQL(Statement statement) {
        if (Proxy.isProxyClass(statement.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(statement);
            Class<? extends InvocationHandler> handlerClass = handler.getClass();
            if (!handlerClass.getName().equals(mybatisMethod)) {
                return;
            }
            try {
                Field field = handlerClass.getDeclaredField("statement");
                field.setAccessible(Boolean.TRUE);
                statement = (Statement) field.get(handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("-------------sql-start------------");
        System.out.println(statement.toString());
        System.out.println("-------------sql-end------------");
    }
}