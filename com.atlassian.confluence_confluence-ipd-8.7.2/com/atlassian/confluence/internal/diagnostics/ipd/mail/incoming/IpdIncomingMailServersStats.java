/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdCustomMetric
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.util.profiling.MetricTag
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.mail.incoming;

import com.atlassian.confluence.internal.diagnostics.ipd.metric.type.IpdConnectionStateType;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdCustomMetric;
import com.atlassian.mail.server.MailServer;
import com.atlassian.util.profiling.MetricTag;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class IpdIncomingMailServersStats {
    public static final String MAIL_INCOMING_CONNECTION_STATE = "mail.incoming.connection.state";
    public static final String SERVER_NAME = "serverName";
    private final IpdMetricRegistry registry;

    public IpdIncomingMailServersStats(IpdMainRegistry mainRegistry) {
        this.registry = mainRegistry.createRegistry(b -> b.withPrefix(MAIL_INCOMING_CONNECTION_STATE).asWorkInProgress());
    }

    public void setConnected(MailServer server) {
        this.metric(server).update(b -> b.setConnected(true));
    }

    public void setDisconnected(MailServer server) {
        this.metric(server).update(b -> b.setConnected(false));
    }

    public void remainMetricsForMailServers(Collection<MailServer> servers) {
        Set serverNames = servers.stream().map(MailServer::getName).collect(Collectors.toSet());
        this.registry.removeIf(metric -> !serverNames.contains(metric.getObjectName().getKeyProperty("tag.serverName")));
    }

    private IpdCustomMetric<IpdConnectionStateType> metric(MailServer server) {
        return this.registry.customMetric("", IpdConnectionStateType.class, new MetricTag.RequiredMetricTag[]{MetricTag.of((String)SERVER_NAME, (String)server.getName())});
    }
}

