package com.lfx.code.mbg.plus.plugin;

import com.lfx.code.mbg.plus.constants.AppConstants;
import com.lfx.code.mbg.plus.context.GlobalContext;
import com.lfx.code.mbg.plus.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2020-04-12 21:34:21
 */
@Slf4j
public class VelocityPlugin extends PluginAdapter {

    private static final VelocityEngine ve;

    static {
        Properties properties = new Properties();
        properties.setProperty(Velocity.OUTPUT_ENCODING, AppConstants.UTF_8);
        properties.setProperty(Velocity.INPUT_ENCODING, AppConstants.UTF_8);
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, "");
        ve = new VelocityEngine(properties);
        ve.init();
    }

    @Override
    public boolean validate(List<String> warnings) {
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

            // 生成模板前先生成所有模板的package,避免模板之间存在引用时没有package问题
            for (File velocityFile : templateList) {
                String shortFileName = getShortFileName(velocityFile);
                String packageKey = String.format(AppConstants.TEMPLATE_PACKAGE_KEY, shortFileName);
                String contextPackageKey = String.format(AppConstants.CONTEXT_PACKAGE_KEY, shortFileName);
                String packageVal = GlobalContext.map.get(contextPackageKey);
                GlobalContext.map.putIfAbsent(packageKey, packageVal);
            }

            // 创建模板
            for (File velocityFile : templateList) {
                String javaPath = GlobalContext.map.get("project.path.java");
                String shortFileName = getShortFileName(velocityFile);
                String contextPackageKey = String.format(AppConstants.CONTEXT_PACKAGE_KEY, shortFileName);
                String packageVal = GlobalContext.map.get(contextPackageKey);
                String targetName = javaPath + File.separator + packageVal.replace(".", File.separator) +
                        File.separator + domainName + velocityFile.getName();
                flushFile(velocityFile, new File(targetName), GlobalContext.map);
            }
        }
        return null;
    }

    private String getShortFileName(File velocityFile) {
        String fileName = StringUtil.getFileNameNoEx(velocityFile.getName());
        return StringUtil.toLowerCaseOfFirst(fileName);
    }

    private void flushFile(File templateFile, File targetFile, Map<String, String> context) {
        if (targetFile.exists() && Boolean.parseBoolean(GlobalContext.map.get("file.ignore"))) {
            return;
        }

        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        VelocityContext vContext = new VelocityContext(context);
        try {
            Template template = ve.getTemplate(templateFile.getCanonicalPath(), AppConstants.UTF_8);
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
