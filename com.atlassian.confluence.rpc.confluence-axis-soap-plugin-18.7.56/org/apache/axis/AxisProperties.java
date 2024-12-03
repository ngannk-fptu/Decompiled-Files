/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis;

import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.classes.DiscoverClasses;
import org.apache.commons.discovery.resource.names.DiscoverMappedNames;
import org.apache.commons.discovery.resource.names.DiscoverNamesInAlternateManagedProperties;
import org.apache.commons.discovery.resource.names.DiscoverNamesInManagedProperties;
import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
import org.apache.commons.discovery.resource.names.NameDiscoverers;
import org.apache.commons.discovery.tools.ClassUtils;
import org.apache.commons.discovery.tools.DefaultClassHolder;
import org.apache.commons.discovery.tools.DiscoverClass;
import org.apache.commons.discovery.tools.ManagedProperties;
import org.apache.commons.discovery.tools.SPInterface;
import org.apache.commons.logging.Log;

public class AxisProperties {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$AxisProperties == null ? (class$org$apache$axis$AxisProperties = AxisProperties.class$("org.apache.axis.AxisProperties")) : class$org$apache$axis$AxisProperties).getName());
    private static DiscoverNamesInAlternateManagedProperties altNameDiscoverer;
    private static DiscoverMappedNames mappedNames;
    private static NameDiscoverers nameDiscoverer;
    private static ClassLoaders loaders;
    static /* synthetic */ Class class$org$apache$axis$AxisProperties;

    public static void setClassOverrideProperty(Class clazz, String propertyName) {
        AxisProperties.getAlternatePropertyNameDiscoverer().addClassToPropertyNameMapping(clazz.getName(), propertyName);
    }

    public static void setClassDefault(Class clazz, String defaultName) {
        AxisProperties.getMappedNames().map(clazz.getName(), defaultName);
    }

    public static void setClassDefaults(Class clazz, String[] defaultNames) {
        AxisProperties.getMappedNames().map(clazz.getName(), defaultNames);
    }

    public static synchronized ResourceNameDiscover getNameDiscoverer() {
        if (nameDiscoverer == null) {
            nameDiscoverer = new NameDiscoverers();
            nameDiscoverer.addResourceNameDiscover(AxisProperties.getAlternatePropertyNameDiscoverer());
            nameDiscoverer.addResourceNameDiscover(new DiscoverNamesInManagedProperties());
            nameDiscoverer.addResourceNameDiscover(new DiscoverServiceNames(AxisProperties.getClassLoaders()));
            nameDiscoverer.addResourceNameDiscover(AxisProperties.getMappedNames());
        }
        return nameDiscoverer;
    }

    public static ResourceClassIterator getResourceClassIterator(Class spi) {
        ResourceNameIterator it = AxisProperties.getNameDiscoverer().findResourceNames(spi.getName());
        return new DiscoverClasses(loaders).findResourceClasses(it);
    }

    private static synchronized ClassLoaders getClassLoaders() {
        if (loaders == null) {
            loaders = ClassLoaders.getAppLoaders(class$org$apache$axis$AxisProperties == null ? (class$org$apache$axis$AxisProperties = AxisProperties.class$("org.apache.axis.AxisProperties")) : class$org$apache$axis$AxisProperties, null, true);
        }
        return loaders;
    }

    private static synchronized DiscoverMappedNames getMappedNames() {
        if (mappedNames == null) {
            mappedNames = new DiscoverMappedNames();
        }
        return mappedNames;
    }

    private static synchronized DiscoverNamesInAlternateManagedProperties getAlternatePropertyNameDiscoverer() {
        if (altNameDiscoverer == null) {
            altNameDiscoverer = new DiscoverNamesInAlternateManagedProperties();
        }
        return altNameDiscoverer;
    }

    public static Object newInstance(Class spiClass) {
        return AxisProperties.newInstance(spiClass, null, null);
    }

    public static Object newInstance(final Class spiClass, final Class[] constructorParamTypes, final Object[] constructorParams) {
        return AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                ResourceClassIterator services = AxisProperties.getResourceClassIterator(spiClass);
                Object obj = null;
                while (obj == null && services.hasNext()) {
                    Class service = services.nextResourceClass().loadClass();
                    if (service == null) continue;
                    try {
                        ClassUtils.verifyAncestory(spiClass, service);
                        obj = ClassUtils.newInstance(service, constructorParamTypes, constructorParams);
                    }
                    catch (InvocationTargetException e) {
                        if (e.getTargetException() instanceof NoClassDefFoundError) {
                            log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                            continue;
                        }
                        log.warn((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                    catch (Exception e) {
                        log.warn((Object)Messages.getMessage("exception00"), (Throwable)e);
                    }
                }
                return obj;
            }
        });
    }

    public static String getProperty(String propertyName) {
        return ManagedProperties.getProperty(propertyName);
    }

    public static String getProperty(String propertyName, String dephault) {
        return ManagedProperties.getProperty(propertyName, dephault);
    }

    public static void setProperty(String propertyName, String value) {
        ManagedProperties.setProperty(propertyName, value);
    }

    public static void setProperty(String propertyName, String value, boolean isDefault) {
        ManagedProperties.setProperty(propertyName, value, isDefault);
    }

    public static void setProperties(Map newProperties) {
        ManagedProperties.setProperties(newProperties);
    }

    public static void setProperties(Map newProperties, boolean isDefault) {
        ManagedProperties.setProperties(newProperties, isDefault);
    }

    public static Enumeration propertyNames() {
        return ManagedProperties.propertyNames();
    }

    public static Properties getProperties() {
        return ManagedProperties.getProperties();
    }

    public static Object newInstance(Class spiClass, Class defaultClass) {
        return AxisProperties.newInstance(new SPInterface(spiClass), new DefaultClassHolder(defaultClass));
    }

    private static Object newInstance(final SPInterface spi, final DefaultClassHolder defaultClass) {
        return AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    return DiscoverClass.newInstance(null, spi, null, defaultClass);
                }
                catch (Exception e) {
                    log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
                    return null;
                }
            }
        });
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

