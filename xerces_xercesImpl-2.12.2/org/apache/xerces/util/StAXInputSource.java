/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.xerces.xni.parser.XMLInputSource;

public final class StAXInputSource
extends XMLInputSource {
    private final XMLStreamReader fStreamReader;
    private final XMLEventReader fEventReader;
    private final boolean fConsumeRemainingContent;

    public StAXInputSource(XMLStreamReader xMLStreamReader) {
        this(xMLStreamReader, false);
    }

    public StAXInputSource(XMLStreamReader xMLStreamReader, boolean bl) {
        super(null, StAXInputSource.getStreamReaderSystemId(xMLStreamReader), null);
        if (xMLStreamReader == null) {
            throw new IllegalArgumentException("XMLStreamReader parameter cannot be null.");
        }
        this.fStreamReader = xMLStreamReader;
        this.fEventReader = null;
        this.fConsumeRemainingContent = bl;
    }

    public StAXInputSource(XMLEventReader xMLEventReader) {
        this(xMLEventReader, false);
    }

    public StAXInputSource(XMLEventReader xMLEventReader, boolean bl) {
        super(null, StAXInputSource.getEventReaderSystemId(xMLEventReader), null);
        if (xMLEventReader == null) {
            throw new IllegalArgumentException("XMLEventReader parameter cannot be null.");
        }
        this.fStreamReader = null;
        this.fEventReader = xMLEventReader;
        this.fConsumeRemainingContent = bl;
    }

    public XMLStreamReader getXMLStreamReader() {
        return this.fStreamReader;
    }

    public XMLEventReader getXMLEventReader() {
        return this.fEventReader;
    }

    public boolean shouldConsumeRemainingContent() {
        return this.fConsumeRemainingContent;
    }

    @Override
    public void setSystemId(String string) {
        throw new UnsupportedOperationException("Cannot set the system ID on a StAXInputSource");
    }

    private static String getStreamReaderSystemId(XMLStreamReader xMLStreamReader) {
        if (xMLStreamReader != null) {
            return xMLStreamReader.getLocation().getSystemId();
        }
        return null;
    }

    private static String getEventReaderSystemId(XMLEventReader xMLEventReader) {
        try {
            if (xMLEventReader != null) {
                return xMLEventReader.peek().getLocation().getSystemId();
            }
        }
        catch (XMLStreamException xMLStreamException) {
            // empty catch block
        }
        return null;
    }
}

