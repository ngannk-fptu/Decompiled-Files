/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.support.AbstractXMLOutputProcessor;
import org.jdom2.output.support.FormatStack;
import org.jdom2.output.support.XMLOutputProcessor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class XMLOutputter
implements Cloneable {
    private static final DefaultXMLProcessor DEFAULTPROCESSOR = new DefaultXMLProcessor();
    private Format myFormat = null;
    private XMLOutputProcessor myProcessor = null;

    private static final Writer makeWriter(OutputStream out, Format format) throws UnsupportedEncodingException {
        return new BufferedWriter(new OutputStreamWriter((OutputStream)new BufferedOutputStream(out), format.getEncoding()));
    }

    public XMLOutputter(Format format, XMLOutputProcessor processor) {
        this.myFormat = format == null ? Format.getRawFormat() : format.clone();
        this.myProcessor = processor == null ? DEFAULTPROCESSOR : processor;
    }

    public XMLOutputter() {
        this(null, null);
    }

    public XMLOutputter(XMLOutputter that) {
        this(that.myFormat, null);
    }

    public XMLOutputter(Format format) {
        this(format, null);
    }

    public XMLOutputter(XMLOutputProcessor processor) {
        this(null, processor);
    }

    public void setFormat(Format newFormat) {
        this.myFormat = newFormat.clone();
    }

    public Format getFormat() {
        return this.myFormat;
    }

    public XMLOutputProcessor getXMLOutputProcessor() {
        return this.myProcessor;
    }

    public void setXMLOutputProcessor(XMLOutputProcessor processor) {
        this.myProcessor = processor;
    }

    public final void output(Document doc, OutputStream out) throws IOException {
        this.output(doc, XMLOutputter.makeWriter(out, this.myFormat));
    }

    public final void output(DocType doctype, OutputStream out) throws IOException {
        this.output(doctype, XMLOutputter.makeWriter(out, this.myFormat));
    }

    public final void output(Element element, OutputStream out) throws IOException {
        this.output(element, XMLOutputter.makeWriter(out, this.myFormat));
    }

    public final void outputElementContent(Element element, OutputStream out) throws IOException {
        this.outputElementContent(element, XMLOutputter.makeWriter(out, this.myFormat));
    }

    public final void output(List<? extends Content> list, OutputStream out) throws IOException {
        this.output(list, XMLOutputter.makeWriter(out, this.myFormat));
    }

    public final void output(CDATA cdata, OutputStream out) throws IOException {
        this.output(cdata, XMLOutputter.makeWriter(out, this.myFormat));
    }

    public final void output(Text text, OutputStream out) throws IOException {
        this.output(text, XMLOutputter.makeWriter(out, this.myFormat));
    }

    public final void output(Comment comment, OutputStream out) throws IOException {
        this.output(comment, XMLOutputter.makeWriter(out, this.myFormat));
    }

    public final void output(ProcessingInstruction pi, OutputStream out) throws IOException {
        this.output(pi, XMLOutputter.makeWriter(out, this.myFormat));
    }

    public void output(EntityRef entity, OutputStream out) throws IOException {
        this.output(entity, XMLOutputter.makeWriter(out, this.myFormat));
    }

    public final String outputString(Document doc) {
        StringWriter out = new StringWriter();
        try {
            this.output(doc, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public final String outputString(DocType doctype) {
        StringWriter out = new StringWriter();
        try {
            this.output(doctype, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public final String outputString(Element element) {
        StringWriter out = new StringWriter();
        try {
            this.output(element, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public final String outputString(List<? extends Content> list) {
        StringWriter out = new StringWriter();
        try {
            this.output(list, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public final String outputString(CDATA cdata) {
        StringWriter out = new StringWriter();
        try {
            this.output(cdata, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public final String outputString(Text text) {
        StringWriter out = new StringWriter();
        try {
            this.output(text, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public final String outputString(Comment comment) {
        StringWriter out = new StringWriter();
        try {
            this.output(comment, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public final String outputString(ProcessingInstruction pi) {
        StringWriter out = new StringWriter();
        try {
            this.output(pi, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public final String outputString(EntityRef entity) {
        StringWriter out = new StringWriter();
        try {
            this.output(entity, (Writer)out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public final String outputElementContentString(Element element) {
        StringWriter out = new StringWriter();
        try {
            this.outputElementContent(element, out);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return out.toString();
    }

    public final void output(Document doc, Writer out) throws IOException {
        this.myProcessor.process(out, this.myFormat, doc);
        out.flush();
    }

    public final void output(DocType doctype, Writer out) throws IOException {
        this.myProcessor.process(out, this.myFormat, doctype);
        out.flush();
    }

    public final void output(Element element, Writer out) throws IOException {
        this.myProcessor.process(out, this.myFormat, element);
        out.flush();
    }

    public final void outputElementContent(Element element, Writer out) throws IOException {
        this.myProcessor.process(out, this.myFormat, element.getContent());
        out.flush();
    }

    public final void output(List<? extends Content> list, Writer out) throws IOException {
        this.myProcessor.process(out, this.myFormat, list);
        out.flush();
    }

    public final void output(CDATA cdata, Writer out) throws IOException {
        this.myProcessor.process(out, this.myFormat, cdata);
        out.flush();
    }

    public final void output(Text text, Writer out) throws IOException {
        this.myProcessor.process(out, this.myFormat, text);
        out.flush();
    }

    public final void output(Comment comment, Writer out) throws IOException {
        this.myProcessor.process(out, this.myFormat, comment);
        out.flush();
    }

    public final void output(ProcessingInstruction pi, Writer out) throws IOException {
        this.myProcessor.process(out, this.myFormat, pi);
        out.flush();
    }

    public final void output(EntityRef entity, Writer out) throws IOException {
        this.myProcessor.process(out, this.myFormat, entity);
        out.flush();
    }

    public String escapeAttributeEntities(String str) {
        return DEFAULTPROCESSOR.escapeAttributeEntities(str, this.myFormat);
    }

    public String escapeElementEntities(String str) {
        return DEFAULTPROCESSOR.escapeElementEntities(str, this.myFormat);
    }

    public XMLOutputter clone() {
        try {
            return (XMLOutputter)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("XMLOutputter[omitDeclaration = ");
        buffer.append(this.myFormat.omitDeclaration);
        buffer.append(", ");
        buffer.append("encoding = ");
        buffer.append(this.myFormat.encoding);
        buffer.append(", ");
        buffer.append("omitEncoding = ");
        buffer.append(this.myFormat.omitEncoding);
        buffer.append(", ");
        buffer.append("indent = '");
        buffer.append(this.myFormat.indent);
        buffer.append("'");
        buffer.append(", ");
        buffer.append("expandEmptyElements = ");
        buffer.append(this.myFormat.expandEmptyElements);
        buffer.append(", ");
        buffer.append("lineSeparator = '");
        block5: for (char ch : this.myFormat.lineSeparator.toCharArray()) {
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
        buffer.append("', ");
        buffer.append("textMode = ");
        buffer.append((Object)((Object)this.myFormat.mode) + "]");
        return buffer.toString();
    }

    private static final class DefaultXMLProcessor
    extends AbstractXMLOutputProcessor {
        private DefaultXMLProcessor() {
        }

        public String escapeAttributeEntities(String str, Format format) {
            StringWriter sw = new StringWriter();
            try {
                super.attributeEscapedEntitiesFilter(sw, new FormatStack(format), str);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return sw.toString();
        }

        public final String escapeElementEntities(String str, Format format) {
            return Format.escapeText(format.getEscapeStrategy(), format.getLineSeparator(), str);
        }
    }
}

