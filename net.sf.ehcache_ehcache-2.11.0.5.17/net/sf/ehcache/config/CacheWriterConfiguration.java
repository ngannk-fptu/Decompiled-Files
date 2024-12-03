/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import java.util.Collection;
import net.sf.ehcache.Cache;
import net.sf.ehcache.config.ConfigError;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.writer.CacheWriterManager;
import net.sf.ehcache.writer.writebehind.WriteBehindManager;
import net.sf.ehcache.writer.writethrough.WriteThroughManager;

public class CacheWriterConfiguration
implements Cloneable {
    public static final WriteMode DEFAULT_WRITE_MODE = WriteMode.WRITE_THROUGH;
    public static final boolean DEFAULT_NOTIFY_LISTENERS_ON_EXCEPTION = false;
    public static final int DEFAULT_MIN_WRITE_DELAY = 1;
    public static final int DEFAULT_MAX_WRITE_DELAY = 1;
    public static final int DEFAULT_RATE_LIMIT_PER_SECOND = 0;
    public static final boolean DEFAULT_WRITE_COALESCING = false;
    public static final boolean DEFAULT_WRITE_BATCHING = false;
    public static final int DEFAULT_WRITE_BATCH_SIZE = 1;
    public static final int DEFAULT_RETRY_ATTEMPTS = 0;
    public static final int DEFAULT_RETRY_ATTEMPT_DELAY_SECONDS = 1;
    public static final int DEFAULT_WRITE_BEHIND_CONCURRENCY = 1;
    public static final int DEFAULT_WRITE_BEHIND_MAX_QUEUE_SIZE = 0;
    private WriteMode writeMode = DEFAULT_WRITE_MODE;
    private boolean notifyListenersOnException = false;
    private int minWriteDelay = 1;
    private int maxWriteDelay = 1;
    private int rateLimitPerSecond = 0;
    private boolean writeCoalescing = false;
    private boolean writeBatching = false;
    private int writeBatchSize = 1;
    private int retryAttempts = 0;
    private int retryAttemptDelaySeconds = 1;
    private int writeBehindConcurrency = 1;
    private int writeBehindMaxQueueSize = 0;
    private CacheWriterFactoryConfiguration cacheWriterFactoryConfiguration;

    public CacheWriterConfiguration clone() {
        CacheWriterConfiguration config;
        try {
            config = (CacheWriterConfiguration)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        if (this.cacheWriterFactoryConfiguration != null) {
            config.cacheWriterFactoryConfiguration = (CacheWriterFactoryConfiguration)this.cacheWriterFactoryConfiguration.clone();
        }
        return config;
    }

    public void setWriteMode(String writeMode) {
        if (writeMode == null) {
            throw new IllegalArgumentException("WriteMode can't be null");
        }
        this.writeMode = WriteMode.valueOf(WriteMode.class, writeMode.replace('-', '_').toUpperCase());
    }

    public CacheWriterConfiguration writeMode(String writeMode) {
        this.setWriteMode(writeMode);
        return this;
    }

    public CacheWriterConfiguration writeMode(WriteMode writeMode) {
        if (null == writeMode) {
            throw new IllegalArgumentException("WriteMode can't be null");
        }
        this.writeMode = writeMode;
        return this;
    }

    public WriteMode getWriteMode() {
        return this.writeMode;
    }

    public void setNotifyListenersOnException(boolean notifyListenersOnException) {
        this.notifyListenersOnException = notifyListenersOnException;
    }

    public CacheWriterConfiguration notifyListenersOnException(boolean notifyListenersOnException) {
        this.setNotifyListenersOnException(notifyListenersOnException);
        return this;
    }

    public boolean getNotifyListenersOnException() {
        return this.notifyListenersOnException;
    }

    public void setMinWriteDelay(int minWriteDelay) {
        this.minWriteDelay = minWriteDelay < 0 ? 0 : minWriteDelay;
    }

    public CacheWriterConfiguration minWriteDelay(int minWriteDelay) {
        this.setMinWriteDelay(minWriteDelay);
        return this;
    }

    public int getMinWriteDelay() {
        return this.minWriteDelay;
    }

    public void setMaxWriteDelay(int maxWriteDelay) {
        this.maxWriteDelay = maxWriteDelay < 0 ? 0 : maxWriteDelay;
    }

    public CacheWriterConfiguration maxWriteDelay(int maxWriteDelay) {
        this.setMaxWriteDelay(maxWriteDelay);
        return this;
    }

    public int getMaxWriteDelay() {
        return this.maxWriteDelay;
    }

    public void setRateLimitPerSecond(int rateLimitPerSecond) {
        this.rateLimitPerSecond = rateLimitPerSecond < 0 ? 0 : rateLimitPerSecond;
    }

    public CacheWriterConfiguration rateLimitPerSecond(int rateLimitPerSecond) {
        this.setRateLimitPerSecond(rateLimitPerSecond);
        return this;
    }

    public int getRateLimitPerSecond() {
        return this.rateLimitPerSecond;
    }

    public void setWriteCoalescing(boolean writeCoalescing) {
        this.writeCoalescing = writeCoalescing;
    }

    public CacheWriterConfiguration writeCoalescing(boolean writeCoalescing) {
        this.setWriteCoalescing(writeCoalescing);
        return this;
    }

    public boolean getWriteCoalescing() {
        return this.writeCoalescing;
    }

    public void setWriteBatching(boolean writeBatching) {
        this.writeBatching = writeBatching;
    }

    public CacheWriterConfiguration writeBatching(boolean writeBatching) {
        this.setWriteBatching(writeBatching);
        return this;
    }

    public boolean getWriteBatching() {
        return this.writeBatching;
    }

    public void setWriteBatchSize(int writeBatchSize) {
        this.writeBatchSize = writeBatchSize < 1 ? 1 : writeBatchSize;
    }

    public CacheWriterConfiguration writeBatchSize(int writeBatchSize) {
        this.setWriteBatchSize(writeBatchSize);
        return this;
    }

    public int getWriteBatchSize() {
        return this.writeBatchSize;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts < 0 ? 0 : retryAttempts;
    }

    public CacheWriterConfiguration retryAttempts(int retryAttempts) {
        this.setRetryAttempts(retryAttempts);
        return this;
    }

    public int getRetryAttempts() {
        return this.retryAttempts;
    }

    public void setRetryAttemptDelaySeconds(int retryAttemptDelaySeconds) {
        this.retryAttemptDelaySeconds = retryAttemptDelaySeconds < 0 ? 0 : retryAttemptDelaySeconds;
    }

    public CacheWriterConfiguration retryAttemptDelaySeconds(int retryAttemptDelaySeconds) {
        this.setRetryAttemptDelaySeconds(retryAttemptDelaySeconds);
        return this;
    }

    public int getRetryAttemptDelaySeconds() {
        return this.retryAttemptDelaySeconds;
    }

    public final void addCacheWriterFactory(CacheWriterFactoryConfiguration cacheWriterFactoryConfiguration) {
        this.cacheWriterFactoryConfiguration = cacheWriterFactoryConfiguration;
    }

    public CacheWriterConfiguration cacheWriterFactory(CacheWriterFactoryConfiguration cacheWriterFactory) {
        this.addCacheWriterFactory(cacheWriterFactory);
        return this;
    }

    public CacheWriterFactoryConfiguration getCacheWriterFactoryConfiguration() {
        return this.cacheWriterFactoryConfiguration;
    }

    public void setWriteBehindConcurrency(int concurrency) {
        this.writeBehindConcurrency = concurrency < 1 ? 1 : concurrency;
    }

    public CacheWriterConfiguration writeBehindConcurrency(int concurrency) {
        this.setWriteBehindConcurrency(concurrency);
        return this;
    }

    public int getWriteBehindConcurrency() {
        return this.writeBehindConcurrency;
    }

    public void setWriteBehindMaxQueueSize(int writeBehindMaxQueueSize) {
        this.writeBehindMaxQueueSize = writeBehindMaxQueueSize < 0 ? 0 : writeBehindMaxQueueSize;
    }

    public int getWriteBehindMaxQueueSize() {
        return this.writeBehindMaxQueueSize;
    }

    public CacheWriterConfiguration writeBehindMaxQueueSize(int writeBehindMaxQueueSize) {
        this.setWriteBehindMaxQueueSize(writeBehindMaxQueueSize);
        return this;
    }

    public int hashCode() {
        int prime = 31;
        int primeTwo = 1231;
        int primeThree = 1237;
        int result = 1;
        result = 31 * result + (this.cacheWriterFactoryConfiguration == null ? 0 : this.cacheWriterFactoryConfiguration.hashCode());
        result = 31 * result + this.maxWriteDelay;
        result = 31 * result + this.minWriteDelay;
        result = 31 * result + (this.notifyListenersOnException ? 1231 : 1237);
        result = 31 * result + this.rateLimitPerSecond;
        result = 31 * result + this.retryAttemptDelaySeconds;
        result = 31 * result + this.retryAttempts;
        result = 31 * result + this.writeBatchSize;
        result = 31 * result + (this.writeBatching ? 1231 : 1237);
        result = 31 * result + (this.writeCoalescing ? 1231 : 1237);
        result = 31 * result + (this.writeMode == null ? 0 : this.writeMode.hashCode());
        result = 31 * result + this.writeBehindConcurrency;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CacheWriterConfiguration other = (CacheWriterConfiguration)obj;
        if (this.cacheWriterFactoryConfiguration == null ? other.cacheWriterFactoryConfiguration != null : !this.cacheWriterFactoryConfiguration.equals(other.cacheWriterFactoryConfiguration)) {
            return false;
        }
        if (this.maxWriteDelay != other.maxWriteDelay) {
            return false;
        }
        if (this.minWriteDelay != other.minWriteDelay) {
            return false;
        }
        if (this.notifyListenersOnException != other.notifyListenersOnException) {
            return false;
        }
        if (this.rateLimitPerSecond != other.rateLimitPerSecond) {
            return false;
        }
        if (this.retryAttemptDelaySeconds != other.retryAttemptDelaySeconds) {
            return false;
        }
        if (this.retryAttempts != other.retryAttempts) {
            return false;
        }
        if (this.writeBatchSize != other.writeBatchSize) {
            return false;
        }
        if (this.writeBatching != other.writeBatching) {
            return false;
        }
        if (this.writeCoalescing != other.writeCoalescing) {
            return false;
        }
        if (this.writeBehindConcurrency != other.writeBehindConcurrency) {
            return false;
        }
        return !(this.writeMode == null ? other.writeMode != null : !this.writeMode.equals((Object)other.writeMode));
    }

    public void validate(Collection<ConfigError> errors) {
        if (this.writeMode.equals((Object)WriteMode.WRITE_BEHIND) && !this.getWriteBatching() && this.getWriteBatchSize() != 1) {
            errors.add(new ConfigError("Configured Write Batch Size is not equal to 1 with Write Batching turned off."));
        }
    }

    public static final class CacheWriterFactoryConfiguration
    extends FactoryConfiguration<CacheWriterFactoryConfiguration> {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }

    public static enum WriteMode {
        WRITE_THROUGH{

            @Override
            public CacheWriterManager createWriterManager(Cache cache, Store store) {
                return new WriteThroughManager();
            }
        }
        ,
        WRITE_BEHIND{

            @Override
            public CacheWriterManager createWriterManager(Cache cache, Store store) {
                return new WriteBehindManager(cache, store);
            }
        };


        public abstract CacheWriterManager createWriterManager(Cache var1, Store var2);
    }
}

