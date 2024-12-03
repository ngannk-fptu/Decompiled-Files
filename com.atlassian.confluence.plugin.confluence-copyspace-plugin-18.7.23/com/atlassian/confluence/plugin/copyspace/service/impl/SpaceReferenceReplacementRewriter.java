/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierConstants
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierConstants;
import com.atlassian.confluence.plugin.copyspace.context.ContentRewriterContext;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.ContentRewriter;
import com.atlassian.confluence.plugin.copyspace.service.impl.AbstractChainedContentRewriter;
import com.atlassian.confluence.plugin.copyspace.util.XmlEventUtils;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

public class SpaceReferenceReplacementRewriter
extends AbstractChainedContentRewriter {
    private final XMLEventFactory xmlEventFactory;
    private final Set<QName> supportedResources;

    public SpaceReferenceReplacementRewriter(XMLEventFactory xmlEventFactory, CopySpaceContext copySpaceContext, ContentRewriter nextContentRewriter, ContentRewriterContext contentRewriterContext) {
        super(nextContentRewriter, contentRewriterContext, copySpaceContext);
        this.xmlEventFactory = xmlEventFactory;
        this.supportedResources = this.buildSupportedResourcesSet();
    }

    @Override
    protected List<XMLEvent> updateLinkEventsInternal(List<XMLEvent> xmlEvents) {
        boolean isWrappedInAttachment = false;
        for (int i = 0; i < xmlEvents.size(); ++i) {
            XMLEvent nextEvent = xmlEvents.get(i);
            if (!nextEvent.isStartElement()) continue;
            StartElement nextElement = nextEvent.asStartElement();
            if (StorageResourceIdentifierConstants.ATTACHMENT_RESOURCE_QNAME.equals(nextEvent.asStartElement().getName())) {
                isWrappedInAttachment = true;
            }
            if (isWrappedInAttachment && !this.copySpaceContext.isCopyAttachments() || !this.isSupportedResourceWithMatchingSpace(nextEvent.asStartElement())) continue;
            Attribute newAttribute = this.xmlEventFactory.createAttribute(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME, this.contentRewriterContext.getNewSpaceKey());
            List<Attribute> attributesToWrite = XmlEventUtils.getExtendedAttributes(nextElement, newAttribute);
            xmlEvents.set(i, this.xmlEventFactory.createStartElement(nextElement.getName(), attributesToWrite.iterator(), null));
        }
        return xmlEvents;
    }

    private boolean isSupportedResourceWithMatchingSpace(StartElement startElement) {
        if (!this.supportedResources.contains(startElement.getName())) {
            return false;
        }
        Attribute spaceKeyAttr = startElement.getAttributeByName(StorageResourceIdentifierConstants.SPACEKEY_ATTRIBUTE_QNAME);
        return spaceKeyAttr == null || StringUtils.isEmpty((CharSequence)spaceKeyAttr.getValue()) || this.contentRewriterContext.getOriginalSpaceKey().equals(spaceKeyAttr.getValue());
    }

    private Set<QName> buildSupportedResourcesSet() {
        HashSet supportedResources = Sets.newHashSet();
        if (this.copySpaceContext.isCopyBlogPosts()) {
            supportedResources.add(StorageResourceIdentifierConstants.BLOG_POST_RESOURCE_QNAME);
        }
        if (this.copySpaceContext.isCopyPages()) {
            supportedResources.add(StorageResourceIdentifierConstants.PAGE_RESOURCE_QNAME);
        }
        return supportedResources;
    }
}

