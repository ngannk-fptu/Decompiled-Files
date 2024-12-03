/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugin.copyspace.context.ContentRewriterContext;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.exception.CopySpaceRelinkException;
import com.atlassian.confluence.plugin.copyspace.service.ContentRewriter;
import com.atlassian.confluence.plugin.copyspace.service.LinksUpdater;
import com.atlassian.confluence.plugin.copyspace.service.impl.ExpandReferencesRewriter;
import com.atlassian.confluence.plugin.copyspace.service.impl.OldSpaceKeyRewriter;
import com.atlassian.confluence.plugin.copyspace.service.impl.SpaceReferenceReplacementRewriter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="linksUpdaterImpl")
public class LinksUpdaterImpl
implements LinksUpdater {
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XmlOutputFactory xmlOutputFactory;
    private final XMLEventFactory xmlEventFactory;

    @Autowired
    public LinksUpdaterImpl(@ComponentImport XmlEventReaderFactory xmlEventReaderFactory, @ComponentImport(value="xmlFragmentOutputFactory") XmlOutputFactory xmlFragmentOutputFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventFactory = XMLEventFactory.newInstance();
    }

    @Override
    public String rewriteLinks(String body, ContentEntityObject container, CopySpaceContext context) {
        ContentRewriterContext contentRewriterContext = new ContentRewriterContext(container, context.getTargetSpaceKey(), context.getOriginalSpaceKey());
        SpaceReferenceReplacementRewriter thirdRewriter = new SpaceReferenceReplacementRewriter(this.xmlEventFactory, context, null, contentRewriterContext);
        OldSpaceKeyRewriter secondRewriter = new OldSpaceKeyRewriter(this.xmlEventFactory, thirdRewriter, contentRewriterContext, context);
        ExpandReferencesRewriter firstRewriter = new ExpandReferencesRewriter(this.xmlEventFactory, secondRewriter, contentRewriterContext, context);
        return this.applyRewriter(body, firstRewriter);
    }

    private String applyRewriter(String content, ContentRewriter rewriter) {
        if (StringUtils.isBlank((CharSequence)content)) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        ResettableXmlEventReader xmlEventReader = null;
        XMLEventWriter xmlEventWriter = null;
        try {
            xmlEventReader = new ResettableXmlEventReader(this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader(content)));
            xmlEventWriter = this.xmlOutputFactory.createXMLEventWriter((Writer)stringWriter);
            while (xmlEventReader.hasNext()) {
                XMLEvent currentElement = xmlEventReader.peek();
                if (currentElement.isStartElement() && LinksUpdaterImpl.isAc(currentElement.asStartElement())) {
                    LinkedList<XMLEvent> linkEvents = new LinkedList<XMLEvent>();
                    while (xmlEventReader.hasNext()) {
                        XMLEvent current = xmlEventReader.nextEvent();
                        linkEvents.add(current);
                        if (!current.isEndElement() || !current.asEndElement().getName().equals(currentElement.asStartElement().getName())) continue;
                        break;
                    }
                    List<XMLEvent> updatedEvents = rewriter.updateLinkEvents(linkEvents);
                    for (XMLEvent xmlEvent : updatedEvents) {
                        xmlEventWriter.add(xmlEvent);
                    }
                    continue;
                }
                XMLEvent next = xmlEventReader.nextEvent();
                xmlEventWriter.add(next);
            }
        }
        catch (Exception e) {
            try {
                throw new CopySpaceRelinkException(e);
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(xmlEventReader);
                StaxUtils.closeQuietly(xmlEventWriter);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly((XMLEventReader)xmlEventReader);
        StaxUtils.closeQuietly((XMLEventWriter)xmlEventWriter);
        return stringWriter.toString();
    }

    private static boolean isAc(StartElement startElement) {
        QName name = startElement.getName();
        return "ac".equals(name.getPrefix());
    }
}

