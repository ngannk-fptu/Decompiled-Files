/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider.xmlprinter;

import com.opensymphony.provider.ProviderConfigurationException;
import com.opensymphony.provider.XMLPrinterProvider;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DefaultXMLPrinterProvider
implements XMLPrinterProvider {
    private static final String INDENT = "  ";

    @Override
    public void destroy() {
    }

    @Override
    public void init() throws ProviderConfigurationException {
    }

    @Override
    public void print(Document doc, Writer out) throws IOException {
        PrintWriter printWriter = new PrintWriter(out);
        this.walk(printWriter, doc, 0, false);
        printWriter.flush();
        out.flush();
    }

    private void escape(PrintWriter out, String str) throws IOException {
        if (str == null) {
            return;
        }
        str = str.trim();
        block7: for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            switch (c) {
                case '<': {
                    out.print("&lt;");
                    continue block7;
                }
                case '>': {
                    out.print("&gt;");
                    continue block7;
                }
                case '&': {
                    out.print("&amp;");
                    continue block7;
                }
                case '\"': {
                    out.print("&quot;");
                    continue block7;
                }
                case '\n': 
                case '\r': {
                    continue block7;
                }
                default: {
                    out.print(c);
                }
            }
        }
    }

    private void indent(PrintWriter out, int level) throws IOException {
        for (int i = 0; i < level; ++i) {
            out.print(INDENT);
        }
    }

    private void walk(PrintWriter out, Node node, int indent, boolean textOnly) throws IOException {
        boolean keepFormatting;
        if (node == null) {
            return;
        }
        short type = node.getNodeType();
        boolean bl = keepFormatting = textOnly && type == 3 || type == 4;
        if (!keepFormatting) {
            this.indent(out, indent);
        }
        switch (type) {
            case 9: {
                out.print("<?xml version=\"1.0\" ?>\n");
                this.walk(out, ((Document)node).getDocumentElement(), indent, false);
                break;
            }
            case 1: {
                out.print('<');
                out.print(node.getNodeName());
                NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); ++i) {
                    Node attr = attrs.item(i);
                    out.print(' ');
                    out.print(attr.getNodeName());
                    out.print("=\"");
                    this.escape(out, attr.getNodeValue());
                    out.print('\"');
                }
                NodeList children = node.getChildNodes();
                if (children == null || children.getLength() == 0) {
                    out.print('/');
                }
                out.print('>');
                if (children != null) {
                    boolean nodeTextOnly = false;
                    if (children.getLength() == 1 && children.item(0).getNodeType() == 3) {
                        nodeTextOnly = true;
                    }
                    if (children.getLength() > 0 && !nodeTextOnly) {
                        out.print('\n');
                    }
                    for (int i = 0; i < children.getLength(); ++i) {
                        this.walk(out, children.item(i), indent + 1, nodeTextOnly);
                    }
                    if (children.getLength() > 0 && !nodeTextOnly) {
                        this.indent(out, indent);
                    }
                }
                if (children.getLength() <= 0) break;
                out.print("</");
                out.print(node.getNodeName());
                out.print('>');
                break;
            }
            case 3: 
            case 4: {
                this.escape(out, node.getNodeValue());
                break;
            }
            case 7: {
                out.print("<?");
                out.print(node.getNodeName());
                if (node.getNodeValue() != null && node.getNodeValue().length() > 0) {
                    out.print(' ');
                    out.print(node.getNodeValue());
                }
                out.print("?>");
                break;
            }
            case 5: {
                out.print('&');
                out.print(node.getNodeName());
                out.print(';');
            }
        }
        if (!keepFormatting) {
            out.print('\n');
        }
    }
}

