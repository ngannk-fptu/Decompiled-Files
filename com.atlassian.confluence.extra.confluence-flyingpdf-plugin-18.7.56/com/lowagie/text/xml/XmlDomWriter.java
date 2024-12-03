/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XmlDomWriter {
    protected PrintWriter fOut;
    protected boolean fCanonical;
    protected boolean fXML11;

    public XmlDomWriter() {
    }

    public XmlDomWriter(boolean canonical) {
        this.fCanonical = canonical;
    }

    public void setCanonical(boolean canonical) {
        this.fCanonical = canonical;
    }

    public void setOutput(OutputStream stream, String encoding) throws UnsupportedEncodingException {
        if (encoding == null) {
            encoding = "UTF8";
        }
        OutputStreamWriter writer = new OutputStreamWriter(stream, encoding);
        this.fOut = new PrintWriter(writer);
    }

    public void setOutput(Writer writer) {
        this.fOut = writer instanceof PrintWriter ? (PrintWriter)writer : new PrintWriter(writer);
    }

    public void write(Node node) {
        if (node == null) {
            return;
        }
        short type = node.getNodeType();
        switch (type) {
            case 9: {
                Document document = (Document)node;
                this.fXML11 = false;
                if (!this.fCanonical) {
                    if (this.fXML11) {
                        this.fOut.println("<?xml version=\"1.1\" encoding=\"UTF-8\"?>");
                    } else {
                        this.fOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    }
                    this.fOut.flush();
                    this.write(document.getDoctype());
                }
                this.write(document.getDocumentElement());
                break;
            }
            case 10: {
                DocumentType doctype = (DocumentType)node;
                this.fOut.print("<!DOCTYPE ");
                this.fOut.print(doctype.getName());
                String publicId = doctype.getPublicId();
                String systemId = doctype.getSystemId();
                if (publicId != null) {
                    this.fOut.print(" PUBLIC '");
                    this.fOut.print(publicId);
                    this.fOut.print("' '");
                    this.fOut.print(systemId);
                    this.fOut.print('\'');
                } else if (systemId != null) {
                    this.fOut.print(" SYSTEM '");
                    this.fOut.print(systemId);
                    this.fOut.print('\'');
                }
                String internalSubset = doctype.getInternalSubset();
                if (internalSubset != null) {
                    this.fOut.println(" [");
                    this.fOut.print(internalSubset);
                    this.fOut.print(']');
                }
                this.fOut.println('>');
                break;
            }
            case 1: {
                Attr[] attrs;
                this.fOut.print('<');
                this.fOut.print(node.getNodeName());
                for (Attr attr : attrs = this.sortAttributes(node.getAttributes())) {
                    this.fOut.print(' ');
                    this.fOut.print(attr.getNodeName());
                    this.fOut.print("=\"");
                    this.normalizeAndPrint(attr.getNodeValue(), true);
                    this.fOut.print('\"');
                }
                this.fOut.print('>');
                this.fOut.flush();
                for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                    this.write(child);
                }
                break;
            }
            case 5: {
                if (this.fCanonical) {
                    for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                        this.write(child);
                    }
                    break;
                }
                this.fOut.print('&');
                this.fOut.print(node.getNodeName());
                this.fOut.print(';');
                this.fOut.flush();
                break;
            }
            case 4: {
                if (this.fCanonical) {
                    this.normalizeAndPrint(node.getNodeValue(), false);
                } else {
                    this.fOut.print("<![CDATA[");
                    this.fOut.print(node.getNodeValue());
                    this.fOut.print("]]>");
                }
                this.fOut.flush();
                break;
            }
            case 3: {
                this.normalizeAndPrint(node.getNodeValue(), false);
                this.fOut.flush();
                break;
            }
            case 7: {
                this.fOut.print("<?");
                this.fOut.print(node.getNodeName());
                String data = node.getNodeValue();
                if (data != null && data.length() > 0) {
                    this.fOut.print(' ');
                    this.fOut.print(data);
                }
                this.fOut.print("?>");
                this.fOut.flush();
                break;
            }
            case 8: {
                if (this.fCanonical) break;
                this.fOut.print("<!--");
                String comment = node.getNodeValue();
                if (comment != null && comment.length() > 0) {
                    this.fOut.print(comment);
                }
                this.fOut.print("-->");
                this.fOut.flush();
            }
        }
        if (type == 1) {
            this.fOut.print("</");
            this.fOut.print(node.getNodeName());
            this.fOut.print('>');
            this.fOut.flush();
        }
    }

    protected Attr[] sortAttributes(NamedNodeMap attrs) {
        int i;
        int len = attrs != null ? attrs.getLength() : 0;
        Attr[] array = new Attr[len];
        for (i = 0; i < len; ++i) {
            array[i] = (Attr)attrs.item(i);
        }
        for (i = 0; i < len - 1; ++i) {
            String name = array[i].getNodeName();
            int index = i;
            for (int j = i + 1; j < len; ++j) {
                String curName = array[j].getNodeName();
                if (curName.compareTo(name) >= 0) continue;
                name = curName;
                index = j;
            }
            if (index == i) continue;
            Attr temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
        return array;
    }

    protected void normalizeAndPrint(String s, boolean isAttValue) {
        int len = s != null ? s.length() : 0;
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            this.normalizeAndPrint(c, isAttValue);
        }
    }

    protected void normalizeAndPrint(char c, boolean isAttValue) {
        switch (c) {
            case '<': {
                this.fOut.print("&lt;");
                break;
            }
            case '>': {
                this.fOut.print("&gt;");
                break;
            }
            case '&': {
                this.fOut.print("&amp;");
                break;
            }
            case '\"': {
                if (isAttValue) {
                    this.fOut.print("&quot;");
                    break;
                }
                this.fOut.print("\"");
                break;
            }
            case '\r': {
                this.fOut.print("&#xD;");
                break;
            }
            case '\n': {
                if (this.fCanonical) {
                    this.fOut.print("&#xA;");
                    break;
                }
            }
            default: {
                if (this.fXML11 && (c >= '\u0001' && c <= '\u001f' && c != '\t' && c != '\n' || c >= '\u007f' && c <= '\u009f' || c == '\u2028') || isAttValue && (c == '\t' || c == '\n')) {
                    this.fOut.print("&#x");
                    this.fOut.print(Integer.toHexString(c).toUpperCase());
                    this.fOut.print(";");
                    break;
                }
                this.fOut.print(c);
            }
        }
    }
}

