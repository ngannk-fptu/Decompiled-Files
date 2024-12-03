/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.migration.BatchTask;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.spaces.SpaceDescription;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeSpaceDescriptionsBodyTypeTask
implements BatchTask<ContentEntityObject> {
    private static final Logger log = LoggerFactory.getLogger(ChangeSpaceDescriptionsBodyTypeTask.class);
    private final HtmlToXmlConverter htmlToXmlConverter;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XMLOutputFactory xmlOutputFactory;

    public ChangeSpaceDescriptionsBodyTypeTask(HtmlToXmlConverter htmlToXmlConverter, XmlEventReaderFactory xmlEventReaderFactory) {
        this.htmlToXmlConverter = htmlToXmlConverter;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = XMLOutputFactory.newInstance();
        this.xmlOutputFactory.setProperty("com.ctc.wstx.outputValidateStructure", Boolean.FALSE);
        this.xmlOutputFactory.setProperty("com.ctc.wstx.automaticEndElements", Boolean.FALSE);
    }

    @Override
    public boolean apply(ContentEntityObject entity, int index, int batchSize) {
        Object[] loggingParams = new String[]{String.valueOf(index + 1), String.valueOf(batchSize), entity.toString()};
        log.info("({}/{}): Processing: {}", loggingParams);
        if (!(entity instanceof SpaceDescription)) {
            return false;
        }
        if (entity.getBodyContents().isEmpty() || !BodyType.XHTML.equals(entity.getBodyContent().getBodyType())) {
            return false;
        }
        BodyContent bodyContent = entity.getBodyContent();
        bodyContent.setBodyType(BodyType.WIKI);
        String fixedBody = this.resolveEntityReferences(bodyContent.getBody());
        if (fixedBody != null && !fixedBody.equals(bodyContent.getBody())) {
            bodyContent.setBody(fixedBody);
        }
        entity.setBodyContent(bodyContent);
        log.info("({}/{}): Space description fixed: {}", loggingParams);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String resolveEntityReferences(String data) {
        XMLEventReader xmlEventReader;
        String xml;
        try {
            xml = this.htmlToXmlConverter.convert(data);
        }
        catch (Exception e) {
            log.error("Error converting space description to parsable xml", (Throwable)e);
            return data;
        }
        try {
            xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(xml), true);
        }
        catch (XMLStreamException e) {
            log.error("Error creating xml event reader", (Throwable)e);
            return data;
        }
        StringWriter result = new StringWriter();
        XMLEventWriter xmlEventWriter = null;
        try {
            xmlEventWriter = this.xmlOutputFactory.createXMLEventWriter(result);
            xmlEventWriter.add(xmlEventReader);
        }
        catch (XMLStreamException e) {
            String string;
            try {
                log.error("Error creating xml event writer or writing event stream", (Throwable)e);
                string = data;
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(xmlEventWriter);
                StaxUtils.closeQuietly(xmlEventReader);
                throw throwable;
            }
            StaxUtils.closeQuietly(xmlEventWriter);
            StaxUtils.closeQuietly(xmlEventReader);
            return string;
        }
        StaxUtils.closeQuietly(xmlEventWriter);
        StaxUtils.closeQuietly(xmlEventReader);
        return result.toString();
    }
}

