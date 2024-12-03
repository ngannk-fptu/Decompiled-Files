/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.spring.container.ContainerManager;
import java.io.InputStream;

public class AttachmentUtils {
    @Deprecated
    public static InputStream getLatestAttachmentStream(Attachment attachment) {
        AttachmentManager attachmentManager = (AttachmentManager)ContainerManager.getInstance().getContainerContext().getComponent((Object)"attachmentManager");
        if (attachmentManager == null) {
            throw new IllegalStateException("Could not retrieve the attachmentManager from the application context!");
        }
        return attachmentManager.getAttachmentData(attachment);
    }
}

