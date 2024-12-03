/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.dialect.AbstractStAXDialect;
import org.apache.axiom.util.stax.dialect.NamespaceContextCorrectingXMLStreamWriterWrapper;
import org.apache.axiom.util.stax.dialect.NormalizingXMLOutputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.StAXDialectUtils;
import org.apache.axiom.util.stax.dialect.XLXP1StreamReaderWrapper;
import org.apache.axiom.util.stax.dialect.XLXPInputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.XLXPStreamWriterWrapper;

class XLXP1Dialect
extends AbstractStAXDialect {
    private final boolean isSetPrefixBroken;

    public XLXP1Dialect(boolean isSetPrefixBroken) {
        this.isSetPrefixBroken = isSetPrefixBroken;
    }

    public String getName() {
        return this.isSetPrefixBroken ? "XL XP-J (StAX non-compliant versions)" : "XL XP-J (StAX compliant versions)";
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
        return new XLXP1StreamReaderWrapper(reader);
    }

    public XMLStreamWriter normalize(XMLStreamWriter writer) {
        XMLStreamWriter wrapper = new XLXPStreamWriterWrapper(writer);
        if (this.isSetPrefixBroken) {
            wrapper = new NamespaceContextCorrectingXMLStreamWriterWrapper(wrapper);
        }
        return wrapper;
    }

    public XMLInputFactory normalize(XMLInputFactory factory) {
        return new XLXPInputFactoryWrapper(factory, this);
    }

    public XMLOutputFactory normalize(XMLOutputFactory factory) {
        return new NormalizingXMLOutputFactoryWrapper(factory, this);
    }
}

