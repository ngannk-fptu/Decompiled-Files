/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.config;

import net.sf.ehcache.config.NonstopConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerracottaConfiguration
implements Cloneable {
    public static final boolean DEFAULT_CLUSTERED = true;
    public static final boolean DEFAULT_COHERENT_READS = true;
    public static final boolean DEFAULT_CACHE_XA = false;
    public static final boolean DEFAULT_ORPHAN_EVICTION = true;
    public static final int DEFAULT_ORPHAN_EVICTION_PERIOD = 4;
    public static final boolean DEFAULT_LOCAL_KEY_CACHE = false;
    public static final int DEFAULT_LOCAL_KEY_CACHE_SIZE = 300000;
    public static final boolean DEFAULT_COPY_ON_READ = false;
    public static final boolean DEFAULT_COMPRESSION_ENABLED = false;
    public static final NonstopConfiguration DEFAULT_NON_STOP_CONFIGURATION = TerracottaConfiguration.makeDefaultNonstopConfiguration();
    @Deprecated
    public static final boolean DEFAULT_CACHE_COHERENT = true;
    public static final Consistency DEFAULT_CONSISTENCY_TYPE = Consistency.EVENTUAL;
    public static final boolean DEFAULT_SYNCHRONOUS_WRITES = false;
    public static final int DEFAULT_CONCURRENCY = 0;
    public static final boolean DEFAULT_LOCAL_CACHE_ENABLED = true;
    private static final Logger LOG = LoggerFactory.getLogger((String)TerracottaConfiguration.class.getName());
    private boolean clustered = true;
    private boolean coherentReads = true;
    private boolean orphanEviction = true;
    private int orphanEvictionPeriod = 4;
    private boolean localKeyCache = false;
    private int localKeyCacheSize = 300000;
    private boolean isCopyOnRead = false;
    private boolean cacheXA = false;
    private boolean synchronousWrites = false;
    private int concurrency = 0;
    private NonstopConfiguration nonStopConfiguration = TerracottaConfiguration.makeDefaultNonstopConfiguration();
    private boolean copyOnReadSet;
    private Consistency consistency = DEFAULT_CONSISTENCY_TYPE;
    private volatile boolean localCacheEnabled = true;
    private volatile boolean compressionEnabled = false;

    public TerracottaConfiguration clone() {
        try {
            TerracottaConfiguration clone = (TerracottaConfiguration)super.clone();
            if (this.nonStopConfiguration != null) {
                clone.nonstop(this.nonStopConfiguration.clone());
            }
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertArgumentNotNull(String name, Object object) {
        if (object == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }

    public void setClustered(boolean clustered) {
        this.clustered = clustered;
    }

    public TerracottaConfiguration clustered(boolean clustered) {
        this.setClustered(clustered);
        return this;
    }

    public boolean isClustered() {
        return this.clustered;
    }

    @Deprecated
    public void setCopyOnRead(boolean isCopyOnRead) {
        LOG.warn("copyOnRead is deprecated on the <terracotta /> element, please use the copyOnRead attribute on <cache /> or <defaultCache />");
        this.copyOnReadSet = true;
        this.isCopyOnRead = isCopyOnRead;
    }

    public void setCompressionEnabled(boolean enabled) {
        this.compressionEnabled = enabled;
    }

    boolean isCopyOnReadSet() {
        return this.copyOnReadSet;
    }

    @Deprecated
    public TerracottaConfiguration copyOnRead(boolean isCopyOnRead) {
        this.setCopyOnRead(isCopyOnRead);
        return this;
    }

    public TerracottaConfiguration compressionEnabled(boolean enabled) {
        this.setCompressionEnabled(enabled);
        return this;
    }

    public boolean isCacheXA() {
        return this.cacheXA;
    }

    public void setCacheXA(boolean cacheXA) {
        this.cacheXA = cacheXA;
    }

    public TerracottaConfiguration cacheXA(boolean cacheXA) {
        this.setCacheXA(cacheXA);
        return this;
    }

    @Deprecated
    public boolean isCopyOnRead() {
        return this.isCopyOnRead;
    }

    public boolean isCompressionEnabled() {
        return this.compressionEnabled;
    }

    @Deprecated
    public void setCoherentReads(boolean coherentReads) {
        LOG.warn("The attribute \"coherentReads\" in \"terracotta\" element is deprecated. Please use the new \"coherent\" attribute instead.");
        this.coherentReads = coherentReads;
    }

    @Deprecated
    public TerracottaConfiguration coherentReads(boolean coherentReads) {
        this.setCoherentReads(coherentReads);
        return this;
    }

    @Deprecated
    public boolean getCoherentReads() {
        return this.coherentReads;
    }

    public void setOrphanEviction(boolean orphanEviction) {
        this.orphanEviction = orphanEviction;
    }

    public TerracottaConfiguration orphanEviction(boolean orphanEviction) {
        this.setOrphanEviction(orphanEviction);
        return this;
    }

    public boolean getOrphanEviction() {
        return this.orphanEviction;
    }

    public void setOrphanEvictionPeriod(int orphanEvictionPeriod) {
        this.orphanEvictionPeriod = orphanEvictionPeriod;
    }

    public TerracottaConfiguration orphanEvictionPeriod(int orphanEvictionPeriod) {
        this.setOrphanEvictionPeriod(orphanEvictionPeriod);
        return this;
    }

    public int getOrphanEvictionPeriod() {
        return this.orphanEvictionPeriod;
    }

    public void setLocalKeyCache(boolean localKeyCache) {
        this.localKeyCache = localKeyCache;
    }

    public TerracottaConfiguration localKeyCache(boolean localKeyCache) {
        this.setLocalKeyCache(localKeyCache);
        return this;
    }

    public boolean getLocalKeyCache() {
        return this.localKeyCache;
    }

    public void setLocalKeyCacheSize(int localKeyCacheSize) {
        this.localKeyCacheSize = localKeyCacheSize;
    }

    public TerracottaConfiguration localKeyCacheSize(int localKeyCacheSize) {
        this.setLocalKeyCacheSize(localKeyCacheSize);
        return this;
    }

    public int getLocalKeyCacheSize() {
        return this.localKeyCacheSize;
    }

    @Deprecated
    public void setCoherent(boolean coherent) {
        Consistency consistencyType = coherent ? Consistency.STRONG : Consistency.EVENTUAL;
        this.consistency(consistencyType);
    }

    @Deprecated
    public TerracottaConfiguration coherent(boolean coherent) {
        Consistency consistencyType = coherent ? Consistency.STRONG : Consistency.EVENTUAL;
        this.consistency(consistencyType);
        return this;
    }

    @Deprecated
    public boolean isCoherent() {
        return this.consistency == Consistency.STRONG;
    }

    public boolean isSynchronousWrites() {
        return this.synchronousWrites;
    }

    public void setSynchronousWrites(boolean synchronousWrites) {
        this.synchronousWrites = synchronousWrites;
    }

    public TerracottaConfiguration synchronousWrites(boolean synchronousWrites) {
        this.setSynchronousWrites(synchronousWrites);
        return this;
    }

    public TerracottaConfiguration concurrency(int concurrency) {
        this.setConcurrency(concurrency);
        return this;
    }

    public void setConcurrency(int concurrency) {
        if (concurrency < 0) {
            throw new IllegalArgumentException("Only non-negative integers allowed");
        }
        this.concurrency = concurrency;
    }

    public int getConcurrency() {
        return this.concurrency;
    }

    public void addNonstop(NonstopConfiguration nonstopConfiguration) {
        this.nonStopConfiguration = nonstopConfiguration;
    }

    public TerracottaConfiguration nonstop(NonstopConfiguration nonstopConfiguration) {
        this.addNonstop(nonstopConfiguration);
        return this;
    }

    public NonstopConfiguration getNonstopConfiguration() {
        return this.nonStopConfiguration;
    }

    public boolean isNonstopEnabled() {
        return this.nonStopConfiguration != null && this.nonStopConfiguration.isEnabled();
    }

    public TerracottaConfiguration consistency(Consistency consistency) {
        this.setConsistency(consistency);
        return this;
    }

    public void setConsistency(Consistency consistency) {
        this.consistency = consistency;
    }

    public void setConsistency(String consistency) {
        if (consistency == null) {
            throw new IllegalArgumentException("Consistency cannot be null");
        }
        this.setConsistency(Consistency.valueOf(consistency.toUpperCase()));
    }

    public Consistency getConsistency() {
        return this.consistency;
    }

    public boolean isLocalCacheEnabled() {
        return this.localCacheEnabled;
    }

    public void setLocalCacheEnabled(boolean localCacheEnabled) {
        this.localCacheEnabled = localCacheEnabled;
    }

    public TerracottaConfiguration localCacheEnabled(boolean localCacheEnabled) {
        this.setLocalCacheEnabled(localCacheEnabled);
        return this;
    }

    private static NonstopConfiguration makeDefaultNonstopConfiguration() {
        return new NonstopConfiguration().enabled(false);
    }

    public static enum Consistency {
        STRONG,
        EVENTUAL;

    }
}

