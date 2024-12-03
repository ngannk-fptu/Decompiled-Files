/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.config.providers;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlHelper {
    public static Map<String, String> getParams(Element paramsElement) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        if (paramsElement == null) {
            return params;
        }
        NodeList childNodes = paramsElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() != 1 || !"param".equals(childNode.getNodeName())) continue;
            Element paramElement = (Element)childNode;
            String paramName = paramElement.getAttribute("name");
            String val = XmlHelper.getContent(paramElement);
            if (val.length() <= 0) continue;
            params.put(paramName, val);
        }
        return params;
    }

    public static String getContent(Element element) {
        StringBuilder paramValue = new StringBuilder();
        NodeList childNodes = element.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); ++j) {
            String val;
            Node currentNode = childNodes.item(j);
            if (currentNode == null || currentNode.getNodeType() != 3 || (val = currentNode.getNodeValue()) == null) continue;
            paramValue.append(val.trim());
        }
        return paramValue.toString().trim();
    }

    public static Integer getLoadOrder(Document doc) {
        Element rootElement = doc.getDocumentElement();
        String number = rootElement.getAttribute("order");
        if (StringUtils.isNotBlank((CharSequence)number)) {
            try {
                return Integer.parseInt(number);
            }
            catch (NumberFormatException e) {
                return Integer.MAX_VALUE;
            }
        }
        return Integer.MAX_VALUE;
    }
}

