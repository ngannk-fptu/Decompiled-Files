/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.inlinetask;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem;
import com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StorageInlineTaskMarshaller
implements Marshaller<InlineTaskList> {
    private final XMLOutputFactory xmlOutputFactory;

    public StorageInlineTaskMarshaller(XMLOutputFactory xmlOutputFactory, MarshallingRegistry registry) {
        this.xmlOutputFactory = xmlOutputFactory;
        registry.register(this, InlineTaskList.class, MarshallingType.STORAGE);
    }

    @Override
    public Streamable marshal(InlineTaskList inlineTaskList, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                XMLStreamWriter xmlStreamWriter = this.xmlOutputFactory.createXMLStreamWriter(out);
                xmlStreamWriter.writeStartElement(StorageInlineTaskConstants.TASK_LIST_ELEMENT.getPrefix(), StorageInlineTaskConstants.TASK_LIST_ELEMENT.getLocalPart(), StorageInlineTaskConstants.TASK_LIST_ELEMENT.getNamespaceURI());
                xmlStreamWriter.writeCharacters("\n");
                for (InlineTaskListItem listItem : inlineTaskList.getItems()) {
                    xmlStreamWriter.writeStartElement(StorageInlineTaskConstants.TASK_ELEMENT.getPrefix(), StorageInlineTaskConstants.TASK_ELEMENT.getLocalPart(), StorageInlineTaskConstants.TASK_ELEMENT.getNamespaceURI());
                    xmlStreamWriter.writeCharacters("\n");
                    String id = listItem.getId();
                    if (id != null) {
                        xmlStreamWriter.writeStartElement(StorageInlineTaskConstants.TASK_ID_ELEMENT.getPrefix(), StorageInlineTaskConstants.TASK_ID_ELEMENT.getLocalPart(), StorageInlineTaskConstants.TASK_ID_ELEMENT.getNamespaceURI());
                        xmlStreamWriter.writeCharacters(id);
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");
                    }
                    xmlStreamWriter.writeStartElement(StorageInlineTaskConstants.TASK_STATUS_ELEMENT.getPrefix(), StorageInlineTaskConstants.TASK_STATUS_ELEMENT.getLocalPart(), StorageInlineTaskConstants.TASK_STATUS_ELEMENT.getNamespaceURI());
                    xmlStreamWriter.writeCharacters(listItem.isCompleted() ? StorageInlineTaskConstants.TASK_STATUS_COMPLETE : StorageInlineTaskConstants.TASK_STATUS_INCOMPLETE);
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                    xmlStreamWriter.writeStartElement(StorageInlineTaskConstants.TASK_BODY_ELEMENT.getPrefix(), StorageInlineTaskConstants.TASK_BODY_ELEMENT.getLocalPart(), StorageInlineTaskConstants.TASK_BODY_ELEMENT.getNamespaceURI());
                    xmlStreamWriter.writeCharacters("");
                    xmlStreamWriter.flush();
                    out.write(listItem.getBody());
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.flush();
                StaxUtils.closeQuietly(xmlStreamWriter);
            }
            catch (XMLStreamException e) {
                throw new IOException(e);
            }
        };
    }
}

