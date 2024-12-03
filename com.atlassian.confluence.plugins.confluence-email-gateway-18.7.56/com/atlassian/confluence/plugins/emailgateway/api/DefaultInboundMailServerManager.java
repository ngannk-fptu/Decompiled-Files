/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.MailServerManager
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.confluence.plugins.emailgateway.api.EmailGatewaySettingsManager;
import com.atlassian.confluence.plugins.emailgateway.api.InboundMailServerManager;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.MailServerManager;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class DefaultInboundMailServerManager
implements InboundMailServerManager {
    private final EmailGatewaySettingsManager emailGatewaySettingsManager;
    private final MailServerManager mailServerManager;

    public DefaultInboundMailServerManager(EmailGatewaySettingsManager emailGatewaySettingsManager, MailServerManager mailServerManager) {
        this.emailGatewaySettingsManager = Objects.requireNonNull(emailGatewaySettingsManager);
        this.mailServerManager = Objects.requireNonNull(mailServerManager);
    }

    @Override
    public MailServer getMailServer() {
        InboundMailServer mailServer = this.emailGatewaySettingsManager.getDefaultInboundMailServer();
        Optional<Object> configServer = Optional.empty();
        if (mailServer != null) {
            configServer = Stream.concat(this.mailServerManager.getPopMailServers().stream(), this.mailServerManager.getImapMailServers().stream()).filter(arg_0 -> DefaultInboundMailServerManager.lambda$getMailServer$0((MailServer)mailServer, arg_0)).findFirst();
        }
        return (MailServer)configServer.orElse(this.mailServerManager.getDefaultPopMailServer());
    }

    private static /* synthetic */ boolean lambda$getMailServer$0(MailServer mailServer, MailServer server) {
        return server.getId().equals(mailServer.getId());
    }
}

