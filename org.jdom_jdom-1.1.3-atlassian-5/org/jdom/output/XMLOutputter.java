/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.output;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.IllegalDataException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.Verifier;
import org.jdom.output.EscapeStrategy;
import org.jdom.output.Format;

public class XMLOutputter
implements Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: XMLOutputter.java,v $ $Revision: 1.117 $ $Date: 2009/07/23 05:54:23 $ $Name:  $";
    private Format userFormat;
    protected static final Format preserveFormat = Format.getRawFormat();
    protected Format currentFormat;
    private boolean escapeOutput;

    public XMLOutputter() {
        this.currentFormat = this.userFormat = Format.getRawFormat();
        this.escapeOutput = true;
    }

    public XMLOutputter(Format format) {
        this.currentFormat = this.userFormat = Format.getRawFormat();
        this.escapeOutput = true;
        this.currentFormat = this.userFormat = (Format)format.clone();
    }

    public XMLOutputter(XMLOutputter that) {
        this.currentFormat = this.userFormat = Format.getRawFormat();
        this.escapeOutput = true;
        this.currentFormat = this.userFormat = (Format)that.userFormat.clone();
    }

    public void setFormat(Format newFormat) {
        this.currentFormat = this.userFormat = (Format)newFormat.clone();
    }

    public Format getFormat() {
        return (Format)this.userFormat.clone();
    }

    public void output(Document doc, OutputStream out) throws IOException {
        Writer writer = this.makeWriter(out);
        this.output(doc, writer);
    }

    public void output(DocType doctype, OutputStream out) throws IOException {
        Writer writer = this.makeWriter(out);
        this.output(doctype, writer);
    }

    public void output(Element element, OutputStream out) throws IOException {
        Writer writer = this.makeWriter(out);
        this.output(element, writer);
    }

    public void outputElementContent(Element element, OutputStream out) throws IOException {
        Writer writer = this.makeWriter(out);
        this.outputElementContent(element, writer);
    }

    public void output(List list, OutputStream out) throws IOException {
        Writer writer = this.makeWriter(out);
        this.output(list, writer);
    }

    public void output(CDATA cdata, OutputStream out) throws IOException {
        Writer writer = this.makeWriter(out);
        this.output(cdata, writer);
    }

    public void output(Text text, OutputStream out) throws IOException {
        Writer writer = this.makeWriter(out);
        this.output(text, writer);
    }

    public void output(Comment comment, OutputStream out) throws IOException {
        Writer writer = this.makeWriter(out);
        this.output(comment, writer);
    }

    public void output(ProcessingInstruction pi, OutputStream out) throws IOException {
        Writer writer = this.makeWriter(out);
        this.output(pi, writer);
    }

    public void output(EntityRef entity, OutputStream out) throws IOException {
        Writer writer = this.makeWriter(out);
        this.output(entity, writer);
    }

    private Writer makeWriter(OutputStream out) throws UnsupportedEncodingException {
        return XMLOutputter.makeWriter(out, this.userFormat.encoding);
    }

    private static Writer makeWriter(OutputStream out, String enc) throws UnsupportedEncodingException {
        if ("UTF-8".equals(enc)) {
            enc = "UTF8";
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)new BufferedOutputStream(out), enc));
        return writer;
    }

    public void output(Document doc, Writer out) throws IOException {
        this.printDeclaration(out, doc, this.userFormat.encoding);
        List content = doc.getContent();
        int size = content.size();
        for (int i = 0; i < size; ++i) {
            Object obj = content.get(i);
            if (obj instanceof Element) {
                this.printElement(out, doc.getRootElement(), 0, this.createNamespaceStack());
            } else if (obj instanceof Comment) {
                this.printComment(out, (Comment)obj);
            } else if (obj instanceof ProcessingInstruction) {
                this.printProcessingInstruction(out, (ProcessingInstruction)obj);
            } else if (obj instanceof DocType) {
                this.printDocType(out, doc.getDocType());
                out.write(this.currentFormat.lineSeparator);
            }
            this.newline(out);
            this.indent(out, 0);
        }
        out.write(this.currentFormat.lineSeparator);
        out.flush();
    }

    public void output(DocType doctype, Writer out) throws IOException {
        this.printDocType(out, doctype);
        out.flush();
    }

    public void output(Element element, Writer out) throws IOException {
        this.printElement(out, element, 0, this.createNamespaceStack());
        out.flush();
    }

    public void outputElementContent(Element element, Writer out) throws IOException {
        List content = element.getContent();
        this.printContentRange(out, content, 0, content.size(), 0, this.createNamespaceStack());
        out.flush();
    }

    public void output(List list, Writer out) throws IOException {
        this.printContentRange(out, list, 0, list.size(), 0, this.createNamespaceStack());
        out.flush();
    }

    public void output(CDATA cdata, Writer out) throws IOException {
        this.printCDATA(out, cdata);
        out.flush();
    }

    public void output(Text text, Writer out) throws IOException {
        this.printText(out, text);
        out.flush();
    }

    public void output(Comment comment, Writer out) throws IOException {
        this.printComment(out, comment);
        out.flush();
    }

    public void output(ProcessingInstruction pi, Writer out) throws IOException {
        boolean currentEscapingPolicy = this.currentFormat.ignoreTrAXEscapingPIs;
        this.currentFormat.setIgnoreTrAXEscapingPIs(true);
        this.printProcessingInstruction(out, pi);
        this.currentFormat.setIgnoreTrAXEscapingPIs(currentEscapingPolicy);
        out.flush();
    }

    public void output(EntityRef entity, Writer out) throws IOException {
        this.printEntityRef(out, entity);
        out.flush();
    }

    public String outputString(Document doc) {
        StringWriter out = new StringWriter();
        try {
            this.output(doc, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public String outputString(DocType doctype) {
        StringWriter out = new StringWriter();
        try {
            this.output(doctype, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public String outputString(Element element) {
        StringWriter out = new StringWriter();
        try {
            this.output(element, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public String outputString(List list) {
        StringWriter out = new StringWriter();
        try {
            this.output(list, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public String outputString(CDATA cdata) {
        StringWriter out = new StringWriter();
        try {
            this.output(cdata, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public String outputString(Text text) {
        StringWriter out = new StringWriter();
        try {
            this.output(text, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public String outputString(Comment comment) {
        StringWriter out = new StringWriter();
        try {
            this.output(comment, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public String outputString(ProcessingInstruction pi) {
        StringWriter out = new StringWriter();
        try {
            this.output(pi, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public String outputString(EntityRef entity) {
        StringWriter out = new StringWriter();
        try {
            this.output(entity, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    protected void printDeclaration(Writer out, Document doc, String encoding) throws IOException {
        if (!this.userFormat.omitDeclaration) {
            out.write("<?xml version=\"1.0\"");
            if (!this.userFormat.omitEncoding) {
                out.write(" encoding=\"" + encoding + "\"");
            }
            out.write("?>");
            out.write(this.currentFormat.lineSeparator);
        }
    }

    protected void printDocType(Writer out, DocType docType) throws IOException {
        String publicID = docType.getPublicID();
        String systemID = docType.getSystemID();
        String internalSubset = docType.getInternalSubset();
        boolean hasPublic = false;
        out.write("<!DOCTYPE ");
        out.write(docType.getElementName());
        if (publicID != null) {
            out.write(" PUBLIC \"");
            out.write(publicID);
            out.write("\"");
            hasPublic = true;
        }
        if (systemID != null) {
            if (!hasPublic) {
                out.write(" SYSTEM");
            }
            out.write(" \"");
            out.write(systemID);
            out.write("\"");
        }
        if (internalSubset != null && !internalSubset.equals("")) {
            out.write(" [");
            out.write(this.currentFormat.lineSeparator);
            out.write(docType.getInternalSubset());
            out.write("]");
        }
        out.write(">");
    }

    protected void printComment(Writer out, Comment comment) throws IOException {
        out.write("<!--");
        out.write(comment.getText());
        out.write("-->");
    }

    protected void printProcessingInstruction(Writer out, ProcessingInstruction pi) throws IOException {
        String target = pi.getTarget();
        boolean piProcessed = false;
        if (!this.currentFormat.ignoreTrAXEscapingPIs) {
            if (target.equals("javax.xml.transform.disable-output-escaping")) {
                this.escapeOutput = false;
                piProcessed = true;
            } else if (target.equals("javax.xml.transform.enable-output-escaping")) {
                this.escapeOutput = true;
                piProcessed = true;
            }
        }
        if (!piProcessed) {
            String rawData = pi.getData();
            if (!"".equals(rawData)) {
                out.write("<?");
                out.write(target);
                out.write(" ");
                out.write(rawData);
                out.write("?>");
            } else {
                out.write("<?");
                out.write(target);
                out.write("?>");
            }
        }
    }

    protected void printEntityRef(Writer out, EntityRef entity) throws IOException {
        out.write("&");
        out.write(entity.getName());
        out.write(";");
    }

    protected void printCDATA(Writer out, CDATA cdata) throws IOException {
        String str = this.currentFormat.mode == Format.TextMode.NORMALIZE ? cdata.getTextNormalize() : (this.currentFormat.mode == Format.TextMode.TRIM ? cdata.getText().trim() : cdata.getText());
        out.write("<![CDATA[");
        out.write(str);
        out.write("]]>");
    }

    protected void printText(Writer out, Text text) throws IOException {
        String str = this.currentFormat.mode == Format.TextMode.NORMALIZE ? text.getTextNormalize() : (this.currentFormat.mode == Format.TextMode.TRIM ? text.getText().trim() : text.getText());
        out.write(this.escapeElementEntities(str));
    }

    private void printString(Writer out, String str) throws IOException {
        if (this.currentFormat.mode == Format.TextMode.NORMALIZE) {
            str = Text.normalizeString(str);
        } else if (this.currentFormat.mode == Format.TextMode.TRIM) {
            str = str.trim();
        }
        out.write(this.escapeElementEntities(str));
    }

    protected void printElement(Writer out, Element element, int level, NamespaceStack namespaces) throws IOException {
        int size;
        int start;
        List attributes = element.getAttributes();
        List content = element.getContent();
        String space = null;
        if (attributes != null) {
            space = element.getAttributeValue("space", Namespace.XML_NAMESPACE);
        }
        Format previousFormat = this.currentFormat;
        if ("default".equals(space)) {
            this.currentFormat = this.userFormat;
        } else if ("preserve".equals(space)) {
            this.currentFormat = preserveFormat;
        }
        out.write("<");
        this.printQualifiedName(out, element);
        int previouslyDeclaredNamespaces = namespaces.size();
        this.printElementNamespace(out, element, namespaces);
        this.printAdditionalNamespaces(out, element, namespaces);
        if (attributes != null) {
            this.printAttributes(out, attributes, element, namespaces);
        }
        if ((start = this.skipLeadingWhite(content, 0)) >= (size = content.size())) {
            if (this.currentFormat.expandEmptyElements) {
                out.write("></");
                this.printQualifiedName(out, element);
                out.write(">");
            } else {
                out.write(" />");
            }
        } else {
            out.write(">");
            if (XMLOutputter.nextNonText(content, start) < size) {
                this.newline(out);
                this.printContentRange(out, content, start, size, level + 1, namespaces);
                this.newline(out);
                this.indent(out, level);
            } else {
                this.printTextRange(out, content, start, size);
            }
            out.write("</");
            this.printQualifiedName(out, element);
            out.write(">");
        }
        while (namespaces.size() > previouslyDeclaredNamespaces) {
            namespaces.pop();
        }
        this.currentFormat = previousFormat;
    }

    private void printContentRange(Writer out, List content, int start, int end, int level, NamespaceStack namespaces) throws IOException {
        int index = start;
        while (index < end) {
            boolean firstNode = index == start;
            Object next = content.get(index);
            if (next instanceof Text || next instanceof EntityRef) {
                int first = this.skipLeadingWhite(content, index);
                if (first >= (index = XMLOutputter.nextNonText(content, first))) continue;
                if (!firstNode) {
                    this.newline(out);
                }
                this.indent(out, level);
                this.printTextRange(out, content, first, index);
                continue;
            }
            if (!firstNode) {
                this.newline(out);
            }
            this.indent(out, level);
            if (next instanceof Comment) {
                this.printComment(out, (Comment)next);
            } else if (next instanceof Element) {
                this.printElement(out, (Element)next, level, namespaces);
            } else if (next instanceof ProcessingInstruction) {
                this.printProcessingInstruction(out, (ProcessingInstruction)next);
            }
            ++index;
        }
    }

    private void printTextRange(Writer out, List content, int start, int end) throws IOException {
        int size;
        String previous = null;
        if ((start = this.skipLeadingWhite(content, start)) < (size = content.size())) {
            end = this.skipTrailingWhite(content, end);
            for (int i = start; i < end; ++i) {
                String next;
                Object node = content.get(i);
                if (node instanceof Text) {
                    next = ((Text)node).getText();
                } else if (node instanceof EntityRef) {
                    next = "&" + ((EntityRef)node).getValue() + ";";
                } else {
                    throw new IllegalStateException("Should see only CDATA, Text, or EntityRef");
                }
                if (next == null || "".equals(next)) continue;
                if (previous != null && (this.currentFormat.mode == Format.TextMode.NORMALIZE || this.currentFormat.mode == Format.TextMode.TRIM) && (this.endsWithWhite(previous) || this.startsWithWhite(next))) {
                    out.write(" ");
                }
                if (node instanceof CDATA) {
                    this.printCDATA(out, (CDATA)node);
                } else if (node instanceof EntityRef) {
                    this.printEntityRef(out, (EntityRef)node);
                } else {
                    this.printString(out, next);
                }
                previous = next;
            }
        }
    }

    private void printNamespace(Writer out, Namespace ns, NamespaceStack namespaces) throws IOException {
        String prefix = ns.getPrefix();
        String uri = ns.getURI();
        if (uri.equals(namespaces.getURI(prefix))) {
            return;
        }
        out.write(" xmlns");
        if (!prefix.equals("")) {
            out.write(":");
            out.write(prefix);
        }
        out.write("=\"");
        out.write(this.escapeAttributeEntities(uri));
        out.write("\"");
        namespaces.push(ns);
    }

    protected void printAttributes(Writer out, List attributes, Element parent, NamespaceStack namespaces) throws IOException {
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute attribute = (Attribute)attributes.get(i);
            Namespace ns = attribute.getNamespace();
            if (ns != Namespace.NO_NAMESPACE && ns != Namespace.XML_NAMESPACE) {
                this.printNamespace(out, ns, namespaces);
            }
            out.write(" ");
            this.printQualifiedName(out, attribute);
            out.write("=");
            out.write("\"");
            out.write(this.escapeAttributeEntities(attribute.getValue()));
            out.write("\"");
        }
    }

    private void printElementNamespace(Writer out, Element element, NamespaceStack namespaces) throws IOException {
        Namespace ns = element.getNamespace();
        if (ns == Namespace.XML_NAMESPACE) {
            return;
        }
        if (ns != Namespace.NO_NAMESPACE || namespaces.getURI("") != null) {
            this.printNamespace(out, ns, namespaces);
        }
    }

    private void printAdditionalNamespaces(Writer out, Element element, NamespaceStack namespaces) throws IOException {
        List list = element.getAdditionalNamespaces();
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                Namespace additional = (Namespace)list.get(i);
                this.printNamespace(out, additional, namespaces);
            }
        }
    }

    private void newline(Writer out) throws IOException {
        if (this.currentFormat.indent != null) {
            out.write(this.currentFormat.lineSeparator);
        }
    }

    private void indent(Writer out, int level) throws IOException {
        if (this.currentFormat.indent == null || this.currentFormat.indent.equals("")) {
            return;
        }
        for (int i = 0; i < level; ++i) {
            out.write(this.currentFormat.indent);
        }
    }

    private int skipLeadingWhite(List content, int start) {
        int index;
        if (start < 0) {
            start = 0;
        }
        int size = content.size();
        if (this.currentFormat.mode == Format.TextMode.TRIM_FULL_WHITE || this.currentFormat.mode == Format.TextMode.NORMALIZE || this.currentFormat.mode == Format.TextMode.TRIM) {
            for (index = start; index < size; ++index) {
                if (this.isAllWhitespace(content.get(index))) continue;
                return index;
            }
        }
        return index;
    }

    private int skipTrailingWhite(List content, int start) {
        int index;
        int size = content.size();
        if (start > size) {
            start = size;
        }
        if (this.currentFormat.mode == Format.TextMode.TRIM_FULL_WHITE || this.currentFormat.mode == Format.TextMode.NORMALIZE || this.currentFormat.mode == Format.TextMode.TRIM) {
            for (index = start; index >= 0 && this.isAllWhitespace(content.get(index - 1)); --index) {
            }
        }
        return index;
    }

    private static int nextNonText(List content, int start) {
        if (start < 0) {
            start = 0;
        }
        int size = content.size();
        for (int index = start; index < size; ++index) {
            Object node = content.get(index);
            if (node instanceof Text || node instanceof EntityRef) continue;
            return index;
        }
        return size;
    }

    private boolean isAllWhitespace(Object obj) {
        String str = null;
        if (obj instanceof String) {
            str = (String)obj;
        } else if (obj instanceof Text) {
            str = ((Text)obj).getText();
        } else {
            if (obj instanceof EntityRef) {
                return false;
            }
            return false;
        }
        for (int i = 0; i < str.length(); ++i) {
            if (Verifier.isXMLWhitespace(str.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private boolean startsWithWhite(String str) {
        return str != null && str.length() > 0 && Verifier.isXMLWhitespace(str.charAt(0));
    }

    private boolean endsWithWhite(String str) {
        return str != null && str.length() > 0 && Verifier.isXMLWhitespace(str.charAt(str.length() - 1));
    }

    public String escapeAttributeEntities(String str) {
        EscapeStrategy strategy = this.currentFormat.escapeStrategy;
        StringBuffer buffer = null;
        for (int i = 0; i < str.length(); ++i) {
            String entity;
            int ch = str.charAt(i);
            int pos = i++;
            switch (ch) {
                case 60: {
                    entity = "&lt;";
                    break;
                }
                case 62: {
                    entity = "&gt;";
                    break;
                }
                case 34: {
                    entity = "&quot;";
                    break;
                }
                case 38: {
                    entity = "&amp;";
                    break;
                }
                case 13: {
                    entity = "&#xD;";
                    break;
                }
                case 9: {
                    entity = "&#x9;";
                    break;
                }
                case 10: {
                    entity = "&#xA;";
                    break;
                }
                default: {
                    if (strategy.shouldEscape((char)ch)) {
                        if (Verifier.isHighSurrogate((char)ch)) {
                            if (i < str.length()) {
                                char low = str.charAt(i);
                                if (!Verifier.isLowSurrogate(low)) {
                                    throw new IllegalDataException("Could not decode surrogate pair 0x" + Integer.toHexString(ch) + " / 0x" + Integer.toHexString(low));
                                }
                                ch = Verifier.decodeSurrogatePair((char)ch, low);
                            } else {
                                throw new IllegalDataException("Surrogate pair 0x" + Integer.toHexString(ch) + " truncated");
                            }
                        }
                        entity = "&#x" + Integer.toHexString(ch) + ";";
                        break;
                    }
                    entity = null;
                }
            }
            if (buffer == null) {
                if (entity == null) continue;
                buffer = new StringBuffer(str.length() + 20);
                buffer.append(str.substring(0, pos));
                buffer.append(entity);
                continue;
            }
            if (entity == null) {
                buffer.append((char)ch);
                continue;
            }
            buffer.append(entity);
        }
        return buffer == null ? str : buffer.toString();
    }

    public String escapeElementEntities(String str) {
        if (!this.escapeOutput) {
            return str;
        }
        EscapeStrategy strategy = this.currentFormat.escapeStrategy;
        StringBuffer buffer = null;
        for (int i = 0; i < str.length(); ++i) {
            String entity;
            int ch = str.charAt(i);
            int pos = i++;
            switch (ch) {
                case 60: {
                    entity = "&lt;";
                    break;
                }
                case 62: {
                    entity = "&gt;";
                    break;
                }
                case 38: {
                    entity = "&amp;";
                    break;
                }
                case 13: {
                    entity = "&#xD;";
                    break;
                }
                case 10: {
                    entity = this.currentFormat.lineSeparator;
                    break;
                }
                default: {
                    if (strategy.shouldEscape((char)ch)) {
                        if (Verifier.isHighSurrogate((char)ch)) {
                            if (i < str.length()) {
                                char low = str.charAt(i);
                                if (!Verifier.isLowSurrogate(low)) {
                                    throw new IllegalDataException("Could not decode surrogate pair 0x" + Integer.toHexString(ch) + " / 0x" + Integer.toHexString(low));
                                }
                                ch = Verifier.decodeSurrogatePair((char)ch, low);
                            } else {
                                throw new IllegalDataException("Surrogate pair 0x" + Integer.toHexString(ch) + " truncated");
                            }
                        }
                        entity = "&#x" + Integer.toHexString(ch) + ";";
                        break;
                    }
                    entity = null;
                }
            }
            if (buffer == null) {
                if (entity == null) continue;
                buffer = new StringBuffer(str.length() + 20);
                buffer.append(str.substring(0, pos));
                buffer.append(entity);
                continue;
            }
            if (entity == null) {
                buffer.append((char)ch);
                continue;
            }
            buffer.append(entity);
        }
        return buffer == null ? str : buffer.toString();
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        block5: for (int i = 0; i < this.userFormat.lineSeparator.length(); ++i) {
            char ch = this.userFormat.lineSeparator.charAt(i);
            switch (ch) {
                case '\r': {
                    buffer.append("\\r");
                    continue block5;
                }
                case '\n': {
                    buffer.append("\\n");
                    continue block5;
                }
                case '\t': {
                    buffer.append("\\t");
                    continue block5;
                }
                default: {
                    buffer.append("[" + ch + "]");
                }
            }
        }
        return "XMLOutputter[omitDeclaration = " + this.userFormat.omitDeclaration + ", encoding = " + this.userFormat.encoding + ", omitEncoding = " + this.userFormat.omitEncoding + ", indent = '" + this.userFormat.indent + "', expandEmptyElements = " + this.userFormat.expandEmptyElements + ", lineSeparator = '" + buffer.toString() + "', textMode = " + this.userFormat.mode + "]";
    }

    private NamespaceStack createNamespaceStack() {
        return new NamespaceStack();
    }

    private void printQualifiedName(Writer out, Element e) throws IOException {
        if (e.getNamespace().getPrefix().length() == 0) {
            out.write(e.getName());
        } else {
            out.write(e.getNamespace().getPrefix());
            out.write(58);
            out.write(e.getName());
        }
    }

    private void printQualifiedName(Writer out, Attribute a) throws IOException {
        String prefix = a.getNamespace().getPrefix();
        if (prefix != null && !prefix.equals("")) {
            out.write(prefix);
            out.write(58);
            out.write(a.getName());
        } else {
            out.write(a.getName());
        }
    }

    protected class NamespaceStack
    extends org.jdom.output.NamespaceStack {
        protected NamespaceStack() {
        }
    }
}

