/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete;

import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentIdentifier;
import java.util.List;

public interface ContainerAttachmentIdentifier
extends AttachmentIdentifier {
    public List<AttachmentIdentifier> getAttachmentIdentifiedList();

    public int getTotalCountLatestAttachment();
}

