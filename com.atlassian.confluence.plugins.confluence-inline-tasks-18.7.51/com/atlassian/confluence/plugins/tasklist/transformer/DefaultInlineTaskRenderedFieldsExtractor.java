/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.view.inlinetask.ViewInlineTaskConstants
 *  com.atlassian.confluence.util.HTMLSearchableTextUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.core.util.HTMLUtils
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.tasklist.transformer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.view.inlinetask.ViewInlineTaskConstants;
import com.atlassian.confluence.plugins.tasklist.transformer.InlineTaskRenderedFieldsExtractor;
import com.atlassian.confluence.util.HTMLSearchableTextUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.core.util.HTMLUtils;
import com.google.common.collect.Lists;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class DefaultInlineTaskRenderedFieldsExtractor
implements InlineTaskRenderedFieldsExtractor {
    private static final Logger log = LoggerFactory.getLogger(DefaultInlineTaskRenderedFieldsExtractor.class);
    private I18NBeanFactory i18NBeanFactory;
    private XmlEventReaderFactory xmlEventReaderFactory;
    private XhtmlContent xhtmlContent;

    public DefaultInlineTaskRenderedFieldsExtractor(I18NBeanFactory i18NBeanFactory, XmlEventReaderFactory xmlEventReaderFactory, XhtmlContent xhtmlContent) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xhtmlContent = xhtmlContent;
    }

    @Override
    public String renderTaskBody(String taskBody, ConversionContext conversionContext) {
        try {
            taskBody = this.xhtmlContent.convertStorageToView(taskBody, conversionContext);
        }
        catch (XhtmlException | XMLStreamException e) {
            log.error("Unable to convert from storage to view format.", e);
        }
        return taskBody;
    }

    @Override
    public String stripTagsFromRenderedBody(String renderedTask) {
        if (renderedTask == null) {
            return null;
        }
        try {
            return HTMLSearchableTextUtil.stripTags((String)renderedTask, (String[])ViewInlineTaskConstants.TAGS_TO_IGNORE_IN_TASK_TITLE).trim();
        }
        catch (SAXException saxe) {
            log.error("Unable to strip tags from storage format.", (Throwable)saxe);
            return HTMLUtils.stripTags((String)renderedTask);
        }
    }

    @Override
    public String buildDescription(String renderedTask) {
        if (renderedTask == null) {
            return null;
        }
        try {
            StringBuilder description = new StringBuilder();
            List<String> linkURLs = this.parseTitleHtmlForLinks(renderedTask);
            if (!linkURLs.isEmpty()) {
                description.append(this.i18NBeanFactory.getI18NBean().getText("inline.task.related.links.title"));
                for (String url : linkURLs) {
                    description.append("\n").append(url);
                }
            }
            return description.toString();
        }
        catch (Exception e) {
            log.warn("Unable to extract links from the Inline Task title. The Inline Task will be created without a description.");
            return "";
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<String> parseTitleHtmlForLinks(String titleHtml) {
        XMLEventReader reader = null;
        ArrayList linkURLs = Lists.newArrayList();
        try {
            reader = this.xmlEventReaderFactory.createEditorXmlEventReader((Reader)new StringReader(titleHtml));
            while (reader.hasNext()) {
                String url;
                StartElement startElement;
                XMLEvent event = reader.nextEvent();
                if (!event.isStartElement() || !"a".equals((startElement = event.asStartElement()).getName().getLocalPart()) || "userinfo".equals(StaxUtils.getAttributeValue((StartElement)startElement, (String)"data-linked-resource-type")) || !StringUtils.isNotBlank((CharSequence)(url = StaxUtils.getAttributeValue((StartElement)startElement, (String)"href")))) continue;
                linkURLs.add(url);
            }
        }
        catch (XMLStreamException e) {
            try {
                log.error("Unable to extract link URLs from title html", (Throwable)e);
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(reader);
                throw throwable;
            }
            StaxUtils.closeQuietly((XMLEventReader)reader);
        }
        StaxUtils.closeQuietly((XMLEventReader)reader);
        return linkURLs;
    }
}

