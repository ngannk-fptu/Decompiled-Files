/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.logging.Logger
 *  org.glassfish.ha.store.api.BackingStore
 *  org.glassfish.ha.store.api.BackingStoreConfiguration
 *  org.glassfish.ha.store.api.BackingStoreException
 *  org.glassfish.ha.store.api.BackingStoreFactory
 *  org.glassfish.ha.store.spi.BackingStoreFactoryRegistry
 */
package com.sun.xml.ws.api.ha;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.ha.HighAvailabilityProviderException;
import java.io.Serializable;
import org.glassfish.ha.store.api.BackingStore;
import org.glassfish.ha.store.api.BackingStoreConfiguration;
import org.glassfish.ha.store.api.BackingStoreException;
import org.glassfish.ha.store.api.BackingStoreFactory;
import org.glassfish.ha.store.spi.BackingStoreFactoryRegistry;

public enum HighAvailabilityProvider {
    INSTANCE;

    private static final Logger LOGGER;
    private volatile HaEnvironment haEnvironment = HaEnvironment.NO_HA_ENVIRONMENT;

    public void initHaEnvironment(String clusterName, String instanceName) {
        this.initHaEnvironment(clusterName, instanceName, false);
    }

    public void initHaEnvironment(String clusterName, String instanceName, boolean disableJreplica) {
        System.out.println("initHaEnvironment is called: " + clusterName + " " + instanceName);
        this.haEnvironment = HaEnvironment.getInstance(clusterName, instanceName, disableJreplica);
    }

    public boolean isDisabledJreplica() {
        return this.haEnvironment.isDisabledJreplica();
    }

    public <K extends Serializable, V extends Serializable> BackingStoreConfiguration<K, V> initBackingStoreConfiguration(String storeName, Class<K> keyClass, Class<V> valueClass) {
        HaEnvironment env = this.haEnvironment;
        return new BackingStoreConfiguration().setClusterName(env.clusterName).setInstanceName(env.getInstanceName()).setStoreName(storeName).setKeyClazz(keyClass).setValueClazz(valueClass);
    }

    public BackingStoreFactory getBackingStoreFactory(StoreType type) throws HighAvailabilityProviderException {
        if (!this.isHaEnvironmentConfigured()) {
            return this.getSafeBackingStoreFactory(StoreType.NOOP);
        }
        return this.getSafeBackingStoreFactory(type);
    }

    private BackingStoreFactory getSafeBackingStoreFactory(StoreType type) throws HighAvailabilityProviderException {
        try {
            return BackingStoreFactoryRegistry.getFactoryInstance((String)type.storeTypeId);
        }
        catch (BackingStoreException ex) {
            throw (HighAvailabilityProviderException)((Object)LOGGER.logSevereException((Throwable)((Object)new HighAvailabilityProviderException("", ex))));
        }
    }

    public boolean isHaEnvironmentConfigured() {
        return !HaEnvironment.NO_HA_ENVIRONMENT.equals(this.haEnvironment);
    }

    public <K extends Serializable, V extends Serializable> BackingStore<K, V> createBackingStore(BackingStoreFactory factory, String backingStoreName, Class<K> keyClass, Class<V> valueClass) {
        BackingStoreConfiguration<K, V> bsConfig = this.initBackingStoreConfiguration(backingStoreName, keyClass, valueClass);
        try {
            return factory.createBackingStore(bsConfig);
        }
        catch (BackingStoreException ex) {
            throw (HighAvailabilityProviderException)((Object)LOGGER.logSevereException((Throwable)((Object)new HighAvailabilityProviderException("", ex))));
        }
    }

    public static <K extends Serializable, V extends Serializable> V loadFrom(BackingStore<K, V> backingStore, K key, String version) {
        try {
            return (V)backingStore.load(key, version);
        }
        catch (BackingStoreException ex) {
            throw (HighAvailabilityProviderException)((Object)LOGGER.logSevereException((Throwable)((Object)new HighAvailabilityProviderException("", ex))));
        }
    }

    public static <K extends Serializable, V extends Serializable> String saveTo(BackingStore<K, V> backingStore, K key, V value, boolean isNew) {
        try {
            return backingStore.save(key, value, isNew);
        }
        catch (BackingStoreException ex) {
            throw (HighAvailabilityProviderException)((Object)LOGGER.logSevereException((Throwable)((Object)new HighAvailabilityProviderException("", ex))));
        }
    }

    public static <K extends Serializable, V extends Serializable> void removeFrom(BackingStore<K, V> backingStore, K key) {
        try {
            backingStore.remove(key);
        }
        catch (BackingStoreException ex) {
            throw (HighAvailabilityProviderException)((Object)LOGGER.logSevereException((Throwable)((Object)new HighAvailabilityProviderException("", ex))));
        }
    }

    public static void close(BackingStore<?, ?> backingStore) {
        try {
            backingStore.close();
        }
        catch (BackingStoreException ex) {
            throw (HighAvailabilityProviderException)((Object)LOGGER.logSevereException((Throwable)((Object)new HighAvailabilityProviderException("", ex))));
        }
    }

    public static void destroy(BackingStore<?, ?> backingStore) {
        try {
            backingStore.destroy();
        }
        catch (BackingStoreException ex) {
            throw (HighAvailabilityProviderException)((Object)LOGGER.logSevereException((Throwable)((Object)new HighAvailabilityProviderException("", ex))));
        }
    }

    public static <K extends Serializable> void removeExpired(BackingStore<K, ?> backingStore) {
        try {
            backingStore.removeExpired();
        }
        catch (BackingStoreException ex) {
            throw (HighAvailabilityProviderException)((Object)LOGGER.logSevereException((Throwable)((Object)new HighAvailabilityProviderException("", ex))));
        }
    }

    static {
        LOGGER = Logger.getLogger(HighAvailabilityProvider.class);
    }

    private static class HaEnvironment {
        public static final HaEnvironment NO_HA_ENVIRONMENT = new HaEnvironment(null, null, false);
        private final String clusterName;
        private final String instanceName;
        private final boolean disableJreplica;

        private HaEnvironment(String clusterName, String instanceName, boolean disableJreplica) {
            this.clusterName = clusterName;
            this.instanceName = instanceName;
            this.disableJreplica = disableJreplica;
        }

        public static HaEnvironment getInstance(String clusterName, String instanceName, boolean disableJreplica) {
            if (clusterName == null && instanceName == null) {
                return NO_HA_ENVIRONMENT;
            }
            return new HaEnvironment(clusterName, instanceName, disableJreplica);
        }

        public String getClusterName() {
            return this.clusterName;
        }

        public String getInstanceName() {
            return this.instanceName;
        }

        public boolean isDisabledJreplica() {
            return this.disableJreplica;
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
            HaEnvironment other = (HaEnvironment)obj;
            if (this.clusterName == null ? other.clusterName != null : !this.clusterName.equals(other.clusterName)) {
                return false;
            }
            return !(this.instanceName == null ? other.instanceName != null : !this.instanceName.equals(other.instanceName));
        }

        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + (this.clusterName != null ? this.clusterName.hashCode() : 0);
            hash = 89 * hash + (this.instanceName != null ? this.instanceName.hashCode() : 0);
            return hash;
        }
    }

    public static enum StoreType {
        IN_MEMORY("replicated"),
        NOOP("noop");

        private final String storeTypeId;

        private StoreType(String storeTypeId) {
            this.storeTypeId = storeTypeId;
        }
    }
}

