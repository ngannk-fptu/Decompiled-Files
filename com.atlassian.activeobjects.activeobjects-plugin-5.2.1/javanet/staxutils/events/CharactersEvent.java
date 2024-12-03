/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractCharactersEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Characters;

public class CharactersEvent
extends AbstractCharactersEvent {
    public CharactersEvent(String data) {
        super(data);
    }

    public CharactersEvent(String data, Location location) {
        super(data, location);
    }

    public CharactersEvent(String data, Location location, QName schemaType) {
        super(data, location, schemaType);
    }

    public CharactersEvent(Characters that) {
        super(that);
    }

    public boolean isCData() {
        return false;
    }

    public boolean isIgnorableWhiteSpace() {
        return false;
    }

    public int getEventType() {
        return 4;
    }
}

