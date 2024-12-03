/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.lang.reflect.Method;
import java.util.Hashtable;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.impl.xs.opti.ElementImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSException;

public class DOMUtil {
    protected DOMUtil() {
    }

    public static void copyInto(Node node, Node node2) throws DOMException {
        Document document = node2.getOwnerDocument();
        boolean bl = document instanceof DocumentImpl;
        Node node3 = node;
        Node node4 = node;
        Node node5 = node;
        while (node5 != null) {
            Node node6 = null;
            short s = node5.getNodeType();
            switch (s) {
                case 4: {
                    node6 = document.createCDATASection(node5.getNodeValue());
                    break;
                }
                case 8: {
                    node6 = document.createComment(node5.getNodeValue());
                    break;
                }
                case 1: {
                    Element element = document.createElement(node5.getNodeName());
                    node6 = element;
                    NamedNodeMap namedNodeMap = node5.getAttributes();
                    int n = namedNodeMap.getLength();
                    for (int i = 0; i < n; ++i) {
                        Attr attr = (Attr)namedNodeMap.item(i);
                        String string = attr.getNodeName();
                        String string2 = attr.getNodeValue();
                        element.setAttribute(string, string2);
                        if (!bl || attr.getSpecified()) continue;
                        ((AttrImpl)element.getAttributeNode(string)).setSpecified(false);
                    }
                    break;
                }
                case 5: {
                    node6 = document.createEntityReference(node5.getNodeName());
                    break;
                }
                case 7: {
                    node6 = document.createProcessingInstruction(node5.getNodeName(), node5.getNodeValue());
                    break;
                }
                case 3: {
                    node6 = document.createTextNode(node5.getNodeValue());
                    break;
                }
                default: {
                    throw new IllegalArgumentException("can't copy node type, " + s + " (" + node5.getNodeName() + ')');
                }
            }
            node2.appendChild(node6);
            if (node5.hasChildNodes()) {
                node4 = node5;
                node5 = node5.getFirstChild();
                node2 = node6;
                continue;
            }
            node5 = node5.getNextSibling();
            while (node5 == null && node4 != node3) {
                node5 = node4.getNextSibling();
                node4 = node4.getParentNode();
                node2 = node2.getParentNode();
            }
        }
    }

    public static Element getFirstChildElement(Node node) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getFirstVisibleChildElement(Node node) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1 || DOMUtil.isHidden(node2)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getFirstVisibleChildElement(Node node, Hashtable hashtable) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1 || DOMUtil.isHidden(node2, hashtable)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getLastChildElement(Node node) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() != 1) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getLastVisibleChildElement(Node node) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() != 1 || DOMUtil.isHidden(node2)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getLastVisibleChildElement(Node node, Hashtable hashtable) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() != 1 || DOMUtil.isHidden(node2, hashtable)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getNextSiblingElement(Node node) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getNextVisibleSiblingElement(Node node) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1 || DOMUtil.isHidden(node2)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getNextVisibleSiblingElement(Node node, Hashtable hashtable) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1 || DOMUtil.isHidden(node2, hashtable)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static void setHidden(Node node) {
        if (node instanceof org.apache.xerces.impl.xs.opti.NodeImpl) {
            ((org.apache.xerces.impl.xs.opti.NodeImpl)node).setReadOnly(true, false);
        } else if (node instanceof NodeImpl) {
            ((NodeImpl)node).setReadOnly(true, false);
        }
    }

    public static void setHidden(Node node, Hashtable hashtable) {
        if (node instanceof org.apache.xerces.impl.xs.opti.NodeImpl) {
            ((org.apache.xerces.impl.xs.opti.NodeImpl)node).setReadOnly(true, false);
        } else {
            hashtable.put(node, "");
        }
    }

    public static void setVisible(Node node) {
        if (node instanceof org.apache.xerces.impl.xs.opti.NodeImpl) {
            ((org.apache.xerces.impl.xs.opti.NodeImpl)node).setReadOnly(false, false);
        } else if (node instanceof NodeImpl) {
            ((NodeImpl)node).setReadOnly(false, false);
        }
    }

    public static void setVisible(Node node, Hashtable hashtable) {
        if (node instanceof org.apache.xerces.impl.xs.opti.NodeImpl) {
            ((org.apache.xerces.impl.xs.opti.NodeImpl)node).setReadOnly(false, false);
        } else {
            hashtable.remove(node);
        }
    }

    public static boolean isHidden(Node node) {
        if (node instanceof org.apache.xerces.impl.xs.opti.NodeImpl) {
            return ((org.apache.xerces.impl.xs.opti.NodeImpl)node).getReadOnly();
        }
        if (node instanceof NodeImpl) {
            return ((NodeImpl)node).getReadOnly();
        }
        return false;
    }

    public static boolean isHidden(Node node, Hashtable hashtable) {
        if (node instanceof org.apache.xerces.impl.xs.opti.NodeImpl) {
            return ((org.apache.xerces.impl.xs.opti.NodeImpl)node).getReadOnly();
        }
        return hashtable.containsKey(node);
    }

    public static Element getFirstChildElement(Node node, String string) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1 || !node2.getNodeName().equals(string)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getLastChildElement(Node node, String string) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() != 1 || !node2.getNodeName().equals(string)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getNextSiblingElement(Node node, String string) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1 || !node2.getNodeName().equals(string)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getFirstChildElementNS(Node node, String string, String string2) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            String string3;
            if (node2.getNodeType() != 1 || (string3 = node2.getNamespaceURI()) == null || !string3.equals(string) || !node2.getLocalName().equals(string2)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getLastChildElementNS(Node node, String string, String string2) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            String string3;
            if (node2.getNodeType() != 1 || (string3 = node2.getNamespaceURI()) == null || !string3.equals(string) || !node2.getLocalName().equals(string2)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getNextSiblingElementNS(Node node, String string, String string2) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            String string3;
            if (node2.getNodeType() != 1 || (string3 = node2.getNamespaceURI()) == null || !string3.equals(string) || !node2.getLocalName().equals(string2)) continue;
            return (Element)node2;
        }
        return null;
    }

    public static Element getFirstChildElement(Node node, String[] stringArray) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1) continue;
            for (int i = 0; i < stringArray.length; ++i) {
                if (!node2.getNodeName().equals(stringArray[i])) continue;
                return (Element)node2;
            }
        }
        return null;
    }

    public static Element getLastChildElement(Node node, String[] stringArray) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() != 1) continue;
            for (int i = 0; i < stringArray.length; ++i) {
                if (!node2.getNodeName().equals(stringArray[i])) continue;
                return (Element)node2;
            }
        }
        return null;
    }

    public static Element getNextSiblingElement(Node node, String[] stringArray) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1) continue;
            for (int i = 0; i < stringArray.length; ++i) {
                if (!node2.getNodeName().equals(stringArray[i])) continue;
                return (Element)node2;
            }
        }
        return null;
    }

    public static Element getFirstChildElementNS(Node node, String[][] stringArray) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1) continue;
            for (int i = 0; i < stringArray.length; ++i) {
                String string = node2.getNamespaceURI();
                if (string == null || !string.equals(stringArray[i][0]) || !node2.getLocalName().equals(stringArray[i][1])) continue;
                return (Element)node2;
            }
        }
        return null;
    }

    public static Element getLastChildElementNS(Node node, String[][] stringArray) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() != 1) continue;
            for (int i = 0; i < stringArray.length; ++i) {
                String string = node2.getNamespaceURI();
                if (string == null || !string.equals(stringArray[i][0]) || !node2.getLocalName().equals(stringArray[i][1])) continue;
                return (Element)node2;
            }
        }
        return null;
    }

    public static Element getNextSiblingElementNS(Node node, String[][] stringArray) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() != 1) continue;
            for (int i = 0; i < stringArray.length; ++i) {
                String string = node2.getNamespaceURI();
                if (string == null || !string.equals(stringArray[i][0]) || !node2.getLocalName().equals(stringArray[i][1])) continue;
                return (Element)node2;
            }
        }
        return null;
    }

    public static Element getFirstChildElement(Node node, String string, String string2, String string3) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            Element element;
            if (node2.getNodeType() != 1 || !(element = (Element)node2).getNodeName().equals(string) || !element.getAttribute(string2).equals(string3)) continue;
            return element;
        }
        return null;
    }

    public static Element getLastChildElement(Node node, String string, String string2, String string3) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            Element element;
            if (node2.getNodeType() != 1 || !(element = (Element)node2).getNodeName().equals(string) || !element.getAttribute(string2).equals(string3)) continue;
            return element;
        }
        return null;
    }

    public static Element getNextSiblingElement(Node node, String string, String string2, String string3) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            Element element;
            if (node2.getNodeType() != 1 || !(element = (Element)node2).getNodeName().equals(string) || !element.getAttribute(string2).equals(string3)) continue;
            return element;
        }
        return null;
    }

    public static String getChildText(Node node) {
        if (node == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            short s = node2.getNodeType();
            if (s == 3) {
                stringBuffer.append(node2.getNodeValue());
                continue;
            }
            if (s != 4) continue;
            stringBuffer.append(DOMUtil.getChildText(node2));
        }
        return stringBuffer.toString();
    }

    public static String getName(Node node) {
        return node.getNodeName();
    }

    public static String getLocalName(Node node) {
        String string = node.getLocalName();
        return string != null ? string : node.getNodeName();
    }

    public static Element getParent(Element element) {
        Node node = element.getParentNode();
        if (node instanceof Element) {
            return (Element)node;
        }
        return null;
    }

    public static Document getDocument(Node node) {
        return node.getOwnerDocument();
    }

    public static Element getRoot(Document document) {
        return document.getDocumentElement();
    }

    public static Attr getAttr(Element element, String string) {
        return element.getAttributeNode(string);
    }

    public static Attr getAttrNS(Element element, String string, String string2) {
        return element.getAttributeNodeNS(string, string2);
    }

    public static Attr[] getAttrs(Element element) {
        NamedNodeMap namedNodeMap = element.getAttributes();
        Attr[] attrArray = new Attr[namedNodeMap.getLength()];
        for (int i = 0; i < namedNodeMap.getLength(); ++i) {
            attrArray[i] = (Attr)namedNodeMap.item(i);
        }
        return attrArray;
    }

    public static String getValue(Attr attr) {
        return attr.getValue();
    }

    public static String getAttrValue(Element element, String string) {
        return element.getAttribute(string);
    }

    public static String getAttrValueNS(Element element, String string, String string2) {
        return element.getAttributeNS(string, string2);
    }

    public static String getPrefix(Node node) {
        return node.getPrefix();
    }

    public static String getNamespaceURI(Node node) {
        return node.getNamespaceURI();
    }

    public static String getAnnotation(Node node) {
        if (node instanceof ElementImpl) {
            return ((ElementImpl)node).getAnnotation();
        }
        return null;
    }

    public static String getSyntheticAnnotation(Node node) {
        if (node instanceof ElementImpl) {
            return ((ElementImpl)node).getSyntheticAnnotation();
        }
        return null;
    }

    public static DOMException createDOMException(short s, Throwable throwable) {
        DOMException dOMException = new DOMException(s, throwable != null ? throwable.getMessage() : null);
        if (throwable != null && ThrowableMethods.fgThrowableMethodsAvailable) {
            try {
                ThrowableMethods.fgThrowableInitCauseMethod.invoke((Object)dOMException, throwable);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return dOMException;
    }

    public static LSException createLSException(short s, Throwable throwable) {
        LSException lSException = new LSException(s, throwable != null ? throwable.getMessage() : null);
        if (throwable != null && ThrowableMethods.fgThrowableMethodsAvailable) {
            try {
                ThrowableMethods.fgThrowableInitCauseMethod.invoke((Object)lSException, throwable);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return lSException;
    }

    static class ThrowableMethods {
        private static Method fgThrowableInitCauseMethod = null;
        private static boolean fgThrowableMethodsAvailable = false;

        private ThrowableMethods() {
        }

        static {
            try {
                fgThrowableInitCauseMethod = Throwable.class.getMethod("initCause", Throwable.class);
                fgThrowableMethodsAvailable = true;
            }
            catch (Exception exception) {
                fgThrowableInitCauseMethod = null;
                fgThrowableMethodsAvailable = false;
            }
        }
    }
}

