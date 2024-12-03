/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;

public class DelegatingResourceIdentifierMarshaller
implements Marshaller<ResourceIdentifier> {
    private Marshaller<PageResourceIdentifier> pageResourceIdentifierMarshaller;
    private Marshaller<BlogPostResourceIdentifier> blogPostResourceIdentifierMarshaller;
    private Marshaller<AttachmentResourceIdentifier> attachmentResourceIdentifierMarshaller;
    private Marshaller<UrlResourceIdentifier> urlResourceIdentifierMarshaller;
    private Marshaller<ShortcutResourceIdentifier> shortcutResourceIdentifierMarshaller;
    private Marshaller<UserResourceIdentifier> userResourceIdentifierMarshaller;
    private Marshaller<SpaceResourceIdentifier> spaceResourceIdentifierMarshaller;
    private Marshaller<ContentEntityResourceIdentifier> contentEntityResourceIdentifierMarshaller;

    @Override
    public Streamable marshal(ResourceIdentifier resourceIdentifier, ConversionContext conversionContext) throws XhtmlException {
        if (resourceIdentifier instanceof PageResourceIdentifier) {
            return this.pageResourceIdentifierMarshaller.marshal((PageResourceIdentifier)resourceIdentifier, conversionContext);
        }
        if (resourceIdentifier instanceof BlogPostResourceIdentifier) {
            return this.blogPostResourceIdentifierMarshaller.marshal((BlogPostResourceIdentifier)resourceIdentifier, conversionContext);
        }
        if (resourceIdentifier instanceof AttachmentResourceIdentifier && this.attachmentResourceIdentifierMarshaller != null) {
            return this.attachmentResourceIdentifierMarshaller.marshal((AttachmentResourceIdentifier)resourceIdentifier, conversionContext);
        }
        if (resourceIdentifier instanceof UrlResourceIdentifier && this.urlResourceIdentifierMarshaller != null) {
            return this.urlResourceIdentifierMarshaller.marshal((UrlResourceIdentifier)resourceIdentifier, conversionContext);
        }
        if (resourceIdentifier instanceof ShortcutResourceIdentifier && this.shortcutResourceIdentifierMarshaller != null) {
            return this.shortcutResourceIdentifierMarshaller.marshal((ShortcutResourceIdentifier)resourceIdentifier, conversionContext);
        }
        if (resourceIdentifier instanceof UserResourceIdentifier && this.userResourceIdentifierMarshaller != null) {
            return this.userResourceIdentifierMarshaller.marshal((UserResourceIdentifier)resourceIdentifier, conversionContext);
        }
        if (resourceIdentifier instanceof SpaceResourceIdentifier && this.spaceResourceIdentifierMarshaller != null) {
            return this.spaceResourceIdentifierMarshaller.marshal((SpaceResourceIdentifier)resourceIdentifier, conversionContext);
        }
        if (resourceIdentifier instanceof ContentEntityResourceIdentifier && this.contentEntityResourceIdentifierMarshaller != null) {
            return this.contentEntityResourceIdentifierMarshaller.marshal((ContentEntityResourceIdentifier)resourceIdentifier, conversionContext);
        }
        throw new UnsupportedOperationException("Cannot marshal resource identifier: " + resourceIdentifier);
    }

    public void setPageResourceIdentifierMarshaller(Marshaller<PageResourceIdentifier> pageResourceIdentifierMarshaller) {
        this.pageResourceIdentifierMarshaller = pageResourceIdentifierMarshaller;
    }

    public void setBlogPostResourceIdentifierMarshaller(Marshaller<BlogPostResourceIdentifier> blogPostResourceIdentifierMarshaller) {
        this.blogPostResourceIdentifierMarshaller = blogPostResourceIdentifierMarshaller;
    }

    public void setAttachmentResourceIdentifierMarshaller(Marshaller<AttachmentResourceIdentifier> attachmentResourceIdentifierMarshaller) {
        this.attachmentResourceIdentifierMarshaller = attachmentResourceIdentifierMarshaller;
    }

    public void setUrlResourceIdentifierMarshaller(Marshaller<UrlResourceIdentifier> urlResourceIdentifierMarshaller) {
        this.urlResourceIdentifierMarshaller = urlResourceIdentifierMarshaller;
    }

    public void setShortcutResourceIdentifierMarshaller(Marshaller<ShortcutResourceIdentifier> shortcutResourceIdentifierMarshaller) {
        this.shortcutResourceIdentifierMarshaller = shortcutResourceIdentifierMarshaller;
    }

    public void setUserResourceIdentifierMarshaller(Marshaller<UserResourceIdentifier> userResourceIdentifierMarshaller) {
        this.userResourceIdentifierMarshaller = userResourceIdentifierMarshaller;
    }

    public void setSpaceResourceIdentifierMarshaller(Marshaller<SpaceResourceIdentifier> spaceResourceIdentifierMarshaller) {
        this.spaceResourceIdentifierMarshaller = spaceResourceIdentifierMarshaller;
    }

    public void setContentEntityResourceIdentifierMarshaller(Marshaller<ContentEntityResourceIdentifier> contentEntityResourceIdentifierMarshaller) {
        this.contentEntityResourceIdentifierMarshaller = contentEntityResourceIdentifierMarshaller;
    }
}

