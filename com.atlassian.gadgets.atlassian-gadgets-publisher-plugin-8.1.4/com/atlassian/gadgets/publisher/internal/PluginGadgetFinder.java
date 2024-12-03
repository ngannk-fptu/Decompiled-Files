/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.plugins.PluginGadgetSpec
 *  com.atlassian.gadgets.plugins.PluginGadgetSpecEventListener
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker$Customizer
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.gadgets.publisher.internal;

import com.atlassian.gadgets.plugins.PluginGadgetSpec;
import com.atlassian.gadgets.plugins.PluginGadgetSpecEventListener;
import com.atlassian.gadgets.publisher.GadgetModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class PluginGadgetFinder
implements DisposableBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Iterable<PluginGadgetSpecEventListener> eventListeners;
    private final PluginModuleTracker<PluginGadgetSpec, GadgetModuleDescriptor> gadgetTracker;

    public PluginGadgetFinder(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport PluginEventManager pluginEventManager, Iterable<PluginGadgetSpecEventListener> eventListeners) {
        Preconditions.checkNotNull((Object)pluginEventManager, (Object)"pluginEventManager");
        Preconditions.checkNotNull((Object)pluginAccessor, (Object)"pluginAccessor");
        this.eventListeners = (Iterable)Preconditions.checkNotNull(eventListeners, (Object)"eventListener");
        this.gadgetTracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, GadgetModuleDescriptor.class, this.getCustomizer());
    }

    private PluginModuleTracker.Customizer<PluginGadgetSpec, GadgetModuleDescriptor> getCustomizer() {
        return new PluginModuleTracker.Customizer<PluginGadgetSpec, GadgetModuleDescriptor>(){

            public GadgetModuleDescriptor adding(GadgetModuleDescriptor gadgetModuleDescriptor) {
                PluginGadgetFinder.this.installGadget(gadgetModuleDescriptor.getModule());
                return gadgetModuleDescriptor;
            }

            public void removed(GadgetModuleDescriptor gadgetModuleDescriptor) {
                PluginGadgetFinder.this.removeGadget(gadgetModuleDescriptor.getModule());
            }
        };
    }

    private void installGadget(PluginGadgetSpec gadgetSpec) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Publishing gadget spec " + gadgetSpec);
        }
        for (PluginGadgetSpecEventListener eventListener : this.eventListeners) {
            this.installGadget(eventListener, gadgetSpec);
        }
    }

    private void installGadget(PluginGadgetSpecEventListener eventListener, PluginGadgetSpec gadgetSpec) {
        try {
            eventListener.pluginGadgetSpecEnabled(gadgetSpec);
        }
        catch (RuntimeException e) {
            this.warn("Gadget spec " + gadgetSpec + " could not be added to " + eventListener + ", ignoring", e);
        }
    }

    private void removeGadget(PluginGadgetSpec gadgetSpec) {
        for (PluginGadgetSpecEventListener eventListener : this.eventListeners) {
            this.removeGadget(eventListener, gadgetSpec);
        }
    }

    private void removeGadget(PluginGadgetSpecEventListener eventListener, PluginGadgetSpec gadgetSpec) {
        try {
            eventListener.pluginGadgetSpecDisabled(gadgetSpec);
        }
        catch (RuntimeException e) {
            this.warn("Gadget spec " + gadgetSpec + " could not be removed from " + eventListener + ", ignoring", e);
        }
    }

    private void warn(String message, Throwable t) {
        if (this.log.isDebugEnabled()) {
            this.log.warn(message, t);
        } else {
            this.log.warn(message);
        }
    }

    public void destroy() throws Exception {
        this.gadgetTracker.close();
    }
}

