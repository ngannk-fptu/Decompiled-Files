/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.cluster.AWSClusterJoinConfig
 *  com.atlassian.confluence.cluster.ClusterConfig
 *  com.atlassian.confluence.cluster.ClusterConfigurationUtils
 *  com.atlassian.confluence.cluster.ClusterException
 *  com.atlassian.confluence.cluster.ClusterJoinConfig$Decoder
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.KubernetesClusterJoinConfig
 *  com.atlassian.confluence.cluster.MulticastClusterJoinConfig
 *  com.atlassian.confluence.cluster.TCPIPClusterJoinConfig
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.util.tomcat.TomcatConfigHelper
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableMap
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.commons.lang3.tuple.ImmutablePair
 *  org.apache.commons.lang3.tuple.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 *  oshi.SystemInfo
 *  oshi.software.os.OperatingSystem
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.cluster.AWSClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterConfig;
import com.atlassian.confluence.cluster.ClusterConfigurationUtils;
import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.KubernetesClusterJoinConfig;
import com.atlassian.confluence.cluster.MulticastClusterJoinConfig;
import com.atlassian.confluence.cluster.TCPIPClusterJoinConfig;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyEnv;
import com.atlassian.confluence.plugins.synchrony.bootstrap.SynchronyProcessConfigurationUtils;
import com.atlassian.confluence.plugins.synchrony.utils.JdbcUrlUtil;
import com.atlassian.confluence.plugins.synchrony.utils.SynchronyPropertiesUtil;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.util.tomcat.TomcatConfigHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableMap;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

@Component
public class SynchronyEnvironmentBuilder {
    private static final Logger log = LoggerFactory.getLogger(SynchronyEnvironmentBuilder.class);
    private static final String DEFAULT_LISTENING_IP = "0.0.0.0";
    private static final String CLUSTER_MULTICAST_PORT_SYS_PROP = "synchrony.cluster.multicast.port";
    public static final int DEFAULT_MULTICAST_PORT = 54328;
    private final BootstrapManager bootstrapManager;
    private final SystemInformationService systemInformationService;
    private final ClusterManager clusterManager;
    private final TomcatConfigHelper tomcatConfigHelper;
    public static final String URL = "hibernate.connection.url";
    public static final String USER = "hibernate.connection.username";
    public static final String PASS = "hibernate.connection.password";
    public static final String DATASOURCE = "hibernate.connection.datasource";

    @Autowired
    public SynchronyEnvironmentBuilder(@ComponentImport(value="bootstrapManager") BootstrapManager bootstrapManager, @ComponentImport SystemInformationService systemInformationService, @ComponentImport(value="clusterManager") ClusterManager clusterManager, @ComponentImport TomcatConfigHelper tomcatConfigHelper) {
        this.bootstrapManager = bootstrapManager;
        this.systemInformationService = systemInformationService;
        this.clusterManager = clusterManager;
        this.tomcatConfigHelper = tomcatConfigHelper;
    }

    public SynchronyEnvironment build(boolean isSynchronyClusterManuallyManaged) {
        return this.setupEnvironment(isSynchronyClusterManuallyManaged);
    }

    private SynchronyEnvironment setupEnvironment(boolean isSynchronyClusterManuallyManaged) {
        String internalBaseUrl;
        Properties env = SynchronyPropertiesUtil.computeRenamedProperties(SynchronyEnv.getDefaultProperties());
        env.putAll((Map<?, ?>)System.getProperties());
        Object synchronyContextPath = env.getProperty(SynchronyEnv.ContextPath.getEnvName());
        if (!StringUtils.startsWith((CharSequence)synchronyContextPath, (CharSequence)"/")) {
            synchronyContextPath = "/" + (String)synchronyContextPath;
            env.setProperty(SynchronyEnv.ContextPath.getEnvName(), (String)synchronyContextPath);
        }
        System.setProperty(SynchronyEnv.ContextPath.getEnvName(), env.getProperty(SynchronyEnv.ContextPath.getEnvName()));
        System.setProperty(SynchronyEnv.Port.getEnvName(), env.getProperty(SynchronyEnv.Port.getEnvName()));
        System.setProperty(SynchronyEnv.Host.getEnvName(), env.getProperty(SynchronyEnv.Host.getEnvName()));
        ApplicationConfiguration config = this.bootstrapManager.getApplicationConfig();
        Properties hibernateProps = this.bootstrapManager.getHibernateProperties();
        String connectionUrl = hibernateProps.getProperty(URL);
        String username = hibernateProps.getProperty(USER);
        String password = hibernateProps.getProperty(PASS);
        String datasource = hibernateProps.getProperty(DATASOURCE);
        Object credentials = StringUtils.isNotBlank((CharSequence)datasource) ? (Pair)this.tomcatConfigHelper.getDatasourceCredentials().orElse(ImmutablePair.of((Object)username, (Object)password)) : ImmutablePair.of((Object)username, (Object)password);
        if (connectionUrl == null) {
            connectionUrl = this.systemInformationService.getDatabaseInfo().getUrl();
        }
        env.setProperty(SynchronyEnv.JdbcUrl.getEnvName(), JdbcUrlUtil.normalizeSchemeAndSubprotocol(connectionUrl));
        env.setProperty(SynchronyEnv.JdbcUser.getEnvName(), (String)credentials.getLeft());
        env.setProperty(SynchronyEnv.JdbcPassword.getEnvName(), (String)credentials.getRight());
        env.setProperty(SynchronyEnv.JwtPublicKey.getEnvName(), (String)config.getProperty((Object)"jwt.public.key"));
        env.setProperty(SynchronyEnv.JwtPrivateKey.getEnvName(), (String)config.getProperty((Object)"jwt.private.key"));
        env.setProperty(SynchronyEnv.WatchPid.getEnvName(), Integer.toString(this.getConfluenceProcessId()));
        env.setProperty(SynchronyEnv.DefaultLogging.getEnvName(), "false");
        if (config.getProperty((Object)"synchrony.service.authtoken") != null) {
            env.setProperty(SynchronyEnv.FeatureAuthToken.getEnvName(), "true");
            env.setProperty(SynchronyEnv.AuthTokens.getEnvName(), (String)config.getProperty((Object)"synchrony.service.authtoken"));
        }
        if (this.clusterManager.isClustered() && !isSynchronyClusterManuallyManaged) {
            env.setProperty(SynchronyEnv.ClusterAuthenticationEnabled.getEnvName(), (String)config.getProperty((Object)"confluence.cluster.authentication.enabled"));
            env.setProperty(SynchronyEnv.ClusterAuthenticationSecret.getEnvName(), (String)config.getProperty((Object)"confluence.cluster.authentication.secret"));
            try {
                this.populateClusterOptions(env);
            }
            catch (ClusterException e) {
                throw new RuntimeException("Failed to populate cluster options", e);
            }
        }
        boolean isProxyEnabled = this.isSynchronyProxyEnabled(isSynchronyClusterManuallyManaged);
        String defaultRezaPort = env.getProperty(SynchronyEnv.Port.getEnvName(), SynchronyEnv.Port.getDefaultValue());
        String externalBaseUrl = this.computeSynchronyExternalBaseUrl(defaultRezaPort, env.getProperty(SynchronyEnv.ContextPath.getEnvName(), SynchronyEnv.ContextPath.getDefaultValue()), isProxyEnabled);
        String serviceUrl = externalBaseUrl.equalsIgnoreCase(internalBaseUrl = this.computeInternalBaseUrl(env.getProperty(SynchronyEnv.Host.getEnvName()), env.getProperty(SynchronyEnv.Port.getEnvName()), env.getProperty(SynchronyEnv.ContextPath.getEnvName()))) ? externalBaseUrl : String.join((CharSequence)",", externalBaseUrl, internalBaseUrl);
        env.setProperty(SynchronyEnv.ServiceUrl.getEnvName(), serviceUrl);
        ImmutableMap properties = ImmutableMap.builder().putAll((Map)env).build();
        return new SynchronyEnvironment((Map<String, String>)properties, isProxyEnabled, externalBaseUrl, internalBaseUrl);
    }

    @VisibleForTesting
    String computeSynchronyExternalBaseUrl(String defaultSynchronyPort, String contextPath, boolean isProxyEnabled) {
        String confluenceBaseUrl = this.systemInformationService.getConfluenceInfo().getBaseUrl();
        URI confluenceBaseUri = URI.create(confluenceBaseUrl);
        UriBuilder uriBuilder = UriBuilder.fromUri((URI)confluenceBaseUri);
        StringBuilder synchronyBaseUriBuilder = new StringBuilder();
        if (isProxyEnabled) {
            synchronyBaseUriBuilder.append(uriBuilder.replacePath("synchrony-proxy").build(new Object[0]).toASCIIString());
        } else {
            uriBuilder.replacePath(contextPath);
            int synchronyPort = this.tomcatConfigHelper.getProxyPort().orElse(NumberUtils.toInt((String)defaultSynchronyPort));
            if (synchronyPort > 0 && !this.tomcatConfigHelper.isStandardPort(synchronyPort)) {
                uriBuilder.port(synchronyPort);
            }
            synchronyBaseUriBuilder.append(uriBuilder.build(new Object[0]).toASCIIString());
        }
        synchronyBaseUriBuilder.append(",").append(confluenceBaseUrl).append("/synchrony-proxy");
        return synchronyBaseUriBuilder.toString();
    }

    private String computeInternalBaseUrl(String host, String port, String contextPath) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)"http://localhost").host(host).port(NumberUtils.toInt((String)port)).path(contextPath);
        return uriBuilder.build(new Object[0]).toASCIIString();
    }

    @VisibleForTesting
    boolean isSynchronyProxyEnabled(boolean isSynchronyClusterManuallyManaged) {
        if (isSynchronyClusterManuallyManaged) {
            return false;
        }
        String synchronyProxyEnabled = System.getProperty("synchrony.proxy.enabled");
        if (synchronyProxyEnabled != null) {
            log.info("System property synchrony.proxy.enabled: {}", (Object)synchronyProxyEnabled);
            return Boolean.parseBoolean(synchronyProxyEnabled);
        }
        Optional proxyPort = this.tomcatConfigHelper.getProxyPort();
        log.info("proxy port present: {}", (Object)proxyPort.isPresent());
        if (proxyPort.isPresent()) {
            return false;
        }
        log.info("app config synchrony.proxy.enabled: {}", (Object)this.bootstrapManager.getApplicationConfig().getBooleanProperty((Object)"synchrony.proxy.enabled"));
        return this.bootstrapManager.getApplicationConfig().getBooleanProperty((Object)"synchrony.proxy.enabled");
    }

    private int getConfluenceProcessId() {
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        return os.getProcessId();
    }

    private void populateClusterOptions(final Properties env) throws ClusterException {
        ClusterConfig clusterConfig = ClusterConfigurationUtils.getClusterConfig((ApplicationConfiguration)this.bootstrapManager.getApplicationConfig());
        env.setProperty(SynchronyEnv.ClusterImpl.getEnvName(), "hazelcast-btf");
        String ipToListen = SynchronyProcessConfigurationUtils.getIp(clusterConfig, DEFAULT_LISTENING_IP);
        env.setProperty(SynchronyEnv.AlephBind.getEnvName(), ipToListen);
        env.setProperty(SynchronyEnv.HazelcastInterfaces.getEnvName(), ipToListen);
        env.setProperty(SynchronyEnv.HazelcastGroupName.getEnvName(), clusterConfig.getClusterName() + "-Synchrony");
        clusterConfig.getJoinConfig().decode(new ClusterJoinConfig.Decoder(){

            public void accept(TCPIPClusterJoinConfig tcpIpJoinConfig) {
                SynchronyProcessConfigurationUtils.populateTcpIpConfig(env, tcpIpJoinConfig);
            }

            public void accept(MulticastClusterJoinConfig multicastJoinConfig) {
                SynchronyProcessConfigurationUtils.populateMulticastConfig(env, SynchronyEnvironmentBuilder.this.getMulticastConfigWithUpdatedPort(env, multicastJoinConfig));
            }

            public void accept(AWSClusterJoinConfig awsJoinConfig) {
                SynchronyProcessConfigurationUtils.populateAwsConfig(env, awsJoinConfig, ClusterConfigurationUtils.getAwsEc2PrivateIp((String)SynchronyEnvironmentBuilder.DEFAULT_LISTENING_IP));
            }

            public void accept(KubernetesClusterJoinConfig kubernetesJoinConfig) {
                SynchronyProcessConfigurationUtils.populateKubernetesConfig(env);
            }
        });
    }

    @VisibleForTesting
    MulticastClusterJoinConfig getMulticastConfigWithUpdatedPort(Properties env, MulticastClusterJoinConfig defaultMulticastJoinConfig) {
        int multicastPort = 54328;
        Integer sysPropertyPort = Integer.getInteger(CLUSTER_MULTICAST_PORT_SYS_PROP);
        String envPort = env.getProperty(SynchronyEnv.ClusterJoinMulticastPort.getEnvName());
        if (sysPropertyPort != null) {
            multicastPort = sysPropertyPort;
        } else if (envPort != null) {
            multicastPort = Integer.parseInt(envPort);
        }
        return (MulticastClusterJoinConfig)MulticastClusterJoinConfig.createForConfig((InetAddress)defaultMulticastJoinConfig.getMulticastAddress(), (int)defaultMulticastJoinConfig.getMulticastTTL(), (int)multicastPort).right().getOrElse((Object)defaultMulticastJoinConfig);
    }

    public static class SynchronyEnvironment {
        private final Map<String, String> environmentProperties;
        private final String internalBaseUrl;
        private final String externalBaseUrl;
        private final boolean isProxyEnabled;

        private SynchronyEnvironment(Map<String, String> environmentProperties, boolean isProxyEnabled, String externalBaseUrl, String internalBaseUrl) {
            this.environmentProperties = environmentProperties;
            this.isProxyEnabled = isProxyEnabled;
            this.externalBaseUrl = externalBaseUrl;
            this.internalBaseUrl = internalBaseUrl;
        }

        public String getSynchronyProperty(SynchronyEnv env) {
            return this.environmentProperties.getOrDefault(env.getEnvName(), env.getDefaultValue());
        }

        public Map<String, String> getAllSynchronyProperties() {
            return this.environmentProperties;
        }

        public String getInternalBaseUrl() {
            return this.internalBaseUrl;
        }

        public String getExternalBaseUrl() {
            return this.externalBaseUrl;
        }

        public boolean isProxyEnabled() {
            return this.isProxyEnabled;
        }
    }
}

