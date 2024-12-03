/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.attachment;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class AttachmentMigrationChecker {
    private static final Logger log = ContextLoggerFactory.getLogger(AttachmentMigrationChecker.class);
    private final BootstrapManager bootstrapManager;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public AttachmentMigrationChecker(BootstrapManager bootstrapManager, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.bootstrapManager = bootstrapManager;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    public boolean isAttachmentsDirectoryReadable() {
        String attachmentsDir = this.getAttachmentsDirectory();
        Path filePath = Paths.get(attachmentsDir, new String[0]);
        boolean isReadable = Files.isReadable(filePath);
        log.info("Attachments Directory: {} is readable: {}", (Object)attachmentsDir, (Object)isReadable);
        return isReadable;
    }

    public boolean skipAttachmentUpload() {
        return this.migrationDarkFeaturesManager.skipAttachmentUploadEnabled();
    }

    public String getAttachmentsDirectory() {
        return this.bootstrapManager.getFilePathProperty("attachments.dir");
    }
}

