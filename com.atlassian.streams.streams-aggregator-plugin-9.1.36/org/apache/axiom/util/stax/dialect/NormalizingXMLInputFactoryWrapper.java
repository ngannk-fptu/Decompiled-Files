/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.stax.dialect.AbstractStAXDialect;
import org.apache.axiom.util.stax.wrapper.WrappingXMLInputFactory;

class NormalizingXMLInputFactoryWrapper
extends WrappingXMLInputFactory {
    private final AbstractStAXDialect dialect;

    public NormalizingXMLInputFactoryWrapper(XMLInputFactory parent, AbstractStAXDialect dialect) {
        super(parent);
        this.dialect = dialect;
    }

    protected XMLStreamReader wrap(XMLStreamReader reader) {
        return this.dialect.normalize(reader);
    }
}

