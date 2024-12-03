/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import org.apache.axiom.util.stax.dialect.BEAStreamReaderWrapper;
import org.apache.axiom.util.stax.dialect.EncodingDetectionHelper;
import org.apache.axiom.util.stax.wrapper.XMLInputFactoryWrapper;

class BEAInputFactoryWrapper
extends XMLInputFactoryWrapper {
    public BEAInputFactoryWrapper(XMLInputFactory parent) {
        super(parent);
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
        return this.createXMLStreamReader(null, stream);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream) throws XMLStreamException {
        EncodingDetectionHelper helper = new EncodingDetectionHelper(stream);
        stream = helper.getInputStream();
        String encoding = helper.detectEncoding();
        XMLStreamReader reader = systemId == null ? super.createXMLStreamReader(stream) : super.createXMLStreamReader(systemId, stream);
        return new BEAStreamReaderWrapper(reader, encoding);
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding) throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(stream, encoding), null);
    }

    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(reader), null);
    }

    public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(source), null);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(systemId, reader), null);
    }
}

