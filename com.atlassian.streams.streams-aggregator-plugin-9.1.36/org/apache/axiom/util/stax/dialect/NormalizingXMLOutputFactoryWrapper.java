/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.dialect.AbstractStAXDialect;
import org.apache.axiom.util.stax.wrapper.WrappingXMLOutputFactory;

class NormalizingXMLOutputFactoryWrapper
extends WrappingXMLOutputFactory {
    private final AbstractStAXDialect dialect;

    public NormalizingXMLOutputFactoryWrapper(XMLOutputFactory parent, AbstractStAXDialect dialect) {
        super(parent);
        this.dialect = dialect;
    }

    protected XMLStreamWriter wrap(XMLStreamWriter writer) {
        return this.dialect.normalize(writer);
    }
}

