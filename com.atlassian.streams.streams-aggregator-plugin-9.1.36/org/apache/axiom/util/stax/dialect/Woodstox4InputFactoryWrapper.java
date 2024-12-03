/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.stax.dialect.AbstractStAXDialect;
import org.apache.axiom.util.stax.dialect.CloseShieldInputStream;
import org.apache.axiom.util.stax.dialect.CloseShieldReader;
import org.apache.axiom.util.stax.dialect.NormalizingXMLInputFactoryWrapper;

public class Woodstox4InputFactoryWrapper
extends NormalizingXMLInputFactoryWrapper {
    private final boolean wstx276;

    public Woodstox4InputFactoryWrapper(XMLInputFactory parent, AbstractStAXDialect dialect, boolean wstx276) {
        super(parent, dialect);
        this.wstx276 = wstx276;
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding) throws XMLStreamException {
        return super.createXMLStreamReader(this.wstx276 ? new CloseShieldInputStream(stream) : stream, encoding);
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
        return super.createXMLStreamReader(this.wstx276 ? new CloseShieldInputStream(stream) : stream);
    }

    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return super.createXMLStreamReader(this.wstx276 ? new CloseShieldReader(reader) : reader);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream) throws XMLStreamException {
        return super.createXMLStreamReader(systemId, this.wstx276 ? new CloseShieldInputStream(stream) : stream);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
        return super.createXMLStreamReader(systemId, this.wstx276 ? new CloseShieldReader(reader) : reader);
    }
}

