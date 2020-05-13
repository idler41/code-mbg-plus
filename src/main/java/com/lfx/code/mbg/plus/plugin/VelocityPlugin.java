package com.lfx.code.mbg.plus.plugin;

import com.lfx.code.mbg.plus.constants.AppConstants;
import com.lfx.code.mbg.plus.context.GlobalContext;
import com.lfx.code.mbg.plus.plugin.context.OriginClassField;
import com.lfx.code.mbg.plus.plugin.context.OriginClassParam;
import com.lfx.code.mbg.plus.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2020-04-12 21:34:21
 */
@Slf4j
public class VelocityPlugin extends PluginAdapter {

    private static final VelocityEngine VE;

    static {
        Properties properties = new Properties();
        properties.setProperty(Velocity.OUTPUT_ENCODING, AppConstants.UTF_8);
        properties.setProperty(Velocity.INPUT_ENCODING, AppConstants.UTF_8);
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, "");
        VE = new VelocityEngine(properties);
        VE.init();
    }

    private Map<String, OriginClassParam> classCache = new HashMap<>();

    @Override
    public boolean validate(List<String> warnings) {
        return Boolean.parseBoolean(GlobalContext.map.get("plugin.template.enable"));
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String domainName = topLevelClass.getType().getShortName();

        OriginClassParam originClassParam = new OriginClassParam();
        originClassParam.setJavaDocLines(topLevelClass.getJavaDocLines());
        originClassParam.setFieldImportList(new HashSet<>());
        originClassParam.setOriginClassFieldList(new ArrayList<>(introspectedTable.getBaseColumns().size()));
        for (IntrospectedColumn column : introspectedTable.getBaseColumns()) {
            if (column.getFullyQualifiedJavaType().isExplicitlyImported()) {
                originClassParam.getFieldImportList().add(column.getFullyQualifiedJavaType().getFullyQualifiedName());
            }
            OriginClassField originClassField = new OriginClassField();
            originClassField.setFieldName(column.getJavaProperty());
            originClassField.setFieldType(column.getFullyQualifiedJavaType().getShortName());
            originClassField.setFieldRemark(column.getRemarks());
            originClassParam.getOriginClassFieldList().add(originClassField);
        }
        // 不直接缓存TopLevelClass，因为引用的对象有可能会被其他插件修改
        putClassParamCache(domainName, originClassParam);
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String velocityFolder = GlobalContext.map.get("template.home");
        File folder = new File(velocityFolder);
        File[] templateList = folder.listFiles();
        if (ArrayUtils.isEmpty(templateList)) {
            log.warn("未找到模板:template.home={}", velocityFolder);
            return null;
        }

        for (GeneratedJavaFile javaFile : introspectedTable.getGeneratedJavaFiles()) {
            TopLevelClass topLevelClass = (TopLevelClass) javaFile.getCompilationUnit();

            String domainName = topLevelClass.getType().getShortName();
            String domainFullName = topLevelClass.getType().getFullyQualifiedName();
            GlobalContext.map.put("domainName", domainName);
            GlobalContext.map.put("domainFullName", domainFullName);
            OriginClassParam originClassParam = getClassParamFromCache(domainName);
            originClassParam.setTableRemark(StringUtil.replaceLast(introspectedTable.getRemarks(), "表", StringUtils.EMPTY));
            // 创建模板
            for (File templateFile : templateList) {
                String shortTemplateFileName = getShortFileName(templateFile);
                String enableKey = String.format(AppConstants.PLUGIN_TEMPLATE_ENABLE_KEY, shortTemplateFileName);
                if (Boolean.parseBoolean(GlobalContext.map.get(enableKey))) {
                    String targetName = resolveTargetFileName(shortTemplateFileName, domainName);
                    flushFile(templateFile, new File(targetName), GlobalContext.map, originClassParam);
                }
            }
        }
        return null;
    }

    private String resolveTargetFileName(String shortTemplateFileName, String domainName) {
        String javaPath = GlobalContext.map.get("project.path.java");
        String packageVal = GlobalContext.map.get(String.format(AppConstants.PLUGIN_TEMPLATE_PACKAGE_KEY, shortTemplateFileName));
        String velocityFileName = "model".equals(shortTemplateFileName) ? ".java" : (StringUtil.toUpperCaseOfFirst(shortTemplateFileName) + ".java");
        return javaPath + File.separator + packageVal.replace(AppConstants.DOT, File.separator) +
                File.separator + domainName + velocityFileName;
    }

    private void putClassParamCache(String domainName, OriginClassParam originClassParam) {
        classCache.put("topLevelClass-" + domainName, originClassParam);
    }

    private OriginClassParam getClassParamFromCache(String domainName) {
        return classCache.get("topLevelClass-" + domainName);
    }

    private String getShortFileName(File velocityFile) {
        String fileName = StringUtil.getFileNameNoEx(velocityFile.getName());
        return StringUtil.toLowerCaseOfFirst(fileName);
    }

    private void flushFile(File templateFile, File targetFile, Map<String, String> context, OriginClassParam originClassParam) {
        if (targetFile.exists() && Boolean.parseBoolean(GlobalContext.map.get("file.ignore"))) {
            return;
        }

        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        Map<String, Object> globalContext = new HashMap<>(context);
        globalContext.put("context", context);
        globalContext.put("originClassParam", originClassParam);
        VelocityContext vContext = new VelocityContext(globalContext);
        try {
            Template template = VE.getTemplate(templateFile.getCanonicalPath(), AppConstants.UTF_8);
            //noinspection ResultOfMethodCallIgnored
            targetFile.createNewFile();
            FileOutputStream outStream = new FileOutputStream(targetFile);
            OutputStreamWriter writer = new OutputStreamWriter(outStream, StandardCharsets.UTF_8);
            template.merge(vContext, writer);
            new BufferedWriter(writer).flush();
            log.info("生成模板文件：" + targetFile.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
