/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import java.util.LinkedList;
import java.util.List;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.Message;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.weaver.tools.GeneratedClassHandler;
import org.aspectj.weaver.tools.cache.CacheBacking;
import org.aspectj.weaver.tools.cache.CacheFactory;
import org.aspectj.weaver.tools.cache.CacheKeyResolver;
import org.aspectj.weaver.tools.cache.CacheStatistics;
import org.aspectj.weaver.tools.cache.CachedClassEntry;
import org.aspectj.weaver.tools.cache.CachedClassReference;
import org.aspectj.weaver.tools.cache.DefaultCacheFactory;
import org.aspectj.weaver.tools.cache.GeneratedCachedClassHandler;

public class WeavedClassCache {
    public static final String WEAVED_CLASS_CACHE_ENABLED = "aj.weaving.cache.enabled";
    public static final String CACHE_IMPL = "aj.weaving.cache.impl";
    private static CacheFactory DEFAULT_FACTORY = new DefaultCacheFactory();
    public static final byte[] ZERO_BYTES = new byte[0];
    private final IMessageHandler messageHandler;
    private final GeneratedCachedClassHandler cachingClassHandler;
    private final CacheBacking backing;
    private final CacheStatistics stats;
    private final CacheKeyResolver resolver;
    private final String name;
    private static final List<WeavedClassCache> cacheRegistry = new LinkedList<WeavedClassCache>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected WeavedClassCache(GeneratedClassHandler existingClassHandler, IMessageHandler messageHandler, String name, CacheBacking backing, CacheKeyResolver resolver) {
        this.resolver = resolver;
        this.name = name;
        this.backing = backing;
        this.messageHandler = messageHandler;
        this.cachingClassHandler = new GeneratedCachedClassHandler(this, existingClassHandler);
        this.stats = new CacheStatistics();
        List<WeavedClassCache> list = cacheRegistry;
        synchronized (list) {
            cacheRegistry.add(this);
        }
    }

    public static WeavedClassCache createCache(ClassLoader loader, List<String> aspects, GeneratedClassHandler existingClassHandler, IMessageHandler messageHandler) {
        CacheKeyResolver resolver = DEFAULT_FACTORY.createResolver();
        String name = resolver.createClassLoaderScope(loader, aspects);
        if (name == null) {
            return null;
        }
        CacheBacking backing = DEFAULT_FACTORY.createBacking(name);
        if (backing != null) {
            return new WeavedClassCache(existingClassHandler, messageHandler, name, backing, resolver);
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public static void setDefaultCacheFactory(CacheFactory factory) {
        DEFAULT_FACTORY = factory;
    }

    public CachedClassReference createGeneratedCacheKey(String className) {
        return this.resolver.generatedKey(className);
    }

    public CachedClassReference createCacheKey(String className, byte[] originalBytes) {
        return this.resolver.weavedKey(className, originalBytes);
    }

    public GeneratedClassHandler getCachingClassHandler() {
        return this.cachingClassHandler;
    }

    public static boolean isEnabled() {
        String enabled = System.getProperty(WEAVED_CLASS_CACHE_ENABLED);
        String impl = System.getProperty(CACHE_IMPL);
        return enabled != null && (impl == null || !"shared".equalsIgnoreCase(impl));
    }

    public void put(CachedClassReference ref, byte[] classBytes, byte[] weavedBytes) {
        CachedClassEntry.EntryType type = CachedClassEntry.EntryType.WEAVED;
        if (ref.getKey().matches(this.resolver.getGeneratedRegex())) {
            type = CachedClassEntry.EntryType.GENERATED;
        }
        this.backing.put(new CachedClassEntry(ref, weavedBytes, type), classBytes);
        this.stats.put();
    }

    public CachedClassEntry get(CachedClassReference ref, byte[] classBytes) {
        CachedClassEntry entry = this.backing.get(ref, classBytes);
        if (entry == null) {
            this.stats.miss();
        } else {
            this.stats.hit();
            if (entry.isGenerated()) {
                this.stats.generated();
            }
            if (entry.isWeaved()) {
                this.stats.weaved();
            }
            if (entry.isIgnored()) {
                this.stats.ignored();
            }
        }
        return entry;
    }

    public void ignore(CachedClassReference ref, byte[] classBytes) {
        this.stats.putIgnored();
        this.backing.put(new CachedClassEntry(ref, ZERO_BYTES, CachedClassEntry.EntryType.IGNORED), classBytes);
    }

    public void remove(CachedClassReference ref) {
        this.backing.remove(ref);
    }

    public void clear() {
        this.backing.clear();
    }

    public CacheStatistics getStats() {
        return this.stats;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<WeavedClassCache> getCaches() {
        List<WeavedClassCache> list = cacheRegistry;
        synchronized (list) {
            return new LinkedList<WeavedClassCache>(cacheRegistry);
        }
    }

    protected void error(String message, Throwable th) {
        this.messageHandler.handleMessage(new Message(message, IMessage.ERROR, th, null));
    }

    protected void error(String message) {
        MessageUtil.error(this.messageHandler, message);
    }

    protected void info(String message) {
        MessageUtil.info(message);
    }
}

