/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.internal.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XmlHelper {
    private XmlHelper() {
    }

    public static Iterator getChildrenByTagName(Element element, String tagName) {
        if (element == null) {
            return null;
        }
        NodeList children = element.getChildNodes();
        ArrayList<Node> goodChildren = new ArrayList<Node>();
        for (int i = 0; i < children.getLength(); ++i) {
            Node currentChild = children.item(i);
            if (currentChild.getNodeType() != 1 || !((Element)currentChild).getTagName().equals(tagName)) continue;
            goodChildren.add(currentChild);
        }
        return goodChildren.iterator();
    }

    public static Element getUniqueChild(Element element, String tagName) throws Exception {
        Iterator goodChildren = XmlHelper.getChildrenByTagName(element, tagName);
        if (goodChildren != null && goodChildren.hasNext()) {
            Element child = (Element)goodChildren.next();
            if (goodChildren.hasNext()) {
                throw new Exception("expected only one " + tagName + " tag");
            }
            return child;
        }
        throw new Exception("expected one " + tagName + " tag");
    }

    public static Element getOptionalChild(Element element, String tagName) throws Exception {
        return XmlHelper.getOptionalChild(element, tagName, null);
    }

    public static Element getOptionalChild(Element element, String tagName, Element defaultElement) throws Exception {
        Iterator goodChildren = XmlHelper.getChildrenByTagName(element, tagName);
        if (goodChildren != null && goodChildren.hasNext()) {
            Element child = (Element)goodChildren.next();
            if (goodChildren.hasNext()) {
                throw new Exception("expected only one " + tagName + " tag");
            }
            return child;
        }
        return defaultElement;
    }

    public static String getElementContent(Element element) throws Exception {
        return XmlHelper.getElementContent(element, null);
    }

    public static String getElementContent(Element element, String defaultStr) throws Exception {
        if (element == null) {
            return defaultStr;
        }
        NodeList children = element.getChildNodes();
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < children.getLength(); ++i) {
            if (children.item(i).getNodeType() != 3 && children.item(i).getNodeType() != 4) continue;
            result.append(children.item(i).getNodeValue());
        }
        return result.toString().trim();
    }

    public static String getUniqueChildContent(Element element, String tagName) throws Exception {
        return XmlHelper.getElementContent(XmlHelper.getUniqueChild(element, tagName));
    }

    public static String getOptionalChildContent(Element element, String tagName) throws Exception {
        return XmlHelper.getElementContent(XmlHelper.getOptionalChild(element, tagName));
    }

    public static boolean getOptionalChildBooleanContent(Element element, String name) throws Exception {
        Element child = XmlHelper.getOptionalChild(element, name);
        if (child != null) {
            String value = XmlHelper.getElementContent(child).toLowerCase(Locale.ROOT);
            return value.equals("true") || value.equals("yes");
        }
        return false;
    }
}

