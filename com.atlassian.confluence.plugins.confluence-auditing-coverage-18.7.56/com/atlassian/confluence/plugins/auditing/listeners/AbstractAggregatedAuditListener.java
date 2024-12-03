/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.confluence.audit.AuditingContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.google.errorprone.annotations.concurrent.GuardedBy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.auditing.listeners;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.plugins.auditing.listeners.AbstractEventListener;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAggregatedAuditListener
extends AbstractEventListener {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UserAccessor userAccessor;
    @GuardedBy(value="this")
    private final HashMap<String, AuditSession> sessionByUsername;
    private final AuditType auditType;
    private final String auditEntry18nKey;
    private final Duration auditSessionTime;
    private final SchedulerService schedulerService;
    private final Duration jobInterval;
    private final String jobRunnerKeyAndId;
    private final int maxAuditEntriesBeforeFlush;
    protected Supplier<Long> currentTimeInNanosSupplier;
    private static final int AUDIT_STRING_LENGTH_LIMIT = 512;

    protected AbstractAggregatedAuditListener(SchedulerService schedulerService, UserAccessor userAccessor, AuditService auditService, EventListenerRegistrar eventListenerRegistrar, I18nResolver i18nResolver, LocaleResolver localeResolver, Duration auditSessionTime, Duration jobInterval, String jobRunnerKeyAndId, String auditEntry18nKey, AuditType auditType, int maxAuditEntriesBeforeFlush, AuditingContext auditingContext, Supplier<Long> timeSource) {
        super(auditService, eventListenerRegistrar, i18nResolver, localeResolver, auditingContext);
        this.userAccessor = userAccessor;
        this.auditSessionTime = auditSessionTime;
        this.auditEntry18nKey = auditEntry18nKey;
        this.auditType = auditType;
        this.maxAuditEntriesBeforeFlush = maxAuditEntriesBeforeFlush;
        this.currentTimeInNanosSupplier = timeSource;
        this.schedulerService = schedulerService;
        this.jobInterval = jobInterval;
        this.jobRunnerKeyAndId = jobRunnerKeyAndId;
        this.sessionByUsername = new HashMap();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        this.registerAuditRunner();
    }

    public void registerAuditRunner() {
        this.schedulerService.registerJobRunner(JobRunnerKey.of((String)this.jobRunnerKeyAndId), x -> this.processAuditEvents());
        try {
            Schedule schedule = Schedule.forInterval((long)this.jobInterval.toMillis(), (Date)new Date());
            JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JobRunnerKey.of((String)this.jobRunnerKeyAndId)).withRunMode(RunMode.RUN_LOCALLY).withSchedule(schedule);
            this.schedulerService.scheduleJob(JobId.of((String)this.jobRunnerKeyAndId), jobConfig);
        }
        catch (SchedulerServiceException e) {
            throw new RuntimeException(e);
        }
    }

    protected synchronized void registerAudit(String userName, String message) {
        this.sessionByUsername.putIfAbsent(userName, new AuditSession());
        String lengthValidatedMessage = message.substring(0, Math.min(message.length(), 512));
        this.sessionByUsername.get(userName).addQuery(lengthValidatedMessage);
        this.sessionByUsername.get(userName).resetTimestamp();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    synchronized JobRunnerResponse processAuditEvents() {
        try {
            AuditSession auditSession;
            ArrayList<String> userAuditSessionsToBeFlushed = new ArrayList<String>();
            long currentTimeNanos = this.currentTimeInNanosSupplier.get();
            for (Map.Entry<String, AuditSession> entry : this.sessionByUsername.entrySet()) {
                auditSession = entry.getValue();
                long timestampNs = auditSession.getTimestampNanos();
                List<String> auditEntries = auditSession.getEntries();
                if (currentTimeNanos - timestampNs <= this.auditSessionTime.toNanos() && auditEntries.size() < this.maxAuditEntriesBeforeFlush) continue;
                String userName = entry.getKey();
                userAuditSessionsToBeFlushed.add(userName);
            }
            for (String userName : userAuditSessionsToBeFlushed) {
                auditSession = this.sessionByUsername.remove(userName);
                if (auditSession == null) continue;
                List<String> auditEntries = auditSession.getEntries();
                AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)this.auditType);
                for (String auditEntry : auditEntries) {
                    auditEventBuilder.extraAttribute(AuditAttribute.fromI18nKeys((String)this.auditEntry18nKey, (String)auditEntry).build());
                }
                AuthenticatedUserThreadLocal.set((ConfluenceUser)this.userAccessor.getUserByName(userName));
                RequestCacheThreadLocal.setRequestCache((Map)auditSession.getRequestCache());
                try {
                    this.save(() -> ((AuditEvent.Builder)auditEventBuilder).build());
                }
                finally {
                    AuthenticatedUserThreadLocal.reset();
                    RequestCacheThreadLocal.clearRequestCache();
                }
            }
        }
        catch (RuntimeException e) {
            this.log.error("Error in job runner.", (Throwable)e);
            return JobRunnerResponse.failed((Throwable)e);
        }
        return JobRunnerResponse.success();
    }

    private class AuditSession {
        private final List<String> entries = new ArrayList<String>();
        private final Map requestCache;
        private long timestampNanos;

        public AuditSession() {
            this.timestampNanos = AbstractAggregatedAuditListener.this.currentTimeInNanosSupplier.get();
            this.requestCache = RequestCacheThreadLocal.getRequestCache();
        }

        public List<String> getEntries() {
            return this.entries;
        }

        public void addQuery(String query) {
            this.entries.add(query);
        }

        public long getTimestampNanos() {
            return this.timestampNanos;
        }

        public void resetTimestamp() {
            this.timestampNanos = AbstractAggregatedAuditListener.this.currentTimeInNanosSupplier.get();
        }

        public Map getRequestCache() {
            return this.requestCache;
        }
    }
}

