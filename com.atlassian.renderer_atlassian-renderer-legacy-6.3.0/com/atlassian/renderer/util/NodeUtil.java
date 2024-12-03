/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NodeUtil {
    public static String getAttribute(Node node, String name) {
        NamedNodeMap map = node.getAttributes();
        if (map == null) {
            return null;
        }
        Node n = node.getAttributes().getNamedItem(name);
        return n != null ? n.getNodeValue() : null;
    }

    public static boolean getBooleanAttributeValue(Node node, String attributeName, boolean defaultValue) {
        String attributeValue = NodeUtil.getAttribute(node, attributeName);
        return String.valueOf(!defaultValue).equalsIgnoreCase(attributeValue) ? !defaultValue : defaultValue;
    }

    public static boolean attributeContains(Node node, String name, String value) {
        String attr = NodeUtil.getAttribute(node, name);
        return attr != null && attr.indexOf(value) != -1;
    }

    public static boolean isTextNode(Node node) {
        return node.getNodeType() == 3;
    }

    public static boolean isList(Node node) {
        return node.getNodeName().toLowerCase().equals("ol") || node.getNodeName().toLowerCase().equals("ul");
    }
}

