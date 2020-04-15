package com.lfx.code.mbg.plus.plugin;

import com.lfx.code.mbg.plus.context.GlobalContext;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * @author <a href="mailto:linfuxin@wljs.com">linfuxin</a>
 * @date 2020-04-13 16:03:42
 */
public class DomainTablePlugin extends PluginAdapter {
    private static final boolean TABLE_ENABLE;

    static {
        TABLE_ENABLE = Boolean.parseBoolean(GlobalContext.map.get("domain.table.enable"));
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (TABLE_ENABLE) {
            topLevelClass.addImportedType("com.baomidou.mybatisplus.annotations.TableName");
            String tableName = introspectedTable.getTableConfiguration().getTableName();
            topLevelClass.addAnnotation("@TableName(\"" + tableName + "\")");
        }
        return true;
    }
}
