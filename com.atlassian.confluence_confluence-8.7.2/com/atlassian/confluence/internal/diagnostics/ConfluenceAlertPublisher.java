/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertListener
 *  com.atlassian.diagnostics.internal.AlertPublisher
 *  com.atlassian.diagnostics.internal.LockFreeAlertPublisher
 *  com.atlassian.diagnostics.internal.LoggingAlertListener
 *  com.atlassian.diagnostics.internal.PersistingAlertListener
 *  com.atlassian.diagnostics.internal.PluginHelper
 *  com.atlassian.diagnostics.internal.dao.AlertEntityDao
 *  com.atlassian.diagnostics.internal.jmx.JmxReportingAlertListener
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.ImmutableList
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertListener;
import com.atlassian.diagnostics.internal.AlertPublisher;
import com.atlassian.diagnostics.internal.LockFreeAlertPublisher;
import com.atlassian.diagnostics.internal.LoggingAlertListener;
import com.atlassian.diagnostics.internal.PersistingAlertListener;
import com.atlassian.diagnostics.internal.PluginHelper;
import com.atlassian.diagnostics.internal.dao.AlertEntityDao;
import com.atlassian.diagnostics.internal.jmx.JmxReportingAlertListener;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.concurrent.Executor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.LoggerFactory;

public class ConfluenceAlertPublisher
implements AlertPublisher {
    private final AlertPublisher delegate;

    public ConfluenceAlertPublisher(PluginHelper pluginHelper, AlertEntityDao alertEntityDao, Executor confluenceExecutor, TransactionTemplate transactionTemplate) {
        LoggingAlertListener loggingAlertListener = new LoggingAlertListener();
        loggingAlertListener.setDataLogger(LoggerFactory.getLogger((String)"confluence.alert-log"));
        loggingAlertListener.setRegularLogger(LoggerFactory.getLogger((String)"atlassian-monitor"));
        JmxReportingAlertListener jmxReportingAlertListener = new JmxReportingAlertListener(pluginHelper);
        PersistingAlertListener persistingAlertListener = new PersistingAlertListener(alertEntityDao, transactionTemplate);
        this.delegate = new LockFreeAlertPublisher((Collection)ImmutableList.of((Object)loggingAlertListener, (Object)jmxReportingAlertListener, (Object)persistingAlertListener), confluenceExecutor, pluginHelper);
    }

    public void publish(@NonNull Alert alert) {
        this.delegate.publish(alert);
    }

    public @NonNull String subscribe(@NonNull AlertListener alertListener) {
        return this.delegate.subscribe(alertListener);
    }

    public boolean unsubscribe(@NonNull String subscriptionId) {
        return this.delegate.unsubscribe(subscriptionId);
    }
}

