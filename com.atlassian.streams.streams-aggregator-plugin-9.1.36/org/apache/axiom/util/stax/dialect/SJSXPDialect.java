/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.dialect.AbstractStAXDialect;
import org.apache.axiom.util.stax.dialect.DisallowDoctypeDeclInputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.NormalizingXMLInputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.SJSXPOutputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.SJSXPStreamReaderWrapper;
import org.apache.axiom.util.stax.dialect.SJSXPStreamWriterWrapper;
import org.apache.axiom.util.stax.dialect.SynchronizedOutputFactoryWrapper;

class SJSXPDialect
extends AbstractStAXDialect {
    private final boolean isUnsafeStreamResult;

    public SJSXPDialect(boolean isUnsafeStreamResult) {
        this.isUnsafeStreamResult = isUnsafeStreamResult;
    }

    public String getName() {
        return this.isUnsafeStreamResult ? "SJSXP (with thread safety issue)" : "SJSXP";
    }

    public XMLInputFactory enableCDataReporting(XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
        factory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);
        return factory;
    }

    public XMLInputFactory disallowDoctypeDecl(XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.supportDTD", Boolean.TRUE);
        factory.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
        factory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
        factory.setXMLResolver(new XMLResolver(){

            public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
                throw new XMLStreamException("DOCTYPE is not allowed");
            }
        });
        return new DisallowDoctypeDeclInputFactoryWrapper(factory);
    }

    public XMLInputFactory makeThreadSafe(XMLInputFactory factory) {
        factory.setProperty("reuse-instance", Boolean.FALSE);
        return factory;
    }

    public XMLOutputFactory makeThreadSafe(XMLOutputFactory factory) {
        factory.setProperty("reuse-instance", Boolean.FALSE);
        if (this.isUnsafeStreamResult) {
            factory = new SynchronizedOutputFactoryWrapper(factory);
        }
        return factory;
    }

    public XMLStreamReader normalize(XMLStreamReader reader) {
        return new SJSXPStreamReaderWrapper(reader);
    }

    public XMLStreamWriter normalize(XMLStreamWriter writer) {
        return new SJSXPStreamWriterWrapper(writer);
    }

    public XMLInputFactory normalize(XMLInputFactory factory) {
        return new NormalizingXMLInputFactoryWrapper(factory, this);
    }

    public XMLOutputFactory normalize(XMLOutputFactory factory) {
        return new SJSXPOutputFactoryWrapper(factory, this);
    }
}

