/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.tools;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ManagedProperties {
    private static Log log = LogFactory.getLog(ManagedProperties.class);
    private static final Map<ClassLoader, Map<String, Value>> propertiesCache = new HashMap<ClassLoader, Map<String, Value>>();

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public static String getProperty(String propertyName) {
        return ManagedProperties.getProperty(ManagedProperties.getThreadContextClassLoader(), propertyName);
    }

    public static String getProperty(String propertyName, String dephault) {
        return ManagedProperties.getProperty(ManagedProperties.getThreadContextClassLoader(), propertyName, dephault);
    }

    public static String getProperty(ClassLoader classLoader, String propertyName) {
        String value = JDKHooks.getJDKHooks().getSystemProperty(propertyName);
        if (value == null) {
            Value val = ManagedProperties.getValueProperty(classLoader, propertyName);
            if (val != null) {
                value = val.value;
            }
        } else if (log.isDebugEnabled()) {
            log.debug((Object)("found System property '" + propertyName + "'" + " with value '" + value + "'."));
        }
        return value;
    }

    public static String getProperty(ClassLoader classLoader, String propertyName, String dephault) {
        String value = ManagedProperties.getProperty(classLoader, propertyName);
        return value == null ? dephault : value;
    }

    public static void setProperty(String propertyName, String value) {
        ManagedProperties.setProperty(propertyName, value, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setProperty(String propertyName, String value, boolean isDefault) {
        if (propertyName != null) {
            Map<ClassLoader, Map<String, Value>> map = propertiesCache;
            synchronized (map) {
                ClassLoader classLoader = ManagedProperties.getThreadContextClassLoader();
                Map<String, Value> properties = propertiesCache.get(classLoader);
                if (value == null) {
                    if (properties != null) {
                        properties.remove(propertyName);
                    }
                } else {
                    if (properties == null) {
                        properties = new HashMap<String, Value>();
                        propertiesCache.put(classLoader, properties);
                    }
                    properties.put(propertyName, new Value(value, isDefault));
                }
            }
        }
    }

    public static void setProperties(Map<?, ?> newProperties) {
        ManagedProperties.setProperties(newProperties, false);
    }

    public static void setProperties(Map<?, ?> newProperties, boolean isDefault) {
        for (Map.Entry<?, ?> entry : newProperties.entrySet()) {
            ManagedProperties.setProperty(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), isDefault);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Enumeration<String> propertyNames() {
        Hashtable<String, Value> allProps = new Hashtable<String, Value>();
        ClassLoader classLoader = ManagedProperties.getThreadContextClassLoader();
        while (true) {
            Map<String, Value> properties = null;
            Map<ClassLoader, Map<String, Value>> map = propertiesCache;
            synchronized (map) {
                properties = propertiesCache.get(classLoader);
            }
            if (properties != null) {
                allProps.putAll(properties);
            }
            if (classLoader == null) break;
            classLoader = ManagedProperties.getParent(classLoader);
        }
        return Collections.enumeration(allProps.keySet());
    }

    public static Properties getProperties() {
        Properties p = new Properties();
        Enumeration<String> names = ManagedProperties.propertyNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            p.put(name, ManagedProperties.getProperty(name));
        }
        return p;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final Value getValueProperty(ClassLoader classLoader, String propertyName) {
        Value value = null;
        if (propertyName != null) {
            if (classLoader != null) {
                value = ManagedProperties.getValueProperty(ManagedProperties.getParent(classLoader), propertyName);
            }
            if (value == null || value.isDefault) {
                Map<ClassLoader, Map<String, Value>> map = propertiesCache;
                synchronized (map) {
                    Value altValue;
                    Map<String, Value> properties = propertiesCache.get(classLoader);
                    if (properties != null && (altValue = properties.get(propertyName)) != null) {
                        value = altValue;
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("found Managed property '" + propertyName + "'" + " with value '" + value + "'" + " bound to classloader " + classLoader + "."));
                        }
                    }
                }
            }
        }
        return value;
    }

    private static final ClassLoader getThreadContextClassLoader() {
        return JDKHooks.getJDKHooks().getThreadContextClassLoader();
    }

    private static final ClassLoader getParent(final ClassLoader classLoader) {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                try {
                    return classLoader.getParent();
                }
                catch (SecurityException se) {
                    return null;
                }
            }
        });
    }

    private static class Value {
        final String value;
        final boolean isDefault;

        Value(String value, boolean isDefault) {
            this.value = value;
            this.isDefault = isDefault;
        }
    }
}

