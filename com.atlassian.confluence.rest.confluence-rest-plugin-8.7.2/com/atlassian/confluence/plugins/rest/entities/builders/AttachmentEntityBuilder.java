/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.persistence.AnyTypeDao
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.search.contentnames.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResult
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.builders.SearchEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DefaultRestAttachmentManager;
import com.atlassian.confluence.search.v2.SearchResult;

public class AttachmentEntityBuilder
implements SearchEntityBuilder {
    private AnyTypeDao anyTypeDao;
    private DefaultRestAttachmentManager restAttachmentManager;

    public AttachmentEntityBuilder(AnyTypeDao anyTypeDao, DefaultRestAttachmentManager restAttachmentManager) {
        this.anyTypeDao = anyTypeDao;
        this.restAttachmentManager = restAttachmentManager;
    }

    @Override
    public SearchResultEntity build(SearchResult result) {
        long id = ((HibernateHandle)result.getHandle()).getId();
        Attachment attachment = (Attachment)this.anyTypeDao.getByIdAndType(id, Attachment.class);
        return this.build(attachment);
    }

    private SearchResultEntity build(Attachment attachment) {
        return this.restAttachmentManager.convertToAttachmentEntity(attachment);
    }

    @Override
    public SearchResultEntity build(com.atlassian.confluence.search.contentnames.SearchResult result) {
        Attachment attachment = (Attachment)this.anyTypeDao.getByIdAndType(result.getId().longValue(), Attachment.class);
        return this.build(attachment);
    }
}

