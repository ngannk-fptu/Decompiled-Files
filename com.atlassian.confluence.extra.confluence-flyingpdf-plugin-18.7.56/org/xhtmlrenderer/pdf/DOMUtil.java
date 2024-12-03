/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtil {
    public static Element getChild(Element parent, String name) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Element elem;
            Node n = children.item(i);
            if (n.getNodeType() != 1 || !(elem = (Element)n).getTagName().equals(name)) continue;
            return elem;
        }
        return null;
    }

    public static List getChildren(Element parent, String name) {
        ArrayList<Element> result = new ArrayList<Element>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Element elem;
            Node n = children.item(i);
            if (n.getNodeType() != 1 || !(elem = (Element)n).getTagName().equals(name)) continue;
            result.add(elem);
        }
        return result.size() == 0 ? null : result;
    }

    public static String getText(Element parent) {
        StringBuilder sb = new StringBuilder();
        DOMUtil.getText(parent, sb);
        return sb.toString();
    }

    public static void getText(Element parent, StringBuilder sb) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node n = children.item(i);
            if (n.getNodeType() == 1) {
                DOMUtil.getText((Element)n, sb);
                continue;
            }
            if (n.getNodeType() != 3) continue;
            sb.append(n.getNodeValue());
        }
    }
}

