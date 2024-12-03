/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output;

import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
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
import org.jdom2.output.support.AbstractStAXStreamProcessor;
import org.jdom2.output.support.StAXStreamProcessor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class StAXStreamOutputter
implements Cloneable {
    private static final DefaultStAXStreamProcessor DEFAULTPROCESSOR = new DefaultStAXStreamProcessor();
    private Format myFormat = null;
    private StAXStreamProcessor myProcessor = null;

    public StAXStreamOutputter(Format format, StAXStreamProcessor processor) {
        this.myFormat = format == null ? Format.getRawFormat() : format.clone();
        this.myProcessor = processor == null ? DEFAULTPROCESSOR : processor;
    }

    public StAXStreamOutputter() {
        this(null, null);
    }

    public StAXStreamOutputter(Format format) {
        this(format, null);
    }

    public StAXStreamOutputter(StAXStreamProcessor processor) {
        this(null, processor);
    }

    public void setFormat(Format newFormat) {
        this.myFormat = newFormat.clone();
    }

    public Format getFormat() {
        return this.myFormat;
    }

    public StAXStreamProcessor getStAXStream() {
        return this.myProcessor;
    }

    public void setStAXStreamProcessor(StAXStreamProcessor processor) {
        this.myProcessor = processor;
    }

    public final void output(Document doc, XMLStreamWriter out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, doc);
        out.flush();
    }

    public final void output(DocType doctype, XMLStreamWriter out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, doctype);
        out.flush();
    }

    public final void output(Element element, XMLStreamWriter out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, element);
        out.flush();
    }

    public final void outputElementContent(Element element, XMLStreamWriter out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, element.getContent());
        out.flush();
    }

    public final void output(List<? extends Content> list, XMLStreamWriter out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, list);
        out.flush();
    }

    public final void output(CDATA cdata, XMLStreamWriter out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, cdata);
        out.flush();
    }

    public final void output(Text text, XMLStreamWriter out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, text);
        out.flush();
    }

    public final void output(Comment comment, XMLStreamWriter out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, comment);
        out.flush();
    }

    public final void output(ProcessingInstruction pi, XMLStreamWriter out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, pi);
        out.flush();
    }

    public final void output(EntityRef entity, XMLStreamWriter out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, entity);
        out.flush();
    }

    public StAXStreamOutputter clone() {
        try {
            return (StAXStreamOutputter)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("StAXStreamOutputter[omitDeclaration = ");
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

    private static final class DefaultStAXStreamProcessor
    extends AbstractStAXStreamProcessor {
        private DefaultStAXStreamProcessor() {
        }
    }
}

