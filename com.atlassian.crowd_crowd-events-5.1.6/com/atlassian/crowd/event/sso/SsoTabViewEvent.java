/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.sso;

public class SsoTabViewEvent {
    private final boolean isSsoAvailable;

    public SsoTabViewEvent(boolean isSsoAvailable) {
        this.isSsoAvailable = isSsoAvailable;
    }

    public boolean isSsoAvailable() {
        return this.isSsoAvailable;
    }
}

