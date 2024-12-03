/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.util.stax.dialect.ImplicitNamespaceContextWrapper;
import org.apache.axiom.util.stax.dialect.NamespaceURICorrectingNamespaceContextWrapper;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class BEAStreamReaderWrapper
extends XMLStreamReaderWrapper
implements DelegatingXMLStreamReader {
    private final String encodingFromStartBytes;
    private int depth;

    public BEAStreamReaderWrapper(XMLStreamReader parent, String encodingFromStartBytes) {
        super(parent);
        this.encodingFromStartBytes = encodingFromStartBytes;
    }

    public String getCharacterEncodingScheme() {
        if (this.getEventType() == 7) {
            return super.getCharacterEncodingScheme();
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

    public int next() throws XMLStreamException {
        if (!this.hasNext()) {
            throw new IllegalStateException("Already reached end of document");
        }
        int event = super.next();
        switch (event) {
            case 1: {
                ++this.depth;
                break;
            }
            case 2: {
                --this.depth;
            }
        }
        return event;
    }

    public String getEncoding() {
        if (this.getEventType() == 7) {
            String encoding = super.getEncoding();
            if (encoding != null) {
                return encoding;
            }
            if (this.encodingFromStartBytes == null) {
                return null;
            }
            encoding = this.getCharacterEncodingScheme();
            return encoding == null ? this.encodingFromStartBytes : encoding;
        }
        throw new IllegalStateException();
    }

    public String getText() {
        if (this.depth == 0) {
            String text = super.getText();
            StringBuffer buffer = null;
            int len = text.length();
            for (int i = 0; i < len; ++i) {
                char c = text.charAt(i);
                if (c == '\r' && (i == len || text.charAt(i + 1) == '\n')) {
                    if (buffer != null) continue;
                    buffer = new StringBuffer(len - 1);
                    buffer.append(text.substring(0, i));
                    continue;
                }
                if (buffer == null) continue;
                buffer.append(c);
            }
            return buffer != null ? buffer.toString() : text;
        }
        return super.getText();
    }

    public NamespaceContext getNamespaceContext() {
        return new ImplicitNamespaceContextWrapper(new NamespaceURICorrectingNamespaceContextWrapper(super.getNamespaceContext()));
    }

    public XMLStreamReader getParent() {
        return super.getParent();
    }
}

