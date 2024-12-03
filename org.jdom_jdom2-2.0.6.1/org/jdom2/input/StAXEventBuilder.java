/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.jdom2.AttributeType;
import org.jdom2.Comment;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.JDOMFactory;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.input.stax.DTDParser;

public class StAXEventBuilder {
    private JDOMFactory factory = new DefaultJDOMFactory();

    private static final Document process(JDOMFactory factory, XMLEventReader events) throws JDOMException {
        try {
            Document document = factory.document(null);
            Element current = null;
            XMLEvent event = events.peek();
            if (7 != event.getEventType()) {
                throw new JDOMException("JDOM requires that XMLStreamReaders are at their beginning when being processed.");
            }
            while (event.getEventType() != 8) {
                if (event.isStartDocument()) {
                    document.setBaseURI(event.getLocation().getSystemId());
                    document.setProperty("ENCODING_SCHEME", ((StartDocument)event).getCharacterEncodingScheme());
                    document.setProperty("STANDALONE", String.valueOf(((StartDocument)event).isStandalone()));
                } else if (event instanceof DTD) {
                    DocType dtype = DTDParser.parse(((DTD)event).getDocumentTypeDeclaration(), factory);
                    document.setDocType(dtype);
                } else if (event.isStartElement()) {
                    Element emt = StAXEventBuilder.processElement(factory, event.asStartElement());
                    if (current == null) {
                        document.setRootElement(emt);
                        DocType dt = document.getDocType();
                        if (dt != null) {
                            dt.setElementName(emt.getName());
                        }
                    } else {
                        current.addContent(emt);
                    }
                    current = emt;
                } else if (event.isCharacters() && current != null) {
                    Characters chars = event.asCharacters();
                    if (chars.isCData()) {
                        current.addContent(factory.cdata(((Characters)event).getData()));
                    } else {
                        current.addContent(factory.text(((Characters)event).getData()));
                    }
                } else if (event instanceof javax.xml.stream.events.Comment) {
                    Comment comment = factory.comment(((javax.xml.stream.events.Comment)event).getText());
                    if (current == null) {
                        document.addContent(comment);
                    } else {
                        current.addContent(comment);
                    }
                } else if (event.isEntityReference()) {
                    current.addContent(factory.entityRef(((EntityReference)event).getName()));
                } else if (event.isProcessingInstruction()) {
                    ProcessingInstruction pi = factory.processingInstruction(((javax.xml.stream.events.ProcessingInstruction)event).getTarget(), ((javax.xml.stream.events.ProcessingInstruction)event).getData());
                    if (current == null) {
                        document.addContent(pi);
                    } else {
                        current.addContent(pi);
                    }
                } else if (event.isEndElement()) {
                    current = current.getParentElement();
                }
                if (!events.hasNext()) break;
                event = events.nextEvent();
            }
            return document;
        }
        catch (XMLStreamException xse) {
            throw new JDOMException("Unable to process XMLStream. See Cause.", xse);
        }
    }

    private static final Element processElement(JDOMFactory factory, StartElement event) {
        QName qname = event.getName();
        Element element = factory.element(qname.getLocalPart(), Namespace.getNamespace(qname.getPrefix(), qname.getNamespaceURI()));
        Iterator<Attribute> it = event.getAttributes();
        while (it.hasNext()) {
            Attribute att = it.next();
            QName aqname = att.getName();
            Namespace attNs = Namespace.getNamespace(aqname.getPrefix(), aqname.getNamespaceURI());
            factory.setAttribute(element, factory.attribute(aqname.getLocalPart(), att.getValue(), AttributeType.getAttributeType(att.getDTDType()), attNs));
        }
        it = event.getNamespaces();
        while (it.hasNext()) {
            javax.xml.stream.events.Namespace ns = (javax.xml.stream.events.Namespace)it.next();
            element.addNamespaceDeclaration(Namespace.getNamespace(ns.getPrefix(), ns.getNamespaceURI()));
        }
        return element;
    }

    public JDOMFactory getFactory() {
        return this.factory;
    }

    public void setFactory(JDOMFactory factory) {
        this.factory = factory;
    }

    public Document build(XMLEventReader events) throws JDOMException {
        return StAXEventBuilder.process(this.factory, events);
    }
}

