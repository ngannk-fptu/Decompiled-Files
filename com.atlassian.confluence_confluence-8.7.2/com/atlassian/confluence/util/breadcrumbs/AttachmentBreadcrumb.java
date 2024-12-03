/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BlogPostBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.PageBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb;

public class AttachmentBreadcrumb
extends AbstractBreadcrumb {
    private Attachment attachment;

    public AttachmentBreadcrumb(Attachment attachment) {
        super(attachment.getFileName(), attachment.getDownloadPath());
        this.displayTitle = attachment.getFileName();
        this.attachment = attachment;
    }

    @Override
    protected Breadcrumb getParent() {
        ContentEntityObject content = this.attachment.getContainer();
        if (content instanceof Page) {
            return new PageBreadcrumb((Page)content);
        }
        if (content instanceof BlogPost) {
            return new BlogPostBreadcrumb((BlogPost)content);
        }
        if (content instanceof SpaceDescription) {
            return new SpaceBreadcrumb(((SpaceDescription)content).getSpace());
        }
        return null;
    }
}

