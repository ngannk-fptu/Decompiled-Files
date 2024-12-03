/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javanet.staxutils.BaseXMLEventWriter;
import javanet.staxutils.io.XMLWriterUtils;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StreamEventWriter
extends BaseXMLEventWriter {
    private Writer writer;
    private StartElement savedStart;

    public StreamEventWriter(File file) throws IOException {
        this(new FileWriter(file));
    }

    public StreamEventWriter(OutputStream os) {
        this(new OutputStreamWriter(os));
    }

    public StreamEventWriter(Writer writer) {
        this.writer = writer;
    }

    public synchronized void flush() throws XMLStreamException {
        super.flush();
        try {
            this.writer.flush();
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    protected void sendEvent(XMLEvent event) throws XMLStreamException {
        try {
            if (this.savedStart != null) {
                StartElement start = this.savedStart;
                this.savedStart = null;
                if (event.getEventType() == 2) {
                    XMLWriterUtils.writeStartElement(start, true, this.writer);
                    this.writer.flush();
                    return;
                }
                XMLWriterUtils.writeStartElement(start, false, this.writer);
            }
            if (event.isStartElement()) {
                this.savedStart = event.asStartElement();
            } else {
                event.writeAsEncodedUnicode(this.writer);
            }
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}

