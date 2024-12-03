/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.poller;

import java.util.Objects;

public class PollerInfo {
    private final String key;
    private final boolean running;

    public PollerInfo(String key, boolean running) {
        this.key = key;
        this.running = running;
    }

    public String getKey() {
        return this.key;
    }

    public boolean isRunning() {
        return this.running;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PollerInfo that = (PollerInfo)o;
        return this.running == that.running && Objects.equals(this.key, that.key);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.running);
    }
}

