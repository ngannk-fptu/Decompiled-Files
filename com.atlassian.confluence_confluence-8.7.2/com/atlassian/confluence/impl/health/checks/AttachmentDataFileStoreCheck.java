/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.johnson.event.Event
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.health.HealthCheckMessage;
import com.atlassian.confluence.impl.health.HealthCheckTemplate;
import com.atlassian.confluence.impl.pages.attachments.AttachmentStorageChecker;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.DelegatingAttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.objectstorage.ObjectStorageAttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.objectstorage.S3ConfigFactory;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.johnson.event.Event;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentDataFileStoreCheck
extends HealthCheckTemplate {
    private static final Logger log = LoggerFactory.getLogger(AttachmentDataFileStoreCheck.class);
    public static final String CONNECTIVITY_TEST_FAILED = "johnson.message.attachment.storage.s3.connectivity.test.failed";
    public static final String S3_DATA_CENTER_ONLY_FEATURE_TITLE = "johnson.message.attachment.storage.s3.data.center.only.feature.title";
    public static final String S3_DATA_CENTER_ONLY_FEATURE_MESSAGE = "johnson.message.attachment.storage.s3.data.center.only.feature.message";
    public static final String S3_CONFIG_DATA_MISSING_TITLE = "johnson.message.attachment.storage.s3.config.missing.title";
    public static final String S3_CONFIG_DATA_MISSING_MESSAGE = "johnson.message.attachment.storage.s3.config.missing.message";
    public static final String S3_CONFIGURATION = "johnson.message.attachment.storage.s3.sub.title";
    private final Supplier<I18NBean> i18NBeanSupplier;
    private final ApplicationConfiguration appConfig;
    private final Supplier<AttachmentDataFileSystem> attachmentDataFileSystemSupplier;
    private final ClusterConfigurationHelperInternal clusterConfigurationHelper;
    private final LicenseService licenseService;

    public AttachmentDataFileStoreCheck(Supplier<I18NBean> i18NBeanSupplier, ApplicationConfiguration appConfig, Supplier<AttachmentDataFileSystem> attachmentDataFileSystemSupplier, ClusterConfigurationHelperInternal clusterConfigurationHelper, LicenseService licenseService) {
        super(Collections.emptyList());
        this.i18NBeanSupplier = i18NBeanSupplier;
        this.appConfig = appConfig;
        this.attachmentDataFileSystemSupplier = attachmentDataFileSystemSupplier;
        this.clusterConfigurationHelper = clusterConfigurationHelper;
        this.licenseService = licenseService;
    }

    @Override
    protected Set<LifecyclePhase> getApplicablePhases() {
        return Set.of(LifecyclePhase.PLUGIN_FRAMEWORK_STARTED);
    }

    @Override
    protected List<HealthCheckResult> doPerform() {
        if (this.s3BucketNameSupplied() && !this.s3BucketRegionSupplied() || !this.s3BucketNameSupplied() && this.s3BucketRegionSupplied()) {
            return this.renderS3misconfig(S3_CONFIG_DATA_MISSING_TITLE, S3_CONFIG_DATA_MISSING_MESSAGE);
        }
        if (this.s3BucketNameSupplied() && this.s3BucketRegionSupplied() && !this.licenseService.isLicensedForDataCenter()) {
            return this.renderS3misconfig(S3_DATA_CENTER_ONLY_FEATURE_TITLE, S3_DATA_CENTER_ONLY_FEATURE_MESSAGE);
        }
        String storageType = System.getProperty("AttachmentStorageType");
        if (storageType == null) {
            throw new IllegalStateException("Missing required system property: AttachmentStorageType");
        }
        if (!storageType.equals("s3.object.based.attachments.storage")) {
            return Collections.emptyList();
        }
        AttachmentDataFileSystem attachmentDataFileSystem = this.attachmentDataFileSystemSupplier.get();
        while (attachmentDataFileSystem instanceof DelegatingAttachmentDataFileSystem) {
            DelegatingAttachmentDataFileSystem delegatingAttachmentDataFileSystem = (DelegatingAttachmentDataFileSystem)attachmentDataFileSystem;
            attachmentDataFileSystem = delegatingAttachmentDataFileSystem.getDelegate();
        }
        ObjectStorageAttachmentDataFileSystem objectStorageAttachmentDataFileSystem = (ObjectStorageAttachmentDataFileSystem)attachmentDataFileSystem;
        ArrayList<HealthCheckResult> results = new ArrayList<HealthCheckResult>();
        results.addAll(this.checkConnectivity(objectStorageAttachmentDataFileSystem));
        return Collections.unmodifiableList(results);
    }

    private boolean s3BucketNameSupplied() {
        String bucketName = S3ConfigFactory.getConfigProperty(this.appConfig, this.clusterConfigurationHelper, "confluence.filestore.attachments.s3.bucket.name");
        return StringUtils.isNotBlank((CharSequence)bucketName);
    }

    private boolean s3BucketRegionSupplied() {
        String bucketName = S3ConfigFactory.getConfigProperty(this.appConfig, this.clusterConfigurationHelper, "confluence.filestore.attachments.s3.bucket.region");
        return StringUtils.isNotBlank((CharSequence)bucketName);
    }

    private List<HealthCheckResult> renderS3misconfig(String errorTitle, String errorMessage) {
        HealthCheckMessage.Builder builder = new HealthCheckMessage.Builder();
        builder.append(this.i18NBeanSupplier.get().getText(errorMessage));
        builder.lineBreak().lineBreak();
        builder.append(this.i18NBeanSupplier.get().getText(S3_CONFIGURATION)).lineBreak();
        builder.appendList(this.getS3ConfigProperties());
        HealthCheckMessage message = builder.build();
        String internationalMessage = this.i18NBeanSupplier.get().getText(errorTitle);
        Event event = new Event(JohnsonEventType.STARTUP.eventType(), internationalMessage, message.asHtml(), JohnsonEventLevel.ERROR.level());
        HealthCheckResult healthCheckResult = new HealthCheckResult(this, event, null, errorMessage, message.asText());
        return List.of(healthCheckResult);
    }

    private List<HealthCheckResult> checkConnectivity(ObjectStorageAttachmentDataFileSystem attachmentDataFileSystem) {
        try {
            AttachmentStorageChecker.testOperations(attachmentDataFileSystem.getRootDir());
            return Collections.emptyList();
        }
        catch (IOException exception) {
            HealthCheckMessage.Builder builder = new HealthCheckMessage.Builder();
            builder.append(exception.getMessage() != null ? exception.getMessage() : "Unknown Error");
            if (exception.getCause() != null) {
                builder.append(String.format(": %s", exception.getCause().getMessage()));
            }
            builder.lineBreak().lineBreak();
            builder.append(this.i18NBeanSupplier.get().getText(S3_CONFIGURATION)).lineBreak();
            builder.appendList(this.getS3ConfigProperties());
            log.error("AWS S3 connectivity error: {}", (Object)ExceptionUtils.getStackTrace((Throwable)exception));
            HealthCheckMessage message = builder.build();
            String internationalMessage = this.i18NBeanSupplier.get().getText(CONNECTIVITY_TEST_FAILED);
            Event event = new Event(JohnsonEventType.STARTUP.eventType(), internationalMessage, message.asHtml(), JohnsonEventLevel.FATAL.level());
            HealthCheckResult healthCheckResult = new HealthCheckResult(this, event, null, CONNECTIVITY_TEST_FAILED, message.asText());
            return List.of(healthCheckResult);
        }
    }

    private Iterable<String> getS3ConfigProperties() {
        ArrayList<String> properties = new ArrayList<String>(3);
        String keyValuePairFormat = "%s: %s";
        String bucketName = S3ConfigFactory.getConfigProperty(this.appConfig, this.clusterConfigurationHelper, "confluence.filestore.attachments.s3.bucket.name");
        String bucketRegion = S3ConfigFactory.getConfigProperty(this.appConfig, this.clusterConfigurationHelper, "confluence.filestore.attachments.s3.bucket.region");
        String endpointOverride = S3ConfigFactory.getConfigProperty(this.appConfig, this.clusterConfigurationHelper, "confluence.filestore.attachments.s3.endpoint.override");
        properties.add(String.format("%s: %s", "confluence.filestore.attachments.s3.bucket.name", bucketName));
        properties.add(String.format("%s: %s", "confluence.filestore.attachments.s3.bucket.region", bucketRegion));
        if (endpointOverride != null) {
            properties.add(String.format("%s: %s", "confluence.filestore.attachments.s3.endpoint.override", endpointOverride));
        }
        return properties;
    }
}

