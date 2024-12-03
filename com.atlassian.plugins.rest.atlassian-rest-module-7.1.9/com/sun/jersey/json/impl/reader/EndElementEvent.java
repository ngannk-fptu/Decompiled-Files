/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.reader;

import com.sun.jersey.json.impl.reader.JsonXmlEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

public class EndElementEvent
extends JsonXmlEvent {
    public EndElementEvent(QName name, Location location) {
        super(2, location);
        this.setName(name);
    }

    public String toString() {
        return "EndElementEvent(" + this.getName() + ")";
    }
}

