/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers;

import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.internal.pages.AttachmentManagerInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.ContainerAttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers.DefaultAttachmentIdentifier;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AllLatestVersionAttachmentIdentifier
implements ContainerAttachmentIdentifier {
    private static int maxLimit = Integer.getInteger("NameVersionAttachmentIdentifier.MAX_LIMIT", 10);
    protected final long containerId;
    protected final PageManager pageManager;
    protected final AttachmentManager attachmentManager;
    protected int totalCountLatestAttachment;

    public AllLatestVersionAttachmentIdentifier(PageManager pageManager, AttachmentManager attachmentManager, long containerId) {
        this.pageManager = pageManager;
        this.attachmentManager = attachmentManager;
        this.containerId = containerId;
    }

    @Override
    public int getTotalCountLatestAttachment() {
        return this.totalCountLatestAttachment;
    }

    @Override
    public List<AttachmentIdentifier> getAttachmentIdentifiedList() {
        AbstractPage abstractPage = this.pageManager.getAbstractPage(this.containerId);
        if (abstractPage == null) {
            return Collections.EMPTY_LIST;
        }
        return this.getAttachmentIdentifiers(abstractPage);
    }

    protected List<AttachmentIdentifier> getAttachmentIdentifiers(AbstractPage abstractPage) {
        AttachmentManagerInternal attachmentManagerInternal = (AttachmentManagerInternal)this.attachmentManager;
        com.google.common.base.Predicate predicate = attachment -> this.getFilterCondition().test((Attachment)attachment);
        this.totalCountLatestAttachment = attachmentManagerInternal.countLatestVersionsOfAttachments(abstractPage);
        PageResponse<Attachment> pageResponse = attachmentManagerInternal.getFilteredAttachments(abstractPage, LimitedRequestImpl.create((int)0, (int)maxLimit, (int)maxLimit), (Predicate<? super Attachment>)predicate);
        List<AttachmentIdentifier> attachmentIdentifiers = pageResponse.getResults().stream().map(attachment -> new DefaultAttachmentIdentifier((Attachment)attachment)).collect(Collectors.toList());
        if (pageResponse.hasMore()) {
            attachmentIdentifiers.add(this.getNextBatch(attachmentIdentifiers.size()));
        }
        return attachmentIdentifiers;
    }

    protected AttachmentIdentifier getNextBatch(int previousOutputSize) {
        AllLatestVersionAttachmentIdentifier attachmentIdentifier = new AllLatestVersionAttachmentIdentifier(this.pageManager, this.attachmentManager, this.containerId);
        attachmentIdentifier.totalCountLatestAttachment = this.totalCountLatestAttachment;
        return attachmentIdentifier;
    }

    protected Predicate<Attachment> getFilterCondition() {
        return attachment -> true;
    }
}

