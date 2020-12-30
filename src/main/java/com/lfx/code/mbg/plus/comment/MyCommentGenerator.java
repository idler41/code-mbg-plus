package com.lfx.code.mbg.plus.comment;

import com.lfx.code.mbg.plus.context.PlusContext;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 自定义注释转换：domain文件head注释 + field中文注释
 *
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-02-03 17:36
 */
public class MyCommentGenerator extends DefaultCommentGenerator {

    /**
     * xml 中的注释. 提示大家这段代码是自动生成的
     *
     * @param xmlElement the xml element
     */
    @Override
    public void addComment(XmlElement xmlElement) {
        // xml 中不生成注释
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass,
                                     IntrospectedTable introspectedTable) {

        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * @author " + PlusContext.getProperty("comment.author"));
        topLevelClass.addJavaDocLine(" * @date " + PlusContext.getProperty("generateTime"));
        topLevelClass.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable table, IntrospectedColumn column) {
        // 属性的注释只包含数据库的列备注
        String remarks = column.getRemarks();
        if (StringUtility.stringHasValue(remarks)) {
            field.addJavaDocLine("/**");
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));  
            for (String remarkLine : remarkLines) {
                field.addJavaDocLine(" * " + remarkLine);
            }
            field.addJavaDocLine(" */");
        }
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        // field 不生成注释
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable table, IntrospectedColumn column) {
        // get 方法不生成注释
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable table, IntrospectedColumn column) {
        // set 方法不生成注释
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        // mapper方法不生成注释
    }


}
