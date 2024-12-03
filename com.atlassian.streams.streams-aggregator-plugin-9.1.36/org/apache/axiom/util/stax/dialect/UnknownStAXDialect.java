/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import org.apache.axiom.util.stax.dialect.StAXDialect;
import org.apache.axiom.util.stax.dialect.StAXDialectUtils;

class UnknownStAXDialect
implements StAXDialect {
    public static final UnknownStAXDialect INSTANCE = new UnknownStAXDialect();

    UnknownStAXDialect() {
    }

    public String getName() {
        return "Unknown";
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

    public XMLInputFactory normalize(XMLInputFactory factory) {
        return factory;
    }

    public XMLOutputFactory normalize(XMLOutputFactory factory) {
        return factory;
    }
}

