package com.lfx.code.mbg.plus.type;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

/**
 * Java类型与数据库类型自定义转换
 *
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-02-03 17:36
 */
public class JavaTypeResolverImpl extends JavaTypeResolverDefaultImpl {

    private static final FullyQualifiedJavaType INT_TYPE = new FullyQualifiedJavaType("java.lang.Integer");

    /**
     * 覆盖默认的映射类型
     */
    @Override
    public FullyQualifiedJavaType calculateJavaType(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType fullyQualifiedJavaType = super.calculateJavaType(introspectedColumn);
        switch (introspectedColumn.getJdbcType()) {
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.BIT:
                return INT_TYPE;
            default:
                return fullyQualifiedJavaType;
        }
    }
}