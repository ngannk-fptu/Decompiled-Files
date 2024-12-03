/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.authentication;

import com.atlassian.crowd.manager.rememberme.CrowdSpecificRememberMeSettings;
import java.time.Duration;
import java.util.Objects;

public class ImmutableCrowdSpecificRememberMeSettings
implements CrowdSpecificRememberMeSettings {
    private final boolean enabled;
    private final Duration expirationPeriod;

    public ImmutableCrowdSpecificRememberMeSettings(boolean enabled, Duration expirationPeriod) {
        this.enabled = enabled;
        this.expirationPeriod = expirationPeriod;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public Duration getExpirationDuration() {
        return this.expirationPeriod;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableCrowdSpecificRememberMeSettings that = (ImmutableCrowdSpecificRememberMeSettings)o;
        return this.enabled == that.enabled && Objects.equals(this.expirationPeriod, that.expirationPeriod);
    }

    public int hashCode() {
        return Objects.hash(this.enabled, this.expirationPeriod);
    }
}

