/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.annotations.Internal;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import java.time.ZonedDateTime;
import javax.annotation.Nonnull;

@Internal
public interface IdpConfig {
    public Long getId();

    public String getName();

    @Nonnull
    public SsoType getSsoType();

    public boolean isEnabled();

    public String getIssuer();

    public boolean isIncludeCustomerLogins();

    public boolean isEnableRememberMe();

    public ZonedDateTime getLastUpdated();

    public String getButtonText();

    public JustInTimeConfig getJustInTimeConfig();
}

