/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataDao;

public interface AttachmentDaoFactory {
    public AttachmentDao getInstance(AttachmentDataDao var1);
}

