/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.util.OsgiPlatformDetector
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.springframework.util.ClassUtils
 */
package org.eclipse.gemini.blueprint.extender.internal.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.internal.activator.BlueprintNamespaceHandlerActivator;
import org.eclipse.gemini.blueprint.extender.internal.activator.ContextLoaderListener;
import org.eclipse.gemini.blueprint.extender.internal.activator.JavaBeansCacheActivator;
import org.eclipse.gemini.blueprint.extender.internal.activator.ListenerServiceActivator;
import org.eclipse.gemini.blueprint.extender.internal.activator.LoggingActivator;
import org.eclipse.gemini.blueprint.extender.internal.activator.NamespaceHandlerActivator;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.BlueprintLoaderListener;
import org.eclipse.gemini.blueprint.extender.internal.support.ExtenderConfiguration;
import org.eclipse.gemini.blueprint.util.OsgiPlatformDetector;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.util.ClassUtils;

public class ChainActivator
implements BundleActivator {
    protected final Log log = LogFactory.getLog(this.getClass());
    private static final boolean BLUEPRINT_AVAILABLE = ClassUtils.isPresent((String)"org.osgi.service.blueprint.container.BlueprintContainer", (ClassLoader)ChainActivator.class.getClassLoader());
    private final BundleActivator[] CHAIN;

    public ChainActivator() {
        LoggingActivator logStatus = new LoggingActivator();
        JavaBeansCacheActivator activateJavaBeansCache = new JavaBeansCacheActivator();
        NamespaceHandlerActivator activateCustomNamespaceHandling = new NamespaceHandlerActivator();
        BlueprintNamespaceHandlerActivator activateBlueprintspecificNamespaceHandling = new BlueprintNamespaceHandlerActivator();
        ExtenderConfiguration initializeExtenderConfiguration = new ExtenderConfiguration();
        ListenerServiceActivator activateListeners = new ListenerServiceActivator(initializeExtenderConfiguration);
        ContextLoaderListener listenForSpringDmBundles = new ContextLoaderListener(initializeExtenderConfiguration);
        BlueprintLoaderListener listenForBlueprintBundles = new BlueprintLoaderListener(initializeExtenderConfiguration, activateListeners);
        if (OsgiPlatformDetector.isR42()) {
            if (BLUEPRINT_AVAILABLE) {
                this.log.info((Object)"Blueprint API detected; enabling Blueprint Container functionality");
                this.CHAIN = new BundleActivator[]{logStatus, activateJavaBeansCache, activateCustomNamespaceHandling, activateBlueprintspecificNamespaceHandling, initializeExtenderConfiguration, activateListeners, listenForSpringDmBundles, listenForBlueprintBundles};
            } else {
                this.log.warn((Object)"Blueprint API not found; disabling Blueprint Container functionality");
                this.CHAIN = new BundleActivator[]{logStatus, activateJavaBeansCache, activateCustomNamespaceHandling, initializeExtenderConfiguration, activateListeners, listenForSpringDmBundles};
            }
        } else {
            this.log.warn((Object)"Pre-4.2 OSGi platform detected; disabling Blueprint Container functionality");
            this.CHAIN = new BundleActivator[]{logStatus, activateJavaBeansCache, activateCustomNamespaceHandling, initializeExtenderConfiguration, activateListeners, listenForSpringDmBundles};
        }
    }

    public void start(BundleContext context) throws Exception {
        for (int i = 0; i < this.CHAIN.length; ++i) {
            this.CHAIN[i].start(context);
        }
    }

    public void stop(BundleContext context) throws Exception {
        for (int i = this.CHAIN.length - 1; i >= 0; --i) {
            this.CHAIN[i].stop(context);
        }
    }
}

