package com.lfx.code.mbg.plus.plugin.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:linfx@dydf.cn">linfuxin</a>
 * @date 2020-05-13 11:53:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OriginClassParam implements Serializable {

    /**
     * 对应的表名称
     */
    private String tableName;

    /**
     * 对应的表备注
     */
    private String tableRemark;

    /**
     * 文件备注
     */
    private List<String> javaDocLines;

    /**
     * 字段import
     */
    private Set<String> fieldImportList;

    /**
     * 字段集合
     */
    private List<OriginClassField> originClassFieldList;
}
