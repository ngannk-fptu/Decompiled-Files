/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.confluence.internal.index.attachment.AttachmentStatus;
import java.util.Optional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface AttachmentStatusManager {
    public static final String ATTACHMENT_STATUS = "_atl_AttachmentStatus";

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void updateAttachmentStatus(long var1, AttachmentStatus var3);

    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public Optional<AttachmentStatus> getAttachmentStatus(long var1);
}

