/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractCharactersEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Characters;

public class IgnorableSpaceEvent
extends AbstractCharactersEvent {
    public IgnorableSpaceEvent(String data) {
        super(data);
    }

    public IgnorableSpaceEvent(String data, Location location) {
        super(data, location);
    }

    public IgnorableSpaceEvent(String data, Location location, QName schemaType) {
        super(data, location, schemaType);
    }

    public IgnorableSpaceEvent(Characters that) {
        super(that);
    }

    public int getEventType() {
        return 6;
    }

    public boolean isCData() {
        return false;
    }

    public boolean isIgnorableWhiteSpace() {
        return true;
    }
}

