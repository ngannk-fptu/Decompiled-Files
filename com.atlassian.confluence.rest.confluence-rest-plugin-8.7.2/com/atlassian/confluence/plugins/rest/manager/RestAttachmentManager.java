/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.rest.entities.AttachmentEntity;
import com.atlassian.confluence.plugins.rest.entities.AttachmentEntityList;

public interface RestAttachmentManager {
    public AttachmentEntity getAttachmentEntity(Long var1);

    public AttachmentEntity convertToAttachmentEntity(Attachment var1);

    public AttachmentEntityList createAttachmentEntityListForContent(ContentEntityObject var1);
}

