/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.dialect.AbstractStAXDialect;
import org.apache.axiom.util.stax.dialect.BEAInputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.BEAStreamReaderWrapper;
import org.apache.axiom.util.stax.dialect.NamespaceContextCorrectingXMLStreamWriterWrapper;
import org.apache.axiom.util.stax.dialect.NormalizingXMLOutputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.StAXDialect;
import org.apache.axiom.util.stax.dialect.StAXDialectUtils;

class BEADialect
extends AbstractStAXDialect {
    public static final StAXDialect INSTANCE = new BEADialect();

    BEADialect() {
    }

    public String getName() {
        return "BEA";
    }

    public XMLInputFactory enableCDataReporting(XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
        factory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);
        return factory;
    }

    public XMLInputFactory disallowDoctypeDecl(XMLInputFactory factory) {
        return StAXDialectUtils.disallowDoctypeDecl(factory);
    }

    public XMLInputFactory makeThreadSafe(XMLInputFactory factory) {
        return factory;
    }

    public XMLOutputFactory makeThreadSafe(XMLOutputFactory factory) {
        return factory;
    }

    public XMLStreamReader normalize(XMLStreamReader reader) {
        return new BEAStreamReaderWrapper(reader, null);
    }

    public XMLStreamWriter normalize(XMLStreamWriter writer) {
        return new NamespaceContextCorrectingXMLStreamWriterWrapper(writer);
    }

    public XMLInputFactory normalize(XMLInputFactory factory) {
        return new BEAInputFactoryWrapper(factory);
    }

    public XMLOutputFactory normalize(XMLOutputFactory factory) {
        return new NormalizingXMLOutputFactoryWrapper(factory, this);
    }
}

