package com.lfx.code.mbg.plus.plugin;

import com.lfx.code.mbg.plus.context.GlobalContext;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * @author <a href="mailto:linfuxin@wljs.com">linfuxin</a>
 * @date 2020-04-13 16:03:42
 */
public class DomainTablePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return Boolean.parseBoolean(GlobalContext.map.get("plugin.table-name.enable"));
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("com.baomidou.mybatisplus.annotations.TableName");
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        String suffixRegex = GlobalContext.map.get("remove.table.suffix.regex");
        if (StringUtils.isNotEmpty(suffixRegex)) {
            tableName = tableName.replaceFirst(suffixRegex, StringUtils.EMPTY);
        }
        topLevelClass.addAnnotation("@TableName(\"" + tableName + "\")");

        return true;
    }
}
