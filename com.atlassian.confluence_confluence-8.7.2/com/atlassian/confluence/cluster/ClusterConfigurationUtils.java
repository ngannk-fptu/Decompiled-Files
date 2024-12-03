/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.config.ApplicationConfiguration
 *  com.hazelcast.aws.utility.MetadataUtil
 *  io.atlassian.fugue.Either
 *  javax.annotation.Nonnull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster;

import com.atlassian.annotations.Internal;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.cluster.AWSClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterConfig;
import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.cluster.InvalidClusterAddressException;
import com.atlassian.confluence.cluster.InvalidClusterJoinConfigException;
import com.atlassian.confluence.cluster.KubernetesClusterJoinConfig;
import com.atlassian.confluence.cluster.MulticastClusterJoinConfig;
import com.atlassian.confluence.cluster.TCPIPClusterJoinConfig;
import com.atlassian.confluence.util.i18n.Message;
import com.hazelcast.aws.utility.MetadataUtil;
import io.atlassian.fugue.Either;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public final class ClusterConfigurationUtils {
    private static final Logger log = LoggerFactory.getLogger(ClusterConfigurationUtils.class);
    private static final String IMDS_LOCAL_IP_SUFFIX = "local-ipv4";
    private static final int IMDS_TIMEOUT_IN_SECONDS = 5;
    private static final int IMDS_RETRIES_NO = 10;

    private ClusterConfigurationUtils() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ClusterConfig getClusterConfig(ApplicationConfiguration applicationConfig) throws ClusterException {
        ClusterJoinConfig joinConfig;
        String clusterInterfaceName;
        File clusterHome;
        String clusterName;
        ApplicationConfiguration applicationConfiguration = applicationConfig;
        synchronized (applicationConfiguration) {
            clusterName = String.valueOf(applicationConfig.getProperty((Object)"confluence.cluster.name"));
            clusterHome = ClusterConfigurationUtils.getSharedHome(applicationConfig);
            clusterInterfaceName = String.valueOf(applicationConfig.getProperty((Object)"confluence.cluster.interface"));
            joinConfig = ClusterConfigurationUtils.createJoinConfig(applicationConfig);
        }
        NetworkInterface clusterInterface = ClusterConfigurationUtils.resolveNetworkInterface(clusterInterfaceName);
        ClusterConfigurationUtils.checkSharedHomeIsNotLocalHome(clusterHome, applicationConfig);
        return new ClusterConfig(joinConfig, clusterName, clusterHome, clusterInterface);
    }

    private static ClusterJoinConfig createJoinConfig(ApplicationConfiguration applicationConfig) throws InvalidClusterJoinConfigException, InvalidClusterAddressException {
        String joinTypeString = (String)applicationConfig.getProperty((Object)"confluence.cluster.join.type");
        ClusterJoinConfig.ClusterJoinType joinType = joinTypeString == null ? ClusterJoinConfig.ClusterJoinType.MULTICAST : ClusterJoinConfig.ClusterJoinType.fromString(joinTypeString);
        switch (joinType) {
            case MULTICAST: {
                return ClusterConfigurationUtils.createMulticastJoinConfig(applicationConfig);
            }
            case TCP_IP: {
                return ClusterConfigurationUtils.createTciIpJoinConfig(applicationConfig);
            }
            case AWS: {
                return ClusterConfigurationUtils.createAwsJoinConfig(applicationConfig);
            }
            case KUBERNETES: {
                return ClusterConfigurationUtils.createKubernetesJoinConfig();
            }
        }
        throw new InvalidClusterJoinConfigException("The cluster join config type: '" + joinTypeString + "' is invalid");
    }

    private static ClusterJoinConfig createKubernetesJoinConfig() {
        return new KubernetesClusterJoinConfig();
    }

    private static ClusterJoinConfig createAwsJoinConfig(ApplicationConfiguration applicationConfig) throws InvalidClusterJoinConfigException {
        Either<Message, AWSClusterJoinConfig> joinConfigEither = AWSClusterJoinConfig.createForKeys((String)applicationConfig.getProperty((Object)"confluence.cluster.aws.access.key"), (String)applicationConfig.getProperty((Object)"confluence.cluster.aws.secret.key"), (String)applicationConfig.getProperty((Object)"confluence.cluster.aws.iam.role"), (String)applicationConfig.getProperty((Object)"confluence.cluster.aws.region"), (String)applicationConfig.getProperty((Object)"confluence.cluster.aws.host.header"), (String)applicationConfig.getProperty((Object)"confluence.cluster.aws.security.group.name"), (String)applicationConfig.getProperty((Object)"confluence.cluster.aws.tag.key"), (String)applicationConfig.getProperty((Object)"confluence.cluster.aws.tag.value"));
        return ClusterConfigurationUtils.getOrThrow(joinConfigEither);
    }

    private static ClusterJoinConfig createTciIpJoinConfig(ApplicationConfiguration applicationConfig) throws InvalidClusterJoinConfigException {
        Either<Message, TCPIPClusterJoinConfig> joinConfigEither = TCPIPClusterJoinConfig.createForPeers((String)applicationConfig.getProperty((Object)"confluence.cluster.peers"));
        return ClusterConfigurationUtils.getOrThrow(joinConfigEither);
    }

    private static ClusterJoinConfig createMulticastJoinConfig(ApplicationConfiguration applicationConfig) throws InvalidClusterJoinConfigException, InvalidClusterAddressException {
        String address = String.valueOf(applicationConfig.getProperty((Object)"confluence.cluster.address"));
        String ttl = String.valueOf(applicationConfig.getProperty((Object)"confluence.cluster.ttl"));
        int port = Integer.getInteger("confluence.cluster.multicast.port", 54327);
        try {
            Either<Message, MulticastClusterJoinConfig> joinConfigEither = MulticastClusterJoinConfig.createForConfig(InetAddress.getByName(address), Integer.parseInt(ttl), port);
            return ClusterConfigurationUtils.getOrThrow(joinConfigEither);
        }
        catch (UnknownHostException e) {
            throw new InvalidClusterAddressException("The address '" + address + "' is not a valid network address", e);
        }
    }

    public static boolean isClusterHomeConfigured(ApplicationConfiguration applicationConfig) {
        return applicationConfig.getProperty((Object)"confluence.cluster.home") != null;
    }

    @Nonnull
    public static File getSharedHome(ApplicationConfiguration applicationConfig) {
        Object clusterHomeProperty = applicationConfig.getProperty((Object)"confluence.cluster.home");
        return clusterHomeProperty instanceof String ? new File((String)clusterHomeProperty) : new File(applicationConfig.getApplicationHome(), "shared-home");
    }

    static void checkSharedHomeIsNotLocalHome(File clusterHome, ApplicationConfiguration applicationConfig) throws ClusterException {
        try {
            if (clusterHome.getCanonicalPath().equals(new File(applicationConfig.getApplicationHome()).getCanonicalPath())) {
                throw new ClusterException("Shared home directory cannot be in the same location as home directory.");
            }
        }
        catch (IOException e) {
            throw new ClusterException("Failed to check shared home directory: " + e.getMessage(), e);
        }
    }

    private static NetworkInterface resolveNetworkInterface(@Nullable String clusterInterfaceName) {
        if (clusterInterfaceName != null) {
            try {
                return NetworkInterface.getByName(clusterInterfaceName);
            }
            catch (SocketException e) {
                log.error("Could not find network interface '{}'", (Object)clusterInterfaceName, (Object)e);
            }
        }
        return null;
    }

    public static String getAwsEc2PrivateIp(@NonNull String defaultIp) {
        try {
            String uri = "http://169.254.169.254/latest/meta-data/local-ipv4";
            return MetadataUtil.retrieveMetadataFromURI((String)uri, (int)5, (int)10);
        }
        catch (Exception e) {
            log.error("Error getting network interface from the AWS metadata url", (Throwable)e);
            return defaultIp;
        }
    }

    static <T> T getOrThrow(Either<Message, T> joinConfig) throws InvalidClusterJoinConfigException {
        if (joinConfig.isLeft()) {
            throw new InvalidClusterJoinConfigException("Error bootstrapping cluster (" + ((Message)joinConfig.left().get()).getKey() + ")");
        }
        return (T)joinConfig.right().get();
    }
}

