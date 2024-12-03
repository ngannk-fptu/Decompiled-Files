/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.EhcacheDefaultClassLoader;
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.ConfigError;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.config.ManagementRESTServiceConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.config.TerracottaClientConfiguration;
import net.sf.ehcache.config.generator.ConfigurationSource;
import net.sf.ehcache.transaction.manager.DefaultTransactionManagerLookup;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Configuration {
    public static final boolean DEFAULT_DYNAMIC_CONFIG = true;
    public static final int DEFAULT_TRANSACTION_TIMEOUT = 15;
    @Deprecated
    public static final long DEFAULT_MAX_BYTES_ON_HEAP = 0L;
    public static final long DEFAULT_MAX_BYTES_OFF_HEAP = 0L;
    public static final long DEFAULT_MAX_BYTES_ON_DISK = 0L;
    public static final Monitoring DEFAULT_MONITORING = Monitoring.AUTODETECT;
    public static final SizeOfPolicyConfiguration DEFAULT_SIZEOF_POLICY_CONFIGURATION = new SizeOfPolicyConfiguration();
    public static final FactoryConfiguration DEFAULT_TRANSACTION_MANAGER_LOOKUP_CONFIG = Configuration.getDefaultTransactionManagerLookupConfiguration();
    private static final int HUNDRED = 100;
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);
    private volatile RuntimeCfg cfg;
    private final List<PropertyChangeListener> propertyChangeListeners = new CopyOnWriteArrayList<PropertyChangeListener>();
    private String cacheManagerName;
    private int defaultTransactionTimeoutInSeconds = 15;
    private Monitoring monitoring = DEFAULT_MONITORING;
    private DiskStoreConfiguration diskStoreConfiguration;
    private CacheConfiguration defaultCacheConfiguration;
    private final List<FactoryConfiguration> cacheManagerPeerProviderFactoryConfiguration = new ArrayList<FactoryConfiguration>();
    private final List<FactoryConfiguration> cacheManagerPeerListenerFactoryConfiguration = new ArrayList<FactoryConfiguration>();
    private SizeOfPolicyConfiguration sizeOfPolicyConfiguration;
    private FactoryConfiguration transactionManagerLookupConfiguration;
    private FactoryConfiguration cacheManagerEventListenerFactoryConfiguration;
    private TerracottaClientConfiguration terracottaConfigConfiguration;
    private ManagementRESTServiceConfiguration managementRESTService;
    private final Map<String, CacheConfiguration> cacheConfigurations = new ConcurrentHashMap<String, CacheConfiguration>();
    private ConfigurationSource configurationSource;
    private boolean dynamicConfig = true;
    private Long maxBytesLocalHeap;
    private String maxBytesLocalHeapInput;
    private Long maxBytesLocalOffHeap;
    private String maxBytesLocalOffHeapInput;
    private Long maxBytesLocalDisk;
    private String maxBytesLocalDiskInput;
    private volatile ClassLoader classLoader = EhcacheDefaultClassLoader.getInstance();

    static Set<Cache> getAllActiveCaches(CacheManager cacheManager) {
        HashSet<Cache> caches = new HashSet<Cache>();
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) continue;
            caches.add(cache);
        }
        return caches;
    }

    public RuntimeCfg setupFor(CacheManager cacheManager, String fallbackName) throws InvalidConfigurationException {
        Collection<ConfigError> errors;
        if (this.cfg != null) {
            if (this.cfg.cacheManager == cacheManager) {
                return this.cfg;
            }
            if (this.cfg.cacheManager.getStatus() != Status.STATUS_SHUTDOWN) {
                throw new IllegalStateException("You cannot share a Configuration instance across multiple running CacheManager instances");
            }
        }
        if (!(errors = this.validate()).isEmpty()) {
            throw new InvalidConfigurationException(errors);
        }
        this.cfg = new RuntimeCfg(cacheManager, fallbackName);
        return this.cfg;
    }

    public Collection<ConfigError> validate() {
        ArrayList<ConfigError> errors = new ArrayList<ConfigError>();
        for (CacheConfiguration cacheConfiguration : this.cacheConfigurations.values()) {
            errors.addAll(cacheConfiguration.validate(this));
        }
        return errors;
    }

    public boolean isMaxBytesLocalDiskSet() {
        return this.maxBytesLocalDisk != null;
    }

    public boolean isMaxBytesLocalOffHeapSet() {
        return this.maxBytesLocalOffHeap != null;
    }

    @Deprecated
    public boolean isMaxBytesLocalHeapSet() {
        return this.maxBytesLocalHeap != null;
    }

    private static FactoryConfiguration getDefaultTransactionManagerLookupConfiguration() {
        FactoryConfiguration configuration = new FactoryConfiguration();
        configuration.setClass(DefaultTransactionManagerLookup.class.getName());
        return configuration;
    }

    public final Configuration name(String name) {
        this.setName(name);
        return this;
    }

    public final void setName(String name) {
        this.assertArgumentNotNull("name", name);
        String prop = "cacheManagerName";
        boolean publishChange = this.checkDynChange("cacheManagerName");
        String oldValue = this.cacheManagerName;
        this.cacheManagerName = name;
        if (publishChange) {
            this.firePropertyChange("cacheManagerName", oldValue, name);
        }
    }

    private void assertArgumentNotNull(String name, Object object) {
        if (object == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }

    public final String getName() {
        return this.cacheManagerName;
    }

    @Deprecated
    public final Configuration updateCheck(boolean updateCheck) {
        return this;
    }

    @Deprecated
    public final void setUpdateCheck(boolean updateCheck) {
    }

    @Deprecated
    public final boolean getUpdateCheck() {
        return false;
    }

    public final Configuration defaultTransactionTimeoutInSeconds(int defaultTransactionTimeoutInSeconds) {
        this.setDefaultTransactionTimeoutInSeconds(defaultTransactionTimeoutInSeconds);
        return this;
    }

    public final void setDefaultTransactionTimeoutInSeconds(int defaultTransactionTimeoutInSeconds) {
        String prop = "defaultTransactionTimeoutInSeconds";
        boolean publish = this.checkDynChange("defaultTransactionTimeoutInSeconds");
        int oldValue = this.defaultTransactionTimeoutInSeconds;
        this.defaultTransactionTimeoutInSeconds = defaultTransactionTimeoutInSeconds;
        if (publish) {
            this.firePropertyChange("defaultTransactionTimeoutInSeconds", oldValue, defaultTransactionTimeoutInSeconds);
        }
    }

    public final int getDefaultTransactionTimeoutInSeconds() {
        return this.defaultTransactionTimeoutInSeconds;
    }

    public final Configuration monitoring(Monitoring monitoring) {
        if (null == monitoring) {
            throw new IllegalArgumentException("Monitoring value must be non-null");
        }
        String prop = "monitoring";
        boolean publish = this.checkDynChange("monitoring");
        Monitoring oldValue = this.monitoring;
        this.monitoring = monitoring;
        if (publish) {
            this.firePropertyChange("monitoring", oldValue, monitoring);
        }
        return this;
    }

    public final void setMonitoring(String monitoring) {
        this.assertArgumentNotNull("Monitoring", monitoring);
        this.monitoring(Monitoring.valueOf(Monitoring.class, monitoring.toUpperCase()));
    }

    public final Monitoring getMonitoring() {
        return this.monitoring;
    }

    public final Configuration dynamicConfig(boolean dynamicConfig) {
        this.setDynamicConfig(dynamicConfig);
        return this;
    }

    public final void setDynamicConfig(boolean dynamicConfig) {
        String prop = "dynamicConfig";
        boolean publish = this.checkDynChange("dynamicConfig");
        boolean oldValue = this.dynamicConfig;
        this.dynamicConfig = dynamicConfig;
        if (publish) {
            this.firePropertyChange("dynamicConfig", oldValue, dynamicConfig);
        }
    }

    public final boolean getDynamicConfig() {
        return this.dynamicConfig;
    }

    @Deprecated
    public long getMaxBytesLocalHeap() {
        return this.maxBytesLocalHeap == null ? 0L : this.maxBytesLocalHeap;
    }

    @Deprecated
    public void setMaxBytesLocalHeap(String maxBytesOnHeap) {
        this.assertArgumentNotNull("MaxBytesLocalHeap", maxBytesOnHeap);
        String origInput = this.maxBytesLocalHeapInput;
        try {
            this.maxBytesLocalHeapInput = maxBytesOnHeap;
            if (this.isPercentage(maxBytesOnHeap)) {
                long maxMemory = Runtime.getRuntime().maxMemory();
                long mem = maxMemory / 100L * (long)this.parsePercentage(maxBytesOnHeap);
                this.setMaxBytesLocalHeap(mem);
            } else {
                this.setMaxBytesLocalHeap(MemoryUnit.parseSizeInBytes(maxBytesOnHeap));
            }
        }
        catch (RuntimeException rte) {
            this.maxBytesLocalHeapInput = origInput;
            throw rte;
        }
    }

    @Deprecated
    public String getMaxBytesLocalHeapAsString() {
        return this.maxBytesLocalHeapInput != null ? this.maxBytesLocalHeapInput : Long.toString(this.getMaxBytesLocalHeap());
    }

    private int parsePercentage(String stringValue) {
        String trimmed = stringValue.trim();
        int percentage = Integer.parseInt(trimmed.substring(0, trimmed.length() - 1));
        if (percentage > 100 || percentage < 0) {
            throw new IllegalArgumentException("Percentage need values need to be between 0 and 100 inclusive, but got : " + percentage);
        }
        return percentage;
    }

    private boolean isPercentage(String stringValue) {
        String trimmed = stringValue.trim();
        return trimmed.charAt(trimmed.length() - 1) == '%';
    }

    @Deprecated
    public void setMaxBytesLocalHeap(Long maxBytesOnHeap) {
        String prop = "maxBytesLocalHeap";
        this.verifyGreaterThanZero(maxBytesOnHeap, "maxBytesLocalHeap");
        boolean publish = this.checkDynChange("maxBytesLocalHeap");
        Long oldValue = this.maxBytesLocalHeap;
        this.maxBytesLocalHeap = maxBytesOnHeap;
        if (publish) {
            this.firePropertyChange("maxBytesLocalHeap", oldValue, maxBytesOnHeap);
        }
    }

    @Deprecated
    public Configuration maxBytesLocalHeap(long amount, MemoryUnit memoryUnit) {
        this.setMaxBytesLocalHeap(memoryUnit.toBytes(amount));
        return this;
    }

    public long getMaxBytesLocalOffHeap() {
        return this.maxBytesLocalOffHeap == null ? 0L : this.maxBytesLocalOffHeap;
    }

    public void setMaxBytesLocalOffHeap(String maxBytesOffHeap) {
        this.assertArgumentNotNull("MaxBytesLocalOffHeap", maxBytesOffHeap);
        String origInput = this.maxBytesLocalOffHeapInput;
        try {
            this.maxBytesLocalOffHeapInput = maxBytesOffHeap;
            if (this.isPercentage(maxBytesOffHeap)) {
                long maxMemory = this.getOffHeapLimit();
                long mem = maxMemory / 100L * (long)this.parsePercentage(maxBytesOffHeap);
                this.setMaxBytesLocalOffHeap(mem);
            } else {
                this.setMaxBytesLocalOffHeap(MemoryUnit.parseSizeInBytes(maxBytesOffHeap));
            }
        }
        catch (RuntimeException rte) {
            this.maxBytesLocalOffHeapInput = origInput;
            throw rte;
        }
    }

    public String getMaxBytesLocalOffHeapAsString() {
        return this.maxBytesLocalOffHeapInput != null ? this.maxBytesLocalOffHeapInput : Long.toString(this.getMaxBytesLocalOffHeap());
    }

    public long getTotalConfiguredOffheap() {
        long total = this.getMaxBytesLocalOffHeap();
        for (String cacheName : this.getCacheConfigurationsKeySet()) {
            CacheConfiguration config = this.getCacheConfigurations().get(cacheName);
            total += config.getMaxBytesLocalOffHeap();
        }
        return total;
    }

    private long getOffHeapLimit() {
        try {
            Class<?> enterpriseFmClass = Class.forName("net.sf.ehcache.EnterpriseFeaturesManager");
            try {
                return (Long)enterpriseFmClass.getMethod("getMaxBytesAllocatable", new Class[0]).invoke(null, new Object[0]);
            }
            catch (NoSuchMethodException e) {
                throw new CacheException("Cache: " + this.getName() + " cannot find static factory method create(Ehcache, String) in store class net.sf.ehcache.EnterpriseFeaturesManager", e);
            }
            catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                throw new CacheException("Cache: " + this.getName() + " cannot instantiate store net.sf.ehcache.EnterpriseFeaturesManager", cause);
            }
            catch (IllegalAccessException e) {
                throw new CacheException("Cache: " + this.getName() + " cannot instantiate store net.sf.ehcache.EnterpriseFeaturesManager", e);
            }
        }
        catch (ClassNotFoundException e) {
            throw new CacheException("Cache " + this.getName() + " cannot be configured because the off-heap store class could not be found. You must use an enterprise version of Ehcache to successfully enable overflowToOffHeap.");
        }
    }

    public void setMaxBytesLocalOffHeap(Long maxBytesOffHeap) {
        String prop = "maxBytesLocalOffHeap";
        this.verifyGreaterThanZero(maxBytesOffHeap, prop);
        boolean publish = this.checkDynChange(prop);
        Long oldValue = this.maxBytesLocalOffHeap;
        this.maxBytesLocalOffHeap = maxBytesOffHeap;
        if (publish) {
            this.firePropertyChange(prop, oldValue, maxBytesOffHeap);
        }
    }

    public Configuration maxBytesLocalOffHeap(long amount, MemoryUnit memoryUnit) {
        this.setMaxBytesLocalOffHeap(memoryUnit.toBytes(amount));
        return this;
    }

    public long getMaxBytesLocalDisk() {
        return this.maxBytesLocalDisk == null ? 0L : this.maxBytesLocalDisk;
    }

    public void setMaxBytesLocalDisk(String maxBytesOnDisk) {
        this.assertArgumentNotNull("MaxBytesLocalDisk", maxBytesOnDisk);
        String origInput = this.maxBytesLocalDiskInput;
        try {
            this.maxBytesLocalDiskInput = maxBytesOnDisk;
            this.setMaxBytesLocalDisk(MemoryUnit.parseSizeInBytes(maxBytesOnDisk));
        }
        catch (RuntimeException rte) {
            this.maxBytesLocalDiskInput = origInput;
            throw rte;
        }
    }

    public String getMaxBytesLocalDiskAsString() {
        return this.maxBytesLocalDiskInput != null ? this.maxBytesLocalDiskInput : Long.toString(this.getMaxBytesLocalDisk());
    }

    public void setMaxBytesLocalDisk(Long maxBytesOnDisk) {
        String prop = "maxBytesLocalDisk";
        this.verifyGreaterThanZero(maxBytesOnDisk, prop);
        boolean publish = this.checkDynChange(prop);
        Long oldValue = this.maxBytesLocalDisk;
        this.maxBytesLocalDisk = maxBytesOnDisk;
        if (publish) {
            this.firePropertyChange(prop, oldValue, maxBytesOnDisk);
        }
    }

    public Configuration maxBytesLocalDisk(long amount, MemoryUnit memoryUnit) {
        this.setMaxBytesLocalDisk(memoryUnit.toBytes(amount));
        return this;
    }

    private void verifyGreaterThanZero(Long maxBytesOnHeap, String field) {
        if (maxBytesOnHeap != null && maxBytesOnHeap < 1L) {
            throw new IllegalArgumentException(field + " has to be larger than 0");
        }
    }

    public final Configuration diskStore(DiskStoreConfiguration diskStoreConfigurationParameter) throws ObjectExistsException {
        this.addDiskStore(diskStoreConfigurationParameter);
        return this;
    }

    public final void addDiskStore(DiskStoreConfiguration diskStoreConfigurationParameter) throws ObjectExistsException {
        if (this.diskStoreConfiguration != null) {
            throw new ObjectExistsException("The Disk Store has already been configured");
        }
        String prop = "diskStoreConfiguration";
        boolean publish = this.checkDynChange("diskStoreConfiguration");
        DiskStoreConfiguration oldValue = this.diskStoreConfiguration;
        this.diskStoreConfiguration = diskStoreConfigurationParameter;
        if (publish) {
            this.firePropertyChange("diskStoreConfiguration", oldValue, this.diskStoreConfiguration);
        }
    }

    public final Configuration sizeOfPolicy(SizeOfPolicyConfiguration sizeOfPolicyConfiguration) {
        this.addSizeOfPolicy(sizeOfPolicyConfiguration);
        return this;
    }

    public final void addSizeOfPolicy(SizeOfPolicyConfiguration sizeOfPolicy) {
        if (this.sizeOfPolicyConfiguration != null) {
            throw new ObjectExistsException("The SizeOfPolicy class has already been configured");
        }
        this.sizeOfPolicyConfiguration = sizeOfPolicy;
    }

    public final Configuration transactionManagerLookup(FactoryConfiguration transactionManagerLookupParameter) throws ObjectExistsException {
        this.addTransactionManagerLookup(transactionManagerLookupParameter);
        return this;
    }

    public final void addTransactionManagerLookup(FactoryConfiguration transactionManagerLookupParameter) throws ObjectExistsException {
        if (this.transactionManagerLookupConfiguration != null) {
            throw new ObjectExistsException("The TransactionManagerLookup class has already been configured");
        }
        String prop = "transactionManagerLookupConfiguration";
        boolean publish = this.checkDynChange("transactionManagerLookupConfiguration");
        FactoryConfiguration oldValue = this.transactionManagerLookupConfiguration;
        this.transactionManagerLookupConfiguration = transactionManagerLookupParameter;
        if (publish) {
            this.firePropertyChange("transactionManagerLookupConfiguration", oldValue, transactionManagerLookupParameter);
        }
    }

    public final Configuration cacheManagerEventListenerFactory(FactoryConfiguration cacheManagerEventListenerFactoryConfiguration) {
        this.addCacheManagerEventListenerFactory(cacheManagerEventListenerFactoryConfiguration);
        return this;
    }

    public final void addCacheManagerEventListenerFactory(FactoryConfiguration cacheManagerEventListenerFactoryConfiguration) {
        String prop = "cacheManagerEventListenerFactoryConfiguration";
        boolean publish = this.checkDynChange("cacheManagerEventListenerFactoryConfiguration");
        if (this.cacheManagerEventListenerFactoryConfiguration == null) {
            this.cacheManagerEventListenerFactoryConfiguration = cacheManagerEventListenerFactoryConfiguration;
            if (publish) {
                this.firePropertyChange("cacheManagerEventListenerFactoryConfiguration", null, cacheManagerEventListenerFactoryConfiguration);
            }
        }
    }

    public final Configuration cacheManagerPeerProviderFactory(FactoryConfiguration factory) {
        this.addCacheManagerPeerProviderFactory(factory);
        return this;
    }

    public final void addCacheManagerPeerProviderFactory(FactoryConfiguration factory) {
        String prop = "cacheManagerPeerProviderFactoryConfiguration";
        boolean publish = this.checkDynChange("cacheManagerPeerProviderFactoryConfiguration");
        ArrayList<FactoryConfiguration> oldValue = null;
        if (publish) {
            oldValue = new ArrayList<FactoryConfiguration>(this.cacheManagerPeerProviderFactoryConfiguration);
        }
        this.cacheManagerPeerProviderFactoryConfiguration.add(factory);
        if (publish) {
            this.firePropertyChange("cacheManagerPeerProviderFactoryConfiguration", oldValue, this.cacheManagerPeerProviderFactoryConfiguration);
        }
    }

    public final Configuration cacheManagerPeerListenerFactory(FactoryConfiguration factory) {
        this.addCacheManagerPeerListenerFactory(factory);
        return this;
    }

    public final void addCacheManagerPeerListenerFactory(FactoryConfiguration factory) {
        String prop = "cacheManagerPeerListenerFactoryConfiguration";
        boolean publish = this.checkDynChange("cacheManagerPeerListenerFactoryConfiguration");
        ArrayList<FactoryConfiguration> oldValue = null;
        if (publish) {
            oldValue = new ArrayList<FactoryConfiguration>(this.cacheManagerPeerListenerFactoryConfiguration);
        }
        this.cacheManagerPeerListenerFactoryConfiguration.add(factory);
        if (publish) {
            this.firePropertyChange("cacheManagerPeerListenerFactoryConfiguration", oldValue, this.cacheManagerPeerListenerFactoryConfiguration);
        }
    }

    public final Configuration terracotta(TerracottaClientConfiguration terracottaConfiguration) throws ObjectExistsException {
        this.addTerracottaConfig(terracottaConfiguration);
        return this;
    }

    public final void addTerracottaConfig(TerracottaClientConfiguration terracottaConfiguration) throws ObjectExistsException {
        if (this.terracottaConfigConfiguration != null && terracottaConfiguration != null) {
            throw new ObjectExistsException("The TerracottaConfig has already been configured");
        }
        String prop = "terracottaConfigConfiguration";
        boolean publish = this.checkDynChange("terracottaConfigConfiguration");
        TerracottaClientConfiguration oldValue = this.terracottaConfigConfiguration;
        this.terracottaConfigConfiguration = terracottaConfiguration;
        if (publish) {
            this.firePropertyChange("terracottaConfigConfiguration", oldValue, terracottaConfiguration);
        }
    }

    public final Configuration managementRESTService(ManagementRESTServiceConfiguration cfg) throws ObjectExistsException {
        this.addManagementRESTService(cfg);
        return this;
    }

    public final void addManagementRESTService(ManagementRESTServiceConfiguration managementRESTServiceConfiguration) throws ObjectExistsException {
        if (this.managementRESTService != null) {
            throw new ObjectExistsException("The ManagementRESTService has already been configured");
        }
        String prop = "managementRESTService";
        boolean publish = this.checkDynChange("managementRESTService");
        ManagementRESTServiceConfiguration oldValue = this.managementRESTService;
        this.managementRESTService = managementRESTServiceConfiguration;
        if (publish) {
            this.firePropertyChange("managementRESTService", oldValue, managementRESTServiceConfiguration);
        }
    }

    public final Configuration defaultCache(CacheConfiguration defaultCacheConfiguration) throws ObjectExistsException {
        this.setDefaultCacheConfiguration(defaultCacheConfiguration);
        return this;
    }

    public final void addDefaultCache(CacheConfiguration defaultCacheConfiguration) throws ObjectExistsException {
        if (this.defaultCacheConfiguration != null) {
            throw new ObjectExistsException("The Default Cache has already been configured");
        }
        this.setDefaultCacheConfiguration(defaultCacheConfiguration);
    }

    public final Configuration cache(CacheConfiguration cacheConfiguration) throws ObjectExistsException {
        this.addCache(cacheConfiguration);
        return this;
    }

    public final void addCache(CacheConfiguration cacheConfiguration) throws ObjectExistsException {
        this.addCache(cacheConfiguration, true);
    }

    void addCache(CacheConfiguration cacheConfiguration, boolean strict) throws ObjectExistsException {
        boolean publishChange;
        String prop = "cacheConfigurations";
        HashMap<String, CacheConfiguration> oldValue = null;
        boolean bl = publishChange = strict && this.checkDynChange("cacheConfigurations");
        if (publishChange) {
            oldValue = new HashMap<String, CacheConfiguration>(this.cacheConfigurations);
        }
        if (this.cacheConfigurations.get(cacheConfiguration.name) != null) {
            throw new ObjectExistsException("Cannot create cache: " + cacheConfiguration.name + " with the same name as an existing one.");
        }
        if (cacheConfiguration.name.equalsIgnoreCase("default")) {
            throw new ObjectExistsException("The Default Cache has already been configured");
        }
        this.cacheConfigurations.put(cacheConfiguration.name, cacheConfiguration);
        if (publishChange) {
            this.firePropertyChange("cacheConfigurations", oldValue, this.cacheConfigurations);
        }
    }

    private boolean checkDynChange(String prop) {
        if (!this.propertyChangeListeners.isEmpty()) {
            try {
                if (this.cfg != null) {
                    DynamicProperty.valueOf(prop);
                }
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(this.getClass().getName() + "." + prop + " can't be changed dynamically");
            }
            return true;
        }
        return false;
    }

    public final Set<String> getCacheConfigurationsKeySet() {
        return this.cacheConfigurations.keySet();
    }

    public final CacheConfiguration getDefaultCacheConfiguration() {
        return this.defaultCacheConfiguration;
    }

    public final void setDefaultCacheConfiguration(CacheConfiguration defaultCacheConfiguration) {
        String prop = "defaultCacheConfiguration";
        boolean publish = this.checkDynChange("defaultCacheConfiguration");
        CacheConfiguration oldValue = this.defaultCacheConfiguration;
        this.defaultCacheConfiguration = defaultCacheConfiguration;
        if (publish) {
            this.firePropertyChange("defaultCacheConfiguration", oldValue, defaultCacheConfiguration);
        }
    }

    public final DiskStoreConfiguration getDiskStoreConfiguration() {
        return this.diskStoreConfiguration;
    }

    public final SizeOfPolicyConfiguration getSizeOfPolicyConfiguration() {
        if (this.sizeOfPolicyConfiguration == null) {
            return DEFAULT_SIZEOF_POLICY_CONFIGURATION;
        }
        return this.sizeOfPolicyConfiguration;
    }

    public final FactoryConfiguration getTransactionManagerLookupConfiguration() {
        if (this.transactionManagerLookupConfiguration == null) {
            return Configuration.getDefaultTransactionManagerLookupConfiguration();
        }
        return this.transactionManagerLookupConfiguration;
    }

    public final List<FactoryConfiguration> getCacheManagerPeerProviderFactoryConfiguration() {
        return this.cacheManagerPeerProviderFactoryConfiguration;
    }

    public final List<FactoryConfiguration> getCacheManagerPeerListenerFactoryConfigurations() {
        return this.cacheManagerPeerListenerFactoryConfiguration;
    }

    public final ManagementRESTServiceConfiguration getManagementRESTService() {
        return this.managementRESTService;
    }

    public final FactoryConfiguration getCacheManagerEventListenerFactoryConfiguration() {
        return this.cacheManagerEventListenerFactoryConfiguration;
    }

    public final TerracottaClientConfiguration getTerracottaConfiguration() {
        return this.terracottaConfigConfiguration;
    }

    public final Map<String, CacheConfiguration> getCacheConfigurations() {
        return this.cacheConfigurations;
    }

    public final Configuration source(ConfigurationSource configurationSource) {
        this.setSource(configurationSource);
        return this;
    }

    public final void setSource(ConfigurationSource configurationSource) {
        String prop = "configurationSource";
        boolean publish = this.checkDynChange("configurationSource");
        ConfigurationSource oldValue = this.configurationSource;
        this.configurationSource = configurationSource;
        if (publish) {
            this.firePropertyChange("configurationSource", oldValue, configurationSource);
        }
    }

    public final ConfigurationSource getConfigurationSource() {
        return this.configurationSource;
    }

    public boolean addPropertyChangeListener(PropertyChangeListener listener) {
        return this.propertyChangeListeners.add(listener);
    }

    public boolean removePropertyChangeListener(PropertyChangeListener listener) {
        return this.propertyChangeListeners.remove(listener);
    }

    private <T> void firePropertyChange(String prop, T oldValue, T newValue) {
        if (oldValue != null && !oldValue.equals(newValue) || newValue != null) {
            for (PropertyChangeListener propertyChangeListener : this.propertyChangeListeners) {
                propertyChangeListener.propertyChange(new PropertyChangeEvent(this, prop, oldValue, newValue));
            }
        }
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader loader) {
        this.classLoader = loader;
    }

    public void cleanup() {
        this.propertyChangeListeners.remove(this.cfg);
        this.cfg = null;
    }

    public class RuntimeCfg
    implements PropertyChangeListener {
        private final CacheManager cacheManager;
        private volatile String cacheManagerName;
        private final boolean named;
        private TransactionManagerLookup transactionManagerLookup;
        private boolean allowsSizeBasedTunings;

        public RuntimeCfg(CacheManager cacheManager, String fallbackName) {
            if (Configuration.this.cacheManagerName != null) {
                this.cacheManagerName = Configuration.this.cacheManagerName;
                this.named = true;
            } else if (this.hasTerracottaClusteredCaches()) {
                this.cacheManagerName = "__DEFAULT__";
                this.named = false;
            } else {
                this.cacheManagerName = fallbackName;
                this.named = false;
            }
            FactoryConfiguration lookupConfiguration = Configuration.this.getTransactionManagerLookupConfiguration();
            try {
                Properties properties = PropertyUtil.parseProperties(lookupConfiguration.getProperties(), lookupConfiguration.getPropertySeparator());
                ClassLoader loader = Configuration.this.getClassLoader();
                if (DEFAULT_TRANSACTION_MANAGER_LOOKUP_CONFIG.getFullyQualifiedClassPath().equals(lookupConfiguration.getFullyQualifiedClassPath())) {
                    loader = this.getClass().getClassLoader();
                }
                Class<?> transactionManagerLookupClass = loader.loadClass(lookupConfiguration.getFullyQualifiedClassPath());
                this.transactionManagerLookup = (TransactionManagerLookup)transactionManagerLookupClass.newInstance();
                this.transactionManagerLookup.setProperties(properties);
            }
            catch (Exception e) {
                LOG.error("could not instantiate transaction manager lookup class: {}", (Object)lookupConfiguration.getFullyQualifiedClassPath(), (Object)e);
            }
            this.cacheManager = cacheManager;
            Configuration.this.propertyChangeListeners.add(this);
            this.allowsSizeBasedTunings = Configuration.this.defaultCacheConfiguration == null || !Configuration.this.defaultCacheConfiguration.isCountBasedTuned();
            for (CacheConfiguration cacheConfiguration : Configuration.this.cacheConfigurations.values()) {
                if (!cacheConfiguration.isCountBasedTuned()) continue;
                this.allowsSizeBasedTunings = false;
                break;
            }
        }

        public String getCacheManagerName() {
            return this.cacheManagerName;
        }

        public boolean allowsDynamicCacheConfig() {
            return Configuration.this.getDynamicConfig();
        }

        public boolean isNamed() {
            return this.named;
        }

        public Configuration getConfiguration() {
            return Configuration.this;
        }

        public boolean isTerracottaRejoin() {
            TerracottaClientConfiguration terracottaConfiguration = Configuration.this.getTerracottaConfiguration();
            return terracottaConfiguration != null && terracottaConfiguration.isRejoin();
        }

        private boolean hasTerracottaClusteredCaches() {
            if (Configuration.this.defaultCacheConfiguration != null && Configuration.this.defaultCacheConfiguration.isTerracottaClustered()) {
                return true;
            }
            for (CacheConfiguration config : Configuration.this.cacheConfigurations.values()) {
                if (!config.isTerracottaClustered()) continue;
                return true;
            }
            return false;
        }

        public TransactionManagerLookup getTransactionManagerLookup() {
            return this.transactionManagerLookup;
        }

        public void removeCache(CacheConfiguration cacheConfiguration) {
            if (this.cacheManager.getOnHeapPool() != null) {
                this.cacheManager.getOnHeapPool().setMaxSize(this.cacheManager.getOnHeapPool().getMaxSize() + cacheConfiguration.getMaxBytesLocalHeap());
            }
            if (this.cacheManager.getOnDiskPool() != null) {
                this.cacheManager.getOnDiskPool().setMaxSize(this.cacheManager.getOnDiskPool().getMaxSize() + cacheConfiguration.getMaxBytesLocalDisk());
            }
            this.getConfiguration().getCacheConfigurations().remove(cacheConfiguration.getName());
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            try {
                DynamicProperty.valueOf(evt.getPropertyName()).applyChange(evt, this);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(evt.getPropertyName() + " can't be changed dynamically");
            }
        }

        public boolean hasOffHeapPool() {
            return Configuration.this.isMaxBytesLocalOffHeapSet();
        }
    }

    private static enum DynamicProperty {
        defaultCacheConfiguration{

            @Override
            void applyChange(PropertyChangeEvent evt, RuntimeCfg config) {
                LOG.debug("Default Cache Configuration has changed, previously created caches remain untouched");
            }
        }
        ,
        maxBytesLocalHeap{

            @Override
            void applyChange(PropertyChangeEvent evt, RuntimeCfg config) {
                Long newValue = (Long)evt.getNewValue();
                Long oldValue = (Long)evt.getOldValue();
                if (oldValue > newValue) {
                    DynamicProperty.validateOverAllocation(config, newValue);
                }
                long cacheAllocated = 0L;
                for (Cache cache : Configuration.getAllActiveCaches(config.cacheManager)) {
                    cache.getCacheConfiguration().configCachePools(config.getConfiguration());
                    long bytesLocalHeap = cache.getCacheConfiguration().getMaxBytesLocalHeap();
                    cacheAllocated += bytesLocalHeap;
                }
                config.cacheManager.getOnHeapPool().setMaxSize(newValue - cacheAllocated);
            }
        }
        ,
        maxBytesLocalDisk{

            @Override
            void applyChange(PropertyChangeEvent evt, RuntimeCfg config) {
                Long newValue = (Long)evt.getNewValue();
                Long oldValue = (Long)evt.getOldValue();
                if (oldValue > newValue) {
                    DynamicProperty.validateOverAllocation(config, newValue);
                }
                long diskAllocated = 0L;
                for (Cache cache : Configuration.getAllActiveCaches(config.cacheManager)) {
                    cache.getCacheConfiguration().configCachePools(config.getConfiguration());
                    long bytesOnDiskPool = cache.getCacheConfiguration().getMaxBytesLocalDisk();
                    diskAllocated += bytesOnDiskPool;
                }
                config.cacheManager.getOnDiskPool().setMaxSize(newValue - diskAllocated);
            }
        };


        private static void validateOverAllocation(RuntimeCfg config, Long newValue) {
            ArrayList<ConfigError> errors = new ArrayList<ConfigError>();
            for (Cache cache : Configuration.getAllActiveCaches(config.cacheManager)) {
                CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();
                errors.addAll(cacheConfiguration.validateCachePools(config.getConfiguration()));
                errors.addAll(cacheConfiguration.verifyPoolAllocationsBeforeAddingTo(config.cacheManager, newValue, config.getConfiguration().getMaxBytesLocalOffHeap(), config.getConfiguration().getMaxBytesLocalDisk(), null));
            }
            if (!errors.isEmpty()) {
                throw new InvalidConfigurationException("Can't reduce CacheManager byte tuning by so much", errors);
            }
        }

        abstract void applyChange(PropertyChangeEvent var1, RuntimeCfg var2);
    }

    public static enum Monitoring {
        AUTODETECT,
        ON,
        OFF;

    }
}

