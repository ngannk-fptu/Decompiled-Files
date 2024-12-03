/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {
    public static boolean isText(Node node) {
        short ntype = node.getNodeType();
        return ntype == 3 || ntype == 4;
    }

    public static String getText(Element element) {
        StringBuilder content = new StringBuilder();
        if (element != null) {
            NodeList nodes = element.getChildNodes();
            for (int i = 0; i < nodes.getLength(); ++i) {
                Node child = nodes.item(i);
                if (!XMLUtil.isText(child)) continue;
                content.append(((CharacterData)child).getData());
            }
        }
        return content.length() == 0 ? null : content.toString();
    }

    public static String getText(Element element, String defaultValue) {
        String txt = XMLUtil.getText(element);
        return txt == null ? defaultValue : txt;
    }

    public static String getChildText(Element parent, String childLocalName, String childNamespaceURI) {
        Element child = XMLUtil.getChildElement(parent, childLocalName, childNamespaceURI);
        return child == null ? null : XMLUtil.getText(child);
    }

    public static Element getChildElement(Node parent, String childLocalName, String childNamespaceURI) {
        if (parent != null) {
            NodeList children = parent.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (child.getNodeType() != 1 || !childLocalName.equals(child.getLocalName()) || !childNamespaceURI.equals(child.getNamespaceURI())) continue;
                return (Element)child;
            }
        }
        return null;
    }

    public static String getAttribute(Element parent, String localName, String namespaceURI) {
        if (parent == null) {
            return null;
        }
        Attr attribute = namespaceURI == null ? parent.getAttributeNode(localName) : parent.getAttributeNodeNS(namespaceURI, localName);
        if (attribute != null) {
            return attribute.getValue();
        }
        return null;
    }
}

