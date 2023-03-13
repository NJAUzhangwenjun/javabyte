package cn.wjhub.asm.first.asm;

import cn.wjhub.utils.FileUtil;
import javassist.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.*;

/**
 * 监控类入口
 *
 * @author zhangwenjun
 * @date 2022/12/22
 */
@Slf4j
public class PreMain {

    private static final Map<String, String> paths = new HashMap<>();
    private static final Map<String, String> names = new HashMap<>();
    private static final String namePath = "name-path.json";

    static {
        try {
            Map<String, String> map = FileUtil.getMap(namePath, String.class);
            if (!CollectionUtils.isEmpty(map)) {
                names.putAll(map);
                map.forEach((key, value) -> paths.put(key, key.replaceAll("/", ".")));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void premain(String arg, Instrumentation inst) {
        log.info("PreMain.premain");
        ClassPool pool = new ClassPool();
        pool.appendSystemPath();

        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {

            // System.out.println(className);
            if (!paths.containsKey(className)) {
                return null;
            }
            String fallName = paths.get(className);
            String methodName = names.get(className);
            if (!StringUtils.hasText(methodName)) {
                return null;
            }

            try {
                CtClass ctClass = pool.get(fallName);
                CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
                for (CtMethod method : declaredMethods) {

                    CtMethod copy = CtNewMethod.copy(method, ctClass, null);
                    String newName = method.getName() + "$agent";
                    copy.setName(newName);
                    ctClass.addMethod(copy);


                    String newBody = getBefore() + "Object ret = " + newName + "();\n" + getAfter() + "return  ret;";
                    String src = "{" + newBody + "}";
                    method.setBody(src);
                }
                return ctClass.toBytecode();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    private static String getAfter() {

        return "long after = System.currentTimeMillis();\n" +
                "System.out.println(\"time1=\"+(after-before));\n";
    }

    private static String getBefore() {

        return "long before = System.currentTimeMillis();\n";
    }

}