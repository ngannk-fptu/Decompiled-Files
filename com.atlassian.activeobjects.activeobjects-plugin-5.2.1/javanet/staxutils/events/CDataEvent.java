/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractCharactersEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Characters;

public class CDataEvent
extends AbstractCharactersEvent {
    public CDataEvent(String data) {
        super(data);
    }

    public CDataEvent(String data, Location location) {
        super(data, location);
    }

    public CDataEvent(String data, Location location, QName schemaType) {
        super(data, location, schemaType);
    }

    public CDataEvent(Characters that) {
        super(that);
    }

    public boolean isCData() {
        return true;
    }

    public int getEventType() {
        return 12;
    }

    public boolean isIgnorableWhiteSpace() {
        return false;
    }
}

