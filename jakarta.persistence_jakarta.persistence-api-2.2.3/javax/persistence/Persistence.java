/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUtil;
import javax.persistence.spi.LoadState;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceProviderResolverHolder;

public class Persistence {
    @Deprecated
    public static final String PERSISTENCE_PROVIDER = "javax.persistence.spi.PeristenceProvider";
    @Deprecated
    protected static final Set<PersistenceProvider> providers = new HashSet<PersistenceProvider>();

    public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
        return Persistence.createEntityManagerFactory(persistenceUnitName, null);
    }

    public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map properties) {
        PersistenceProvider provider;
        EntityManagerFactory emf = null;
        PersistenceProviderResolver resolver = PersistenceProviderResolverHolder.getPersistenceProviderResolver();
        List<PersistenceProvider> providers = resolver.getPersistenceProviders();
        Iterator<PersistenceProvider> iterator = providers.iterator();
        while (iterator.hasNext() && (emf = (provider = iterator.next()).createEntityManagerFactory(persistenceUnitName, properties)) == null) {
        }
        if (emf == null) {
            throw new PersistenceException("No Persistence provider for EntityManager named " + persistenceUnitName);
        }
        return emf;
    }

    public static void generateSchema(String persistenceUnitName, Map map) {
        PersistenceProviderResolver resolver = PersistenceProviderResolverHolder.getPersistenceProviderResolver();
        List<PersistenceProvider> providers = resolver.getPersistenceProviders();
        for (PersistenceProvider provider : providers) {
            if (!provider.generateSchema(persistenceUnitName, map)) continue;
            return;
        }
        throw new PersistenceException("No Persistence provider to generate schema named " + persistenceUnitName);
    }

    public static PersistenceUtil getPersistenceUtil() {
        return new PersistenceUtilImpl();
    }

    private static class PersistenceUtilImpl
    implements PersistenceUtil {
        private PersistenceUtilImpl() {
        }

        @Override
        public boolean isLoaded(Object entity, String attributeName) {
            LoadState loadstate;
            PersistenceProviderResolver resolver = PersistenceProviderResolverHolder.getPersistenceProviderResolver();
            List<PersistenceProvider> providers = resolver.getPersistenceProviders();
            for (PersistenceProvider provider : providers) {
                loadstate = provider.getProviderUtil().isLoadedWithoutReference(entity, attributeName);
                if (loadstate == LoadState.LOADED) {
                    return true;
                }
                if (loadstate != LoadState.NOT_LOADED) continue;
                return false;
            }
            for (PersistenceProvider provider : providers) {
                loadstate = provider.getProviderUtil().isLoadedWithReference(entity, attributeName);
                if (loadstate == LoadState.LOADED) {
                    return true;
                }
                if (loadstate != LoadState.NOT_LOADED) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean isLoaded(Object entity) {
            PersistenceProviderResolver resolver = PersistenceProviderResolverHolder.getPersistenceProviderResolver();
            List<PersistenceProvider> providers = resolver.getPersistenceProviders();
            for (PersistenceProvider provider : providers) {
                LoadState loadstate = provider.getProviderUtil().isLoaded(entity);
                if (loadstate == LoadState.LOADED) {
                    return true;
                }
                if (loadstate != LoadState.NOT_LOADED) continue;
                return false;
            }
            return true;
        }
    }
}

