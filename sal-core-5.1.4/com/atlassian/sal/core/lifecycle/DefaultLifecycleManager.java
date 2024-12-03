/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisablingEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkShuttingDownEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.lifecycle.LifecycleManager
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceEvent
 *  org.osgi.framework.ServiceListener
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.sal.core.lifecycle;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisablingEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginFrameworkShuttingDownEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.lifecycle.LifecycleManager;
import com.atlassian.sal.core.lifecycle.LifecycleLog;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class DefaultLifecycleManager
implements LifecycleManager,
InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DefaultLifecycleManager.class);
    private final PluginEventManager pluginEventManager;
    private final PluginAccessor pluginAccessor;
    private final BundleContext bundleContext;
    private final Set<ServiceReference<LifecycleAware>> pendingOnStart;
    private final Set<ServiceReference<LifecycleAware>> pendingOnStop;
    private final ServiceListener serviceListener;
    private boolean started;

    public DefaultLifecycleManager(PluginEventManager pluginEventManager, PluginAccessor pluginAccessor, BundleContext bundleContext) {
        this.pluginEventManager = pluginEventManager;
        this.pluginAccessor = pluginAccessor;
        this.bundleContext = bundleContext;
        this.pendingOnStart = Collections.synchronizedSet(new HashSet());
        this.pendingOnStop = Collections.synchronizedSet(new HashSet());
        this.serviceListener = new LifecycleAwareServiceListener();
        this.started = false;
    }

    public void afterPropertiesSet() throws InvalidSyntaxException {
        this.pluginEventManager.register((Object)this);
        String filter = "(objectClass=" + LifecycleAware.class.getName() + ")";
        this.bundleContext.addServiceListener(this.serviceListener, filter);
        Collection services = this.bundleContext.getServiceReferences(LifecycleAware.class, null);
        this.pendingOnStart.addAll(services);
        for (ServiceReference service : services) {
            if (null != service.getBundle()) continue;
            this.pendingOnStart.remove(service);
        }
    }

    private void clearPendingOnStop() {
        if (this.pendingOnStop.isEmpty()) {
            return;
        }
        String pluginKeys = LifecycleLog.listPluginKeys(new ArrayList(this.pendingOnStop));
        log.warn("Failed to notify with LifecycleAware.onStop(): {}", (Object)pluginKeys);
        this.pendingOnStop.clear();
    }

    public void destroy() {
        this.bundleContext.removeServiceListener(this.serviceListener);
        this.pendingOnStart.clear();
        this.clearPendingOnStop();
        this.pluginEventManager.unregister((Object)this);
    }

    @PluginEventListener
    public void onPluginFrameworkStarted(PluginFrameworkStartedEvent event) {
        this.startIfApplicationSetup();
    }

    @PluginEventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        if (this.started) {
            this.notifyStartableLifecycleAwares();
        }
    }

    @PluginEventListener
    public void onPluginFrameworkShuttingDown(PluginFrameworkShuttingDownEvent event) {
        Collection<ServiceReference<LifecycleAware>> completed = this.notifyLifecycleAwares(this.pendingOnStop, this::notifyOnStopIfEnabled);
        this.pendingOnStop.removeAll(completed);
    }

    private Predicate<ServiceReference<LifecycleAware>> notifyIfMyEvent(PluginDisablingEvent event) {
        return lifecycleAwareServiceReference -> {
            Bundle bundle = lifecycleAwareServiceReference.getBundle();
            if (bundle == null) {
                log.warn("Discarding onStop() for stale LifecycleAware");
                return true;
            }
            return OsgiHeaderUtil.getPluginKey((Bundle)bundle).equals(event.getPlugin().getKey()) && this.notifyOnStopIfEnabled((ServiceReference<LifecycleAware>)lifecycleAwareServiceReference);
        };
    }

    @PluginEventListener
    public void onPluginDisabling(PluginDisablingEvent event) {
        Collection<ServiceReference<LifecycleAware>> completed = this.notifyLifecycleAwares(this.pendingOnStop, this.notifyIfMyEvent(event));
        this.pendingOnStop.removeAll(completed);
    }

    public void start() {
        this.startIfApplicationSetup();
    }

    private void startIfApplicationSetup() {
        boolean doSetup;
        boolean bl = doSetup = !this.started && this.isApplicationSetUp();
        if (doSetup) {
            this.started = true;
            this.notifyStartableLifecycleAwares();
            this.notifyOnStart();
        }
    }

    protected void notifyOnStart() {
    }

    private void notifyStartableLifecycleAwares() {
        Collection<ServiceReference<LifecycleAware>> completed = this.notifyLifecycleAwares(this.pendingOnStart, this::notifyOnStartIfStartedAndEnabled);
        this.pendingOnStart.removeAll(completed);
        this.pendingOnStop.addAll(completed);
    }

    private Collection<ServiceReference<LifecycleAware>> notifyLifecycleAwares(Set<ServiceReference<LifecycleAware>> lifeCycleAwares, Predicate<ServiceReference<LifecycleAware>> event) {
        Object[] pending = lifeCycleAwares.toArray();
        ArrayList<ServiceReference<LifecycleAware>> completed = new ArrayList<ServiceReference<LifecycleAware>>(pending.length);
        for (Object serviceRaw : pending) {
            ServiceReference service = (ServiceReference)serviceRaw;
            if (!event.test((ServiceReference<LifecycleAware>)service)) continue;
            completed.add((ServiceReference<LifecycleAware>)service);
        }
        return completed;
    }

    private boolean notifyOnStartIfStartedAndEnabled(ServiceReference<LifecycleAware> service) {
        if (this.started) {
            return this.notifyLifecyleAware(service, new Consumer<LifecycleAware>(){

                @Override
                public void accept(LifecycleAware lifecycleAware) {
                    lifecycleAware.onStart();
                }

                public String toString() {
                    return "onStart()";
                }
            });
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean notifyLifecyleAware(ServiceReference<LifecycleAware> service, Consumer<LifecycleAware> event) {
        Bundle bundle = service.getBundle();
        LifecycleAware lifecycleAware = (LifecycleAware)this.bundleContext.getService(service);
        try {
            if (null != bundle && null != lifecycleAware) {
                String pluginKey = OsgiHeaderUtil.getPluginKey((Bundle)bundle);
                if (this.pluginAccessor.isPluginEnabled(pluginKey)) {
                    try {
                        log.debug("Calling LifecycleAware.{} '{}' from plugin '{}'", new Object[]{event, lifecycleAware, pluginKey});
                        event.accept(lifecycleAware);
                    }
                    catch (Throwable ex) {
                        log.error("LifecycleAware.{} failed for component with class '{}' from plugin '{}'", new Object[]{event, lifecycleAware.getClass().getName(), pluginKey, ex});
                    }
                    boolean bl = true;
                    return bl;
                }
                boolean bl = false;
                return bl;
            }
            log.warn("Discarding {} for stale LifecycleAware", event);
            boolean bl = true;
            return bl;
        }
        finally {
            if (null != lifecycleAware) {
                this.bundleContext.ungetService(service);
            }
        }
    }

    private boolean notifyOnStopIfEnabled(ServiceReference<LifecycleAware> service) {
        return this.notifyLifecyleAware(service, new Consumer<LifecycleAware>(){

            @Override
            public void accept(LifecycleAware lifecycleAware) {
                try {
                    lifecycleAware.onStop();
                }
                catch (AbstractMethodError e) {
                    String errorMessage = "Failed to notify with LifecycleAware.onStop()";
                    try {
                        Method onStop = lifecycleAware.getClass().getMethod("onStop", new Class[0]);
                        if (onStop.getDeclaringClass() != LifecycleAware.class) {
                            throw e;
                        }
                        log.debug("Failed to notify with LifecycleAware.onStop()", (Throwable)e);
                    }
                    catch (NoSuchMethodException e1) {
                        log.warn("Failed to notify with LifecycleAware.onStop()", (Throwable)e1);
                    }
                }
            }

            public String toString() {
                return "onStop()";
            }
        });
    }

    private class LifecycleAwareServiceListener
    implements ServiceListener {
        private LifecycleAwareServiceListener() {
        }

        public void serviceChanged(ServiceEvent serviceEvent) {
            ServiceReference service = serviceEvent.getServiceReference();
            switch (serviceEvent.getType()) {
                case 1: {
                    if (DefaultLifecycleManager.this.notifyOnStartIfStartedAndEnabled((ServiceReference<LifecycleAware>)service)) break;
                    DefaultLifecycleManager.this.pendingOnStart.add(service);
                    break;
                }
                case 4: {
                    DefaultLifecycleManager.this.pendingOnStart.remove(service);
                    if (!DefaultLifecycleManager.this.pendingOnStop.remove(service)) break;
                    log.warn("Notifying with LifecycleAware.onStop() on service unregister");
                    if (DefaultLifecycleManager.this.notifyOnStopIfEnabled((ServiceReference<LifecycleAware>)service)) break;
                    Bundle bundle = service.getBundle();
                    log.warn("Failed to notify {} with LifecycleAware.onStop()", (Object)LifecycleLog.getPluginKeyFromBundle(bundle));
                    break;
                }
            }
        }
    }
}

