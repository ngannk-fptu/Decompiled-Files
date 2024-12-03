/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdCustomMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.mail.server.SMTPMailServer
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics.ipd.mail.outgoing;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.internal.diagnostics.ipd.mail.ConnectionVerifier;
import com.atlassian.confluence.internal.diagnostics.ipd.mail.outgoing.DefaultSmtpConnectionVerifier;
import com.atlassian.confluence.internal.diagnostics.ipd.metric.type.IpdConnectionStateType;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdCustomMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.util.profiling.MetricTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutgoingMailServerConnectionIpdJob
implements IpdJob {
    private static final Logger LOG = LoggerFactory.getLogger(OutgoingMailServerConnectionIpdJob.class);
    private final MailServerManager mailServerManager;
    private final ConnectionVerifier connectionVerifier;
    private final IpdMetricRegistry ipdMetricRegistry;
    private IpdCustomMetric<IpdConnectionStateType> smtpConnectionStateMetric;

    public OutgoingMailServerConnectionIpdJob(IpdJobRunner ipdJobRunner, IpdMainRegistry ipdMainRegistry, MailServerManager mailServerManager) {
        this(ipdJobRunner, (IpdMetricRegistry)ipdMainRegistry, mailServerManager, new DefaultSmtpConnectionVerifier());
    }

    @VisibleForTesting
    OutgoingMailServerConnectionIpdJob(IpdJobRunner ipdJobRunner, IpdMetricRegistry ipdMetricRegistry, MailServerManager mailServerManager, ConnectionVerifier connectionVerifier) {
        this.mailServerManager = mailServerManager;
        this.connectionVerifier = connectionVerifier;
        this.ipdMetricRegistry = ipdMetricRegistry;
        ipdJobRunner.register((IpdJob)this);
    }

    public void runJob() {
        SMTPMailServer smtpMailServer = this.mailServerManager.getDefaultSMTPMailServer();
        if (smtpMailServer == null) {
            LOG.debug("IPD metric 'mail.outgoing.connection.state' skipped, SMTP server is not configured");
            if (this.smtpConnectionStateMetric != null) {
                this.ipdMetricRegistry.remove(this.smtpConnectionStateMetric.getMetricKey());
                this.smtpConnectionStateMetric = null;
            }
            return;
        }
        this.smtpConnectionStateMetric = (IpdCustomMetric)this.ipdMetricRegistry.register(IpdCustomMetric.builder((String)"mail.outgoing.connection.state", IpdConnectionStateType.class, (MetricTag.RequiredMetricTag[])new MetricTag.RequiredMetricTag[0]).asWorkInProgress());
        try {
            this.connectionVerifier.verifyConnection((MailServer)smtpMailServer);
            this.smtpConnectionStateMetric.update(m -> m.setConnected(true));
        }
        catch (Exception e) {
            this.smtpConnectionStateMetric.update(m -> m.setConnected(false));
            LOG.debug("IPD metric 'mail.outgoing.connection.state' failed", (Throwable)e);
        }
    }

    public boolean isWorkInProgressJob() {
        return true;
    }
}

