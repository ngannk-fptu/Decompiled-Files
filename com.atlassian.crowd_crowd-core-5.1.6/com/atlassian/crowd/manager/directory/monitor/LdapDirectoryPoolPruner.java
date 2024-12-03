/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.SpringLdapPooledContextSourceProvider
 *  com.atlassian.crowd.directory.SpringLdapPooledContextSourceProvider$LdapPoolDestroyedReason
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.spi.DcLicenseChecker
 *  com.atlassian.crowd.event.application.ApplicationReadyEvent
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
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
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.directory.monitor;

import com.atlassian.crowd.directory.SpringLdapPooledContextSourceProvider;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.spi.DcLicenseChecker;
import com.atlassian.crowd.event.application.ApplicationReadyEvent;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
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
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapDirectoryPoolPruner
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(LdapDirectoryPoolPruner.class);
    static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)LdapDirectoryPoolPruner.class.getName());
    static final JobId JOB_ID = JobId.of((String)"inactive-ldap-pool-cleanup");
    private final EntityQuery<Directory> INACTIVE_LDAP_DIRECTORIES_QUERY = QueryBuilder.queryFor(Directory.class, (EntityDescriptor)EntityDescriptor.directory()).with((SearchRestriction)Combine.allOf((SearchRestriction[])new SearchRestriction[]{Restriction.on((Property)DirectoryTermKeys.TYPE).exactlyMatchingAny((Collection)ImmutableSet.of((Object)DirectoryType.CONNECTOR, (Object)DirectoryType.DELEGATING)), Restriction.on((Property)DirectoryTermKeys.ACTIVE).exactlyMatching((Object)false)})).returningAtMost(-1);
    private final String CRON_EXPRESSION = "0 0 * * * ?";
    private final SchedulerService schedulerService;
    private final DirectoryManager directoryManager;
    private final SpringLdapPooledContextSourceProvider springLdapPooledContextSourceProvider;
    private final EventPublisher eventPublisher;
    private final DcLicenseChecker dcLicenseChecker;

    public LdapDirectoryPoolPruner(SchedulerService schedulerService, DirectoryManager directoryManager, SpringLdapPooledContextSourceProvider springLdapPooledContextSourceProvider, EventPublisher eventPublisher, DcLicenseChecker dcLicenseChecker) {
        this.schedulerService = schedulerService;
        this.directoryManager = directoryManager;
        this.springLdapPooledContextSourceProvider = springLdapPooledContextSourceProvider;
        this.eventPublisher = eventPublisher;
        this.dcLicenseChecker = dcLicenseChecker;
    }

    @PostConstruct
    public void registerListener() {
        if (this.dcLicenseChecker.isDcLicense()) {
            this.eventPublisher.register((Object)this);
        }
    }

    @PreDestroy
    public void onDestroy() {
        this.eventPublisher.unregister((Object)this);
        if (this.dcLicenseChecker.isDcLicense()) {
            this.schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
        }
    }

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent applicationStartedEvent) throws SchedulerServiceException {
        this.registerJobRunner();
    }

    public void registerJobRunner() throws SchedulerServiceException {
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)this);
        this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.forCronExpression((String)"0 0 * * * ?")));
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        Set directoriesIdsFromPoolStats = this.springLdapPooledContextSourceProvider.getPoolStatistics().keySet();
        Set inactiveDirectoriesIds = this.getInactiveLdapDirectories().stream().map(directory -> directory.getId()).collect(Collectors.toSet());
        HashSet directoryIdsWithCommonsPool2 = new HashSet(directoriesIdsFromPoolStats);
        log.debug("Found {} directories with available Dynamic connection pools.", (Object)directoryIdsWithCommonsPool2.size());
        log.debug("Found {} inactive directories.", (Object)inactiveDirectoriesIds.size());
        directoryIdsWithCommonsPool2.retainAll(inactiveDirectoriesIds);
        if (directoryIdsWithCommonsPool2.isEmpty()) {
            log.debug("There were no Dynamic connection pools to be cleaned up.");
        } else {
            log.debug("Proceeding to remove {} Dynamic connection pools.", (Object)directoryIdsWithCommonsPool2.size());
            for (Long directoryId : directoryIdsWithCommonsPool2) {
                this.springLdapPooledContextSourceProvider.removeContextSource(directoryId, SpringLdapPooledContextSourceProvider.LdapPoolDestroyedReason.DIRECTORY_DEACTIVATED, false);
            }
        }
        return JobRunnerResponse.success();
    }

    private List<Directory> getInactiveLdapDirectories() {
        return this.directoryManager.searchDirectories(this.INACTIVE_LDAP_DIRECTORIES_QUERY);
    }
}

