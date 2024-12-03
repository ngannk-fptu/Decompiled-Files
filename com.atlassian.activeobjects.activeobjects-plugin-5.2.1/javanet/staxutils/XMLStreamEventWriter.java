/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import javanet.staxutils.BaseXMLEventWriter;
import javanet.staxutils.events.ExtendedXMLEvent;
import javanet.staxutils.io.XMLWriterUtils;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XMLStreamEventWriter
extends BaseXMLEventWriter {
    private XMLStreamWriter writer;
    private StartElement savedStart;

    public XMLStreamEventWriter(XMLStreamWriter writer) {
        super(null, writer.getNamespaceContext());
        this.writer = writer;
    }

    public synchronized void flush() throws XMLStreamException {
        super.flush();
        if (this.savedStart != null) {
            XMLWriterUtils.writeStartElement(this.savedStart, false, this.writer);
        }
        this.writer.flush();
    }

    public synchronized void close() throws XMLStreamException {
        super.close();
        this.writer.close();
    }

    protected synchronized void sendEvent(XMLEvent event) throws XMLStreamException {
        if (this.savedStart != null) {
            StartElement start = this.savedStart;
            this.savedStart = null;
            if (event.getEventType() == 2) {
                XMLWriterUtils.writeStartElement(start, true, this.writer);
                return;
            }
            XMLWriterUtils.writeStartElement(start, false, this.writer);
        }
        if (event.isStartElement()) {
            this.savedStart = event.asStartElement();
        } else if (event instanceof ExtendedXMLEvent) {
            ((ExtendedXMLEvent)event).writeEvent(this.writer);
        } else {
            XMLWriterUtils.writeEvent(event, this.writer);
        }
    }
}

