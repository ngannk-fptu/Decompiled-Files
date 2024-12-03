/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.DiagnosticsConfiguration
 *  com.atlassian.net.NetworkUtils
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.DiagnosticsConfiguration;
import com.atlassian.net.NetworkUtils;
import com.google.common.collect.ImmutableSet;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Enumeration;
import java.util.Set;
import javax.annotation.Nonnull;

public class DefaultDiagnosticsConfiguration
implements DiagnosticsConfiguration {
    private static final Set<String> LOCALHOST_IPS = ImmutableSet.of((Object)"127.0.0.1", (Object)"0:0:0:0:0:0:0:1", (Object)"::1");
    private volatile String nodeName;

    @Nonnull
    public Duration getAlertRetentionPeriod() {
        return Duration.ofDays(30L);
    }

    @Nonnull
    public Duration getAlertTruncationInterval() {
        return Duration.ofDays(1L);
    }

    public boolean isEnabled() {
        return true;
    }

    @Nonnull
    public Duration getThreadDumpProducerCooldown() {
        return Duration.ofMinutes(5L);
    }

    @Nonnull
    public String getNodeName() {
        if (this.nodeName == null) {
            try {
                InetAddress address = null;
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    address = this.getBestAddress(address, this.getBestAddress(interfaces.nextElement()));
                }
                String hostName = NetworkUtils.getLocalHostName();
                if ("localhost".equalsIgnoreCase(hostName) && address != null) {
                    this.nodeName = LOCALHOST_IPS.contains(address.getHostAddress()) ? hostName : address.getHostAddress();
                }
                this.nodeName = hostName;
            }
            catch (SocketException | UnknownHostException e) {
                this.nodeName = "unknown";
            }
        }
        return this.nodeName;
    }

    private InetAddress getBestAddress(NetworkInterface networkInterface) {
        InetAddress best = null;
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
            best = this.getBestAddress(best, addresses.nextElement());
        }
        return best;
    }

    private InetAddress getBestAddress(InetAddress currentBest, InetAddress candidate) {
        if (currentBest == null) {
            return candidate;
        }
        if (this.isLocal(currentBest) && !this.isLocal(candidate)) {
            return candidate;
        }
        if (!(currentBest instanceof Inet4Address) && candidate instanceof Inet4Address) {
            return candidate;
        }
        return currentBest;
    }

    private boolean isLocal(InetAddress address) {
        return address != null && (address.isSiteLocalAddress() || address.isLinkLocalAddress() || address.isLoopbackAddress());
    }
}

