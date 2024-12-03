/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mail.server.MailServer
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.confluence.plugins.emailgateway.api.EmailGatewaySettingsManager;
import com.atlassian.confluence.plugins.emailgateway.api.analytics.CreatePageByEmailAnalytics;
import com.atlassian.confluence.plugins.emailgateway.api.analytics.ReplyToCommentByEmailAnalytics;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mail.server.MailServer;
import java.util.Optional;

public class DefaultEmailGatewaySettingsManager
implements EmailGatewaySettingsManager {
    private static final BandanaContext BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT = new ConfluenceBandanaContext("email-gateway-configuration");
    private final BandanaManager bandanaManager;
    private final EventPublisher eventPublisher;

    public DefaultEmailGatewaySettingsManager(BandanaManager bandanaManager, EventPublisher eventPublisher) {
        this.bandanaManager = bandanaManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean isAllowToCreatePageByEmail() {
        return this.getBoolean("com.atlassian.confluence.plugins.emailgateway.allow.create.page", false);
    }

    @Override
    public boolean isAllowToCreateCommentByEmail() {
        return this.getBoolean("com.atlassian.confluence.plugins.emailgateway.allow.create.comment", false);
    }

    @Override
    public InboundMailServer getDefaultInboundMailServer() {
        return (InboundMailServer)this.bandanaManager.getValue(BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT, "com.atlassian.confluence.plugins.emailgateway.default.pop.server");
    }

    @Override
    public void setAllowToCreatePageByEmail(boolean allowToCreatePageByEmail) {
        this.bandanaManager.setValue(BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT, "com.atlassian.confluence.plugins.emailgateway.allow.create.page", (Object)allowToCreatePageByEmail);
        this.eventPublisher.publish(allowToCreatePageByEmail ? new CreatePageByEmailAnalytics.EnableFeatureEvent() : new CreatePageByEmailAnalytics.DisableFeatureEvent());
    }

    @Override
    public void setAllowToCreateCommentByEmail(boolean allowToCreateCommentByEmail) {
        this.bandanaManager.setValue(BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT, "com.atlassian.confluence.plugins.emailgateway.allow.create.comment", (Object)allowToCreateCommentByEmail);
        this.eventPublisher.publish(allowToCreateCommentByEmail ? new ReplyToCommentByEmailAnalytics.EnableFeatureEvent() : new ReplyToCommentByEmailAnalytics.DisableFeatureEvent());
    }

    @Override
    public void setDefaultMailServer(MailServer mailServer) {
        this.bandanaManager.setValue(BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT, "com.atlassian.confluence.plugins.emailgateway.default.pop.server", (Object)mailServer);
    }

    private boolean getBoolean(String key, boolean fallback) {
        return (Boolean)Optional.ofNullable(this.bandanaManager.getValue(BANDANA_EMAIL_GATEWAY_CONFIGURATION_CONTEXT, key)).orElse(fallback);
    }
}

