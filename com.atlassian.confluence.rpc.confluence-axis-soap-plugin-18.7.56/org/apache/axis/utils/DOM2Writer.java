/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.NSStack;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOM2Writer {
    public static String nodeToString(Node node, boolean omitXMLDecl) {
        StringWriter sw = new StringWriter();
        DOM2Writer.serializeAsXML(node, sw, omitXMLDecl);
        return sw.toString();
    }

    public static void serializeAsXML(Node node, Writer writer, boolean omitXMLDecl) {
        DOM2Writer.serializeAsXML(node, writer, omitXMLDecl, false);
    }

    public static void serializeAsXML(Node node, Writer writer, boolean omitXMLDecl, boolean pretty) {
        PrintWriter out = new PrintWriter(writer);
        if (!omitXMLDecl) {
            out.print("<?xml version=\"1.0\" encoding=\"");
            out.print(XMLUtils.getEncoding());
            out.println("\"?>");
        }
        NSStack namespaceStack = new NSStack();
        DOM2Writer.print(node, namespaceStack, node, out, pretty, 0);
        out.flush();
    }

    private static void print(Node node, NSStack namespaceStack, Node startnode, PrintWriter out, boolean pretty, int indent) {
        if (node == null) {
            return;
        }
        boolean hasChildren = false;
        short type = node.getNodeType();
        switch (type) {
            case 9: {
                NodeList children = node.getChildNodes();
                if (children == null) break;
                int numChildren = children.getLength();
                for (int i = 0; i < numChildren; ++i) {
                    DOM2Writer.print(children.item(i), namespaceStack, startnode, out, pretty, indent);
                }
                break;
            }
            case 1: {
                NamedNodeMap attrs;
                namespaceStack.push();
                if (pretty) {
                    for (int i = 0; i < indent; ++i) {
                        out.print(' ');
                    }
                }
                out.print('<' + node.getNodeName());
                String elPrefix = node.getPrefix();
                String elNamespaceURI = node.getNamespaceURI();
                if (elPrefix != null && elNamespaceURI != null && elPrefix.length() > 0) {
                    boolean prefixIsDeclared = false;
                    try {
                        String namespaceURI = namespaceStack.getNamespaceURI(elPrefix);
                        if (elNamespaceURI.equals(namespaceURI)) {
                            prefixIsDeclared = true;
                        }
                    }
                    catch (IllegalArgumentException e) {
                        // empty catch block
                    }
                    if (!prefixIsDeclared) {
                        DOM2Writer.printNamespaceDecl(node, namespaceStack, startnode, out);
                    }
                }
                int len = (attrs = node.getAttributes()) != null ? attrs.getLength() : 0;
                for (int i = 0; i < len; ++i) {
                    Attr attr = (Attr)attrs.item(i);
                    out.print(' ' + attr.getNodeName() + "=\"" + DOM2Writer.normalize(attr.getValue()) + '\"');
                    String attrPrefix = attr.getPrefix();
                    String attrNamespaceURI = attr.getNamespaceURI();
                    if (attrPrefix == null || attrNamespaceURI == null || attrPrefix.length() <= 0) continue;
                    boolean prefixIsDeclared = false;
                    try {
                        String namespaceURI = namespaceStack.getNamespaceURI(attrPrefix);
                        if (attrNamespaceURI.equals(namespaceURI)) {
                            prefixIsDeclared = true;
                        }
                    }
                    catch (IllegalArgumentException e) {
                        // empty catch block
                    }
                    if (prefixIsDeclared) continue;
                    DOM2Writer.printNamespaceDecl(attr, namespaceStack, startnode, out);
                }
                NodeList children = node.getChildNodes();
                if (children != null) {
                    int numChildren = children.getLength();
                    boolean bl = hasChildren = numChildren > 0;
                    if (hasChildren) {
                        out.print('>');
                        if (pretty) {
                            out.print(JavaUtils.LS);
                        }
                    }
                    for (int i = 0; i < numChildren; ++i) {
                        DOM2Writer.print(children.item(i), namespaceStack, startnode, out, pretty, indent + 1);
                    }
                } else {
                    hasChildren = false;
                }
                if (!hasChildren) {
                    out.print("/>");
                    if (pretty) {
                        out.print(JavaUtils.LS);
                    }
                }
                namespaceStack.pop();
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
                if (!pretty) break;
                out.print(JavaUtils.LS);
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
                if (!pretty) break;
                out.print(JavaUtils.LS);
                break;
            }
        }
        if (type == 1 && hasChildren) {
            if (pretty) {
                for (int i = 0; i < indent; ++i) {
                    out.print(' ');
                }
            }
            out.print("</");
            out.print(node.getNodeName());
            out.print('>');
            if (pretty) {
                out.print(JavaUtils.LS);
            }
            hasChildren = false;
        }
    }

    private static void printNamespaceDecl(Node node, NSStack namespaceStack, Node startnode, PrintWriter out) {
        switch (node.getNodeType()) {
            case 2: {
                DOM2Writer.printNamespaceDecl(((Attr)node).getOwnerElement(), node, namespaceStack, startnode, out);
                break;
            }
            case 1: {
                DOM2Writer.printNamespaceDecl((Element)node, node, namespaceStack, startnode, out);
            }
        }
    }

    private static void printNamespaceDecl(Element owner, Node node, NSStack namespaceStack, Node startnode, PrintWriter out) {
        String namespaceURI = node.getNamespaceURI();
        String prefix = node.getPrefix();
        if (!(namespaceURI.equals("http://www.w3.org/2000/xmlns/") && prefix.equals("xmlns") || namespaceURI.equals("http://www.w3.org/XML/1998/namespace") && prefix.equals("xml"))) {
            if (XMLUtils.getNamespace(prefix, owner, startnode) == null) {
                out.print(" xmlns:" + prefix + "=\"" + namespaceURI + '\"');
            }
        } else {
            prefix = node.getLocalName();
            namespaceURI = node.getNodeValue();
        }
        namespaceStack.add(namespaceURI, prefix);
    }

    public static String normalize(String s) {
        return XMLUtils.xmlEncodeString(s);
    }
}

