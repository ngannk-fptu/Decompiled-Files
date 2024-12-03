/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import org.apache.http.client.cache.HttpCacheInvalidator;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.ResourceFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.cache.AsynchronousValidator;
import org.apache.http.impl.client.cache.BasicHttpCache;
import org.apache.http.impl.client.cache.BasicHttpCacheStorage;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CacheInvalidator;
import org.apache.http.impl.client.cache.CacheKeyGenerator;
import org.apache.http.impl.client.cache.CachingExec;
import org.apache.http.impl.client.cache.FileResourceFactory;
import org.apache.http.impl.client.cache.HeapResourceFactory;
import org.apache.http.impl.client.cache.ImmediateSchedulingStrategy;
import org.apache.http.impl.client.cache.ManagedHttpCacheStorage;
import org.apache.http.impl.client.cache.SchedulingStrategy;
import org.apache.http.impl.execchain.ClientExecChain;

public class CachingHttpClientBuilder
extends HttpClientBuilder {
    private ResourceFactory resourceFactory;
    private HttpCacheStorage storage;
    private File cacheDir;
    private CacheConfig cacheConfig;
    private SchedulingStrategy schedulingStrategy;
    private HttpCacheInvalidator httpCacheInvalidator;
    private boolean deleteCache = true;

    public static CachingHttpClientBuilder create() {
        return new CachingHttpClientBuilder();
    }

    protected CachingHttpClientBuilder() {
    }

    public final CachingHttpClientBuilder setResourceFactory(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
        return this;
    }

    public final CachingHttpClientBuilder setHttpCacheStorage(HttpCacheStorage storage) {
        this.storage = storage;
        return this;
    }

    public final CachingHttpClientBuilder setCacheDir(File cacheDir) {
        this.cacheDir = cacheDir;
        return this;
    }

    public final CachingHttpClientBuilder setCacheConfig(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
        return this;
    }

    public final CachingHttpClientBuilder setSchedulingStrategy(SchedulingStrategy schedulingStrategy) {
        this.schedulingStrategy = schedulingStrategy;
        return this;
    }

    public final CachingHttpClientBuilder setHttpCacheInvalidator(HttpCacheInvalidator cacheInvalidator) {
        this.httpCacheInvalidator = cacheInvalidator;
        return this;
    }

    public CachingHttpClientBuilder setDeleteCache(boolean deleteCache) {
        this.deleteCache = deleteCache;
        return this;
    }

    @Override
    protected ClientExecChain decorateMainExec(ClientExecChain mainExec) {
        HttpCacheStorage storageCopy;
        CacheConfig config = this.cacheConfig != null ? this.cacheConfig : CacheConfig.DEFAULT;
        ResourceFactory resourceFactoryCopy = this.resourceFactory;
        if (resourceFactoryCopy == null) {
            resourceFactoryCopy = this.cacheDir == null ? new HeapResourceFactory() : new FileResourceFactory(this.cacheDir);
        }
        if ((storageCopy = this.storage) == null) {
            if (this.cacheDir == null) {
                storageCopy = new BasicHttpCacheStorage(config);
            } else {
                final ManagedHttpCacheStorage managedStorage = new ManagedHttpCacheStorage(config);
                if (this.deleteCache) {
                    this.addCloseable(new Closeable(){

                        @Override
                        public void close() throws IOException {
                            managedStorage.shutdown();
                        }
                    });
                } else {
                    this.addCloseable(managedStorage);
                }
                storageCopy = managedStorage;
            }
        }
        AsynchronousValidator revalidator = this.createAsynchronousRevalidator(config);
        CacheKeyGenerator uriExtractor = new CacheKeyGenerator();
        HttpCacheInvalidator cacheInvalidator = this.httpCacheInvalidator;
        if (cacheInvalidator == null) {
            cacheInvalidator = new CacheInvalidator(uriExtractor, storageCopy);
        }
        return new CachingExec(mainExec, new BasicHttpCache(resourceFactoryCopy, storageCopy, config, uriExtractor, cacheInvalidator), config, revalidator);
    }

    private AsynchronousValidator createAsynchronousRevalidator(CacheConfig config) {
        if (config.getAsynchronousWorkersMax() > 0) {
            SchedulingStrategy configuredSchedulingStrategy = this.createSchedulingStrategy(config);
            AsynchronousValidator revalidator = new AsynchronousValidator(configuredSchedulingStrategy);
            this.addCloseable(revalidator);
            return revalidator;
        }
        return null;
    }

    private SchedulingStrategy createSchedulingStrategy(CacheConfig config) {
        return this.schedulingStrategy != null ? this.schedulingStrategy : new ImmediateSchedulingStrategy(config);
    }
}

