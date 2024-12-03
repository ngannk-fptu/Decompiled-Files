/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.google.common.base.Function
 */
package com.atlassian.mywork.client.listener;

import com.atlassian.mywork.client.listener.ServiceListener;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.google.common.base.Function;
import java.io.Closeable;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PluginModuleServiceListener
implements ServiceListener,
LifecycleAware {
    private final PluginEventManager pluginEventManager;
    private final PluginAccessor pluginAccessor;
    private final Set<Function<Plugin, Void>> listeners = Collections.newSetFromMap(new ConcurrentHashMap());

    public PluginModuleServiceListener(PluginEventManager pluginEventManager, PluginAccessor pluginAccessor) {
        this.pluginEventManager = pluginEventManager;
        this.pluginAccessor = pluginAccessor;
    }

    public synchronized <M> Closeable addListener(final Class<M> type, final Function<M, Void> callback) {
        Function<Plugin, Void> wrappedCallback = new Function<Plugin, Void>(){

            public Void apply(Plugin plugin) {
                for (ModuleDescriptor descriptor : plugin.getModuleDescriptorsByModuleClass(type)) {
                    callback.apply(descriptor.getModule());
                }
                return null;
            }
        };
        this.listeners.add(wrappedCallback);
        for (Object module : this.pluginAccessor.getEnabledModulesByClass(type)) {
            callback.apply(module);
        }
        return new Closeable((Function)wrappedCallback){
            final /* synthetic */ Function val$wrappedCallback;
            {
                this.val$wrappedCallback = function;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void close() {
                PluginModuleServiceListener pluginModuleServiceListener = PluginModuleServiceListener.this;
                synchronized (pluginModuleServiceListener) {
                    PluginModuleServiceListener.this.listeners.remove(this.val$wrappedCallback);
                }
            }
        };
    }

    @PluginEventListener
    public void pluginEnabledEvent(PluginEnabledEvent event) {
        for (Function<Plugin, Void> listener : this.listeners) {
            listener.apply((Object)event.getPlugin());
        }
    }

    public void onStart() {
        this.pluginEventManager.register((Object)this);
    }

    public void onStop() {
        this.pluginEventManager.unregister((Object)this);
    }
}

