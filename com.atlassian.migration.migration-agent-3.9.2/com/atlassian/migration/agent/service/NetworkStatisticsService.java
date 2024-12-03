/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.logging.LoggingContextAwareExecutorService;
import com.atlassian.migration.agent.media.MediaFileUploader;
import com.atlassian.migration.agent.media.MediaFileUploaderFactory;
import com.atlassian.migration.agent.media.MediaUploadException;
import com.atlassian.migration.agent.service.StatsStoringService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.util.concurrent.ThreadFactories;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class NetworkStatisticsService {
    private static final int TEST_FILE_SIZE_KB = 100000;
    protected static final int TEST_FILE_SIZE_BYTES = 102400000;
    private static final long UNCALCULATED_BANDWIDTH_KBPS = -1L;
    private static final Logger log = ContextLoggerFactory.getLogger(NetworkStatisticsService.class);
    private final ExecutorService executorService;
    private final Supplier<Instant> instantSupplier;
    private final StatsStoringService statsStoringService;
    private final MediaFileUploaderFactory mediaFileUploaderFactory;
    private final CloudSiteService cloudSiteService;

    public NetworkStatisticsService(StatsStoringService statsStoringService, MediaFileUploaderFactory mediaFileUploaderFactory, CloudSiteService cloudSiteService) {
        this(new LoggingContextAwareExecutorService(Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)NetworkStatisticsService.class.getName()))), Instant::now, statsStoringService, mediaFileUploaderFactory, cloudSiteService);
    }

    @VisibleForTesting
    NetworkStatisticsService(ExecutorService executorService, Supplier<Instant> instantSupplier, StatsStoringService statsStoringService, MediaFileUploaderFactory mediaFileUploaderFactory, CloudSiteService cloudSiteService) {
        this.executorService = executorService;
        this.instantSupplier = instantSupplier;
        this.statsStoringService = statsStoringService;
        this.mediaFileUploaderFactory = mediaFileUploaderFactory;
        this.cloudSiteService = cloudSiteService;
    }

    public static long getUncalculatedBandwidthKBPS() {
        return -1L;
    }

    @PreDestroy
    public void cleanup() {
        this.executorService.shutdownNow();
    }

    public void measureConnectionStats() {
        this.executorService.execute(() -> this.statsStoringService.storeBandwidthKBS(this.measureBandwidthKBPS()));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public long measureBandwidthKBPS() {
        if (!this.cloudSiteService.getNonFailingToken().isPresent()) {
            log.error("No Container Token present");
            return -1L;
        }
        try (ByteArrayInputStream testInputStream = new ByteArrayInputStream(new byte[102400000]);){
            MediaFileUploader mediaFileUploader = this.mediaFileUploaderFactory.create(this.cloudSiteService.getNonFailingToken().get());
            long startTime = this.instantSupplier.get().toEpochMilli();
            mediaFileUploader.upload(testInputStream, "testFile", uploadedBytes -> {}, 102400000L);
            long endTime = this.instantSupplier.get().toEpochMilli();
            long l = 100000000L / (endTime - startTime);
            return l;
        }
        catch (IOException e) {
            log.error("Unable to generate test file", (Throwable)e);
            return -1L;
        }
        catch (MediaUploadException e) {
            log.error("Unable to upload test file", (Throwable)e);
            return -1L;
        }
    }
}

