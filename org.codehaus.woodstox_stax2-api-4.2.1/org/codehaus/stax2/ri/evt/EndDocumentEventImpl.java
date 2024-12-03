/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndDocument;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public class EndDocumentEventImpl
extends BaseEventImpl
implements EndDocument {
    public EndDocumentEventImpl(Location loc) {
        super(loc);
    }

    @Override
    public int getEventType() {
        return 8;
    }

    @Override
    public boolean isEndDocument() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
    }

    @Override
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        w.writeEndDocument();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        return o instanceof EndDocument;
    }

    @Override
    public int hashCode() {
        return 8;
    }
}

