/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform.stax;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;

public class StAXSource
implements Source {
    public static final String FEATURE = "http://javax.xml.transform.stax.StAXSource/feature";
    private final XMLStreamReader xmlStreamReader;
    private final XMLEventReader xmlEventReader;
    private final String systemId;

    public StAXSource(XMLStreamReader xMLStreamReader) {
        if (xMLStreamReader == null) {
            throw new IllegalArgumentException("XMLStreamReader cannot be null.");
        }
        int n = xMLStreamReader.getEventType();
        if (n != 7 && n != 1) {
            throw new IllegalStateException("The state of the XMLStreamReader must be START_DOCUMENT or START_ELEMENT");
        }
        this.xmlStreamReader = xMLStreamReader;
        this.xmlEventReader = null;
        this.systemId = xMLStreamReader.getLocation().getSystemId();
    }

    public StAXSource(XMLEventReader xMLEventReader) throws XMLStreamException {
        if (xMLEventReader == null) {
            throw new IllegalArgumentException("XMLEventReader cannot be null.");
        }
        XMLEvent xMLEvent = xMLEventReader.peek();
        if (!xMLEvent.isStartDocument() && !xMLEvent.isStartElement()) {
            throw new IllegalStateException("The state of the XMLEventReader must be START_DOCUMENT or START_ELEMENT");
        }
        this.xmlStreamReader = null;
        this.xmlEventReader = xMLEventReader;
        this.systemId = xMLEvent.getLocation().getSystemId();
    }

    public XMLStreamReader getXMLStreamReader() {
        return this.xmlStreamReader;
    }

    public XMLEventReader getXMLEventReader() {
        return this.xmlEventReader;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public void setSystemId(String string) {
        throw new UnsupportedOperationException("Setting systemId is not supported.");
    }
}

