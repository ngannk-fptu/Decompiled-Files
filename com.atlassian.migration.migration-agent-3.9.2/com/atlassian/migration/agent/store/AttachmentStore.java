/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.Attachment;
import com.atlassian.migration.agent.entity.AttachmentCheckMetadata;
import com.atlassian.migration.agent.store.jpa.impl.StatelessResults;
import java.util.List;

public interface AttachmentStore {
    public StatelessResults<Attachment> getAttachmentsToMigrate(String var1, String var2);

    public long getAttachmentsToMigrateSize(String var1, String var2);

    public long countAttachmentsBySpaceKey(String var1);

    public StatelessResults<AttachmentCheckMetadata> getAttachmentsToCheck(List<String> var1);
}

