/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.framework.ServiceRegistration
 *  org.osgi.service.cm.ConfigurationAdmin
 *  org.osgi.service.cm.ConfigurationEvent
 *  org.osgi.service.cm.ConfigurationListener
 *  org.osgi.service.cm.ManagedService
 *  org.springframework.beans.BeanWrapper
 *  org.springframework.beans.PropertyAccessorFactory
 *  org.springframework.beans.PropertyEditorRegistry
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.support.AbstractBeanFactory
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import org.eclipse.gemini.blueprint.compendium.internal.cm.BeanManagedUpdate;
import org.eclipse.gemini.blueprint.compendium.internal.cm.ChainedManagedUpdate;
import org.eclipse.gemini.blueprint.compendium.internal.cm.ContainerManagedUpdate;
import org.eclipse.gemini.blueprint.compendium.internal.cm.UpdateCallback;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiServiceUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.eclipse.gemini.blueprint.util.internal.MapBasedDictionary;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.cm.ManagedService;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.util.StringUtils;

public abstract class CMUtils {
    public static void applyMapOntoInstance(Object instance, Map<String, ?> properties, AbstractBeanFactory beanFactory) {
        if (properties != null && !properties.isEmpty()) {
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess((Object)instance);
            beanWrapper.setAutoGrowNestedPaths(true);
            if (beanFactory != null) {
                beanFactory.copyRegisteredEditorsTo((PropertyEditorRegistry)beanWrapper);
            }
            for (Map.Entry<String, ?> entry : properties.entrySet()) {
                String propertyName = entry.getKey();
                if (!beanWrapper.isWritableProperty(propertyName)) continue;
                beanWrapper.setPropertyValue(propertyName, entry.getValue());
            }
        }
    }

    public static void bulkUpdate(UpdateCallback callback, Collection<?> instances, Map<?, ?> properties) {
        for (Object instance : instances) {
            callback.update(instance, properties);
        }
    }

    public static UpdateCallback createCallback(boolean autowireOnUpdate, String methodName, BeanFactory beanFactory) {
        BeanManagedUpdate beanManaged = null;
        ContainerManagedUpdate containerManaged = null;
        if (autowireOnUpdate) {
            containerManaged = new ContainerManagedUpdate(beanFactory);
        }
        if (StringUtils.hasText((String)methodName)) {
            beanManaged = new BeanManagedUpdate(methodName);
        }
        if (containerManaged != null && beanManaged != null) {
            return new ChainedManagedUpdate(new UpdateCallback[]{containerManaged, beanManaged});
        }
        return containerManaged != null ? containerManaged : beanManaged;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map getConfiguration(BundleContext bundleContext, final String pid, long initTimeout) throws IOException {
        ConfigurationAdmin cm;
        ServiceReference ref = bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
        if (ref != null && (cm = (ConfigurationAdmin)bundleContext.getService(ref)) != null) {
            Dictionary dict = cm.getConfiguration(pid).getProperties();
            if (dict != null || initTimeout == 0L) {
                return new MapBasedDictionary(dict);
            }
            final MapBasedDictionary monitor = new MapBasedDictionary();
            Hashtable<String, String> props = new Hashtable<String, String>();
            ((Dictionary)props).put("service.pid", pid);
            ServiceRegistration reg = bundleContext.registerService(ConfigurationListener.class.getName(), (Object)new ConfigurationListener(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void configurationEvent(ConfigurationEvent event) {
                    if (1 == event.getType() && pid.equals(event.getPid())) {
                        Object object = monitor;
                        synchronized (object) {
                            monitor.notify();
                        }
                    }
                }
            }, props);
            try {
                dict = cm.getConfiguration(pid).getProperties();
                if (dict != null) {
                    MapBasedDictionary mapBasedDictionary = new MapBasedDictionary(dict);
                    return mapBasedDictionary;
                }
                MapBasedDictionary mapBasedDictionary = monitor;
                synchronized (mapBasedDictionary) {
                    try {
                        monitor.wait(initTimeout);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
                mapBasedDictionary = new MapBasedDictionary(cm.getConfiguration(pid).getProperties());
                return mapBasedDictionary;
            }
            finally {
                OsgiServiceUtils.unregisterService(reg);
            }
        }
        return Collections.EMPTY_MAP;
    }

    public static ServiceRegistration registerManagedService(BundleContext bundleContext, ManagedService listener, String pid) {
        Hashtable<String, String> props = new Hashtable<String, String>();
        ((Dictionary)props).put("service.pid", pid);
        Bundle bundle = bundleContext.getBundle();
        ((Dictionary)props).put("Bundle-SymbolicName", OsgiStringUtils.nullSafeSymbolicName(bundle));
        ((Dictionary)props).put("Bundle-Version", (String)OsgiBundleUtils.getBundleVersion(bundle));
        return bundleContext.registerService(ManagedService.class.getName(), (Object)listener, props);
    }
}

