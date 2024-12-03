/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.io.OutputStream;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.dialect.AbstractStAXDialect;
import org.apache.axiom.util.stax.dialect.NormalizingXMLOutputFactoryWrapper;

class Woodstox4OutputFactoryWrapper
extends NormalizingXMLOutputFactoryWrapper {
    public Woodstox4OutputFactoryWrapper(XMLOutputFactory parent, AbstractStAXDialect dialect) {
        super(parent, dialect);
    }

    public XMLEventWriter createXMLEventWriter(OutputStream stream, String encoding) throws XMLStreamException {
        if (encoding == null) {
            throw new IllegalArgumentException();
        }
        return super.createXMLEventWriter(stream, encoding);
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream stream, String encoding) throws XMLStreamException {
        if (encoding == null) {
            throw new IllegalArgumentException();
        }
        return super.createXMLStreamWriter(stream, encoding);
    }
}

