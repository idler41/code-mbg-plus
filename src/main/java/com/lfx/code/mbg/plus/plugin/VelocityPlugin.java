package com.lfx.code.mbg.plus.plugin;

import com.lfx.code.mbg.plus.constants.AppConstants;
import com.lfx.code.mbg.plus.context.GlobalContext;
import com.lfx.code.mbg.plus.plugin.context.OriginClassField;
import com.lfx.code.mbg.plus.plugin.context.OriginClassParam;
import com.lfx.code.mbg.plus.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
        originClassParam.setOriginClassFieldList(new ArrayList<>(introspectedTable.getAllColumns().size()));
        String logicTableName = introspectedTable.getTableConfiguration().getTableName();
        String suffixRegex = GlobalContext.map.get("table.remove.suffix.regex");
        if (StringUtils.isNotEmpty(suffixRegex)) {
            logicTableName = logicTableName.replaceFirst(suffixRegex, StringUtils.EMPTY);
        }
        originClassParam.setTableName(logicTableName);
        originClassParam.setTableRemark(StringUtil.replaceLast(introspectedTable.getRemarks(), "表", StringUtils.EMPTY));
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
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
            // 创建模板
            for (File templateFile : templateList) {
                String shortTemplateFileName = getShortFileName(templateFile);
                // 每个模板都用独立的对象，避免互相干扰
                OriginClassParam originClassParam = getClassParamFromCache(domainName);
                originClassParam = SerializationUtils.clone(originClassParam);
                doFilter(shortTemplateFileName, originClassParam);
                String enableKey = String.format(AppConstants.PLUGIN_TEMPLATE_ENABLE_KEY, shortTemplateFileName);
                if (Boolean.parseBoolean(GlobalContext.map.get(enableKey))) {
                    String targetName = resolveTargetFileName(shortTemplateFileName, domainName);
                    flushFile(templateFile, new File(targetName), GlobalContext.map, originClassParam);
                }
            }
        }
        return null;
    }

    private Set<String> filterSet;

    private void doFilter(String shortTemplateFileName, OriginClassParam originClassParam) {
        if (StringUtils.isBlank(GlobalContext.map.get("plugin.template.field.exclude.filter"))) {
            return;
        }

        if (filterSet == null) {
            filterSet = new HashSet<>(Arrays.asList(GlobalContext.map.get("plugin.template.field.exclude.filter").split(",")));
        }

        if (!filterSet.contains(shortTemplateFileName)) {
            return;
        }
        String key = String.format(AppConstants.PLUGIN_TEMPLATE_FIELD_EXCLUDE_KEY, shortTemplateFileName);
        if (StringUtils.isBlank(GlobalContext.map.get(key))) {
            return;
        }
        String[] excludeFields = GlobalContext.map.get(key).split(",");
        if (excludeFields.length == 0) {
            return;
        }
        if (CollectionUtils.isEmpty(originClassParam.getOriginClassFieldList())) {
            return;
        }
        Map<String, Long> typeCountMap = originClassParam.getOriginClassFieldList().stream().collect(Collectors.groupingBy(OriginClassField::getFieldType, Collectors.counting()));
        Map<String, String> fieldNameTypeMap = originClassParam.getOriginClassFieldList().stream().collect(Collectors.toMap(OriginClassField::getFieldName, OriginClassField::getFieldType));
        int size = originClassParam.getOriginClassFieldList().size();
        Map<String, Integer> indexMap = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            indexMap.put(originClassParam.getOriginClassFieldList().get(i).getFieldName(), i);
        }

        List<OriginClassField> delList = new ArrayList<>(excludeFields.length);
        for (String excludeField : excludeFields) {
            String fieldType = fieldNameTypeMap.get(excludeField);
            Long importTimes = typeCountMap.get(fieldType);
            if (importTimes == null) {
                // fix npe bug
                continue;
            }
            importTimes--;
            if (importTimes.equals(0L)) {
                originClassParam.getFieldImportList().remove(fieldType);
            }
            int index = indexMap.get(excludeField);
            delList.add(originClassParam.getOriginClassFieldList().get(index));
        }
        originClassParam.getOriginClassFieldList().removeAll(delList);
    }

    private String resolveTargetFileName(String shortTemplateFileName, String domainName) {
        String path = shortTemplateFileName.endsWith("Test") ? GlobalContext.map.get("project.path.test") : GlobalContext.map.get("project.path.java");
        String packageVal = GlobalContext.map.get(String.format(AppConstants.PLUGIN_TEMPLATE_PACKAGE_KEY, shortTemplateFileName));
        String velocityFileName = "model".equals(shortTemplateFileName) ? ".java" : (StringUtil.toUpperCaseOfFirst(shortTemplateFileName) + ".java");
        return path + File.separator + packageVal.replace(AppConstants.DOT, File.separator) +
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
