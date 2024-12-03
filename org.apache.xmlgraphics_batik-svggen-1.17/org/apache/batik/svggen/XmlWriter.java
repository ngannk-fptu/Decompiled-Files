/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.svggen;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

class XmlWriter
implements SVGConstants {
    private static String EOL;
    private static final String TAG_END = "/>";
    private static final String TAG_START = "</";
    private static final char[] SPACES;
    private static final int SPACES_LEN;

    XmlWriter() {
    }

    private static void writeXml(Attr attr, IndentWriter out, boolean escaped) throws IOException {
        String name = attr.getName();
        out.write(name);
        out.write("=\"");
        XmlWriter.writeChildrenXml(attr, out, escaped);
        out.write(34);
    }

    private static void writeChildrenXml(Attr attr, IndentWriter out, boolean escaped) throws IOException {
        int last;
        char[] data = attr.getValue().toCharArray();
        if (data == null) {
            return;
        }
        int length = data.length;
        int start = 0;
        block6: for (last = 0; last < length; ++last) {
            char c = data[last];
            switch (c) {
                case '<': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&lt;");
                    continue block6;
                }
                case '>': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&gt;");
                    continue block6;
                }
                case '&': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&amp;");
                    continue block6;
                }
                case '\"': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&quot;");
                    continue block6;
                }
                default: {
                    if (!escaped || c <= '\u007f') continue block6;
                    out.write(data, start, last - start);
                    String hex = "0000" + Integer.toHexString(c);
                    out.write("&#x" + hex.substring(hex.length() - 4) + ";");
                    start = last + 1;
                }
            }
        }
        out.write(data, start, last - start);
    }

    private static void writeXml(Comment comment, IndentWriter out, boolean escaped) throws IOException {
        int last;
        char[] data = comment.getData().toCharArray();
        if (data == null) {
            out.write("<!---->");
            return;
        }
        out.write("<!--");
        boolean sawDash = false;
        int length = data.length;
        int start = 0;
        for (last = 0; last < length; ++last) {
            char c = data[last];
            if (c == '-') {
                if (sawDash) {
                    out.write(data, start, last - start);
                    start = last;
                    out.write(32);
                }
                sawDash = true;
                continue;
            }
            sawDash = false;
        }
        out.write(data, start, last - start);
        if (sawDash) {
            out.write(32);
        }
        out.write("-->");
    }

    private static void writeXml(Text text, IndentWriter out, boolean escaped) throws IOException {
        XmlWriter.writeXml(text, out, false, escaped);
    }

    private static void writeXml(Text text, IndentWriter out, boolean trimWS, boolean escaped) throws IOException {
        char c;
        int last;
        char[] data = text.getData().toCharArray();
        if (data == null) {
            System.err.println("Null text data??");
            return;
        }
        int length = data.length;
        int start = 0;
        if (trimWS) {
            block12: for (last = 0; last < length; ++last) {
                c = data[last];
                switch (c) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        continue block12;
                    }
                }
            }
            start = last;
        }
        block13: while (last < length) {
            c = data[last];
            switch (c) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    if (!trimWS) break;
                    int wsStart = last++;
                    block14: while (last < length) {
                        switch (data[last]) {
                            case '\t': 
                            case '\n': 
                            case '\r': 
                            case ' ': {
                                ++last;
                                continue block14;
                            }
                        }
                    }
                    if (last != length) continue block13;
                    out.write(data, start, wsStart - start);
                    return;
                }
                case '<': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&lt;");
                    break;
                }
                case '>': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&gt;");
                    break;
                }
                case '&': {
                    out.write(data, start, last - start);
                    start = last + 1;
                    out.write("&amp;");
                    break;
                }
                default: {
                    if (!escaped || c <= '\u007f') break;
                    out.write(data, start, last - start);
                    String hex = "0000" + Integer.toHexString(c);
                    out.write("&#x" + hex.substring(hex.length() - 4) + ";");
                    start = last + 1;
                }
            }
            ++last;
        }
        out.write(data, start, last - start);
    }

    private static void writeXml(CDATASection cdataSection, IndentWriter out, boolean escaped) throws IOException {
        char[] data = cdataSection.getData().toCharArray();
        if (data == null) {
            out.write("<![CDATA[]]>");
            return;
        }
        out.write("<![CDATA[");
        int length = data.length;
        int start = 0;
        int last = 0;
        while (last < length) {
            char c = data[last];
            if (c == ']' && last + 2 < data.length && data[last + 1] == ']' && data[last + 2] == '>') {
                out.write(data, start, last - start);
                start = last + 1;
                out.write("]]]]><![CDATA[>");
                continue;
            }
            ++last;
        }
        out.write(data, start, last - start);
        out.write("]]>");
    }

    private static void writeXml(Element element, IndentWriter out, boolean escaped) throws IOException, SVGGraphics2DIOException {
        boolean lastElem;
        out.write(TAG_START, 0, 1);
        out.write(element.getTagName());
        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null) {
            int nAttr = attributes.getLength();
            for (int i = 0; i < nAttr; ++i) {
                Attr attr = (Attr)attributes.item(i);
                out.write(32);
                XmlWriter.writeXml(attr, out, escaped);
            }
        }
        boolean bl = lastElem = element.getParentNode().getLastChild() == element;
        if (!element.hasChildNodes()) {
            if (lastElem) {
                out.setIndentLevel(out.getIndentLevel() - 2);
            }
            out.printIndent();
            out.write(TAG_END, 0, 2);
            return;
        }
        Node child = element.getFirstChild();
        out.printIndent();
        out.write(TAG_END, 1, 1);
        if (child.getNodeType() != 3 || element.getLastChild() != child) {
            out.setIndentLevel(out.getIndentLevel() + 2);
        }
        XmlWriter.writeChildrenXml(element, out, escaped);
        out.write(TAG_START, 0, 2);
        out.write(element.getTagName());
        if (lastElem) {
            out.setIndentLevel(out.getIndentLevel() - 2);
        }
        out.printIndent();
        out.write(TAG_END, 1, 1);
    }

    private static void writeChildrenXml(Element element, IndentWriter out, boolean escaped) throws IOException, SVGGraphics2DIOException {
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            XmlWriter.writeXml(child, (Writer)out, escaped);
        }
    }

    private static void writeDocumentHeader(IndentWriter out) throws IOException {
        String encoding = null;
        if (out.getProxied() instanceof OutputStreamWriter) {
            OutputStreamWriter osw = (OutputStreamWriter)out.getProxied();
            encoding = XmlWriter.java2std(osw.getEncoding());
        }
        out.write("<?xml version=\"1.0\"");
        if (encoding != null) {
            out.write(" encoding=\"");
            out.write(encoding);
            out.write(34);
        }
        out.write("?>");
        out.write(EOL);
        out.write("<!DOCTYPE svg PUBLIC '");
        out.write("-//W3C//DTD SVG 1.0//EN");
        out.write("'");
        out.write(EOL);
        out.write("          '");
        out.write("http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
        out.write("'");
        out.write(">");
        out.write(EOL);
    }

    private static void writeXml(Document document, IndentWriter out, boolean escaped) throws IOException, SVGGraphics2DIOException {
        XmlWriter.writeDocumentHeader(out);
        NodeList childList = document.getChildNodes();
        XmlWriter.writeXml(childList, out, escaped);
    }

    private static void writeXml(NodeList childList, IndentWriter out, boolean escaped) throws IOException, SVGGraphics2DIOException {
        int length = childList.getLength();
        if (length == 0) {
            return;
        }
        for (int i = 0; i < length; ++i) {
            Node child = childList.item(i);
            XmlWriter.writeXml(child, (Writer)out, escaped);
            out.write(EOL);
        }
    }

    static String java2std(String encodingName) {
        if (encodingName == null) {
            return null;
        }
        if (encodingName.startsWith("ISO8859_")) {
            return "ISO-8859-" + encodingName.substring(8);
        }
        if (encodingName.startsWith("8859_")) {
            return "ISO-8859-" + encodingName.substring(5);
        }
        if ("ASCII7".equalsIgnoreCase(encodingName) || "ASCII".equalsIgnoreCase(encodingName)) {
            return "US-ASCII";
        }
        if ("UTF8".equalsIgnoreCase(encodingName)) {
            return "UTF-8";
        }
        if (encodingName.startsWith("Unicode")) {
            return "UTF-16";
        }
        if ("SJIS".equalsIgnoreCase(encodingName)) {
            return "Shift_JIS";
        }
        if ("JIS".equalsIgnoreCase(encodingName)) {
            return "ISO-2022-JP";
        }
        if ("EUCJIS".equalsIgnoreCase(encodingName)) {
            return "EUC-JP";
        }
        return "UTF-8";
    }

    public static void writeXml(Node node, Writer writer, boolean escaped) throws SVGGraphics2DIOException {
        try {
            IndentWriter out = null;
            out = writer instanceof IndentWriter ? (IndentWriter)writer : new IndentWriter(writer);
            switch (node.getNodeType()) {
                case 2: {
                    XmlWriter.writeXml((Attr)node, out, escaped);
                    break;
                }
                case 8: {
                    XmlWriter.writeXml((Comment)node, out, escaped);
                    break;
                }
                case 3: {
                    XmlWriter.writeXml((Text)node, out, escaped);
                    break;
                }
                case 4: {
                    XmlWriter.writeXml((CDATASection)node, out, escaped);
                    break;
                }
                case 9: {
                    XmlWriter.writeXml((Document)node, out, escaped);
                    break;
                }
                case 11: {
                    XmlWriter.writeDocumentHeader(out);
                    NodeList childList = node.getChildNodes();
                    XmlWriter.writeXml(childList, out, escaped);
                    break;
                }
                case 1: {
                    XmlWriter.writeXml((Element)node, out, escaped);
                    break;
                }
                default: {
                    throw new SVGGraphics2DRuntimeException("Unable to write node of type " + node.getClass().getName());
                }
            }
        }
        catch (IOException io) {
            throw new SVGGraphics2DIOException(io);
        }
    }

    static {
        String temp;
        SPACES = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
        SPACES_LEN = SPACES.length;
        try {
            temp = System.getProperty("line.separator", "\n");
        }
        catch (SecurityException e) {
            temp = "\n";
        }
        EOL = temp;
    }

    static class IndentWriter
    extends Writer {
        protected Writer proxied;
        protected int indentLevel;
        protected int column;

        public IndentWriter(Writer proxied) {
            if (proxied == null) {
                throw new SVGGraphics2DRuntimeException("proxy should not be null");
            }
            this.proxied = proxied;
        }

        public void setIndentLevel(int indentLevel) {
            this.indentLevel = indentLevel;
        }

        public int getIndentLevel() {
            return this.indentLevel;
        }

        public void printIndent() throws IOException {
            this.proxied.write(EOL);
            for (int temp = this.indentLevel; temp > 0; temp -= SPACES_LEN) {
                if (temp > SPACES_LEN) {
                    this.proxied.write(SPACES, 0, SPACES_LEN);
                    continue;
                }
                this.proxied.write(SPACES, 0, temp);
                break;
            }
            this.column = this.indentLevel;
        }

        public Writer getProxied() {
            return this.proxied;
        }

        public int getColumn() {
            return this.column;
        }

        @Override
        public void write(int c) throws IOException {
            ++this.column;
            this.proxied.write(c);
        }

        @Override
        public void write(char[] cbuf) throws IOException {
            this.column += cbuf.length;
            this.proxied.write(cbuf);
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            this.column += len;
            this.proxied.write(cbuf, off, len);
        }

        @Override
        public void write(String str) throws IOException {
            this.column += str.length();
            this.proxied.write(str);
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            this.column += len;
            this.proxied.write(str, off, len);
        }

        @Override
        public void flush() throws IOException {
            this.proxied.flush();
        }

        @Override
        public void close() throws IOException {
            this.column = -1;
            this.proxied.close();
        }
    }
}

