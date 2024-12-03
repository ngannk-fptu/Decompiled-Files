/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.embedded.EmbeddedResource
 *  com.atlassian.renderer.util.UrlUtil
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.renderer.embedded;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.BlogPostReference;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.util.UrlUtil;
import com.atlassian.spring.container.ContainerManager;
import java.text.ParseException;
import java.util.Objects;

public class EmbeddedResourceUtils {
    public static Attachment resolveAttachment(PageContext context, EmbeddedResource resource) {
        ContentEntityObject ceo;
        if (resource.isExternal()) {
            throw new IllegalArgumentException("Cannot resolve external resource into attachment.");
        }
        String spaceKey = resource.getSpace();
        String pageTitle = resource.getPage();
        ContentEntityObject contextEntity = context.getEntity();
        PageManager pageManager = (PageManager)ContainerManager.getComponent((String)"pageManager");
        if (pageTitle == null) {
            ceo = contextEntity;
            if (contextEntity instanceof Comment) {
                ceo = ((Comment)contextEntity).getContainer();
            }
        } else if (spaceKey == null) {
            if (contextEntity instanceof Comment) {
                Comment comment = (Comment)contextEntity;
                spaceKey = comment.getSpaceKey();
            } else if (contextEntity instanceof SpaceContentEntityObject) {
                SpaceContentEntityObject spaceContentEntityObject = (SpaceContentEntityObject)contextEntity;
                spaceKey = spaceContentEntityObject.getSpaceKey();
            }
            ceo = pageManager.getPage(Objects.requireNonNull(spaceKey), pageTitle);
        } else {
            ceo = pageManager.getPage(spaceKey, pageTitle);
        }
        if (ceo == null) {
            try {
                BlogPostReference blogPostReference = new BlogPostReference(pageTitle, spaceKey, pageManager);
                ceo = blogPostReference.getBlogPost();
            }
            catch (ParseException blogPostReference) {
                // empty catch block
            }
        }
        if (ceo == null) {
            return null;
        }
        ceo = (ContentEntityObject)ceo.getLatestVersion();
        AttachmentManager attachmentManager = (AttachmentManager)ContainerManager.getComponent((String)"attachmentManager");
        return attachmentManager.getAttachment(ceo, UrlUtil.escapeSpecialCharacters((String)resource.getFilename()), 0);
    }
}

