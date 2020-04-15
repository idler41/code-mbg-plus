package com.lfx.code.mbg.plus.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2020-04-12 21:12:54
 */
public class ExamplePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }
}
