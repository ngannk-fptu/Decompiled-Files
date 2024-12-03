/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.AWSClusterJoinConfig
 *  com.atlassian.confluence.cluster.ClusterInformation
 *  com.atlassian.confluence.cluster.ClusterJoinConfig
 *  com.atlassian.confluence.cluster.MulticastClusterJoinConfig
 *  com.atlassian.confluence.cluster.TCPIPClusterJoinConfig
 *  com.hazelcast.config.AwsConfig
 *  com.hazelcast.config.MulticastConfig
 *  com.hazelcast.config.TcpIpConfig
 *  com.hazelcast.core.HazelcastInstance
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.confluence.cluster.AWSClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterInformation;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.cluster.MulticastClusterJoinConfig;
import com.atlassian.confluence.cluster.TCPIPClusterJoinConfig;
import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.HazelcastInstance;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated(since="8.2", forRemoval=true)
public class HazelcastClusterInformation
implements ClusterInformation {
    private final HazelcastInstance instance;

    public HazelcastClusterInformation(HazelcastInstance instance) {
        this.instance = instance;
    }

    public boolean isRunning() {
        return this.instance.getLifecycleService().isRunning();
    }

    public String getName() {
        return this.instance.getName();
    }

    public String getDescription() {
        return "HazelcastClusterInformation: " + this.getName() + ", networking on " + this.instance.getConfig().getNetworkConfig().toString();
    }

    public List<String> getMembers() {
        return this.instance.getCluster().getMembers().stream().map(member -> "[" + member.getUuid() + " listening on " + member.getSocketAddress().toString() + "]").collect(Collectors.toList());
    }

    public int getMemberCount() {
        return this.instance.getCluster().getMembers().size();
    }

    public ClusterJoinConfig getClusterJoinConfig() {
        MulticastConfig multicastConfig = this.instance.getConfig().getNetworkConfig().getJoin().getMulticastConfig();
        TcpIpConfig tcpIpConfig = this.instance.getConfig().getNetworkConfig().getJoin().getTcpIpConfig();
        AwsConfig awsConfig = this.instance.getConfig().getNetworkConfig().getJoin().getAwsConfig();
        if (multicastConfig.isEnabled()) {
            try {
                return (ClusterJoinConfig)MulticastClusterJoinConfig.createForConfig((InetAddress)InetAddress.getByName(multicastConfig.getMulticastGroup()), (int)multicastConfig.getMulticastTimeToLive(), (int)multicastConfig.getMulticastTimeToLive()).right().get();
            }
            catch (UnknownHostException e) {
                throw new IllegalStateException("Invalid multicast address configured!", e);
            }
        }
        if (tcpIpConfig.isEnabled()) {
            return (ClusterJoinConfig)TCPIPClusterJoinConfig.createForPeers((List)tcpIpConfig.getMembers()).right().get();
        }
        if (awsConfig.isEnabled()) {
            return (ClusterJoinConfig)AWSClusterJoinConfig.createForKeys((String)awsConfig.getAccessKey(), (String)awsConfig.getSecretKey(), (String)awsConfig.getIamRole(), (String)awsConfig.getRegion(), (String)awsConfig.getHostHeader(), (String)awsConfig.getSecurityGroupName(), (String)awsConfig.getTagKey(), (String)awsConfig.getTagValue()).right().get();
        }
        throw new IllegalStateException("No cluster join configuration found!");
    }
}

