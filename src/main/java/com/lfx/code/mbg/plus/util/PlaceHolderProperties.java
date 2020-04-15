package com.lfx.code.mbg.plus.util;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参考链接：https://github.com/tcking/PlaceHolderProperties/blob/master/src/com/github/tcking/PlaceHolderProperties.java
 * <pre>
 * 支持placeHolder的Properties，例如：
 * app_home=/giraffe
 * cache_dir=${app_home}/cache
 * image_cache=${cache_dir}/imageCache
 * log_file=${app_home}/app.log
 * </pre>
 *
 * 更改部分代码:
 *
 * 1. 修改了正则表达式
 *  原代码的正则表达式在多个占位符的场景下会解析异常,如: ${project.home}/${project.name}
 * 2. 属性的顺序遍历
 *  顺序打印配置信息
 *
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2020-04-12 10:23:22
 */
public class PlaceHolderProperties extends Properties {

    private static Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");

    private final LinkedHashSet<Object> keys = new LinkedHashSet<>();

    public Set<Object> orderKeySet() {
        return keys;
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }

    @Override
    public synchronized void load(Reader in) throws IOException {
        super.load(in);
    }

    @Override
    public String getProperty(String name) {
        String value = super.getProperty(name);
        if (value == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String group = matcher.group(1);
            Set<String> input = new HashSet<>();
            input.add(name);
            String replace = getPlaceHolderProperties(group, input);
            if (replace != null) {
                value = matcher.replaceFirst(replace);
                matcher.reset(value);
            } else {
                throw new RuntimeException("Can't parse placeHoler:" + name);
            }
        }
        return value;
    }

    private String getPlaceHolderProperties(String name, Set<String> input) {
        String value = super.getProperty(name);
        if (value == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            if (input.contains(name)) {
                throw new RuntimeException("circular reference :" + name);
            } else {
                input.add(name);
            }
            String group = matcher.group(1);
            String replace = getPlaceHolderProperties(group, input);
            if (replace != null) {
                value = matcher.replaceFirst(replace);
            }
        }
        return value;
    }
}