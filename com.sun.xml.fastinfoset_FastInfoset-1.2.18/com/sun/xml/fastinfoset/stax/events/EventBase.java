/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public abstract class EventBase
implements XMLEvent {
    protected int _eventType;
    protected Location _location = null;

    public EventBase() {
    }

    public EventBase(int eventType) {
        this._eventType = eventType;
    }

    @Override
    public int getEventType() {
        return this._eventType;
    }

    protected void setEventType(int eventType) {
        this._eventType = eventType;
    }

    @Override
    public boolean isStartElement() {
        return this._eventType == 1;
    }

    @Override
    public boolean isEndElement() {
        return this._eventType == 2;
    }

    @Override
    public boolean isEntityReference() {
        return this._eventType == 9;
    }

    @Override
    public boolean isProcessingInstruction() {
        return this._eventType == 3;
    }

    @Override
    public boolean isStartDocument() {
        return this._eventType == 7;
    }

    @Override
    public boolean isEndDocument() {
        return this._eventType == 8;
    }

    @Override
    public Location getLocation() {
        return this._location;
    }

    public void setLocation(Location loc) {
        this._location = loc;
    }

    public String getSystemId() {
        if (this._location == null) {
            return "";
        }
        return this._location.getSystemId();
    }

    @Override
    public Characters asCharacters() {
        if (this.isCharacters()) {
            return (Characters)((Object)this);
        }
        throw new ClassCastException(CommonResourceBundle.getInstance().getString("message.charactersCast", new Object[]{this.getEventTypeString()}));
    }

    @Override
    public EndElement asEndElement() {
        if (this.isEndElement()) {
            return (EndElement)((Object)this);
        }
        throw new ClassCastException(CommonResourceBundle.getInstance().getString("message.endElementCase", new Object[]{this.getEventTypeString()}));
    }

    @Override
    public StartElement asStartElement() {
        if (this.isStartElement()) {
            return (StartElement)((Object)this);
        }
        throw new ClassCastException(CommonResourceBundle.getInstance().getString("message.startElementCase", new Object[]{this.getEventTypeString()}));
    }

    @Override
    public QName getSchemaType() {
        return null;
    }

    @Override
    public boolean isAttribute() {
        return this._eventType == 10;
    }

    @Override
    public boolean isCharacters() {
        return this._eventType == 4;
    }

    @Override
    public boolean isNamespace() {
        return this._eventType == 13;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
    }

    private String getEventTypeString() {
        switch (this._eventType) {
            case 1: {
                return "StartElementEvent";
            }
            case 2: {
                return "EndElementEvent";
            }
            case 3: {
                return "ProcessingInstructionEvent";
            }
            case 4: {
                return "CharacterEvent";
            }
            case 5: {
                return "CommentEvent";
            }
            case 7: {
                return "StartDocumentEvent";
            }
            case 8: {
                return "EndDocumentEvent";
            }
            case 9: {
                return "EntityReferenceEvent";
            }
            case 10: {
                return "AttributeBase";
            }
            case 11: {
                return "DTDEvent";
            }
            case 12: {
                return "CDATA";
            }
        }
        return "UNKNOWN_EVENT_TYPE";
    }
}

