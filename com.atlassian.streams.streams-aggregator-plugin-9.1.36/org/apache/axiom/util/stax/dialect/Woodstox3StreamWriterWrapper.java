/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.wrapper.XMLStreamWriterWrapper;

class Woodstox3StreamWriterWrapper
extends XMLStreamWriterWrapper {
    public Woodstox3StreamWriterWrapper(XMLStreamWriter parent) {
        super(parent);
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        if (encoding == null) {
            throw new IllegalArgumentException();
        }
        super.writeStartDocument(encoding, version);
    }
}

