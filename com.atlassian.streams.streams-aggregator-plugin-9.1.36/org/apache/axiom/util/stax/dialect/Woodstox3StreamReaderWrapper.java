/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.util.stax.dialect.NamespaceURICorrectingNamespaceContextWrapper;
import org.apache.axiom.util.stax.dialect.StAX2StreamReaderWrapper;

class Woodstox3StreamReaderWrapper
extends StAX2StreamReaderWrapper
implements DelegatingXMLStreamReader {
    public Woodstox3StreamReaderWrapper(XMLStreamReader reader) {
        super(reader);
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

    public boolean isCharacters() {
        return this.getEventType() == 4;
    }

    public NamespaceContext getNamespaceContext() {
        return new NamespaceURICorrectingNamespaceContextWrapper(super.getNamespaceContext());
    }

    public XMLStreamReader getParent() {
        return super.getParent();
    }
}

