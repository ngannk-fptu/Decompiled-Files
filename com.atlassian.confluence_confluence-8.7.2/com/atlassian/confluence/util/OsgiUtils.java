/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.confluence.util;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import java.util.Optional;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class OsgiUtils {
    private static final Logger log = LoggerFactory.getLogger((String)OsgiUtils.class.getName());

    public static Optional<Object> findApplicationContextInOsgiBundle(Plugin plugin) {
        if (!(plugin instanceof OsgiPlugin)) {
            log.error("Unsupported plugin type for plugin: {}. Unable to access OSGi bundle info", (Object)plugin);
            return Optional.empty();
        }
        OsgiPlugin osgiPlugin = (OsgiPlugin)plugin;
        String referencingBundleName = osgiPlugin.getBundle().getSymbolicName();
        BundleContext bundleContext = osgiPlugin.getBundle().getBundleContext();
        try {
            String filterString = "(&(objectClass=" + ApplicationContext.class.getName() + ")(Bundle-SymbolicName=" + referencingBundleName + "))";
            Filter filter = bundleContext.createFilter(filterString);
            ServiceReference[] serviceReferences = bundleContext.getServiceReferences(ApplicationContext.class.getName(), filter.toString());
            if (serviceReferences == null || serviceReferences.length == 0) {
                log.error("No ApplicationContext found for plugin: {}. Unable to access OSGi bundle info", (Object)plugin);
                return Optional.empty();
            }
            return Optional.of(bundleContext.getService(serviceReferences[0]));
        }
        catch (InvalidSyntaxException e) {
            throw new RuntimeException("Unexpected issue with OSGi filter expression", e);
        }
    }
}

