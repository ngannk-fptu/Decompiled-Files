/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.retention;

import com.atlassian.confluence.api.model.retention.RetentionRule;
import com.atlassian.confluence.api.model.retention.TrashRetentionRule;
import java.util.List;

public interface RetentionPolicy {
    public List<String> validate();

    public RetentionRule getPageVersionRule();

    public RetentionRule getAttachmentRetentionRule();

    public TrashRetentionRule getTrashRetentionRule();
}

