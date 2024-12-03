/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.stax.dialect.AbstractStAXDialect;
import org.apache.axiom.util.stax.dialect.EncodingDetectionHelper;
import org.apache.axiom.util.stax.dialect.NormalizingXMLInputFactoryWrapper;

class XLXPInputFactoryWrapper
extends NormalizingXMLInputFactoryWrapper {
    public XLXPInputFactoryWrapper(XMLInputFactory parent, AbstractStAXDialect dialect) {
        super(parent, dialect);
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
        return this.createXMLStreamReader(null, stream);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream) throws XMLStreamException {
        EncodingDetectionHelper helper = new EncodingDetectionHelper(stream);
        stream = helper.getInputStream();
        String encoding = helper.detectEncoding();
        if (encoding.startsWith("UTF-16")) {
            if (systemId == null) {
                return super.createXMLStreamReader(stream, encoding);
            }
            return super.createXMLStreamReader(systemId, stream);
        }
        if (systemId == null) {
            return super.createXMLStreamReader(stream);
        }
        return super.createXMLStreamReader(systemId, stream);
    }
}

