/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.XMLEvent2;

public class Stax2EventWriterImpl
implements XMLEventWriter,
XMLStreamConstants {
    final XMLStreamWriter2 mWriter;

    public Stax2EventWriterImpl(XMLStreamWriter2 xMLStreamWriter2) {
        this.mWriter = xMLStreamWriter2;
    }

    public void add(XMLEvent xMLEvent) throws XMLStreamException {
        switch (xMLEvent.getEventType()) {
            case 10: {
                Attribute attribute = (Attribute)xMLEvent;
                QName qName = attribute.getName();
                this.mWriter.writeAttribute(qName.getPrefix(), qName.getNamespaceURI(), qName.getLocalPart(), attribute.getValue());
                break;
            }
            case 8: {
                this.mWriter.writeEndDocument();
                break;
            }
            case 2: {
                this.mWriter.writeEndElement();
                break;
            }
            case 13: {
                Namespace namespace = (Namespace)xMLEvent;
                this.mWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
                break;
            }
            case 7: {
                StartDocument startDocument = (StartDocument)xMLEvent;
                if (!startDocument.encodingSet()) {
                    this.mWriter.writeStartDocument(startDocument.getVersion());
                    break;
                }
                this.mWriter.writeStartDocument(startDocument.getCharacterEncodingScheme(), startDocument.getVersion());
                break;
            }
            case 1: {
                Attribute attribute;
                StartElement startElement = xMLEvent.asStartElement();
                QName qName = startElement.getName();
                this.mWriter.writeStartElement(qName.getPrefix(), qName.getLocalPart(), qName.getNamespaceURI());
                Iterator iterator = startElement.getNamespaces();
                while (iterator.hasNext()) {
                    attribute = (Namespace)iterator.next();
                    this.add(attribute);
                }
                iterator = startElement.getAttributes();
                while (iterator.hasNext()) {
                    attribute = (Attribute)iterator.next();
                    this.add(attribute);
                }
                break;
            }
            case 4: {
                Characters characters = xMLEvent.asCharacters();
                String string = characters.getData();
                if (characters.isCData()) {
                    this.mWriter.writeCData(string);
                    break;
                }
                this.mWriter.writeCharacters(string);
                break;
            }
            case 12: {
                this.mWriter.writeCData(xMLEvent.asCharacters().getData());
                break;
            }
            case 5: {
                this.mWriter.writeComment(((Comment)xMLEvent).getText());
                break;
            }
            case 11: {
                this.mWriter.writeDTD(((DTD)xMLEvent).getDocumentTypeDeclaration());
                break;
            }
            case 9: {
                this.mWriter.writeEntityRef(((EntityReference)xMLEvent).getName());
                break;
            }
            case 3: {
                ProcessingInstruction processingInstruction = (ProcessingInstruction)xMLEvent;
                this.mWriter.writeProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData());
                break;
            }
            default: {
                if (xMLEvent instanceof XMLEvent2) {
                    ((XMLEvent2)xMLEvent).writeUsing(this.mWriter);
                    break;
                }
                throw new XMLStreamException("Don't know how to output event " + xMLEvent);
            }
        }
    }

    public void add(XMLEventReader xMLEventReader) throws XMLStreamException {
        while (xMLEventReader.hasNext()) {
            this.add(xMLEventReader.nextEvent());
        }
    }

    public void close() throws XMLStreamException {
        this.mWriter.close();
    }

    public void flush() throws XMLStreamException {
        this.mWriter.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return this.mWriter.getNamespaceContext();
    }

    public String getPrefix(String string) throws XMLStreamException {
        return this.mWriter.getPrefix(string);
    }

    public void setDefaultNamespace(String string) throws XMLStreamException {
        this.mWriter.setDefaultNamespace(string);
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        this.mWriter.setNamespaceContext(namespaceContext);
    }

    public void setPrefix(String string, String string2) throws XMLStreamException {
        this.mWriter.setPrefix(string, string2);
    }
}

