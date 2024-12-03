/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output;

import java.util.List;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.util.XMLEventConsumer;
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
import org.jdom2.output.support.AbstractStAXEventProcessor;
import org.jdom2.output.support.StAXEventProcessor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class StAXEventOutputter
implements Cloneable {
    private static final DefaultStAXEventProcessor DEFAULTPROCESSOR = new DefaultStAXEventProcessor();
    private static final XMLEventFactory DEFAULTEVENTFACTORY = XMLEventFactory.newInstance();
    private Format myFormat = null;
    private StAXEventProcessor myProcessor = null;
    private XMLEventFactory myEventFactory = null;

    public StAXEventOutputter(Format format, StAXEventProcessor processor, XMLEventFactory eventfactory) {
        this.myFormat = format == null ? Format.getRawFormat() : format.clone();
        this.myProcessor = processor == null ? DEFAULTPROCESSOR : processor;
        this.myEventFactory = eventfactory == null ? DEFAULTEVENTFACTORY : eventfactory;
    }

    public StAXEventOutputter() {
        this(null, null, null);
    }

    public StAXEventOutputter(Format format) {
        this(format, null, null);
    }

    public StAXEventOutputter(StAXEventProcessor processor) {
        this(null, processor, null);
    }

    public StAXEventOutputter(XMLEventFactory eventfactory) {
        this(null, null, eventfactory);
    }

    public void setFormat(Format newFormat) {
        this.myFormat = newFormat.clone();
    }

    public Format getFormat() {
        return this.myFormat;
    }

    public StAXEventProcessor getStAXStream() {
        return this.myProcessor;
    }

    public void setStAXEventProcessor(StAXEventProcessor processor) {
        this.myProcessor = processor;
    }

    public XMLEventFactory getEventFactory() {
        return this.myEventFactory;
    }

    public void setEventFactory(XMLEventFactory myEventFactory) {
        this.myEventFactory = myEventFactory;
    }

    public final void output(Document doc, XMLEventConsumer out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, this.myEventFactory, doc);
    }

    public final void output(DocType doctype, XMLEventConsumer out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, this.myEventFactory, doctype);
    }

    public final void output(Element element, XMLEventConsumer out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, this.myEventFactory, element);
    }

    public final void outputElementContent(Element element, XMLEventConsumer out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, this.myEventFactory, element.getContent());
    }

    public final void output(List<? extends Content> list, XMLEventConsumer out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, this.myEventFactory, list);
    }

    public final void output(CDATA cdata, XMLEventConsumer out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, this.myEventFactory, cdata);
    }

    public final void output(Text text, XMLEventConsumer out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, this.myEventFactory, text);
    }

    public final void output(Comment comment, XMLEventConsumer out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, this.myEventFactory, comment);
    }

    public final void output(ProcessingInstruction pi, XMLEventConsumer out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, this.myEventFactory, pi);
    }

    public final void output(EntityRef entity, XMLEventConsumer out) throws XMLStreamException {
        this.myProcessor.process(out, this.myFormat, this.myEventFactory, entity);
    }

    public StAXEventOutputter clone() {
        try {
            return (StAXEventOutputter)super.clone();
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

    private static final class DefaultStAXEventProcessor
    extends AbstractStAXEventProcessor {
        private DefaultStAXEventProcessor() {
        }
    }
}

