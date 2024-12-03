/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.event;

import java.util.Objects;

public class JfrFeatureFlagStateChangedEvent {
    private final boolean enabled;

    public JfrFeatureFlagStateChangedEvent(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JfrFeatureFlagStateChangedEvent that = (JfrFeatureFlagStateChangedEvent)o;
        return this.enabled == that.enabled;
    }

    public int hashCode() {
        return Objects.hash(this.enabled);
    }
}

