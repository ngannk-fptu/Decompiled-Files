/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cluster.monitoring.spi.model.NodeIdentifier
 *  com.atlassian.cluster.monitoring.spi.model.NodeInformation
 *  com.hazelcast.core.Member
 *  com.hazelcast.core.MemberSelector
 *  io.atlassian.fugue.Maybe
 *  io.atlassian.fugue.Option
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.cluster.monitoring.spi.model.NodeIdentifier;
import com.atlassian.cluster.monitoring.spi.model.NodeInformation;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import io.atlassian.fugue.Maybe;
import io.atlassian.fugue.Option;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Internal
public final class HazelcastUtils {
    private static final String CONFIGURED_NODE_NAME = "Confluence.ConfiguredNodeName";

    private HazelcastUtils() {
        throw new AssertionError((Object)"Cannot instantiate utility class");
    }

    public static String getMemberId(Member member) {
        return Integer.toHexString(Objects.hash(member.getSocketAddress().getAddress(), member.getSocketAddress().getPort()));
    }

    public static NodeIdentifier getMemberNodeIdentifier(Member member) {
        return new NodeIdentifier(HazelcastUtils.getMemberId(member));
    }

    public static MemberSelector getMemberSelector(NodeIdentifier nodeIdentifier) {
        return member -> HazelcastUtils.getMemberNodeIdentifier(member).equals((Object)nodeIdentifier);
    }

    public static Function<Member, NodeIdentifier> extractNodeId() {
        return HazelcastUtils::getMemberNodeIdentifier;
    }

    public static void setConfiguredMemberName(Member member, String name) {
        member.setStringAttribute(CONFIGURED_NODE_NAME, name);
    }

    public static Optional<String> getConfiguredMemberName(Member member) {
        return Optional.ofNullable(member.getStringAttribute(CONFIGURED_NODE_NAME));
    }

    public static Function<Member, NodeInformation> extractNodeInfo() {
        return member -> {
            InetSocketAddress socketAddress = member.getSocketAddress();
            String hostAddress = String.format("%s:%s", socketAddress.getAddress().getHostAddress(), socketAddress.getPort());
            return new NodeInformation(HazelcastUtils.extractNodeId().apply((Member)member), hostAddress, socketAddress.getAddress().getHostName(), (Maybe)Option.option((Object)member.getStringAttribute(CONFIGURED_NODE_NAME)));
        };
    }
}

