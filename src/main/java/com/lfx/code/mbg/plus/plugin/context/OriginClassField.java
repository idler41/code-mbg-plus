package com.lfx.code.mbg.plus.plugin.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="mailto:linfx@dydf.cn">linfuxin</a>
 * @date 2020-05-13 12:10:25
 */
@Data
public class OriginClassField implements Serializable {
    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字段备注
     */
    private String fieldRemark;

    /**
     * 字段名称
     */
    private String fieldName;
}
