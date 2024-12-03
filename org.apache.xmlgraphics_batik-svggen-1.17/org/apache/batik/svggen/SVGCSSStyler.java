/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.util.ArrayList;
import org.apache.batik.svggen.SVGStylingAttributes;
import org.apache.batik.svggen.SVGSyntax;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SVGCSSStyler
implements SVGSyntax {
    private static final char CSS_PROPERTY_VALUE_SEPARATOR = ':';
    private static final char CSS_RULE_SEPARATOR = ';';
    private static final char SPACE = ' ';

    public static void style(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Element element = (Element)node;
            StringBuffer styleAttrBuffer = new StringBuffer();
            int nAttr = attributes.getLength();
            ArrayList<String> toBeRemoved = new ArrayList<String>();
            for (int i = 0; i < nAttr; ++i) {
                Attr attr = (Attr)attributes.item(i);
                String string = attr.getName();
                if (!SVGStylingAttributes.set.contains(string)) continue;
                styleAttrBuffer.append(string);
                styleAttrBuffer.append(':');
                styleAttrBuffer.append(attr.getValue());
                styleAttrBuffer.append(';');
                styleAttrBuffer.append(' ');
                toBeRemoved.add(string);
            }
            if (styleAttrBuffer.length() > 0) {
                element.setAttributeNS(null, "style", styleAttrBuffer.toString().trim());
                int n = toBeRemoved.size();
                for (Object e : toBeRemoved) {
                    element.removeAttribute((String)e);
                }
            }
        }
        NodeList children = node.getChildNodes();
        int nChildren = children.getLength();
        for (int i = 0; i < nChildren; ++i) {
            Node child = children.item(i);
            SVGCSSStyler.style(child);
        }
    }
}

