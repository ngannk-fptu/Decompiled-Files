/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.dialect.AbstractStAXDialect;
import org.apache.axiom.util.stax.dialect.NormalizingXMLInputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.StAXDialectUtils;
import org.apache.axiom.util.stax.dialect.Woodstox3OutputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.Woodstox3StreamReaderWrapper;
import org.apache.axiom.util.stax.dialect.Woodstox3StreamWriterWrapper;

class Woodstox3Dialect
extends AbstractStAXDialect {
    public static final Woodstox3Dialect INSTANCE = new Woodstox3Dialect();

    Woodstox3Dialect() {
    }

    public String getName() {
        return "Woodstox 3.x";
    }

    public XMLInputFactory enableCDataReporting(XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
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
        return new Woodstox3StreamReaderWrapper(reader);
    }

    public XMLStreamWriter normalize(XMLStreamWriter writer) {
        return new Woodstox3StreamWriterWrapper(writer);
    }

    public XMLInputFactory normalize(XMLInputFactory factory) {
        return new NormalizingXMLInputFactoryWrapper(factory, this);
    }

    public XMLOutputFactory normalize(XMLOutputFactory factory) {
        return new Woodstox3OutputFactoryWrapper(factory, this);
    }
}

