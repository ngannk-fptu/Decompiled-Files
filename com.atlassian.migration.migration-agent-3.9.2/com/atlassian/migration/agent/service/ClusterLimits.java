/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.springframework.jdbc.BadSqlGrammarException
 */
package com.atlassian.migration.agent.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.newexport.Query;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.rest.QueryFailedException;
import com.atlassian.migration.agent.service.execution.AccumulationScheduling;
import com.atlassian.migration.agent.service.execution.BalancedScheduling;
import com.atlassian.migration.agent.service.execution.SchedulingAlgorithm;
import com.atlassian.migration.agent.service.impl.ConcurrencySettingsService;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.springframework.jdbc.BadSqlGrammarException;

@Named
@ParametersAreNonnullByDefault
public final class ClusterLimits
implements JobRunner {
    private static final Logger log = ContextLoggerFactory.getLogger(ClusterLimits.class);
    public static final int DEFAULT_CLUSTER_STEP_CONCURRENCY_LIMIT = 1;
    public static final int DEFAULT_CONCURRENCY_PER_NODE_LIMIT = 1;
    private static final String SELECT_CPU_COUNT_MSSQL = "SELECT CPU_COUNT FROM sys.dm_os_sys_info";
    private static final String SELECT_CPU_COUNT_MYSQL = "SELECT COUNT from information_schema.INNODB_METRICS WHERE name='cpu_n'";
    private static final String SELECT_CPU_STATISTICS_ENABLED_MYSQL = "SELECT status FROM INFORMATION_SCHEMA.INNODB_METRICS where NAME ='cpu_n'";
    private static final String SELECT_CPU_COUNT_ORACLE = "select VALUE from v$osstat where STAT_NAME='NUM_CPUS'";
    private static final String ENABLED = "enabled";
    private static final SchedulingAlgorithm BALANCED = new BalancedScheduling();
    private static final SchedulingAlgorithm UNLIMITED_ACCUMULATION = new AccumulationScheduling();
    private static final int MAX_DB_QUERY_FAIL_COUNT = 3;
    private final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"migration-plugin:node-count-checker");
    private final JobId JOB_ID = JobId.of((String)"migration-plugin:node-count-checker-job-id");
    private final Duration NODE_CHECK_INTERVAL = Duration.ofSeconds(300L);
    private final ConcurrencySettingsService concurrencySettingsService;
    private Optional<Query> cpuCountQuery;
    private final ConcurrentHashMap<StepType, Integer> maxClusterConcurrencyByStepTypeExplicitFallbackValue;
    private final ConcurrentHashMap<StepType, Integer> maxConcurrencyPerNodeByStepTypeExplicitFallbackValue;
    private final MigrationAgentConfiguration config;
    private final SchedulerService schedulerService;
    private final JdbcConfluenceStore jdbcConfluenceStore;
    private final MigrationDarkFeaturesManager darkFeaturesManager;
    private int dbQueryFailCount;

    @Inject
    public ClusterLimits(MigrationAgentConfiguration config, SchedulerService schedulerService, JdbcConfluenceStore jdbcConfluenceStore, MigrationDarkFeaturesManager darkFeaturesManager, ConcurrencySettingsService concurrencySettingsService) {
        this.config = config;
        this.schedulerService = schedulerService;
        this.jdbcConfluenceStore = jdbcConfluenceStore;
        this.cpuCountQuery = this.buildCpuCountQuery(config.getDBType());
        this.darkFeaturesManager = darkFeaturesManager;
        this.concurrencySettingsService = concurrencySettingsService;
        this.maxClusterConcurrencyByStepTypeExplicitFallbackValue = new ConcurrentHashMap();
        this.maxConcurrencyPerNodeByStepTypeExplicitFallbackValue = new ConcurrentHashMap();
    }

    @PostConstruct
    public void postConstruct() throws SchedulerServiceException {
        this.schedulerService.registerJobRunner(this.RUNNER_KEY, (JobRunner)this);
        this.schedulerService.scheduleJob(this.JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)this.RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.forInterval((long)this.NODE_CHECK_INTERVAL.toMillis(), (Date)new Date(System.currentTimeMillis() + 5000L))));
        log.info("Maximum node count will be periodically checked on the database every {}", (Object)this.NODE_CHECK_INTERVAL);
    }

    @PreDestroy
    public void cleanup() {
        this.schedulerService.unregisterJobRunner(this.RUNNER_KEY);
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        if (this.isCPUQueryPresent()) {
            return this.queryCpuCoreStatistic(this.cpuCountQuery.get());
        }
        this.setMaxConcurrencyBasedOnSystemProcessor();
        return JobRunnerResponse.success();
    }

    private Optional<Query> buildCpuCountQuery(DbType dbType) {
        switch (dbType) {
            case MSSQL: {
                return Optional.of(new Query(SELECT_CPU_COUNT_MSSQL));
            }
            case MYSQL: {
                return this.getMySqlQuery();
            }
            case ORACLE: {
                return Optional.of(new Query(SELECT_CPU_COUNT_ORACLE));
            }
        }
        return Optional.empty();
    }

    private Optional<Query> getMySqlQuery() {
        try {
            Optional<String> result = this.jdbcConfluenceStore.fetchString(new Query(SELECT_CPU_STATISTICS_ENABLED_MYSQL));
            if (result.isPresent() && result.get().equalsIgnoreCase(ENABLED)) {
                return Optional.of(new Query(SELECT_CPU_COUNT_MYSQL));
            }
        }
        catch (QueryFailedException e) {
            log.warn("Error occoured when determing if CPU statistics from MySQL are enabled. It is most likely that your version of MySQL does NOT support them: {}", (Object)e.getMessage());
        }
        catch (NoClassDefFoundError e) {
            log.warn("No class defined error occured. It is likely there is a class missmatch happening with spring");
        }
        log.info("CPU Statistics are not enabled, will use system CPU");
        return Optional.empty();
    }

    public int getClusterConcurrencyLimit(StepType stepType) {
        return (Integer)ObjectUtils.firstNonNull((Object[])new Integer[]{this.getMaxClusterConcurrencyForStepType(stepType), this.maxClusterConcurrencyByStepTypeExplicitFallbackValue.get(stepType), 1});
    }

    public int getConcurrencyPerNodeLimit(StepType stepType) {
        return (Integer)ObjectUtils.firstNonNull((Object[])new Integer[]{this.getMaxConcurrencyPerNodeForStepType(stepType), this.maxConcurrencyPerNodeByStepTypeExplicitFallbackValue.get(stepType), 1});
    }

    @VisibleForTesting
    private Integer getMaxClusterConcurrencyForStepType(StepType stepType) {
        switch (stepType) {
            case ATTACHMENT_UPLOAD: {
                return this.config.getAttachmentMigrationConcurrencyClusterMax();
            }
            case SPACE_USERS_MIGRATION: {
                return this.concurrencySettingsService.getSpaceUsersMigrationExecutorConcurrencyClusterMax();
            }
            case CONFLUENCE_EXPORT: {
                return this.config.getExportConcurrencyClusterMax().orElse(null);
            }
            case DATA_UPLOAD: {
                return this.config.getUploadConcurrencyClusterMax();
            }
            case CONFLUENCE_IMPORT: {
                return this.config.getImportConcurrencyClusterMax();
            }
        }
        return null;
    }

    @VisibleForTesting
    private Integer getMaxConcurrencyPerNodeForStepType(StepType stepType) {
        switch (stepType) {
            case ATTACHMENT_UPLOAD: {
                return this.config.getAttachmentMigrationConcurrencyNodeMax();
            }
            case SPACE_USERS_MIGRATION: {
                return this.concurrencySettingsService.getSpaceUsersMigrationConcurrencyNodeMax();
            }
            case CONFLUENCE_EXPORT: {
                return this.config.getExportConcurrencyNodeMax().orElse(null);
            }
            case DATA_UPLOAD: {
                return this.config.getUploadConcurrencyNodeMax();
            }
            case CONFLUENCE_IMPORT: {
                return this.config.getImportConcurrencyNodeMax();
            }
        }
        return null;
    }

    public SchedulingAlgorithm getSchedulingAlgorithm(StepType stepType) {
        if (stepType == StepType.CONFLUENCE_IMPORT && this.darkFeaturesManager.isUnlimitedSpaceImportConcurrencyEnabled()) {
            return UNLIMITED_ACCUMULATION;
        }
        return BALANCED;
    }

    private JobRunnerResponse queryCpuCoreStatistic(Query query) {
        try {
            Optional<Integer> result = this.jdbcConfluenceStore.fetchInteger(query);
            if (result.isPresent() && result.get() > 0) {
                this.updateExportLimit(Math.max(1, result.get() / 2));
            } else {
                this.setMaxConcurrencyBasedOnSystemProcessor();
            }
            return JobRunnerResponse.success();
        }
        catch (SecurityException e) {
            String logMessage = "It is most likely Confluence does not have the permissions (in the previous) to read the CPU statistics, please add these and restart CCMA. Until restart, CCMA will no longer run the scheduled CPU query and it will be based upon system properties";
            this.disableDBQuery(logMessage);
            return JobRunnerResponse.failed((Throwable)e);
        }
        catch (BadSqlGrammarException e) {
            String logMessage = "There is a problem with the SQL Query. Please refer to the logs above for the exact issue. More than likely there is a compatibility issue with your database.";
            this.disableDBQuery(logMessage);
            return JobRunnerResponse.failed((Throwable)e);
        }
        catch (NoClassDefFoundError e) {
            String logMessage = "No class defined error occurred. It is likely there is a class missmatch happening with spring";
            this.disableDBQuery(logMessage);
            return JobRunnerResponse.failed((Throwable)e);
        }
        catch (Exception e) {
            ++this.dbQueryFailCount;
            if (this.dbQueryFailCount >= 3) {
                String logMessage = "The DB Query for CPU statistics has now failed 3 times, and has been disabled. Concurrency Limits will now be based upon the CPU count. Please refer to the above logs for the exact cause of this";
                this.disableDBQuery(logMessage);
            } else {
                this.setMaxConcurrencyBasedOnSystemProcessor();
            }
            return JobRunnerResponse.failed((Throwable)e);
        }
    }

    private void disableDBQuery(String logMessage) {
        log.warn(logMessage);
        this.setMaxConcurrencyBasedOnSystemProcessor();
        this.cpuCountQuery = Optional.empty();
    }

    private void setMaxConcurrencyBasedOnSystemProcessor() {
        this.updateExportLimit(Math.max(1, Runtime.getRuntime().availableProcessors() / 2));
    }

    private void updateExportLimit(int limit) {
        this.maxClusterConcurrencyByStepTypeExplicitFallbackValue.put(StepType.CONFLUENCE_EXPORT, limit);
        this.maxConcurrencyPerNodeByStepTypeExplicitFallbackValue.put(StepType.CONFLUENCE_EXPORT, limit);
    }

    @VisibleForTesting
    boolean isCPUQueryPresent() {
        return this.cpuCountQuery.isPresent();
    }
}

