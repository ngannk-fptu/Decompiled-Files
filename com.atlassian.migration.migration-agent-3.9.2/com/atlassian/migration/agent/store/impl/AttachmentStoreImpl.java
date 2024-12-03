/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.entity.Attachment;
import com.atlassian.migration.agent.entity.AttachmentCheckMetadata;
import com.atlassian.migration.agent.store.AttachmentStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.impl.StatelessResults;
import java.util.List;

public class AttachmentStoreImpl
implements AttachmentStore {
    private static final String SPACE_KEY = "spaceKey";
    private static final String CLOUD_ID_KEY = "cloudId";
    @VisibleForTesting
    static final String ATTACHMENTS_TO_MIGRATE_QUERY = "select attachment from Attachment attachment left join attachment.migrations m on m.cloudId=:cloudId join attachment.space s on s.key=:spaceKey where m is null or m.version <> attachment.version";
    @VisibleForTesting
    static final String ATTACHMENTS_TO_CHECK_QUERY = "select new com.atlassian.migration.agent.entity.AttachmentCheckMetadata(a.id, a.version, a.container.id, a.previousVersion, a.title, s.id, s.key)from Attachment a join a.space s on s.key in :spaceKeys where a.container is not null";
    private final EntityManagerTemplate tmpl;

    public AttachmentStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public StatelessResults<Attachment> getAttachmentsToMigrate(String cloudId, String spaceKey) {
        return this.tmpl.getStatelessResults(Attachment.class, ATTACHMENTS_TO_MIGRATE_QUERY, query -> query.setParameter(SPACE_KEY, (Object)spaceKey).setParameter(CLOUD_ID_KEY, (Object)cloudId));
    }

    @Override
    public long getAttachmentsToMigrateSize(String cloudId, String spaceKey) {
        String query = String.format("select sum(property.longval) from ContentProperty property where property.content in (%s) and property.name = 'FILESIZE'", ATTACHMENTS_TO_MIGRATE_QUERY);
        Long size = this.tmpl.query(Long.class, query).param(SPACE_KEY, (Object)spaceKey).param(CLOUD_ID_KEY, (Object)cloudId).single();
        return size == null ? 0L : size;
    }

    @Override
    public long countAttachmentsBySpaceKey(String spaceKey) {
        String query = "select count(attachment) from Attachment attachment join attachment.space s on s.key=:spaceKey";
        Long size = this.tmpl.query(Long.class, query).param(SPACE_KEY, (Object)spaceKey).single();
        return size == null ? 0L : size;
    }

    @Override
    public StatelessResults<AttachmentCheckMetadata> getAttachmentsToCheck(List<String> spaceKeys) {
        return this.tmpl.getStatelessResults(AttachmentCheckMetadata.class, ATTACHMENTS_TO_CHECK_QUERY, query -> query.setParameter("spaceKeys", (Object)spaceKeys));
    }
}

