/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream.events;

import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

public interface XMLEvent
extends XMLStreamConstants {
    public Characters asCharacters();

    public EndElement asEndElement();

    public StartElement asStartElement();

    public int getEventType();

    public Location getLocation();

    public QName getSchemaType();

    public boolean isAttribute();

    public boolean isCharacters();

    public boolean isEndDocument();

    public boolean isEndElement();

    public boolean isEntityReference();

    public boolean isNamespace();

    public boolean isProcessingInstruction();

    public boolean isStartDocument();

    public boolean isStartElement();

    public void writeAsEncodedUnicode(Writer var1) throws XMLStreamException;
}

