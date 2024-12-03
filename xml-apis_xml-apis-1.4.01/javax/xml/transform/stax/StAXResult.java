/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform.stax;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

public class StAXResult
implements Result {
    public static final String FEATURE = "http://javax.xml.transform.stax.StAXResult/feature";
    private final XMLStreamWriter xmlStreamWriter;
    private final XMLEventWriter xmlEventWriter;

    public StAXResult(XMLStreamWriter xMLStreamWriter) {
        if (xMLStreamWriter == null) {
            throw new IllegalArgumentException("XMLStreamWriter cannot be null.");
        }
        this.xmlStreamWriter = xMLStreamWriter;
        this.xmlEventWriter = null;
    }

    public StAXResult(XMLEventWriter xMLEventWriter) {
        if (xMLEventWriter == null) {
            throw new IllegalArgumentException("XMLEventWriter cannot be null.");
        }
        this.xmlStreamWriter = null;
        this.xmlEventWriter = xMLEventWriter;
    }

    public XMLStreamWriter getXMLStreamWriter() {
        return this.xmlStreamWriter;
    }

    public XMLEventWriter getXMLEventWriter() {
        return this.xmlEventWriter;
    }

    public String getSystemId() {
        return null;
    }

    public void setSystemId(String string) {
        throw new UnsupportedOperationException("Setting systemId is not supported.");
    }
}

