/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.links.AttachmentLinksUpdater;
import com.atlassian.confluence.content.render.xhtml.links.LinksUpdateException;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierConstants;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

public class XhtmlAttachmentLinksUpdater
implements AttachmentLinksUpdater {
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XMLOutputFactory xmlOutputFactory;
    private final XMLEventFactory xmlEventFactory;
    private static final Set<QName> SUPPORTED_RESOURCES = Set.of(StorageResourceIdentifierConstants.ATTACHMENT_RESOURCE_QNAME);

    public XhtmlAttachmentLinksUpdater(XmlEventReaderFactory xmlEventReaderFactory, XMLOutputFactory xmlOutputFactory, XMLEventFactory xmlEventFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = xmlOutputFactory;
        this.xmlEventFactory = xmlEventFactory;
    }

    @Override
    public String updateLinksInContent(String content, String oldAttachmentName, String newAttachmentName) throws LinksUpdateException {
        if (StringUtils.isBlank((CharSequence)content)) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        XMLEventReader xmlEventReader = null;
        XMLEventWriter xmlEventWriter = null;
        try {
            xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(content));
            xmlEventWriter = this.xmlOutputFactory.createXMLEventWriter(stringWriter);
            while (xmlEventReader.hasNext()) {
                XMLEvent currentEvent = xmlEventReader.nextEvent();
                if (currentEvent.isStartElement() && SUPPORTED_RESOURCES.contains(currentEvent.asStartElement().getName())) {
                    StartElement startElement = currentEvent.asStartElement();
                    Attribute filename = startElement.getAttributeByName(StorageResourceIdentifierConstants.FILENAME_ATTRIBUTE_QNAME);
                    if (oldAttachmentName.equals(filename.getValue())) {
                        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
                        Iterator<Attribute> attributesIterator = startElement.getAttributes();
                        while (attributesIterator.hasNext()) {
                            Attribute attribute = attributesIterator.next();
                            if (StorageResourceIdentifierConstants.FILENAME_ATTRIBUTE_QNAME.equals(attribute.getName())) {
                                attributes.add(this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.FILENAME_ATTRIBUTE_QNAME, newAttachmentName));
                                continue;
                            }
                            attributes.add(attribute);
                        }
                        xmlEventWriter.add(this.xmlEventFactory.createStartElement(startElement.getName(), attributes.iterator(), null));
                        continue;
                    }
                    xmlEventWriter.add(currentEvent);
                    continue;
                }
                xmlEventWriter.add(currentEvent);
            }
        }
        catch (XMLStreamException e) {
            try {
                throw new RuntimeException(e);
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(xmlEventReader);
                StaxUtils.closeQuietly(xmlEventWriter);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly(xmlEventReader);
        StaxUtils.closeQuietly(xmlEventWriter);
        return stringWriter.toString();
    }
}

