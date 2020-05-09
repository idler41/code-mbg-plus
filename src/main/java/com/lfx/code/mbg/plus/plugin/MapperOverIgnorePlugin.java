package com.lfx.code.mbg.plus.plugin;

import com.lfx.code.mbg.plus.context.GlobalContext;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-02-03 17:36
 */
public class MapperOverIgnorePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return Boolean.parseBoolean(GlobalContext.map.get("plugin.mapper-ignore.enable"));
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        try {
            Field field = sqlMap.getClass().getDeclaredField("isMergeable");
            field.setAccessible(true);
            field.setBoolean(sqlMap, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
