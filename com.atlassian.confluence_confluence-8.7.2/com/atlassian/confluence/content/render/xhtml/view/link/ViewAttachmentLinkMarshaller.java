/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.model.links.NotPermittedLink;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;
import com.atlassian.confluence.content.render.xhtml.view.ModelToRenderedClassMapper;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewLinkMarshallerMetricsKey;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollector;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollectors;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MetricsCollectingMarshaller;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class ViewAttachmentLinkMarshaller
implements Marshaller<Link> {
    private static final ViewLinkMarshallerMetricsKey METRICS_ACCUMULATION_KEY = new ViewLinkMarshallerMetricsKey("attachmentLink");
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final PageManager pageManager;
    private final ContentEntityManager contentEntityManager;
    private final PermissionManager permissionManager;
    private final AttachmentManager attachmentManager;
    private final Marshaller<UnresolvedLink> unresolvedLinkMarshaller;
    private final StaxStreamMarshaller<Link> commonLinkAttributeStaxStreamMarshaller;
    private final Marshaller<Link> linkBodyMarshaller;
    private final DraftManager draftManager;
    private final HrefEvaluator hrefEvaluator;
    private final Marshaller<Link> notPermittedLinkMarshaller;
    private final ResourceIdentifierContextUtility riUtils;
    private final ModelToRenderedClassMapper linkClassRenderer;

    public ViewAttachmentLinkMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate, PageManager pageManager, ContentEntityManager contentEntityManager, AttachmentManager attachmentManager, PermissionManager permissionManager, Marshaller<UnresolvedLink> unresolvedLinkMarshaller, Marshaller<Link> linkBodyMarshaller, @Nullable StaxStreamMarshaller<Link> commonLinkAttributeStaxStreamMarshaller, DraftManager draftManager, HrefEvaluator hrefEvaluator, Marshaller<Link> notPermittedLinkMarshaller, ResourceIdentifierContextUtility riUtils, ModelToRenderedClassMapper linkClassRenderer) {
        this.xmlStreamWriterTemplate = (XmlStreamWriterTemplate)Preconditions.checkNotNull((Object)xmlStreamWriterTemplate);
        this.pageManager = (PageManager)Preconditions.checkNotNull((Object)pageManager);
        this.contentEntityManager = (ContentEntityManager)Preconditions.checkNotNull((Object)contentEntityManager);
        this.attachmentManager = (AttachmentManager)Preconditions.checkNotNull((Object)attachmentManager);
        this.permissionManager = (PermissionManager)Preconditions.checkNotNull((Object)permissionManager);
        this.unresolvedLinkMarshaller = (Marshaller)Preconditions.checkNotNull(unresolvedLinkMarshaller);
        this.linkBodyMarshaller = (Marshaller)Preconditions.checkNotNull(linkBodyMarshaller);
        this.commonLinkAttributeStaxStreamMarshaller = commonLinkAttributeStaxStreamMarshaller;
        this.draftManager = (DraftManager)Preconditions.checkNotNull((Object)draftManager);
        this.hrefEvaluator = (HrefEvaluator)Preconditions.checkNotNull((Object)hrefEvaluator);
        this.notPermittedLinkMarshaller = (Marshaller)Preconditions.checkNotNull(notPermittedLinkMarshaller);
        this.riUtils = (ResourceIdentifierContextUtility)Preconditions.checkNotNull((Object)riUtils);
        this.linkClassRenderer = (ModelToRenderedClassMapper)Preconditions.checkNotNull((Object)linkClassRenderer);
    }

    @Override
    public Streamable marshal(Link attachmentLink, ConversionContext conversionContext) throws XhtmlException {
        MarshallerMetricsCollector metricsCollector = MarshallerMetricsCollectors.metricsCollector(conversionContext, METRICS_ACCUMULATION_KEY);
        Marshaller<Link> timedMarshaller = MetricsCollectingMarshaller.forMarshaller(metricsCollector, this.marshalInternal());
        return timedMarshaller.marshal(attachmentLink, conversionContext);
    }

    private Marshaller<Link> marshalInternal() {
        return this::marshalInternal;
    }

    private Streamable marshalInternal(Link attachmentLink, ConversionContext conversionContext) throws XhtmlException {
        AttachmentResourceIdentifier attachmentResourceIdentifier = this.getAttachmentResourceIdentifier(attachmentLink, conversionContext);
        ContentEntityObject attachmentContainer = this.getAttachmentContainer(attachmentResourceIdentifier);
        if (attachmentContainer == null) {
            return this.unresolvedLink(attachmentLink, conversionContext);
        }
        Attachment attachment = this.attachmentManager.getAttachment(attachmentContainer, attachmentResourceIdentifier.getFilename());
        if (attachment == null) {
            return this.unresolvedLink(attachmentLink, conversionContext);
        }
        if (!this.hasPermissionToView(attachment)) {
            return this.notPermittedLink(attachmentLink, conversionContext);
        }
        return this.attachmentLink(attachmentLink, conversionContext, attachment);
    }

    private Streamable notPermittedLink(Link attachmentLink, ConversionContext conversionContext) throws XhtmlException {
        return this.notPermittedLinkMarshaller.marshal(new NotPermittedLink(attachmentLink), conversionContext);
    }

    private boolean hasPermissionToView(Attachment attachment) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, attachment);
    }

    private Streamable unresolvedLink(Link attachmentLink, ConversionContext conversionContext) throws XhtmlException {
        return this.unresolvedLinkMarshaller.marshal(new UnresolvedLink(attachmentLink), conversionContext);
    }

    private Streamable attachmentLink(Link attachmentLink, ConversionContext conversionContext, Attachment attachment) throws XhtmlException {
        Streamable marshalledLinkBody = this.linkBodyMarshaller.marshal(attachmentLink, conversionContext);
        String href = this.hrefEvaluator.createHref(conversionContext, attachment, null);
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> this.writeAttachmentLink(xmlStreamWriter, underlyingWriter, attachmentLink, href, conversionContext, marshalledLinkBody));
    }

    private void writeAttachmentLink(XMLStreamWriter xmlWriter, Writer underlyingWriter, Link attachmentLink, String href, ConversionContext conversionContext, Streamable linkBody) throws XMLStreamException, IOException {
        xmlWriter.writeStartElement("a");
        String displayClass = this.linkClassRenderer.getRenderedClass(attachmentLink);
        if (StringUtils.isNotBlank((CharSequence)displayClass)) {
            xmlWriter.writeAttribute("class", displayClass);
        }
        xmlWriter.writeAttribute("href", href);
        if (this.commonLinkAttributeStaxStreamMarshaller != null) {
            this.commonLinkAttributeStaxStreamMarshaller.marshal(attachmentLink, xmlWriter, conversionContext);
        }
        xmlWriter.writeCharacters("");
        xmlWriter.flush();
        linkBody.writeTo(underlyingWriter);
        xmlWriter.writeEndElement();
    }

    private ContentEntityObject getAttachmentContainer(AttachmentResourceIdentifier attachmentResourceIdentifier) {
        AttachmentContainerResourceIdentifier attachmentContainerResourceIdentifier = attachmentResourceIdentifier.getAttachmentContainerResourceIdentifier();
        if (attachmentContainerResourceIdentifier instanceof PageResourceIdentifier) {
            PageResourceIdentifier pageResourceIdentifier = (PageResourceIdentifier)attachmentContainerResourceIdentifier;
            return this.pageManager.getPage(pageResourceIdentifier.getSpaceKey(), pageResourceIdentifier.getTitle());
        }
        if (attachmentContainerResourceIdentifier instanceof BlogPostResourceIdentifier) {
            BlogPostResourceIdentifier blogPostResourceIdentifier = (BlogPostResourceIdentifier)attachmentContainerResourceIdentifier;
            return this.pageManager.getBlogPost(blogPostResourceIdentifier.getSpaceKey(), blogPostResourceIdentifier.getTitle(), blogPostResourceIdentifier.getPostingDay());
        }
        if (attachmentContainerResourceIdentifier instanceof DraftResourceIdentifier) {
            return this.draftManager.getDraft(((DraftResourceIdentifier)attachmentContainerResourceIdentifier).getDraftId());
        }
        if (attachmentContainerResourceIdentifier instanceof ContentEntityResourceIdentifier) {
            return this.contentEntityManager.getById(((ContentEntityResourceIdentifier)attachmentContainerResourceIdentifier).getContentId());
        }
        return null;
    }

    private AttachmentResourceIdentifier getAttachmentResourceIdentifier(Link attachmentLink, @Nullable ConversionContext conversionContext) {
        AttachmentResourceIdentifier attachmentResourceIdentifier = (AttachmentResourceIdentifier)attachmentLink.getDestinationResourceIdentifier();
        if (conversionContext != null && attachmentResourceIdentifier != null) {
            return (AttachmentResourceIdentifier)this.riUtils.convertToAbsolute(attachmentResourceIdentifier, conversionContext.getEntity());
        }
        return attachmentResourceIdentifier;
    }
}

