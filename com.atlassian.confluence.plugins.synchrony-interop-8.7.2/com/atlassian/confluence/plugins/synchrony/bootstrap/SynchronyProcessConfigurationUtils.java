/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.AWSClusterJoinConfig
 *  com.atlassian.confluence.cluster.ClusterConfig
 *  com.atlassian.confluence.cluster.MulticastClusterJoinConfig
 *  com.atlassian.confluence.cluster.TCPIPClusterJoinConfig
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.confluence.cluster.AWSClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterConfig;
import com.atlassian.confluence.cluster.MulticastClusterJoinConfig;
import com.atlassian.confluence.cluster.TCPIPClusterJoinConfig;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyEnv;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SynchronyProcessConfigurationUtils {
    private static Logger log = LoggerFactory.getLogger(SynchronyProcessConfigurationUtils.class);
    private static final String IPv4_PREFERRED = "hazelcast.prefer.ipv4.stack";

    private SynchronyProcessConfigurationUtils() {
    }

    static void populateAwsConfig(Properties env, AWSClusterJoinConfig awsClusterJoinConfig, String awsIp) {
        env.setProperty(SynchronyEnv.AlephBind.getEnvName(), awsIp);
        env.setProperty(SynchronyEnv.HazelcastInterfaces.getEnvName(), awsIp);
        env.setProperty(SynchronyEnv.ClusterJoinType.getEnvName(), "aws");
        awsClusterJoinConfig.getAccessKey().ifPresent(val -> env.setProperty(SynchronyEnv.ClusterJoinAwsAccess.getEnvName(), (String)val));
        awsClusterJoinConfig.getSecretKey().ifPresent(val -> env.setProperty(SynchronyEnv.ClusterJoinAwsSecret.getEnvName(), (String)val));
        awsClusterJoinConfig.getRegion().ifPresent(val -> env.setProperty(SynchronyEnv.ClusterJoinAwsRegion.getEnvName(), (String)val));
        awsClusterJoinConfig.getSecurityGroupName().ifPresent(val -> env.setProperty(SynchronyEnv.ClusterJoinAwsGroup.getEnvName(), (String)val));
        awsClusterJoinConfig.getTagKey().ifPresent(val -> env.setProperty(SynchronyEnv.ClusterJoinAwsTagKey.getEnvName(), (String)val));
        awsClusterJoinConfig.getTagValue().ifPresent(val -> env.setProperty(SynchronyEnv.ClusterJoinAwsTagValue.getEnvName(), (String)val));
        SynchronyProcessConfigurationUtils.setHostHeader(env, awsClusterJoinConfig);
        awsClusterJoinConfig.getIamRole().ifPresent(val -> env.setProperty(SynchronyEnv.ClusterJoinAwsIam.getEnvName(), (String)val));
    }

    static void populateTcpIpConfig(Properties env, TCPIPClusterJoinConfig tcpipClusterJoinConfig) {
        env.setProperty(SynchronyEnv.ClusterJoinType.getEnvName(), "tcpip");
        env.setProperty(SynchronyEnv.ClusterJoinTCPIPMembers.getEnvName(), tcpipClusterJoinConfig.getPeerAddressString());
    }

    static void populateMulticastConfig(Properties env, MulticastClusterJoinConfig multicastClusterJoinConfig) {
        env.setProperty(SynchronyEnv.ClusterJoinType.getEnvName(), "multicast");
        env.setProperty(SynchronyEnv.ClusterJoinMulticastGroup.getEnvName(), multicastClusterJoinConfig.getMulticastAddress().getHostAddress());
        env.setProperty(SynchronyEnv.ClusterJoinMulticastPort.getEnvName(), String.valueOf(multicastClusterJoinConfig.getMulticastPort()));
    }

    static void populateKubernetesConfig(Properties env) {
        env.setProperty(SynchronyEnv.ClusterJoinType.getEnvName(), "kubernetes");
    }

    static String getIp(ClusterConfig clusterConfig, @NonNull String defaultIp) {
        Optional<String> maybeIp = Optional.empty();
        try {
            maybeIp = Collections.list(clusterConfig.getNetworkInterface().getInetAddresses()).stream().sorted(new Comparator<InetAddress>(){

                @Override
                public int compare(InetAddress o1, InetAddress o2) {
                    if (this.isPublic(o1) == this.isPublic(o2)) {
                        if (this.isIpv4(o1) == this.isIpv4(o2)) {
                            return 0;
                        }
                        return this.isIpv4(o1) ? -1 : 1;
                    }
                    return this.isPublic(o1) ? -1 : 1;
                }

                private boolean isPublic(InetAddress inetAddress) {
                    return !inetAddress.isLoopbackAddress() && (inetAddress.isSiteLocalAddress() || !inetAddress.isLinkLocalAddress());
                }

                private boolean isIpv4(InetAddress inetAddress) {
                    return inetAddress instanceof Inet4Address;
                }
            }).map(InetAddress::getHostAddress).findFirst();
        }
        catch (Exception e) {
            log.debug("Error getting network interface from the confluence config file", (Throwable)e);
        }
        return maybeIp.orElse(defaultIp);
    }

    static void addIpV6SupportIfNeeded(Map<String, String> synchronyEnvironment, Collection<String> synchronySysProps) {
        String clusterIp = synchronyEnvironment.get(SynchronyEnv.HazelcastInterfaces.getEnvName());
        boolean isIpSettingsOverridden = false;
        for (String property : synchronySysProps) {
            if (property.contains(SynchronyEnv.HazelcastInterfaces.getEnvName())) {
                clusterIp = SynchronyProcessConfigurationUtils.parseSysProp(property);
            }
            if (!property.contains(IPv4_PREFERRED)) continue;
            isIpSettingsOverridden = true;
        }
        if (!isIpSettingsOverridden && StringUtils.isNotBlank((CharSequence)clusterIp) && SynchronyProcessConfigurationUtils.isIpv6(clusterIp)) {
            synchronySysProps.add("-Dhazelcast.prefer.ipv4.stack=false");
        }
    }

    private static String parseSysProp(String property) {
        if (property.contains("=")) {
            return property.split("=")[1];
        }
        return null;
    }

    private static boolean isIpv6(String ip) {
        try {
            return InetAddress.getByName(ip) instanceof Inet6Address;
        }
        catch (Exception e) {
            log.warn("Ip address check ({}) has failed!", (Throwable)e);
            return false;
        }
    }

    private static void setHostHeader(Properties env, AWSClusterJoinConfig awsClusterJoinConfig) {
        String region = awsClusterJoinConfig.getRegion().orElse(null);
        String hostHeader = awsClusterJoinConfig.getHostHeader().orElse(null);
        if (region != null && hostHeader == null) {
            env.setProperty(SynchronyEnv.ClusterJoinAwsHeader.getEnvName(), "ec2.amazonaws.com");
        } else if (region != null) {
            env.setProperty(SynchronyEnv.ClusterJoinAwsHeader.getEnvName(), hostHeader.replace(region + ".", ""));
        } else if (hostHeader != null) {
            env.setProperty(SynchronyEnv.ClusterJoinAwsHeader.getEnvName(), hostHeader);
        }
    }
}

