/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.util.xml;

import com.ibm.wsdl.util.ObjectRegistry;
import com.ibm.wsdl.util.StringUtils;
import com.ibm.wsdl.util.xml.DOMUtils;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DOM2Writer {
    private static String NS_URI_XMLNS = "http://www.w3.org/2000/xmlns/";
    private static String NS_URI_XML = "http://www.w3.org/XML/1998/namespace";
    private static Map xmlEncodingMap = new HashMap();

    public static String nodeToString(Node node) {
        return DOM2Writer.nodeToString(node, new HashMap());
    }

    public static String nodeToString(Node node, Map namespaces) {
        StringWriter sw = new StringWriter();
        DOM2Writer.serializeAsXML(node, namespaces, sw);
        return sw.toString();
    }

    public static void serializeElementAsDocument(Element el, Writer writer) {
        DOM2Writer.serializeElementAsDocument(el, new HashMap(), writer);
    }

    public static void serializeElementAsDocument(Element el, Map namespaces, Writer writer) {
        PrintWriter pw = new PrintWriter(writer);
        String javaEncoding = writer instanceof OutputStreamWriter ? ((OutputStreamWriter)writer).getEncoding() : null;
        String xmlEncoding = DOM2Writer.java2XMLEncoding(javaEncoding);
        if (xmlEncoding != null) {
            pw.println("<?xml version=\"1.0\" encoding=\"" + xmlEncoding + "\"?>");
        } else {
            pw.println("<?xml version=\"1.0\"?>");
        }
        DOM2Writer.serializeAsXML(el, namespaces, writer);
    }

    public static void serializeAsXML(Node node, Writer writer) {
        DOM2Writer.serializeAsXML(node, new HashMap(), writer);
    }

    public static void serializeAsXML(Node node, Map namespaces, Writer writer) {
        ObjectRegistry namespaceStack = new ObjectRegistry(namespaces);
        namespaceStack.register("xml", NS_URI_XML);
        PrintWriter pw = new PrintWriter(writer);
        String javaEncoding = writer instanceof OutputStreamWriter ? ((OutputStreamWriter)writer).getEncoding() : null;
        DOM2Writer.print(node, namespaceStack, pw, DOM2Writer.java2XMLEncoding(javaEncoding));
    }

    private static void print(Node node, ObjectRegistry namespaceStack, PrintWriter out, String xmlEncoding) {
        if (node == null) {
            return;
        }
        boolean hasChildren = false;
        short type = node.getNodeType();
        switch (type) {
            case 9: {
                if (xmlEncoding != null) {
                    out.println("<?xml version=\"1.0\" encoding=\"" + xmlEncoding + "\"?>");
                } else {
                    out.println("<?xml version=\"1.0\"?>");
                }
                for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                    DOM2Writer.print(child, namespaceStack, out, xmlEncoding);
                }
                break;
            }
            case 1: {
                NamedNodeMap attrs;
                namespaceStack = new ObjectRegistry(namespaceStack);
                out.print('<' + node.getNodeName());
                String elPrefix = node.getPrefix();
                String elNamespaceURI = node.getNamespaceURI();
                if (elPrefix != null && elNamespaceURI != null) {
                    boolean prefixIsDeclared = false;
                    try {
                        String namespaceURI = (String)namespaceStack.lookup(elPrefix);
                        if (elNamespaceURI.equals(namespaceURI)) {
                            prefixIsDeclared = true;
                        }
                    }
                    catch (IllegalArgumentException e) {
                        // empty catch block
                    }
                    if (!prefixIsDeclared) {
                        DOM2Writer.printNamespaceDecl(node, namespaceStack, out);
                    }
                }
                int len = (attrs = node.getAttributes()) != null ? attrs.getLength() : 0;
                for (int i = 0; i < len; ++i) {
                    Attr attr = (Attr)attrs.item(i);
                    out.print(' ' + attr.getNodeName() + "=\"" + DOM2Writer.normalize(attr.getValue()) + '\"');
                    String attrPrefix = attr.getPrefix();
                    String attrNamespaceURI = attr.getNamespaceURI();
                    if (attrPrefix == null || attrNamespaceURI == null) continue;
                    boolean prefixIsDeclared = false;
                    try {
                        String namespaceURI = (String)namespaceStack.lookup(attrPrefix);
                        if (attrNamespaceURI.equals(namespaceURI)) {
                            prefixIsDeclared = true;
                        }
                    }
                    catch (IllegalArgumentException e) {
                        // empty catch block
                    }
                    if (prefixIsDeclared) continue;
                    DOM2Writer.printNamespaceDecl(attr, namespaceStack, out);
                }
                Node child = node.getFirstChild();
                if (child != null) {
                    hasChildren = true;
                    out.print('>');
                    while (child != null) {
                        DOM2Writer.print(child, namespaceStack, out, xmlEncoding);
                        child = child.getNextSibling();
                    }
                    break;
                }
                hasChildren = false;
                out.print("/>");
                break;
            }
            case 5: {
                out.print('&');
                out.print(node.getNodeName());
                out.print(';');
                break;
            }
            case 4: {
                out.print("<![CDATA[");
                out.print(node.getNodeValue());
                out.print("]]>");
                break;
            }
            case 3: {
                out.print(DOM2Writer.normalize(node.getNodeValue()));
                break;
            }
            case 8: {
                out.print("<!--");
                out.print(node.getNodeValue());
                out.print("-->");
                break;
            }
            case 7: {
                out.print("<?");
                out.print(node.getNodeName());
                String data = node.getNodeValue();
                if (data != null && data.length() > 0) {
                    out.print(' ');
                    out.print(data);
                }
                out.println("?>");
                break;
            }
        }
        if (type == 1 && hasChildren) {
            out.print("</");
            out.print(node.getNodeName());
            out.print('>');
            hasChildren = false;
        }
    }

    public static String java2XMLEncoding(String javaEnc) {
        return (String)xmlEncodingMap.get(javaEnc);
    }

    private static void printNamespaceDecl(Node node, ObjectRegistry namespaceStack, PrintWriter out) {
        switch (node.getNodeType()) {
            case 2: {
                DOM2Writer.printNamespaceDecl(((Attr)node).getOwnerElement(), node, namespaceStack, out);
                break;
            }
            case 1: {
                DOM2Writer.printNamespaceDecl((Element)node, node, namespaceStack, out);
            }
        }
    }

    private static void printNamespaceDecl(Element owner, Node node, ObjectRegistry namespaceStack, PrintWriter out) {
        String namespaceURI = node.getNamespaceURI();
        String prefix = node.getPrefix();
        if (!namespaceURI.equals(NS_URI_XMLNS) || !prefix.equals("xmlns")) {
            if (DOMUtils.getAttributeNS(owner, NS_URI_XMLNS, prefix) == null) {
                out.print(" xmlns:" + prefix + "=\"" + namespaceURI + '\"');
            }
        } else {
            prefix = node.getLocalName();
            namespaceURI = node.getNodeValue();
        }
        namespaceStack.register(prefix, namespaceURI);
    }

    private static String normalize(String s) {
        StringBuffer str = new StringBuffer();
        int len = s != null ? s.length() : 0;
        block7: for (int i = 0; i < len; ++i) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<': {
                    str.append("&lt;");
                    continue block7;
                }
                case '>': {
                    str.append("&gt;");
                    continue block7;
                }
                case '&': {
                    str.append("&amp;");
                    continue block7;
                }
                case '\"': {
                    str.append("&quot;");
                    continue block7;
                }
                case '\n': {
                    if (i > 0) {
                        char lastChar = str.charAt(str.length() - 1);
                        if (lastChar != '\r') {
                            str.append(StringUtils.lineSeparator);
                            continue block7;
                        }
                        str.append('\n');
                        continue block7;
                    }
                    str.append(StringUtils.lineSeparator);
                    continue block7;
                }
                default: {
                    str.append(ch);
                }
            }
        }
        return str.toString();
    }

    static {
        xmlEncodingMap.put(null, "UTF-8");
        xmlEncodingMap.put(System.getProperty("file.encoding"), "UTF-8");
        xmlEncodingMap.put("UTF8", "UTF-8");
        xmlEncodingMap.put("UTF-16", "UTF-16");
        xmlEncodingMap.put("UnicodeBig", "UTF-16");
        xmlEncodingMap.put("UnicodeLittle", "UTF-16");
        xmlEncodingMap.put("ASCII", "US-ASCII");
        xmlEncodingMap.put("ISO8859_1", "ISO-8859-1");
        xmlEncodingMap.put("ISO8859_2", "ISO-8859-2");
        xmlEncodingMap.put("ISO8859_3", "ISO-8859-3");
        xmlEncodingMap.put("ISO8859_4", "ISO-8859-4");
        xmlEncodingMap.put("ISO8859_5", "ISO-8859-5");
        xmlEncodingMap.put("ISO8859_6", "ISO-8859-6");
        xmlEncodingMap.put("ISO8859_7", "ISO-8859-7");
        xmlEncodingMap.put("ISO8859_8", "ISO-8859-8");
        xmlEncodingMap.put("ISO8859_9", "ISO-8859-9");
        xmlEncodingMap.put("ISO8859_13", "ISO-8859-13");
        xmlEncodingMap.put("ISO8859_15_FDIS", "ISO-8859-15");
        xmlEncodingMap.put("GBK", "GBK");
        xmlEncodingMap.put("Big5", "Big5");
    }
}

