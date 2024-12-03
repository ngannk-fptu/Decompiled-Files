/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.refreshahead;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class RefreshAheadCacheConfiguration
implements Cloneable {
    public static final String BATCH_SIZE_KEY = "batchSize";
    public static final String NUMBER_OF_THREADS_KEY = "numberOfThreads";
    public static final String NAME_KEY = "name";
    public static final String TIME_TO_REFRESH_SECONDS_KEY = "timeToRefreshSeconds";
    public static final String MAX_BACKLOG = "maximumBacklogItems";
    public static final String EVICT_ON_LOAD_MISS = "evictOnLoadMiss";
    private static final int DEFAULT_NUMBER_THREADS = 1;
    private static final int DEFAULT_BATCHSIZE = 100;
    private static final int DEFAULT_BACKLOG_MAX = -1;
    private long timeToRefreshSeconds = Long.MAX_VALUE;
    private long timeToRefreshMillis = 0L;
    private int maximumRefreshBacklogItems = -1;
    private int batchSize = 100;
    private boolean evictOnLoadMiss = false;
    private int numberOfThreads = 1;
    private String name = null;
    private volatile boolean valid = false;

    public RefreshAheadCacheConfiguration fromProperties(Properties properties) {
        this.valid = false;
        if (properties != null) {
            for (String property : properties.stringPropertyNames()) {
                String stringValue = properties.getProperty(property).trim();
                if (TIME_TO_REFRESH_SECONDS_KEY.equals(property)) {
                    this.setTimeToRefreshSeconds(Long.parseLong(stringValue));
                    continue;
                }
                if (NAME_KEY.equals(property)) {
                    this.setName(stringValue);
                    continue;
                }
                if (NUMBER_OF_THREADS_KEY.equals(property)) {
                    this.setNumberOfThreads(Integer.parseInt(stringValue));
                    continue;
                }
                if (BATCH_SIZE_KEY.equals(property)) {
                    this.setBatchSize(Integer.parseInt(stringValue));
                    continue;
                }
                if (EVICT_ON_LOAD_MISS.equals(property)) {
                    this.setEvictOnLoadMiss(Boolean.parseBoolean(stringValue));
                    continue;
                }
                if (MAX_BACKLOG.equals(property)) {
                    this.setMaximumRefreshBacklogItems(Integer.parseInt(stringValue));
                    continue;
                }
                throw new IllegalArgumentException("Unrecognized RefreshAhead cache config key: " + property);
            }
        }
        return this.build();
    }

    public Properties toProperties() {
        Properties p = new Properties();
        p.setProperty(NAME_KEY, this.getName());
        p.setProperty(NUMBER_OF_THREADS_KEY, Long.toString(this.getNumberOfThreads()));
        p.setProperty(TIME_TO_REFRESH_SECONDS_KEY, Long.toString(this.getTimeToRefreshSeconds()));
        p.setProperty(BATCH_SIZE_KEY, Long.toString(this.getBatchSize()));
        p.setProperty(EVICT_ON_LOAD_MISS, Boolean.toString(this.isEvictOnLoadMiss()));
        p.setProperty(MAX_BACKLOG, Long.toString(this.getMaximumRefreshBacklogItems()));
        return p;
    }

    public RefreshAheadCacheConfiguration build() {
        this.validate();
        return this;
    }

    private void validate() {
        if (this.timeToRefreshSeconds <= 0L) {
            throw new IllegalStateException("Must provide >=0 timeToRefreshSeconds for refresh ahead caching");
        }
        if (this.maximumRefreshBacklogItems <= 0) {
            throw new IllegalStateException("Must provide >=0 maximumBacklogItems for refresh ahead caching");
        }
        this.valid = true;
    }

    private void checkValid() {
        if (!this.valid) {
            throw new IllegalStateException("InlineRefreshCacheConfig not built yet");
        }
    }

    public long getTimeToRefreshMillis() {
        return this.timeToRefreshMillis;
    }

    public RefreshAheadCacheConfiguration timeToRefreshSeconds(long secs) {
        this.setTimeToRefreshSeconds(secs);
        return this;
    }

    public long getTimeToRefreshSeconds() {
        this.checkValid();
        return this.timeToRefreshSeconds;
    }

    public void setTimeToRefreshSeconds(long timeToRefreshSeconds) {
        this.valid = false;
        this.timeToRefreshSeconds = timeToRefreshSeconds;
        this.timeToRefreshMillis = TimeUnit.MILLISECONDS.convert(this.timeToRefreshSeconds, TimeUnit.SECONDS);
    }

    public int getMaximumRefreshBacklogItems() {
        this.checkValid();
        return this.maximumRefreshBacklogItems;
    }

    public RefreshAheadCacheConfiguration maximumRefreshBacklogItems(int maximumRefreshBacklogItems) {
        this.setMaximumRefreshBacklogItems(maximumRefreshBacklogItems);
        return this;
    }

    public void setMaximumRefreshBacklogItems(int maximumRefreshBacklogItems) {
        this.valid = false;
        this.maximumRefreshBacklogItems = maximumRefreshBacklogItems;
    }

    public int getNumberOfThreads() {
        return this.numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.valid = false;
        this.numberOfThreads = numberOfThreads;
    }

    public RefreshAheadCacheConfiguration numberOfThreads(int numberOfThreads) {
        this.setNumberOfThreads(numberOfThreads);
        return this;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.valid = false;
        this.batchSize = batchSize;
    }

    public RefreshAheadCacheConfiguration batchSize(int batchSize) {
        this.setBatchSize(batchSize);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.valid = false;
        this.name = name;
    }

    public RefreshAheadCacheConfiguration name(String name) {
        this.setName(name);
        return this;
    }

    public boolean isEvictOnLoadMiss() {
        return this.evictOnLoadMiss;
    }

    public void setEvictOnLoadMiss(boolean loadMissEvicts) {
        this.valid = false;
        this.evictOnLoadMiss = loadMissEvicts;
    }

    public RefreshAheadCacheConfiguration evictOnLoadMiss(boolean loadMissEvicts) {
        this.setEvictOnLoadMiss(loadMissEvicts);
        return this;
    }

    public String toString() {
        return "RefreshAheadCacheConfiguration:  " + this.toProperties().toString();
    }
}

