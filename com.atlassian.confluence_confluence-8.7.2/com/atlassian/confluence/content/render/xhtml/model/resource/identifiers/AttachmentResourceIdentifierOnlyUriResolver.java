/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.setup.settings.SettingsManager;
import java.net.URI;
import java.util.Calendar;
import java.util.Objects;
import javax.ws.rs.core.UriBuilder;

public class AttachmentResourceIdentifierOnlyUriResolver
implements ResourceIdentifierResolver<AttachmentResourceIdentifier, URI> {
    private final SettingsManager settingsManager;

    public AttachmentResourceIdentifierOnlyUriResolver(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public URI resolve(AttachmentResourceIdentifier resourceIdentifier, ConversionContext conversionContext) throws CannotResolveResourceIdentifierException {
        UriBuilder uriBuilder;
        AttachmentContainerResourceIdentifier containerResourceIdentifier = resourceIdentifier.getAttachmentContainerResourceIdentifier();
        String attachmentFilename = resourceIdentifier.getFilename();
        if (containerResourceIdentifier == null) {
            ContentEntityObject entity = conversionContext.getEntity();
            Objects.requireNonNull(entity);
            ContentEntityObject currentCEO = (ContentEntityObject)entity.getLatestVersion();
            if (currentCEO instanceof Comment) {
                currentCEO = ((Comment)currentCEO).getContainer();
            }
            Objects.requireNonNull(currentCEO);
            String type = currentCEO.getType();
            if (currentCEO instanceof BlogPost) {
                Calendar postingDay = ((BlogPost)currentCEO).getPostingCalendarDate();
                uriBuilder = this.createUriForBlogPost(attachmentFilename, ((BlogPost)currentCEO).getSpaceKey(), currentCEO.getTitle(), postingDay, type);
            } else {
                uriBuilder = currentCEO instanceof Page ? this.createUriForPage(attachmentFilename, ((Page)currentCEO).getSpaceKey(), currentCEO.getTitle(), type) : this.createUriForGenericContent(attachmentFilename, currentCEO);
            }
        } else if (containerResourceIdentifier instanceof PageResourceIdentifier) {
            PageResourceIdentifier pageRI = (PageResourceIdentifier)containerResourceIdentifier;
            String pageTitle = pageRI.getTitle();
            String spaceKey = this.computeSpaceKey(pageRI.getSpaceKey(), conversionContext);
            String type = "page";
            uriBuilder = this.createUriForPage(attachmentFilename, spaceKey, pageTitle, type);
        } else if (containerResourceIdentifier instanceof BlogPostResourceIdentifier) {
            BlogPostResourceIdentifier blogPostRI = (BlogPostResourceIdentifier)containerResourceIdentifier;
            String pageTitle = blogPostRI.getTitle();
            String spaceKey = this.computeSpaceKey(blogPostRI.getSpaceKey(), conversionContext);
            Calendar postingDay = blogPostRI.getPostingDay();
            String type = "blogpost";
            uriBuilder = this.createUriForBlogPost(attachmentFilename, spaceKey, pageTitle, postingDay, type);
        } else {
            ContentEntityObject entity = conversionContext.getEntity();
            Objects.requireNonNull(entity);
            ContentEntityObject currentCEO = (ContentEntityObject)entity.getLatestVersion();
            uriBuilder = this.createUriForGenericContent(attachmentFilename, currentCEO);
        }
        return uriBuilder.build(new Object[0]);
    }

    private UriBuilder createUriForBlogPost(String attachmentFilename, String spaceKey, String pageTitle, Calendar postingDay, String type) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)(this.getBaseUrlNoEndingSlash() + Attachment.DOWNLOAD_PATH_BASE)).path("embedded-" + type).path(spaceKey).path(BlogPost.toDatePath(postingDay.getTime())).path(pageTitle).path(attachmentFilename).queryParam("api", new Object[]{"v2"});
        return uriBuilder;
    }

    private UriBuilder createUriForPage(String attachmentFilename, String spaceKey, String pageTitle, String type) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)(this.getBaseUrlNoEndingSlash() + Attachment.DOWNLOAD_PATH_BASE)).path("embedded-" + type).path(spaceKey).path(pageTitle).path(attachmentFilename).queryParam("api", new Object[]{"v2"});
        return uriBuilder;
    }

    private UriBuilder createUriForGenericContent(String attachmentFilename, ContentEntityObject ceo) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)(this.getBaseUrlNoEndingSlash() + Attachment.DOWNLOAD_PATH_BASE)).path("" + ceo.getId()).path(attachmentFilename).queryParam("api", new Object[]{"v2"});
        return uriBuilder;
    }

    private String getBaseUrlNoEndingSlash() {
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    private String computeSpaceKey(String spaceKey, ConversionContext conversionContext) {
        if (spaceKey == null) {
            ContentEntityObject entity = conversionContext.getEntity();
            Objects.requireNonNull(entity);
            ContentEntityObject currentCEO = (ContentEntityObject)entity.getLatestVersion();
            if (currentCEO instanceof SpaceContentEntityObject) {
                spaceKey = ((SpaceContentEntityObject)currentCEO).getSpaceKey();
            }
        }
        return spaceKey;
    }
}

