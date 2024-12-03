/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import javanet.staxutils.events.ExtendedXMLEvent;
import javanet.staxutils.helpers.EventMatcher;
import javanet.staxutils.helpers.UnknownLocation;
import javanet.staxutils.io.XMLWriterUtils;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public abstract class AbstractXMLEvent
implements ExtendedXMLEvent,
Serializable,
Cloneable {
    protected Location location;
    protected QName schemaType;

    public AbstractXMLEvent() {
    }

    public AbstractXMLEvent(Location location) {
        this.location = location;
    }

    public AbstractXMLEvent(Location location, QName schemaType) {
        this.location = location;
        this.schemaType = schemaType;
    }

    public AbstractXMLEvent(XMLEvent that) {
        this.location = that.getLocation();
        this.schemaType = that.getSchemaType();
    }

    public Location getLocation() {
        return this.location == null ? UnknownLocation.INSTANCE : this.location;
    }

    public QName getSchemaType() {
        return this.schemaType;
    }

    public Characters asCharacters() {
        return (Characters)((Object)this);
    }

    public EndElement asEndElement() {
        return (EndElement)((Object)this);
    }

    public StartElement asStartElement() {
        return (StartElement)((Object)this);
    }

    public boolean isAttribute() {
        return this.getEventType() == 10;
    }

    public boolean isCharacters() {
        switch (this.getEventType()) {
            case 4: 
            case 6: 
            case 12: {
                return true;
            }
        }
        return false;
    }

    public boolean isEndDocument() {
        return this.getEventType() == 8;
    }

    public boolean isEndElement() {
        return this.getEventType() == 2;
    }

    public boolean isEntityReference() {
        return this.getEventType() == 9;
    }

    public boolean isNamespace() {
        return this.getEventType() == 13;
    }

    public boolean isProcessingInstruction() {
        return this.getEventType() == 3;
    }

    public boolean isStartDocument() {
        return this.getEventType() == 7;
    }

    public boolean isStartElement() {
        return this.getEventType() == 1;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Unexpected exception cloning XMLEvent", e);
        }
    }

    public boolean matches(XMLEvent event) {
        return EventMatcher.eventsMatch(this, event);
    }

    public void writeEvent(XMLStreamWriter writer) throws XMLStreamException {
        XMLWriterUtils.writeEvent((XMLEvent)this, writer);
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            XMLWriterUtils.writeEvent((XMLEvent)this, writer);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            this.writeAsEncodedUnicode(writer);
        }
        catch (XMLStreamException xMLStreamException) {
            // empty catch block
        }
        return writer.toString();
    }
}

