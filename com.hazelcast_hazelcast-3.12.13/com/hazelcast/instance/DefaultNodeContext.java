/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.cluster.Joiner;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigAccessor;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.MemberAddressProviderConfig;
import com.hazelcast.instance.AddressPicker;
import com.hazelcast.instance.AdvancedNetworkAddressPicker;
import com.hazelcast.instance.DefaultAddressPicker;
import com.hazelcast.instance.DelegatingAddressPicker;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeContext;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.instance.NodeExtensionFactory;
import com.hazelcast.internal.networking.Networking;
import com.hazelcast.internal.networking.ServerSocketRegistry;
import com.hazelcast.internal.networking.nio.NioNetworking;
import com.hazelcast.internal.util.InstantiationUtils;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggingServiceImpl;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.nio.NodeIOService;
import com.hazelcast.nio.tcp.TcpIpConnectionChannelErrorHandler;
import com.hazelcast.nio.tcp.TcpIpNetworkingService;
import com.hazelcast.spi.MemberAddressProvider;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@PrivateApi
public class DefaultNodeContext
implements NodeContext {
    public static final List<String> EXTENSION_PRIORITY_LIST = Collections.unmodifiableList(Arrays.asList("com.hazelcast.instance.EnterpriseNodeExtension", "com.hazelcast.instance.DefaultNodeExtension"));

    @Override
    public NodeExtension createNodeExtension(Node node) {
        return NodeExtensionFactory.create(node, EXTENSION_PRIORITY_LIST);
    }

    @Override
    public AddressPicker createAddressPicker(Node node) {
        Config config = node.getConfig();
        MemberAddressProviderConfig memberAddressProviderConfig = ConfigAccessor.getActiveMemberNetworkConfig(config).getMemberAddressProviderConfig();
        ILogger addressPickerLogger = node.getLogger(AddressPicker.class);
        if (!memberAddressProviderConfig.isEnabled()) {
            if (config.getAdvancedNetworkConfig().isEnabled()) {
                return new AdvancedNetworkAddressPicker(config, addressPickerLogger);
            }
            return new DefaultAddressPicker(config, addressPickerLogger);
        }
        MemberAddressProvider implementation = memberAddressProviderConfig.getImplementation();
        if (implementation != null) {
            return new DelegatingAddressPicker(implementation, config, addressPickerLogger);
        }
        ClassLoader classLoader = config.getClassLoader();
        String classname = memberAddressProviderConfig.getClassName();
        Class<? extends MemberAddressProvider> clazz = this.loadMemberAddressProviderClass(classLoader, classname);
        ILogger memberAddressProviderLogger = node.getLogger(clazz);
        Properties properties = memberAddressProviderConfig.getProperties();
        MemberAddressProvider memberAddressProvider = DefaultNodeContext.newMemberAddressProviderInstance(clazz, memberAddressProviderLogger, properties);
        return new DelegatingAddressPicker(memberAddressProvider, config, addressPickerLogger);
    }

    private static MemberAddressProvider newMemberAddressProviderInstance(Class<? extends MemberAddressProvider> clazz, ILogger logger, Properties properties) {
        Properties nonNullProps = properties == null ? new Properties() : properties;
        MemberAddressProvider provider = InstantiationUtils.newInstanceOrNull(clazz, nonNullProps, logger);
        if (provider == null) {
            provider = InstantiationUtils.newInstanceOrNull(clazz, logger, nonNullProps);
        }
        if (provider == null) {
            provider = InstantiationUtils.newInstanceOrNull(clazz, nonNullProps);
        }
        if (provider == null) {
            if (properties != null && !properties.isEmpty()) {
                throw new ConfigurationException("Cannot find a matching constructor for MemberAddressProvider.  The member address provider has properties configured, but the class '" + clazz.getName() + "' does not have a public constructor accepting properties.");
            }
            provider = InstantiationUtils.newInstanceOrNull(clazz, logger);
        }
        if (provider == null) {
            provider = InstantiationUtils.newInstanceOrNull(clazz, new Object[0]);
        }
        if (provider == null) {
            throw new ConfigurationException("Cannot find a matching constructor for MemberAddressProvider implementation '" + clazz.getName() + "'.");
        }
        return provider;
    }

    private Class<? extends MemberAddressProvider> loadMemberAddressProviderClass(ClassLoader classLoader, String classname) {
        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(classLoader, classname);
            if (!MemberAddressProvider.class.isAssignableFrom(clazz)) {
                throw new ConfigurationException("Configured member address provider " + clazz.getName() + " does not implement the interface" + MemberAddressProvider.class.getName());
            }
            return clazz;
        }
        catch (ClassNotFoundException e) {
            throw new ConfigurationException("Cannot create a new instance of MemberAddressProvider '" + classname + "'", e);
        }
    }

    @Override
    public Joiner createJoiner(Node node) {
        return node.createJoiner();
    }

    @Override
    public NetworkingService createNetworkingService(Node node, ServerSocketRegistry registry) {
        NodeIOService ioService = new NodeIOService(node, node.nodeEngine);
        Networking networking = this.createNetworking(node);
        Config config = node.getConfig();
        return new TcpIpNetworkingService(config, ioService, registry, node.loggingService, node.nodeEngine.getMetricsRegistry(), networking, node.getNodeExtension().createChannelInitializerProvider(ioService), node.getProperties());
    }

    private Networking createNetworking(Node node) {
        LoggingServiceImpl loggingService = node.loggingService;
        TcpIpConnectionChannelErrorHandler errorHandler = new TcpIpConnectionChannelErrorHandler(loggingService.getLogger(TcpIpConnectionChannelErrorHandler.class));
        HazelcastProperties props = node.getProperties();
        return new NioNetworking(new NioNetworking.Context().loggingService(loggingService).metricsRegistry(node.nodeEngine.getMetricsRegistry()).threadNamePrefix(node.hazelcastInstance.getName()).errorHandler(errorHandler).inputThreadCount(props.getInteger(GroupProperty.IO_INPUT_THREAD_COUNT)).outputThreadCount(props.getInteger(GroupProperty.IO_OUTPUT_THREAD_COUNT)).balancerIntervalSeconds(props.getInteger(GroupProperty.IO_BALANCER_INTERVAL_SECONDS)));
    }
}

