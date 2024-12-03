/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.diff.marshallers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem;
import com.atlassian.confluence.content.render.xhtml.view.inlinetask.ViewInlineTaskConstants;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DiffInlineTaskMarshaller
implements Marshaller<InlineTaskList> {
    private final XMLOutputFactory xmlOutputFactory;

    public DiffInlineTaskMarshaller(XMLOutputFactory xmlOutputFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public Streamable marshal(InlineTaskList inlineTaskList, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                XMLStreamWriter xmlStreamWriter = this.xmlOutputFactory.createXMLStreamWriter(out);
                xmlStreamWriter.writeStartElement("inline-task-list-ul");
                xmlStreamWriter.writeAttribute("class", "inline-task-list diff-inline-task-list");
                for (InlineTaskListItem listItem : inlineTaskList.getItems()) {
                    xmlStreamWriter.writeStartElement("inline-task-list-li");
                    String id = listItem.getId();
                    if (ViewInlineTaskConstants.EMPTY_LIST_ITEM_ID.equals(id)) {
                        xmlStreamWriter.writeAttribute("class", "empty-body");
                        xmlStreamWriter.writeCharacters("");
                    } else {
                        xmlStreamWriter.writeCharacters("");
                        this.writeInlineTaskImage(xmlStreamWriter, listItem);
                    }
                    xmlStreamWriter.flush();
                    out.write(listItem.getBody());
                    xmlStreamWriter.writeEndElement();
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.flush();
                StaxUtils.closeQuietly(xmlStreamWriter);
            }
            catch (XMLStreamException ex) {
                throw new IOException("Exception while writing inline task list for the editor", ex);
            }
        };
    }

    protected void writeInlineTaskImage(XMLStreamWriter xmlStreamWriter, InlineTaskListItem listItem) throws XMLStreamException {
        xmlStreamWriter.writeStartElement("inline-task-check-marker");
        xmlStreamWriter.writeAttribute("class", listItem.isCompleted() ? "inline-task checked" : "inline-task");
        xmlStreamWriter.writeCharacters("\u00a0");
        xmlStreamWriter.writeEndElement();
    }
}

