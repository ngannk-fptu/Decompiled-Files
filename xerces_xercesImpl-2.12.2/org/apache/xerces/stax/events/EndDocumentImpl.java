/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax.events;

import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndDocument;
import org.apache.xerces.stax.events.XMLEventImpl;

public final class EndDocumentImpl
extends XMLEventImpl
implements EndDocument {
    public EndDocumentImpl(Location location) {
        super(8, location);
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
    }
}

