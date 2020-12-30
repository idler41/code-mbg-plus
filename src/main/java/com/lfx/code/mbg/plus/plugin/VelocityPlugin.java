package com.lfx.code.mbg.plus.plugin;

import com.lfx.code.mbg.plus.context.PlusContext;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2020-04-12 21:34:21
 */
@Slf4j
public class VelocityPlugin extends PluginAdapter {

    private static final VelocityEngine VE;

    private static final String UTF_8 = "UTF-8";

    public static final String DOT = ".";

    private static final String EXCLUDE_TEMPLATE_KEY = "plugin.template.%s.field.exclude";

    public static final String PLUGIN_TEMPLATE_ENABLE_KEY = "plugin.template.%s.enable";

    public static final String PLUGIN_TEMPLATE_PACKAGE_KEY = "plugin.template.%s.package";

    static {
        Properties properties = new Properties();
        properties.setProperty(Velocity.OUTPUT_ENCODING, UTF_8);
        properties.setProperty(Velocity.INPUT_ENCODING, UTF_8);
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, "");
        VE = new VelocityEngine(properties);
        VE.init();
    }

    @Override
    public boolean validate(List<String> warnings) {
        return Boolean.parseBoolean(PlusContext.getProperty("plugin.template.enable"));
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String templateHome = PlusContext.getProperty("plugin.template.home");
        File folder = new File(templateHome);
        File[] templateList = !folder.exists() ? null : folder.listFiles();
        if (ArrayUtils.isEmpty(templateList)) {
            log.warn("未找到模板:template.home={}", templateHome);
            return null;
        }

        if (introspectedTable.getGeneratedJavaFiles().size() != 1) {
            log.error("模板引擎只能生成一个java文件!introspectedTable.getGeneratedJavaFiles().size()={}", introspectedTable.getGeneratedJavaFiles().size());
            return null;
        }

        GeneratedJavaFile javaFile = introspectedTable.getGeneratedJavaFiles().get(0);
        TopLevelClass topLevelClass = (TopLevelClass) javaFile.getCompilationUnit();
        for (File templateFile : templateList) {
            if (isEnableTemplate(templateFile)) {
                Map<String, Object> context = buildTemplateContext(introspectedTable, topLevelClass, templateFile);
                File targetFile = createTargetFile(topLevelClass, templateFile);
                flushFile(templateFile, targetFile, context);
            }
        }
        return null;
    }

    private boolean isEnableTemplate(File templateFile) {
        return Boolean.parseBoolean(PlusContext.getProperty(String.format(PLUGIN_TEMPLATE_ENABLE_KEY, getShortFileName(templateFile))));
    }

    private Map<String, Object> buildTemplateContext(IntrospectedTable introspectedTable, TopLevelClass topLevelClass, File templateFile) {
        OriginClassParam originClassParam = buildOriginClassParam(topLevelClass, introspectedTable, getExcludeFields(templateFile));
        Map<String, Object> context = new HashMap<>();
        context.put("config", PlusContext.CONFIGURATION);
        context.put("domainName", topLevelClass.getType().getShortName());
        context.put("domainFullName", topLevelClass.getType().getFullyQualifiedName());
        context.put("originClassParam", originClassParam);
        context.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        return context;
    }

    private File createTargetFile(TopLevelClass topLevelClass, File templateFile) {
        String shortTemplateFileName = getShortFileName(templateFile);
        String path = StringUtils.endsWithIgnoreCase(shortTemplateFileName, "Test") ? PlusContext.getProperty("target.project.path.test") : PlusContext.getProperty("target.project.path.java");
        String packagePath = PlusContext.getProperty(String.format(PLUGIN_TEMPLATE_PACKAGE_KEY, shortTemplateFileName))
                .replace(DOT, File.separator);
        String domainName = topLevelClass.getType().getShortName();
        String suffix = "model".equals(shortTemplateFileName) ? ".java" : (StringUtil.toUpperCaseOfFirst(shortTemplateFileName) + ".java");
        String targetName = path + File.separator + packagePath + File.separator + domainName + suffix;
        return new File(targetName);
    }

    private Set<String> getExcludeFields(File templateFile) {
        String shortTemplateFileName = getShortFileName(templateFile);
        String excludeKey = String.format(EXCLUDE_TEMPLATE_KEY, shortTemplateFileName);
        return StringUtils.isNotBlank(PlusContext.getProperty(excludeKey)) ?
                new HashSet<>(Arrays.asList(PlusContext.getProperty(excludeKey).split(","))) : Collections.emptySet();
    }

    private OriginClassParam buildOriginClassParam(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, Set<String> excludeFields) {
        Set<String> fieldImportSet = new HashSet<>();
        List<OriginClassField> originClassFieldList = new LinkedList<>();
        List<IntrospectedColumn> columnListToDel = new LinkedList<>();
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            if (column.getFullyQualifiedJavaType().isExplicitlyImported()) {
                fieldImportSet.add(column.getFullyQualifiedJavaType().getFullyQualifiedName());
            }
            if (excludeFields.contains(column.getJavaProperty())) {
                columnListToDel.add(column);
                continue;
            }
            OriginClassField originClassField = new OriginClassField();
            originClassField.setFieldName(column.getJavaProperty());
            originClassField.setFieldType(column.getFullyQualifiedJavaType().getShortName());
            originClassField.setFieldRemark(column.getRemarks());
            originClassFieldList.add(originClassField);
        }

        // 删除无用的引用
        if (!columnListToDel.isEmpty()) {
            List<String> allFieldType = originClassFieldList.stream().map(OriginClassField::getFieldType).collect(Collectors.toList());
            for (IntrospectedColumn introspectedColumn : columnListToDel) {
                if (!allFieldType.contains(introspectedColumn.getFullyQualifiedJavaType().getShortName())) {
                    fieldImportSet.remove(introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName());
                }
            }
        }

        return OriginClassParam.builder()
                .fieldImportList(fieldImportSet)
                .originClassFieldList(originClassFieldList)
                .javaDocLines(topLevelClass.getJavaDocLines())
                .tableRemark(StringUtil.replaceLast(introspectedTable.getRemarks(), "表", StringUtils.EMPTY))
                .build();
    }

    private String getShortFileName(File velocityFile) {
        String fileName = StringUtil.getFileNameNoEx(velocityFile.getName());
        return StringUtil.toLowerCaseOfFirst(fileName);
    }

    private void flushFile(File templateFile, File targetFile, Map<String, Object> context) {
        if (targetFile.exists() && PlusContext.isIgnoreFile()) {
            return;
        }

        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        VelocityContext vContext = new VelocityContext(context);
        try {
            Template template = VE.getTemplate(templateFile.getCanonicalPath(), UTF_8);
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
