/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.fugue.Try
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.service;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.portfolioanalyzer.model.SpaceRelationsGraph;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.RelationsAnalyzerService;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.WarnLogFileWriter;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.atlassian.fugue.Try;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationsAnalyzerRunner
implements JobRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(RelationsAnalyzerRunner.class);
    static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"relationships-assessment-job-runner-key");
    static final JobId JOB_ID = JobId.of((String)"relationships-assessment-job-runner-id");
    static final String APPLICATION_USER_PARAM = "APPLICATION_USER";
    private final SchedulerService schedulerService;
    private final RelationsAnalyzerService relationsAnalyzerService;
    private final UserAccessor userAccessor;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final WarnLogFileWriter warnLogFileWriter;
    final Path jsonFilePath;
    final Path errorFilePath;
    final Path zipFilePath;

    public RelationsAnalyzerRunner(SchedulerService schedulerService, RelationsAnalyzerService relationsAnalyzerService, BootstrapManager bootstrapManager, UserAccessor userAccessor, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, WarnLogFileWriter warnLogFileWriter) {
        this.schedulerService = schedulerService;
        this.relationsAnalyzerService = relationsAnalyzerService;
        this.userAccessor = userAccessor;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.warnLogFileWriter = warnLogFileWriter;
        Path basePath = Optional.ofNullable(bootstrapManager.getSharedHome()).orElse(bootstrapManager.getLocalHome()).toPath();
        this.jsonFilePath = basePath.resolve("relationships-assessment-job-runner-output-file.json");
        this.errorFilePath = basePath.resolve("relationships-assessment-job-runner-error.log");
        this.zipFilePath = basePath.resolve("relations-assessment.zip");
    }

    @PostConstruct
    public void initialize() {
        this.schedulerService.registerJobRunner(RUNNER_KEY, (JobRunner)this);
    }

    @PreDestroy
    public void cleanup() {
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
    }

    public Try<JobId> scheduleJob() {
        if (!this.isAnyActiveJob()) {
            try {
                this.schedulerService.scheduleJob(JOB_ID, RelationsAnalyzerRunner.jobConfigOf());
                return Try.successful((Object)JOB_ID);
            }
            catch (SchedulerServiceException e) {
                log.error("Error occurred when scheduling job", (Throwable)e);
                return Try.failure((Exception)((Object)e));
            }
        }
        return Try.failure((Exception)new IllegalStateException("Job already scheduled"));
    }

    public Status getJobStatus() {
        if (this.isAnyActiveJob()) {
            return Status.IN_PROGRESS;
        }
        if (Files.exists(this.jsonFilePath, new LinkOption[0])) {
            return Status.COMPLETED;
        }
        if (Files.exists(this.errorFilePath, new LinkOption[0])) {
            return Status.ERROR;
        }
        return Status.UNKNOWN;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public JobRunnerResponse runJob(@NotNull JobRunnerRequest request) {
        this.warnLogFileWriter.clearWarnLogs();
        long startTime = System.currentTimeMillis();
        try {
            UserKey userKey = new UserKey(((Serializable)request.getJobConfig().getParameters().get(APPLICATION_USER_PARAM)).toString());
            AuthenticatedUserThreadLocal.set((ConfluenceUser)this.userAccessor.getUserByKey(userKey));
            SpaceRelationsGraph graph = this.relationsAnalyzerService.getGraph();
            this.removeFilesAndCreateOneWithContent(this.jsonFilePath, graph);
            this.logSuccessAndSendEvent(startTime, graph.getNodes().size(), graph.getRelations().size());
            JobRunnerResponse jobRunnerResponse = JobRunnerResponse.success((String)("Connection graph successfully saved to " + this.jsonFilePath + "."));
            return jobRunnerResponse;
        }
        catch (UncheckedIOException e) {
            this.logErrorAndSendEvent(startTime, e);
            JobRunnerResponse jobRunnerResponse = JobRunnerResponse.failed((String)("Failed to run job: " + e.getMessage()));
            return jobRunnerResponse;
        }
        catch (Exception e) {
            this.removeFilesAndCreateOneWithContent(this.errorFilePath, ExceptionUtils.getStackTrace((Throwable)e));
            this.logErrorAndSendEvent(startTime, e);
            JobRunnerResponse jobRunnerResponse = JobRunnerResponse.failed((String)("Failed to run job: " + e.getMessage()));
            return jobRunnerResponse;
        }
        finally {
            this.generateZip();
        }
    }

    private void generateZip() {
        try {
            this.doGenerateZip();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void doGenerateZip() throws IOException {
        Files.deleteIfExists(this.zipFilePath);
        List filesToExport = ImmutableList.of((Object)this.jsonFilePath.toFile(), (Object)this.errorFilePath.toFile(), (Object)this.warnLogFileWriter.getWarnLogFilePath().toFile()).stream().filter(File::exists).collect(Collectors.toList());
        try (FileOutputStream fos = new FileOutputStream(this.zipFilePath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos);){
            for (File file : filesToExport) {
                FileInputStream fis = new FileInputStream(file);
                Throwable throwable = null;
                try {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);
                    IOUtils.copy((InputStream)fis, (OutputStream)zos);
                    zos.closeEntry();
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (fis == null) continue;
                    if (throwable != null) {
                        try {
                            fis.close();
                        }
                        catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    fis.close();
                }
            }
        }
    }

    private void logSuccessAndSendEvent(long startTime, int numberOfNodes, int numberOfRelations) {
        long durationMs = System.currentTimeMillis() - startTime;
        log.info("Successful relations analysis job took {} ms", (Object)durationMs);
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildRelationsAnalysisJobFinishedEvent(durationMs, numberOfNodes, numberOfRelations));
    }

    private void logErrorAndSendEvent(long startTime, Throwable e) {
        long durationMs = System.currentTimeMillis() - startTime;
        log.error("Failed relations analysis job took {} ms", (Object)durationMs, (Object)e);
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildRelationsAnalysisJobFailedEvent(durationMs));
    }

    private boolean isAnyActiveJob() {
        return !this.schedulerService.getJobsByJobRunnerKey(RUNNER_KEY).isEmpty();
    }

    private void removeFilesAndCreateOneWithContent(Path path, Object content) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Files.deleteIfExists(this.jsonFilePath);
            Files.deleteIfExists(this.errorFilePath);
            Path pathToCreatedFile = Files.createFile(path, new FileAttribute[0]);
            mapper.writeValue(pathToCreatedFile.toFile(), content);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static JobConfig jobConfigOf() {
        return JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withParameters((Map)ImmutableMap.of((Object)APPLICATION_USER_PARAM, (Object)AuthenticatedUserThreadLocal.get().getKey().getStringValue()));
    }

    @Generated
    public Path getZipFilePath() {
        return this.zipFilePath;
    }

    public static enum Status {
        IN_PROGRESS,
        COMPLETED,
        ERROR,
        UNKNOWN;

    }
}

