/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import java.util.Objects;
import java.util.Optional;

public class ZduStatus {
    private final State state;
    private final String originalClusterVersion;

    public ZduStatus(State state, String originalClusterVersion) {
        this.state = state;
        this.originalClusterVersion = originalClusterVersion;
    }

    public static ZduStatus enabled(String originalClusterVersion) {
        return new ZduStatus(State.ENABLED, Objects.requireNonNull(originalClusterVersion));
    }

    public static ZduStatus disabled() {
        return new ZduStatus(State.DISABLED, null);
    }

    public State getState() {
        return this.state;
    }

    public Optional<String> getOriginalClusterVersion() {
        return Optional.ofNullable(this.originalClusterVersion);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ZduStatus zduStatus = (ZduStatus)o;
        return this.state == zduStatus.state && Objects.equals(this.originalClusterVersion, zduStatus.originalClusterVersion);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.state, this.originalClusterVersion});
    }

    public String toString() {
        return "ZduStatus{state=" + this.state + ", originalClusterVersion='" + this.originalClusterVersion + "'}";
    }

    public static enum State {
        DISABLED,
        ENABLED;

    }
}

