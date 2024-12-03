/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.reader;

import com.sun.jersey.json.impl.reader.JsonXmlEvent;
import javax.xml.stream.Location;

public class EndDocumentEvent
extends JsonXmlEvent {
    protected EndDocumentEvent(Location location) {
        super(8, location);
    }

    public String toString() {
        return "EndDocumentEvent()";
    }
}

