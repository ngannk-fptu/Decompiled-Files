/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveAttachmentContainerException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import org.apache.commons.lang3.StringUtils;

public class AttachmentOwningContentResolver
implements ResourceIdentifierResolver<AttachmentResourceIdentifier, ContentEntityObject> {
    private final ResourceIdentifierResolver<PageResourceIdentifier, Page> pageResourceResolver;
    private final ResourceIdentifierResolver<BlogPostResourceIdentifier, BlogPost> blogPostResourceResolver;
    private final ResourceIdentifierResolver<ContentEntityResourceIdentifier, ContentEntityObject> contentEntityResourceResolver;
    private final ResourceIdentifierResolver<DraftResourceIdentifier, Draft> draftResourceResolver;
    private final ContentEntityManager contentEntityManager;
    private final AttachmentManager attachmentManager;

    public AttachmentOwningContentResolver(ResourceIdentifierResolver<PageResourceIdentifier, Page> pageResourceResolver, ResourceIdentifierResolver<BlogPostResourceIdentifier, BlogPost> blogPostResourceResolver, ResourceIdentifierResolver<ContentEntityResourceIdentifier, ContentEntityObject> contentEntityResourceResolver, ResourceIdentifierResolver<DraftResourceIdentifier, Draft> draftResourceResolver, ContentEntityManager contentEntityManager, AttachmentManager attachmentManager) {
        this.pageResourceResolver = pageResourceResolver;
        this.blogPostResourceResolver = blogPostResourceResolver;
        this.contentEntityResourceResolver = contentEntityResourceResolver;
        this.draftResourceResolver = draftResourceResolver;
        this.contentEntityManager = contentEntityManager;
        this.attachmentManager = attachmentManager;
    }

    @Override
    public ContentEntityObject resolve(AttachmentResourceIdentifier resourceIdentifier, ConversionContext conversionContext) throws CannotResolveResourceIdentifierException {
        return this.getAttachmentOwningContent(resourceIdentifier, conversionContext);
    }

    private ContentEntityObject getAttachmentOwningContent(AttachmentResourceIdentifier attachmentResourceIdentifier, ConversionContext conversionContext) throws CannotResolveAttachmentContainerException {
        AttachmentContainerResourceIdentifier attachmentContainerResourceIdentifier = attachmentResourceIdentifier.getAttachmentContainerResourceIdentifier();
        ContentEntityObject owningContent = null;
        try {
            String downloadPath;
            ContentEntityObject entity = conversionContext.getEntity();
            if (attachmentContainerResourceIdentifier == null && entity != null) {
                owningContent = (ContentEntityObject)entity.getLatestVersion();
            } else if (attachmentContainerResourceIdentifier instanceof PageResourceIdentifier) {
                owningContent = this.pageResourceResolver.resolve((PageResourceIdentifier)attachmentContainerResourceIdentifier, conversionContext);
            } else if (attachmentContainerResourceIdentifier instanceof BlogPostResourceIdentifier) {
                owningContent = this.blogPostResourceResolver.resolve((BlogPostResourceIdentifier)attachmentContainerResourceIdentifier, conversionContext);
            } else if (attachmentContainerResourceIdentifier instanceof DraftResourceIdentifier) {
                owningContent = this.draftResourceResolver.resolve((DraftResourceIdentifier)attachmentContainerResourceIdentifier, conversionContext);
            } else if (attachmentContainerResourceIdentifier instanceof ContentEntityResourceIdentifier && (owningContent = this.contentEntityResourceResolver.resolve((ContentEntityResourceIdentifier)attachmentContainerResourceIdentifier, conversionContext)) != null && owningContent.isDraft() && StringUtils.isEmpty((CharSequence)(downloadPath = this.attachmentManager.getAttachmentDownloadPath(owningContent, attachmentResourceIdentifier.getResourceName())))) {
                Long originalContentId = owningContent.getLatestVersionId();
                owningContent = this.contentEntityManager.getById(originalContentId);
            }
            if (owningContent == null) {
                throw new CannotResolveResourceIdentifierException(attachmentResourceIdentifier, "Unable to resolve the resource identifier");
            }
        }
        catch (CannotResolveResourceIdentifierException ex) {
            throw new CannotResolveAttachmentContainerException(attachmentResourceIdentifier, "Cannot resolve attachment " + attachmentResourceIdentifier, (Throwable)ex);
        }
        return owningContent;
    }
}

