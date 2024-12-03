/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.util.collections.GuavaConversionUtil
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.internal.pages.persistence;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.util.collections.GuavaConversionUtil;
import com.google.common.base.Predicate;
import java.util.List;

public interface AttachmentDaoInternal
extends AttachmentDao {
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatusForContainers(Iterable<? extends ContentEntityObject> var1);

    default public PageResponse<Attachment> getFilteredLatestVersionsOfAttachments(ContentEntityObject ceo, LimitedRequest pageRequest, java.util.function.Predicate<? super Attachment> predicate) {
        return this.getLatestVersionsOfAttachments(ceo, pageRequest, (Predicate<? super Attachment>)GuavaConversionUtil.toGuavaPredicate(predicate));
    }

    public List<Attachment> removeAllVersionsFromServer(Attachment var1);
}

