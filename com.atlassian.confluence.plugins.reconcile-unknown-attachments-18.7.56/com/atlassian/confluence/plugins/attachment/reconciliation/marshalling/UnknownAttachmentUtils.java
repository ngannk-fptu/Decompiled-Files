/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.pages.AttachmentManager
 */
package com.atlassian.confluence.plugins.attachment.reconciliation.marshalling;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.pages.AttachmentManager;

public final class UnknownAttachmentUtils {
    public static final String UNRESTORED_LABEL = "unrestored-unknown-attachment";
    private static final String ATTACHMENT_COUNT_PROPERTY = "attachmentCount";

    public static int countAttachmentsOnContent(ConversionContext conversionContext, AttachmentManager attachmentManager) {
        if (conversionContext.hasProperty(ATTACHMENT_COUNT_PROPERTY)) {
            return (Integer)conversionContext.getProperty(ATTACHMENT_COUNT_PROPERTY);
        }
        int attachmentCount = attachmentManager.countLatestVersionsOfAttachments(conversionContext.getEntity());
        conversionContext.setProperty(ATTACHMENT_COUNT_PROPERTY, (Object)attachmentCount);
        return attachmentCount;
    }
}

