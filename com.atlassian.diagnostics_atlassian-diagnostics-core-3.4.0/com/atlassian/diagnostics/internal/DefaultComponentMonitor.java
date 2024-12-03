/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertRequest
 *  com.atlassian.diagnostics.Component
 *  com.atlassian.diagnostics.DiagnosticsConfiguration
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.IssueBuilder
 *  com.atlassian.diagnostics.JsonMapper
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.AlertRequest;
import com.atlassian.diagnostics.Component;
import com.atlassian.diagnostics.DiagnosticsConfiguration;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.IssueBuilder;
import com.atlassian.diagnostics.JsonMapper;
import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.AlertPublisher;
import com.atlassian.diagnostics.internal.InternalComponentMonitor;
import com.atlassian.diagnostics.internal.IssueId;
import com.atlassian.diagnostics.internal.SimpleAlert;
import com.atlassian.diagnostics.internal.SimpleIssue;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultComponentMonitor
implements InternalComponentMonitor {
    private static final Logger log = LoggerFactory.getLogger(DefaultComponentMonitor.class);
    private final Component component;
    private final DiagnosticsConfiguration configuration;
    private final MonitorConfiguration monitorConfiguration;
    private final JsonMapper defaultJsonMapper;
    private final I18nResolver i18nResolver;
    private final ConcurrentMap<Integer, Issue> issues;
    private final AlertPublisher publisher;
    private final AtomicBoolean destroyed = new AtomicBoolean();

    @VisibleForTesting
    DefaultComponentMonitor(Component component, DiagnosticsConfiguration configuration, MonitorConfiguration monitorConfiguration, I18nResolver i18nResolver, JsonMapper defaultJsonMapper, AlertPublisher publisher) {
        this.component = component;
        this.configuration = configuration;
        this.monitorConfiguration = monitorConfiguration;
        this.i18nResolver = i18nResolver;
        this.defaultJsonMapper = defaultJsonMapper;
        this.publisher = publisher;
        this.issues = new ConcurrentHashMap<Integer, Issue>();
    }

    public void alert(@Nonnull AlertRequest request) {
        this.checkMonitorState();
        if (!this.isEnabled()) {
            return;
        }
        Objects.requireNonNull(request, "request");
        Issue issue = request.getIssue();
        if (!this.component.equals(issue.getComponent())) {
            throw new IllegalArgumentException("Issue " + issue.getId() + " is unknown for component " + this.component.getId());
        }
        SimpleAlert alert = ((SimpleAlert.Builder)((SimpleAlert.Builder)((SimpleAlert.Builder)new SimpleAlert.Builder(issue, this.configuration.getNodeName()).details(request.getDetailsSupplier().map(Supplier::get).orElse(null))).trigger(request.getTrigger().orElse(null))).timestamp(request.getTimestamp())).build();
        this.publisher.publish(alert);
    }

    @Nonnull
    public IssueBuilder defineIssue(int issueId) {
        this.checkMonitorState();
        if (issueId <= 0 || issueId > 9999) {
            throw new IllegalArgumentException("issueId must be greater than 0 and less than 10000");
        }
        if (this.issues.containsKey(issueId)) {
            throw new IllegalStateException("Issue " + issueId + " is already defined");
        }
        return new ComponentIssueBuilder(issueId).jsonMapper(this.defaultJsonMapper);
    }

    @Nonnull
    public Component getComponent() {
        return this.component;
    }

    @Nonnull
    public Optional<Issue> getIssue(int issueId) {
        return Optional.ofNullable(this.issues.get(issueId));
    }

    @Nonnull
    public List<Issue> getIssues() {
        return ImmutableList.copyOf(this.issues.values());
    }

    public boolean isEnabled() {
        this.checkMonitorState();
        return this.configuration.isEnabled() && this.monitorConfiguration.isEnabled();
    }

    private void checkMonitorState() {
        if (this.destroyed.get()) {
            log.error("ComponentMonitor '{}' has been destroyed", (Object)this.component.getId());
        }
    }

    @Override
    public void destroy() {
        this.checkMonitorState();
        this.destroyed.set(true);
    }

    private class ComponentIssueBuilder
    implements IssueBuilder {
        private final int id;
        private String descriptionI18nKey;
        private JsonMapper jsonMapper;
        private String summaryI18nKey;
        private Severity severity;

        ComponentIssueBuilder(int id) {
            DefaultComponentMonitor.this.checkMonitorState();
            this.id = id;
            this.severity = Severity.INFO;
        }

        @Nonnull
        public Issue build() {
            SimpleIssue issue = new SimpleIssue(DefaultComponentMonitor.this.i18nResolver, DefaultComponentMonitor.this.component, new IssueId(DefaultComponentMonitor.this.component.getId(), this.id), this.summaryI18nKey, this.descriptionI18nKey, this.severity, (JsonMapper)MoreObjects.firstNonNull((Object)this.jsonMapper, (Object)DefaultComponentMonitor.this.defaultJsonMapper));
            Issue existing = DefaultComponentMonitor.this.issues.putIfAbsent(this.id, issue);
            if (existing != null) {
                throw new IllegalStateException("Issue " + this.id + " has already been defined");
            }
            return issue;
        }

        @Nonnull
        public ComponentIssueBuilder descriptionI18nKey(@Nonnull String value) {
            this.descriptionI18nKey = Objects.requireNonNull(value, "descriptionI18nKey");
            return this;
        }

        @Nonnull
        public ComponentIssueBuilder jsonMapper(JsonMapper value) {
            this.jsonMapper = value;
            return this;
        }

        @Nonnull
        public ComponentIssueBuilder severity(@Nonnull Severity value) {
            this.severity = Objects.requireNonNull(value, "severity");
            return this;
        }

        @Nonnull
        public ComponentIssueBuilder summaryI18nKey(@Nonnull String value) {
            this.summaryI18nKey = Objects.requireNonNull(value, "summaryI18nKey");
            return this;
        }
    }
}

