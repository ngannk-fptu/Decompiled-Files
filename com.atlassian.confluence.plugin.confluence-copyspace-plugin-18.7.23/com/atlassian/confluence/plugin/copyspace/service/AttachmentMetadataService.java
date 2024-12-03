/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import java.util.List;

public interface AttachmentMetadataService {
    public void preserveMetadata(ContentEntityObject var1, ContentEntityObject var2);

    public void preserveMetadata(List<Attachment> var1, ContentEntityObject var2);
}

