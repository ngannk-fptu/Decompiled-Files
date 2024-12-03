/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.AttachmentMigration;
import com.atlassian.migration.agent.store.AttachmentMigrationStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Optional;

public class AttachmentMigrationStoreImpl
implements AttachmentMigrationStore {
    private static final String CLOUD_ID_KEY = "cloudId";
    private static final String SPACE_KEY = "spaceKey";
    private final EntityManagerTemplate tmpl;

    public AttachmentMigrationStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public void saveMigration(AttachmentMigration attachmentMigration) {
        this.tmpl.saveOrUpdate(attachmentMigration);
        this.tmpl.flush();
    }

    @Override
    public Optional<AttachmentMigration> findMigration(long attachmentId, String cloudId) {
        String query = "select a from AttachmentMigration a where a.cloudId = :cloudId and a.attachmentId = :attachmentId";
        return this.tmpl.query(AttachmentMigration.class, query).param(CLOUD_ID_KEY, (Object)cloudId).param("attachmentId", (Object)attachmentId).first();
    }

    @Override
    public int deleteMigrationsByCloudId(String cloudId) {
        String query = "delete from AttachmentMigration m where m.cloudId=:cloudId";
        return this.tmpl.query(query).param(CLOUD_ID_KEY, (Object)cloudId).update();
    }

    @Override
    public long countRetrievedAttachmentMigrationsBySpaceKeyAndCloudId(String spaceKey, String cloudId) {
        String query = "select count(am) from AttachmentMigration am join am.attachment a join a.space s ON s.key = :spaceKey where am.cloudSite.cloudId = :cloudId and am.mediaId is NOT NULL";
        return this.countMigAttachmentQuery(spaceKey, cloudId, query);
    }

    @Override
    public long countUnRetrievableAttachmentMigrationsBySpaceKeyAndCloudId(String spaceKey, String cloudId) {
        String query = "select count(am) from AttachmentMigration am join am.attachment a join a.space s ON s.key = :spaceKey where am.cloudSite.cloudId = :cloudId and am.mediaId is NULL";
        return this.countMigAttachmentQuery(spaceKey, cloudId, query);
    }

    @Override
    public long countAlreadyMigratedAttachmentsBySpaceKeyAndCloudId(String spaceKey, String cloudId) {
        String query = "select count(am) from AttachmentMigration am join am.attachment a ON a.version = am.version join a.space s ON s.key = :spaceKey where am.cloudSite.cloudId = :cloudId";
        return this.countMigAttachmentQuery(spaceKey, cloudId, query);
    }

    private long countMigAttachmentQuery(String spaceKey, String cloudId, String query) {
        Long size = this.tmpl.query(Long.class, query).param(CLOUD_ID_KEY, (Object)cloudId).param(SPACE_KEY, (Object)spaceKey).single();
        return size == null ? 0L : size;
    }
}

