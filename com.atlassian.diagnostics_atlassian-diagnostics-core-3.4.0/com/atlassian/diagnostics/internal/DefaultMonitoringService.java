/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertCount
 *  com.atlassian.diagnostics.AlertCriteria
 *  com.atlassian.diagnostics.AlertListener
 *  com.atlassian.diagnostics.AlertTrigger$Builder
 *  com.atlassian.diagnostics.AlertWithElisions
 *  com.atlassian.diagnostics.CallbackResult
 *  com.atlassian.diagnostics.Component
 *  com.atlassian.diagnostics.ComponentMonitor
 *  com.atlassian.diagnostics.DiagnosticsConfiguration
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.JsonMapper
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  com.atlassian.diagnostics.PageCallback
 *  com.atlassian.diagnostics.PageRequest
 *  com.atlassian.diagnostics.PageSummary
 *  com.atlassian.diagnostics.PluginDetails
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.transaction.TransactionTemplate
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
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertCount;
import com.atlassian.diagnostics.AlertCriteria;
import com.atlassian.diagnostics.AlertListener;
import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.AlertWithElisions;
import com.atlassian.diagnostics.CallbackResult;
import com.atlassian.diagnostics.Component;
import com.atlassian.diagnostics.ComponentMonitor;
import com.atlassian.diagnostics.DiagnosticsConfiguration;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.JsonMapper;
import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.PageCallback;
import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.PageSummary;
import com.atlassian.diagnostics.PluginDetails;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.AlertCountCollector;
import com.atlassian.diagnostics.internal.AlertPublisher;
import com.atlassian.diagnostics.internal.AlertWithElisionsCollector;
import com.atlassian.diagnostics.internal.DefaultComponentMonitor;
import com.atlassian.diagnostics.internal.InternalComponentMonitor;
import com.atlassian.diagnostics.internal.InternalMonitoringService;
import com.atlassian.diagnostics.internal.IssueId;
import com.atlassian.diagnostics.internal.IssueSupplier;
import com.atlassian.diagnostics.internal.PluginHelper;
import com.atlassian.diagnostics.internal.SimpleAlert;
import com.atlassian.diagnostics.internal.SimpleComponent;
import com.atlassian.diagnostics.internal.SimpleIssue;
import com.atlassian.diagnostics.internal.SimplePageSummary;
import com.atlassian.diagnostics.internal.dao.AlertEntity;
import com.atlassian.diagnostics.internal.dao.AlertEntityDao;
import com.atlassian.diagnostics.internal.dao.AlertMetric;
import com.atlassian.diagnostics.internal.dao.MinimalAlertEntity;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.transaction.TransactionTemplate;
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
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMonitoringService
implements LifecycleAware,
InternalMonitoringService,
IssueSupplier {
    private static final Logger log = LoggerFactory.getLogger(DefaultMonitoringService.class);
    static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)TruncateAlertsJobRunner.class.getName());
    static final JobId JOB_ID = JobId.of((String)TruncateAlertsJobRunner.class.getSimpleName());
    static final Duration WINDOW_SIZE = Duration.ofMinutes(1L);
    private static final int COLLECTOR_PAGE_FACTOR = 3;
    private static final MonitorConfiguration DEFAULT_MONITORING_CONFIGURATION = new MonitorConfiguration(){

        public boolean isEnabled() {
            return true;
        }
    };
    private final DiagnosticsConfiguration configuration;
    private final AlertEntityDao dao;
    private final I18nResolver i18nResolver;
    private final JsonMapper jsonMapper;
    private final ConcurrentMap<String, InternalComponentMonitor> monitors;
    private final PermissionEnforcer permissionEnforcer;
    private final ConcurrentMap<String, Component> placeholderComponents;
    private final ConcurrentMap<IssueId, Issue> placeholderIssues;
    private final PluginHelper pluginHelper;
    private final AlertPublisher publisher;
    private final SchedulerService schedulerService;
    private final TransactionTemplate transactionTemplate;

    public DefaultMonitoringService(DiagnosticsConfiguration configuration, AlertEntityDao dao, I18nResolver i18nResolver, JsonMapper jsonMapper, PermissionEnforcer permissionEnforcer, PluginHelper pluginHelper, AlertPublisher publisher, SchedulerService schedulerService, TransactionTemplate transactionTemplate) {
        this.configuration = configuration;
        this.dao = dao;
        this.i18nResolver = i18nResolver;
        this.jsonMapper = jsonMapper;
        this.permissionEnforcer = permissionEnforcer;
        this.pluginHelper = pluginHelper;
        this.publisher = publisher;
        this.schedulerService = schedulerService;
        this.transactionTemplate = transactionTemplate;
        this.monitors = new ConcurrentHashMap<String, InternalComponentMonitor>();
        this.placeholderComponents = new ConcurrentHashMap<String, Component>();
        this.placeholderIssues = new ConcurrentHashMap<IssueId, Issue>();
    }

    @Nonnull
    public ComponentMonitor createMonitor(@Nonnull String componentId, @Nonnull String componentNameI18nKey) {
        return this.createMonitor(componentId, componentNameI18nKey, DEFAULT_MONITORING_CONFIGURATION);
    }

    @Nonnull
    public ComponentMonitor createMonitor(@Nonnull String componentId, @Nonnull String componentNameI18nKey, @Nonnull MonitorConfiguration monitorConfiguration) {
        Objects.requireNonNull(componentId, "componentId");
        Objects.requireNonNull(componentNameI18nKey, "componentNameI18nKey");
        Objects.requireNonNull(monitorConfiguration, "monitorConfiguration");
        String upperComponentId = componentId.toUpperCase();
        return this.monitors.computeIfAbsent(upperComponentId, id -> this.internalCreateMonitor(upperComponentId, componentNameI18nKey, monitorConfiguration));
    }

    private InternalComponentMonitor internalCreateMonitor(@Nonnull String componentId, @Nonnull String nameI18nKey, @Nonnull MonitorConfiguration monitorConfiguration) {
        return new DefaultComponentMonitor(new SimpleComponent(this.i18nResolver, componentId, nameI18nKey), this.configuration, monitorConfiguration, this.i18nResolver, this.jsonMapper, this.publisher);
    }

    public boolean destroyMonitor(@Nonnull String componentId) {
        InternalComponentMonitor componentMonitor = (InternalComponentMonitor)this.monitors.remove(componentId);
        if (componentMonitor != null) {
            componentMonitor.destroy();
        }
        return componentMonitor != null;
    }

    @Nonnull
    public Set<Component> findAllComponents() {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        Set<String> componentsIdsWithAlerts = ((Set)this.transactionTemplate.execute(this.dao::findAllComponentIds)).stream().map(value -> StringUtils.upperCase((String)value, (Locale)Locale.ROOT)).collect(Collectors.toSet());
        this.monitors.values().stream().map(ComponentMonitor::getComponent).peek(component -> componentsIdsWithAlerts.remove(component.getId())).forEach(arg_0 -> ((ImmutableSet.Builder)builder).add(arg_0));
        componentsIdsWithAlerts.forEach(id -> builder.add((Object)this.getPlaceholderComponent((String)id)));
        return builder.build();
    }

    @Nonnull
    public Set<Issue> findAllIssues() {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        HashMap<String, Severity> issueIdsWithAlerts = new HashMap<String, Severity>((Map)this.transactionTemplate.execute(this.dao::findAllIssueIds));
        this.monitors.values().stream().flatMap(monitor -> monitor.getIssues().stream()).peek(issue -> {
            Severity cfr_ignored_0 = (Severity)issueIdsWithAlerts.remove(issue.getId());
        }).forEach(arg_0 -> ((ImmutableSet.Builder)builder).add(arg_0));
        issueIdsWithAlerts.forEach((id, severity) -> builder.add((Object)this.getIssueOrPlaceholder((String)id, (Severity)severity)));
        return builder.build();
    }

    @Nonnull
    public Set<String> findAllNodesWithAlerts() {
        return (Set)this.transactionTemplate.execute(this.dao::findAllNodeNames);
    }

    @Nonnull
    public Set<PluginDetails> findAllPluginsWithAlerts() {
        Set pluginKeys = (Set)this.transactionTemplate.execute(this.dao::findAllPluginKeys);
        Set<PluginDetails> pluginDetails = pluginKeys.stream().map(key -> new PluginDetails(key, this.pluginHelper.getPluginName((String)key), null)).collect(Collectors.toSet());
        log.info("pluginKeys: [{}], pluginDetails: [{}]", pluginDetails);
        return pluginDetails;
    }

    @Override
    @Nonnull
    public Issue getIssue(@Nonnull String issueId, @Nonnull Severity severity) {
        return this.getIssueOrPlaceholder(issueId, severity);
    }

    @Nonnull
    public Optional<ComponentMonitor> getMonitor(@Nonnull String componentId) {
        Objects.requireNonNull(componentId, "componentId");
        return Optional.ofNullable(this.monitors.get(componentId.toUpperCase()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T internalStreamAlertCounts(@Nonnull AlertCriteria criteria, @Nonnull PageCallback<? super AlertCount, T> callback, @Nonnull PageRequest pageRequest) {
        int row;
        Objects.requireNonNull(callback, "callback");
        Objects.requireNonNull(criteria, "criteria");
        Objects.requireNonNull(pageRequest, "pageRequest");
        MutableBoolean done = new MutableBoolean(false);
        MutableInt emittedRow = new MutableInt(0);
        MutableInt entityRow = new MutableInt(0);
        int start = pageRequest.getStart();
        int limit = pageRequest.getLimit();
        int end = start + limit;
        AlertCountCollector collector = new AlertCountCollector(this, this.pluginHelper);
        int daoPageSize = limit * 3;
        try {
            callback.onStart(pageRequest);
            while (done.isFalse() && emittedRow.getValue() <= end) {
                this.transactionTemplate.execute(() -> {
                    MutableInt pageSize = new MutableInt(0);
                    this.dao.streamMetrics(criteria, entity -> {
                        int row;
                        if (pageSize.incrementAndGet() > daoPageSize) {
                            return CallbackResult.CONTINUE;
                        }
                        entityRow.increment();
                        AlertCount alertCount = collector.onRow((AlertMetric)entity);
                        if (alertCount != null && (row = emittedRow.getAndIncrement()) >= start && row < end && callback.onItem((Object)alertCount) == CallbackResult.DONE) {
                            done.setTrue();
                        }
                        done.setValue(done.isTrue() || emittedRow.getValue() > end);
                        return done.isTrue() ? CallbackResult.DONE : CallbackResult.CONTINUE;
                    }, PageRequest.of((int)entityRow.getValue(), (int)daoPageSize));
                    done.setValue(done.isTrue() || pageSize.getValue() <= daoPageSize);
                    return null;
                });
            }
        }
        catch (Throwable throwable) {
            int row2;
            AlertCount finalItem = collector.onEnd();
            if (finalItem != null && (row2 = emittedRow.getAndIncrement()) >= start && row2 < end) {
                callback.onItem((Object)finalItem);
            }
            PageRequest prevRequest = start == 0 ? null : PageRequest.of((int)Math.max(0, start - limit), (int)limit);
            PageRequest nextRequest = emittedRow.getValue() <= end ? null : PageRequest.of((int)end, (int)limit);
            int size = Math.min(limit, emittedRow.getValue() - start);
            Object result = callback.onEnd((PageSummary)new SimplePageSummary(prevRequest, nextRequest, size));
            throw throwable;
        }
        AlertCount finalItem = collector.onEnd();
        if (finalItem != null && (row = emittedRow.getAndIncrement()) >= start && row < end) {
            callback.onItem((Object)finalItem);
        }
        PageRequest prevRequest = start == 0 ? null : PageRequest.of((int)Math.max(0, start - limit), (int)limit);
        PageRequest nextRequest = emittedRow.getValue() <= end ? null : PageRequest.of((int)end, (int)limit);
        int size = Math.min(limit, emittedRow.getValue() - start);
        Object result = callback.onEnd((PageSummary)new SimplePageSummary(prevRequest, nextRequest, size));
        return (T)result;
    }

    public boolean isEnabled() {
        return this.configuration.isEnabled();
    }

    public void onStart() {
        long intervalInMillis = this.configuration.getAlertTruncationInterval().toMillis();
        long firstRunTime = System.currentTimeMillis() + intervalInMillis;
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)new TruncateAlertsJobRunner());
        try {
            this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withSchedule(Schedule.forInterval((long)intervalInMillis, (Date)new Date(firstRunTime))).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER));
        }
        catch (SchedulerServiceException e) {
            log.warn("Failed to schedule periodic alert truncation", (Throwable)e);
        }
    }

    public void onStop() {
        this.schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
    }

    public <T> T streamAlerts(@Nonnull AlertCriteria criteria, @Nonnull PageCallback<? super Alert, T> callback, @Nonnull PageRequest pageRequest) {
        Objects.requireNonNull(callback, "callback");
        Objects.requireNonNull(criteria, "criteria");
        Objects.requireNonNull(pageRequest, "pageRequest");
        this.permissionEnforcer.enforceSystemAdmin();
        return (T)this.transactionTemplate.execute(() -> {
            Object result;
            callback.onStart(pageRequest);
            MutableInt count = new MutableInt(0);
            int limit = pageRequest.getLimit();
            try {
                this.dao.streamAll(criteria, entity -> {
                    if (count.incrementAndGet() > limit) {
                        return CallbackResult.DONE;
                    }
                    return callback.onItem((Object)this.toAlert((AlertEntity)entity));
                }, pageRequest);
            }
            finally {
                result = callback.onEnd((PageSummary)new SimplePageSummary(pageRequest, count.getValue()));
            }
            return result;
        });
    }

    public <T> T streamAlertCounts(@Nonnull AlertCriteria criteria, @Nonnull PageCallback<? super AlertCount, T> callback, @Nonnull PageRequest pageRequest) {
        this.permissionEnforcer.enforceSystemAdmin();
        return this.internalStreamAlertCounts(criteria, callback, pageRequest);
    }

    public <T> T streamAlertsWithElisions(@Nonnull AlertCriteria criteria, @Nonnull PageCallback<? super AlertWithElisions, T> callback, @Nonnull PageRequest pageRequest) {
        Objects.requireNonNull(callback, "callback");
        Objects.requireNonNull(criteria, "criteria");
        Objects.requireNonNull(pageRequest, "pageRequest");
        this.permissionEnforcer.enforceSystemAdmin();
        return (T)this.transactionTemplate.execute(() -> {
            Object result;
            block5: {
                AlertWithElisionsCollector collector = new AlertWithElisionsCollector(this, pageRequest, WINDOW_SIZE);
                this.dao.streamMinimalAlerts(criteria, alert -> {
                    collector.add((MinimalAlertEntity)alert);
                    return collector.hasCompletePage() ? CallbackResult.DONE : CallbackResult.CONTINUE;
                }, PageRequest.ofSize((int)0x7FFFFFFE));
                collector.onEndAlertScan();
                MutableInt count = new MutableInt(0);
                int limit = pageRequest.getLimit();
                callback.onStart(pageRequest);
                try {
                    MutableBoolean done = new MutableBoolean(false);
                    this.dao.streamByIds(collector.getAlertIdsToLoad(), entity -> {
                        for (AlertWithElisions alertWithElisions : collector.resolveCandidate((AlertEntity)entity)) {
                            if (count.incrementAndGet() > limit) {
                                return CallbackResult.DONE;
                            }
                            if (callback.onItem((Object)alertWithElisions) != CallbackResult.DONE) continue;
                            done.setTrue();
                            return CallbackResult.DONE;
                        }
                        return CallbackResult.CONTINUE;
                    });
                    if (done.isTrue()) break block5;
                    for (AlertWithElisions alert2 : collector.onEndAlertResolution()) {
                        if (count.incrementAndGet() <= limit) {
                            if (callback.onItem((Object)alert2) != CallbackResult.DONE) continue;
                        }
                        break;
                    }
                }
                finally {
                    result = callback.onEnd((PageSummary)new SimplePageSummary(pageRequest, count.getValue()));
                }
            }
            return result;
        });
    }

    @Nonnull
    public String subscribe(@Nonnull AlertListener listener) {
        return this.publisher.subscribe(listener);
    }

    public boolean unsubscribe(@Nonnull String subscriptionId) {
        return this.publisher.unsubscribe(subscriptionId);
    }

    private Issue getIssueOrPlaceholder(String issueId, Severity severity) {
        return this.getIssueOrPlaceholder(IssueId.valueOf(issueId), severity);
    }

    private Issue getIssueOrPlaceholder(IssueId issueId, Severity severity) {
        return this.getMonitor(issueId.getComponentId()).flatMap(monitor -> monitor.getIssue(issueId.getCode())).orElseGet(() -> this.getPlaceHolderIssue(issueId, severity));
    }

    private Component getPlaceholderComponent(String componentId) {
        String upperComponentId = componentId.toUpperCase();
        return this.placeholderComponents.computeIfAbsent(upperComponentId, id -> new PlaceholderComponent(upperComponentId));
    }

    private Issue getPlaceHolderIssue(IssueId id, Severity severity) {
        Component component = this.getMonitor(id.getComponentId()).map(ComponentMonitor::getComponent).orElseGet(() -> this.getPlaceholderComponent(id.getComponentId()));
        return this.placeholderIssues.computeIfAbsent(id, issueId -> new PlaceholderIssue(this.i18nResolver, component, id, severity, this.jsonMapper));
    }

    private Alert toAlert(AlertEntity entity) {
        Issue issue = this.getIssueOrPlaceholder(entity.getIssueId(), entity.getIssueSeverity());
        return ((SimpleAlert.Builder)((SimpleAlert.Builder)((SimpleAlert.Builder)((SimpleAlert.Builder)new SimpleAlert.Builder(issue, entity.getNodeName()).id(entity.getId())).detailsAsJson(entity.getDetailsJson())).timestamp(entity.getTimestamp())).trigger(new AlertTrigger.Builder().plugin(entity.getTriggerPluginKey(), entity.getTriggerPluginVersion()).module(entity.getTriggerModule()).build())).build();
    }

    private void truncateAlerts() {
        this.transactionTemplate.execute(() -> {
            this.dao.deleteAll(AlertCriteria.builder().until(Instant.now().minus(this.configuration.getAlertRetentionPeriod())).build());
            return null;
        });
    }

    private class TruncateAlertsJobRunner
    implements JobRunner {
        private TruncateAlertsJobRunner() {
        }

        public JobRunnerResponse runJob(@Nonnull JobRunnerRequest jobRunnerRequest) {
            DefaultMonitoringService.this.truncateAlerts();
            return JobRunnerResponse.success();
        }
    }

    private static class PlaceholderIssue
    extends SimpleIssue {
        PlaceholderIssue(I18nResolver i18nResolver, Component component, IssueId id, Severity severity, JsonMapper jsonMapper) {
            super(i18nResolver, component, id, "diagnostics.unknown.issue", "diagnostics.unknown.issue", (Severity)MoreObjects.firstNonNull((Object)severity, (Object)Severity.WARNING), jsonMapper);
        }

        @Override
        @Nonnull
        public String getSummary() {
            return this.getId();
        }

        @Override
        @Nonnull
        public String getDescription() {
            return this.i18nResolver.getText("diagnostics.unknown.issue", new Serializable[]{this.getId()});
        }
    }

    private static class PlaceholderComponent
    implements Component {
        private final String id;

        PlaceholderComponent(String id) {
            this.id = Objects.requireNonNull(id, "id").toUpperCase(Locale.ROOT);
        }

        @Nonnull
        public String getId() {
            return this.id;
        }

        @Nonnull
        public String getName() {
            return this.id;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).toString();
        }
    }
}

