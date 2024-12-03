/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.xml;

import java.io.Reader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.lucene.queryparser.xml.ParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class DOMUtils {
    public static Element getChildByTagOrFail(Element e, String name) throws ParserException {
        Element kid = DOMUtils.getChildByTagName(e, name);
        if (null == kid) {
            throw new ParserException(e.getTagName() + " missing \"" + name + "\" child element");
        }
        return kid;
    }

    public static Element getFirstChildOrFail(Element e) throws ParserException {
        Element kid = DOMUtils.getFirstChildElement(e);
        if (null == kid) {
            throw new ParserException(e.getTagName() + " does not contain a child element");
        }
        return kid;
    }

    public static String getAttributeOrFail(Element e, String name) throws ParserException {
        String v = e.getAttribute(name);
        if (null == v) {
            throw new ParserException(e.getTagName() + " missing \"" + name + "\" attribute");
        }
        return v;
    }

    public static String getAttributeWithInheritanceOrFail(Element e, String name) throws ParserException {
        String v = DOMUtils.getAttributeWithInheritance(e, name);
        if (null == v) {
            throw new ParserException(e.getTagName() + " missing \"" + name + "\" attribute");
        }
        return v;
    }

    public static String getNonBlankTextOrFail(Element e) throws ParserException {
        String v = DOMUtils.getText(e);
        if (null != v) {
            v = v.trim();
        }
        if (null == v || 0 == v.length()) {
            throw new ParserException(e.getTagName() + " has no text");
        }
        return v;
    }

    public static Element getChildByTagName(Element e, String name) {
        for (Node kid = e.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() != 1 || !name.equals(kid.getNodeName())) continue;
            return (Element)kid;
        }
        return null;
    }

    public static String getAttributeWithInheritance(Element element, String attributeName) {
        String result = element.getAttribute(attributeName);
        if (result == null || "".equals(result)) {
            Node n = element.getParentNode();
            if (n == element || n == null) {
                return null;
            }
            if (n instanceof Element) {
                Element parent = (Element)n;
                return DOMUtils.getAttributeWithInheritance(parent, attributeName);
            }
            return null;
        }
        return result;
    }

    public static String getChildTextByTagName(Element e, String tagName) {
        Element child = DOMUtils.getChildByTagName(e, tagName);
        return child != null ? DOMUtils.getText(child) : null;
    }

    public static Element insertChild(Element parent, String tagName, String text) {
        Element child = parent.getOwnerDocument().createElement(tagName);
        parent.appendChild(child);
        if (text != null) {
            child.appendChild(child.getOwnerDocument().createTextNode(text));
        }
        return child;
    }

    public static String getAttribute(Element element, String attributeName, String deflt) {
        String result = element.getAttribute(attributeName);
        return result == null || "".equals(result) ? deflt : result;
    }

    public static float getAttribute(Element element, String attributeName, float deflt) {
        String result = element.getAttribute(attributeName);
        return result == null || "".equals(result) ? deflt : Float.parseFloat(result);
    }

    public static int getAttribute(Element element, String attributeName, int deflt) {
        String result = element.getAttribute(attributeName);
        return result == null || "".equals(result) ? deflt : Integer.parseInt(result);
    }

    public static boolean getAttribute(Element element, String attributeName, boolean deflt) {
        String result = element.getAttribute(attributeName);
        return result == null || "".equals(result) ? deflt : Boolean.valueOf(result);
    }

    public static String getText(Node e) {
        StringBuilder sb = new StringBuilder();
        DOMUtils.getTextBuffer(e, sb);
        return sb.toString();
    }

    public static Element getFirstChildElement(Element element) {
        for (Node kid = element.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() != 1) continue;
            return (Element)kid;
        }
        return null;
    }

    private static void getTextBuffer(Node e, StringBuilder sb) {
        block5: for (Node kid = e.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            switch (kid.getNodeType()) {
                case 3: {
                    sb.append(kid.getNodeValue());
                    continue block5;
                }
                case 1: {
                    DOMUtils.getTextBuffer(kid, sb);
                    continue block5;
                }
                case 5: {
                    DOMUtils.getTextBuffer(kid, sb);
                }
            }
        }
    }

    public static Document loadXML(Reader is) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        }
        catch (Exception se) {
            throw new RuntimeException("Parser configuration error", se);
        }
        Document doc = null;
        try {
            doc = db.parse(new InputSource(is));
        }
        catch (Exception se) {
            throw new RuntimeException("Error parsing file:" + se, se);
        }
        return doc;
    }
}

