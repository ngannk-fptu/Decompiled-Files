/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class NamespaceContextCorrectingXMLStreamReaderWrapper
extends XMLStreamReaderWrapper
implements DelegatingXMLStreamReader {
    private final ScopedNamespaceContext namespaceContext = new ScopedNamespaceContext();

    public NamespaceContextCorrectingXMLStreamReaderWrapper(XMLStreamReader parent) {
        super(parent);
    }

    private void startElement() {
        this.namespaceContext.startScope();
        int c = this.getNamespaceCount();
        for (int i = 0; i < c; ++i) {
            String prefix = this.getNamespacePrefix(i);
            this.namespaceContext.setPrefix(prefix == null ? "" : prefix, this.getNamespaceURI(i));
        }
    }

    public int next() throws XMLStreamException {
        int event;
        if (this.isEndElement()) {
            this.namespaceContext.endScope();
        }
        if ((event = super.next()) == 1) {
            this.startElement();
        }
        return event;
    }

    public int nextTag() throws XMLStreamException {
        int event;
        if (this.isEndElement()) {
            this.namespaceContext.endScope();
        }
        if ((event = super.nextTag()) == 1) {
            this.startElement();
        }
        return event;
    }

    public NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }

    public String getNamespaceURI(String prefix) {
        return this.namespaceContext.getNamespaceURI(prefix);
    }

    public XMLStreamReader getParent() {
        return super.getParent();
    }
}

