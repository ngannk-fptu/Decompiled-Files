/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.authentication.impl.analytics.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.impl.analytics.events.AnalyticsEvent;
import java.util.List;
import javax.annotation.Nonnull;

public class AuthFallbackStatusAnalyticsEvent
implements AnalyticsEvent {
    private final SsoConfig ssoConfig;
    private final List<IdpConfig> idpConfigs;

    public AuthFallbackStatusAnalyticsEvent(@Nonnull SsoConfig ssoConfig, @Nonnull List<IdpConfig> idpConfigs) {
        this.ssoConfig = ssoConfig;
        this.idpConfigs = idpConfigs;
    }

    @Override
    @EventName
    public String getEventName() {
        return "plugins.authentication.status." + (this.ssoConfig.enableAuthenticationFallback() ? "redirectoverride.enabled" : "redirectoverride.disabled");
    }

    @Override
    public boolean shouldPublish() {
        return !this.idpConfigs.isEmpty();
    }
}

