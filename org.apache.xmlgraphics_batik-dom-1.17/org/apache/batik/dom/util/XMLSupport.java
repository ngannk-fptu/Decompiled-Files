/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.constants.XMLConstants
 */
package org.apache.batik.dom.util;

import org.apache.batik.constants.XMLConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class XMLSupport
implements XMLConstants {
    private XMLSupport() {
    }

    public static String getXMLLang(Element elt) {
        Attr attr = elt.getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "lang");
        if (attr != null) {
            return attr.getNodeValue();
        }
        for (Node n = elt.getParentNode(); n != null; n = n.getParentNode()) {
            if (n.getNodeType() != 1 || (attr = ((Element)n).getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "lang")) == null) continue;
            return attr.getNodeValue();
        }
        return "en";
    }

    public static String getXMLSpace(Element elt) {
        Attr attr = elt.getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "space");
        if (attr != null) {
            return attr.getNodeValue();
        }
        for (Node n = elt.getParentNode(); n != null; n = n.getParentNode()) {
            if (n.getNodeType() != 1 || (attr = ((Element)n).getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "space")) == null) continue;
            return attr.getNodeValue();
        }
        return "default";
    }

    public static String defaultXMLSpace(String data) {
        int nChars = data.length();
        StringBuffer result = new StringBuffer(nChars);
        boolean space = false;
        block4: for (int i = 0; i < nChars; ++i) {
            char c = data.charAt(i);
            switch (c) {
                case '\n': 
                case '\r': {
                    space = false;
                    continue block4;
                }
                case '\t': 
                case ' ': {
                    if (space) continue block4;
                    result.append(' ');
                    space = true;
                    continue block4;
                }
                default: {
                    result.append(c);
                    space = false;
                }
            }
        }
        return result.toString().trim();
    }

    public static String preserveXMLSpace(String data) {
        int nChars = data.length();
        StringBuffer result = new StringBuffer(nChars);
        block3: for (int i = 0; i < data.length(); ++i) {
            char c = data.charAt(i);
            switch (c) {
                case '\t': 
                case '\n': 
                case '\r': {
                    result.append(' ');
                    continue block3;
                }
                default: {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }
}

