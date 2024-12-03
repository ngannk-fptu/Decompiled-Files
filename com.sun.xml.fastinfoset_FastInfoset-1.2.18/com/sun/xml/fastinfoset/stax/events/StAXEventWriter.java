/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.stax.events.Util;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
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

public class StAXEventWriter
implements XMLEventWriter {
    private XMLStreamWriter _streamWriter;

    public StAXEventWriter(XMLStreamWriter streamWriter) {
        this._streamWriter = streamWriter;
    }

    @Override
    public void flush() throws XMLStreamException {
        this._streamWriter.flush();
    }

    @Override
    public void close() throws XMLStreamException {
        this._streamWriter.close();
    }

    @Override
    public void add(XMLEventReader eventReader) throws XMLStreamException {
        if (eventReader == null) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.nullEventReader"));
        }
        while (eventReader.hasNext()) {
            this.add(eventReader.nextEvent());
        }
    }

    @Override
    public void add(XMLEvent event) throws XMLStreamException {
        int type = event.getEventType();
        switch (type) {
            case 11: {
                DTD dtd = (DTD)event;
                this._streamWriter.writeDTD(dtd.getDocumentTypeDeclaration());
                break;
            }
            case 7: {
                StartDocument startDocument = (StartDocument)event;
                this._streamWriter.writeStartDocument(startDocument.getCharacterEncodingScheme(), startDocument.getVersion());
                break;
            }
            case 1: {
                StartElement startElement = event.asStartElement();
                QName qname = startElement.getName();
                this._streamWriter.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
                Iterator<Namespace> iterator = startElement.getNamespaces();
                while (iterator.hasNext()) {
                    Namespace namespace = iterator.next();
                    this._streamWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
                }
                Iterator<Attribute> attributes = startElement.getAttributes();
                while (attributes.hasNext()) {
                    Attribute attribute = attributes.next();
                    QName name = attribute.getName();
                    this._streamWriter.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attribute.getValue());
                }
                break;
            }
            case 13: {
                Namespace namespace = (Namespace)event;
                this._streamWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
                break;
            }
            case 5: {
                Comment comment = (Comment)event;
                this._streamWriter.writeComment(comment.getText());
                break;
            }
            case 3: {
                ProcessingInstruction processingInstruction = (ProcessingInstruction)event;
                this._streamWriter.writeProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData());
                break;
            }
            case 4: {
                Characters characters = event.asCharacters();
                if (characters.isCData()) {
                    this._streamWriter.writeCData(characters.getData());
                    break;
                }
                this._streamWriter.writeCharacters(characters.getData());
                break;
            }
            case 9: {
                EntityReference entityReference = (EntityReference)event;
                this._streamWriter.writeEntityRef(entityReference.getName());
                break;
            }
            case 10: {
                Attribute attribute = (Attribute)event;
                QName qname = attribute.getName();
                this._streamWriter.writeAttribute(qname.getPrefix(), qname.getNamespaceURI(), qname.getLocalPart(), attribute.getValue());
                break;
            }
            case 12: {
                Characters characters = (Characters)event;
                if (!characters.isCData()) break;
                this._streamWriter.writeCData(characters.getData());
                break;
            }
            case 2: {
                this._streamWriter.writeEndElement();
                break;
            }
            case 8: {
                this._streamWriter.writeEndDocument();
                break;
            }
            default: {
                throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.eventTypeNotSupported", new Object[]{Util.getEventTypeString(type)}));
            }
        }
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return this._streamWriter.getPrefix(uri);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this._streamWriter.getNamespaceContext();
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this._streamWriter.setDefaultNamespace(uri);
    }

    @Override
    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        this._streamWriter.setNamespaceContext(namespaceContext);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this._streamWriter.setPrefix(prefix, uri);
    }
}

