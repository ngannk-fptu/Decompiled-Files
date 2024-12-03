/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 */
package com.atlassian.user.configuration.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Attribute;
import org.dom4j.Element;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class XMLConfigUtil {
    private XMLConfigUtil() {
    }

    public static HashMap<String, String> parseRepositoryElementForClassNames(Element repositoryElement) {
        HashMap<String, String> values = new HashMap<String, String>();
        Element classesElement = repositoryElement.element("classes");
        if (classesElement == null) {
            return values;
        }
        List subElements = classesElement.elements();
        for (Object subElement : subElements) {
            Element element = (Element)subElement;
            if (element.getName().equals("param")) {
                Attribute paramAttribute = element.attribute("name");
                values.put(paramAttribute.getText(), element.getText());
                continue;
            }
            values.put(element.getName(), element.getText());
        }
        Attribute attr = repositoryElement.attribute("class");
        if (attr != null) {
            values.put("class", attr.getText());
        }
        return values;
    }

    public static Map<String, String> parseRepositoryElementForStringData(Element repositoryElement) {
        HashMap<String, String> values = new HashMap<String, String>();
        for (Object o : repositoryElement.attributes()) {
            Attribute attr = (Attribute)o;
            if (attr.getName().equals("class")) continue;
            values.put(attr.getName(), attr.getText());
        }
        for (Object o : repositoryElement.elements()) {
            Element element = (Element)o;
            if (element.getName().equals("classes")) continue;
            values.put(element.getName(), element.getText());
        }
        return values;
    }
}

