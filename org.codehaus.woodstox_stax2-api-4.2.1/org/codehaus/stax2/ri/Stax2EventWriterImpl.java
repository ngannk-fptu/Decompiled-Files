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
    protected final XMLStreamWriter2 _writer;

    public Stax2EventWriterImpl(XMLStreamWriter2 sw) {
        this._writer = sw;
    }

    @Override
    public void add(XMLEvent event) throws XMLStreamException {
        switch (event.getEventType()) {
            case 10: {
                Attribute attr = (Attribute)event;
                QName name = attr.getName();
                this._writer.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attr.getValue());
                break;
            }
            case 8: {
                this._writer.writeEndDocument();
                break;
            }
            case 2: {
                this._writer.writeEndElement();
                break;
            }
            case 13: {
                Namespace ns = (Namespace)event;
                this._writer.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
                break;
            }
            case 7: {
                StartDocument sd = (StartDocument)event;
                if (!sd.encodingSet()) {
                    this._writer.writeStartDocument(sd.getVersion());
                    break;
                }
                if (sd.standaloneSet()) {
                    this._writer.writeStartDocument(sd.getVersion(), sd.getCharacterEncodingScheme(), sd.isStandalone());
                    break;
                }
                this._writer.writeStartDocument(sd.getCharacterEncodingScheme(), sd.getVersion());
                break;
            }
            case 1: {
                StartElement se = event.asStartElement();
                QName n = se.getName();
                this._writer.writeStartElement(n.getPrefix(), n.getLocalPart(), n.getNamespaceURI());
                Iterator<Attribute> it = se.getNamespaces();
                while (it.hasNext()) {
                    Namespace ns = it.next();
                    this.add(ns);
                }
                it = se.getAttributes();
                while (it.hasNext()) {
                    Attribute attr = it.next();
                    this.add(attr);
                }
                break;
            }
            case 4: {
                Characters ch = event.asCharacters();
                String text = ch.getData();
                if (ch.isCData()) {
                    this._writer.writeCData(text);
                    break;
                }
                this._writer.writeCharacters(text);
                break;
            }
            case 12: {
                this._writer.writeCData(event.asCharacters().getData());
                break;
            }
            case 5: {
                this._writer.writeComment(((Comment)event).getText());
                break;
            }
            case 11: {
                this._writer.writeDTD(((DTD)event).getDocumentTypeDeclaration());
                break;
            }
            case 9: {
                this._writer.writeEntityRef(((EntityReference)event).getName());
                break;
            }
            case 3: {
                ProcessingInstruction pi = (ProcessingInstruction)event;
                this._writer.writeProcessingInstruction(pi.getTarget(), pi.getData());
                break;
            }
            default: {
                if (event instanceof XMLEvent2) {
                    ((XMLEvent2)event).writeUsing(this._writer);
                    break;
                }
                throw new XMLStreamException("Don't know how to output event " + event);
            }
        }
    }

    @Override
    public void add(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            this.add(reader.nextEvent());
        }
    }

    @Override
    public void close() throws XMLStreamException {
        this._writer.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        this._writer.flush();
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this._writer.getNamespaceContext();
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return this._writer.getPrefix(uri);
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this._writer.setDefaultNamespace(uri);
    }

    @Override
    public void setNamespaceContext(NamespaceContext ctxt) throws XMLStreamException {
        this._writer.setNamespaceContext(ctxt);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this._writer.setPrefix(prefix, uri);
    }
}

