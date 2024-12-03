/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants
 *  com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierConstants
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierConstants;
import com.atlassian.confluence.plugin.copyspace.context.ContentRewriterContext;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.ContentRewriter;
import com.atlassian.confluence.plugin.copyspace.service.impl.AbstractChainedContentRewriter;
import com.atlassian.confluence.plugin.copyspace.util.XmlEventUtils;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

public class OldSpaceKeyRewriter
extends AbstractChainedContentRewriter {
    private final XMLEventFactory xmlEventFactory;
    private final Set<QName> SUPPORTED_RESOURCES = Sets.newHashSet((Object[])new QName[]{StorageResourceIdentifierConstants.BLOG_POST_RESOURCE_QNAME, StorageResourceIdentifierConstants.PAGE_RESOURCE_QNAME});

    public OldSpaceKeyRewriter(XMLEventFactory xmlEventFactory, ContentRewriter contentRewriter, ContentRewriterContext contentRewriterContext, CopySpaceContext copySpaceContext) {
        super(contentRewriter, contentRewriterContext, copySpaceContext);
        this.xmlEventFactory = xmlEventFactory;
    }

    private boolean isStartPdfMacro(XMLEvent nextEvent) {
        return nextEvent.isStartElement() && StorageMacroConstants.MACRO_V2_ELEMENT.equals(nextEvent.asStartElement().getName()) && "viewpdf".equals(nextEvent.asStartElement().getAttributeByName(StorageMacroConstants.NAME_ATTRIBUTE).getValue());
    }

    @Override
    protected List<XMLEvent> updateLinkEventsInternal(List<XMLEvent> xmlEvents) {
        boolean inPdfMacro = false;
        for (int i = 0; i < xmlEvents.size(); ++i) {
            XMLEvent nextEvent = xmlEvents.get(i);
            if (this.isStartPdfMacro(nextEvent)) {
                inPdfMacro = true;
            }
            if (nextEvent.isEndElement() && StorageMacroConstants.MACRO_V2_ELEMENT.equals(nextEvent.asEndElement().getName()) && inPdfMacro) {
                inPdfMacro = false;
            }
            if (!nextEvent.isStartElement() || !this.isSupportedResourceWithEmptySpace(nextEvent.asStartElement(), inPdfMacro)) continue;
            StartElement element = nextEvent.asStartElement();
            Iterator<Attribute> attributeIterator = null;
            if (inPdfMacro) {
                attributeIterator = XmlEventUtils.removeAttribute(element, StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME).iterator();
            } else {
                Attribute newAttribute = this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME, this.contentRewriterContext.getOriginalSpaceKey());
                attributeIterator = XmlEventUtils.getExtendedAttributes(element, newAttribute).iterator();
            }
            xmlEvents.set(i, this.xmlEventFactory.createStartElement(element.getName(), attributeIterator, null));
        }
        return xmlEvents;
    }

    private boolean isSupportedResourceWithEmptySpace(StartElement startElement, boolean inPdfMacro) {
        if (!this.SUPPORTED_RESOURCES.contains(startElement.getName())) {
            return false;
        }
        Attribute spaceKeyAttr = startElement.getAttributeByName(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME);
        return spaceKeyAttr == null || StringUtils.isEmpty((CharSequence)spaceKeyAttr.getValue()) || inPdfMacro && (spaceKeyAttr.getValue().equals(this.copySpaceContext.getOriginalSpaceKey()) || spaceKeyAttr.getValue().equals(this.copySpaceContext.getTargetSpaceKey()));
    }
}

