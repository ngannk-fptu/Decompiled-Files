/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.tools;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Properties;
import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.ResourceClass;
import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.classes.DiscoverClasses;
import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
import org.apache.commons.discovery.tools.DefaultClassHolder;
import org.apache.commons.discovery.tools.ManagedProperties;
import org.apache.commons.discovery.tools.PropertiesHolder;
import org.apache.commons.discovery.tools.SPInterface;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DiscoverClass {
    public static final PropertiesHolder nullProperties = null;
    private final ClassLoaders classLoaders;

    public DiscoverClass() {
        this(null);
    }

    public DiscoverClass(ClassLoaders classLoaders) {
        this.classLoaders = classLoaders;
    }

    public ClassLoaders getClassLoaders(Class<?> spiClass) {
        return this.classLoaders;
    }

    public <T, S extends T> Class<S> find(Class<T> spiClass) throws DiscoveryException {
        return DiscoverClass.find(this.getClassLoaders(spiClass), new SPInterface<T>(spiClass), nullProperties, null);
    }

    public <T, S extends T> Class<S> find(Class<T> spiClass, Properties properties) throws DiscoveryException {
        return DiscoverClass.find(this.getClassLoaders(spiClass), new SPInterface<T>(spiClass), new PropertiesHolder(properties), null);
    }

    public <T, S extends T> Class<S> find(Class<T> spiClass, String defaultImpl) throws DiscoveryException {
        return DiscoverClass.find(this.getClassLoaders(spiClass), new SPInterface<T>(spiClass), nullProperties, new DefaultClassHolder(defaultImpl));
    }

    public <T, S extends T> Class<S> find(Class<T> spiClass, Properties properties, String defaultImpl) throws DiscoveryException {
        return DiscoverClass.find(this.getClassLoaders(spiClass), new SPInterface<T>(spiClass), new PropertiesHolder(properties), new DefaultClassHolder(defaultImpl));
    }

    public <T, S extends T> Class<S> find(Class<T> spiClass, String propertiesFileName, String defaultImpl) throws DiscoveryException {
        return DiscoverClass.find(this.getClassLoaders(spiClass), new SPInterface<T>(spiClass), new PropertiesHolder(propertiesFileName), new DefaultClassHolder(defaultImpl));
    }

    public static <T, S extends T> Class<S> find(ClassLoaders loaders, SPInterface<T> spi, PropertiesHolder properties, DefaultClassHolder<T> defaultImpl) throws DiscoveryException {
        if (loaders == null) {
            loaders = ClassLoaders.getLibLoaders(spi.getSPClass(), DiscoverClass.class, true);
        }
        Properties props = properties == null ? null : properties.getProperties(spi, loaders);
        String[] classNames = DiscoverClass.discoverClassNames(spi, props);
        Exception error = null;
        if (classNames.length > 0) {
            DiscoverClasses classDiscovery = new DiscoverClasses(loaders);
            for (String className : classNames) {
                ResourceClassIterator classes = classDiscovery.findResourceClasses(className);
                if (!classes.hasNext()) continue;
                ResourceClass info = classes.nextResourceClass();
                try {
                    return info.loadClass();
                }
                catch (Exception e) {
                    error = e;
                }
            }
        } else {
            ResourceNameIterator classIter = new DiscoverServiceNames(loaders).findResourceNames(spi.getSPName());
            ResourceClassIterator classes = new DiscoverClasses(loaders).findResourceClasses(classIter);
            if (!classes.hasNext() && defaultImpl != null) {
                return defaultImpl.getDefaultClass(spi, loaders);
            }
            while (classes.hasNext()) {
                ResourceClass info = classes.nextResourceClass();
                try {
                    return info.loadClass();
                }
                catch (Exception e) {
                    error = e;
                }
            }
        }
        throw new DiscoveryException("No implementation defined for " + spi.getSPName(), error);
    }

    public <T> T newInstance(Class<T> spiClass) throws DiscoveryException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return DiscoverClass.newInstance(this.getClassLoaders(spiClass), new SPInterface<T>(spiClass), nullProperties, null);
    }

    public <T> T newInstance(Class<T> spiClass, Properties properties) throws DiscoveryException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return DiscoverClass.newInstance(this.getClassLoaders(spiClass), new SPInterface<T>(spiClass), new PropertiesHolder(properties), null);
    }

    public <T> T newInstance(Class<T> spiClass, String defaultImpl) throws DiscoveryException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return DiscoverClass.newInstance(this.getClassLoaders(spiClass), new SPInterface<T>(spiClass), nullProperties, new DefaultClassHolder(defaultImpl));
    }

    public <T> T newInstance(Class<T> spiClass, Properties properties, String defaultImpl) throws DiscoveryException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return DiscoverClass.newInstance(this.getClassLoaders(spiClass), new SPInterface<T>(spiClass), new PropertiesHolder(properties), new DefaultClassHolder(defaultImpl));
    }

    public <T> T newInstance(Class<T> spiClass, String propertiesFileName, String defaultImpl) throws DiscoveryException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return DiscoverClass.newInstance(this.getClassLoaders(spiClass), new SPInterface<T>(spiClass), new PropertiesHolder(propertiesFileName), new DefaultClassHolder(defaultImpl));
    }

    public static <T> T newInstance(ClassLoaders loaders, SPInterface<T> spi, PropertiesHolder properties, DefaultClassHolder<T> defaultImpl) throws DiscoveryException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return (T)spi.newInstance(DiscoverClass.find(loaders, spi, properties, defaultImpl));
    }

    public static <T> String[] discoverClassNames(SPInterface<T> spi, Properties properties) {
        String propertyName;
        LinkedList<String> names = new LinkedList<String>();
        String spiName = spi.getSPName();
        boolean includeAltProperty = !spiName.equals(propertyName = spi.getPropertyName());
        String className = DiscoverClass.getManagedProperty(spiName);
        if (className != null) {
            names.add(className);
        }
        if (includeAltProperty && (className = DiscoverClass.getManagedProperty(propertyName)) != null) {
            names.add(className);
        }
        if (properties != null) {
            className = properties.getProperty(spiName);
            if (className != null) {
                names.add(className);
            }
            if (includeAltProperty && (className = properties.getProperty(propertyName)) != null) {
                names.add(className);
            }
        }
        String[] results = new String[names.size()];
        names.toArray(results);
        return results;
    }

    public static String getManagedProperty(String propertyName) {
        String value;
        try {
            value = ManagedProperties.getProperty(propertyName);
        }
        catch (SecurityException e) {
            value = null;
        }
        return value;
    }
}

