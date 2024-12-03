/*
 * Decompiled with CFR 0.152.
 */
package javax.cache;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ServiceLoader;
import java.util.WeakHashMap;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.spi.CachingProvider;

public final class Caching {
    public static final String JAVAX_CACHE_CACHING_PROVIDER = "javax.cache.spi.CachingProvider";
    private static final CachingProviderRegistry CACHING_PROVIDERS = new CachingProviderRegistry();

    private Caching() {
    }

    public static ClassLoader getDefaultClassLoader() {
        return CACHING_PROVIDERS.getDefaultClassLoader();
    }

    public static void setDefaultClassLoader(ClassLoader classLoader) {
        CACHING_PROVIDERS.setDefaultClassLoader(classLoader);
    }

    public static CachingProvider getCachingProvider() {
        return CACHING_PROVIDERS.getCachingProvider();
    }

    public static CachingProvider getCachingProvider(ClassLoader classLoader) {
        return CACHING_PROVIDERS.getCachingProvider(classLoader);
    }

    public static Iterable<CachingProvider> getCachingProviders() {
        return CACHING_PROVIDERS.getCachingProviders();
    }

    public static Iterable<CachingProvider> getCachingProviders(ClassLoader classLoader) {
        return CACHING_PROVIDERS.getCachingProviders(classLoader);
    }

    public static CachingProvider getCachingProvider(String fullyQualifiedClassName) {
        return CACHING_PROVIDERS.getCachingProvider(fullyQualifiedClassName);
    }

    public static CachingProvider getCachingProvider(String fullyQualifiedClassName, ClassLoader classLoader) {
        return CACHING_PROVIDERS.getCachingProvider(fullyQualifiedClassName, classLoader);
    }

    public static <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        return Caching.getCachingProvider().getCacheManager().getCache(cacheName, keyType, valueType);
    }

    private static class CachingProviderRegistry {
        private WeakHashMap<ClassLoader, LinkedHashMap<String, CachingProvider>> cachingProviders = new WeakHashMap();
        private volatile ClassLoader classLoader = null;

        public ClassLoader getDefaultClassLoader() {
            ClassLoader loader = this.classLoader;
            return loader == null ? Thread.currentThread().getContextClassLoader() : loader;
        }

        public void setDefaultClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        public CachingProvider getCachingProvider() {
            return this.getCachingProvider(this.getDefaultClassLoader());
        }

        public CachingProvider getCachingProvider(ClassLoader classLoader) {
            Iterator<CachingProvider> iterator = this.getCachingProviders(classLoader).iterator();
            if (iterator.hasNext()) {
                CachingProvider provider = iterator.next();
                if (iterator.hasNext()) {
                    throw new CacheException("Multiple CachingProviders have been configured when only a single CachingProvider is expected");
                }
                return provider;
            }
            throw new CacheException("No CachingProviders have been configured");
        }

        public Iterable<CachingProvider> getCachingProviders() {
            return this.getCachingProviders(this.getDefaultClassLoader());
        }

        public synchronized Iterable<CachingProvider> getCachingProviders(ClassLoader classLoader) {
            final ClassLoader serviceClassLoader = classLoader == null ? this.getDefaultClassLoader() : classLoader;
            LinkedHashMap<String, CachingProvider> providers = this.cachingProviders.get(serviceClassLoader);
            if (providers == null) {
                if (System.getProperties().containsKey(Caching.JAVAX_CACHE_CACHING_PROVIDER)) {
                    String className = System.getProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER);
                    providers = new LinkedHashMap();
                    providers.put(className, this.loadCachingProvider(className, serviceClassLoader));
                } else {
                    providers = AccessController.doPrivileged(new PrivilegedAction<LinkedHashMap<String, CachingProvider>>(){

                        @Override
                        public LinkedHashMap<String, CachingProvider> run() {
                            LinkedHashMap<String, CachingProvider> result = new LinkedHashMap<String, CachingProvider>();
                            ServiceLoader<CachingProvider> serviceLoader = ServiceLoader.load(CachingProvider.class, serviceClassLoader);
                            for (CachingProvider provider : serviceLoader) {
                                result.put(provider.getClass().getName(), provider);
                            }
                            return result;
                        }
                    });
                }
                this.cachingProviders.put(serviceClassLoader, providers);
            }
            return providers.values();
        }

        public CachingProvider getCachingProvider(String fullyQualifiedClassName) {
            return this.getCachingProvider(fullyQualifiedClassName, this.getDefaultClassLoader());
        }

        protected CachingProvider loadCachingProvider(String fullyQualifiedClassName, ClassLoader classLoader) throws CacheException {
            ClassLoader classLoader2 = classLoader;
            synchronized (classLoader2) {
                try {
                    Class<?> clazz = classLoader.loadClass(fullyQualifiedClassName);
                    if (CachingProvider.class.isAssignableFrom(clazz)) {
                        return (CachingProvider)clazz.newInstance();
                    }
                    throw new CacheException("The specified class [" + fullyQualifiedClassName + "] is not a CachingProvider");
                }
                catch (Exception e) {
                    throw new CacheException("Failed to load the CachingProvider [" + fullyQualifiedClassName + "]", e);
                }
            }
        }

        public synchronized CachingProvider getCachingProvider(String fullyQualifiedClassName, ClassLoader classLoader) {
            CachingProvider provider;
            ClassLoader serviceClassLoader = classLoader == null ? this.getDefaultClassLoader() : classLoader;
            LinkedHashMap<String, CachingProvider> providers = this.cachingProviders.get(serviceClassLoader);
            if (providers == null) {
                this.getCachingProviders(serviceClassLoader);
                providers = this.cachingProviders.get(serviceClassLoader);
            }
            if ((provider = providers.get(fullyQualifiedClassName)) == null) {
                provider = this.loadCachingProvider(fullyQualifiedClassName, serviceClassLoader);
                providers.put(fullyQualifiedClassName, provider);
            }
            return provider;
        }
    }
}

