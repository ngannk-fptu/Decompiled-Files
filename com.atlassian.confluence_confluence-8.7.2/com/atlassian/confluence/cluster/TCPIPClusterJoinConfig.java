/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.net.InetAddresses
 *  io.atlassian.fugue.Either
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.util.i18n.Message;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.net.InetAddresses;
import io.atlassian.fugue.Either;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class TCPIPClusterJoinConfig
implements ClusterJoinConfig {
    private final List<String> peerAddresses;

    private TCPIPClusterJoinConfig(List<String> peerAddresses) {
        this.peerAddresses = ImmutableList.copyOf((Collection)((Collection)Preconditions.checkNotNull(peerAddresses)));
    }

    public static Either<Message, TCPIPClusterJoinConfig> createForPeers(String peerAddressString) {
        return TCPIPClusterJoinConfig.createForPeers((List<String>)ImmutableList.copyOf((Iterable)Splitter.on((String)",").trimResults().split((CharSequence)peerAddressString)));
    }

    public static Either<Message, TCPIPClusterJoinConfig> createForPeers(List<String> peerAddresses) {
        if (peerAddresses == null || peerAddresses.size() == 0) {
            return Either.left((Object)Message.getInstance("error.cluster.peers.not.defined"));
        }
        for (String peerAddress : peerAddresses) {
            try {
                InetAddresses.forString((String)peerAddress);
            }
            catch (IllegalArgumentException e) {
                return Either.left((Object)Message.getInstance("error.cluster.peer.not.valid", peerAddress));
            }
        }
        return Either.right((Object)new TCPIPClusterJoinConfig(peerAddresses));
    }

    public List<String> getPeerAddresses() {
        return this.peerAddresses;
    }

    @Override
    public ClusterJoinConfig.ClusterJoinType getType() {
        return ClusterJoinConfig.ClusterJoinType.TCP_IP;
    }

    @Override
    public void decode(ClusterJoinConfig.Decoder decoder) {
        decoder.accept(this);
    }

    public String toString() {
        return "TCP/IP member addresses: " + StringUtils.join(this.peerAddresses, (String)"|");
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof TCPIPClusterJoinConfig)) {
            return false;
        }
        TCPIPClusterJoinConfig that = (TCPIPClusterJoinConfig)o;
        return this.peerAddresses.equals(that.getPeerAddresses());
    }

    public int hashCode() {
        return Objects.hash(this.peerAddresses);
    }

    public String getPeerAddressString() {
        return Joiner.on((String)",").join(this.peerAddresses);
    }
}

