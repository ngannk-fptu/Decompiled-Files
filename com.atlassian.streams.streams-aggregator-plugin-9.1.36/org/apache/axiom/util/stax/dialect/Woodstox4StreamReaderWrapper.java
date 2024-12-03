/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.axiom.util.stax.dialect.NamespaceURICorrectingNamespaceContextWrapper;
import org.apache.axiom.util.stax.dialect.StAX2StreamReaderWrapper;
import org.codehaus.stax2.XMLStreamReader2;

class Woodstox4StreamReaderWrapper
extends StAX2StreamReaderWrapper
implements DelegatingXMLStreamReader,
CharacterDataReader {
    public Woodstox4StreamReaderWrapper(XMLStreamReader reader) {
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

    public String getPrefix() {
        String prefix = super.getPrefix();
        return prefix == null || prefix.length() == 0 ? null : prefix;
    }

    public String getNamespaceURI() {
        String uri = super.getNamespaceURI();
        return uri == null || uri.length() == 0 ? null : uri;
    }

    public String getNamespaceURI(String prefix) {
        String uri = super.getNamespaceURI(prefix);
        return uri == null || uri.length() == 0 ? null : uri;
    }

    public String getNamespacePrefix(int index) {
        String prefix = super.getNamespacePrefix(index);
        return prefix == null || prefix.length() == 0 ? null : prefix;
    }

    public String getAttributeNamespace(int index) {
        String uri = super.getAttributeNamespace(index);
        return uri == null || uri.length() == 0 ? null : uri;
    }

    public NamespaceContext getNamespaceContext() {
        return new NamespaceURICorrectingNamespaceContextWrapper(super.getNamespaceContext());
    }

    public XMLStreamReader getParent() {
        return super.getParent();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (CharacterDataReader.PROPERTY.equals(name)) {
            return this;
        }
        return super.getProperty(name);
    }

    public void writeTextTo(Writer writer) throws XMLStreamException, IOException {
        ((XMLStreamReader2)XMLStreamReaderUtils.getOriginalXMLStreamReader(this)).getText(writer, false);
    }
}

