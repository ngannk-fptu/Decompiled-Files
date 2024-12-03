/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.datetime.DateFormatService
 */
package com.atlassian.confluence.content.render.xhtml.view.time;

import com.atlassian.confluence.api.service.datetime.DateFormatService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.time.Time;
import com.atlassian.confluence.content.render.xhtml.storage.time.StorageTimeConstants;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ViewTimeMarshaller
implements Marshaller<Time> {
    private final XMLOutputFactory xmlOutputFactory;
    private final DateFormatService dateFormatService;

    public ViewTimeMarshaller(XMLOutputFactory xmlOutputFactory, DateFormatService dateFormatService) {
        this.xmlOutputFactory = xmlOutputFactory;
        this.dateFormatService = dateFormatService;
    }

    @Override
    public Streamable marshal(Time time, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                XMLStreamWriter xmlStreamWriter = this.xmlOutputFactory.createXMLStreamWriter(out);
                xmlStreamWriter.writeStartElement(StorageTimeConstants.TIME_ELEMENT.getPrefix(), StorageTimeConstants.TIME_ELEMENT.getLocalPart(), StorageTimeConstants.TIME_ELEMENT.getNamespaceURI());
                xmlStreamWriter.writeAttribute("", "http://atlassian.com/content", StorageTimeConstants.DATETIME_ATTRIBUTE_NAME, time.getDatetimeString());
                if (time.getCssClasses() != null) {
                    xmlStreamWriter.writeAttribute("class", time.getCssClasses());
                }
                xmlStreamWriter.writeCharacters(this.dateFormatService.getFormattedDateByUserLocale(time.getLocalDate()));
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.flush();
                StaxUtils.closeQuietly(xmlStreamWriter);
            }
            catch (XMLStreamException e) {
                throw new IOException("An error occurred while marshalling a time into html in view and editor mode.", e);
            }
        };
    }
}

