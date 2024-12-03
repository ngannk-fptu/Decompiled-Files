/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertListener
 *  com.atlassian.diagnostics.Issue
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.jmx;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertListener;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.internal.PluginHelper;
import com.atlassian.diagnostics.internal.jmx.Alerts;
import com.atlassian.diagnostics.internal.jmx.IssueAlerts;
import com.atlassian.diagnostics.internal.jmx.PluginAlerts;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nonnull;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxReportingAlertListener
implements AlertListener {
    private static final Logger log = LoggerFactory.getLogger(JmxReportingAlertListener.class);
    private final Alerts alerts;
    private final ConcurrentMap<String, IssueAlerts> alertsByIssue;
    private final ConcurrentMap<String, PluginAlerts> alertsByPlugin;
    private final PluginHelper pluginHelper;

    public JmxReportingAlertListener(PluginHelper pluginHelper) {
        this.pluginHelper = pluginHelper;
        this.alerts = new Alerts();
        this.alertsByIssue = new ConcurrentHashMap<String, IssueAlerts>();
        this.alertsByPlugin = new ConcurrentHashMap<String, PluginAlerts>();
        this.registerJmxBean("type=Alerts,name=Total", this.alerts);
    }

    public void onAlert(@Nonnull Alert alert) {
        this.alerts.onAlert(alert);
        this.alertsByPlugin.computeIfAbsent(alert.getTrigger().getPluginKey(), this::createPluginAlerts).onAlert(alert);
        this.alertsByIssue.computeIfAbsent(alert.getIssue().getId(), id -> this.createIssueAlerts(alert.getIssue())).onAlert(alert);
    }

    private IssueAlerts createIssueAlerts(Issue issue) {
        IssueAlerts result = new IssueAlerts(issue);
        this.registerJmxBean("type=Alerts,Category=Issue,name=" + issue.getId(), result);
        return result;
    }

    private PluginAlerts createPluginAlerts(String pluginKey) {
        PluginAlerts result;
        if ("System".equalsIgnoreCase(pluginKey)) {
            result = new PluginAlerts("System");
        } else {
            result = new PluginAlerts(this.pluginHelper.getPluginName(pluginKey));
            this.registerJmxBean("type=Alerts,Category=Plugin,name=" + this.maybeQuote(pluginKey), result);
        }
        return result;
    }

    private String maybeQuote(String value) {
        if (StringUtils.containsAny((CharSequence)value, (char[])new char[]{','})) {
            return ObjectName.quote(value);
        }
        return value;
    }

    private void registerJmxBean(String objectNameProperties, Object alerts) {
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName mxbeanName = new ObjectName("com.atlassian.diagnostics:" + objectNameProperties);
            mbeanServer.registerMBean(alerts, mxbeanName);
        }
        catch (RuntimeException | JMException e) {
            log.warn("Failed to register Alerts JMX bean", (Throwable)e);
        }
    }
}

