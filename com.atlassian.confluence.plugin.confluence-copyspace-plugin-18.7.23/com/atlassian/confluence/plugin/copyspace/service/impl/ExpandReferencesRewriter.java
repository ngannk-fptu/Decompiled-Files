/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XhtmlConstants
 *  com.atlassian.confluence.content.render.xhtml.storage.link.StorageLinkConstants
 *  com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants
 *  com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierConstants
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.storage.link.StorageLinkConstants;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierConstants;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugin.copyspace.context.ContentRewriterContext;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.ContentRewriter;
import com.atlassian.confluence.plugin.copyspace.service.impl.AbstractChainedContentRewriter;
import com.atlassian.confluence.spaces.Space;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

public class ExpandReferencesRewriter
extends AbstractChainedContentRewriter {
    public static final String SPACE_AND_PAGE_SEPARATOR = ":";
    private final XMLEventFactory xmlEventFactory;

    public ExpandReferencesRewriter(XMLEventFactory xmlEventFactory, ContentRewriter contentRewriter, ContentRewriterContext contentRewriterContext, CopySpaceContext copySpaceContext) {
        super(contentRewriter, contentRewriterContext, copySpaceContext);
        this.xmlEventFactory = xmlEventFactory;
    }

    private boolean isStartPdfMacro(XMLEvent nextEvent) {
        return nextEvent.isStartElement() && StorageMacroConstants.MACRO_V2_ELEMENT.equals(nextEvent.asStartElement().getName()) && "viewpdf".equals(nextEvent.asStartElement().getAttributeByName(StorageMacroConstants.NAME_ATTRIBUTE).getValue());
    }

    @Override
    protected List<XMLEvent> updateLinkEventsInternal(List<XMLEvent> xmlEvents) {
        ListIterator<XMLEvent> listIterator = xmlEvents.listIterator();
        while (listIterator.hasNext()) {
            XMLEvent currentEvent = listIterator.next();
            if (this.isStartPdfMacro(currentEvent)) {
                return this.handlePdfMacro(xmlEvents);
            }
            if (this.isStartPageTreeSearchMacro(currentEvent)) {
                return this.handlePageTreeSearchMacro(xmlEvents);
            }
            if (!currentEvent.isStartElement() || !currentEvent.asStartElement().getName().equals(StorageResourceIdentifierConstants.ATTACHMENT_RESOURCE_QNAME) || !xmlEvents.get(listIterator.nextIndex()).isEndElement() || this.copySpaceContext.isCopyAttachments()) continue;
            this.addResourceElement(listIterator);
        }
        return xmlEvents;
    }

    private List<XMLEvent> handlePageTreeSearchMacro(List<XMLEvent> xmlEvents) {
        if (this.copySpaceContext.isCopyPages()) {
            return xmlEvents;
        }
        for (int i = 0; i < xmlEvents.size(); ++i) {
            XMLEvent characters;
            XMLEvent nextEvent = xmlEvents.get(i);
            if (!nextEvent.isStartElement() || !nextEvent.asStartElement().getName().equals(StorageMacroConstants.MACRO_PARAMETER_ELEMENT) || !"rootPage".equals(nextEvent.asStartElement().getAttributeByName(StorageMacroConstants.NAME_ATTRIBUTE).getValue()) || !(characters = xmlEvents.get(i + 1)).isCharacters() || StringUtils.contains((CharSequence)characters.asCharacters().getData(), (CharSequence)SPACE_AND_PAGE_SEPARATOR) && Space.isValidSpaceKey((String)StringUtils.substringBefore((String)characters.asCharacters().getData(), (String)SPACE_AND_PAGE_SEPARATOR))) continue;
            xmlEvents.set(i + 1, this.xmlEventFactory.createCharacters(this.copySpaceContext.getOriginalSpaceKey() + SPACE_AND_PAGE_SEPARATOR + characters.asCharacters().getData()));
            break;
        }
        return xmlEvents;
    }

    private boolean isStartPageTreeSearchMacro(XMLEvent nextEvent) {
        return nextEvent.isStartElement() && StorageMacroConstants.MACRO_V2_ELEMENT.equals(nextEvent.asStartElement().getName()) && "pagetreesearch".equals(nextEvent.asStartElement().getAttributeByName(StorageMacroConstants.NAME_ATTRIBUTE).getValue());
    }

    private List<XMLEvent> handlePdfMacro(List<XMLEvent> xmlEvents) {
        return new PdfMacroUpdater(xmlEvents).updateEvents();
    }

    private void addResourceElement(ListIterator<XMLEvent> listIterator) {
        ContentEntityObject container = this.contentRewriterContext.getContainer();
        if (container instanceof Page) {
            StartElement pageResourceElement = this.createPageResource(container);
            listIterator.add(pageResourceElement);
            listIterator.add(this.xmlEventFactory.createEndElement(pageResourceElement.getName(), null));
        }
        if (container instanceof BlogPost) {
            StartElement blogPostResourceElement = this.createBlogPostResource(container);
            listIterator.add(blogPostResourceElement);
            listIterator.add(this.xmlEventFactory.createEndElement(blogPostResourceElement.getName(), null));
        }
    }

    private StartElement createPageResource(ContentEntityObject container) {
        List<Attribute> attributes = Arrays.asList(this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME, this.contentRewriterContext.getOriginalSpaceKey()), this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.CONTENT_TITLE_ATTRIBUTE_QNAME, container.getTitle()));
        return this.xmlEventFactory.createStartElement(StorageResourceIdentifierConstants.PAGE_RESOURCE_QNAME, attributes.iterator(), null);
    }

    private StartElement createBlogPostResource(ContentEntityObject container) {
        BlogPost blogPost = (BlogPost)container;
        String postingDate = XhtmlConstants.DATE_FORMATS.getPostingDayFormat().format(blogPost.getPostingDate());
        List<Attribute> attributes = Arrays.asList(this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME, this.contentRewriterContext.getOriginalSpaceKey()), this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.CONTENT_TITLE_ATTRIBUTE_QNAME, container.getTitle()), this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.POSTING_DAY_ATTRIBUTE_QNAME, postingDate));
        return this.xmlEventFactory.createStartElement(StorageResourceIdentifierConstants.BLOG_POST_RESOURCE_QNAME, attributes.iterator(), null);
    }

    private class PdfMacroUpdater {
        private static final String PDF_MACRO_DATE_ATTRIBUTE_VALUE = "date";
        private boolean hasAttachmentParam;
        private boolean hasSpaceParam;
        private boolean hasPageParam;
        private boolean hasBlogPostParam;
        private boolean hasDateParam;
        private final List<XMLEvent> xmlEvents;

        private PdfMacroUpdater(List<XMLEvent> xmlEvents) {
            this.xmlEvents = xmlEvents;
        }

        List<XMLEvent> updateEvents() {
            this.initParams();
            if (!ExpandReferencesRewriter.this.copySpaceContext.isCopyAttachments() && this.hasAttachmentParam) {
                ListIterator<XMLEvent> xmlEventListIterator = this.getInMacroIterator();
                if (!this.hasSpaceParam) {
                    this.addSpaceParam(xmlEventListIterator);
                    this.hasSpaceParam = true;
                }
                if (!this.hasPageParam && ExpandReferencesRewriter.this.contentRewriterContext.getContainer() instanceof Page) {
                    this.addPageParam(xmlEventListIterator);
                    this.hasPageParam = true;
                }
                if (!this.hasBlogPostParam && !this.hasPageParam && ExpandReferencesRewriter.this.contentRewriterContext.getContainer() instanceof BlogPost) {
                    this.addPageParam(xmlEventListIterator);
                    this.addBlogpostDateParam(xmlEventListIterator);
                    this.hasBlogPostParam = true;
                }
            }
            if (this.hasPageParam && !this.hasSpaceParam && !ExpandReferencesRewriter.this.copySpaceContext.isCopyPages() && !this.hasDateParam) {
                this.addSpaceParam(this.getInMacroIterator());
            }
            if (this.hasBlogPostParam && !this.hasSpaceParam && !ExpandReferencesRewriter.this.copySpaceContext.isCopyBlogPosts() && this.hasDateParam) {
                this.addSpaceParam(this.getInMacroIterator());
            }
            return this.xmlEvents;
        }

        private void initParams() {
            for (XMLEvent currentEvent : this.xmlEvents) {
                if (!currentEvent.isStartElement()) continue;
                StartElement startElement = currentEvent.asStartElement();
                QName elementName = startElement.getName();
                if (StorageMacroConstants.MACRO_PARAMETER_ELEMENT.equals(elementName)) {
                    Attribute nameAttribute = startElement.getAttributeByName(StorageMacroConstants.NAME_ATTRIBUTE);
                    if ("page".equals(nameAttribute.getValue())) {
                        this.hasPageParam = true;
                    }
                    if ("blog-post".equals(nameAttribute.getValue())) {
                        this.hasBlogPostParam = true;
                    }
                    if ("space".equals(nameAttribute.getValue())) {
                        this.hasSpaceParam = true;
                    }
                    if (PDF_MACRO_DATE_ATTRIBUTE_VALUE.equals(nameAttribute.getValue())) {
                        this.hasDateParam = true;
                    }
                }
                if (!StorageResourceIdentifierConstants.ATTACHMENT_RESOURCE_QNAME.equals(elementName)) continue;
                this.hasAttachmentParam = true;
            }
        }

        private ListIterator<XMLEvent> getInMacroIterator() {
            XMLEvent currentEvent;
            ListIterator<XMLEvent> xmlEventListIterator = this.xmlEvents.listIterator();
            while (!(!xmlEventListIterator.hasNext() || (currentEvent = xmlEventListIterator.next()).isStartElement() && currentEvent.asStartElement().getName().equals(StorageMacroConstants.MACRO_V2_ELEMENT))) {
            }
            return xmlEventListIterator;
        }

        private void addPageParam(ListIterator<XMLEvent> xmlEventListIterator) {
            List<Attribute> attributes = Collections.singletonList(ExpandReferencesRewriter.this.xmlEventFactory.createAttribute(StorageMacroConstants.NAME_ATTRIBUTE, "page"));
            StartElement startParam = ExpandReferencesRewriter.this.xmlEventFactory.createStartElement(StorageMacroConstants.MACRO_PARAMETER_ELEMENT, attributes.iterator(), null);
            StartElement startLink = ExpandReferencesRewriter.this.xmlEventFactory.createStartElement(StorageLinkConstants.LINK_ELEMENT, null, null);
            List<Attribute> pageAttributes = Collections.singletonList(ExpandReferencesRewriter.this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.CONTENT_TITLE_ATTRIBUTE_QNAME, ExpandReferencesRewriter.this.contentRewriterContext.getContainer().getTitle()));
            StartElement startPage = ExpandReferencesRewriter.this.xmlEventFactory.createStartElement(StorageResourceIdentifierConstants.PAGE_RESOURCE_QNAME, pageAttributes.iterator(), null);
            EndElement endPage = ExpandReferencesRewriter.this.xmlEventFactory.createEndElement(StorageResourceIdentifierConstants.PAGE_RESOURCE_QNAME, null);
            EndElement endLink = ExpandReferencesRewriter.this.xmlEventFactory.createEndElement(StorageLinkConstants.LINK_ELEMENT, null);
            EndElement endParam = ExpandReferencesRewriter.this.xmlEventFactory.createEndElement(StorageMacroConstants.MACRO_PARAMETER_ELEMENT, null);
            xmlEventListIterator.add(startParam);
            xmlEventListIterator.add(startLink);
            xmlEventListIterator.add(startPage);
            xmlEventListIterator.add(endPage);
            xmlEventListIterator.add(endLink);
            xmlEventListIterator.add(endParam);
        }

        private void addSpaceParam(ListIterator<XMLEvent> xmlEventListIterator) {
            List<Attribute> attributes = Collections.singletonList(ExpandReferencesRewriter.this.xmlEventFactory.createAttribute(StorageMacroConstants.NAME_ATTRIBUTE, "space"));
            StartElement startParam = ExpandReferencesRewriter.this.xmlEventFactory.createStartElement(StorageMacroConstants.MACRO_PARAMETER_ELEMENT, attributes.iterator(), null);
            Characters characters = ExpandReferencesRewriter.this.xmlEventFactory.createCharacters(ExpandReferencesRewriter.this.copySpaceContext.getOriginalSpaceKey());
            EndElement endParam = ExpandReferencesRewriter.this.xmlEventFactory.createEndElement(StorageMacroConstants.MACRO_PARAMETER_ELEMENT, null);
            xmlEventListIterator.add(startParam);
            xmlEventListIterator.add(characters);
            xmlEventListIterator.add(endParam);
        }

        private void addBlogpostDateParam(ListIterator<XMLEvent> xmlEventListIterator) {
            BlogPost blogPost = (BlogPost)ExpandReferencesRewriter.this.contentRewriterContext.getContainer();
            SimpleDateFormat macroDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String postingDate = macroDateFormat.format(blogPost.getPostingDate());
            List<Attribute> attributes = Collections.singletonList(ExpandReferencesRewriter.this.xmlEventFactory.createAttribute(StorageMacroConstants.NAME_ATTRIBUTE, PDF_MACRO_DATE_ATTRIBUTE_VALUE));
            StartElement startParam = ExpandReferencesRewriter.this.xmlEventFactory.createStartElement(StorageMacroConstants.MACRO_PARAMETER_ELEMENT, attributes.iterator(), null);
            Characters characters = ExpandReferencesRewriter.this.xmlEventFactory.createCharacters(postingDate);
            EndElement endParam = ExpandReferencesRewriter.this.xmlEventFactory.createEndElement(StorageMacroConstants.MACRO_PARAMETER_ELEMENT, null);
            xmlEventListIterator.add(startParam);
            xmlEventListIterator.add(characters);
            xmlEventListIterator.add(endParam);
        }
    }
}

