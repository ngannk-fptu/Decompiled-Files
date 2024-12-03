/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.ContainerAttachmentIdentifier;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.identifiers.DefaultAttachmentIdentifier;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IdListAttachmentIdentifier
implements ContainerAttachmentIdentifier {
    private static int maxLimit = Integer.getInteger("IdListAttachmentIdentifier.MAX_LIMIT", 10);
    private List<Long> ids;
    private int total;
    protected AttachmentManager attachmentManager;

    public IdListAttachmentIdentifier(List<Long> ids, AttachmentManager attachmentManager) {
        this.ids = ids;
        this.total = ids != null ? ids.size() : 0;
        this.attachmentManager = attachmentManager;
    }

    private IdListAttachmentIdentifier(List<Long> ids, int total, AttachmentManager attachmentManager) {
        this.ids = ids;
        this.total = total;
        this.attachmentManager = attachmentManager;
    }

    @Override
    public List<AttachmentIdentifier> getAttachmentIdentifiedList() {
        int upperLimit = Math.min(maxLimit, this.ids.size());
        if (upperLimit == 0) {
            return Collections.emptyList();
        }
        List<Long> subList = this.ids.subList(0, upperLimit);
        List<Attachment> attachmentList = this.attachmentManager.getAttachments(subList);
        IdListAttachmentIdentifier nextIdentifier = new IdListAttachmentIdentifier(this.ids.subList(upperLimit, this.ids.size()), this.total, this.attachmentManager);
        List<AttachmentIdentifier> resultList = attachmentList.stream().map(DefaultAttachmentIdentifier::new).collect(Collectors.toList());
        resultList.add(nextIdentifier);
        return resultList;
    }

    @Override
    public int getTotalCountLatestAttachment() {
        return this.total;
    }
}

