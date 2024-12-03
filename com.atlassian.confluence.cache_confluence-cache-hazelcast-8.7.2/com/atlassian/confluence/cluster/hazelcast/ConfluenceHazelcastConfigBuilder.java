/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.config.ApplicationConfig
 *  com.atlassian.confluence.cluster.AWSClusterJoinConfig
 *  com.atlassian.confluence.cluster.ClusterConfig
 *  com.atlassian.confluence.cluster.ClusterJoinConfig
 *  com.atlassian.confluence.cluster.ClusterJoinConfig$Decoder
 *  com.atlassian.confluence.cluster.KubernetesClusterJoinConfig
 *  com.atlassian.confluence.cluster.MulticastClusterJoinConfig
 *  com.atlassian.confluence.cluster.TCPIPClusterJoinConfig
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.hazelcast.serialization.OsgiClassLoaderRegistrySynchronizer
 *  com.atlassian.hazelcast.serialization.OsgiSafe
 *  com.atlassian.hazelcast.serialization.OsgiSafeStreamSerializer
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.hazelcast.aws.AwsDiscoveryStrategyFactory
 *  com.hazelcast.aws.AwsProperties
 *  com.hazelcast.config.Config
 *  com.hazelcast.config.DiscoveryStrategyConfig
 *  com.hazelcast.config.EvictionPolicy
 *  com.hazelcast.config.InMemoryFormat
 *  com.hazelcast.config.InterfacesConfig
 *  com.hazelcast.config.JoinConfig
 *  com.hazelcast.config.ListenerConfig
 *  com.hazelcast.config.MapConfig
 *  com.hazelcast.config.MaxSizeConfig
 *  com.hazelcast.config.MaxSizeConfig$MaxSizePolicy
 *  com.hazelcast.config.NearCacheConfig
 *  com.hazelcast.config.NetworkConfig
 *  com.hazelcast.config.SerializationConfig
 *  com.hazelcast.config.SerializerConfig
 *  com.hazelcast.config.SetConfig
 *  com.hazelcast.config.SocketInterceptorConfig
 *  com.hazelcast.config.XmlConfigBuilder
 *  com.hazelcast.nio.serialization.Serializer
 *  com.hazelcast.spi.discovery.DiscoveryStrategyFactory
 *  com.hazelcast.spi.properties.GroupProperty
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.config.ApplicationConfig;
import com.atlassian.confluence.cache.hazelcast.HazelcastHelper;
import com.atlassian.confluence.cluster.AWSClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterConfig;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.cluster.KubernetesClusterJoinConfig;
import com.atlassian.confluence.cluster.MulticastClusterJoinConfig;
import com.atlassian.confluence.cluster.TCPIPClusterJoinConfig;
import com.atlassian.confluence.cluster.hazelcast.AlwaysNullMapMergePolicy;
import com.atlassian.confluence.cluster.hazelcast.HazelcastClusterSafetyManager;
import com.atlassian.confluence.cluster.hazelcast.LoggingClusterMembershipListener;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.ClusterJoinSocketInterceptor;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinManager;
import com.atlassian.hazelcast.serialization.OsgiClassLoaderRegistrySynchronizer;
import com.atlassian.hazelcast.serialization.OsgiSafe;
import com.atlassian.hazelcast.serialization.OsgiSafeStreamSerializer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.hazelcast.aws.AwsDiscoveryStrategyFactory;
import com.hazelcast.aws.AwsProperties;
import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.nio.serialization.Serializer;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;
import com.hazelcast.spi.properties.GroupProperty;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
class ConfluenceHazelcastConfigBuilder {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceHazelcastConfigBuilder.class);
    private static final int MAX_CACHE_SIZE = 25000;
    private static final String LISTEN_PORT_SYSPROP = "confluence.cluster.hazelcast.listenPort";
    private static final String MAX_HEARTBEAT = System.getProperty("confluence.cluster.hazelcast.max.no.heartbeat.seconds", "30");
    private static final String CLUSTER_INTERFACE_IP = System.getProperty("confluence.cluster.interface.ip", "");
    private static final boolean IPv6_SUPPORTED = Boolean.getBoolean("confluence.cluster.interface.ip6.support");
    private static final boolean IS_LITE_MEMBER = Boolean.getBoolean("confluence.hazelcast.litemember");
    private static final int DEFAULT_CACHE_MAP_SYNC_BACKUPS = 0;
    private static final int DEFAULT_CACHE_MAP_ASYNC_BACKUPS = 0;
    private static final int DEFAULT_HAZELCAST_PORT = 5801;
    private static final Collection<String> DEFAULT_OUTBOUND_PORT_DEFINITIONS = ImmutableList.of((Object)"33000-35000");
    private static final Map<String, String> DEFAULT_HAZELCAST_PROPERTIES = ImmutableMap.builder().put((Object)"hazelcast.logging.type", (Object)"slf4j").put((Object)"hazelcast.heartbeat.interval.seconds", (Object)"1").put((Object)"hazelcast.max.no.heartbeat.seconds", (Object)MAX_HEARTBEAT).put((Object)"hazelcast.prefer.ipv4.stack", (Object)String.valueOf(!IPv6_SUPPORTED)).put((Object)"hazelcast.operation.call.timeout.millis", (Object)"60000").put((Object)"hazelcast.map.invalidation.batch.enabled", (Object)"false").put((Object)"hazelcast.map.invalidation.batch.size", (Object)"1").put((Object)"javax.xml.transform.TransformerFactory", (Object)"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl").put((Object)"javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema", (Object)"com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory").build();
    private static final String HAZELCAST_INSTANCE_NAME = "confluence";
    private final ApplicationConfig applicationConfig;
    private final ClassLoader classLoader;
    private final OsgiSafeStreamSerializer osgiSafeStreamSerializer;
    private final HazelcastHelper hazelcastHelper;
    private final Consumer<Config> extraConfig;
    private final boolean devMode;
    private final ClusterJoinManager joinManager;

    ConfluenceHazelcastConfigBuilder(ApplicationConfig applicationConfig, ClassLoader classLoader, OsgiSafeStreamSerializer osgiSafeStreamSerializer, boolean devMode, HazelcastHelper hazelcastHelper, Consumer<Config> extraConfig, ClusterJoinManager joinManager) {
        this.applicationConfig = Objects.requireNonNull(applicationConfig);
        this.classLoader = Objects.requireNonNull(classLoader);
        this.osgiSafeStreamSerializer = Objects.requireNonNull(osgiSafeStreamSerializer);
        this.devMode = devMode;
        this.hazelcastHelper = Objects.requireNonNull(hazelcastHelper);
        this.extraConfig = extraConfig;
        this.joinManager = joinManager;
    }

    Config createHazelcastConfig(ClusterConfig clusterConfig, String hazelcastConfigResourceName) {
        Config hazelcastConfig = this.createBaseConfig(hazelcastConfigResourceName);
        hazelcastConfig.setProperty(GroupProperty.SEARCH_DYNAMIC_CONFIG_FIRST.getName(), Boolean.TRUE.toString());
        hazelcastConfig.setProperty(GroupProperty.ENABLE_JMX.getName(), Boolean.toString(ConfluenceSystemProperties.isEnableHazelcastJMX()));
        hazelcastConfig.setInstanceName(HAZELCAST_INSTANCE_NAME);
        if (StringUtils.isNotBlank((CharSequence)clusterConfig.getClusterName())) {
            hazelcastConfig.setGroupConfig(hazelcastConfig.getGroupConfig().setName(clusterConfig.getClusterName()));
        } else {
            hazelcastConfig.setGroupConfig(hazelcastConfig.getGroupConfig().setName(HAZELCAST_INSTANCE_NAME));
        }
        hazelcastConfig.setClassLoader(this.classLoader);
        this.updateConfigInterceptors(hazelcastConfig);
        ConfluenceHazelcastConfigBuilder.configureManagementCenter(hazelcastConfig);
        ConfluenceHazelcastConfigBuilder.configureLiteMember(hazelcastConfig);
        this.extraConfig.accept(hazelcastConfig);
        this.updateNetworkConfig(clusterConfig, hazelcastConfig);
        this.updateSerializationConfig(hazelcastConfig.getSerializationConfig());
        this.updateBaseCacheDataStructureConfig(hazelcastConfig);
        this.updatePropertiesConfig(hazelcastConfig);
        this.updateListenerConfig(hazelcastConfig);
        OsgiClassLoaderRegistrySynchronizer.configure((Config)hazelcastConfig);
        return hazelcastConfig;
    }

    private void updateListenerConfig(Config hazelcastConfig) {
        hazelcastConfig.addListenerConfig(new ListenerConfig(LoggingClusterMembershipListener.class.getName()));
    }

    private static void configureLiteMember(Config hazelcastConfig) {
        if (IS_LITE_MEMBER) {
            log.info("Configuring hazelcast node as a lite-member");
            hazelcastConfig.setLiteMember(true);
        }
    }

    private static void configureManagementCenter(Config config) {
        String managementCenterUrl = ConfluenceSystemProperties.getHazelcastManagementCenterUrl();
        if (managementCenterUrl != null) {
            config.getManagementCenterConfig().setUrl(managementCenterUrl);
            config.getManagementCenterConfig().setEnabled(true);
        }
    }

    private void updatePropertiesConfig(Config hazelcastConfig) {
        DEFAULT_HAZELCAST_PROPERTIES.entrySet().stream().filter(property -> !this.isPropertyAlreadySet(hazelcastConfig, (String)property.getKey())).forEach(property -> hazelcastConfig.setProperty((String)property.getKey(), (String)property.getValue()));
    }

    private void updateBaseCacheDataStructureConfig(Config hazelcastConfig) {
        hazelcastConfig.addMapConfig(new MapConfig(hazelcastConfig.getMapConfig("default")));
        String baseAtlassianCacheMapName = this.hazelcastHelper.getHazelcastMapNameForCache("*");
        String baseAtlassianCachedReferenceMapName = this.hazelcastHelper.getHazelcastMapNameForCachedReference("*");
        hazelcastConfig.addMapConfig(this.createMapConfig(hazelcastConfig, baseAtlassianCacheMapName, true));
        hazelcastConfig.addMapConfig(this.createMapConfig(hazelcastConfig, baseAtlassianCachedReferenceMapName, true));
        String baseSharedDataName = this.hazelcastHelper.getBaseSharedDataName();
        hazelcastConfig.addMapConfig(new MapConfig(hazelcastConfig.getMapConfig("default")).setName(baseSharedDataName).setBackupCount(0).setAsyncBackupCount(1).setTimeToLiveSeconds(0).setMaxIdleSeconds(0).setEvictionPolicy(EvictionPolicy.LFU).setMaxSizeConfig(new MaxSizeConfig(25000, MaxSizeConfig.MaxSizePolicy.PER_NODE)).setNearCacheConfig(null));
        hazelcastConfig.addSetConfig((SetConfig)((SetConfig)((SetConfig)new SetConfig(hazelcastConfig.getSetConfig("default")).setName(baseSharedDataName)).setMaxSize(25000)).setBackupCount(1));
        hazelcastConfig.addMapConfig(new MapConfig(hazelcastConfig.getMapConfig("default")).setName(HazelcastClusterSafetyManager.SAFETY_MAP_PREFIX + "*").setBackupCount(0).setTimeToLiveSeconds(0).setMaxIdleSeconds(0).setEvictionPolicy(EvictionPolicy.NONE).setMaxSizeConfig(new MaxSizeConfig(25000, MaxSizeConfig.MaxSizePolicy.PER_NODE)).setNearCacheConfig(null).setMergePolicy(AlwaysNullMapMergePolicy.class.getName()));
    }

    private MapConfig createMapConfig(Config hazelcastConfig, String name, boolean withNearCache) {
        NearCacheConfig nearCacheConfig = withNearCache ? new NearCacheConfig().setMaxIdleSeconds(3600).setTimeToLiveSeconds(86400).setInMemoryFormat(InMemoryFormat.OBJECT).setCacheLocalEntries(true).setInvalidateOnChange(true).setMaxSize(25000).setEvictionPolicy(EvictionPolicy.LFU.name()) : null;
        return new MapConfig(hazelcastConfig.getMapConfig("default")).setName(name).setBackupCount(0).setAsyncBackupCount(0).setTimeToLiveSeconds(0).setMaxIdleSeconds(0).setEvictionPolicy(EvictionPolicy.LFU).setMaxSizeConfig(new MaxSizeConfig(25000, MaxSizeConfig.MaxSizePolicy.PER_NODE)).setNearCacheConfig(nearCacheConfig);
    }

    private void updateSerializationConfig(SerializationConfig serializationConfig) {
        serializationConfig.addSerializerConfig(new SerializerConfig().setImplementation((Serializer)this.osgiSafeStreamSerializer).setTypeClass(OsgiSafe.class));
    }

    private void updateNetworkConfig(ClusterConfig confluenceClusterConfig, Config config) {
        NetworkInterface configuredNetworkInterface = confluenceClusterConfig.getNetworkInterface();
        if (configuredNetworkInterface != null) {
            ConfluenceHazelcastConfigBuilder.updateInterfacesConfig(config, configuredNetworkInterface);
        }
        this.updateListenPort(config.getNetworkConfig());
        ConfluenceHazelcastConfigBuilder.updateJoinConfig(confluenceClusterConfig, config);
    }

    private void updateListenPort(NetworkConfig networkConfig) {
        String portStr = System.getProperty(LISTEN_PORT_SYSPROP);
        NetworkConfig defaultNetworkConfig = new Config().getNetworkConfig();
        if (StringUtils.isNotEmpty((CharSequence)portStr)) {
            int listenPort = Integer.parseInt(portStr);
            networkConfig.setPort(listenPort);
        } else if (networkConfig.getPort() == defaultNetworkConfig.getPort()) {
            networkConfig.setPort(5801);
        }
        if (!Objects.equals(defaultNetworkConfig.getOutboundPortDefinitions(), networkConfig.getOutboundPortDefinitions())) {
            networkConfig.setOutboundPortDefinitions(DEFAULT_OUTBOUND_PORT_DEFINITIONS);
        }
        networkConfig.setPortAutoIncrement(this.devMode);
    }

    private static void updateJoinConfig(ClusterConfig confluenceClusterConfig, final Config hazelcastConfig) {
        ClusterJoinConfig clusterJoinConfig = confluenceClusterConfig.getJoinConfig();
        final JoinConfig joinConfig = hazelcastConfig.getNetworkConfig().getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getAwsConfig().setEnabled(false);
        joinConfig.getMulticastConfig().setEnabled(false);
        clusterJoinConfig.decode(new ClusterJoinConfig.Decoder(){

            public void accept(TCPIPClusterJoinConfig tcpIpJoinConfig) {
                ConfluenceHazelcastConfigBuilder.configureTcpIpJoin(joinConfig, tcpIpJoinConfig);
            }

            public void accept(MulticastClusterJoinConfig multicastJoinConfig) {
                ConfluenceHazelcastConfigBuilder.configureMulticastJoin(joinConfig, multicastJoinConfig);
            }

            public void accept(AWSClusterJoinConfig awsJoinConfig) {
                ConfluenceHazelcastConfigBuilder.configureAwsJoin(hazelcastConfig, joinConfig, awsJoinConfig);
            }

            public void accept(KubernetesClusterJoinConfig kubernetesJoinConfig) {
                ConfluenceHazelcastConfigBuilder.configureKubernetesJoin(joinConfig);
            }
        });
    }

    private static void configureKubernetesJoin(JoinConfig joinConfig) {
        joinConfig.getKubernetesConfig().setEnabled(true);
    }

    private static void configureAwsJoin(Config hazelcastConfig, JoinConfig joinConfig, AWSClusterJoinConfig awsJoinConfig) {
        AwsDiscoveryStrategyFactory awsDiscoveryStrategyFactory = new AwsDiscoveryStrategyFactory();
        HashMap<String, Comparable> properties = new HashMap<String, Comparable>();
        hazelcastConfig.getProperties().setProperty(GroupProperty.DISCOVERY_SPI_ENABLED.getName(), "true");
        Optional accessKey = awsJoinConfig.getAccessKey();
        Optional secretKey = awsJoinConfig.getSecretKey();
        awsJoinConfig.getTagKey().ifPresent(tagKey -> properties.put(AwsProperties.TAG_KEY.getDefinition().key(), (Comparable)tagKey));
        awsJoinConfig.getTagValue().ifPresent(tagValue -> properties.put(AwsProperties.TAG_VALUE.getDefinition().key(), (Comparable)tagValue));
        awsJoinConfig.getRegion().ifPresent(region -> properties.put(AwsProperties.REGION.getDefinition().key(), (Comparable)region));
        awsJoinConfig.getSecurityGroupName().ifPresent(groupName -> properties.put(AwsProperties.SECURITY_GROUP_NAME.getDefinition().key(), (Comparable)groupName));
        properties.put(AwsProperties.PORT.getDefinition().key(), Integer.valueOf(hazelcastConfig.getNetworkConfig().getPort()));
        ConfluenceHazelcastConfigBuilder.setHostHeader(properties, awsJoinConfig.getHostHeader().orElse(null), awsJoinConfig.getRegion().orElse(null));
        if (accessKey.isPresent() && secretKey.isPresent()) {
            properties.put(AwsProperties.ACCESS_KEY.getDefinition().key(), (Comparable)accessKey.get());
            properties.put(AwsProperties.SECRET_KEY.getDefinition().key(), (Comparable)secretKey.get());
        } else {
            awsJoinConfig.getIamRole().ifPresent(s -> properties.put(AwsProperties.IAM_ROLE.getDefinition().key(), (Comparable)s));
        }
        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig((DiscoveryStrategyFactory)awsDiscoveryStrategyFactory, properties);
        joinConfig.getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);
    }

    private static void configureTcpIpJoin(JoinConfig joinConfig, TCPIPClusterJoinConfig clusterJoinConfig) {
        joinConfig.getTcpIpConfig().setEnabled(true).setMembers(clusterJoinConfig.getPeerAddresses());
    }

    private static void configureMulticastJoin(JoinConfig joinConfig, MulticastClusterJoinConfig multicastClusterJoinConfig) {
        joinConfig.getMulticastConfig().setEnabled(true).setMulticastTimeToLive(multicastClusterJoinConfig.getMulticastTTL()).setMulticastGroup(multicastClusterJoinConfig.getMulticastAddress().getHostAddress()).setMulticastPort(multicastClusterJoinConfig.getMulticastPort());
    }

    private static void setHostHeader(Map<String, Comparable> awsProperties, @Nullable String hostHeader, @Nullable String region) {
        if (region != null && hostHeader == null) {
            awsProperties.put(AwsProperties.HOST_HEADER.getDefinition().key(), (Comparable)((Object)"ec2.amazonaws.com"));
        } else if (region != null) {
            awsProperties.put(AwsProperties.HOST_HEADER.getDefinition().key(), (Comparable)((Object)hostHeader.replace(region + ".", "")));
        } else if (hostHeader != null) {
            awsProperties.put(AwsProperties.HOST_HEADER.getDefinition().key(), (Comparable)((Object)hostHeader));
        }
    }

    private static void updateInterfacesConfig(Config config, NetworkInterface configuredNetworkInterface) {
        InterfacesConfig interfacesConfig = config.getNetworkConfig().getInterfaces();
        if (StringUtils.isBlank((CharSequence)CLUSTER_INTERFACE_IP)) {
            for (InetAddress networkInterfaceAddress : Collections.list(configuredNetworkInterface.getInetAddresses())) {
                interfacesConfig.addInterface(networkInterfaceAddress.getHostAddress());
            }
        } else {
            interfacesConfig.addInterface(CLUSTER_INTERFACE_IP);
            if (ConfluenceHazelcastConfigBuilder.isIpv6(CLUSTER_INTERFACE_IP)) {
                config.setProperty(GroupProperty.PREFER_IPv4_STACK.getName(), "false");
            }
        }
        interfacesConfig.setEnabled(true);
    }

    private static boolean isIpv6(String ip) {
        try {
            return InetAddress.getByName(ip) instanceof Inet6Address;
        }
        catch (Exception e) {
            log.warn("Ip address check ({}) has failed!", (Object)ip, (Object)e);
            return false;
        }
    }

    private Optional<File> getConfigOverride(String hazelcastConfigResourceName) {
        File configDirectory;
        File configResource;
        Object clusterHome = this.applicationConfig.getProperty((Object)"confluence.cluster.home");
        if (clusterHome != null && (configResource = new File(configDirectory = new File(clusterHome.toString(), "config"), hazelcastConfigResourceName)).exists()) {
            return Optional.of(configResource);
        }
        return Optional.empty();
    }

    private Config createBaseConfig(String hazelcastConfigResourceName) {
        try {
            return this.getConfigInputStream(hazelcastConfigResourceName).map(input -> new XmlConfigBuilder(input).build()).orElse(new Config());
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to read config", e);
        }
    }

    private Optional<InputStream> getConfigInputStream(String hazelcastConfigResourceName) throws FileNotFoundException {
        Optional<File> sharedConfig = this.getConfigOverride(hazelcastConfigResourceName);
        if (sharedConfig.isPresent()) {
            log.info("Reading Hazelcast config from shared-home location [{}]", (Object)sharedConfig.get().getAbsolutePath());
            return Optional.of(new FileInputStream(sharedConfig.get()));
        }
        return Optional.empty();
    }

    private boolean isPropertyAlreadySet(Config config, String propertyName) {
        Config defaultConfig = new Config();
        return !Objects.equals(defaultConfig.getProperty(propertyName), config.getProperty(propertyName));
    }

    private void updateConfigInterceptors(Config hazelcastConfig) {
        SocketInterceptorConfig interceptorConfig = new SocketInterceptorConfig();
        interceptorConfig.getProperties().put(ClusterJoinManager.class.getName(), this.joinManager);
        interceptorConfig.setClassName(ClusterJoinSocketInterceptor.class.getName());
        interceptorConfig.setEnabled(true);
        hazelcastConfig.getNetworkConfig().setSocketInterceptorConfig(interceptorConfig);
    }
}

