/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

abstract class EventHelper
implements XMLEvent {
    private final Location location;

    protected EventHelper(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isStartElement() {
        return false;
    }

    public boolean isAttribute() {
        return false;
    }

    public boolean isNamespace() {
        return false;
    }

    public boolean isEndElement() {
        return false;
    }

    public boolean isEntityReference() {
        return false;
    }

    public boolean isProcessingInstruction() {
        return false;
    }

    public boolean isCharacters() {
        return false;
    }

    public boolean isStartDocument() {
        return false;
    }

    public boolean isEndDocument() {
        return false;
    }

    public StartElement asStartElement() {
        throw new UnsupportedOperationException();
    }

    public EndElement asEndElement() {
        throw new UnsupportedOperationException();
    }

    public Characters asCharacters() {
        throw new UnsupportedOperationException();
    }

    public QName getSchemaType() {
        throw new UnsupportedOperationException();
    }

    public abstract void writeAsEncodedUnicode(Writer var1) throws XMLStreamException;

    public int getEventType() {
        throw new UnsupportedOperationException();
    }
}

