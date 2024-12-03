/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractXMLEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.EndDocument;

public class EndDocumentEvent
extends AbstractXMLEvent
implements EndDocument {
    public EndDocumentEvent() {
    }

    public EndDocumentEvent(Location location) {
        super(location);
    }

    public EndDocumentEvent(Location location, QName schemaType) {
        super(location, schemaType);
    }

    public EndDocumentEvent(EndDocument that) {
        super(that);
    }

    public int getEventType() {
        return 8;
    }
}

