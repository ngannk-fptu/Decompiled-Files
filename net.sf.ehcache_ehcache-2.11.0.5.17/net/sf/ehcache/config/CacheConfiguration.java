/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.EhcacheDefaultClassLoader;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.AbstractCacheConfigurationListener;
import net.sf.ehcache.config.CacheConfigError;
import net.sf.ehcache.config.CacheConfigurationListener;
import net.sf.ehcache.config.CacheWriterConfiguration;
import net.sf.ehcache.config.ConfigError;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.CopyStrategyConfiguration;
import net.sf.ehcache.config.DynamicSearchListener;
import net.sf.ehcache.config.ElementValueComparatorConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.event.NotificationScope;
import net.sf.ehcache.search.attribute.DynamicAttributesExtractor;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheConfiguration
implements Cloneable {
    public static final boolean DEFAULT_CLEAR_ON_FLUSH = true;
    public static final long DEFAULT_EXPIRY_THREAD_INTERVAL_SECONDS = 120L;
    public static final int DEFAULT_SPOOL_BUFFER_SIZE = 30;
    public static final int DEFAULT_DISK_ACCESS_STRIPES = 1;
    public static final boolean DEFAULT_LOGGING = false;
    public static final MemoryStoreEvictionPolicy DEFAULT_MEMORY_STORE_EVICTION_POLICY = MemoryStoreEvictionPolicy.LRU;
    public static final CacheWriterConfiguration DEFAULT_CACHE_WRITER_CONFIGURATION = new CacheWriterConfiguration();
    public static final boolean DEFAULT_COPY_ON_READ = false;
    public static final boolean DEFAULT_COPY_ON_WRITE = false;
    public static final long DEFAULT_TTL = 0L;
    public static final long DEFAULT_TTI = 0L;
    public static final int DEFAULT_MAX_ELEMENTS_ON_DISK = 0;
    public static final long DEFAULT_MAX_ENTRIES_IN_CACHE = 0L;
    public static final TransactionalMode DEFAULT_TRANSACTIONAL_MODE = TransactionalMode.OFF;
    public static final boolean DEFAULT_STATISTICS = true;
    @Deprecated
    public static final boolean DEFAULT_DISK_PERSISTENT = false;
    public static final CopyStrategyConfiguration DEFAULT_COPY_STRATEGY_CONFIGURATION = new CopyStrategyConfiguration();
    @Deprecated
    public static final long DEFAULT_MAX_BYTES_ON_HEAP = 0L;
    public static final long DEFAULT_MAX_BYTES_OFF_HEAP = 0L;
    public static final long DEFAULT_MAX_BYTES_ON_DISK = 0L;
    public static final boolean DEFAULT_ETERNAL_VALUE = false;
    private static final Logger LOG = LoggerFactory.getLogger((String)CacheConfiguration.class.getName());
    private static final int HUNDRED_PERCENT = 100;
    private static final int MINIMUM_RECOMMENDED_IN_MEMORY = 100;
    protected volatile String name;
    protected volatile long cacheLoaderTimeoutMillis;
    protected volatile Integer maxEntriesLocalHeap;
    protected volatile int maxElementsOnDisk = 0;
    protected volatile long maxEntriesInCache = 0L;
    protected volatile MemoryStoreEvictionPolicy memoryStoreEvictionPolicy = DEFAULT_MEMORY_STORE_EVICTION_POLICY;
    protected volatile boolean clearOnFlush = true;
    protected volatile boolean eternal = false;
    protected volatile long timeToIdleSeconds = 0L;
    protected volatile long timeToLiveSeconds = 0L;
    @Deprecated
    protected volatile Boolean overflowToDisk;
    @Deprecated
    protected volatile Boolean diskPersistent;
    protected volatile int diskSpoolBufferSizeMB = 30;
    protected volatile int diskAccessStripes = 1;
    protected volatile long diskExpiryThreadIntervalSeconds = 120L;
    protected volatile boolean logging = false;
    protected volatile Boolean overflowToOffHeap;
    protected volatile List<CacheEventListenerFactoryConfiguration> cacheEventListenerConfigurations = new ArrayList<CacheEventListenerFactoryConfiguration>();
    protected volatile List<CacheExtensionFactoryConfiguration> cacheExtensionConfigurations = new ArrayList<CacheExtensionFactoryConfiguration>();
    protected BootstrapCacheLoaderFactoryConfiguration bootstrapCacheLoaderFactoryConfiguration;
    protected CacheExceptionHandlerFactoryConfiguration cacheExceptionHandlerFactoryConfiguration;
    protected TerracottaConfiguration terracottaConfiguration;
    protected volatile PinningConfiguration pinningConfiguration;
    protected CacheWriterConfiguration cacheWriterConfiguration = DEFAULT_CACHE_WRITER_CONFIGURATION;
    protected volatile List<CacheLoaderFactoryConfiguration> cacheLoaderConfigurations = new ArrayList<CacheLoaderFactoryConfiguration>();
    protected volatile List<CacheDecoratorFactoryConfiguration> cacheDecoratorConfigurations = new ArrayList<CacheDecoratorFactoryConfiguration>();
    protected volatile Set<CacheConfigurationListener> listeners = new CopyOnWriteArraySet<CacheConfigurationListener>();
    private volatile Set<DynamicSearchListener> dynamicSearchListeners = new CopyOnWriteArraySet<DynamicSearchListener>();
    private DynamicAttributesExtractor flexIndexer;
    private volatile boolean frozen;
    private volatile TransactionalMode transactionalMode;
    private volatile boolean statistics = true;
    private volatile CopyStrategyConfiguration copyStrategyConfiguration = DEFAULT_COPY_STRATEGY_CONFIGURATION.copy();
    private volatile SizeOfPolicyConfiguration sizeOfPolicyConfiguration;
    private volatile PersistenceConfiguration persistenceConfiguration;
    private volatile ElementValueComparatorConfiguration elementValueComparatorConfiguration = new ElementValueComparatorConfiguration();
    private volatile Boolean copyOnRead;
    private volatile Boolean copyOnWrite;
    private volatile boolean conflictingEternalValuesWarningLogged;
    private volatile Searchable searchable;
    private String maxBytesLocalHeapInput;
    private String maxBytesLocalOffHeapInput;
    private String maxBytesLocalDiskInput;
    private Long maxBytesLocalHeap;
    private Long maxBytesLocalOffHeap;
    private Long maxBytesLocalDisk;
    private Integer maxBytesLocalHeapPercentage;
    private Integer maxBytesLocalOffHeapPercentage;
    private Integer maxBytesLocalDiskPercentage;
    private PoolUsage onHeapPoolUsage;
    private PoolUsage offHeapPoolUsage;
    private PoolUsage onDiskPoolUsage;
    private volatile boolean maxEntriesLocalDiskExplicitlySet;
    private volatile boolean maxBytesLocalDiskExplicitlySet;
    private volatile boolean maxBytesLocalOffHeapExplicitlySet;
    private volatile ClassLoader classLoader = EhcacheDefaultClassLoader.getInstance();

    public CacheConfiguration() {
    }

    public CacheConfiguration(String name, int maxEntriesLocalHeap) {
        this.name = name;
        this.verifyGreaterThanOrEqualToZero(Long.valueOf(maxEntriesLocalHeap), "maxEntriesLocalHeap");
        this.maxEntriesLocalHeap = maxEntriesLocalHeap;
    }

    public CacheConfiguration clone() {
        CacheConfiguration config;
        try {
            config = (CacheConfiguration)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        this.cloneCacheEventListenerConfigurations(config);
        this.cloneCacheExtensionConfigurations(config);
        if (this.bootstrapCacheLoaderFactoryConfiguration != null) {
            config.bootstrapCacheLoaderFactoryConfiguration = (BootstrapCacheLoaderFactoryConfiguration)this.bootstrapCacheLoaderFactoryConfiguration.clone();
        }
        if (this.cacheExceptionHandlerFactoryConfiguration != null) {
            config.cacheExceptionHandlerFactoryConfiguration = (CacheExceptionHandlerFactoryConfiguration)this.cacheExceptionHandlerFactoryConfiguration.clone();
        }
        if (this.terracottaConfiguration != null) {
            config.terracottaConfiguration = this.terracottaConfiguration.clone();
        }
        if (this.cacheWriterConfiguration != null) {
            config.cacheWriterConfiguration = this.cacheWriterConfiguration.clone();
        }
        this.cloneCacheLoaderConfigurations(config);
        this.cloneCacheDecoratorConfigurations(config);
        config.listeners = new CopyOnWriteArraySet<CacheConfigurationListener>();
        config.dynamicSearchListeners = new CopyOnWriteArraySet<DynamicSearchListener>();
        return config;
    }

    private void cloneCacheEventListenerConfigurations(CacheConfiguration config) {
        ArrayList<CacheEventListenerFactoryConfiguration> copy = new ArrayList<CacheEventListenerFactoryConfiguration>();
        for (CacheEventListenerFactoryConfiguration item : this.cacheEventListenerConfigurations) {
            copy.add((CacheEventListenerFactoryConfiguration)item.clone());
        }
        config.cacheEventListenerConfigurations = copy;
    }

    private void cloneCacheExtensionConfigurations(CacheConfiguration config) {
        ArrayList<CacheExtensionFactoryConfiguration> copy = new ArrayList<CacheExtensionFactoryConfiguration>();
        for (CacheExtensionFactoryConfiguration item : this.cacheExtensionConfigurations) {
            copy.add((CacheExtensionFactoryConfiguration)item.clone());
        }
        config.cacheExtensionConfigurations = copy;
    }

    private void cloneCacheLoaderConfigurations(CacheConfiguration config) {
        ArrayList<CacheLoaderFactoryConfiguration> copy = new ArrayList<CacheLoaderFactoryConfiguration>();
        for (CacheLoaderFactoryConfiguration item : this.cacheLoaderConfigurations) {
            copy.add((CacheLoaderFactoryConfiguration)item.clone());
        }
        config.cacheLoaderConfigurations = copy;
    }

    private void cloneCacheDecoratorConfigurations(CacheConfiguration config) {
        ArrayList<CacheDecoratorFactoryConfiguration> copy = new ArrayList<CacheDecoratorFactoryConfiguration>();
        for (CacheDecoratorFactoryConfiguration item : this.cacheDecoratorConfigurations) {
            copy.add((CacheDecoratorFactoryConfiguration)item.clone());
        }
        config.cacheDecoratorConfigurations = copy;
    }

    private void assertArgumentNotNull(String name, Object object) {
        if (object == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }

    public final void setName(String name) {
        this.checkDynamicChange();
        this.assertArgumentNotNull("Cache name", name);
        this.name = name;
    }

    public final CacheConfiguration name(String name) {
        this.setName(name);
        return this;
    }

    public final void setLogging(boolean enable) {
        this.checkDynamicChange();
        boolean oldLoggingEnabled = this.logging;
        this.logging = enable;
        this.fireLoggingChanged(oldLoggingEnabled, enable);
    }

    public final void setOverflowToOffHeap(boolean overflowToOffHeap) {
        this.checkDynamicChange();
        this.overflowToOffHeap = overflowToOffHeap;
    }

    public CacheConfiguration overflowToOffHeap(boolean overflowToOffHeap) {
        this.setOverflowToOffHeap(overflowToOffHeap);
        return this;
    }

    public void addSizeOfPolicy(SizeOfPolicyConfiguration sizeOfPolicyConfiguration) {
        this.sizeOfPolicyConfiguration = sizeOfPolicyConfiguration;
    }

    public CacheConfiguration sizeOfPolicy(SizeOfPolicyConfiguration sizeOfPolicyConfiguration) {
        this.addSizeOfPolicy(sizeOfPolicyConfiguration);
        return this;
    }

    public void addPersistence(PersistenceConfiguration persistenceConfiguration) {
        if (this.diskPersistent != null) {
            throw new InvalidConfigurationException("Cannot use both <persistence ...> and diskPersistent in a single cache configuration.");
        }
        if (Boolean.TRUE.equals(this.overflowToDisk)) {
            throw new InvalidConfigurationException("Cannot use both <persistence ...> and overflowToDisk in a single cache configuration.");
        }
        this.persistenceConfiguration = persistenceConfiguration;
    }

    public CacheConfiguration persistence(PersistenceConfiguration persistenceConfiguration) {
        this.addPersistence(persistenceConfiguration);
        return this;
    }

    @Deprecated
    public final void setMaxMemoryOffHeap(String maxMemoryOffHeap) {
        this.checkDynamicChange();
        this.assertArgumentNotNull("Cache maxMemoryOffHeap", maxMemoryOffHeap);
        this.setMaxBytesLocalOffHeap(maxMemoryOffHeap);
    }

    @Deprecated
    public CacheConfiguration maxMemoryOffHeap(String maxMemoryOffHeap) {
        this.setMaxMemoryOffHeap(maxMemoryOffHeap);
        return this;
    }

    public final CacheConfiguration logging(boolean enable) {
        this.setLogging(enable);
        return this;
    }

    @Deprecated
    public final void setMaxElementsInMemory(int maxElementsInMemory) {
        this.setMaxEntriesLocalHeap(maxElementsInMemory);
    }

    public final void setMaxEntriesLocalHeap(long maxEntriesLocalHeap) {
        this.verifyGreaterThanOrEqualToZero(maxEntriesLocalHeap, "maxEntriesLocalHeap");
        if (maxEntriesLocalHeap > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Values larger than Integer.MAX_VALUE are not currently supported.");
        }
        this.checkDynamicChange();
        if (this.onHeapPoolUsage != null && this.onHeapPoolUsage != PoolUsage.None) {
            throw new InvalidConfigurationException("MaxEntriesLocalHeap is not compatible with MaxBytesLocalHeap set on cache");
        }
        int oldCapacity = this.maxEntriesLocalHeap == null ? 0 : this.maxEntriesLocalHeap;
        int newCapacity = (int)maxEntriesLocalHeap;
        this.maxEntriesLocalHeap = (int)maxEntriesLocalHeap;
        this.fireMemoryCapacityChanged(oldCapacity, newCapacity);
    }

    @Deprecated
    public final CacheConfiguration maxElementsInMemory(int maxElementsInMemory) {
        this.setMaxElementsInMemory(maxElementsInMemory);
        return this;
    }

    public final CacheConfiguration maxEntriesLocalHeap(int maxElementsInMemory) {
        this.setMaxEntriesLocalHeap(maxElementsInMemory);
        return this;
    }

    public final void setCacheLoaderTimeoutMillis(long cacheLoaderTimeoutMillis) {
        this.checkDynamicChange();
        this.cacheLoaderTimeoutMillis = cacheLoaderTimeoutMillis;
    }

    public CacheConfiguration timeoutMillis(long timeoutMillis) {
        this.setCacheLoaderTimeoutMillis(timeoutMillis);
        return this;
    }

    public final void setMemoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
        this.assertArgumentNotNull("Cache memoryStoreEvictionPolicy", memoryStoreEvictionPolicy);
        this.setMemoryStoreEvictionPolicyFromObject(MemoryStoreEvictionPolicy.fromString(memoryStoreEvictionPolicy));
    }

    public final CacheConfiguration memoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
        this.setMemoryStoreEvictionPolicy(memoryStoreEvictionPolicy);
        return this;
    }

    public final void setMemoryStoreEvictionPolicyFromObject(MemoryStoreEvictionPolicy memoryStoreEvictionPolicy) {
        this.checkDynamicChange();
        this.memoryStoreEvictionPolicy = null == memoryStoreEvictionPolicy ? DEFAULT_MEMORY_STORE_EVICTION_POLICY : memoryStoreEvictionPolicy;
    }

    public final CacheConfiguration memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy memoryStoreEvictionPolicy) {
        this.setMemoryStoreEvictionPolicyFromObject(memoryStoreEvictionPolicy);
        return this;
    }

    public final void setClearOnFlush(boolean clearOnFlush) {
        this.checkDynamicChange();
        this.clearOnFlush = clearOnFlush;
    }

    public final CacheConfiguration clearOnFlush(boolean clearOnFlush) {
        this.setClearOnFlush(clearOnFlush);
        return this;
    }

    public final void setEternal(boolean eternal) {
        this.checkDynamicChange();
        this.isEternalValueConflictingWithTTIOrTTL(eternal, this.getTimeToLiveSeconds(), this.getTimeToIdleSeconds());
        this.eternal = eternal;
        if (eternal) {
            this.setTimeToIdleSeconds(0L);
            this.setTimeToLiveSeconds(0L);
        }
    }

    private boolean isEternalValueConflictingWithTTIOrTTL(boolean newEternalValue, long newTTLValue, long newTTIValue) {
        boolean conflicting = false;
        if (newEternalValue && (newTTLValue != 0L || newTTIValue != 0L)) {
            conflicting = true;
        }
        if (conflicting && !this.conflictingEternalValuesWarningLogged) {
            this.conflictingEternalValuesWarningLogged = true;
            LOG.warn("Cache '" + this.getName() + "' is set to eternal but also has TTI/TTL set.  To avoid this warning, clean up the config removing conflicting values of eternal, TTI and TTL. Effective configuration for Cache '" + this.getName() + "' will be eternal='" + newEternalValue + "', timeToIdleSeconds='0', timeToLiveSeconds='0'.");
        }
        return conflicting;
    }

    public final CacheConfiguration eternal(boolean eternal) {
        this.setEternal(eternal);
        return this;
    }

    public final void setTimeToIdleSeconds(long timeToIdleSeconds) {
        this.checkDynamicChange();
        this.verifyGreaterThanOrEqualToZero(timeToIdleSeconds, "timeToIdleSeconds");
        if (!this.isEternalValueConflictingWithTTIOrTTL(this.eternal, 0L, timeToIdleSeconds)) {
            long oldTti = this.timeToIdleSeconds;
            long newTti = timeToIdleSeconds;
            this.timeToIdleSeconds = timeToIdleSeconds;
            this.fireTtiChanged(oldTti, newTti);
        }
    }

    public final CacheConfiguration timeToIdleSeconds(long timeToIdleSeconds) {
        this.setTimeToIdleSeconds(timeToIdleSeconds);
        return this;
    }

    public final void setTimeToLiveSeconds(long timeToLiveSeconds) {
        this.checkDynamicChange();
        this.verifyGreaterThanOrEqualToZero(timeToLiveSeconds, "timeToLiveSeconds");
        if (!this.isEternalValueConflictingWithTTIOrTTL(this.eternal, timeToLiveSeconds, 0L)) {
            long oldTtl = this.timeToLiveSeconds;
            long newTtl = timeToLiveSeconds;
            this.timeToLiveSeconds = timeToLiveSeconds;
            this.fireTtlChanged(oldTtl, newTtl);
        }
    }

    public final CacheConfiguration timeToLiveSeconds(long timeToLiveSeconds) {
        this.setTimeToLiveSeconds(timeToLiveSeconds);
        return this;
    }

    @Deprecated
    public final void setOverflowToDisk(boolean overflowToDisk) {
        this.checkDynamicChange();
        if (this.persistenceConfiguration != null && Boolean.TRUE.equals(overflowToDisk)) {
            throw new InvalidConfigurationException("Cannot use both <persistence ...> and overflowToDisk in a single cache configuration.");
        }
        this.overflowToDisk = overflowToDisk;
        this.validateConfiguration();
    }

    @Deprecated
    public final CacheConfiguration overflowToDisk(boolean overflowToDisk) {
        this.setOverflowToDisk(overflowToDisk);
        return this;
    }

    @Deprecated
    public final void setDiskPersistent(boolean diskPersistent) {
        this.checkDynamicChange();
        if (this.persistenceConfiguration != null) {
            throw new InvalidConfigurationException("Cannot use both <persistence ...> and diskPersistent in a single cache configuration.");
        }
        this.diskPersistent = diskPersistent;
        this.validateConfiguration();
    }

    @Deprecated
    public final CacheConfiguration diskPersistent(boolean diskPersistent) {
        this.setDiskPersistent(diskPersistent);
        return this;
    }

    public void setDiskSpoolBufferSizeMB(int diskSpoolBufferSizeMB) {
        this.checkDynamicChange();
        this.diskSpoolBufferSizeMB = diskSpoolBufferSizeMB <= 0 ? 30 : diskSpoolBufferSizeMB;
    }

    public final CacheConfiguration diskSpoolBufferSizeMB(int diskSpoolBufferSizeMB) {
        this.setDiskSpoolBufferSizeMB(diskSpoolBufferSizeMB);
        return this;
    }

    public void setDiskAccessStripes(int stripes) {
        this.checkDynamicChange();
        this.diskAccessStripes = stripes <= 0 ? 1 : stripes;
    }

    public final CacheConfiguration diskAccessStripes(int stripes) {
        this.setDiskAccessStripes(stripes);
        return this;
    }

    public void setMaxElementsOnDisk(int maxElementsOnDisk) {
        if (this.onDiskPoolUsage != null && this.onDiskPoolUsage != PoolUsage.None) {
            throw new InvalidConfigurationException("MaxEntriesLocalDisk is not compatible with MaxBytesLocalDisk set on cache");
        }
        this.verifyGreaterThanOrEqualToZero(Long.valueOf(maxElementsOnDisk), "maxElementsOnDisk");
        this.checkDynamicChange();
        int oldCapacity = this.maxElementsOnDisk;
        this.maxElementsOnDisk = maxElementsOnDisk;
        this.fireDiskCapacityChanged(oldCapacity, this.maxElementsOnDisk);
    }

    public void setMaxEntriesInCache(long maxEntriesInCache) {
        this.checkDynamicChange();
        this.verifyGreaterThanOrEqualToZero(maxEntriesInCache, "maxEntriesInCache");
        this.checkIfCachePinned(maxEntriesInCache);
        long oldValue = this.maxEntriesInCache;
        this.maxEntriesInCache = maxEntriesInCache;
        this.fireMaxEntriesInCacheChanged(oldValue, this.maxEntriesInCache);
    }

    private void checkIfCachePinned(long maxEntriesInCache) {
        if (maxEntriesInCache != 0L && this.getPinningConfiguration() != null && PinningConfiguration.Store.INCACHE.equals((Object)this.getPinningConfiguration().getStore())) {
            throw new InvalidConfigurationException("Setting maxEntriesInCache on an in-cache pinned cache is not legal");
        }
    }

    public void setMaxEntriesLocalDisk(long maxEntriesLocalDisk) {
        if (this.isTerracottaClustered()) {
            throw new InvalidConfigurationException("MaxEntriesLocalDisk is not applicable for Terracotta clustered caches");
        }
        this.verifyGreaterThanOrEqualToZero(maxEntriesLocalDisk, "maxEntriesLocalDisk");
        if (maxEntriesLocalDisk > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Values greater than Integer.MAX_VALUE are not currently supported.");
        }
        if (this.onDiskPoolUsage != null && this.isTerracottaClustered()) {
            throw new IllegalStateException("Can't use local disks with Terracotta clustered caches!");
        }
        this.maxEntriesLocalDiskExplicitlySet = true;
        this.setMaxElementsOnDisk((int)maxEntriesLocalDisk);
    }

    public final CacheConfiguration maxElementsOnDisk(int maxElementsOnDisk) {
        this.setMaxElementsOnDisk(maxElementsOnDisk);
        return this;
    }

    public final CacheConfiguration maxEntriesInCache(long maxEntriesInCache) {
        this.setMaxEntriesInCache(maxEntriesInCache);
        return this;
    }

    public final CacheConfiguration maxEntriesLocalDisk(int maxElementsOnDisk) {
        this.setMaxEntriesLocalDisk(maxElementsOnDisk);
        return this;
    }

    public final void setDiskExpiryThreadIntervalSeconds(long diskExpiryThreadIntervalSeconds) {
        this.checkDynamicChange();
        this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds <= 0L ? 120L : diskExpiryThreadIntervalSeconds;
    }

    public final CacheConfiguration diskExpiryThreadIntervalSeconds(long diskExpiryThreadIntervalSeconds) {
        this.setDiskExpiryThreadIntervalSeconds(diskExpiryThreadIntervalSeconds);
        return this;
    }

    public void freezeConfiguration() {
        this.frozen = true;
        if (this.searchable != null) {
            this.searchable.freezeConfiguration();
        }
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    public ReadWriteCopyStrategy<Element> getCopyStrategy() {
        return this.copyStrategyConfiguration.getCopyStrategyInstance(this.getClassLoader());
    }

    public CacheConfiguration copyOnRead(boolean copyOnRead) {
        this.setCopyOnRead(copyOnRead);
        return this;
    }

    public boolean isCopyOnRead() {
        this.validateTransactionalSettings();
        return this.copyOnRead;
    }

    public void setCopyOnRead(boolean copyOnRead) {
        this.copyOnRead = copyOnRead;
    }

    public CacheConfiguration copyOnWrite(boolean copyOnWrite) {
        this.copyOnWrite = copyOnWrite;
        return this;
    }

    public boolean isCopyOnWrite() {
        this.validateTransactionalSettings();
        return this.copyOnWrite;
    }

    public void setCopyOnWrite(boolean copyOnWrite) {
        this.copyOnWrite = copyOnWrite;
    }

    public void addCopyStrategy(CopyStrategyConfiguration copyStrategyConfiguration) {
        this.copyStrategyConfiguration = copyStrategyConfiguration;
    }

    public void addElementValueComparator(ElementValueComparatorConfiguration elementValueComparatorConfiguration) {
        this.elementValueComparatorConfiguration = elementValueComparatorConfiguration;
    }

    public final void addSearchable(Searchable searchable) {
        this.checkDynamicChange();
        this.searchable = searchable;
    }

    @Deprecated
    public long getMaxBytesLocalHeap() {
        return this.maxBytesLocalHeap == null ? 0L : this.maxBytesLocalHeap;
    }

    @Deprecated
    public void setMaxBytesLocalHeap(String maxBytesHeap) {
        this.assertArgumentNotNull("Cache maxBytesLocalHeap", maxBytesHeap);
        if (this.isPercentage(maxBytesHeap)) {
            this.maxBytesLocalHeapPercentage = this.parsePercentage(maxBytesHeap);
        } else {
            this.setMaxBytesLocalHeap(MemoryUnit.parseSizeInBytes(maxBytesHeap));
        }
        this.maxBytesLocalHeapInput = maxBytesHeap;
    }

    @Deprecated
    public void setMaxBytesLocalHeap(Long maxBytesHeap) {
        if (this.onHeapPoolUsage != null && this.getMaxEntriesLocalHeap() > 0L) {
            throw new InvalidConfigurationException("MaxEntriesLocalHeap is not compatible with MaxBytesLocalHeap set on cache");
        }
        if (this.onHeapPoolUsage != null && this.onHeapPoolUsage != PoolUsage.Cache) {
            throw new IllegalStateException("A Cache can't switch memory pool!");
        }
        this.checkDynamicChange();
        this.verifyGreaterThanZero(maxBytesHeap, "maxBytesLocalHeap");
        Long oldValue = this.maxBytesLocalHeap;
        this.maxBytesLocalHeap = maxBytesHeap;
        this.fireMaxBytesOnLocalHeapChanged(oldValue, maxBytesHeap);
    }

    private void fireMaxBytesOnLocalHeapChanged(Long oldValue, Long newValue) {
        if (oldValue != null && !oldValue.equals(newValue) || newValue != null && !newValue.equals(oldValue)) {
            for (CacheConfigurationListener listener : this.listeners) {
                listener.maxBytesLocalHeapChanged(oldValue != null ? oldValue : 0L, newValue);
            }
        }
    }

    private void fireMaxBytesOnLocalDiskChanged(Long oldValue, Long newValue) {
        if (oldValue != null && !oldValue.equals(newValue) || newValue != null && !newValue.equals(oldValue)) {
            for (CacheConfigurationListener listener : this.listeners) {
                listener.maxBytesLocalDiskChanged(oldValue != null ? oldValue : 0L, newValue);
            }
        }
    }

    private void fireDynamicAttributesExtractorAdded(DynamicAttributesExtractor oldValue, DynamicAttributesExtractor newValue) {
        if (oldValue != newValue) {
            for (DynamicSearchListener lsnr : this.dynamicSearchListeners) {
                lsnr.extractorChanged(oldValue, newValue);
            }
        }
    }

    public CacheConfiguration maxBytesLocalHeap(long amount, MemoryUnit memoryUnit) {
        this.setMaxBytesLocalHeap(memoryUnit.toBytes(amount));
        return this;
    }

    public long getMaxBytesLocalOffHeap() {
        return this.maxBytesLocalOffHeap == null ? 0L : this.maxBytesLocalOffHeap;
    }

    public String getMaxBytesLocalOffHeapAsString() {
        return this.maxBytesLocalOffHeapInput != null ? this.maxBytesLocalOffHeapInput : Long.toString(this.getMaxBytesLocalOffHeap());
    }

    public void setMaxBytesLocalOffHeap(String maxBytesOffHeap) {
        this.assertArgumentNotNull("Cache maxBytesLocalOffHeap", maxBytesOffHeap);
        if (this.isPercentage(maxBytesOffHeap)) {
            this.maxBytesLocalOffHeapPercentage = this.parsePercentage(maxBytesOffHeap);
        } else {
            this.setMaxBytesLocalOffHeap(MemoryUnit.parseSizeInBytes(maxBytesOffHeap));
        }
        this.maxBytesLocalOffHeapInput = maxBytesOffHeap;
        this.maxBytesLocalOffHeapExplicitlySet = true;
    }

    public Integer getMaxBytesLocalOffHeapPercentage() {
        return this.maxBytesLocalOffHeapPercentage;
    }

    @Deprecated
    public Integer getMaxBytesLocalHeapPercentage() {
        return this.maxBytesLocalHeapPercentage;
    }

    @Deprecated
    public String getMaxBytesLocalHeapAsString() {
        return this.maxBytesLocalHeapInput != null ? this.maxBytesLocalHeapInput : Long.toString(this.getMaxBytesLocalHeap());
    }

    public Integer getMaxBytesLocalDiskPercentage() {
        return this.maxBytesLocalDiskPercentage;
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

    public void setMaxBytesLocalOffHeap(Long maxBytesOffHeap) {
        if (this.offHeapPoolUsage != null) {
            throw new IllegalStateException("OffHeap can't be set dynamically!");
        }
        this.verifyGreaterThanZero(maxBytesOffHeap, "maxBytesLocalOffHeap");
        this.maxBytesLocalOffHeapExplicitlySet = true;
        this.maxBytesLocalOffHeap = maxBytesOffHeap;
    }

    public CacheConfiguration maxBytesLocalOffHeap(long amount, MemoryUnit memoryUnit) {
        this.setMaxBytesLocalOffHeap(memoryUnit.toBytes(amount));
        return this;
    }

    public long getMaxBytesLocalDisk() {
        return this.maxBytesLocalDisk == null ? 0L : this.maxBytesLocalDisk;
    }

    public String getMaxBytesLocalDiskAsString() {
        return this.maxBytesLocalDiskInput != null ? this.maxBytesLocalDiskInput : Long.toString(this.getMaxBytesLocalDisk());
    }

    public void setMaxBytesLocalDisk(String maxBytesDisk) {
        this.assertArgumentNotNull("Cache maxBytesLocalDisk", maxBytesDisk);
        if (this.isPercentage(maxBytesDisk)) {
            this.maxBytesLocalDiskPercentage = this.parsePercentage(maxBytesDisk);
        } else {
            this.setMaxBytesLocalDisk(MemoryUnit.parseSizeInBytes(maxBytesDisk));
        }
        this.maxBytesLocalDiskExplicitlySet = true;
        this.maxBytesLocalDiskInput = maxBytesDisk;
    }

    public void setMaxBytesLocalDisk(Long maxBytesDisk) {
        if (this.isTerracottaClustered()) {
            throw new InvalidConfigurationException("MaxBytesLocalDisk is not applicable for Terracotta clustered caches");
        }
        if (this.onDiskPoolUsage != null && this.getMaxEntriesLocalDisk() > 0L) {
            throw new InvalidConfigurationException("MaxEntriesLocalDisk is not compatible with MaxBytesLocalDisk set on cache");
        }
        if (this.onDiskPoolUsage != null && this.onDiskPoolUsage != PoolUsage.Cache) {
            throw new IllegalStateException("A Cache can't switch disk pool!");
        }
        this.checkDynamicChange();
        this.verifyGreaterThanZero(maxBytesDisk, "maxBytesLocalDisk");
        this.maxBytesLocalDiskExplicitlySet = true;
        Long oldValue = this.maxBytesLocalDisk;
        this.maxBytesLocalDisk = maxBytesDisk;
        this.fireMaxBytesOnLocalDiskChanged(oldValue, maxBytesDisk);
    }

    public CacheConfiguration maxBytesLocalDisk(long amount, MemoryUnit memoryUnit) {
        this.setMaxBytesLocalDisk(memoryUnit.toBytes(amount));
        return this;
    }

    public void setDynamicAttributesExtractor(DynamicAttributesExtractor extractor) {
        if (this.searchable == null || !this.searchable.isDynamicIndexingAllowed()) {
            throw new IllegalArgumentException("Dynamic search attribute extraction not supported");
        }
        if (extractor == null && this.flexIndexer != null) {
            throw new IllegalArgumentException("Dynamic search attributes extractor cannot be set to null by user");
        }
        DynamicAttributesExtractor old = this.flexIndexer;
        this.flexIndexer = extractor;
        this.fireDynamicAttributesExtractorAdded(old, this.flexIndexer);
    }

    public CacheConfiguration dynamicAttributeExtractor(DynamicAttributesExtractor extractor) {
        this.setDynamicAttributesExtractor(extractor);
        return this;
    }

    private void verifyGreaterThanZero(Long fieldVal, String fieldName) {
        if (fieldVal != null && fieldVal < 1L) {
            throw new IllegalArgumentException("Illegal value " + fieldVal + " for " + fieldName + ": has to be larger than 0");
        }
    }

    private void verifyGreaterThanOrEqualToZero(Long fieldVal, String fieldName) {
        if (fieldVal != null && fieldVal < 0L) {
            throw new IllegalArgumentException("Illegal value " + fieldVal + " for " + fieldName + ": has to be larger than or equal to 0");
        }
    }

    public CopyStrategyConfiguration getCopyStrategyConfiguration() {
        return this.copyStrategyConfiguration;
    }

    public ElementValueComparatorConfiguration getElementValueComparatorConfiguration() {
        return this.elementValueComparatorConfiguration;
    }

    @Deprecated
    public boolean isMaxBytesLocalHeapPercentageSet() {
        return this.maxBytesLocalHeapPercentage != null;
    }

    public boolean isMaxBytesLocalOffHeapPercentageSet() {
        return this.maxBytesLocalOffHeapPercentage != null;
    }

    public boolean isMaxBytesLocalDiskPercentageSet() {
        return this.maxBytesLocalDiskPercentage != null;
    }

    public void setupFor(CacheManager cacheManager) {
        this.setupFor(cacheManager, true, null);
    }

    public void setupFor(CacheManager cacheManager, boolean register, String parentCache) {
        Collection<ConfigError> errors = this.validate(cacheManager.getConfiguration());
        this.configCachePools(cacheManager.getConfiguration());
        errors.addAll(this.verifyPoolAllocationsBeforeAddingTo(cacheManager, cacheManager.getConfiguration().getMaxBytesLocalHeap(), cacheManager.getConfiguration().getMaxBytesLocalOffHeap(), cacheManager.getConfiguration().getMaxBytesLocalDisk(), parentCache));
        if (!errors.isEmpty()) {
            throw new InvalidConfigurationException(errors);
        }
        if (!this.isTerracottaClustered()) {
            this.updateCacheManagerPoolSizes(cacheManager);
        }
        if (register) {
            this.registerCacheConfiguration(cacheManager);
        }
        if (cacheManager.getConfiguration().isMaxBytesLocalHeapSet() || cacheManager.getConfiguration().isMaxBytesLocalDiskSet()) {
            this.addConfigurationListener(new DefaultCacheConfigurationListener(cacheManager));
        }
        this.consolidatePersistenceSettings(cacheManager);
        if (this.overflowToOffHeap == null && (cacheManager.getConfiguration().isMaxBytesLocalOffHeapSet() || this.getMaxBytesLocalOffHeap() > 0L)) {
            this.overflowToOffHeap = true;
        }
        if (this.persistenceConfiguration != null && PersistenceConfiguration.Strategy.LOCALTEMPSWAP.equals((Object)this.persistenceConfiguration.getStrategy())) {
            this.overflowToDisk = true;
        }
        if (this.overflowToDisk == null && cacheManager.getConfiguration().isMaxBytesLocalDiskSet() || this.getMaxBytesLocalDisk() > 0L) {
            if (this.persistenceConfiguration != null && PersistenceConfiguration.Strategy.LOCALRESTARTABLE.equals((Object)this.persistenceConfiguration.getStrategy())) {
                throw new InvalidConfigurationException("Cannot use localRestartable persistence and disk overflow in the same cache");
            }
            if (!this.isTerracottaClustered()) {
                this.overflowToDisk = true;
            } else {
                LOG.warn("Clustered cache " + this.getName() + " will not use the local disk pool defined at cache manager level.");
            }
        }
        this.warnMaxEntriesLocalHeap(register, cacheManager);
        this.warnMaxEntriesForOverflowToOffHeap(register);
        this.warnSizeOfPolicyConfiguration();
        this.freezePoolUsages(cacheManager);
        this.warnTieredSizing();
    }

    private void consolidatePersistenceSettings(CacheManager manager) {
        if (this.persistenceConfiguration == null) {
            if (this.diskPersistent == Boolean.TRUE) {
                this.persistenceConfiguration = new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP);
            }
        } else {
            switch (this.persistenceConfiguration.getStrategy()) {
                case DISTRIBUTED: 
                case NONE: {
                    this.diskPersistent = Boolean.FALSE;
                    break;
                }
                case LOCALTEMPSWAP: {
                    if (this.diskPersistent != null) break;
                    this.diskPersistent = Boolean.FALSE;
                    break;
                }
                case LOCALRESTARTABLE: {
                    this.diskPersistent = Boolean.TRUE;
                    break;
                }
            }
        }
        if (this.persistenceConfiguration != null && this.persistenceConfiguration.getSynchronousWrites()) {
            switch (this.persistenceConfiguration.getStrategy()) {
                case NONE: 
                case LOCALTEMPSWAP: {
                    throw new InvalidConfigurationException("Persistence: synchronousWrites=\"true\" is not supported with strategy \"localTempSwap\" or \"none\"");
                }
            }
        }
    }

    private void warnMaxEntriesForOverflowToOffHeap(boolean register) {
        if (this.overflowToOffHeap != null && this.overflowToOffHeap.booleanValue() && register && this.getMaxEntriesLocalHeap() > 0L && this.getMaxEntriesLocalHeap() < 100L) {
            LOG.warn("The " + this.getName() + " cache is configured for off-heap and has a maxEntriesLocalHeap/maxElementsInMemory of " + this.getMaxEntriesLocalHeap() + ".  It is recommended to set maxEntriesLocalHeap/maxElementsInMemory to at least 100 elements when using an off-heap store, otherwise performance will be seriously degraded.");
        }
    }

    private void warnMaxEntriesLocalHeap(boolean register, CacheManager cacheManager) {
        if (this.getMaxEntriesLocalHeap() == 0L && register && this.getMaxBytesLocalHeap() == 0L && !cacheManager.getConfiguration().isMaxBytesLocalHeapSet()) {
            LOG.warn("Cache: " + this.getName() + " has a maxElementsInMemory of 0. This might lead to performance degradation or OutOfMemoryError at Terracotta client.From Ehcache 2.0 onwards this has been changed to mean a store with no capacity limit. Set it to 1 if you want no elements cached in memory");
        }
    }

    private void warnSizeOfPolicyConfiguration() {
        if (this.isTerracottaClustered() && this.getSizeOfPolicyConfiguration() != null) {
            LOG.warn("Terracotta clustered cache: " + this.getName() + " has a sizeOf policy configuration specificed. SizeOfPolicyConfiguration is unsupported for Terracotta clustered caches.");
        }
    }

    private void freezePoolUsages(CacheManager cacheManager) {
        this.onHeapPoolUsage = this.getMaxBytesLocalHeap() > 0L ? PoolUsage.Cache : (cacheManager.getConfiguration().isMaxBytesLocalHeapSet() ? PoolUsage.CacheManager : PoolUsage.None);
        this.offHeapPoolUsage = this.getMaxBytesLocalOffHeap() > 0L ? PoolUsage.Cache : (cacheManager.getConfiguration().isMaxBytesLocalOffHeapSet() ? PoolUsage.CacheManager : PoolUsage.None);
        this.onDiskPoolUsage = this.isTerracottaClustered() ? PoolUsage.None : (this.getMaxBytesLocalDisk() > 0L ? PoolUsage.Cache : (cacheManager.getConfiguration().isMaxBytesLocalDiskSet() ? PoolUsage.CacheManager : PoolUsage.None));
    }

    private void warnTieredSizing() {
        if (this.isOverflowToOffHeap()) {
            if (this.getMaxBytesLocalHeap() >= this.getMaxBytesLocalOffHeap() && this.getMaxBytesLocalOffHeap() != 0L) {
                LOG.warn("Configuration problem for cache " + this.getName() + ": MaxBytesLocalHeap equal or greater than MaxBytesLocalOffHeap. This will result in useless off heap storage.");
            }
            if (this.isOverflowToDisk() && this.getMaxBytesLocalOffHeap() >= this.getMaxBytesLocalDisk() && this.getMaxBytesLocalDisk() != 0L) {
                LOG.warn("Configuration problem for cache " + this.getName() + ": MaxBytesLocalOffHeap equal or greater than MaxBytesLocalDisk. This will result in useless disk storage.");
            }
        }
        if (this.isOverflowToDisk() && this.getMaxEntriesLocalHeap() >= this.getMaxEntriesLocalDisk() && this.getMaxEntriesLocalDisk() != 0L) {
            LOG.warn("Configuration problem for cache " + this.getName() + ": MaxEntriesLocalHeap equal or greater than MaxEntriesLocalDisk. This will result in useless disk storage.");
        }
    }

    private void registerCacheConfiguration(CacheManager cacheManager) {
        Map<String, CacheConfiguration> configMap = cacheManager.getConfiguration().getCacheConfigurations();
        if (!configMap.containsKey(this.getName())) {
            cacheManager.getConfiguration().addCache(this, false);
        }
    }

    private void updateCacheManagerPoolSizes(CacheManager cacheManager) {
        if (cacheManager.getOnHeapPool() != null) {
            cacheManager.getOnHeapPool().setMaxSize(cacheManager.getOnHeapPool().getMaxSize() - this.getMaxBytesLocalHeap());
        }
        if (cacheManager.getOnDiskPool() != null) {
            cacheManager.getOnDiskPool().setMaxSize(cacheManager.getOnDiskPool().getMaxSize() - this.getMaxBytesLocalDisk());
        }
    }

    List<ConfigError> verifyPoolAllocationsBeforeAddingTo(CacheManager cacheManager, long managerMaxBytesLocalHeap, long managerMaxBytesLocalOffHeap, long managerMaxBytesLocalDisk, String parentCacheName) {
        ArrayList<ConfigError> configErrors = new ArrayList<ConfigError>();
        long totalOnHeapAssignedMemory = 0L;
        long totalOffHeapAssignedMemory = 0L;
        long totalOnDiskAssignedMemory = 0L;
        boolean isUpdate = false;
        for (Cache cache : Configuration.getAllActiveCaches(cacheManager)) {
            if (cache.getName().equals(parentCacheName)) continue;
            isUpdate = cache.getName().equals(this.getName()) || isUpdate;
            CacheConfiguration config = cache.getCacheConfiguration();
            totalOnHeapAssignedMemory += config.getMaxBytesLocalHeap();
            totalOffHeapAssignedMemory += config.getMaxBytesLocalOffHeap();
            totalOnDiskAssignedMemory += config.getMaxBytesLocalDisk();
        }
        if (!isUpdate) {
            totalOnHeapAssignedMemory += this.getMaxBytesLocalHeap();
            totalOffHeapAssignedMemory += this.getMaxBytesLocalOffHeap();
            totalOnDiskAssignedMemory += this.getMaxBytesLocalDisk();
        }
        this.verifyLocalHeap(managerMaxBytesLocalHeap, configErrors, totalOnHeapAssignedMemory);
        this.verifyLocalOffHeap(managerMaxBytesLocalOffHeap, configErrors, totalOffHeapAssignedMemory);
        this.verifyLocalDisk(managerMaxBytesLocalDisk, configErrors, totalOnDiskAssignedMemory);
        if (managerMaxBytesLocalHeap > 0L && managerMaxBytesLocalHeap - totalOnHeapAssignedMemory == 0L) {
            LOG.warn("All the onHeap memory has been assigned, there is none left for dynamically added caches");
        }
        if (Runtime.getRuntime().maxMemory() - totalOnHeapAssignedMemory < 0L) {
            configErrors.add(new ConfigError("You've assigned more memory to the on-heap than the VM can sustain, please adjust your -Xmx setting accordingly"));
        }
        if ((double)((float)totalOnHeapAssignedMemory / (float)Runtime.getRuntime().maxMemory()) > 0.8) {
            LOG.warn("You've assigned over 80% of your VM's heap to be used by the cache!");
        }
        return configErrors;
    }

    private void verifyLocalDisk(long managerMaxBytesLocalDisk, List<ConfigError> configErrors, long totalOnDiskAssignedMemory) {
        if ((this.isMaxBytesLocalDiskPercentageSet() || this.getMaxBytesLocalDisk() > 0L) && managerMaxBytesLocalDisk > 0L && managerMaxBytesLocalDisk - totalOnDiskAssignedMemory < 0L) {
            configErrors.add(new ConfigError("Cache '" + this.getName() + "' over-allocates CacheManager's localOnDisk limit!"));
        }
    }

    private void verifyLocalOffHeap(long managerMaxBytesLocalOffHeap, List<ConfigError> configErrors, long totalOffHeapAssignedMemory) {
        if ((this.isMaxBytesLocalOffHeapPercentageSet() || this.getMaxBytesLocalOffHeap() > 0L) && managerMaxBytesLocalOffHeap > 0L && managerMaxBytesLocalOffHeap - totalOffHeapAssignedMemory < 0L) {
            configErrors.add(new ConfigError("Cache '" + this.getName() + "' over-allocates CacheManager's localOffHeap limit!"));
        }
    }

    private void verifyLocalHeap(long managerMaxBytesLocalHeap, List<ConfigError> configErrors, long totalOnHeapAssignedMemory) {
        if ((this.isMaxBytesLocalHeapPercentageSet() || this.getMaxBytesLocalHeap() > 0L) && managerMaxBytesLocalHeap > 0L && managerMaxBytesLocalHeap - totalOnHeapAssignedMemory < 0L) {
            configErrors.add(new ConfigError("Cache '" + this.getName() + "' over-allocates CacheManager's localOnHeap limit!"));
        }
    }

    void configCachePools(Configuration configuration) {
        long cacheAssignedMem;
        if (this.getMaxBytesLocalHeapPercentage() != null) {
            cacheAssignedMem = configuration.getMaxBytesLocalHeap() * (long)this.getMaxBytesLocalHeapPercentage().intValue() / 100L;
            this.setMaxBytesLocalHeap(cacheAssignedMem);
        }
        if (this.offHeapPoolUsage == null && this.getMaxBytesLocalOffHeapPercentage() != null) {
            cacheAssignedMem = configuration.getMaxBytesLocalOffHeap() * (long)this.getMaxBytesLocalOffHeapPercentage().intValue() / 100L;
            this.setMaxBytesLocalOffHeap(cacheAssignedMem);
        }
        if (this.getMaxBytesLocalDiskPercentage() != null) {
            cacheAssignedMem = configuration.getMaxBytesLocalDisk() * (long)this.getMaxBytesLocalDiskPercentage().intValue() / 100L;
            this.setMaxBytesLocalDisk(cacheAssignedMem);
        }
    }

    public Collection<ConfigError> validate(Configuration configuration) {
        ArrayList<ConfigError> errors = new ArrayList<ConfigError>();
        this.verifyClusteredCacheConfiguration(errors);
        if (this.maxEntriesLocalHeap == null && !configuration.isMaxBytesLocalHeapSet() && this.maxBytesLocalHeap == null) {
            errors.add(new CacheConfigError("If your CacheManager has no maxBytesLocalHeap set, you need to either set maxEntriesLocalHeap or maxBytesLocalHeap at the Cache level", this.getName()));
        }
        if (configuration.isMaxBytesLocalHeapSet() && Runtime.getRuntime().maxMemory() - configuration.getMaxBytesLocalHeap() < 0L) {
            errors.add(new ConfigError("You've assigned more memory to the on-heap than the VM can sustain, please adjust your -Xmx setting accordingly"));
        }
        if (this.maxEntriesInCache != 0L && !this.isTerracottaClustered()) {
            errors.add(new CacheConfigError("maxEntriesInCache is not applicable to unclustered caches.", this.getName()));
        }
        if (this.persistenceConfiguration != null && this.persistenceConfiguration.getStrategy() == null) {
            errors.add(new CacheConfigError("Persistence configuration found with no strategy set.", this.getName()));
        }
        errors.addAll(this.validateCachePools(configuration));
        return errors;
    }

    private void verifyClusteredCacheConfiguration(Collection<ConfigError> errors) {
        if (!this.isTerracottaClustered()) {
            return;
        }
        if (this.getPinningConfiguration() != null && this.getPinningConfiguration().getStore() == PinningConfiguration.Store.INCACHE && this.getMaxElementsOnDisk() != 0) {
            errors.add(new CacheConfigError("maxElementsOnDisk may not be used on a pinned cache.", this.getName()));
        }
        if (this.maxEntriesLocalDiskExplicitlySet) {
            errors.add(new CacheConfigError("You can't set maxEntriesLocalDisk when clustering your cache with Terracotta, local disks won't be used! To control elements going in the cache cluster wide, use maxEntriesInCache instead", this.getName()));
        }
        if (this.maxBytesLocalDiskExplicitlySet) {
            errors.add(new CacheConfigError("You can't set maxBytesLocalDisk when clustering your cache with Terracotta", this.getName()));
        }
        if (this.maxElementsOnDisk != 0) {
            errors.add(new CacheConfigError("maxElementsOnDisk is not used with clustered caches. Use maxEntriesInCache to set maximum cache size.", this.getName()));
        }
    }

    List<CacheConfigError> validateCachePools(Configuration configuration) {
        ArrayList<CacheConfigError> errors = new ArrayList<CacheConfigError>();
        if (configuration.isMaxBytesLocalHeapSet() && this.getMaxEntriesLocalHeap() > 0L) {
            errors.add(new CacheConfigError("maxEntriesLocalHeap is not compatible with maxBytesLocalHeap set on cache manager", this.getName()));
        }
        if (this.getMaxBytesLocalHeap() > 0L && this.getMaxEntriesLocalHeap() > 0L) {
            errors.add(new CacheConfigError("maxEntriesLocalHeap is not compatible with maxBytesLocalHeap set on cache", this.getName()));
        }
        if (this.isMaxBytesLocalHeapPercentageSet() && !configuration.isMaxBytesLocalHeapSet()) {
            errors.add(new CacheConfigError("Defines a percentage maxBytesOnHeap value but no CacheManager wide value was configured", this.getName()));
        }
        if (this.isMaxBytesLocalOffHeapPercentageSet() && !configuration.isMaxBytesLocalOffHeapSet()) {
            errors.add(new CacheConfigError("Defines a percentage maxBytesOffHeap value but no CacheManager wide value was configured", this.getName()));
        }
        if (this.isMaxBytesLocalDiskPercentageSet() && !configuration.isMaxBytesLocalDiskSet()) {
            errors.add(new CacheConfigError("Defines a percentage maxBytesOnDisk value but no CacheManager wide value was configured", this.getName()));
        }
        return errors;
    }

    public boolean isCountBasedTuned() {
        return this.maxEntriesLocalHeap != null && this.maxEntriesLocalHeap > 0 || this.maxElementsOnDisk > 0;
    }

    public boolean isOverflowToOffHeapSet() {
        return this.overflowToOffHeap != null;
    }

    public final void addCacheEventListenerFactory(CacheEventListenerFactoryConfiguration factory) {
        this.checkDynamicChange();
        this.cacheEventListenerConfigurations.add(factory);
        this.validateConfiguration();
    }

    public final CacheConfiguration cacheEventListenerFactory(CacheEventListenerFactoryConfiguration factory) {
        this.addCacheEventListenerFactory(factory);
        return this;
    }

    public final void addCacheExtensionFactory(CacheExtensionFactoryConfiguration factory) {
        this.checkDynamicChange();
        this.cacheExtensionConfigurations.add(factory);
    }

    public final CacheConfiguration cacheExtensionFactory(CacheExtensionFactoryConfiguration factory) {
        this.addCacheExtensionFactory(factory);
        return this;
    }

    public final void addBootstrapCacheLoaderFactory(BootstrapCacheLoaderFactoryConfiguration factory) {
        this.checkDynamicChange();
        this.bootstrapCacheLoaderFactoryConfiguration = factory;
    }

    public final CacheConfiguration bootstrapCacheLoaderFactory(BootstrapCacheLoaderFactoryConfiguration factory) {
        this.addBootstrapCacheLoaderFactory(factory);
        return this;
    }

    public final void addCacheExceptionHandlerFactory(CacheExceptionHandlerFactoryConfiguration factory) {
        this.checkDynamicChange();
        this.cacheExceptionHandlerFactoryConfiguration = factory;
    }

    public final CacheConfiguration cacheExceptionHandlerFactory(CacheExceptionHandlerFactoryConfiguration factory) {
        this.addCacheExceptionHandlerFactory(factory);
        return this;
    }

    public final void addCacheLoaderFactory(CacheLoaderFactoryConfiguration factory) {
        this.checkDynamicChange();
        this.cacheLoaderConfigurations.add(factory);
    }

    public final void addCacheDecoratorFactory(CacheDecoratorFactoryConfiguration factory) {
        this.checkDynamicChange();
        this.cacheDecoratorConfigurations.add(factory);
    }

    public final CacheConfiguration cacheLoaderFactory(CacheLoaderFactoryConfiguration factory) {
        this.addCacheLoaderFactory(factory);
        return this;
    }

    public final void addTerracotta(TerracottaConfiguration terracottaConfiguration) {
        this.terracottaConfiguration = terracottaConfiguration;
        this.validateConfiguration();
    }

    public final void addPinning(PinningConfiguration pinningConfiguration) {
        this.pinningConfiguration = pinningConfiguration;
        this.validateConfiguration();
    }

    public final CacheConfiguration pinning(PinningConfiguration pinningConfiguration) {
        this.addPinning(pinningConfiguration);
        return this;
    }

    public final CacheConfiguration terracotta(TerracottaConfiguration terracottaConfiguration) {
        this.addTerracotta(terracottaConfiguration);
        return this;
    }

    public final CacheConfiguration searchable(Searchable searchable) {
        this.addSearchable(searchable);
        return this;
    }

    public final void addCacheWriter(CacheWriterConfiguration cacheWriterConfiguration) {
        this.cacheWriterConfiguration = null == cacheWriterConfiguration ? new CacheWriterConfiguration() : cacheWriterConfiguration;
    }

    public final CacheConfiguration cacheWriter(CacheWriterConfiguration cacheWriterConfiguration) {
        this.addCacheWriter(cacheWriterConfiguration);
        return this;
    }

    public final void setTransactionalMode(String transactionalMode) {
        this.assertArgumentNotNull("Cache transactionalMode", transactionalMode);
        this.transactionalMode(TransactionalMode.valueOf(transactionalMode.toUpperCase()));
    }

    public final CacheConfiguration transactionalMode(String transactionalMode) {
        this.setTransactionalMode(transactionalMode);
        return this;
    }

    public final CacheConfiguration transactionalMode(TransactionalMode transactionalMode) {
        if (transactionalMode == null) {
            throw new IllegalArgumentException("TransactionalMode value must be non-null");
        }
        if (this.transactionalMode != null) {
            throw new InvalidConfigurationException("transactionalMode cannot be changed once set");
        }
        this.transactionalMode = transactionalMode;
        return this;
    }

    @Deprecated
    public final void setStatistics(boolean enabled) {
        if (!enabled) {
            LOG.warn("Statistics can no longer be enabled via configuration.");
            return;
        }
        this.statistics = enabled;
    }

    @Deprecated
    public final CacheConfiguration statistics(boolean statistics) {
        this.setStatistics(statistics);
        return this;
    }

    public final boolean getStatistics() {
        return true;
    }

    public void validateCompleteConfiguration() {
        this.validateConfiguration();
        if (this.name == null) {
            throw new InvalidConfigurationException("Caches must be named.");
        }
    }

    public void validateConfiguration() {
        if (this.terracottaConfiguration != null && this.terracottaConfiguration.isClustered()) {
            if (this.overflowToDisk != null && this.overflowToDisk.booleanValue()) {
                throw new InvalidConfigurationException("overflowToDisk isn't supported for a clustered Terracotta cache");
            }
            if (this.diskPersistent == Boolean.TRUE) {
                throw new InvalidConfigurationException("diskPersistent isn't supported for a clustered Terracotta cache");
            }
            if (this.persistenceConfiguration != null && !PersistenceConfiguration.Strategy.DISTRIBUTED.equals((Object)this.persistenceConfiguration.getStrategy())) {
                throw new InvalidConfigurationException(this.persistenceConfiguration.getStrategy() + " persistence strategy isn't supported for a clustered Terracotta cache");
            }
            if (this.cacheEventListenerConfigurations != null) {
                for (CacheEventListenerFactoryConfiguration listenerConfig : this.cacheEventListenerConfigurations) {
                    if (null == listenerConfig.getFullyQualifiedClassPath() || listenerConfig.getFullyQualifiedClassPath().startsWith("net.sf.ehcache.") || !LOG.isWarnEnabled()) continue;
                    LOG.warn("The non-standard CacheEventListenerFactory '" + listenerConfig.getFullyQualifiedClassPath() + "' is used with a clustered Terracotta cache, if the purpose of this listener is replication it is not supported in a clustered context");
                }
            }
        }
        if (this.cacheWriterConfiguration != null && !this.cacheWriterConfiguration.getWriteBatching() && this.cacheWriterConfiguration.getWriteBatchSize() != 1) {
            throw new InvalidConfigurationException("CacheWriter Batch Size !=1 and CacheWriter Batching turned off");
        }
    }

    private void validateTransactionalSettings() {
        boolean transactional = this.getTransactionalMode().isTransactional();
        if (this.copyOnRead == null) {
            this.copyOnRead = this.terracottaConfiguration != null && this.terracottaConfiguration.isCopyOnReadSet() ? Boolean.valueOf(this.terracottaConfiguration.isCopyOnRead()) : Boolean.valueOf(transactional);
        }
        if (this.copyOnWrite == null) {
            this.copyOnWrite = transactional;
        }
        if (!(!transactional || this.copyOnRead.booleanValue() && this.copyOnWrite.booleanValue())) {
            throw new InvalidConfigurationException("A transactional cache has to be copyOnRead and copyOnWrite!");
        }
    }

    public String getName() {
        return this.name;
    }

    @Deprecated
    public int getMaxElementsInMemory() {
        return (int)this.getMaxEntriesLocalHeap();
    }

    public long getCacheLoaderTimeoutMillis() {
        return this.cacheLoaderTimeoutMillis;
    }

    public int getMaxElementsOnDisk() {
        return this.maxElementsOnDisk;
    }

    public long getMaxEntriesInCache() {
        return this.maxEntriesInCache;
    }

    public long getMaxEntriesLocalDisk() {
        return this.maxElementsOnDisk;
    }

    public long getMaxEntriesLocalHeap() {
        return this.maxEntriesLocalHeap == null ? 0L : (long)this.maxEntriesLocalHeap.intValue();
    }

    public MemoryStoreEvictionPolicy getMemoryStoreEvictionPolicy() {
        return this.memoryStoreEvictionPolicy;
    }

    public boolean isClearOnFlush() {
        return this.clearOnFlush;
    }

    public boolean isEternal() {
        return this.eternal;
    }

    public long getTimeToIdleSeconds() {
        return this.timeToIdleSeconds;
    }

    public long getTimeToLiveSeconds() {
        return this.timeToLiveSeconds;
    }

    @Deprecated
    public boolean isOverflowToDisk() {
        return this.overflowToDisk == null ? false : this.overflowToDisk;
    }

    @Deprecated
    public boolean isDiskPersistent() {
        Boolean persistent = this.diskPersistent;
        return this.diskPersistent == null ? false : persistent;
    }

    public boolean isSearchable() {
        return this.searchable != null;
    }

    public int getDiskSpoolBufferSizeMB() {
        return this.diskSpoolBufferSizeMB;
    }

    public long getDiskExpiryThreadIntervalSeconds() {
        return this.diskExpiryThreadIntervalSeconds;
    }

    public int getDiskAccessStripes() {
        return this.diskAccessStripes;
    }

    public DynamicAttributesExtractor getDynamicExtractor() {
        return this.flexIndexer;
    }

    public boolean getLogging() {
        return this.logging;
    }

    public boolean isOverflowToOffHeap() {
        return this.overflowToOffHeap == null ? false : this.overflowToOffHeap;
    }

    public SizeOfPolicyConfiguration getSizeOfPolicyConfiguration() {
        return this.sizeOfPolicyConfiguration;
    }

    public PersistenceConfiguration getPersistenceConfiguration() {
        return this.persistenceConfiguration;
    }

    @Deprecated
    public String getMaxMemoryOffHeap() {
        return this.maxBytesLocalOffHeapInput;
    }

    @Deprecated
    public long getMaxMemoryOffHeapInBytes() {
        return this.getMaxBytesLocalOffHeap();
    }

    public List getCacheEventListenerConfigurations() {
        return this.cacheEventListenerConfigurations;
    }

    public List getCacheExtensionConfigurations() {
        return this.cacheExtensionConfigurations;
    }

    public List getCacheLoaderConfigurations() {
        return this.cacheLoaderConfigurations;
    }

    public List<CacheDecoratorFactoryConfiguration> getCacheDecoratorConfigurations() {
        return this.cacheDecoratorConfigurations;
    }

    public BootstrapCacheLoaderFactoryConfiguration getBootstrapCacheLoaderFactoryConfiguration() {
        return this.bootstrapCacheLoaderFactoryConfiguration;
    }

    public CacheExceptionHandlerFactoryConfiguration getCacheExceptionHandlerFactoryConfiguration() {
        return this.cacheExceptionHandlerFactoryConfiguration;
    }

    public TerracottaConfiguration getTerracottaConfiguration() {
        return this.terracottaConfiguration;
    }

    public PinningConfiguration getPinningConfiguration() {
        return this.pinningConfiguration;
    }

    public CacheWriterConfiguration getCacheWriterConfiguration() {
        return this.cacheWriterConfiguration;
    }

    public boolean isTerracottaClustered() {
        return this.terracottaConfiguration != null && this.terracottaConfiguration.isClustered();
    }

    public TerracottaConfiguration.Consistency getTerracottaConsistency() {
        return this.terracottaConfiguration != null ? this.terracottaConfiguration.getConsistency() : null;
    }

    public final TransactionalMode getTransactionalMode() {
        if (this.transactionalMode == null) {
            return DEFAULT_TRANSACTIONAL_MODE;
        }
        return this.transactionalMode;
    }

    public boolean isXaStrictTransactional() {
        this.validateTransactionalSettings();
        return this.getTransactionalMode().equals((Object)TransactionalMode.XA_STRICT);
    }

    public boolean isLocalTransactional() {
        this.validateTransactionalSettings();
        return this.getTransactionalMode().equals((Object)TransactionalMode.LOCAL);
    }

    public boolean isXaTransactional() {
        this.validateTransactionalSettings();
        return this.getTransactionalMode().equals((Object)TransactionalMode.XA);
    }

    public boolean addConfigurationListener(CacheConfigurationListener listener) {
        boolean added = this.listeners.add(listener);
        if (added) {
            listener.registered(this);
        }
        return added;
    }

    public boolean addDynamicSearchListener(DynamicSearchListener listener) {
        return this.dynamicSearchListeners.add(listener);
    }

    public boolean removeConfigurationListener(CacheConfigurationListener listener) {
        boolean removed = this.listeners.remove(listener);
        if (removed) {
            listener.deregistered(this);
        }
        return removed;
    }

    private void fireTtiChanged(long oldTti, long newTti) {
        if (oldTti != newTti) {
            for (CacheConfigurationListener l : this.listeners) {
                l.timeToIdleChanged(oldTti, newTti);
            }
        }
    }

    private void fireTtlChanged(long oldTtl, long newTtl) {
        if (oldTtl != newTtl) {
            for (CacheConfigurationListener l : this.listeners) {
                l.timeToLiveChanged(oldTtl, newTtl);
            }
        }
    }

    private void fireLoggingChanged(boolean oldValue, boolean newValue) {
        if (oldValue != newValue) {
            for (CacheConfigurationListener l : this.listeners) {
                l.loggingChanged(oldValue, newValue);
            }
        }
    }

    private void fireDiskCapacityChanged(int oldCapacity, int newCapacity) {
        if (oldCapacity != newCapacity) {
            for (CacheConfigurationListener l : this.listeners) {
                l.diskCapacityChanged(oldCapacity, newCapacity);
            }
        }
    }

    private void fireMaxEntriesInCacheChanged(long oldCapacity, long newCapacity) {
        if (oldCapacity != newCapacity) {
            for (CacheConfigurationListener l : this.listeners) {
                l.maxEntriesInCacheChanged(oldCapacity, newCapacity);
            }
        }
    }

    private void fireMemoryCapacityChanged(int oldCapacity, int newCapacity) {
        if (oldCapacity != newCapacity) {
            for (CacheConfigurationListener l : this.listeners) {
                l.memoryCapacityChanged(oldCapacity, newCapacity);
            }
        }
    }

    private void checkDynamicChange() {
        if (this.frozen) {
            throw new CacheException("Dynamic configuration changes are disabled for this cache");
        }
    }

    public void internalSetTimeToIdle(long timeToIdle) {
        this.timeToIdleSeconds = timeToIdle;
    }

    public void internalSetTimeToLive(long timeToLive) {
        this.timeToLiveSeconds = timeToLive;
    }

    public void internalSetEternal(boolean eternal) {
        this.eternal = eternal;
    }

    public void internalSetMemCapacity(int capacity) {
        this.maxEntriesLocalHeap = capacity;
    }

    @Deprecated
    public void internalSetMemCapacityInBytes(long capacity) {
        this.maxBytesLocalHeap = capacity;
    }

    public void internalSetDiskCapacity(int capacity) {
        this.maxElementsOnDisk = capacity;
    }

    public void internalSetMaxEntriesInCache(int entries) {
        this.maxEntriesInCache = entries;
    }

    public void internalSetLogging(boolean logging) {
        this.logging = logging;
    }

    public void internalSetMaxBytesLocalOffheap(long maxBytesLocalOffheap) {
        this.maxBytesLocalOffHeap = maxBytesLocalOffheap;
    }

    public void internalSetOverflowToOffheap(boolean overflowToOffHeap) {
        this.overflowToOffHeap = overflowToOffHeap;
    }

    public Map<String, SearchAttribute> getSearchAttributes() {
        if (this.searchable == null) {
            return Collections.emptyMap();
        }
        return this.searchable.getSearchAttributes();
    }

    public Searchable getSearchable() {
        return this.searchable;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.checkDynamicChange();
        this.classLoader = classLoader;
    }

    private class DefaultCacheConfigurationListener
    extends AbstractCacheConfigurationListener {
        private final CacheManager cacheManager;

        public DefaultCacheConfigurationListener(CacheManager cacheManager) {
            this.cacheManager = cacheManager;
        }

        @Override
        public void maxBytesLocalHeapChanged(long oldValue, long newValue) {
            if (CacheConfiguration.this.getMaxBytesLocalHeap() > 0L && this.cacheManager.getConfiguration().getCacheConfigurations().keySet().contains(CacheConfiguration.this.getName()) && this.cacheManager.getConfiguration().isMaxBytesLocalHeapSet()) {
                long oldCacheManagerPoolSize = this.cacheManager.getOnHeapPool().getMaxSize();
                long newPoolFreeSize = oldCacheManagerPoolSize + oldValue - newValue;
                if (newPoolFreeSize >= 0L) {
                    this.cacheManager.getOnHeapPool().setMaxSize(newPoolFreeSize);
                } else {
                    CacheConfiguration.this.maxBytesLocalHeap = oldValue;
                    throw new InvalidConfigurationException("Cannot allocate heap size more than the cache pool size reverting to previous size " + CacheConfiguration.this.maxBytesLocalHeap);
                }
            }
        }

        @Override
        public void maxBytesLocalDiskChanged(long oldValue, long newValue) {
            if (CacheConfiguration.this.getMaxBytesLocalDisk() > 0L && this.cacheManager.getConfiguration().getCacheConfigurations().keySet().contains(CacheConfiguration.this.getName()) && this.cacheManager.getConfiguration().isMaxBytesLocalDiskSet()) {
                long previous = this.cacheManager.getOnDiskPool().getMaxSize();
                long newPoolFreeSize = previous + oldValue - newValue;
                if (newPoolFreeSize >= 0L) {
                    this.cacheManager.getOnDiskPool().setMaxSize(newPoolFreeSize);
                } else {
                    LOG.warn("Cannot allocate local disk size more than cache disk size, setting maxBytesLocalDisk {} to old value {}", (Object)CacheConfiguration.this.maxBytesLocalDisk, (Object)oldValue);
                    CacheConfiguration.this.maxBytesLocalDisk = oldValue;
                    throw new InvalidConfigurationException("Cannot allocate disk size more than the cache pool size reverting to previous size " + CacheConfiguration.this.maxBytesLocalHeap);
                }
            }
        }
    }

    public static enum TransactionalMode {
        OFF(false),
        LOCAL(true),
        XA(true),
        XA_STRICT(true);

        private final boolean transactional;

        private TransactionalMode(boolean transactional) {
            this.transactional = transactional;
        }

        public boolean isTransactional() {
            return this.transactional;
        }
    }

    private static enum PoolUsage {
        CacheManager(true),
        Cache(true),
        None(false);

        private final boolean usingPool;

        private PoolUsage(boolean poolUser) {
            this.usingPool = poolUser;
        }

        public boolean isUsingPool() {
            return this.usingPool;
        }
    }

    public static final class CacheDecoratorFactoryConfiguration
    extends FactoryConfiguration<CacheDecoratorFactoryConfiguration> {
    }

    public static final class CacheLoaderFactoryConfiguration
    extends FactoryConfiguration<CacheLoaderFactoryConfiguration> {
    }

    public static final class CacheExceptionHandlerFactoryConfiguration
    extends FactoryConfiguration<CacheExceptionHandlerFactoryConfiguration> {
    }

    public static final class BootstrapCacheLoaderFactoryConfiguration
    extends FactoryConfiguration<BootstrapCacheLoaderFactoryConfiguration> {
    }

    public static final class CacheExtensionFactoryConfiguration
    extends FactoryConfiguration<CacheExtensionFactoryConfiguration> {
    }

    public static final class CacheEventListenerFactoryConfiguration
    extends FactoryConfiguration<CacheEventListenerFactoryConfiguration> {
        private NotificationScope notificationScope = NotificationScope.ALL;

        public void setListenFor(String listenFor) {
            if (listenFor == null) {
                throw new IllegalArgumentException("listenFor must be non-null");
            }
            this.notificationScope = NotificationScope.valueOf(NotificationScope.class, listenFor.toUpperCase());
        }

        public final CacheEventListenerFactoryConfiguration listenFor(String listenFor) {
            this.setListenFor(listenFor);
            return this;
        }

        public NotificationScope getListenFor() {
            return this.notificationScope;
        }
    }
}

