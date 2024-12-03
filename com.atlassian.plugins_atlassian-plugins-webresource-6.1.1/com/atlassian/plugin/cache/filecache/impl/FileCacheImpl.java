/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.cache.RemovalListener
 *  com.google.common.cache.RemovalNotification
 */
package com.atlassian.plugin.cache.filecache.impl;

import com.atlassian.plugin.cache.filecache.Cache;
import com.atlassian.plugin.cache.filecache.impl.OneStreamCache;
import com.atlassian.plugin.cache.filecache.impl.StreamsCache;
import com.atlassian.plugin.cache.filecache.impl.TwoStreamsCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class FileCacheImpl
implements Cache {
    protected static final String EXT = ".cachedfile";
    private static final String ONE_STREAM_KEY_PREFIX = "onestreamkeyprefix:";
    private static final String TWO_STREAMS_KEY_PREFIX = "twostreamskeyprefix:";
    private static final AtomicLong filenameCounter = new AtomicLong(0L);
    private final LoadingCache<String, StreamsCache> cache;
    private final File tmpDir;

    public FileCacheImpl(File tmpDir, int maxSize) throws IOException {
        if (maxSize < 0) {
            throw new IllegalArgumentException("Max files can not be less than zero");
        }
        FileCacheImpl.initDirectory(tmpDir);
        this.tmpDir = tmpDir;
        this.cache = CacheBuilder.newBuilder().maximumSize((long)maxSize).removalListener((RemovalListener)new RemovalListener<String, StreamsCache>(){

            public void onRemoval(RemovalNotification<String, StreamsCache> notification) {
                FileCacheImpl.this.onEviction((StreamsCache)notification.getValue());
            }
        }).build((CacheLoader)new CacheLoader<String, StreamsCache>(){

            public StreamsCache load(String key) throws Exception {
                return FileCacheImpl.this.newCachedFile(key);
            }
        });
    }

    private static void initDirectory(File tmpDir) throws IOException {
        tmpDir.mkdirs();
        File[] files = tmpDir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.getName().toLowerCase().endsWith(EXT) || f.delete()) continue;
                throw new IOException("Could not delete existing cache file " + f);
            }
        }
        if (!tmpDir.isDirectory()) {
            throw new IOException("Could not create tmp directory " + tmpDir);
        }
    }

    public static OutputStream ensureNotNull(OutputStream stream) {
        if (stream == null) {
            return new OutputStream(){

                @Override
                public void write(int b) throws IOException {
                }
            };
        }
        return stream;
    }

    @Override
    public boolean cache(String bucket, String key, OutputStream out, Cache.StreamProvider provider) {
        String cacheKey = ONE_STREAM_KEY_PREFIX + bucket + ":" + key;
        boolean cacheHit = this.cache.asMap().containsKey(cacheKey);
        StreamsCache cachedFile = (StreamsCache)this.cache.getUnchecked((Object)cacheKey);
        cachedFile.stream(out, provider);
        return cacheHit;
    }

    @Override
    public boolean cacheTwo(String bucket, String key, OutputStream out1, OutputStream out2, Cache.TwoStreamProvider provider) {
        String cacheKey = TWO_STREAMS_KEY_PREFIX + bucket + ":" + key;
        boolean cacheHit = this.cache.asMap().containsKey(cacheKey);
        StreamsCache cachedFile = (StreamsCache)this.cache.getUnchecked((Object)cacheKey);
        cachedFile.streamTwo(FileCacheImpl.ensureNotNull(out1), FileCacheImpl.ensureNotNull(out2), provider);
        return cacheHit;
    }

    @Override
    public void clear() {
        ArrayList cachedFiles = new ArrayList(this.cache.asMap().values());
        this.cache.invalidateAll();
        for (StreamsCache cachedFile : cachedFiles) {
            cachedFile.deleteWhenPossible();
        }
    }

    private StreamsCache newCachedFile(String key) {
        if (key.startsWith(ONE_STREAM_KEY_PREFIX)) {
            File file = new File(this.tmpDir, this.generateNextFileName());
            return new OneStreamCache(file);
        }
        if (key.startsWith(TWO_STREAMS_KEY_PREFIX)) {
            File file1 = new File(this.tmpDir, this.generateNextFileName());
            File file2 = new File(this.tmpDir, this.generateNextFileName());
            return new TwoStreamsCache(file1, file2);
        }
        throw new RuntimeException("internal error, invalid cache key '" + key + "' prefix!");
    }

    private void onEviction(StreamsCache node) {
        node.deleteWhenPossible();
    }

    protected String generateNextFileName() {
        return filenameCounter.incrementAndGet() + EXT;
    }
}

