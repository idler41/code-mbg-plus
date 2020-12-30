package com.lfx.code.mbg.plus.plugin;

import com.lfx.code.mbg.plus.context.PlusContext;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-02-03 17:36
 */
public class MapperPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return Boolean.parseBoolean(PlusContext.getProperty("plugin.mapper.enable"));
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        removeElement(document);
        return true;
    }

    private List<String> idList = null;

    private void removeElement(Document document) {
        if (idList == null) {
            String idListStr = PlusContext.getProperty("target.mapperXml.element.include");
            idList = StringUtils.isEmpty(idListStr) ? Collections.emptyList() : Arrays.asList(idListStr.split(","));
        }

        for (Iterator<Element> it = document.getRootElement().getElements().iterator(); it.hasNext(); ) {
            XmlElement element = (XmlElement) it.next();
            for (Attribute attribute : element.getAttributes()) {
                // 删除指定element
                if ("id".equals(attribute.getName())) {
                    if (!idList.contains(attribute.getValue())) {
                        it.remove();
                    }
                    break;
                }
            }
        }
    }
}
