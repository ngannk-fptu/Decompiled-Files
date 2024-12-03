/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 */
package com.benryan.webwork.util;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.benryan.webwork.util.ViewAttachmentsBreadcrumb;

public class AttachmentPreviewBreadcrumb
extends AbstractBreadcrumb {
    private AbstractPage page;

    public AttachmentPreviewBreadcrumb(Attachment attachment) {
        super(attachment.getFileName(), null);
        this.page = (AbstractPage)attachment.getContainer();
    }

    protected Breadcrumb getParent() {
        return new ViewAttachmentsBreadcrumb(this.page);
    }
}

