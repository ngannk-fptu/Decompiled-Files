/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AttachmentManager;
import java.util.Collection;

public interface FileUploadManager {
    public void storeResource(AttachmentResource var1, ContentEntityObject var2);

    public void storeResources(Collection<AttachmentResource> var1, ContentEntityObject var2);

    public void setAttachmentManager(AttachmentManager var1);
}

