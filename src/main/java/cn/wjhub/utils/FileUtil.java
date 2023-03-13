package cn.wjhub.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class FileUtil {

    public static <T> T getObject(String jsonFilePath, Class<T> cls) throws IOException {
        if (!StringUtils.hasLength(jsonFilePath) || jsonFilePath.endsWith(".json")) {
            throw new IllegalArgumentException("文件类型不匹配");
        }
        // 读取json文件
        ClassPathResource resource = new ClassPathResource(jsonFilePath);
        // 获取文件流
        InputStream inputStream = resource.getInputStream();
        String content = IOUtils.toString(inputStream, "utf8");
        // 转为实体类
        return JSON.parseObject(content, cls);
    }

    public static <T> List<T> getList(String jsonFilePath, Class<T> cls) throws IOException {
        if (!StringUtils.hasLength(jsonFilePath) || jsonFilePath.endsWith(".json")) {
            throw new IllegalArgumentException("文件类型不匹配");
        }
        ClassPathResource resource = new ClassPathResource(jsonFilePath);
        // 获取文件流
        InputStream inputStream = resource.getInputStream();

        String content = IOUtils.toString(inputStream, "utf8");
        return JSON.parseArray(content, cls);
    }


    public static <T> Map<String, T> getMap(String jsonFilePath, Class<T> cls) throws IOException {
        if (!StringUtils.hasLength(jsonFilePath) || !jsonFilePath.endsWith(".json")) {
            throw new IllegalArgumentException("文件类型不匹配");
        }
        ClassPathResource resource = new ClassPathResource(jsonFilePath);
        // 获取文件流
        InputStream inputStream = resource.getInputStream();

        String content = IOUtils.toString(inputStream, "utf8");
        return JSON.parseObject(content, new TypeReference<Map<String, T>>() {});
    }


}