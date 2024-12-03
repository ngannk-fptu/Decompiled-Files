/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.dialect.AbstractStAXDialect;
import org.apache.axiom.util.stax.dialect.NamespaceContextCorrectingXMLStreamReaderWrapper;
import org.apache.axiom.util.stax.dialect.NormalizingXMLOutputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.SecureXMLResolver;
import org.apache.axiom.util.stax.dialect.StAXDialect;
import org.apache.axiom.util.stax.dialect.StAXDialectUtils;
import org.apache.axiom.util.stax.dialect.XLXPInputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.XLXPStreamReaderWrapper;
import org.apache.axiom.util.stax.dialect.XLXPStreamWriterWrapper;

class XLXP2Dialect
extends AbstractStAXDialect {
    public static final StAXDialect INSTANCE = new XLXP2Dialect();

    XLXP2Dialect() {
    }

    public String getName() {
        return "XLXP2";
    }

    public XMLInputFactory enableCDataReporting(XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
        return factory;
    }

    public XMLInputFactory disallowDoctypeDecl(XMLInputFactory factory) {
        factory.setXMLResolver(new SecureXMLResolver());
        return StAXDialectUtils.disallowDoctypeDecl(factory);
    }

    public XMLInputFactory makeThreadSafe(XMLInputFactory factory) {
        return factory;
    }

    public XMLOutputFactory makeThreadSafe(XMLOutputFactory factory) {
        return factory;
    }

    public XMLStreamReader normalize(XMLStreamReader reader) {
        return new NamespaceContextCorrectingXMLStreamReaderWrapper(new XLXPStreamReaderWrapper(reader));
    }

    public XMLStreamWriter normalize(XMLStreamWriter writer) {
        return new XLXPStreamWriterWrapper(writer);
    }

    public XMLInputFactory normalize(XMLInputFactory factory) {
        return new XLXPInputFactoryWrapper(factory, this);
    }

    public XMLOutputFactory normalize(XMLOutputFactory factory) {
        return new NormalizingXMLOutputFactoryWrapper(factory, this);
    }
}

