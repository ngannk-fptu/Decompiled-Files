/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public final class XmlUtil
implements Serializable {
    public static Node getOneTaggedNode(Node el, String name) throws SAXException {
        if (!el.hasChildNodes()) {
            return null;
        }
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node n = children.item(i);
            if (!name.equals(n.getNodeName())) continue;
            return n;
        }
        return null;
    }

    public static String getOneNodeVal(Node el, String name) throws SAXException {
        if (!el.hasChildNodes()) {
            return null;
        }
        NodeList children = el.getChildNodes();
        if (children.getLength() > 1) {
            throw new SAXException("Multiple property values: " + name);
        }
        Node child = children.item(0);
        return child.getNodeValue();
    }

    public static String getOneNodeVal(Node el) throws SAXException {
        return XmlUtil.getOneNodeVal(el, el.getNodeName());
    }

    public static String getReqOneNodeVal(Node el, String name) throws SAXException {
        String str = XmlUtil.getOneNodeVal(el, name);
        if (str == null || str.length() == 0) {
            throw new SAXException("Missing property value: " + name);
        }
        return str;
    }

    public static String getReqOneNodeVal(Node el) throws SAXException {
        return XmlUtil.getReqOneNodeVal(el, el.getNodeName());
    }

    public static String getAttrVal(Element el, String name) throws SAXException {
        Attr at = el.getAttributeNode(name);
        if (at == null) {
            return null;
        }
        return at.getValue();
    }

    public static String getReqAttrVal(Element el, String name) throws SAXException {
        String str = XmlUtil.getAttrVal(el, name);
        if (str == null || str.length() == 0) {
            throw new SAXException("Missing attribute value: " + name);
        }
        return str;
    }

    public static String getAttrVal(NamedNodeMap nnm, String name) {
        Node nmAttr = nnm.getNamedItem(name);
        if (nmAttr == null || XmlUtil.absent(nmAttr.getNodeValue())) {
            return null;
        }
        return nmAttr.getNodeValue();
    }

    public static Boolean getYesNoAttrVal(NamedNodeMap nnm, String name) throws SAXException {
        String val = XmlUtil.getAttrVal(nnm, name);
        if (val == null) {
            return null;
        }
        if (!"yes".equals(val) && !"no".equals(val)) {
            throw new SAXException("Invalid attribute value: " + val);
        }
        return new Boolean("yes".equals(val));
    }

    public static int numAttrs(Node nd) {
        NamedNodeMap nnm = nd.getAttributes();
        if (nnm == null) {
            return 0;
        }
        return nnm.getLength();
    }

    public static List<Element> getElements(Node nd) throws SAXException {
        ArrayList<Element> al = new ArrayList<Element>();
        NodeList children = nd.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node curnode = children.item(i);
            if (curnode.getNodeType() == 3) {
                String val = curnode.getNodeValue();
                if (val == null) continue;
                for (int vi = 0; vi < val.length(); ++vi) {
                    if (Character.isWhitespace(val.charAt(vi))) continue;
                    throw new SAXException("Non-whitespace text in element body for " + nd.getLocalName() + "\n text=" + val);
                }
                continue;
            }
            if (curnode.getNodeType() == 8) continue;
            if (curnode.getNodeType() == 1) {
                al.add((Element)curnode);
                continue;
            }
            throw new SAXException("Unexpected child node " + curnode.getLocalName() + " for " + nd.getLocalName());
        }
        return al;
    }

    public static String getElementContent(Element el, boolean trim) throws SAXException {
        StringBuilder sb = new StringBuilder();
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node curnode = children.item(i);
            if (curnode.getNodeType() == 3) {
                sb.append(curnode.getNodeValue());
                continue;
            }
            if (curnode.getNodeType() == 4) {
                sb.append(curnode.getNodeValue());
                continue;
            }
            if (curnode.getNodeType() == 8) continue;
            throw new SAXException("Unexpected child node " + curnode.getLocalName() + " for " + el.getLocalName());
        }
        if (!trim) {
            return sb.toString();
        }
        return sb.toString().trim();
    }

    public static void setElementContent(Node n, String s) throws SAXException {
        NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node curnode = children.item(i);
            n.removeChild(curnode);
        }
        Document d = n.getOwnerDocument();
        Text textNode = d.createTextNode(s);
        n.appendChild(textNode);
    }

    public static String getElementContent(Element el) throws SAXException {
        return XmlUtil.getElementContent(el, true);
    }

    public static boolean hasContent(Element el) throws SAXException {
        String s = XmlUtil.getElementContent(el);
        return s != null && s.length() > 0;
    }

    public static boolean hasChildren(Element el) throws SAXException {
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node curnode = children.item(i);
            short ntype = curnode.getNodeType();
            if (ntype == 3 || ntype == 4 || ntype == 8) continue;
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Element el) throws SAXException {
        return !XmlUtil.hasChildren(el) && !XmlUtil.hasContent(el);
    }

    public static Element[] getElementsArray(Node nd) throws SAXException {
        List<Element> al = XmlUtil.getElements(nd);
        return al.toArray(new Element[al.size()]);
    }

    public static boolean nodeMatches(Node nd, QName tag) {
        if (tag == null) {
            return false;
        }
        String ns = nd.getNamespaceURI();
        if (ns == null ? tag.getNamespaceURI() != null && !"".equals(tag.getNamespaceURI()) : !ns.equals(tag.getNamespaceURI())) {
            return false;
        }
        String ln = nd.getLocalName();
        return !(ln == null ? tag.getLocalPart() != null : !ln.equals(tag.getLocalPart()));
    }

    public static QName fromNode(Node nd) {
        String ns = nd.getNamespaceURI();
        if (ns == null) {
            ns = "";
        }
        return new QName(ns, nd.getLocalName());
    }

    public static Element getOnlyElement(Node nd) throws SAXException {
        Element[] els = XmlUtil.getElementsArray(nd);
        if (els.length != 1) {
            throw new SAXException("Expected exactly one child node for " + nd.getLocalName());
        }
        return els[0];
    }

    private static boolean absent(String val) {
        return val == null || val.length() == 0;
    }
}

