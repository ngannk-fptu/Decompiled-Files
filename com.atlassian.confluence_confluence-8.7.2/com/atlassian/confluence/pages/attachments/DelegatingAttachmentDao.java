/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.attachments;

import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;

public interface DelegatingAttachmentDao {
    public AttachmentDao getDelegate();
}

