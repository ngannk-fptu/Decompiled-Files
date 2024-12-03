/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.reader;

import com.sun.jersey.json.impl.reader.JsonXmlEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

public class StartElementEvent
extends JsonXmlEvent {
    public StartElementEvent(QName name, Location location) {
        super(1, location);
        this.setName(name);
    }

    public String toString() {
        return "StartElementEvent(" + this.getName() + ")";
    }
}

