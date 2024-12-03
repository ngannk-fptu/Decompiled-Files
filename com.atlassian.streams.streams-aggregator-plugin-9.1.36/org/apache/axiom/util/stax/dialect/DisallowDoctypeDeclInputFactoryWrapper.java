/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.stax.dialect.DisallowDoctypeDeclStreamReaderWrapper;
import org.apache.axiom.util.stax.wrapper.WrappingXMLInputFactory;

class DisallowDoctypeDeclInputFactoryWrapper
extends WrappingXMLInputFactory {
    public DisallowDoctypeDeclInputFactoryWrapper(XMLInputFactory parent) {
        super(parent);
    }

    protected XMLStreamReader wrap(XMLStreamReader reader) {
        return new DisallowDoctypeDeclStreamReaderWrapper(reader);
    }
}

