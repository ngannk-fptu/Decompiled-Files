/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.google.common.base.Throwables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.BundleListener
 *  org.osgi.framework.wiring.BundleWiring
 *  org.springframework.beans.CachedIntrospectionResults
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugin.osgi.spring;

import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.google.common.base.Throwables;
import java.lang.reflect.Field;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.wiring.BundleWiring;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class MarkBundleClassesCacheableListener
implements BundleListener,
InitializingBean,
DisposableBean {
    private final BundleContext bundleContext;
    private final PluginEventManager pluginEventManager;
    private final Object lock = new Object();
    private boolean active = Boolean.getBoolean("atlassian.enable.spring.strong.cache.bean.metadata");

    public MarkBundleClassesCacheableListener(BundleContext bundleContext, PluginEventManager pluginEventManager) {
        this.bundleContext = bundleContext;
        this.pluginEventManager = pluginEventManager;
    }

    public void afterPropertiesSet() {
        this.bundleContext.addBundleListener((BundleListener)this);
        this.pluginEventManager.register((Object)this);
        for (Bundle bundle : this.bundleContext.getBundles()) {
            if (bundle.getState() != 32) continue;
            this.maybeAcceptClassLoader(bundle);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() {
        Object object = this.lock;
        synchronized (object) {
            for (Bundle bundle : this.bundleContext.getBundles()) {
                if ((bundle.getState() & 0x30) == 0) continue;
                this.maybeClearClassLoader(bundle);
            }
            this.active = false;
        }
        this.pluginEventManager.unregister((Object)this);
        this.bundleContext.removeBundleListener((BundleListener)this);
    }

    public void bundleChanged(@Nonnull BundleEvent event) {
        switch (event.getType()) {
            case 2: {
                this.maybeAcceptClassLoader(event.getBundle());
                break;
            }
            case 4: {
                this.maybeClearClassLoader(event.getBundle());
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void maybeAcceptClassLoader(@Nonnull Bundle bundle) {
        if (bundle.getBundleId() == 0L) {
            return;
        }
        Object object = this.lock;
        synchronized (object) {
            ClassLoader bundleClassLoader = this.getBundleClassLoader(bundle);
            if (bundleClassLoader != null) {
                CachedIntrospectionResults.acceptClassLoader((ClassLoader)bundleClassLoader);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void maybeClearClassLoader(@Nonnull Bundle bundle) {
        Object object = this.lock;
        synchronized (object) {
            ClassLoader bundleClassLoader = this.getBundleClassLoader(bundle);
            if (bundleClassLoader != null) {
                CachedIntrospectionResults.clearClassLoader((ClassLoader)bundleClassLoader);
            }
        }
    }

    @Nullable
    private ClassLoader getBundleClassLoader(@Nonnull Bundle bundle) {
        BundleWiring bundleWiring;
        if (this.active && (bundleWiring = (BundleWiring)bundle.adapt(BundleWiring.class)) != null) {
            return bundleWiring.getClassLoader();
        }
        return null;
    }

    @PluginEventListener
    public void onPluginEnabled(PluginFrameworkStartedEvent event) {
        if (Boolean.getBoolean("atlassian.enable.spring.strong.cache.bean.metadata.flush")) {
            try {
                Field classCacheField = CachedIntrospectionResults.class.getDeclaredField("strongClassCache");
                classCacheField.setAccessible(true);
                Map classCache = (Map)classCacheField.get(null);
                classCache.clear();
            }
            catch (Exception e) {
                Throwables.propagate((Throwable)e);
            }
        }
    }
}

