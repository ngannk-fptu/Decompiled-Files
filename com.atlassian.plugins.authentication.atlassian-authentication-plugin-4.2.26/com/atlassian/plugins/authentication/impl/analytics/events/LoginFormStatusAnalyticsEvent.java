/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.authentication.impl.analytics.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.impl.analytics.events.AnalyticsEvent;
import javax.annotation.Nonnull;

public class LoginFormStatusAnalyticsEvent
implements AnalyticsEvent {
    private final SsoConfig ssoConfig;

    public LoginFormStatusAnalyticsEvent(@Nonnull SsoConfig ssoConfig) {
        this.ssoConfig = ssoConfig;
    }

    @Override
    @EventName
    public String getEventName() {
        return "plugins.authentication.status.authmethod.native";
    }

    public boolean isEnabled() {
        return this.ssoConfig.getShowLoginForm();
    }

    public boolean isJsmEnabled() {
        return this.ssoConfig.getShowLoginFormForJsm();
    }
}

