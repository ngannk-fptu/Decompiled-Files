/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.services.AnalyticsUploadService
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.time.DateUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.upload;

import com.atlassian.analytics.api.services.AnalyticsUploadService;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.logger.AnalyticsLogger;
import com.atlassian.analytics.client.properties.LoggingProperties;
import com.atlassian.analytics.client.s3.AnalyticsS3Client;
import com.atlassian.analytics.client.upload.AnalyticsGzLogFileDetector;
import com.atlassian.analytics.client.upload.EventUploaderConfigurationProvider;
import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3EventUploader
implements JobRunner,
AnalyticsUploadService,
Serializable {
    static final JobRunnerKey KEY = JobRunnerKey.of((String)(S3EventUploader.class + "_JobHandlerKey"));
    private static final Logger LOG = LoggerFactory.getLogger(S3EventUploader.class);
    private final PeriodicEventUploaderScheduler scheduler;
    private final AnalyticsConfig analyticsConfig;
    private final AnalyticsLogger analyticsLogger;
    private final EventUploaderConfigurationProvider eventUploaderConfigurationProvider;
    private final AnalyticsS3Client analyticsS3Client;

    public S3EventUploader(PeriodicEventUploaderScheduler scheduler, AnalyticsConfig analyticsConfig, AnalyticsLogger analyticsLogger, EventUploaderConfigurationProvider eventUploaderConfigurationProvider, AnalyticsS3Client analyticsS3Client) {
        this.scheduler = scheduler;
        this.analyticsConfig = analyticsConfig;
        this.analyticsLogger = analyticsLogger;
        this.eventUploaderConfigurationProvider = eventUploaderConfigurationProvider;
        this.analyticsS3Client = analyticsS3Client;
    }

    public JobRunnerResponse runJob(@Nullable JobRunnerRequest jobRunnerRequest) {
        LOG.debug("Executing analytics uploader job.");
        this.scheduler.rescheduleUploadJob();
        if (!this.analyticsConfig.canCollectAnalytics()) {
            return JobRunnerResponse.aborted((String)"Analytics are disabled");
        }
        if (this.lastUploadWasToday()) {
            return JobRunnerResponse.aborted((String)"Upload has already happened today");
        }
        this.analyticsLogger.reset();
        List<File> logFiles = this.getLogFiles();
        int filesUploaded = this.analyticsS3Client.uploadFilesToS3Bucket(logFiles, this.eventUploaderConfigurationProvider.getAnalyticsS3BucketName(), this.eventUploaderConfigurationProvider.getAnalyticsS3FolderKeyPrefix());
        if (filesUploaded > 0) {
            this.eventUploaderConfigurationProvider.setLastUploadDate(new Date());
        }
        if (this.logsOverSpaceThreshold(logFiles) || this.lowFreeSpace(this.eventUploaderConfigurationProvider.getLogDirPath())) {
            this.deleteOldLogsOlderThanWeek(this.analyticsLogger);
        }
        return JobRunnerResponse.success();
    }

    private boolean lastUploadWasToday() {
        Date lastUploadDate = this.eventUploaderConfigurationProvider.getLastUploadDate();
        if (lastUploadDate == null) {
            return false;
        }
        return DateUtils.isSameDay((Date)lastUploadDate, (Date)this.eventUploaderConfigurationProvider.today());
    }

    @VisibleForTesting
    void deleteOldLogsOlderThanWeek(AnalyticsLogger analyticsLogger) {
        File logDir = this.eventUploaderConfigurationProvider.getLogDirPath();
        if (!logDir.isDirectory()) {
            LOG.error("log dir is not a directory! {}", (Object)logDir.getAbsolutePath());
            return;
        }
        Instant weekAgo = Instant.now().minus(7L, ChronoUnit.DAYS);
        this.getLogFiles().stream().filter(f -> this.isOlderThan((File)f, weekAgo)).forEach(log -> {
            if (!log.delete()) {
                LOG.debug("Couldn't delete the log file {} during cleanup.", (Object)log.getAbsolutePath());
            }
            this.logCleanupDeletion(analyticsLogger, (File)log);
        });
    }

    private boolean logsOverSpaceThreshold(List<File> compressedLogFiles) {
        long totalFileSize = compressedLogFiles.stream().mapToLong(File::length).sum();
        return totalFileSize > LoggingProperties.getMaxLogsDirSizeBytes();
    }

    private boolean lowFreeSpace(File logDir) {
        return logDir.getUsableSpace() < this.eventUploaderConfigurationProvider.getFreeSpaceCleanupThreshold();
    }

    private boolean isOlderThan(File f, Instant time) {
        return this.parseFilenameDate(f).filter(time::isAfter).isPresent();
    }

    private Optional<Instant> parseFilenameDate(File file) {
        Matcher matcher = this.eventUploaderConfigurationProvider.getAnalyticsLogRollingDatePatternRegexPattern().matcher(file.getName());
        if (matcher.find() && matcher.groupCount() > 0) {
            DateTimeFormatter dateFormat = this.eventUploaderConfigurationProvider.getAnalyticsLogRollingDatePattern();
            try {
                String dateString = matcher.group(1);
                TemporalAccessor parse = dateFormat.withZone(ZoneId.systemDefault()).parse(dateString);
                LocalDateTime ldt = LocalDateTime.of(LocalDate.from(parse), LocalTime.MIDNIGHT);
                return Optional.of(ldt.atZone(ZoneId.systemDefault()).toInstant());
            }
            catch (DateTimeParseException e) {
                LOG.debug("Couldn't extract date from analytics log filename {} for cleanup.", (Object)file.getName());
            }
        }
        return Optional.empty();
    }

    List<File> getLogFiles() {
        File logDir = this.eventUploaderConfigurationProvider.getLogDirPath();
        if (!logDir.isDirectory()) {
            return Collections.emptyList();
        }
        File[] files = logDir.listFiles();
        if (files == null) {
            LOG.error("can't list files in log directory usually this indicates failed security manager check or invalid path");
            return Collections.emptyList();
        }
        return Arrays.stream(files).filter(AnalyticsGzLogFileDetector::isAnalyticsGzLogFile).collect(Collectors.toList());
    }

    private void logCleanupDeletion(AnalyticsLogger analyticsLogger, File logFile) {
        long logSize = logFile.length();
        analyticsLogger.logCleanupDeletion("log file: " + logFile.getName() + (logSize > -1L ? "  size: " + logSize : ""));
    }

    public Date getLastUploadDate() {
        return this.eventUploaderConfigurationProvider.getLastUploadDate();
    }

    public boolean hasUploadedAnalyticsSince(int numberOfDays) {
        Date now = new Date();
        Date lastDateWhenAnalyticsUploaded = this.getLastUploadDate();
        return lastDateWhenAnalyticsUploaded != null && now.before(DateUtils.addDays((Date)lastDateWhenAnalyticsUploaded, (int)numberOfDays));
    }
}

