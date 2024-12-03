/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.spi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.ha.store.api.BackingStoreException;
import org.glassfish.ha.store.api.BackingStoreFactory;
import org.glassfish.ha.store.impl.NoOpBackingStoreFactory;

public final class BackingStoreFactoryRegistry {
    private static final ConcurrentHashMap<String, BackingStoreFactory> factories = new ConcurrentHashMap();
    private static final List<String> predefinedPersistenceTypes;

    public static synchronized BackingStoreFactory register(String type, BackingStoreFactory factory) {
        BackingStoreFactory oldFactory = factories.put(type, factory);
        Logger.getLogger(BackingStoreFactoryRegistry.class.getName()).log(Level.INFO, "Registered " + factory.getClass().getName() + " for persistence-type = " + type + " in BackingStoreFactoryRegistry");
        return oldFactory;
    }

    public static synchronized BackingStoreFactory getFactoryInstance(String type) throws BackingStoreException {
        BackingStoreFactory factory = factories.get(type);
        if (factory == null) {
            throw new BackingStoreException("Backing store for persistence-type " + type + " is not registered.");
        }
        return factory;
    }

    public static synchronized void unregister(String type) {
        factories.remove(type);
    }

    public static synchronized Set<String> getRegisteredTypes() {
        HashSet<String> result = new HashSet<String>(factories.keySet());
        result.addAll(predefinedPersistenceTypes);
        return result;
    }

    static {
        factories.put("noop", new NoOpBackingStoreFactory());
        predefinedPersistenceTypes = Arrays.asList("memory", "file");
    }
}

