/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.wrapper.XMLStreamWriterWrapper;

class SJSXPStreamWriterWrapper
extends XMLStreamWriterWrapper {
    public SJSXPStreamWriterWrapper(XMLStreamWriter parent) {
        super(parent);
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        if (encoding == null) {
            throw new IllegalArgumentException();
        }
        super.writeStartDocument(encoding, version);
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        try {
            return super.getProperty(name);
        }
        catch (NullPointerException ex) {
            throw new IllegalArgumentException();
        }
    }
}

