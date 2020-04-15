package com.lfx.code.mbg.plus.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-02-03 17:36
 */
public class MapperPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        removeElement(document);
        return true;
    }

    private void removeElement(Document document) {
        List<String> idList = Arrays.asList("BaseResultMap", "Base_Column_List");

        for (Iterator<Element> it = document.getRootElement().getElements().iterator(); it.hasNext(); ) {
            XmlElement element = (XmlElement) it.next();
            List<Attribute> attributes = element.getAttributes();

            for (Attribute attribute : attributes) {
                // 删除指定element
                if (attribute.getName().equals("id")) {
                    if (!idList.contains(attribute.getValue())) {
                        it.remove();
                        break;
                    }
                }
            }
        }
    }
}
