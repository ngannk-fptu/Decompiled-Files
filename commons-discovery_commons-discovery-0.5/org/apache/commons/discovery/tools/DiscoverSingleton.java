/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.tools.DefaultClassHolder;
import org.apache.commons.discovery.tools.DiscoverClass;
import org.apache.commons.discovery.tools.EnvironmentCache;
import org.apache.commons.discovery.tools.PropertiesHolder;
import org.apache.commons.discovery.tools.SPInterface;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DiscoverSingleton {
    public static <T> T find(Class<T> spiClass) throws DiscoveryException {
        return DiscoverSingleton.find(null, new SPInterface<T>(spiClass), DiscoverClass.nullProperties, null);
    }

    public static <T> T find(Class<T> spiClass, Properties properties) throws DiscoveryException {
        return DiscoverSingleton.find(null, new SPInterface<T>(spiClass), new PropertiesHolder(properties), null);
    }

    public static <T> T find(Class<T> spiClass, String defaultImpl) throws DiscoveryException {
        return DiscoverSingleton.find(null, new SPInterface<T>(spiClass), DiscoverClass.nullProperties, new DefaultClassHolder(defaultImpl));
    }

    public static <T> T find(Class<T> spiClass, Properties properties, String defaultImpl) throws DiscoveryException {
        return DiscoverSingleton.find(null, new SPInterface<T>(spiClass), new PropertiesHolder(properties), new DefaultClassHolder(defaultImpl));
    }

    public static <T> T find(Class<T> spiClass, String propertiesFileName, String defaultImpl) throws DiscoveryException {
        return DiscoverSingleton.find(null, new SPInterface<T>(spiClass), new PropertiesHolder(propertiesFileName), new DefaultClassHolder(defaultImpl));
    }

    public static <T> T find(ClassLoaders loaders, SPInterface<T> spi, PropertiesHolder properties, DefaultClassHolder<T> defaultImpl) throws DiscoveryException {
        ClassLoader contextLoader = JDKHooks.getJDKHooks().getThreadContextClassLoader();
        Object obj = DiscoverSingleton.get(contextLoader, spi.getSPName());
        if (obj == null) {
            try {
                obj = DiscoverClass.newInstance(loaders, spi, properties, defaultImpl);
                if (obj != null) {
                    DiscoverSingleton.put(contextLoader, spi.getSPName(), obj);
                }
            }
            catch (DiscoveryException de) {
                throw de;
            }
            catch (Exception e) {
                throw new DiscoveryException("Unable to instantiate implementation class for " + spi.getSPName(), e);
            }
        }
        return (T)obj;
    }

    public static synchronized void release() {
        EnvironmentCache.release();
    }

    public static synchronized void release(Class<?> spiClass) {
        Map<String, Object> spis = EnvironmentCache.get(JDKHooks.getJDKHooks().getThreadContextClassLoader());
        if (spis != null) {
            spis.remove(spiClass.getName());
        }
    }

    private static synchronized Object get(ClassLoader classLoader, String spiName) {
        Map<String, Object> spis = EnvironmentCache.get(classLoader);
        if (spis != null) {
            return spis.get(spiName);
        }
        return null;
    }

    private static synchronized void put(ClassLoader classLoader, String spiName, Object service) {
        if (service != null) {
            Map<String, Object> spis = EnvironmentCache.get(classLoader);
            if (spis == null) {
                spis = new HashMap<String, Object>(13);
                EnvironmentCache.put(classLoader, spis);
            }
            spis.put(spiName, service);
        }
    }
}

