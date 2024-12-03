/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.entity.Attachment;
import com.atlassian.migration.agent.entity.AttachmentMigration;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.service.AttachmentService;
import com.atlassian.migration.agent.service.SpaceAttachmentCount;
import com.atlassian.migration.agent.service.SpaceAttachments;
import com.atlassian.migration.agent.store.AttachmentMigrationStore;
import com.atlassian.migration.agent.store.AttachmentStore;
import com.atlassian.migration.agent.store.jpa.impl.StatelessResults;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.util.Optional;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAttachmentService
implements AttachmentService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DefaultAttachmentService.class);
    private final PluginTransactionTemplate ptx;
    private final AttachmentMigrationStore attachmentMigrationStore;
    private final AttachmentStore attachmentStore;

    public DefaultAttachmentService(PluginTransactionTemplate ptx, AttachmentMigrationStore attachmentMigrationStore, AttachmentStore attachmentStore) {
        this.ptx = ptx;
        this.attachmentMigrationStore = attachmentMigrationStore;
        this.attachmentStore = attachmentStore;
    }

    @Override
    public SpaceAttachments getAttachmentsToMigrate(String spaceKey, CloudSite cloudSite) {
        String cloudId = cloudSite.getCloudId();
        long attachmentsSizeToUpload = this.getAttachmentsToMigrateSize(cloudId, spaceKey);
        log.info("For space {} migrationSize to upload {}", (Object)spaceKey, (Object)attachmentsSizeToUpload);
        return new SpaceAttachments(this.listAttachmentsToMigrate(cloudId, spaceKey), attachmentsSizeToUpload);
    }

    @Override
    public void logSuccessfulAttachmentMigration(CloudSite cloudSite, Attachment attachment, String mediaId) {
        this.ptx.write(() -> this.updateAttachmentMigration(cloudSite.getCloudId(), attachment.getId(), mediaId, attachment.getVersion()));
    }

    @Override
    public void logFailedAttachmentMigration(CloudSite cloudSite, Attachment attachment) {
        log.warn("Failed attachment migration for attachment {} to cloud {}", (Object)attachment.getId(), (Object)cloudSite.getCloudId());
        this.ptx.write(() -> this.updateAttachmentMigration(cloudSite.getCloudId(), attachment.getId(), null, attachment.getVersion()));
    }

    public void updateAttachmentMigration(String cloudId, long attachmentId, String mediaId, int attachmentVersion) {
        Optional<AttachmentMigration> attachmentMigrations = this.attachmentMigrationStore.findMigration(attachmentId, cloudId);
        if (!attachmentMigrations.isPresent() || attachmentMigrations.get().getMediaId() == null || mediaId != null) {
            AttachmentMigration attachmentMigration = attachmentMigrations.isPresent() ? attachmentMigrations.get() : new AttachmentMigration();
            attachmentMigration.setCloudId(cloudId);
            attachmentMigration.setAttachmentId(attachmentId);
            attachmentMigration.setMediaId(mediaId);
            attachmentMigration.setVersion(attachmentVersion);
            this.attachmentMigrationStore.saveMigration(attachmentMigration);
        }
    }

    @Override
    public int deleteAttachmentMigrationTrackingByCloudSite(CloudSite cloudSite) {
        return this.ptx.write(() -> this.attachmentMigrationStore.deleteMigrationsByCloudId(cloudSite.getCloudId()));
    }

    @Override
    public SpaceAttachmentCount getAttachmentsCountInSpaceAndMigrated(String spaceKey, String cloudId, long spaceAttachmentCount) {
        return this.ptx.read(() -> {
            long migAttachmentCount = this.attachmentMigrationStore.countRetrievedAttachmentMigrationsBySpaceKeyAndCloudId(spaceKey, cloudId);
            long unRetrievableAttachmentCount = this.attachmentMigrationStore.countUnRetrievableAttachmentMigrationsBySpaceKeyAndCloudId(spaceKey, cloudId);
            return new SpaceAttachmentCount(cloudId, spaceKey, spaceAttachmentCount, migAttachmentCount, unRetrievableAttachmentCount);
        });
    }

    @Override
    public long countAlreadyMigratedAttachmentsBySpaceKeyAndCloudId(String spaceKey, String cloudId) {
        return this.ptx.read(() -> this.attachmentMigrationStore.countAlreadyMigratedAttachmentsBySpaceKeyAndCloudId(spaceKey, cloudId));
    }

    @Override
    public long countAttachmentsInSpace(String spaceKey) {
        return this.ptx.read(() -> this.attachmentStore.countAttachmentsBySpaceKey(spaceKey));
    }

    private StatelessResults<Attachment> listAttachmentsToMigrate(String cloudId, String spaceKey) {
        return this.ptx.read(() -> this.attachmentStore.getAttachmentsToMigrate(cloudId, spaceKey));
    }

    private long getAttachmentsToMigrateSize(String cloudId, String spaceKey) {
        return this.ptx.read(() -> this.attachmentStore.getAttachmentsToMigrateSize(cloudId, spaceKey));
    }
}

