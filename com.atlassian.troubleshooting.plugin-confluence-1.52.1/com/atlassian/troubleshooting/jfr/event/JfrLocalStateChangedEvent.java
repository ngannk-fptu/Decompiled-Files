/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.event;

public class JfrLocalStateChangedEvent {
    private final boolean enabled;
    private final boolean onStart;

    public JfrLocalStateChangedEvent(boolean enabled) {
        this(enabled, false);
    }

    public JfrLocalStateChangedEvent(boolean enabled, boolean onStart) {
        this.enabled = enabled;
        this.onStart = onStart;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isOnStart() {
        return this.onStart;
    }
}

