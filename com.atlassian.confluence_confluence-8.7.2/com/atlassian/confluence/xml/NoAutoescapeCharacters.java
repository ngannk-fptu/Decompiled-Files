/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xml;

import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

public class NoAutoescapeCharacters
implements Characters {
    private final String data;

    public NoAutoescapeCharacters(String data) {
        this.data = data;
    }

    @Override
    public String getData() {
        return this.data;
    }

    @Override
    public boolean isWhiteSpace() {
        return false;
    }

    @Override
    public boolean isCData() {
        return false;
    }

    @Override
    public boolean isIgnorableWhiteSpace() {
        return false;
    }

    @Override
    public int getEventType() {
        return 4;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean isStartElement() {
        return false;
    }

    @Override
    public boolean isAttribute() {
        return false;
    }

    @Override
    public boolean isNamespace() {
        return false;
    }

    @Override
    public boolean isEndElement() {
        return false;
    }

    @Override
    public boolean isEntityReference() {
        return false;
    }

    @Override
    public boolean isProcessingInstruction() {
        return false;
    }

    @Override
    public boolean isCharacters() {
        return true;
    }

    @Override
    public boolean isStartDocument() {
        return false;
    }

    @Override
    public boolean isEndDocument() {
        return false;
    }

    @Override
    public StartElement asStartElement() {
        return null;
    }

    @Override
    public EndElement asEndElement() {
        return null;
    }

    @Override
    public Characters asCharacters() {
        return this;
    }

    @Override
    public QName getSchemaType() {
        return null;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
    }
}

