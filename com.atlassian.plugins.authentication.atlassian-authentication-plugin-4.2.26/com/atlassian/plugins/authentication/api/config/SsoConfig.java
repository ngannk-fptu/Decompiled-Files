/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.annotations.Internal;
import java.time.ZonedDateTime;

@Internal
public interface SsoConfig {
    public boolean enableAuthenticationFallback();

    public String getDiscoveryRefreshCron();

    public boolean getShowLoginForm();

    public boolean getShowLoginFormForJsm();

    public ZonedDateTime getLastUpdated();
}

