/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.util.xml;

import com.ibm.wsdl.util.xml.QNameUtils;
import com.ibm.wsdl.util.xml.XPathUtils;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DOMUtils {
    private static String NS_URI_XMLNS = "http://www.w3.org/2000/xmlns/";
    private static final String ATTR_XMLNS = "xmlns";

    public static List getAttributes(Element el) {
        String prefix = null;
        Vector<Node> attrs = new Vector<Node>();
        NamedNodeMap attrMap = el.getAttributes();
        for (int i = 0; i < attrMap.getLength(); ++i) {
            String nodename = attrMap.item(i).getNodeName();
            prefix = attrMap.item(i).getPrefix();
            if (ATTR_XMLNS.equals(nodename) || ATTR_XMLNS.equals(prefix)) continue;
            attrs.add(attrMap.item(i));
        }
        return attrs;
    }

    public static String getAttribute(Element el, String attrName) {
        String sRet = null;
        Attr attr = el.getAttributeNode(attrName);
        if (attr != null) {
            sRet = attr.getValue();
        }
        return sRet;
    }

    public static String getAttribute(Element el, String attrName, List remainingAttrs) {
        String sRet = null;
        Attr attr = el.getAttributeNode(attrName);
        if (attr != null) {
            sRet = attr.getValue();
            remainingAttrs.remove(attr);
        }
        return sRet;
    }

    public static String getAttributeNS(Element el, String namespaceURI, String localPart) {
        String sRet = null;
        Attr attr = el.getAttributeNodeNS(namespaceURI, localPart);
        if (attr != null) {
            sRet = attr.getValue();
        }
        return sRet;
    }

    public static String getChildCharacterData(Element parentEl) {
        if (parentEl == null) {
            return null;
        }
        StringBuffer strBuf = new StringBuffer();
        for (Node tempNode = parentEl.getFirstChild(); tempNode != null; tempNode = tempNode.getNextSibling()) {
            switch (tempNode.getNodeType()) {
                case 3: 
                case 4: {
                    CharacterData charData = (CharacterData)tempNode;
                    strBuf.append(charData.getData());
                }
            }
        }
        return strBuf.toString();
    }

    public static Element getFirstChildElement(Element elem) {
        for (Node n = elem.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            return (Element)n;
        }
        return null;
    }

    public static Element getNextSiblingElement(Element elem) {
        for (Node n = elem.getNextSibling(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            return (Element)n;
        }
        return null;
    }

    public static Element findChildElementWithAttribute(Element elem, String attrName, String attrValue) {
        for (Node n = elem.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1 || !attrValue.equals(DOMUtils.getAttribute((Element)n, attrName))) continue;
            return (Element)n;
        }
        return null;
    }

    public static int countKids(Element elem, short nodeType) {
        int nkids = 0;
        for (Node n = elem.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != nodeType) continue;
            ++nkids;
        }
        return nkids;
    }

    public static String getNamespaceURIFromPrefix(Node context, String prefix) {
        short nodeType = context.getNodeType();
        Node tempNode = null;
        switch (nodeType) {
            case 2: {
                tempNode = ((Attr)context).getOwnerElement();
                break;
            }
            case 1: {
                tempNode = context;
                break;
            }
            default: {
                tempNode = context.getParentNode();
            }
        }
        while (tempNode != null && tempNode.getNodeType() == 1) {
            String namespaceURI;
            Element tempEl = (Element)tempNode;
            String string = namespaceURI = prefix == null ? DOMUtils.getAttribute(tempEl, ATTR_XMLNS) : DOMUtils.getAttributeNS(tempEl, NS_URI_XMLNS, prefix);
            if (namespaceURI != null) {
                return namespaceURI;
            }
            tempNode = tempEl.getParentNode();
        }
        return null;
    }

    public static QName getQName(String prefixedValue, Element contextEl, Definition def) throws WSDLException {
        int index = prefixedValue.indexOf(58);
        String prefix = index != -1 ? prefixedValue.substring(0, index) : null;
        String localPart = prefixedValue.substring(index + 1);
        String namespaceURI = DOMUtils.getNamespaceURIFromPrefix(contextEl, prefix);
        if (namespaceURI != null) {
            DOMUtils.registerUniquePrefix(prefix, namespaceURI, def);
            return new QName(namespaceURI, localPart);
        }
        String faultCode = prefix == null ? "NO_PREFIX_SPECIFIED" : "UNBOUND_PREFIX";
        WSDLException wsdlExc = new WSDLException(faultCode, "Unable to determine namespace of '" + prefixedValue + "'.");
        wsdlExc.setLocation(XPathUtils.getXPathExprFromNode(contextEl));
        throw wsdlExc;
    }

    public static void registerUniquePrefix(String prefix, String namespaceURI, Definition def) {
        String tempNSUri = def.getNamespace(prefix);
        if (tempNSUri != null && tempNSUri.equals(namespaceURI)) {
            return;
        }
        Collection nSpaces = def.getNamespaces().values();
        if (nSpaces.contains(namespaceURI)) {
            return;
        }
        while (tempNSUri != null && !tempNSUri.equals(namespaceURI)) {
            prefix = prefix != null ? prefix + "_" : "_";
            tempNSUri = def.getNamespace(prefix);
        }
        def.addNamespace(prefix, namespaceURI);
    }

    public static QName getQualifiedAttributeValue(Element el, String attrName, String elDesc, boolean isRequired, Definition def) throws WSDLException {
        String attrValue = DOMUtils.getAttribute(el, attrName);
        if (attrValue != null) {
            return DOMUtils.getQName(attrValue, el, def);
        }
        if (isRequired) {
            WSDLException wsdlExc = new WSDLException("INVALID_WSDL", "The '" + attrName + "' attribute must be " + "specified for every " + elDesc + " element.");
            wsdlExc.setLocation(XPathUtils.getXPathExprFromNode(el));
            throw wsdlExc;
        }
        return null;
    }

    public static QName getQualifiedAttributeValue(Element el, String attrName, String elDesc, boolean isRequired, Definition def, List remainingAttrs) throws WSDLException {
        String attrValue = null;
        attrValue = DOMUtils.getAttribute(el, attrName, remainingAttrs);
        if (attrValue != null) {
            return DOMUtils.getQName(attrValue, el, def);
        }
        if (isRequired) {
            WSDLException wsdlExc = new WSDLException("INVALID_WSDL", "The '" + attrName + "' attribute must be " + "specified for every " + elDesc + " element.");
            wsdlExc.setLocation(XPathUtils.getXPathExprFromNode(el));
            throw wsdlExc;
        }
        return null;
    }

    public static void throwWSDLException(Element location) throws WSDLException {
        String elName = QNameUtils.newQName(location).toString();
        WSDLException wsdlExc = new WSDLException("INVALID_WSDL", "Encountered unexpected element '" + elName + "'.");
        wsdlExc.setLocation(XPathUtils.getXPathExprFromNode(location));
        throw wsdlExc;
    }

    public static void printAttribute(String name, String value, PrintWriter pw) {
        if (value != null) {
            pw.print(' ' + name + "=\"" + DOMUtils.cleanString(value) + '\"');
        }
    }

    public static void printQualifiedAttribute(QName name, String value, Definition def, PrintWriter pw) throws WSDLException {
        if (name != null) {
            DOMUtils.printAttribute(DOMUtils.getQualifiedValue(name.getNamespaceURI(), name.getLocalPart(), def), value, pw);
        }
    }

    public static void printQualifiedAttribute(QName name, QName value, Definition def, PrintWriter pw) throws WSDLException {
        if (value != null) {
            DOMUtils.printAttribute(DOMUtils.getQualifiedValue(name.getNamespaceURI(), name.getLocalPart(), def), DOMUtils.getQualifiedValue(value.getNamespaceURI(), value.getLocalPart(), def), pw);
        }
    }

    public static void printQualifiedAttribute(String name, QName value, Definition def, PrintWriter pw) throws WSDLException {
        if (value != null) {
            DOMUtils.printAttribute(name, DOMUtils.getQualifiedValue(value.getNamespaceURI(), value.getLocalPart(), def), pw);
        }
    }

    public static String getQualifiedValue(String namespaceURI, String localPart, Definition def) throws WSDLException {
        String prefix = null;
        if (namespaceURI != null && !namespaceURI.equals("")) {
            prefix = DOMUtils.getPrefix(namespaceURI, def);
        }
        return (prefix != null && !prefix.equals("") ? prefix + ":" : "") + localPart;
    }

    public static String getPrefix(String namespaceURI, Definition def) throws WSDLException {
        String prefix = def.getPrefix(namespaceURI);
        if (prefix == null) {
            throw new WSDLException("OTHER_ERROR", "Can't find prefix for '" + namespaceURI + "'. Namespace prefixes must be set on the" + " Definition object using the " + "addNamespace(...) method.");
        }
        return prefix;
    }

    public static String cleanString(String orig) {
        if (orig == null) {
            return "";
        }
        StringBuffer strBuf = new StringBuffer();
        char[] chars = orig.toCharArray();
        boolean inCDATA = false;
        for (int i = 0; i < chars.length; ++i) {
            if (!inCDATA) {
                switch (chars[i]) {
                    case '&': {
                        strBuf.append("&amp;");
                        break;
                    }
                    case '\"': {
                        strBuf.append("&quot;");
                        break;
                    }
                    case '\'': {
                        strBuf.append("&apos;");
                        break;
                    }
                    case '<': {
                        if (chars.length >= i + 9) {
                            String tempStr = new String(chars, i, 9);
                            if (tempStr.equals("<![CDATA[")) {
                                strBuf.append(tempStr);
                                i += 8;
                                inCDATA = true;
                                break;
                            }
                            strBuf.append("&lt;");
                            break;
                        }
                        strBuf.append("&lt;");
                        break;
                    }
                    case '>': {
                        strBuf.append("&gt;");
                        break;
                    }
                    default: {
                        strBuf.append(chars[i]);
                        break;
                    }
                }
                continue;
            }
            strBuf.append(chars[i]);
            if (chars[i] != '>' || chars[i - 1] != ']' || chars[i - 2] != ']') continue;
            inCDATA = false;
        }
        return strBuf.toString();
    }
}

