/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Element
 */
package com.atlassian.plugin.loaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Element;

public class LoaderUtils {
    public static Map<String, String> getParams(Element element) {
        List elements = element.elements("param");
        HashMap<String, String> params = new HashMap<String, String>(elements.size());
        for (Element paramEl : elements) {
            String name = paramEl.attributeValue("name");
            String value = paramEl.attributeValue("value");
            if (value == null && paramEl.getTextTrim() != null && !"".equals(paramEl.getTextTrim())) {
                value = paramEl.getTextTrim();
            }
            params.put(name, value);
        }
        return params;
    }
}

