/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.setup.BootstrapManager
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.attachment;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.entity.AttachmentCheckMetadata;
import java.io.File;
import java.nio.file.FileSystems;
import java.util.Optional;
import java.util.function.UnaryOperator;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AttachmentPathService {
    static final String PATH_DELIMITER = FileSystems.getDefault().getSeparator();
    static final String VER_OO3 = "ver003";
    static final String V4 = "v4";
    static final String S3_BASE_PATH_TEMPLATE = "S3://%s/confluence/attachments/v4";
    static final String S3_BUCKET_NAME_PROPERTY_NAME = "confluence.filestore.attachments.s3.bucket.name";
    private final BootstrapManager bootstrapManager;
    private final UnaryOperator<String> environmentAccessor;

    @VisibleForTesting
    AttachmentPathService(BootstrapManager bootstrapManager, UnaryOperator<String> environmentAccessor) {
        this.bootstrapManager = bootstrapManager;
        this.environmentAccessor = environmentAccessor;
    }

    public AttachmentPathService(BootstrapManager bootstrapManager) {
        this(bootstrapManager, System::getenv);
    }

    public String getAttachmentFilePath(AttachmentCheckMetadata attachment) {
        ApplicationConfiguration applicationConfiguration = this.bootstrapManager.getApplicationConfig();
        if (this.isS3Enabled(applicationConfiguration)) {
            return this.getS3AttachmentFilePath(attachment, applicationConfiguration);
        }
        if (this.isFSV4Enabled()) {
            return this.getV4AttachmentFilePath(attachment);
        }
        return this.getV3AttachmentFilePath(attachment);
    }

    private String getV3AttachmentFilePath(AttachmentCheckMetadata attachment) {
        StringBuilder sb = new StringBuilder(this.getV3BasePath());
        sb.append(this.directoryPath(attachment.getSpaceId()));
        sb.append(this.directoryPath(attachment.getContainerId()));
        sb.append(this.attachmentPath(attachment));
        sb.toString();
        return sb.toString();
    }

    private String attachmentPath(AttachmentCheckMetadata attachment) {
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_DELIMITER);
        if (attachment.getPreviousVersion() != null) {
            sb.append(attachment.getPreviousVersion());
        } else {
            sb.append(attachment.getId());
        }
        sb.append(PATH_DELIMITER);
        sb.append(attachment.getVersion());
        return sb.toString();
    }

    private String directoryPath(long entityId) {
        return PATH_DELIMITER + entityId % 250L + PATH_DELIMITER + (entityId - entityId % 1000L) / 1000L % 250L + PATH_DELIMITER + entityId;
    }

    private String getS3AttachmentFilePath(AttachmentCheckMetadata attachment, ApplicationConfiguration applicationConfiguration) {
        return this.getS3BasePath(applicationConfiguration) + PATH_DELIMITER + this.getV4RelativePathPath(attachment);
    }

    private String getV4AttachmentFilePath(AttachmentCheckMetadata attachment) {
        return this.getV4BasePath() + PATH_DELIMITER + this.getV4RelativePathPath(attachment);
    }

    private String getV4RelativePathPath(AttachmentCheckMetadata attachment) {
        long latestAttachmentId = attachment.getPreviousVersion() == null ? attachment.getId() : attachment.getPreviousVersion().longValue();
        long domain = latestAttachmentId % 65535L;
        long folder1 = domain % 256L;
        long folder2 = domain / 256L;
        String fileName = String.format("%s.%s", latestAttachmentId, attachment.getVersion());
        return folder1 + PATH_DELIMITER + folder2 + PATH_DELIMITER + latestAttachmentId + PATH_DELIMITER + fileName;
    }

    private String getS3BasePath(ApplicationConfiguration appConfig) {
        return String.format(S3_BASE_PATH_TEMPLATE, this.getS3Bucket(appConfig).orElseThrow(() -> new IllegalArgumentException("Confluence is not configured to use s3 for attachments.")));
    }

    private String getV4BasePath() {
        return this.bootstrapManager.getFilePathProperty("attachments.dir") + PATH_DELIMITER + V4;
    }

    private String getV3BasePath() {
        return this.bootstrapManager.getFilePathProperty("attachments.dir") + PATH_DELIMITER + VER_OO3;
    }

    public boolean isFSV4Enabled() {
        return new File(this.getV4BasePath()).exists();
    }

    public Optional<String> getS3Bucket(ApplicationConfiguration appConfig) {
        String value = System.getProperty(S3_BUCKET_NAME_PROPERTY_NAME);
        if (value == null) {
            String envPropertyName = S3_BUCKET_NAME_PROPERTY_NAME.toUpperCase().replace(".", "_");
            value = (String)this.environmentAccessor.apply(envPropertyName);
        }
        if (value == null) {
            value = (String)appConfig.getProperty((Object)S3_BUCKET_NAME_PROPERTY_NAME);
        }
        return Optional.ofNullable(value);
    }

    public boolean isS3Enabled(ApplicationConfiguration appConfig) {
        return this.getS3Bucket(appConfig).isPresent();
    }
}

