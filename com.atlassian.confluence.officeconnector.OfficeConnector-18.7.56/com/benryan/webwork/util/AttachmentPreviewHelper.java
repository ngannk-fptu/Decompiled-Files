/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.themes.AttachmentHelper
 */
package com.benryan.webwork.util;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.themes.AttachmentHelper;
import com.benryan.webwork.util.AttachmentPreviewBreadcrumb;
import java.util.List;

public class AttachmentPreviewHelper
extends AttachmentHelper {
    public AttachmentPreviewHelper(ConfluenceActionSupport action, Attachment attachment) {
        super(action, attachment);
    }

    public List getBreadcrumbs() {
        return new AttachmentPreviewBreadcrumb(this.getAttachment()).getBreadcrumbsTrail();
    }

    public AbstractPage getPage() {
        return (AbstractPage)this.getAttachment().getContainer();
    }
}

