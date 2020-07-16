package com.lfx.code.mbg.plus;

import com.lfx.code.mbg.plus.callback.MyShellCallback;
import com.lfx.code.mbg.plus.context.GlobalContext;
import com.lfx.code.mbg.plus.util.PlaceHolderProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2020-04-12 10:29:40
 */
@Slf4j
public class App {

    private static final String DEFAULT_INIT = "init.properties";

    private static final String DEFAULT_XML = "generator.xml";

    public static void main(String[] args) throws Exception {
        log.info("代码生成器运行开始...");
        // 读取配置信息 + 校验必填参数
        PlaceHolderProperties extraProperties = loadProperties();
        validProp(extraProperties);

        // mbg参数初始化 + context初始化
        initGlobalContext(extraProperties);

        // mbg生成代码
        List<String> warnings = new ArrayList<>();
        ConfigurationParser cp = new ConfigurationParser(extraProperties, warnings);
        Configuration config = cp.parseConfiguration(App.class.getClassLoader().getResourceAsStream(DEFAULT_XML));
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, new MyShellCallback(true), warnings);
        myBatisGenerator.generate(null);
        log.info("代码生成器运行结束...");
    }

    private static void initGlobalContext(PlaceHolderProperties extraProperties) {
        GlobalContext.GENERATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (StringUtils.isEmpty(extraProperties.getProperty("project.author"))) {
            extraProperties.put("author", GlobalContext.DEFAULT_AUTHOR);
        }

        extraProperties.put("generateTime", GlobalContext.GENERATE_TIME);

        String includeTable = extraProperties.getProperty("tables.include");
        if (StringUtils.isNotEmpty(includeTable)) {
            String[] tables = includeTable.split(",");
            GlobalContext.includeTable.addAll(Arrays.asList(tables));
        }

        String excludeTable = extraProperties.getProperty("tables.exclude");
        if (StringUtils.isNotEmpty(excludeTable)) {
            String[] tables = excludeTable.split(",");
            GlobalContext.excludeTable.addAll(Arrays.asList(tables));
        }

        GlobalContext.map = new HashMap<String, String>((Map) extraProperties);
    }

    private static PlaceHolderProperties loadProperties() throws IOException {
        PlaceHolderProperties extraProperties = new PlaceHolderProperties();
        String initFilePath = System.getProperty("init.path");
        InputStream inputStream = StringUtils.isNotEmpty(initFilePath) ? new FileInputStream(new File(initFilePath)) : App.class.getClassLoader().getResourceAsStream(DEFAULT_INIT);
        assert inputStream != null;
        extraProperties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        // 解析占位符
        for (Object key : extraProperties.orderKeySet()) {
            String k = key.toString().trim();
            String v = System.getProperty(k);
            // 系统变量覆盖参数
            v = StringUtils.isEmpty(v) ? extraProperties.getProperty(k).trim() : v;
            extraProperties.setProperty(k, v);
            if (log.isDebugEnabled()) {
                log.debug("{}={}", k, v);
            }
        }
        return extraProperties;
    }

    private static final String[] PARAM_NOT_NULL = {
            "jdbc.driver", "jdbc.url", "jdbc.username", "jdbc.password",
            "project.home", "project.name", "project.package.model", "project.package.mapper", "template.home"
    };

    private static void validProp(PlaceHolderProperties extraProperties) {
        for (String param : PARAM_NOT_NULL) {
            if (StringUtils.isEmpty(extraProperties.getProperty(param))) {
                log.error("key={},value=null,请参考提示，输入必要参数!", param);
                log.error("############################ 必填参数 ############################\n" +
                        "jdbc.driver=com.mysql.cj.jdbc.Driver\n" +
                        "jdbc.url=jdbc:mysql://localhost:3306/oms?useUnicode=true&characterEncoding=UTF-8\n" +
                        "jdbc.username=root\n" +
                        "jdbc.password=root\n" +
                        "\n" +
                        "## 模板路径\n" +
                        "### linux\n" +
                        "template.home=/Users/apple/GitHub/code-mbg-plus/src/main/resources/velocity\n" +
                        "### windows\n" +
                        "#template.home=G:\\\\mbg-file\\\\velocity\n" +
                        "## 生成项目路径\n" +
                        "### linux\n" +
                        "project.home=/Users/apple/mywork/micro-service\n" +
                        "### windows\n" +
                        "#project.home=G:\\\\mbg-file\n" +
                        "project.name=demo\n" +
                        "## 生成domain包名\n" +
                        "project.package.model=com.demo.domain\n" +
                        "## 生成mapper包名\n" +
                        "project.package.mapper=com.demo.mapper\n" +
                        "\n" +
                        "project.path=${project.home}/${project.name}\n" +
                        "project.path.java=${project.path}/src/main/java\n" +
                        "project.path.resources=${project.path}/src/main/resources\n" +
                        "project.path.test=${project.path}/src/main/test\n" +
                        "############################ 必填参数 ############################");
                throw new IllegalArgumentException();
            }
        }
    }
}


