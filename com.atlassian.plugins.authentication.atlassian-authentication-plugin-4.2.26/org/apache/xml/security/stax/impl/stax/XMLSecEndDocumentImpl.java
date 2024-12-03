/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecEndDocument;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;

public class XMLSecEndDocumentImpl
extends XMLSecEventBaseImpl
implements XMLSecEndDocument {
    @Override
    public int getEventType() {
        return 8;
    }

    @Override
    public boolean isEndDocument() {
        return true;
    }

    @Override
    public XMLSecEndDocument asEndEndDocument() {
        return this;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
    }
}

