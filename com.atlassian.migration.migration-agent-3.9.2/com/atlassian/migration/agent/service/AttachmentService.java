/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.agent.entity.Attachment;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.service.SpaceAttachmentCount;
import com.atlassian.migration.agent.service.SpaceAttachments;

public interface AttachmentService {
    public SpaceAttachments getAttachmentsToMigrate(String var1, CloudSite var2);

    public void logSuccessfulAttachmentMigration(CloudSite var1, Attachment var2, String var3);

    public void logFailedAttachmentMigration(CloudSite var1, Attachment var2);

    public int deleteAttachmentMigrationTrackingByCloudSite(CloudSite var1);

    public SpaceAttachmentCount getAttachmentsCountInSpaceAndMigrated(String var1, String var2, long var3);

    public long countAlreadyMigratedAttachmentsBySpaceKeyAndCloudId(String var1, String var2);

    public long countAttachmentsInSpace(String var1);
}

