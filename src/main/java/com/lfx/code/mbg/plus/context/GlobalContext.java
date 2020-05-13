package com.lfx.code.mbg.plus.context;

import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2020-04-12 14:58:10
 */
public class GlobalContext {

    public static String GENERATE_TIME;

    public static final String DEFAULT_AUTHOR = "<a href=\"mailto:idler41@163.com\">idler41</a>";

    public static Map<String, String> map;

    public static Set<String> excludeTable = new HashSet<>();

    public static Set<String> includeTable = new HashSet<>();
}
