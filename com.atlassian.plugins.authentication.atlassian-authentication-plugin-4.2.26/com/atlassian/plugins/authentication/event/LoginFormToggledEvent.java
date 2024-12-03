/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.event;

public class LoginFormToggledEvent {
    private final boolean enabled;

    public LoginFormToggledEvent(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

