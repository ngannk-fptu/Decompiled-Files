/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.attachment;

import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.Attachment;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.media.MediaClientToken;
import com.atlassian.migration.agent.media.MediaClientTokenSupplier;
import com.atlassian.migration.agent.media.MediaUploadException;
import com.atlassian.migration.agent.service.AttachmentService;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.SpaceAttachmentCount;
import com.atlassian.migration.agent.service.StatisticsService;
import com.atlassian.migration.agent.service.UploadState;
import com.atlassian.migration.agent.service.check.CheckOverrideService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.execution.SpaceBoundStepExecutor;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrationAnalyticsService;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrationChecker;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrator;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentsToMigrateConsumer;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentsUploadTracker;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import io.atlassian.util.concurrent.ThreadFactories;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterators;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class AttachmentMigrationExecutor
implements SpaceBoundStepExecutor {
    private static final String PRODUCER_CONSUMER_THROWABLE = "PRODUCER_CONSUMER_THROWABLE";
    private static final String MEDIA_UPLOAD_THROWABLE = "MEDIA_UPLOAD_THROWABLE";
    private static final Logger log = ContextLoggerFactory.getLogger(AttachmentMigrationExecutor.class);
    public static final String SUCCESSFULLY_MIGRATED_BYTES_PROGRESS_PROPERTIES = "successfullyMigratedBytes";
    public static final String TOTAL_SPACE_ATTACHMENT_SIZE_PROGRESS_PROPERTIES = "totalSpaceAttachmentSize";
    private final AttachmentMigrator attachmentMigrator;
    private final AttachmentService attachmentService;
    private final CloudSiteService cloudSiteService;
    private final MigrationAgentConfiguration configuration;
    private final StatisticsService statisticsService;
    private final Supplier<ExecutorService> executorServiceSupplier;
    private final ProgressTracker progressTracker;
    private final MediaClientTokenSupplier mediaClientTokenSupplier;
    private final MigrationDarkFeaturesManager darkFeaturesManager;
    private final PluginTransactionTemplate ptx;
    private final StepStore stepStore;
    private final Supplier<Instant> instantSupplier;
    private final AttachmentMigrationChecker attachmentMigrationChecker;
    private final AttachmentMigrationAnalyticsService attachmentMigrationAnalyticsService;
    private final SpaceManager spaceManager;
    private final CheckOverrideService checkOverrideService;

    public AttachmentMigrationExecutor(ProgressTracker progressTracker, AttachmentService attachmentService, AttachmentMigrator attachmentMigrator, CloudSiteService cloudSiteService, MigrationAgentConfiguration migrationAgentConfiguration, StatisticsService statisticsService, MediaClientTokenSupplier mediaClientTokenSupplier, PluginTransactionTemplate ptx, StepStore stepStore, AttachmentMigrationChecker attachmentMigrationChecker, AttachmentMigrationAnalyticsService attachmentMigrationAnalyticsService, MigrationDarkFeaturesManager darkFeaturesManager, SpaceManager spaceManager, CheckOverrideService checkOverrideService) {
        this(progressTracker, attachmentService, attachmentMigrator, cloudSiteService, migrationAgentConfiguration, statisticsService, mediaClientTokenSupplier, ptx, () -> Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)AttachmentMigrationExecutor.class.getName())), stepStore, Instant::now, attachmentMigrationChecker, attachmentMigrationAnalyticsService, darkFeaturesManager, spaceManager, checkOverrideService);
    }

    @VisibleForTesting
    AttachmentMigrationExecutor(ProgressTracker progressTracker, AttachmentService attachmentService, AttachmentMigrator attachmentMigrator, CloudSiteService cloudSiteService, MigrationAgentConfiguration migrationAgentConfiguration, StatisticsService statisticsService, MediaClientTokenSupplier mediaClientTokenSupplier, PluginTransactionTemplate ptx, Supplier<ExecutorService> executorServiceSupplier, StepStore stepStore, Supplier<Instant> instantSupplier, AttachmentMigrationChecker attachmentMigrationChecker, AttachmentMigrationAnalyticsService attachmentMigrationAnalyticsService, MigrationDarkFeaturesManager darkFeaturesManager, SpaceManager spaceManager, CheckOverrideService checkOverrideService) {
        this.progressTracker = progressTracker;
        this.attachmentService = attachmentService;
        this.attachmentMigrator = attachmentMigrator;
        this.cloudSiteService = cloudSiteService;
        this.configuration = migrationAgentConfiguration;
        this.statisticsService = statisticsService;
        this.mediaClientTokenSupplier = mediaClientTokenSupplier;
        this.ptx = ptx;
        this.executorServiceSupplier = executorServiceSupplier;
        this.stepStore = stepStore;
        this.instantSupplier = instantSupplier;
        this.attachmentMigrationChecker = attachmentMigrationChecker;
        this.attachmentMigrationAnalyticsService = attachmentMigrationAnalyticsService;
        this.darkFeaturesManager = darkFeaturesManager;
        this.spaceManager = spaceManager;
        this.checkOverrideService = checkOverrideService;
        log.info("Initialized attachment migration executor with concurrency = {} and batch size = {}.", (Object)migrationAgentConfiguration.getAttachmentUploadConcurrency(), (Object)migrationAgentConfiguration.getAttachmentUploadBatchSize());
    }

    /*
     * Exception decompiling
     */
    @VisibleForTesting
    StepResult doAttachmentMigration(Step step, String spaceKey) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 5 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @VisibleForTesting
    synchronized void updateProgress(String stepId, UploadState uploadState) {
        Map<String, Object> uploadProgressProperties = AttachmentMigrationExecutor.getUploadProgressProperties(uploadState);
        this.progressTracker.progress(stepId, uploadState.percentOfProgress, AttachmentMigrationExecutor.getProgressMessage(uploadState), StepType.ATTACHMENT_UPLOAD.getDetailedStatus(), uploadProgressProperties);
    }

    private static Map<String, Object> getUploadProgressProperties(UploadState uploadState) {
        return ImmutableMap.builder().put((Object)SUCCESSFULLY_MIGRATED_BYTES_PROGRESS_PROPERTIES, (Object)uploadState.uploadedBytes).put((Object)TOTAL_SPACE_ATTACHMENT_SIZE_PROGRESS_PROPERTIES, (Object)uploadState.totalBytesToUpload).build();
    }

    private void logAndBuildAnalyticsPostMigration(String spaceKey, CloudSite cloudSite, String migrationId, long totalCountOfAttachments, UploadState uploadState, Step step, long startTime) {
        SpaceAttachmentCount spaceAttachmentCount = this.attachmentService.getAttachmentsCountInSpaceAndMigrated(spaceKey, cloudSite.getCloudId(), totalCountOfAttachments);
        log.info("Migration {} Attachment count for spaceKey: {}, contentAttachmentCount: {}, retrievedMigAttachmentCount: {}, unRetrievableMigAttachmentCount: {}", new Object[]{migrationId, spaceKey, spaceAttachmentCount.contentAttachmentCount, spaceAttachmentCount.retrievedMigAttachmentCount, spaceAttachmentCount.unRetrievableMigAttachmentCount});
        this.attachmentMigrationAnalyticsService.buildAttachmentMigrationEventSuccessful(this.instantSupplier.get().toEpochMilli() - startTime, spaceAttachmentCount, uploadState, step);
    }

    private StepResult failedStepResult(MigrationErrorCode errorCode, String reason, Optional<Throwable> optionalThrowable, String migrationId, String cloudId, Step step, String spaceKey) {
        log.error("Migration {} Attachment upload failed with {} reason {}", new Object[]{migrationId, errorCode.getCode(), reason});
        return optionalThrowable.map(throwable -> {
            this.attachmentMigrationAnalyticsService.buildAndSaveAnalyticEventsWhenStepFails(errorCode, throwable.getMessage(), migrationId, cloudId, step, spaceKey);
            return StepResult.failed(reason, throwable);
        }).orElseGet(() -> {
            this.attachmentMigrationAnalyticsService.buildAndSaveAnalyticEventsWhenStepFails(errorCode, reason, migrationId, cloudId, step, spaceKey);
            return StepResult.failed(reason);
        });
    }

    private static StepResult getStepResult(UploadState uploadState) {
        return StepResult.succeeded(AttachmentMigrationExecutor.getProgressMessage(uploadState));
    }

    private static String getProgressMessage(UploadState uploadState) {
        return String.format("Migrated %s of %s attachments", FileUtils.byteCountToDisplaySize((long)uploadState.uploadedBytes), FileUtils.byteCountToDisplaySize((long)uploadState.totalBytesToUpload));
    }

    private CloudSite validateAndUpdateMediaClientId(CloudSite cloudSite) {
        return this.ptx.write(() -> {
            String containerToken = cloudSite.getContainerToken();
            MediaClientToken mediaToken = this.mediaClientTokenSupplier.getRefreshedToken(containerToken);
            String currentMediaClientId = mediaToken.getClientId();
            if (currentMediaClientId.equals(cloudSite.getMediaClientId())) {
                log.info("MediaClientId is valid. Proceed with attachments migration.");
                return cloudSite;
            }
            log.info("Current mediaClientId is not valid. Going to clean attachment migration records for the Cloud Site, which means that any previously uploaded attachments will be re-uploaded.");
            int deletedCount = this.attachmentService.deleteAttachmentMigrationTrackingByCloudSite(cloudSite);
            log.info("Deleted {} attachment migration records.", (Object)deletedCount);
            return this.cloudSiteService.updateMediaClientId(cloudSite.getCloudId(), currentMediaClientId);
        });
    }

    @Override
    public StepType getStepType() {
        return StepType.ATTACHMENT_UPLOAD;
    }

    @Override
    public StepResult runStep(String stepId) {
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        return this.wrapStepResultSupplier(this.attachmentMigrationAnalyticsService.getAnalyticsEventBuilder(), this.attachmentMigrationAnalyticsService.getAnalyticsEventService(), step, step.getConfig(), this.spaceManager, () -> this.doAttachmentMigration(step, step.getConfig()));
    }

    private void cancelAttachmentMigrationConsumers(CompletableFuture[] consumers) {
        for (CompletableFuture consumer : consumers) {
            consumer.cancel(true);
        }
    }

    private Optional<Stream<Attachment>> getNonEmptyStream(Stream<Attachment> stream) {
        Iterator iterator = stream.iterator();
        if (iterator.hasNext()) {
            return Optional.of(StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 16), false));
        }
        return Optional.empty();
    }

    private static /* synthetic */ CompletableFuture[] lambda$doAttachmentMigration$7(int x$0) {
        return new CompletableFuture[x$0];
    }

    private static /* synthetic */ CompletableFuture lambda$doAttachmentMigration$6(AtomicBoolean failedToProduceOrConsumeAttachment, Map attachmentMigrationThrowableMap, AtomicBoolean mediaUploadFailed, CompletableFuture future) {
        return future.handle((result, throwable) -> {
            if (throwable != null) {
                failedToProduceOrConsumeAttachment.set(true);
                attachmentMigrationThrowableMap.put(PRODUCER_CONSUMER_THROWABLE, throwable);
                log.error("Attachment uploader finished with exception", throwable);
                if (throwable.getCause() instanceof MediaUploadException) {
                    mediaUploadFailed.set(true);
                    attachmentMigrationThrowableMap.put(MEDIA_UPLOAD_THROWABLE, throwable);
                }
            }
            return null;
        });
    }

    private static /* synthetic */ CompletableFuture lambda$doAttachmentMigration$4(ExecutorService executor, AttachmentsToMigrateConsumer consumer) {
        return CompletableFuture.runAsync(consumer, executor);
    }

    private /* synthetic */ AttachmentsToMigrateConsumer lambda$doAttachmentMigration$3(CloudSite cloudSite, BlockingQueue buffer, AttachmentsUploadTracker uploadTracker, AtomicBoolean allAttachmentsProduced, AtomicBoolean failedToProduceOrConsumeAttachment, String migrationId, int ignored) {
        return new AttachmentsToMigrateConsumer(cloudSite, buffer, this.attachmentMigrator, uploadTracker, allAttachmentsProduced::get, failedToProduceOrConsumeAttachment::get, migrationId);
    }

    private static /* synthetic */ Object lambda$doAttachmentMigration$2(AtomicBoolean failedToProduceOrConsumeAttachment, Map attachmentMigrationThrowableMap, AtomicBoolean allAttachmentsProduced, String spaceKey, Void result, Throwable throwable) {
        if (throwable != null) {
            failedToProduceOrConsumeAttachment.set(true);
            attachmentMigrationThrowableMap.put(PRODUCER_CONSUMER_THROWABLE, throwable);
            log.error("Attachments reader finished with exception", throwable);
        } else {
            allAttachmentsProduced.set(true);
            log.info("All attachments to migrate are read from {}", (Object)spaceKey);
        }
        return null;
    }

    private /* synthetic */ void lambda$doAttachmentMigration$1(String stepId, UploadState uploadState) {
        this.updateProgress(stepId, uploadState);
    }
}

