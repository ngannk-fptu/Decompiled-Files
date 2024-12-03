/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.inlinetask;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem;
import com.atlassian.confluence.content.render.xhtml.view.inlinetask.ViewInlineTaskConstants;
import com.atlassian.confluence.core.ContentEntityObject;
import com.google.common.base.MoreObjects;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class ViewInlineTaskMarshaller
implements Marshaller<InlineTaskList> {
    private final XMLOutputFactory xmlOutputFactory;

    public ViewInlineTaskMarshaller(XMLOutputFactory xmlOutputFactory, MarshallingRegistry registry) {
        this.xmlOutputFactory = xmlOutputFactory;
        registry.register(this, InlineTaskList.class, MarshallingType.VIEW);
        registry.register(this, InlineTaskList.class, MarshallingType.EDITOR);
    }

    @Override
    public Streamable marshal(InlineTaskList inlineTaskList, ConversionContext conversionContext) throws XhtmlException {
        ContentEntityObject entity = conversionContext.getEntity();
        String contentId = entity != null ? Long.toString(entity.getId()) : "";
        return out -> {
            try {
                XMLStreamWriter xmlStreamWriter = this.xmlOutputFactory.createXMLStreamWriter(out);
                xmlStreamWriter.writeStartElement(ViewInlineTaskConstants.TASK_LIST_TAG_NAME);
                xmlStreamWriter.writeAttribute("class", this.getIdentifyingCssClass());
                this.writeContentId(xmlStreamWriter, contentId);
                for (InlineTaskListItem listItem : inlineTaskList.getItems()) {
                    xmlStreamWriter.writeStartElement(ViewInlineTaskConstants.TASK_TAG_NAME);
                    this.writeStatusAttribute(xmlStreamWriter, listItem);
                    String id = listItem.getId();
                    if (ViewInlineTaskConstants.EMPTY_LIST_ITEM_ID.equals(id)) {
                        xmlStreamWriter.writeAttribute("style", ViewInlineTaskConstants.EMPTY_TASK_BODY_STYLE_ATTRIBUTE);
                    } else {
                        xmlStreamWriter.writeAttribute(ViewInlineTaskConstants.TASK_ID_DATA_ATTRIBUTE, (String)MoreObjects.firstNonNull((Object)id, (Object)""));
                    }
                    xmlStreamWriter.writeCharacters("");
                    this.writeInlineTaskImage(xmlStreamWriter, listItem);
                    xmlStreamWriter.flush();
                    if (StringUtils.isBlank((CharSequence)listItem.getBody().trim())) {
                        out.write("<span>&nbsp;</span>");
                    } else {
                        out.write(listItem.getBody());
                    }
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

    protected void writeContentId(XMLStreamWriter xmlStreamWriter, String contentId) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(ViewInlineTaskConstants.TASK_LIST_CONTENT_ID_DATA_ATTRIBUTE, contentId);
    }

    protected String getIdentifyingCssClass() {
        return ViewInlineTaskConstants.TASK_LIST_IDENTIFYING_CSS_CLASS;
    }

    protected void writeStatusAttribute(XMLStreamWriter xmlStreamWriter, InlineTaskListItem listItem) throws XMLStreamException {
        if (listItem.isCompleted()) {
            xmlStreamWriter.writeAttribute("class", ViewInlineTaskConstants.COMPLETED_TASK_CSS_CLASS);
        }
    }

    protected void writeInlineTaskImage(XMLStreamWriter xmlStreamWriter, InlineTaskListItem listItem) throws XMLStreamException {
        xmlStreamWriter.writeCharacters("");
    }
}

