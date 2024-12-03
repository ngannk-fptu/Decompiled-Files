/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.constants.XMLConstants
 *  org.apache.batik.xml.XMLUtilities
 */
package org.apache.batik.dom.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.batik.constants.XMLConstants;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.xml.XMLUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtilities
extends XMLUtilities
implements XMLConstants {
    protected static final String[] LOCK_STRINGS = new String[]{"", "CapsLock", "NumLock", "NumLock CapsLock", "Scroll", "Scroll CapsLock", "Scroll NumLock", "Scroll NumLock CapsLock", "KanaMode", "KanaMode CapsLock", "KanaMode NumLock", "KanaMode NumLock CapsLock", "KanaMode Scroll", "KanaMode Scroll CapsLock", "KanaMode Scroll NumLock", "KanaMode Scroll NumLock CapsLock"};
    protected static final String[] MODIFIER_STRINGS = new String[]{"", "Shift", "Control", "Control Shift", "Meta", "Meta Shift", "Control Meta", "Control Meta Shift", "Alt", "Alt Shift", "Alt Control", "Alt Control Shift", "Alt Meta", "Alt Meta Shift", "Alt Control Meta", "Alt Control Meta Shift", "AltGraph", "AltGraph Shift", "AltGraph Control", "AltGraph Control Shift", "AltGraph Meta", "AltGraph Meta Shift", "AltGraph Control Meta", "AltGraph Control Meta Shift", "Alt AltGraph", "Alt AltGraph Shift", "Alt AltGraph Control", "Alt AltGraph Control Shift", "Alt AltGraph Meta", "Alt AltGraph Meta Shift", "Alt AltGraph Control Meta", "Alt AltGraph Control Meta Shift"};

    protected DOMUtilities() {
    }

    public static void writeDocument(Document doc, Writer w) throws IOException {
        AbstractDocument d = (AbstractDocument)doc;
        if (doc.getDocumentElement() == null) {
            throw new IOException("No document element");
        }
        NSMap m = NSMap.create();
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            DOMUtilities.writeNode(n, w, m, "1.1".equals(d.getXmlVersion()));
        }
    }

    protected static void writeNode(Node n, Writer w, NSMap m, boolean isXML11) throws IOException {
        switch (n.getNodeType()) {
            case 1: {
                Node c;
                String tagName;
                if (n.hasAttributes()) {
                    NamedNodeMap attr = n.getAttributes();
                    int len = attr.getLength();
                    for (int i = 0; i < len; ++i) {
                        Attr a = (Attr)attr.item(i);
                        String name = a.getNodeName();
                        if (!name.startsWith("xmlns")) continue;
                        if (name.length() == 5) {
                            m = m.declare("", a.getNodeValue());
                            continue;
                        }
                        String prefix = name.substring(6);
                        m = m.declare(prefix, a.getNodeValue());
                    }
                }
                w.write(60);
                String ns = n.getNamespaceURI();
                if (ns == null) {
                    tagName = n.getNodeName();
                    w.write(tagName);
                    if (!"".equals(m.getNamespace(""))) {
                        w.write(" xmlns=\"\"");
                        m = m.declare("", "");
                    }
                } else {
                    String prefix = n.getPrefix();
                    if (prefix == null) {
                        prefix = "";
                    }
                    if (ns.equals(m.getNamespace(prefix))) {
                        tagName = n.getNodeName();
                        w.write(tagName);
                    } else {
                        prefix = m.getPrefixForElement(ns);
                        if (prefix == null) {
                            prefix = m.getNewPrefix();
                            tagName = prefix + ':' + n.getLocalName();
                            w.write(tagName + " xmlns:" + prefix + "=\"" + DOMUtilities.contentToString(ns, isXML11) + '\"');
                            m = m.declare(prefix, ns);
                        } else {
                            tagName = prefix.equals("") ? n.getLocalName() : prefix + ':' + n.getLocalName();
                            w.write(tagName);
                        }
                    }
                }
                if (n.hasAttributes()) {
                    NamedNodeMap attr = n.getAttributes();
                    int len = attr.getLength();
                    for (int i = 0; i < len; ++i) {
                        Attr a = (Attr)attr.item(i);
                        String name = a.getNodeName();
                        String prefix = a.getPrefix();
                        String ans = a.getNamespaceURI();
                        if (!(ans == null || "xmlns".equals(prefix) || name.equals("xmlns") || (prefix == null || ans.equals(m.getNamespace(prefix))) && prefix != null)) {
                            prefix = m.getPrefixForAttr(ans);
                            if (prefix == null) {
                                prefix = m.getNewPrefix();
                                m = m.declare(prefix, ans);
                                w.write(" xmlns:" + prefix + "=\"" + DOMUtilities.contentToString(ans, isXML11) + '\"');
                            }
                            name = prefix + ':' + a.getLocalName();
                        }
                        w.write(' ' + name + "=\"" + DOMUtilities.contentToString(a.getNodeValue(), isXML11) + '\"');
                    }
                }
                if ((c = n.getFirstChild()) != null) {
                    w.write(62);
                    do {
                        DOMUtilities.writeNode(c, w, m, isXML11);
                    } while ((c = c.getNextSibling()) != null);
                    w.write("</" + tagName + '>');
                    break;
                }
                w.write("/>");
                break;
            }
            case 3: {
                w.write(DOMUtilities.contentToString(n.getNodeValue(), isXML11));
                break;
            }
            case 4: {
                String data = n.getNodeValue();
                if (data.indexOf("]]>") != -1) {
                    throw new IOException("Unserializable CDATA section node");
                }
                w.write("<![CDATA[" + DOMUtilities.assertValidCharacters(data, isXML11) + "]]>");
                break;
            }
            case 5: {
                w.write('&' + n.getNodeName() + ';');
                break;
            }
            case 7: {
                String target = n.getNodeName();
                String data = n.getNodeValue();
                if (target.equalsIgnoreCase("xml") || target.indexOf(58) != -1 || data.indexOf("?>") != -1) {
                    throw new IOException("Unserializable processing instruction node");
                }
                w.write("<?" + target + ' ' + data + "?>");
                break;
            }
            case 8: {
                w.write("<!--");
                String data = n.getNodeValue();
                int len = data.length();
                if (len != 0 && data.charAt(len - 1) == '-' || data.indexOf("--") != -1) {
                    throw new IOException("Unserializable comment node");
                }
                w.write(data);
                w.write("-->");
                break;
            }
            case 10: {
                String subset;
                String sysID;
                DocumentType dt = (DocumentType)n;
                w.write("<!DOCTYPE " + n.getOwnerDocument().getDocumentElement().getNodeName());
                String pubID = dt.getPublicId();
                if (pubID != null) {
                    char q = DOMUtilities.getUsableQuote(pubID);
                    if (q == '\u0000') {
                        throw new IOException("Unserializable DOCTYPE node");
                    }
                    w.write(" PUBLIC " + q + pubID + q);
                }
                if ((sysID = dt.getSystemId()) != null) {
                    char q = DOMUtilities.getUsableQuote(sysID);
                    if (q == '\u0000') {
                        throw new IOException("Unserializable DOCTYPE node");
                    }
                    if (pubID == null) {
                        w.write(" SYSTEM");
                    }
                    w.write(" " + q + sysID + q);
                }
                if ((subset = dt.getInternalSubset()) != null) {
                    w.write('[' + subset + ']');
                }
                w.write(62);
                break;
            }
            default: {
                throw new IOException("Unknown DOM node type " + n.getNodeType());
            }
        }
    }

    public static void writeNode(Node n, Writer w) throws IOException {
        if (n.getNodeType() == 9) {
            DOMUtilities.writeDocument((Document)n, w);
        } else {
            AbstractDocument d = (AbstractDocument)n.getOwnerDocument();
            DOMUtilities.writeNode(n, w, NSMap.create(), d == null ? false : "1.1".equals(d.getXmlVersion()));
        }
    }

    private static char getUsableQuote(String s) {
        int ret = 0;
        for (int i = s.length() - 1; i >= 0; --i) {
            char c = s.charAt(i);
            if (c == '\"') {
                if (ret == 0) {
                    ret = 39;
                    continue;
                }
                return '\u0000';
            }
            if (c != '\'') continue;
            if (ret == 0) {
                ret = 34;
                continue;
            }
            return '\u0000';
        }
        return (char)(ret == 0 ? 34 : ret);
    }

    public static String getXML(Node n) {
        StringWriter writer = new StringWriter();
        try {
            DOMUtilities.writeNode(n, writer);
            ((Writer)writer).close();
        }
        catch (IOException ex) {
            return "";
        }
        return ((Object)writer).toString();
    }

    protected static String assertValidCharacters(String s, boolean isXML11) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if ((isXML11 || DOMUtilities.isXMLCharacter((int)c)) && (!isXML11 || DOMUtilities.isXML11Character((int)c))) continue;
            throw new IOException("Invalid character");
        }
        return s;
    }

    public static String contentToString(String s, boolean isXML11) throws IOException {
        StringBuffer result = new StringBuffer(s.length());
        int len = s.length();
        block7: for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (!isXML11 && !DOMUtilities.isXMLCharacter((int)c) || isXML11 && !DOMUtilities.isXML11Character((int)c)) {
                throw new IOException("Invalid character");
            }
            switch (c) {
                case '<': {
                    result.append("&lt;");
                    continue block7;
                }
                case '>': {
                    result.append("&gt;");
                    continue block7;
                }
                case '&': {
                    result.append("&amp;");
                    continue block7;
                }
                case '\"': {
                    result.append("&quot;");
                    continue block7;
                }
                case '\'': {
                    result.append("&apos;");
                    continue block7;
                }
                default: {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    public static int getChildIndex(Node child, Node parent) {
        if (child == null || child.getParentNode() != parent || child.getParentNode() == null) {
            return -1;
        }
        return DOMUtilities.getChildIndex(child);
    }

    public static int getChildIndex(Node child) {
        NodeList children = child.getParentNode().getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node currentChild = children.item(i);
            if (currentChild != child) continue;
            return i;
        }
        return -1;
    }

    public static boolean isAnyNodeAncestorOf(ArrayList ancestorNodes, Node node) {
        int n = ancestorNodes.size();
        for (Object ancestorNode : ancestorNodes) {
            Node ancestor = (Node)ancestorNode;
            if (!DOMUtilities.isAncestorOf(ancestor, node)) continue;
            return true;
        }
        return false;
    }

    public static boolean isAncestorOf(Node node, Node descendant) {
        if (node == null || descendant == null) {
            return false;
        }
        for (Node currentNode = descendant.getParentNode(); currentNode != null; currentNode = currentNode.getParentNode()) {
            if (currentNode != node) continue;
            return true;
        }
        return false;
    }

    public static boolean isParentOf(Node node, Node parentNode) {
        return node != null && parentNode != null && node.getParentNode() == parentNode;
    }

    public static boolean canAppend(Node node, Node parentNode) {
        return node != null && parentNode != null && node != parentNode && !DOMUtilities.isAncestorOf(node, parentNode);
    }

    public static boolean canAppendAny(ArrayList children, Node parentNode) {
        if (!DOMUtilities.canHaveChildren(parentNode)) {
            return false;
        }
        int n = children.size();
        for (Object aChildren : children) {
            Node child = (Node)aChildren;
            if (!DOMUtilities.canAppend(child, parentNode)) continue;
            return true;
        }
        return false;
    }

    public static boolean canHaveChildren(Node parentNode) {
        if (parentNode == null) {
            return false;
        }
        switch (parentNode.getNodeType()) {
            case 3: 
            case 4: 
            case 7: 
            case 8: 
            case 9: {
                return false;
            }
        }
        return true;
    }

    public static Node parseXML(String text, Document doc, String uri, Map prefixes, String wrapperElementName, SAXDocumentFactory documentFactory) {
        String wrapperElementPrefix = "";
        String wrapperElementSuffix = "";
        if (wrapperElementName != null) {
            wrapperElementPrefix = "<" + wrapperElementName;
            if (prefixes != null) {
                wrapperElementPrefix = wrapperElementPrefix + " ";
                Iterator iterator = prefixes.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry o;
                    Map.Entry e = o = iterator.next();
                    String currentKey = (String)e.getKey();
                    String currentValue = (String)e.getValue();
                    wrapperElementPrefix = wrapperElementPrefix + currentKey + "=\"" + currentValue + "\" ";
                }
            }
            wrapperElementPrefix = wrapperElementPrefix + ">";
            wrapperElementSuffix = wrapperElementSuffix + "</" + wrapperElementName + '>';
        }
        if (wrapperElementPrefix.trim().length() == 0 && wrapperElementSuffix.trim().length() == 0) {
            try {
                Document d = documentFactory.createDocument(uri, new StringReader(text));
                if (doc == null) {
                    return d;
                }
                DocumentFragment result = doc.createDocumentFragment();
                result.appendChild(doc.importNode(d.getDocumentElement(), true));
                return result;
            }
            catch (Exception d) {
                // empty catch block
            }
        }
        StringBuffer sb = new StringBuffer(wrapperElementPrefix.length() + text.length() + wrapperElementSuffix.length());
        sb.append(wrapperElementPrefix);
        sb.append(text);
        sb.append(wrapperElementSuffix);
        String newText = sb.toString();
        try {
            Document d = documentFactory.createDocument(uri, new StringReader(newText));
            if (doc == null) {
                return d;
            }
            for (Node node = d.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeType() != 1) continue;
                node = doc.importNode(node, true);
                DocumentFragment result = doc.createDocumentFragment();
                result.appendChild(node);
                return result;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public static Document deepCloneDocument(Document doc, DOMImplementation impl) {
        Element root = doc.getDocumentElement();
        Document result = impl.createDocument(root.getNamespaceURI(), root.getNodeName(), null);
        Element rroot = result.getDocumentElement();
        boolean before = true;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n == root) {
                before = false;
                if (root.hasAttributes()) {
                    NamedNodeMap attr = root.getAttributes();
                    int len = attr.getLength();
                    for (int i = 0; i < len; ++i) {
                        rroot.setAttributeNode((Attr)result.importNode(attr.item(i), true));
                    }
                }
                for (Node c = root.getFirstChild(); c != null; c = c.getNextSibling()) {
                    rroot.appendChild(result.importNode(c, true));
                }
                continue;
            }
            if (n.getNodeType() == 10) continue;
            if (before) {
                result.insertBefore(result.importNode(n, true), rroot);
                continue;
            }
            result.appendChild(result.importNode(n, true));
        }
        return result;
    }

    public static boolean isValidName(String s) {
        int m;
        int len = s.length();
        if (len == 0) {
            return false;
        }
        char c = s.charAt(0);
        int d = c / 32;
        if ((NAME_FIRST_CHARACTER[d] & 1 << (m = c % 32)) == 0) {
            return false;
        }
        for (int i = 1; i < len; ++i) {
            c = s.charAt(i);
            d = c / 32;
            if ((NAME_CHARACTER[d] & 1 << (m = c % 32)) != 0) continue;
            return false;
        }
        return true;
    }

    public static boolean isValidName11(String s) {
        int m;
        int len = s.length();
        if (len == 0) {
            return false;
        }
        char c = s.charAt(0);
        int d = c / 32;
        if ((NAME11_FIRST_CHARACTER[d] & 1 << (m = c % 32)) == 0) {
            return false;
        }
        for (int i = 1; i < len; ++i) {
            c = s.charAt(i);
            d = c / 32;
            if ((NAME11_CHARACTER[d] & 1 << (m = c % 32)) != 0) continue;
            return false;
        }
        return true;
    }

    public static boolean isValidPrefix(String s) {
        return s.indexOf(58) == -1;
    }

    public static String getPrefix(String s) {
        int i = s.indexOf(58);
        return i == -1 || i == s.length() - 1 ? null : s.substring(0, i);
    }

    public static String getLocalName(String s) {
        int i = s.indexOf(58);
        return i == -1 || i == s.length() - 1 ? s : s.substring(i + 1);
    }

    public static void parseStyleSheetPIData(String data, HashMap<String, String> table) {
        char c;
        int i;
        for (i = 0; i < data.length() && XMLUtilities.isXMLSpace((char)(c = data.charAt(i))); ++i) {
        }
        while (i < data.length()) {
            int m;
            c = data.charAt(i);
            int d = c / 32;
            if ((NAME_FIRST_CHARACTER[d] & 1 << (m = c % 32)) == 0) {
                throw new DOMException(5, "Wrong name initial:  " + c);
            }
            StringBuffer ident = new StringBuffer();
            ident.append(c);
            while (++i < data.length() && (NAME_CHARACTER[d = (c = data.charAt(i)) / 32] & 1 << (m = c % 32)) != 0) {
                ident.append(c);
            }
            if (i >= data.length()) {
                throw new DOMException(12, "Wrong xml-stylesheet data: " + data);
            }
            while (i < data.length() && XMLUtilities.isXMLSpace((char)(c = data.charAt(i)))) {
                ++i;
            }
            if (i >= data.length()) {
                throw new DOMException(12, "Wrong xml-stylesheet data: " + data);
            }
            if (data.charAt(i) != '=') {
                throw new DOMException(12, "Wrong xml-stylesheet data: " + data);
            }
            ++i;
            while (i < data.length() && XMLUtilities.isXMLSpace((char)(c = data.charAt(i)))) {
                ++i;
            }
            if (i >= data.length()) {
                throw new DOMException(12, "Wrong xml-stylesheet data: " + data);
            }
            c = data.charAt(i);
            ++i;
            StringBuffer value = new StringBuffer();
            if (c == '\'') {
                while (i < data.length() && (c = data.charAt(i)) != '\'') {
                    value.append(c);
                    ++i;
                }
                if (i >= data.length()) {
                    throw new DOMException(12, "Wrong xml-stylesheet data: " + data);
                }
            } else if (c == '\"') {
                while (i < data.length() && (c = data.charAt(i)) != '\"') {
                    value.append(c);
                    ++i;
                }
                if (i >= data.length()) {
                    throw new DOMException(12, "Wrong xml-stylesheet data: " + data);
                }
            } else {
                throw new DOMException(12, "Wrong xml-stylesheet data: " + data);
            }
            table.put(ident.toString().intern(), value.toString());
            ++i;
            while (i < data.length() && XMLUtilities.isXMLSpace((char)(c = data.charAt(i)))) {
                ++i;
            }
        }
    }

    public static String getModifiersList(int lockState, int modifiersEx) {
        modifiersEx = (modifiersEx & 0x2000) != 0 ? 0x10 | modifiersEx >> 6 & 0xF : modifiersEx >> 6 & 0xF;
        String s = LOCK_STRINGS[lockState & 0xF];
        if (s.length() != 0) {
            return s + ' ' + MODIFIER_STRINGS[modifiersEx];
        }
        return MODIFIER_STRINGS[modifiersEx];
    }

    public static boolean isAttributeSpecifiedNS(Element e, String namespaceURI, String localName) {
        Attr a = e.getAttributeNodeNS(namespaceURI, localName);
        return a != null && a.getSpecified();
    }

    private static final class NSMap {
        private String prefix;
        private String ns;
        private NSMap next;
        private int nextPrefixNumber;

        public static NSMap create() {
            return new NSMap().declare("xml", "http://www.w3.org/XML/1998/namespace").declare("xmlns", "http://www.w3.org/2000/xmlns/");
        }

        private NSMap() {
        }

        public NSMap declare(String prefix, String ns) {
            NSMap m = new NSMap();
            m.prefix = prefix;
            m.ns = ns;
            m.next = this;
            m.nextPrefixNumber = this.nextPrefixNumber;
            return m;
        }

        public String getNewPrefix() {
            String prefix;
            while (this.getNamespace(prefix = "a" + this.nextPrefixNumber++) != null) {
            }
            return prefix;
        }

        public String getNamespace(String prefix) {
            NSMap m = this;
            while (m.next != null) {
                if (m.prefix.equals(prefix)) {
                    return m.ns;
                }
                m = m.next;
            }
            return null;
        }

        public String getPrefixForElement(String ns) {
            NSMap m = this;
            while (m.next != null) {
                if (ns.equals(m.ns)) {
                    return m.prefix;
                }
                m = m.next;
            }
            return null;
        }

        public String getPrefixForAttr(String ns) {
            NSMap m = this;
            while (m.next != null) {
                if (ns.equals(m.ns) && !m.prefix.equals("")) {
                    return m.prefix;
                }
                m = m.next;
            }
            return null;
        }
    }
}

