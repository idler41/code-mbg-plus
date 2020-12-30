package com.lfx.code.mbg.plus.context;

import com.lfx.code.mbg.plus.util.PlaceHolderProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.XMLParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author linfuxin
 * @date 2020-12-29 15:28:02
 */
@Slf4j
public class PlusContext {

    public static final Map<String, String> CONFIGURATION = new HashMap<>();

    public static final List<String> WARNINGS = new ArrayList<>();

    public static final String DEFAULT_AUTHOR = "<a href=\"mailto:idler41@163.com\">idler41</a>";

    public static Configuration CONFIG;

    private static final String DEFAULT_INIT = "init.properties";

    private static final String[] PARAM_NOT_NULL = {
            "jdbc.driver", "jdbc.url", "jdbc.username", "jdbc.password", "target.project.home",
            "target.project.name", "target.project.package.model", "target.project.package.mapper", "plugin.template.home"
    };

    private static final String DEFAULT_XML = "generator.xml";

    private static final Set<String> EXCLUDE_TABLE_CACHE = new HashSet<>();

    private static final Set<String> INCLUDE_TABLE_CACHE = new HashSet<>();

    public static void initContext() {
        log.info("mbg-plus上下文初始化开始!");

        String initFilePath = System.getProperties().containsKey("init.path") ?
                System.getProperty("init.path") : Optional.ofNullable(PlusContext.class.getClassLoader().getResource(DEFAULT_INIT)).map(URL::getPath).orElse(null);
        if (initFilePath == null) {
            throw new RuntimeException("请指定配置文件路径!");
        }
        File initFile = new File(initFilePath);
        if (!initFile.exists()) {
            throw new RuntimeException("找不到配置文件!文件路径=" + initFilePath);
        }

        log.info("加载mbg-plus配置文件:{}", initFilePath);
        PlaceHolderProperties extraProperties = loadConfig(initFile);
        extraProperties.forEach((k, v) -> CONFIGURATION.put(k.toString(), v.toString()));
        log.info("解析mbg-plus配置文件完成!");
        ConfigurationParser cp = new ConfigurationParser(extraProperties, WARNINGS);
        try {
            CONFIG = cp.parseConfiguration(PlusContext.class.getClassLoader().getResourceAsStream(DEFAULT_XML));
            log.info("解析mbg配置文件完成!");
        } catch (IOException | XMLParserException e) {
            throw new RuntimeException(e);
        }

        log.info("mbg-plus上下文初始化完成!");
    }

    public static boolean isIncludeTable(String tableName) {
        return INCLUDE_TABLE_CACHE.isEmpty() || INCLUDE_TABLE_CACHE.contains(tableName);
    }

    public static boolean isExcludeTable(String tableName) {
        return EXCLUDE_TABLE_CACHE.contains(tableName);
    }

    public static String getProperty(String key) {
        return CONFIGURATION.get(key);
    }

    public static boolean isScanAllTable() {
        return "scanAllTable".equals(CONFIG.getContexts().get(0).getTableConfigurations().get(0).getTableName());
    }

    public static boolean isIgnoreFile() {
        return "ignore".equals(CONFIGURATION.get("target.file.exist.strategy"));
    }

    public static boolean isOverrideFile() {
        return "override".equals(CONFIGURATION.get("target.file.exist.strategy"));
    }

    private static PlaceHolderProperties loadConfig(File file) {
        // 解析配置文件
        PlaceHolderProperties extraProperties = new PlaceHolderProperties();
        try {
            InputStream inputStream = new FileInputStream(file);
            extraProperties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            // 解析占位符
            for (Object key : extraProperties.orderKeySet()) {
                String k = key.toString().trim();
                String v = System.getProperty(k);
                // 系统变量覆盖配置信息
                v = StringUtils.isEmpty(v) ? extraProperties.getProperty(k).trim() : v;
                extraProperties.setProperty(k, v);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 校验配置信息
        for (String param : PARAM_NOT_NULL) {
            if (StringUtils.isEmpty(extraProperties.getProperty(param))) {
                log.error("key={},value=null,请参考提示，输入必要参数!", param);
                log.error("非空参数包含:\n{}", String.join("\n", PARAM_NOT_NULL));
                throw new IllegalArgumentException();
            }
        }

        // 打印配置信息
        if (log.isDebugEnabled()) {
            extraProperties.forEach((k, v) -> log.debug("{}={}", k, v));
        }

        // 添加自定义默认配置
        setDefaultProperty(extraProperties);

        String includeTable = extraProperties.getProperty("scan.table.include");
        if (StringUtils.isNotEmpty(includeTable)) {
            INCLUDE_TABLE_CACHE.addAll(Arrays.asList(includeTable.split(",")));
        }

        String excludeTable = extraProperties.getProperty("scan.table.exclude");
        if (StringUtils.isNotEmpty(excludeTable)) {
            EXCLUDE_TABLE_CACHE.addAll(Arrays.asList(excludeTable.split(",")));
        }
        return extraProperties;
    }

    private static void setDefaultProperty(PlaceHolderProperties extraProperties) {
        extraProperties.setProperty("generateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        if (StringUtils.isEmpty(extraProperties.getProperty("comment.author"))) {
            extraProperties.put("author", PlusContext.DEFAULT_AUTHOR);
        }
    }
}
