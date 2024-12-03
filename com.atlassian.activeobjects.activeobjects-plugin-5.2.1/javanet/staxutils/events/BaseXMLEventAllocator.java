/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;

public abstract class BaseXMLEventAllocator
implements XMLEventAllocator {
    public void allocate(XMLStreamReader reader, XMLEventConsumer consumer) throws XMLStreamException {
        consumer.add(this.allocate(reader));
    }

    public XMLEvent allocate(XMLStreamReader reader) throws XMLStreamException {
        int eventType = reader.getEventType();
        switch (eventType) {
            case 1: {
                return this.allocateStartElement(reader);
            }
            case 2: {
                return this.allocateEndElement(reader);
            }
            case 4: {
                return this.allocateCharacters(reader);
            }
            case 12: {
                return this.allocateCData(reader);
            }
            case 6: {
                return this.allocateIgnorableSpace(reader);
            }
            case 5: {
                return this.allocateComment(reader);
            }
            case 11: {
                return this.allocateDTD(reader);
            }
            case 9: {
                return this.allocateEntityReference(reader);
            }
            case 3: {
                return this.allocateProcessingInstruction(reader);
            }
            case 7: {
                return this.allocateStartDocument(reader);
            }
            case 8: {
                return this.allocateEndDocument(reader);
            }
        }
        throw new XMLStreamException("Unexpected reader state: " + eventType);
    }

    public abstract StartElement allocateStartElement(XMLStreamReader var1) throws XMLStreamException;

    public abstract EndElement allocateEndElement(XMLStreamReader var1) throws XMLStreamException;

    public abstract Characters allocateCharacters(XMLStreamReader var1) throws XMLStreamException;

    public abstract Characters allocateCData(XMLStreamReader var1) throws XMLStreamException;

    public abstract Characters allocateIgnorableSpace(XMLStreamReader var1) throws XMLStreamException;

    public abstract EntityReference allocateEntityReference(XMLStreamReader var1) throws XMLStreamException;

    public abstract Comment allocateComment(XMLStreamReader var1) throws XMLStreamException;

    public abstract DTD allocateDTD(XMLStreamReader var1) throws XMLStreamException;

    public abstract StartDocument allocateStartDocument(XMLStreamReader var1) throws XMLStreamException;

    public abstract EndDocument allocateEndDocument(XMLStreamReader var1) throws XMLStreamException;

    public abstract ProcessingInstruction allocateProcessingInstruction(XMLStreamReader var1) throws XMLStreamException;

    public abstract NamespaceContext createStableNamespaceContext(XMLStreamReader var1);

    public abstract Location createStableLocation(XMLStreamReader var1);
}

