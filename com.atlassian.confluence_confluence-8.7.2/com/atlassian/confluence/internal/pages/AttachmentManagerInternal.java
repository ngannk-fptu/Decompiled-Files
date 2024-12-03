/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.google.common.base.Predicate
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.pages;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AttachmentManagerInternal
extends AttachmentManager {
    @Deprecated
    @Transactional(readOnly=true)
    default public PageResponse<Attachment> getAttachments(ContentEntityObject content, LimitedRequest pageRequest, com.google.common.base.Predicate<? super Attachment> filterPredicate) {
        return this.getFilteredAttachments(content, pageRequest, (Predicate<? super Attachment>)filterPredicate);
    }

    @Transactional(readOnly=true)
    public PageResponse<Attachment> getFilteredAttachments(ContentEntityObject var1, LimitedRequest var2, Predicate<? super Attachment> var3);

    public void moveAttachment(Attachment var1, ContentEntityObject var2);

    @Transactional(readOnly=true)
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatusForContainers(Iterable<? extends ContentEntityObject> var1);
}

