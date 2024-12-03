/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mail.server.ImapMailServer
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.PopMailServer
 *  com.atlassian.mail.server.SMTPMailServer
 *  com.atlassian.mail.server.managers.AbstractMailServerManager
 *  com.google.common.collect.Iterables
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 */
package com.atlassian.confluence.mail;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.event.events.admin.MailServerCreateEvent;
import com.atlassian.confluence.event.events.admin.MailServerDeleteEvent;
import com.atlassian.confluence.event.events.admin.MailServerEditEvent;
import com.atlassian.confluence.event.events.analytics.MailServerAnalytics;
import com.atlassian.confluence.mail.PasswordFilteringLogPrintStream;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mail.server.ImapMailServer;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.mail.server.managers.AbstractMailServerManager;
import com.google.common.collect.Iterables;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ConfluenceMailServerManager
extends AbstractMailServerManager {
    private static final Logger logger = Logger.getLogger(ConfluenceMailServerManager.class);
    private static final Logger loggerSession = Logger.getLogger((String)"com.atlassian.confluence.mail.session");
    public static final String TLS_HOSTNAME_VERIFICATION_DISABLED = "confluence.mailserver.tls.hostname.verification.disabled";
    private BandanaManager bandanaManager;
    private EventPublisher eventPublisher;
    private final boolean isTLSHostNameVerificationEnabled = !Boolean.getBoolean("confluence.mailserver.tls.hostname.verification.disabled");

    public MailServer getMailServer(Long id) {
        return this.getMailServers().get(id);
    }

    public MailServer getMailServer(String name) {
        return this.getMailServers().values().stream().filter(server -> name.equals(server.getName())).findAny().orElse(null);
    }

    public List<String> getServerNames() {
        return this.getMailServers().values().stream().map(MailServer::getName).collect(Collectors.toList());
    }

    public List<SMTPMailServer> getSmtpMailServers() {
        return this.getMailServers(SMTPMailServer.class);
    }

    public List<PopMailServer> getPopMailServers() {
        return this.getMailServers(PopMailServer.class);
    }

    public List<ImapMailServer> getImapMailServers() {
        return this.getMailServers(ImapMailServer.class);
    }

    public SMTPMailServer getDefaultSMTPMailServer() {
        return (SMTPMailServer)Iterables.getFirst(this.getSmtpMailServers(), null);
    }

    public PopMailServer getDefaultPopMailServer() {
        return (PopMailServer)Iterables.getFirst(this.getPopMailServers(), null);
    }

    public Long create(MailServer mailServer) {
        if (mailServer instanceof SMTPMailServer || mailServer instanceof PopMailServer || mailServer instanceof ImapMailServer) {
            Map<Long, MailServer> mailServers = this.getMailServers();
            Long id = System.currentTimeMillis();
            mailServer.setId(id);
            this.configureMailServer(mailServer);
            mailServers.put(id, mailServer);
            this.saveServerMap(mailServers);
            this.publishCreateEvent(mailServer);
            return id;
        }
        String message = "Attempt to create unsupported mail server type: " + mailServer.getClass().getName();
        logger.warn((Object)message);
        throw new UnsupportedOperationException(message);
    }

    public void update(MailServer mailServer) {
        Map<Long, MailServer> serverMap = this.getMailServers();
        serverMap.put(mailServer.getId(), mailServer);
        this.configureMailServer(mailServer);
        this.saveServerMap(serverMap);
        this.publishEditEvent(mailServer);
    }

    public void delete(Long mailServerId) {
        Map<Long, MailServer> serverMap = this.getMailServers();
        MailServer mailServer = serverMap.remove(mailServerId);
        this.saveServerMap(serverMap);
        this.publishDeleteEvent(mailServer);
    }

    public void deleteAll() {
        this.getMailServers().values().forEach(this::publishDeleteEvent);
        this.saveServerMap(null);
    }

    private void publishDeleteEvent(MailServer mailServer) {
        logger.info((Object)("Deleted " + this.getServerDescription(mailServer)));
        this.eventPublisher.publish((Object)new MailServerDeleteEvent((Object)this, mailServer));
        switch (mailServer.getType()) {
            case "pop": 
            case "imap": {
                this.eventPublisher.publish((Object)new MailServerAnalytics.InboundServerDeleted(mailServer.getType()));
                break;
            }
            default: {
                this.eventPublisher.publish((Object)new MailServerAnalytics.OutboundServerDeleted(mailServer.getType()));
            }
        }
    }

    private void publishEditEvent(MailServer mailServer) {
        logger.info((Object)("Updated " + this.getServerDescription(mailServer)));
        this.eventPublisher.publish((Object)new MailServerEditEvent((Object)this, mailServer, mailServer.getName()));
    }

    private void publishCreateEvent(MailServer mailServer) {
        logger.info((Object)("Created " + this.getServerDescription(mailServer)));
        this.eventPublisher.publish((Object)new MailServerCreateEvent((Object)this, mailServer));
        switch (mailServer.getType()) {
            case "pop": 
            case "imap": {
                this.eventPublisher.publish((Object)new MailServerAnalytics.InboundServerCreated(mailServer.getType()));
                break;
            }
            default: {
                this.eventPublisher.publish((Object)new MailServerAnalytics.OutboundServerCreated(mailServer.getType()));
            }
        }
    }

    private String getServerDescription(MailServer server) {
        return String.format("'%s' mail server '%s' at %s:%s", server.getType(), server.getName(), server.getHostname(), server.getPort());
    }

    private Map<Long, MailServer> getMailServers() {
        Map mailServers = (Map)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.smtp.mail.accounts");
        mailServers = Optional.ofNullable(mailServers).orElse(new LinkedHashMap());
        mailServers.values().forEach(this::configureMailServer);
        return mailServers;
    }

    private List<? extends MailServer> getMailServers(Class<? extends MailServer> clazz) {
        return this.getMailServers().values().stream().filter(clazz::isInstance).collect(Collectors.toList());
    }

    private void configureMailServer(MailServer mailServer) {
        if (this.isTLSHostNameVerificationEnabled) {
            this.enableTLSHostNameVerificationOnSMTPServers(mailServer);
        }
        this.configureLogging(mailServer);
        Optional.ofNullable(this.getMailServerConfigurationHandler()).ifPresent(configHandler -> configHandler.configureMailServer(mailServer));
    }

    private void saveServerMap(Map<Long, MailServer> serverMap) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.smtp.mail.accounts", serverMap);
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    private void enableTLSHostNameVerificationOnSMTPServers(MailServer mailServer) {
        if (mailServer instanceof SMTPMailServer) {
            mailServer.setTlsHostnameCheckRequired(true);
        }
    }

    private void configureLogging(MailServer mailServer) {
        try {
            mailServer.setDebugStream(new PrintStream((OutputStream)new PasswordFilteringLogPrintStream(loggerSession, Level.DEBUG, mailServer.getPassword()), true, StandardCharsets.UTF_8.name()));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        mailServer.setDebug(Boolean.getBoolean("mail.debug"));
        mailServer.setLogger(logger);
    }
}

