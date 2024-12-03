/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.spi;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;

public class PersistenceProviderResolverHolder {
    private static PersistenceProviderResolver singleton = new DefaultPersistenceProviderResolver();

    public static PersistenceProviderResolver getPersistenceProviderResolver() {
        return singleton;
    }

    public static void setPersistenceProviderResolver(PersistenceProviderResolver resolver) {
        singleton = resolver == null ? new DefaultPersistenceProviderResolver() : resolver;
    }

    private static class DefaultPersistenceProviderResolver
    implements PersistenceProviderResolver {
        private volatile HashMap<CacheKey, PersistenceProviderReference> providers = new HashMap();
        private static final ReferenceQueue referenceQueue = new ReferenceQueue();
        private static final String LOGGER_SUBSYSTEM = "javax.persistence.spi";
        private Logger logger;

        private DefaultPersistenceProviderResolver() {
        }

        @Override
        public List<PersistenceProvider> getPersistenceProviders() {
            this.processQueue();
            ClassLoader loader = DefaultPersistenceProviderResolver.getContextClassLoader();
            CacheKey cacheKey = new CacheKey(loader);
            PersistenceProviderReference providersReferent = this.providers.get(cacheKey);
            ArrayList<PersistenceProvider> loadedProviders = null;
            if (providersReferent != null) {
                loadedProviders = (ArrayList<PersistenceProvider>)providersReferent.get();
            }
            if (loadedProviders == null) {
                loadedProviders = new ArrayList<PersistenceProvider>();
                Iterator<PersistenceProvider> ipp = ServiceLoader.load(PersistenceProvider.class, loader).iterator();
                try {
                    while (ipp.hasNext()) {
                        try {
                            PersistenceProvider pp = ipp.next();
                            loadedProviders.add(pp);
                        }
                        catch (ServiceConfigurationError sce) {
                            this.log(Level.FINEST, sce.toString());
                        }
                    }
                }
                catch (ServiceConfigurationError sce) {
                    this.log(Level.FINEST, sce.toString());
                }
                if (loadedProviders.isEmpty()) {
                    this.log(Level.WARNING, "No valid providers found.");
                }
                providersReferent = new PersistenceProviderReference(loadedProviders, referenceQueue, cacheKey);
                this.providers.put(cacheKey, providersReferent);
            }
            return loadedProviders;
        }

        private void processQueue() {
            CacheKeyReference ref;
            while ((ref = (CacheKeyReference)((Object)referenceQueue.poll())) != null) {
                this.providers.remove(ref.getCacheKey());
            }
        }

        private static ClassLoader getContextClassLoader() {
            if (System.getSecurityManager() == null) {
                return Thread.currentThread().getContextClassLoader();
            }
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

                @Override
                public ClassLoader run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
        }

        private void log(Level level, String message) {
            if (this.logger == null) {
                this.logger = Logger.getLogger(LOGGER_SUBSYSTEM);
            }
            this.logger.log(level, "javax.persistence.spi::" + message);
        }

        @Override
        public void clearCachedProviders() {
            this.providers.clear();
        }

        private class PersistenceProviderReference
        extends SoftReference<List<PersistenceProvider>>
        implements CacheKeyReference {
            private CacheKey cacheKey;

            PersistenceProviderReference(List<PersistenceProvider> referent, ReferenceQueue q, CacheKey key) {
                super(referent, q);
                this.cacheKey = key;
            }

            @Override
            public CacheKey getCacheKey() {
                return this.cacheKey;
            }
        }

        private class LoaderReference
        extends WeakReference<ClassLoader>
        implements CacheKeyReference {
            private CacheKey cacheKey;

            LoaderReference(ClassLoader referent, ReferenceQueue q, CacheKey key) {
                super(referent, q);
                this.cacheKey = key;
            }

            @Override
            public CacheKey getCacheKey() {
                return this.cacheKey;
            }
        }

        private class CacheKey
        implements Cloneable {
            private LoaderReference loaderRef;
            private int hashCodeCache;

            CacheKey(ClassLoader loader) {
                this.loaderRef = loader == null ? null : new LoaderReference(loader, referenceQueue, this);
                this.calculateHashCode();
            }

            ClassLoader getLoader() {
                return this.loaderRef != null ? (ClassLoader)this.loaderRef.get() : null;
            }

            public boolean equals(Object other) {
                if (this == other) {
                    return true;
                }
                try {
                    CacheKey otherEntry = (CacheKey)other;
                    if (this.hashCodeCache != otherEntry.hashCodeCache) {
                        return false;
                    }
                    if (this.loaderRef == null) {
                        return otherEntry.loaderRef == null;
                    }
                    ClassLoader loader = (ClassLoader)this.loaderRef.get();
                    return otherEntry.loaderRef != null && loader != null && loader == otherEntry.loaderRef.get();
                }
                catch (NullPointerException nullPointerException) {
                }
                catch (ClassCastException classCastException) {
                    // empty catch block
                }
                return false;
            }

            public int hashCode() {
                return this.hashCodeCache;
            }

            private void calculateHashCode() {
                ClassLoader loader = this.getLoader();
                if (loader != null) {
                    this.hashCodeCache = loader.hashCode();
                }
            }

            public Object clone() {
                try {
                    CacheKey clone = (CacheKey)super.clone();
                    if (this.loaderRef != null) {
                        clone.loaderRef = new LoaderReference((ClassLoader)this.loaderRef.get(), referenceQueue, clone);
                    }
                    return clone;
                }
                catch (CloneNotSupportedException e) {
                    throw new InternalError();
                }
            }

            public String toString() {
                return "CacheKey[" + this.getLoader() + ")]";
            }
        }

        private static interface CacheKeyReference {
            public CacheKey getCacheKey();
        }
    }
}

