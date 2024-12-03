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
    public EndDocumentEventImpl(Location location) {
        super(location);
    }

    public int getEventType() {
        return 8;
    }

    public boolean isEndDocument() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        xMLStreamWriter2.writeEndDocument();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        return object instanceof EndDocument;
    }

    public int hashCode() {
        return 8;
    }
}

