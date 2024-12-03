/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.xml;

import com.mchange.v1.util.DebugUtils;
import java.util.ArrayList;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class DomParseUtils {
    static final boolean DEBUG = true;

    public static String allTextFromUniqueChild(Element element, String string) throws DOMException {
        return DomParseUtils.allTextFromUniqueChild(element, string, false);
    }

    public static String allTextFromUniqueChild(Element element, String string, boolean bl) throws DOMException {
        Element element2 = DomParseUtils.uniqueChildByTagName(element, string);
        if (element2 == null) {
            return null;
        }
        return DomParseUtils.allTextFromElement(element2, bl);
    }

    public static Element uniqueChild(Element element, String string) throws DOMException {
        return DomParseUtils.uniqueChildByTagName(element, string);
    }

    public static Element uniqueChildByTagName(Element element, String string) throws DOMException {
        NodeList nodeList = element.getElementsByTagName(string);
        int n = nodeList.getLength();
        DebugUtils.myAssert(n <= 1, "There is more than one (" + n + ") child with tag name: " + string + "!!!");
        return n == 1 ? (Element)nodeList.item(0) : null;
    }

    public static String allText(Element element) throws DOMException {
        return DomParseUtils.allTextFromElement(element);
    }

    public static String allText(Element element, boolean bl) throws DOMException {
        return DomParseUtils.allTextFromElement(element, bl);
    }

    public static String allTextFromElement(Element element) throws DOMException {
        return DomParseUtils.allTextFromElement(element, false);
    }

    public static String allTextFromElement(Element element, boolean bl) throws DOMException {
        StringBuffer stringBuffer = new StringBuffer();
        NodeList nodeList = element.getChildNodes();
        int n = nodeList.getLength();
        for (int i = 0; i < n; ++i) {
            Node node = nodeList.item(i);
            if (!(node instanceof Text)) continue;
            stringBuffer.append(node.getNodeValue());
        }
        String string = stringBuffer.toString();
        return bl ? string.trim() : string;
    }

    public static String[] allTextFromImmediateChildElements(Element element, String string) throws DOMException {
        return DomParseUtils.allTextFromImmediateChildElements(element, string, false);
    }

    public static String[] allTextFromImmediateChildElements(Element element, String string, boolean bl) throws DOMException {
        NodeList nodeList = DomParseUtils.immediateChildElementsByTagName(element, string);
        int n = nodeList.getLength();
        String[] stringArray = new String[n];
        for (int i = 0; i < n; ++i) {
            stringArray[i] = DomParseUtils.allText((Element)nodeList.item(i), bl);
        }
        return stringArray;
    }

    public static NodeList immediateChildElementsByTagName(Element element, String string) throws DOMException {
        return DomParseUtils.getImmediateChildElementsByTagName(element, string);
    }

    public static NodeList getImmediateChildElementsByTagName(Element element, String string) throws DOMException {
        final ArrayList<Node> arrayList = new ArrayList<Node>();
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof Element) || !((Element)node).getTagName().equals(string)) continue;
            arrayList.add(node);
        }
        return new NodeList(){

            @Override
            public int getLength() {
                return arrayList.size();
            }

            @Override
            public Node item(int n) {
                return (Node)arrayList.get(n);
            }
        };
    }

    public static String allTextFromUniqueImmediateChild(Element element, String string) throws DOMException {
        Element element2 = DomParseUtils.uniqueImmediateChildByTagName(element, string);
        if (element2 == null) {
            return null;
        }
        return DomParseUtils.allTextFromElement(element2);
    }

    public static Element uniqueImmediateChild(Element element, String string) throws DOMException {
        return DomParseUtils.uniqueImmediateChildByTagName(element, string);
    }

    public static Element uniqueImmediateChildByTagName(Element element, String string) throws DOMException {
        NodeList nodeList = DomParseUtils.getImmediateChildElementsByTagName(element, string);
        int n = nodeList.getLength();
        DebugUtils.myAssert(n <= 1, "There is more than one (" + n + ") child with tag name: " + string + "!!!");
        return n == 1 ? (Element)nodeList.item(0) : null;
    }

    public static String attrValFromElement(Element element, String string) throws DOMException {
        Attr attr = element.getAttributeNode(string);
        return attr == null ? null : attr.getValue();
    }

    private DomParseUtils() {
    }
}

