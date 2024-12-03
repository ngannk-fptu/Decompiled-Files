/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.util.stax.dialect.SJSXPNamespaceContextWrapper;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class SJSXPStreamReaderWrapper
extends XMLStreamReaderWrapper
implements DelegatingXMLStreamReader {
    public SJSXPStreamReaderWrapper(XMLStreamReader parent) {
        super(parent);
    }

    public String getCharacterEncodingScheme() {
        if (this.getEventType() == 7) {
            return super.getCharacterEncodingScheme();
        }
        throw new IllegalStateException();
    }

    public String getEncoding() {
        if (this.getEventType() == 7) {
            return super.getEncoding();
        }
        throw new IllegalStateException();
    }

    public String getVersion() {
        if (this.getEventType() == 7) {
            return super.getVersion();
        }
        throw new IllegalStateException();
    }

    public boolean isStandalone() {
        if (this.getEventType() == 7) {
            return super.isStandalone();
        }
        throw new IllegalStateException();
    }

    public boolean standaloneSet() {
        if (this.getEventType() == 7) {
            return super.standaloneSet();
        }
        throw new IllegalStateException();
    }

    public String getLocalName() {
        int event = super.getEventType();
        if (event == 1 || event == 2 || event == 9) {
            return super.getLocalName();
        }
        throw new IllegalStateException();
    }

    public String getPrefix() {
        int event = super.getEventType();
        if (event == 1 || event == 2) {
            String result = super.getPrefix();
            return result == null || result.length() == 0 ? null : result;
        }
        throw new IllegalStateException();
    }

    public String getNamespaceURI() {
        int event = this.getEventType();
        if (event == 1 || event == 2) {
            return super.getNamespaceURI();
        }
        throw new IllegalStateException();
    }

    public QName getName() {
        try {
            return super.getName();
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalStateException();
        }
    }

    public boolean hasName() {
        int event = super.getEventType();
        return event == 1 || event == 2;
    }

    public boolean hasText() {
        return super.hasText() || super.getEventType() == 6;
    }

    public boolean isWhiteSpace() {
        return super.isWhiteSpace() || super.getEventType() == 6;
    }

    public int next() throws XMLStreamException {
        if (this.hasNext()) {
            return super.next();
        }
        throw new IllegalStateException();
    }

    public NamespaceContext getNamespaceContext() {
        return new SJSXPNamespaceContextWrapper(super.getNamespaceContext());
    }

    public XMLStreamReader getParent() {
        return super.getParent();
    }
}

