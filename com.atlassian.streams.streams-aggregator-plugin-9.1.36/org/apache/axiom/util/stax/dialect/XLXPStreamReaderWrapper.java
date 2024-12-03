/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class XLXPStreamReaderWrapper
extends XMLStreamReaderWrapper
implements DelegatingXMLStreamReader {
    public XLXPStreamReaderWrapper(XMLStreamReader parent) {
        super(parent);
    }

    public boolean isCharacters() {
        return this.getEventType() == 4;
    }

    public XMLStreamReader getParent() {
        return super.getParent();
    }
}

