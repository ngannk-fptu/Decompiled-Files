/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.util.Preconditions;
import java.util.HashSet;
import java.util.Set;

public class MulticastConfig {
    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_MULTICAST_GROUP = "224.2.2.3";
    public static final int DEFAULT_MULTICAST_PORT = 54327;
    public static final int DEFAULT_MULTICAST_TIMEOUT_SECONDS = 2;
    public static final int DEFAULT_MULTICAST_TTL = 32;
    public static final boolean DEFAULT_LOOPBACK_MODE_ENABLED = false;
    private static final int MULTICAST_TTL_UPPER_BOUND = 255;
    private boolean enabled = true;
    private String multicastGroup = "224.2.2.3";
    private int multicastPort = 54327;
    private int multicastTimeoutSeconds = 2;
    private int multicastTimeToLive = 32;
    private final Set<String> trustedInterfaces = new HashSet<String>();
    private boolean loopbackModeEnabled = false;

    public boolean isEnabled() {
        return this.enabled;
    }

    public MulticastConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getMulticastGroup() {
        return this.multicastGroup;
    }

    public MulticastConfig setMulticastGroup(String multicastGroup) {
        this.multicastGroup = Preconditions.checkHasText(multicastGroup, "multicastGroup must contain text");
        return this;
    }

    public int getMulticastPort() {
        return this.multicastPort;
    }

    public MulticastConfig setMulticastPort(int multicastPort) {
        if (multicastPort < 0) {
            throw new IllegalArgumentException("multicastPort can't be smaller than 0");
        }
        this.multicastPort = multicastPort;
        return this;
    }

    public int getMulticastTimeoutSeconds() {
        return this.multicastTimeoutSeconds;
    }

    public MulticastConfig setMulticastTimeoutSeconds(int multicastTimeoutSeconds) {
        this.multicastTimeoutSeconds = multicastTimeoutSeconds;
        return this;
    }

    public Set<String> getTrustedInterfaces() {
        return this.trustedInterfaces;
    }

    public MulticastConfig setTrustedInterfaces(Set<String> interfaces) {
        Preconditions.isNotNull(interfaces, "interfaces");
        this.trustedInterfaces.clear();
        this.trustedInterfaces.addAll(interfaces);
        return this;
    }

    public MulticastConfig addTrustedInterface(String ip) {
        this.trustedInterfaces.add(Preconditions.isNotNull(ip, "ip"));
        return this;
    }

    public int getMulticastTimeToLive() {
        return this.multicastTimeToLive;
    }

    public MulticastConfig setMulticastTimeToLive(int multicastTimeToLive) {
        if (multicastTimeToLive < 0 || multicastTimeToLive > 255) {
            throw new IllegalArgumentException("multicastTimeToLive out of range");
        }
        this.multicastTimeToLive = multicastTimeToLive;
        return this;
    }

    public boolean isLoopbackModeEnabled() {
        return this.loopbackModeEnabled;
    }

    public MulticastConfig setLoopbackModeEnabled(boolean enabled) {
        this.loopbackModeEnabled = enabled;
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof MulticastConfig)) {
            return false;
        }
        MulticastConfig that = (MulticastConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.multicastPort != that.multicastPort) {
            return false;
        }
        if (this.multicastTimeoutSeconds != that.multicastTimeoutSeconds) {
            return false;
        }
        if (this.multicastTimeToLive != that.multicastTimeToLive) {
            return false;
        }
        if (this.loopbackModeEnabled != that.loopbackModeEnabled) {
            return false;
        }
        if (this.multicastGroup != null ? !this.multicastGroup.equals(that.multicastGroup) : that.multicastGroup != null) {
            return false;
        }
        return this.trustedInterfaces.equals(that.trustedInterfaces);
    }

    public final int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.multicastGroup != null ? this.multicastGroup.hashCode() : 0);
        result = 31 * result + this.multicastPort;
        result = 31 * result + this.multicastTimeoutSeconds;
        result = 31 * result + this.multicastTimeToLive;
        result = 31 * result + this.trustedInterfaces.hashCode();
        result = 31 * result + (this.loopbackModeEnabled ? 1 : 0);
        return result;
    }

    public String toString() {
        return "MulticastConfig [enabled=" + this.enabled + ", multicastGroup=" + this.multicastGroup + ", multicastPort=" + this.multicastPort + ", multicastTimeToLive=" + this.multicastTimeToLive + ", multicastTimeoutSeconds=" + this.multicastTimeoutSeconds + ", trustedInterfaces=" + this.trustedInterfaces + ", loopbackModeEnabled=" + this.loopbackModeEnabled + "]";
    }
}

