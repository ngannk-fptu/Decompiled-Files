/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Either
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.util.i18n.Message;
import com.google.common.base.Preconditions;
import io.atlassian.fugue.Either;
import java.net.InetAddress;
import java.util.Objects;

public class MulticastClusterJoinConfig
implements ClusterJoinConfig {
    public static final int DEFAULT_MULTICAST_TTL = 1;
    public static final int DEFAULT_MULTICAST_PORT = 54327;
    private final InetAddress multicastAddress;
    private final int multicastTTL;
    private final int multicastPort;

    private MulticastClusterJoinConfig(InetAddress multicastAddress, int multicastTTL, int multicastPort) {
        this.multicastAddress = (InetAddress)Preconditions.checkNotNull((Object)multicastAddress);
        this.multicastTTL = multicastTTL;
        this.multicastPort = multicastPort;
    }

    public static Either<Message, MulticastClusterJoinConfig> createForAddress(InetAddress multicastAddress) {
        return MulticastClusterJoinConfig.createForConfig(multicastAddress, 1, 54327);
    }

    public static Either<Message, MulticastClusterJoinConfig> createForConfig(InetAddress multicastAddress, int multicastTTL, int multicastPort) {
        if (!multicastAddress.isMulticastAddress()) {
            return Either.left((Object)Message.getInstance("error.cluster.address.not.valid", multicastAddress.getCanonicalHostName()));
        }
        if (multicastTTL < 0) {
            return Either.left((Object)Message.getInstance("error.cluster.multicast.ttl.not.valid", multicastTTL));
        }
        if (multicastPort < 0) {
            return Either.left((Object)Message.getInstance("error.cluster.multicast.port.not.valid", multicastPort));
        }
        return Either.right((Object)new MulticastClusterJoinConfig(multicastAddress, multicastTTL, multicastPort));
    }

    public InetAddress getMulticastAddress() {
        return this.multicastAddress;
    }

    public int getMulticastTTL() {
        return this.multicastTTL;
    }

    public int getMulticastPort() {
        return this.multicastPort;
    }

    @Override
    public ClusterJoinConfig.ClusterJoinType getType() {
        return ClusterJoinConfig.ClusterJoinType.MULTICAST;
    }

    @Override
    public void decode(ClusterJoinConfig.Decoder decoder) {
        decoder.accept(this);
    }

    public String toString() {
        return "Multicast (address|port|TTL): (" + this.multicastAddress.getHostAddress() + "|" + this.multicastPort + "|" + this.multicastTTL + ")";
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof MulticastClusterJoinConfig)) {
            return false;
        }
        MulticastClusterJoinConfig that = (MulticastClusterJoinConfig)o;
        return this.multicastAddress.equals(that.getMulticastAddress()) && this.multicastPort == that.getMulticastPort() && this.multicastTTL == that.getMulticastTTL();
    }

    public int hashCode() {
        return Objects.hash(this.multicastAddress, this.multicastPort, this.multicastTTL);
    }
}

