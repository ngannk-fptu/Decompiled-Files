/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.oauth2.OAuth2Service
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.MailServerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics.ipd.mail.incoming;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.internal.diagnostics.ipd.IpdExecutors;
import com.atlassian.confluence.internal.diagnostics.ipd.mail.ConnectionVerifier;
import com.atlassian.confluence.internal.diagnostics.ipd.mail.incoming.DefaultIncomingConnectionVerifier;
import com.atlassian.confluence.internal.diagnostics.ipd.mail.incoming.IpdIncomingMailServersStats;
import com.atlassian.confluence.oauth2.OAuth2Service;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.MailServerManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncomingMailServerConnectionIpdJob
implements IpdJob {
    private static final Logger LOG = LoggerFactory.getLogger(IncomingMailServerConnectionIpdJob.class);
    private final MailServerManager mailServerManager;
    private final IpdIncomingMailServersStats ipdIncomingMailServersStats;
    private final ConnectionVerifier connectionVerifier;
    private final ExecutorService executorService;

    public IncomingMailServerConnectionIpdJob(IpdJobRunner ipdJobRunner, IpdIncomingMailServersStats ipdIncomingMailServersStats, MailServerManager mailServerManager, IpdExecutors ipdExecutors, OAuth2Service oAuth2Service) {
        this(ipdJobRunner, ipdIncomingMailServersStats, ipdExecutors.createSingleTaskExecutorService("ipd-incoming-mail"), new DefaultIncomingConnectionVerifier(oAuth2Service), mailServerManager);
    }

    @VisibleForTesting
    IncomingMailServerConnectionIpdJob(IpdJobRunner ipdJobRunner, IpdIncomingMailServersStats ipdIncomingMailServersStats, ExecutorService executorService, ConnectionVerifier connectionVerifier, MailServerManager mailServerManager) {
        this.connectionVerifier = connectionVerifier;
        ipdJobRunner.register((IpdJob)this);
        this.ipdIncomingMailServersStats = ipdIncomingMailServersStats;
        this.executorService = executorService;
        this.mailServerManager = mailServerManager;
    }

    public void runJob() {
        List<MailServer> incomingMailServers = this.getIncomingMailServers();
        this.ipdIncomingMailServersStats.remainMetricsForMailServers(incomingMailServers);
        if (incomingMailServers.isEmpty()) {
            return;
        }
        try {
            this.executorService.execute(() -> this.generateMetrics(incomingMailServers));
        }
        catch (RejectedExecutionException e) {
            LOG.debug("Unable to instantiate new process to check mail server connections, previous process is still running", (Throwable)e);
        }
    }

    @VisibleForTesting
    void generateMetrics(List<MailServer> mailServers) {
        mailServers.parallelStream().forEach(server -> {
            try {
                this.connectionVerifier.verifyConnection((MailServer)server);
                this.ipdIncomingMailServersStats.setConnected((MailServer)server);
            }
            catch (Exception e) {
                this.ipdIncomingMailServersStats.setDisconnected((MailServer)server);
                LOG.debug("IPD metric incoming mail servers connection for server '" + server.getName() + "' failed", (Throwable)e);
            }
        });
    }

    public boolean isWorkInProgressJob() {
        return true;
    }

    private List<MailServer> getIncomingMailServers() {
        return Stream.concat(this.mailServerManager.getImapMailServers().stream(), this.mailServerManager.getPopMailServers().stream()).map(MailServer.class::cast).collect(Collectors.toList());
    }
}

