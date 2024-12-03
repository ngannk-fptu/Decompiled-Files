/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener
 *  org.eclipse.gemini.blueprint.service.importer.support.Availability
 *  org.eclipse.gemini.blueprint.service.importer.support.CollectionType
 *  org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean
 *  org.eclipse.gemini.blueprint.util.BundleDelegatingClassLoader
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.service.blueprint.container.BlueprintEvent
 *  org.osgi.service.blueprint.container.BlueprintListener
 *  org.springframework.beans.factory.DisposableBean
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.activator;

import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.ReplayEventManager;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.eclipse.gemini.blueprint.service.importer.support.Availability;
import org.eclipse.gemini.blueprint.service.importer.support.CollectionType;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean;
import org.eclipse.gemini.blueprint.util.BundleDelegatingClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintEvent;
import org.osgi.service.blueprint.container.BlueprintListener;
import org.springframework.beans.factory.DisposableBean;

class BlueprintListenerManager
implements BlueprintListener,
DisposableBean {
    private static final Log log = LogFactory.getLog(BlueprintListenerManager.class);
    private volatile DisposableBean cleanupHook;
    private volatile List<BlueprintListener> listeners;
    private volatile ReplayEventManager replayManager;

    public BlueprintListenerManager(BundleContext context) {
        this.replayManager = new ReplayEventManager(context);
        OsgiServiceCollectionProxyFactoryBean fb = new OsgiServiceCollectionProxyFactoryBean();
        fb.setBundleContext(context);
        fb.setAvailability(Availability.OPTIONAL);
        fb.setCollectionType(CollectionType.LIST);
        fb.setInterfaces(new Class[]{BlueprintListener.class});
        fb.setBeanClassLoader((ClassLoader)BundleDelegatingClassLoader.createBundleClassLoaderFor((Bundle)context.getBundle()));
        fb.setListeners(new OsgiServiceLifecycleListener[]{new RegistrationReplayDelivery()});
        fb.afterPropertiesSet();
        this.cleanupHook = fb;
        this.listeners = (List)fb.getObject();
    }

    public void destroy() {
        this.replayManager.destroy();
        if (this.cleanupHook != null) {
            try {
                this.cleanupHook.destroy();
            }
            catch (Exception ex) {
                log.warn((Object)"Cannot destroy listeners collection", (Throwable)ex);
            }
            this.cleanupHook = null;
        }
    }

    public void blueprintEvent(BlueprintEvent event) {
        this.replayManager.addEvent(event);
        for (BlueprintListener listener : this.listeners) {
            try {
                listener.blueprintEvent(event);
            }
            catch (Exception ex) {
                log.warn((Object)("exception encountered when calling listener " + System.identityHashCode(listener)), (Throwable)ex);
            }
        }
    }

    private class RegistrationReplayDelivery
    implements OsgiServiceLifecycleListener {
        private RegistrationReplayDelivery() {
        }

        public void bind(Object service, Map properties) throws Exception {
            BlueprintListener listener = (BlueprintListener)service;
            BlueprintListenerManager.this.replayManager.dispatchReplayEvents(listener);
        }

        public void unbind(Object service, Map properties) throws Exception {
        }
    }
}

