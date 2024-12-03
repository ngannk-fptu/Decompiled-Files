/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentDeleteOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.ContainerAttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers.DefaultAttachmentIdentifier;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AttachmentRefIdentifier
implements ContainerAttachmentIdentifier {
    private final long containerId;
    private final PageManager pageManager;
    private final AttachmentManager attachmentManager;
    private final List<AttachmentDeleteOptions.AttachmentRef> attachmentRefs;

    public AttachmentRefIdentifier(long containerId, PageManager pageManager, AttachmentManager attachmentManager, List<AttachmentDeleteOptions.AttachmentRef> attachmentRefs) {
        Objects.requireNonNull(pageManager, "Should have PageManager");
        Objects.requireNonNull(attachmentManager, "Should have AttachmentManager");
        Objects.requireNonNull(attachmentRefs, "Should have AttachmentRefs");
        this.containerId = containerId;
        this.pageManager = pageManager;
        this.attachmentManager = attachmentManager;
        this.attachmentRefs = attachmentRefs;
    }

    @Override
    public List<AttachmentIdentifier> getAttachmentIdentifiedList() {
        AbstractPage abstractPage = this.pageManager.getAbstractPage(this.containerId);
        if (abstractPage == null) {
            return Collections.EMPTY_LIST;
        }
        return this.attachmentRefs.stream().flatMap(attachmentRef -> {
            Attachment attachment = this.attachmentManager.getAttachment(abstractPage, attachmentRef.getAttachmentName(), attachmentRef.getAttachmentVersion());
            return attachment == null ? Stream.empty() : Stream.of(attachment);
        }).map(attachment -> new DefaultAttachmentIdentifier((Attachment)attachment)).collect(Collectors.toList());
    }

    @Override
    public int getTotalCountLatestAttachment() {
        return this.attachmentRefs != null ? this.attachmentRefs.size() : 0;
    }
}

