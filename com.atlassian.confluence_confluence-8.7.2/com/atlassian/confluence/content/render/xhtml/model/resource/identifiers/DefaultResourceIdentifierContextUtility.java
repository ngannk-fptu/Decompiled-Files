/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageTemplateResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierMatcher;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.confluence.util.ContentUtils;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class DefaultResourceIdentifierContextUtility
implements ResourceIdentifierContextUtility {
    private final ResourceIdentifierFactory riFactory;
    private final ResourceIdentifierMatcher riMatcher;

    public DefaultResourceIdentifierContextUtility(ResourceIdentifierFactory riFactory, ResourceIdentifierMatcher matcher) {
        this.riFactory = riFactory;
        this.riMatcher = matcher;
    }

    @Override
    public ResourceIdentifier createAbsoluteResourceIdentifier(ContentEntityObject ceo) {
        if (ceo instanceof Comment) {
            ceo = ((Comment)ceo).getContainer();
        }
        Objects.requireNonNull(ceo);
        return this.riFactory.getResourceIdentifier(ceo, new DefaultConversionContext(ceo.toPageContext()));
    }

    @Override
    public ResourceIdentifier createAbsolutePageTemplateResourceIdentifier(PageTemplate template) {
        return this.riFactory.getResourceIdentifier(template, new DefaultConversionContext(new PageTemplateContext(template)));
    }

    @Override
    public ResourceIdentifier convertToAbsolute(ResourceIdentifier ri, ContentEntityObject ceo) {
        if (ceo == null) {
            return ri;
        }
        if (ceo instanceof Comment) {
            ceo = ((Comment)ceo).getContainer();
        }
        String contextSpaceKey = ContentUtils.getSpaceKeyFromCeo(ceo);
        if (ri instanceof AttachmentResourceIdentifier) {
            ResourceIdentifier containerRi = ((AttachmentResourceIdentifier)ri).getAttachmentContainerResourceIdentifier();
            if ((containerRi = this.convertToAbsolute(containerRi, ceo)) instanceof AttachmentContainerResourceIdentifier) {
                return new AttachmentResourceIdentifier((AttachmentContainerResourceIdentifier)containerRi, ((AttachmentResourceIdentifier)ri).getFilename());
            }
        } else if (ri instanceof PageResourceIdentifier) {
            PageResourceIdentifier pageRi = (PageResourceIdentifier)ri;
            if (StringUtils.isBlank((CharSequence)pageRi.getSpaceKey()) && StringUtils.isNotBlank((CharSequence)contextSpaceKey)) {
                return new PageResourceIdentifier(contextSpaceKey, pageRi.getTitle());
            }
        } else if (ri instanceof BlogPostResourceIdentifier) {
            BlogPostResourceIdentifier blogRi = (BlogPostResourceIdentifier)ri;
            if (StringUtils.isBlank((CharSequence)blogRi.getSpaceKey()) && StringUtils.isNotBlank((CharSequence)contextSpaceKey)) {
                return new BlogPostResourceIdentifier(contextSpaceKey, blogRi.getTitle(), blogRi.getPostingDay());
            }
        } else if (ri == null) {
            return this.createAbsoluteResourceIdentifier(ceo);
        }
        return ri;
    }

    @Override
    public ResourceIdentifier convertToRelative(ResourceIdentifier ri, ContentEntityObject ceo) {
        if (ri instanceof AttachmentResourceIdentifier) {
            ResourceIdentifier attachmentContainerRi = ((AttachmentResourceIdentifier)ri).getAttachmentContainerResourceIdentifier();
            if ((attachmentContainerRi = this.innerConvertToRelative(attachmentContainerRi, ceo)) == null || attachmentContainerRi instanceof AttachmentContainerResourceIdentifier) {
                return new AttachmentResourceIdentifier((AttachmentContainerResourceIdentifier)attachmentContainerRi, ((AttachmentResourceIdentifier)ri).getFilename());
            }
            return ri;
        }
        if (ri instanceof PageTemplateResourceIdentifier) {
            return ri;
        }
        return this.innerConvertToRelative(ri, ceo);
    }

    public ResourceIdentifier innerConvertToRelative(ResourceIdentifier ri, ContentEntityObject ceo) {
        String spaceKey;
        if (ri == null || ceo == null) {
            return ri;
        }
        if (this.riMatcher.matches(ceo, ri)) {
            return null;
        }
        if (ceo instanceof Comment) {
            ceo = ((Comment)ceo).getContainer();
        }
        if ((spaceKey = ContentUtils.getSpaceKeyFromCeo(ceo)) == null) {
            return ri;
        }
        if (ri instanceof PageResourceIdentifier) {
            PageResourceIdentifier pageRi = (PageResourceIdentifier)ri;
            if (pageRi.getTitle().contains(":")) {
                return new PageResourceIdentifier(pageRi.getSpaceKey(), pageRi.getTitle());
            }
            if (pageRi.getSpaceKey() != null && pageRi.getSpaceKey().equals(spaceKey)) {
                return new PageResourceIdentifier(null, pageRi.getTitle());
            }
        } else if (ri instanceof BlogPostResourceIdentifier) {
            BlogPostResourceIdentifier blogRi = (BlogPostResourceIdentifier)ri;
            if (blogRi.getTitle().contains(":")) {
                return new BlogPostResourceIdentifier(blogRi.getSpaceKey(), blogRi.getTitle(), blogRi.getPostingDay());
            }
            if (blogRi.getSpaceKey() != null && blogRi.getSpaceKey().equals(spaceKey)) {
                return new BlogPostResourceIdentifier(null, blogRi.getTitle(), blogRi.getPostingDay());
            }
        }
        return ri;
    }
}

