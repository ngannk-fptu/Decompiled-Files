/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.dialect.AbstractStAXDialect;
import org.apache.axiom.util.stax.dialect.StAXDialectUtils;
import org.apache.axiom.util.stax.dialect.Woodstox4InputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.Woodstox4OutputFactoryWrapper;
import org.apache.axiom.util.stax.dialect.Woodstox4StreamReaderWrapper;
import org.apache.axiom.util.stax.dialect.Woodstox4StreamWriterWrapper;

class Woodstox4Dialect
extends AbstractStAXDialect {
    private final boolean wstx276;

    Woodstox4Dialect(boolean wstx276) {
        this.wstx276 = wstx276;
    }

    public String getName() {
        StringBuilder result = new StringBuilder("Woodstox 4.x");
        if (this.wstx276) {
            result.append(" [WSTX-276]");
        }
        return result.toString();
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
        return new Woodstox4StreamReaderWrapper(reader);
    }

    public XMLStreamWriter normalize(XMLStreamWriter writer) {
        return new Woodstox4StreamWriterWrapper(writer);
    }

    public XMLInputFactory normalize(XMLInputFactory factory) {
        factory.setProperty("org.codehaus.stax2.reportPrologWhitespace", Boolean.TRUE);
        return new Woodstox4InputFactoryWrapper(factory, this, this.wstx276);
    }

    public XMLOutputFactory normalize(XMLOutputFactory factory) {
        factory.setProperty("com.ctc.wstx.outputFixContent", Boolean.TRUE);
        return new Woodstox4OutputFactoryWrapper(factory, this);
    }
}

