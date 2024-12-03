/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.config.ApplicationConfig
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.ConfigurationPersister
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.security.random.SecureTokenGenerator
 *  com.atlassian.util.profiling.Ticker
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.config.ApplicationConfig;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.ConfigurationPersister;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.cluster.AWSClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterAlreadyExistsException;
import com.atlassian.confluence.cluster.ClusterCompatibilityValidator;
import com.atlassian.confluence.cluster.ClusterConfig;
import com.atlassian.confluence.cluster.ClusterConfigurationUtils;
import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNotPermittedException;
import com.atlassian.confluence.cluster.KubernetesClusterJoinConfig;
import com.atlassian.confluence.cluster.MulticastClusterJoinConfig;
import com.atlassian.confluence.cluster.TCPIPClusterJoinConfig;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.setup.BootstrapDatabaseAccessor;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.internal.license.LicenseServiceInternal;
import com.atlassian.confluence.setup.ConfluenceConfigurationPersister;
import com.atlassian.confluence.util.ClusterUtils;
import com.atlassian.confluence.util.MulticastRouteTester;
import com.atlassian.confluence.util.profiling.TimedAnalytics;
import com.atlassian.security.random.SecureTokenGenerator;
import com.atlassian.util.profiling.Ticker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultClusterConfigurationHelper
implements ClusterConfigurationHelperInternal {
    private static final Logger log = LoggerFactory.getLogger(DefaultClusterConfigurationHelper.class);
    public static final String CLUSTER = "confluence.cluster";
    public static final String CLUSTER_NAME = "confluence.cluster.name";
    public static final String CLUSTER_INTERFACE = "confluence.cluster.interface";
    public static final String CLUSTER_JOIN_TYPE = "confluence.cluster.join.type";
    public static final String CLUSTER_ADDRESS = "confluence.cluster.address";
    public static final String CLUSTER_TTL = "confluence.cluster.ttl";
    public static final String CLUSTER_MULTICAST_PORT_SYS_PROP = "confluence.cluster.multicast.port";
    public static final String CLUSTER_PEERS = "confluence.cluster.peers";
    public static final String CLUSTER_AWS = "confluence.cluster.aws";
    public static final String CLUSTER_AWS_ACCESS_KEY = "confluence.cluster.aws.access.key";
    public static final String CLUSTER_AWS_SECRET_KEY = "confluence.cluster.aws.secret.key";
    public static final String CLUSTER_AWS_IAM_ROLE = "confluence.cluster.aws.iam.role";
    public static final String CLUSTER_AWS_TAG_KEY = "confluence.cluster.aws.tag.key";
    public static final String CLUSTER_AWS_TAG_VALUE = "confluence.cluster.aws.tag.value";
    public static final String CLUSTER_AWS_REGION = "confluence.cluster.aws.region";
    public static final String CLUSTER_AWS_HOST_HEADER = "confluence.cluster.aws.host.header";
    public static final String CLUSTER_AWS_SECURITY_GROUP_NAME = "confluence.cluster.aws.security.group.name";
    public static final String CLUSTER_HOME = "confluence.cluster.home";
    public static final String SHARED_HOME = "shared-home";
    private static final String CONFIG_FILE = "confluence.cfg.xml";
    public static final String CLUSTER_AUTHENTICATION_ENABLED = "confluence.cluster.authentication.enabled";
    public static final String CLUSTER_AUTHENTICATION_ENABLED_DEFAULT_VAL = "true";
    public static final String CLUSTER_AUTHENTICATION_SECRET = "confluence.cluster.authentication.secret";
    public static final List<String> SUPPORTED_SHARED_HOME_CONFIG_PROPERTIES = ImmutableList.of((Object)"confluence.cluster", (Object)"hibernate.setup", (Object)"access.mode", (Object)"lucene.index.dir", (Object)"atlassian.license.message", (Object)"lucene.index.dir", (Object)"jwt.private.key", (Object)"jwt.public.key", (Object)"confluence.cluster.authentication.enabled", (Object)"confluence.cluster.authentication.secret", (Object)"synchrony.service.authtoken");
    public static final Set<String> CLUSTER_SETUP_ENTRIES = ImmutableSet.of((Object)"confluence.cluster", (Object)"confluence.cluster.name", (Object)"confluence.cluster.interface", (Object)"confluence.cluster.join.type", (Object)"confluence.cluster.address", (Object)"confluence.cluster.ttl", (Object[])new String[]{"confluence.cluster.peers", "confluence.cluster.aws", "confluence.cluster.aws.access.key", "confluence.cluster.aws.secret.key", "confluence.cluster.aws.iam.role", "confluence.cluster.aws.tag.key", "confluence.cluster.aws.tag.value", "confluence.cluster.aws.region", "confluence.cluster.aws.host.header", "confluence.cluster.aws.security.group.name", "confluence.cluster.home", "access.mode"});
    private static final int MULTICAST_TEST_PORT = 33333;
    private final ClusterManager clusterManager;
    private final ApplicationConfig applicationConfig;
    private final LicenseServiceInternal licenseService;
    private final SingleConnectionProvider databaseHelper;
    private final HibernateConfig hibernateConfig;
    private final ClusterCompatibilityValidator clusterCompatibilityValidator;
    private final SecureTokenGenerator secureTokenGenerator;

    public DefaultClusterConfigurationHelper(ApplicationConfig applicationConfig, ClusterManager clusterManager, LicenseServiceInternal licenseService, SingleConnectionProvider databaseHelper, HibernateConfig hibernateConfig, ClusterCompatibilityValidator clusterCompatibilityValidator, SecureTokenGenerator secureTokenGenerator) {
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.applicationConfig = Objects.requireNonNull(applicationConfig);
        this.licenseService = Objects.requireNonNull(licenseService);
        this.databaseHelper = Objects.requireNonNull(databaseHelper);
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
        this.clusterCompatibilityValidator = Objects.requireNonNull(clusterCompatibilityValidator);
        this.secureTokenGenerator = secureTokenGenerator;
    }

    private Optional<ApplicationConfig> getBaseClusterApplicationConfig() {
        try {
            Optional<File> sharedHomeDir = this.sharedHome();
            if (!sharedHomeDir.isPresent()) {
                return Optional.empty();
            }
            ApplicationConfig clusterApplicationConfig = new ApplicationConfig();
            clusterApplicationConfig.setConfigurationPersister((ConfigurationPersister)new ConfluenceConfigurationPersister());
            clusterApplicationConfig.setApplicationHome(sharedHomeDir.get().getPath());
            clusterApplicationConfig.setConfigurationFileName(CONFIG_FILE);
            return Optional.of(clusterApplicationConfig);
        }
        catch (ConfigurationException e) {
            log.warn("Error retrieving base cluster application config", (Throwable)e);
            return Optional.empty();
        }
    }

    protected Optional<ApplicationConfig> getClusterApplicationConfig() {
        return this.getBaseClusterApplicationConfig().filter(clusterApplicationConfig -> clusterApplicationConfig.configFileExists());
    }

    private boolean loadClusterConfig(ApplicationConfig clusterApplicationConfig) {
        if (!Files.exists(Paths.get(clusterApplicationConfig.getApplicationHome(), new String[0]), new LinkOption[0])) {
            log.warn("Could not find shared home folder.");
            return false;
        }
        if (!clusterApplicationConfig.configFileExists()) {
            log.warn("Could not find shared config file.");
            return false;
        }
        try {
            clusterApplicationConfig.load();
            return true;
        }
        catch (ConfigurationException e) {
            log.error("Could not load the config from the shared home folder", (Throwable)e);
            return false;
        }
    }

    @Override
    public void createClusterConfig() {
        this.getBaseClusterApplicationConfig().filter(clusterApplicationConfig -> !clusterApplicationConfig.configFileExists()).ifPresent(clusterApplicationConfig -> this.saveSetupConfigIntoSharedHome());
    }

    @Override
    public void populateExistingClusterSetupConfig() {
        if (!this.isClusteredInstance()) {
            return;
        }
        this.getClusterApplicationConfig().ifPresent(clusterApplicationConfig -> {
            log.info("Populating setup configuration if running with Cluster mode...");
            try {
                if ("complete".equals(this.applicationConfig.getCurrentSetupStep())) {
                    log.debug("Syncing the access.mode from the shared home folder");
                    this.getSharedProperty("access.mode").ifPresent(sharedAccessMode -> {
                        this.applicationConfig.setProperty((Object)"access.mode", sharedAccessMode);
                        try {
                            this.applicationConfig.save();
                        }
                        catch (ConfigurationException e) {
                            log.error("Cannot sync the shared access.mode from the shared home folder to the local config", (Throwable)e);
                        }
                    });
                    log.debug("Setup is completed. we don't need to populate reconfiguration from shared home folder.");
                    return;
                }
                if (!this.loadClusterConfig((ApplicationConfig)clusterApplicationConfig)) {
                    log.info("Shared application config is not available. Leaving local config as is.");
                    return;
                }
                ApplicationConfig applicationConfig = this.applicationConfig;
                synchronized (applicationConfig) {
                    int currentBuildNumber = 0;
                    try {
                        currentBuildNumber = Integer.parseInt(this.applicationConfig.getBuildNumber());
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                    if (currentBuildNumber <= 0) {
                        log.debug("Populating build number from shared configuration file to local configuration file");
                        this.applicationConfig.setBuildNumber(clusterApplicationConfig.getBuildNumber());
                    }
                    this.applicationConfig.setSetupType(clusterApplicationConfig.getSetupType());
                    this.applicationConfig.setCurrentSetupStep(clusterApplicationConfig.getCurrentSetupStep());
                    SUPPORTED_SHARED_HOME_CONFIG_PROPERTIES.forEach(key -> {
                        Object value = clusterApplicationConfig.getProperty(key);
                        if (value != null) {
                            this.applicationConfig.setProperty(key, value);
                        }
                    });
                    if ("complete".equals(this.applicationConfig.getCurrentSetupStep())) {
                        this.applicationConfig.setSetupComplete(true);
                    }
                    this.applicationConfig.save();
                }
            }
            catch (ConfigurationException e) {
                log.error("Could not load setup stage from shared home folder", (Throwable)e);
            }
            log.info("Finish Populating setup configuration if running with Cluster mode");
        });
    }

    @Override
    public void saveSharedProperty(Object key, Object value) {
        this.getClusterApplicationConfig().ifPresent(clusterApplicationConfig -> {
            try {
                log.info("Saving {} into the shared confluence.cfg.xml file...", key);
                clusterApplicationConfig.load();
                clusterApplicationConfig.setProperty(key, value);
                clusterApplicationConfig.save();
            }
            catch (ConfigurationException e) {
                log.error("Could not save {} into the shared confluence.cfg.xml file", key, (Object)e);
            }
            log.info("Finished writing {} into the shared confluence.cfg.xml file", key);
        });
    }

    @Override
    public Optional<Object> getSharedProperty(Object key) {
        ApplicationConfig clusterApplicationConfig;
        if (this.getClusterApplicationConfig().isPresent() && this.loadClusterConfig(clusterApplicationConfig = this.getClusterApplicationConfig().get())) {
            return Optional.ofNullable(clusterApplicationConfig.getProperty(key));
        }
        return Optional.empty();
    }

    @Override
    public void saveSharedBuildNumber(String sharedBuildNumber) {
        this.getClusterApplicationConfig().ifPresent(clusterApplicationConfig -> {
            try {
                log.info("Saving the build number into shared home...");
                if (this.loadClusterConfig((ApplicationConfig)clusterApplicationConfig)) {
                    clusterApplicationConfig.setBuildNumber(this.applicationConfig.getBuildNumber());
                    clusterApplicationConfig.save();
                }
            }
            catch (ConfigurationException e) {
                log.error("Could not save the build number into shared home folder", (Throwable)e);
            }
        });
    }

    @Override
    public Optional<String> getSharedBuildNumber() {
        ApplicationConfig clusterApplicationConfig;
        if (this.getClusterApplicationConfig().isPresent() && this.loadClusterConfig(clusterApplicationConfig = this.getClusterApplicationConfig().get())) {
            return Optional.ofNullable(clusterApplicationConfig.getBuildNumber());
        }
        return Optional.empty();
    }

    @Override
    public void saveSetupConfigIntoSharedHome() {
        this.getBaseClusterApplicationConfig().ifPresent(clusterApplicationConfig -> {
            try {
                log.info("Writing setup configuration into shared home...");
                clusterApplicationConfig.setBuildNumber(this.applicationConfig.getBuildNumber());
                clusterApplicationConfig.setConfigurationFileName(CONFIG_FILE);
                clusterApplicationConfig.setSetupType(this.applicationConfig.getSetupType());
                clusterApplicationConfig.setCurrentSetupStep(this.applicationConfig.getCurrentSetupStep());
                SUPPORTED_SHARED_HOME_CONFIG_PROPERTIES.forEach(key -> {
                    Object value = this.applicationConfig.getProperty(key);
                    if (value != null) {
                        clusterApplicationConfig.setProperty(key, value);
                    }
                });
                clusterApplicationConfig.save();
            }
            catch (ConfigurationException e) {
                log.error("Could not save setup stage into shared home folder", (Throwable)e);
            }
            log.info("Finished writing setup configuration into shared home");
        });
    }

    @Override
    public boolean isClusteredInstance() {
        return this.clusterManager.isClustered();
    }

    @Override
    public boolean isClusterHomeConfigured() {
        return ClusterConfigurationUtils.isClusterHomeConfigured((ApplicationConfiguration)this.applicationConfig);
    }

    @Override
    public void createCluster(String clusterName, File clusterHome, @Nullable String networkInterfaceName, ClusterJoinConfig joinConfig) throws ClusterException {
        if (!this.licenseService.isLicensedForDataCenter()) {
            throw new ClusterNotPermittedException();
        }
        NetworkInterface networkInterface = this.getNetworkInterfaceByName(networkInterfaceName);
        ClusterConfig config = new ClusterConfig(joinConfig, clusterName, clusterHome, networkInterface);
        this.createClusterInternal(config);
    }

    private void createClusterInternal(ClusterConfig config) throws ClusterException {
        this.performPreClusterChecks(config);
        try {
            this.createCluster(config);
        }
        catch (ClusterException e) {
            this.clusterManager.stopCluster();
            throw e;
        }
    }

    private void createCluster(ClusterConfig config) throws ClusterException {
        if (log.isInfoEnabled()) {
            log.info("Creating new cluster with configuration {}", (Object)config);
        }
        this.clusterManager.reconfigure(config);
        if (this.clusterManager.getClusterInformation().getMemberCount() >= 2) {
            throw new ClusterAlreadyExistsException(config.getClusterName(), config.getJoinConfig());
        }
        this.setClusterConfig(config);
    }

    private NetworkInterface getNetworkInterfaceByName(@Nullable String networkInterfaceName) {
        NetworkInterface networkInterface = null;
        if (networkInterfaceName != null) {
            try {
                networkInterface = NetworkInterface.getByName(networkInterfaceName);
            }
            catch (SocketException e) {
                throw new RuntimeException("Could not get network interface '" + networkInterfaceName + "'", e);
            }
        }
        return networkInterface;
    }

    @Override
    public void bootstrapCluster(BootstrapDatabaseAccessor.BootstrapDatabaseData bootstrapDatabaseData) throws ClusterException {
        block10: {
            this.configureDevClusterIfSpecified();
            try (Ticker t = TimedAnalytics.timedAnalytics().start("confluence.profiling.startup.bootstrap-cluster");){
                if (this.isClusteredInstance()) {
                    try {
                        this.syncLocalAndSharedClusterAuthConfigProps();
                    }
                    catch (ConfigurationException e) {
                        log.error("Could not update shared config properties into local config file.");
                    }
                    this.clusterManager.reconfigure(ClusterConfigurationUtils.getClusterConfig((ApplicationConfiguration)this.applicationConfig));
                    this.clusterCompatibilityValidator.validate(bootstrapDatabaseData);
                    break block10;
                }
                if (this.applicationConfig.isSetupComplete()) {
                    this.createSharedHome();
                }
            }
        }
    }

    private void configureDevClusterIfSpecified() throws ClusterException {
        String devClusterName = StringUtils.trimToNull((String)System.getProperty("confluence.dev.cluster.name"));
        if (devClusterName != null) {
            InetAddress multicastAddress = ClusterUtils.hashNameToMulticastAddress(devClusterName);
            MulticastClusterJoinConfig joinConfig = (MulticastClusterJoinConfig)MulticastClusterJoinConfig.createForAddress(multicastAddress).getOrError(() -> "Failed to create dev cluster join config");
            File sharedHome = ClusterConfigurationUtils.getSharedHome((ApplicationConfiguration)this.applicationConfig);
            if (!sharedHome.isDirectory() && !sharedHome.mkdirs()) {
                log.warn("Failed to created shared home at {}", (Object)sharedHome);
            }
            NetworkInterface loopbackInterface = DefaultClusterConfigurationHelper.findLoopbackInterface().orElseThrow(() -> new ClusterException("No looback interface present for dev cluster"));
            ClusterConfig config = new ClusterConfig(joinConfig, devClusterName, sharedHome, loopbackInterface);
            log.warn("Configuring dev cluster with {}", (Object)config);
            this.setClusterConfig(config);
        }
    }

    static Optional<NetworkInterface> findLoopbackInterface() {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!networkInterface.isLoopback()) continue;
                return Optional.of(networkInterface);
            }
        }
        catch (SocketException ex) {
            log.error("Failed to determine loopback interface", (Throwable)ex);
        }
        return Optional.empty();
    }

    private void performPreClusterChecks(ClusterConfig config) throws ClusterException {
        this.performPreClusterChecks(config.getJoinConfig(), config.getNetworkInterface(), config.getClusterHome());
    }

    private void performPreClusterChecks(ClusterJoinConfig joinConfig, NetworkInterface networkInterface, File clusterHome) throws ClusterException {
        this.checkMulticastRoutingIfRequired(joinConfig, networkInterface);
        ClusterConfigurationUtils.checkSharedHomeIsNotLocalHome(clusterHome, (ApplicationConfiguration)this.applicationConfig);
    }

    private void checkMulticastRoutingIfRequired(ClusterJoinConfig joinConfig, NetworkInterface networkInterface) {
        if (!(joinConfig instanceof MulticastClusterJoinConfig)) {
            return;
        }
        MulticastRouteTester tester = new MulticastRouteTester(((MulticastClusterJoinConfig)joinConfig).getMulticastAddress(), networkInterface, 33333);
        tester.run();
    }

    @Override
    public List<NetworkInterface> getClusterableInterfaces() {
        try {
            ArrayList interfaces = Lists.newArrayList(ClusterUtils.getClusterableInterfaces());
            Collections.sort(interfaces, new AlphabeticalInterfacesWithLoopbackLast());
            return interfaces;
        }
        catch (SocketException e) {
            throw new RuntimeException("Exception while enumerating network interfaces", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Optional<File> sharedHome() {
        ApplicationConfig applicationConfig = this.applicationConfig;
        synchronized (applicationConfig) {
            return Optional.of(ClusterConfigurationUtils.getSharedHome((ApplicationConfiguration)this.applicationConfig));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void createSharedHome() {
        ApplicationConfig applicationConfig = this.applicationConfig;
        synchronized (applicationConfig) {
            File sharedHome;
            if (!(this.clusterManager.isClustered() || (sharedHome = this.sharedHome().get()).exists() || sharedHome.mkdir())) {
                throw new IllegalStateException("Failed to create shared home directory in " + sharedHome);
            }
        }
    }

    @Override
    public Optional<ClusterJoinConfig> joinConfig() {
        try {
            return Optional.of(ClusterConfigurationUtils.getClusterConfig((ApplicationConfiguration)this.applicationConfig).getJoinConfig());
        }
        catch (Exception e) {
            log.warn("Could not get cluster config from configuration file: {}", (Object)e.getMessage());
            return Optional.empty();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setClusterConfig(ClusterConfig config) throws ClusterException {
        ApplicationConfig applicationConfig = this.applicationConfig;
        synchronized (applicationConfig) {
            this.applicationConfig.setProperty((Object)CLUSTER, (Object)CLUSTER_AUTHENTICATION_ENABLED_DEFAULT_VAL);
            this.applicationConfig.setProperty((Object)CLUSTER_NAME, (Object)config.getClusterName());
            this.applicationConfig.setProperty((Object)CLUSTER_HOME, (Object)config.getClusterHome().getPath());
            if (config.getNetworkInterface() != null) {
                this.applicationConfig.setProperty((Object)CLUSTER_INTERFACE, (Object)config.getNetworkInterface().getName());
            }
            if (Objects.isNull(this.applicationConfig.getProperty((Object)CLUSTER_AUTHENTICATION_ENABLED)) && !"false".equals(this.applicationConfig.getProperty((Object)CLUSTER_AUTHENTICATION_ENABLED))) {
                log.info("Setting property in applicationConfig CLUSTER_AUTHENTICATION_ENABLED.");
                this.applicationConfig.setProperty((Object)CLUSTER_AUTHENTICATION_ENABLED, (Object)CLUSTER_AUTHENTICATION_ENABLED_DEFAULT_VAL);
                if (Objects.isNull(this.applicationConfig.getProperty((Object)CLUSTER_AUTHENTICATION_SECRET))) {
                    log.info("Setting property in applicationConfig CLUSTER_AUTHENTICATION_SECRET.");
                    this.applicationConfig.setProperty((Object)CLUSTER_AUTHENTICATION_SECRET, (Object)this.generateSharedSecret());
                }
            }
            this.applicationConfig.setProperty((Object)CLUSTER_JOIN_TYPE, (Object)config.getJoinConfig().getType().getText());
            config.getJoinConfig().decode(new ClusterJoinConfig.Decoder(){

                @Override
                public void accept(TCPIPClusterJoinConfig tcpIpJoinConfig) {
                    DefaultClusterConfigurationHelper.this.setTcpIpJoinConfig(tcpIpJoinConfig);
                }

                @Override
                public void accept(MulticastClusterJoinConfig multicastJoinConfig) {
                    DefaultClusterConfigurationHelper.this.setMulticastJoinConfig(multicastJoinConfig);
                }

                @Override
                public void accept(AWSClusterJoinConfig awsJoinConfig) {
                    DefaultClusterConfigurationHelper.this.setAwsJoinConfig(awsJoinConfig);
                }

                @Override
                public void accept(KubernetesClusterJoinConfig kubernetesJoinConfig) {
                }
            });
            try {
                this.applicationConfig.save();
            }
            catch (ConfigurationException e) {
                throw new ClusterException("Could not save new cluster settings: " + e.getMessage(), e);
            }
        }
    }

    private void setAwsJoinConfig(AWSClusterJoinConfig joinConfig) {
        this.applicationConfig.setProperty((Object)CLUSTER_AWS_ACCESS_KEY, (Object)joinConfig.getAccessKey().orElse(""));
        this.applicationConfig.setProperty((Object)CLUSTER_AWS_SECRET_KEY, (Object)joinConfig.getSecretKey().orElse(""));
        this.applicationConfig.setProperty((Object)CLUSTER_AWS_IAM_ROLE, (Object)joinConfig.getIamRole().orElse(""));
        this.applicationConfig.setProperty((Object)CLUSTER_AWS_REGION, (Object)joinConfig.getRegion().orElse(""));
        this.applicationConfig.setProperty((Object)CLUSTER_AWS_SECURITY_GROUP_NAME, (Object)joinConfig.getSecurityGroupName().orElse(""));
        this.applicationConfig.setProperty((Object)CLUSTER_AWS_TAG_KEY, (Object)joinConfig.getTagKey().orElse(""));
        this.applicationConfig.setProperty((Object)CLUSTER_AWS_TAG_VALUE, (Object)joinConfig.getTagValue().orElse(""));
    }

    private void setTcpIpJoinConfig(TCPIPClusterJoinConfig joinConfig) {
        this.applicationConfig.setProperty((Object)CLUSTER_PEERS, (Object)joinConfig.getPeerAddressString());
    }

    private void setMulticastJoinConfig(MulticastClusterJoinConfig joinConfig) {
        this.applicationConfig.setProperty((Object)CLUSTER_ADDRESS, (Object)joinConfig.getMulticastAddress().getHostAddress());
        this.applicationConfig.setProperty((Object)CLUSTER_TTL, joinConfig.getMulticastTTL());
    }

    private void syncLocalAndSharedClusterAuthConfigProps() throws ConfigurationException {
        this.updateOrSetLocalAndSharedConfigProperty(CLUSTER_AUTHENTICATION_ENABLED, CLUSTER_AUTHENTICATION_ENABLED_DEFAULT_VAL);
        if (this.applicationConfig.getBooleanProperty((Object)CLUSTER_AUTHENTICATION_ENABLED)) {
            this.updateOrSetLocalAndSharedConfigProperty(CLUSTER_AUTHENTICATION_SECRET, this.generateSharedSecret());
        }
    }

    @VisibleForTesting
    void updateOrSetLocalAndSharedConfigProperty(String key, Object defaultVal) throws ConfigurationException {
        Optional<Object> sharedProperty = this.getSharedProperty(key);
        if (sharedProperty.isPresent()) {
            log.debug("Syncing {} into local from shared confluence.cfg.xml file.", (Object)key);
            this.saveLocalProperty(key, sharedProperty.get());
        } else {
            String localValue = (String)this.applicationConfig.getProperty((Object)key);
            if (localValue == null) {
                log.debug("{} is not set in local confluence.cfg.xml file", (Object)key);
                log.debug("Setting {} is into local confluence.cfg.xml file", (Object)key);
                this.saveLocalProperty(key, defaultVal);
            }
            this.saveSharedProperty(key, this.applicationConfig.getProperty((Object)key));
        }
    }

    @VisibleForTesting
    void saveLocalProperty(String key, Object sharedVal) throws ConfigurationException {
        if (Objects.isNull(this.applicationConfig.getProperty((Object)key)) || !sharedVal.equals(this.applicationConfig.getProperty((Object)key))) {
            log.debug("Saving {} into the local confluence.cfg.xml file.", (Object)key);
            this.applicationConfig.setProperty((Object)key, sharedVal);
            this.applicationConfig.save();
        }
    }

    private String generateSharedSecret() {
        return this.secureTokenGenerator.generateToken();
    }

    private static class AlphabeticalInterfacesWithLoopbackLast
    implements Comparator<NetworkInterface> {
        private AlphabeticalInterfacesWithLoopbackLast() {
        }

        @Override
        public int compare(NetworkInterface iface1, NetworkInterface iface2) {
            boolean iface2Loopback;
            boolean bothInterfacesSameClass;
            boolean iface1Loopback = ClusterUtils.isLoopbackInterface(iface1);
            boolean bl = bothInterfacesSameClass = iface1Loopback == (iface2Loopback = ClusterUtils.isLoopbackInterface(iface2));
            if (bothInterfacesSameClass) {
                return iface1.getName().compareTo(iface2.getName());
            }
            return iface1Loopback ? -1 : 1;
        }
    }
}

