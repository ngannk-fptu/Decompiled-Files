/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertRequest$Builder
 *  com.atlassian.diagnostics.ComponentMonitor
 *  com.atlassian.diagnostics.JsonMapper
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.AlertRequest;
import com.atlassian.diagnostics.ComponentMonitor;
import com.atlassian.diagnostics.JsonMapper;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.JacksonJsonMapper;
import com.atlassian.diagnostics.internal.concurrent.Gate;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;

public abstract class InitializingMonitor {
    protected volatile ComponentMonitor monitor;
    private Map<Integer, Gate> issueGates = new ConcurrentHashMap<Integer, Gate>();

    public abstract void init(MonitoringService var1);

    protected void defineIssue(String i18nPrefix, int id, Severity severity) {
        this.defineIssue(i18nPrefix, id, severity, null);
    }

    protected void defineIssue(String i18nPrefix, int id, Severity severity, Class<?> detailsClass) {
        String keyPrefix = i18nPrefix + "." + StringUtils.leftPad((String)Integer.toString(id), (int)4, (char)'0') + ".";
        this.monitor.defineIssue(id).summaryI18nKey(keyPrefix + "summary").descriptionI18nKey(keyPrefix + "description").jsonMapper((JsonMapper)(detailsClass == null ? null : new JacksonJsonMapper(detailsClass))).severity(severity).build();
        this.issueGates.put(id, new Gate(Duration.ofMinutes(15L)));
    }

    protected void alert(int issueId, Consumer<AlertRequest.Builder> alertBuilder) {
        if (this.monitor != null && this.monitor.isEnabled()) {
            this.monitor.getIssue(issueId).ifPresent(issue -> this.issueGates.get(issueId).ifAccessible(() -> {
                AlertRequest.Builder builder = new AlertRequest.Builder(issue);
                alertBuilder.accept(builder);
                this.monitor.alert(builder.build());
            }));
        }
    }
}

