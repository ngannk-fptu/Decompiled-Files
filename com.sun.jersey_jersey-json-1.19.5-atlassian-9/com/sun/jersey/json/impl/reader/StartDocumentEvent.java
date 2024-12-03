/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.reader;

import com.sun.jersey.json.impl.reader.JsonXmlEvent;
import javax.xml.stream.Location;

public class StartDocumentEvent
extends JsonXmlEvent {
    protected StartDocumentEvent(Location location) {
        super(7, location);
    }

    public String toString() {
        return "StartDocumentEvent()";
    }
}

