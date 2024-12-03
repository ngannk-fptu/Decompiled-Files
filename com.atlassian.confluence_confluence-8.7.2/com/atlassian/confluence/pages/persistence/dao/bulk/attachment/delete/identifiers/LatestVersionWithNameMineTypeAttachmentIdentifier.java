/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentDeleteOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers.AllLatestVersionAttachmentIdentifier;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;

public class LatestVersionWithNameMineTypeAttachmentIdentifier
extends AllLatestVersionAttachmentIdentifier {
    private List<AttachmentDeleteOptions.AttachmentRef> attachmentRefs;

    public LatestVersionWithNameMineTypeAttachmentIdentifier(PageManager pageManager, AttachmentManager attachmentManager, long containerId, List<AttachmentDeleteOptions.AttachmentRef> attachmentRefs) {
        super(pageManager, attachmentManager, containerId);
        Objects.requireNonNull(attachmentRefs, "List attachment ref must not be null");
        this.attachmentRefs = attachmentRefs;
    }

    @Override
    public int getTotalCountLatestAttachment() {
        return this.attachmentRefs != null ? this.attachmentRefs.size() : 0;
    }

    @Override
    protected Predicate<Attachment> getFilterCondition() {
        return attachment -> {
            String fileName = attachment.getFileName();
            String mineType = attachment.getMediaType();
            return this.attachmentRefs.stream().anyMatch(attachmentRef -> {
                boolean isCollect = attachmentRef.getAttachmentName().equals(fileName);
                if (StringUtils.isNotEmpty((CharSequence)attachmentRef.getMimeType())) {
                    isCollect = isCollect && attachmentRef.getMimeType().equals(mineType);
                }
                return isCollect;
            });
        };
    }

    @Override
    protected AttachmentIdentifier getNextBatch(int previousOutputSize) {
        return new LatestVersionWithNameMineTypeAttachmentIdentifier(this.pageManager, this.attachmentManager, this.containerId, this.attachmentRefs);
    }
}

