/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  com.atlassian.config.lifecycle.ServletContextListenerWrapper
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.ServletContextListener
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.config.lifecycle.ServletContextListenerWrapper;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.annotations.VisibleForTesting;
import javax.servlet.ServletContextListener;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class LifecyclePluginModuleDescriptor
extends AbstractModuleDescriptor
implements Comparable<LifecyclePluginModuleDescriptor> {
    private Object module;
    private int sequence;

    public LifecyclePluginModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.sequence = this.determineSequenceNumber(element);
    }

    private void ensureCompatibleModuleType() throws PluginParseException {
        Class moduleClass = this.getModuleClass();
        if (!LifecycleItem.class.isAssignableFrom(moduleClass) && !ServletContextListener.class.isAssignableFrom(moduleClass)) {
            throw new PluginParseException("Lifecycle classes must extend LifecycleItem or ServletContextListener. Module class: " + moduleClass.getName());
        }
    }

    private int determineSequenceNumber(Element element) throws PluginParseException {
        Attribute att = element.attribute("sequence");
        if (att != null) {
            String value = att.getValue();
            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                throw new PluginParseException("Could not determine sequence from: " + value);
            }
        }
        throw new PluginParseException("Missing required attribute: sequence");
    }

    public Object getModule() {
        return this.module;
    }

    private Object makeModule() {
        Object module = ContainerManager.getInstance().getContainerContext().createComponent(this.getModuleClass());
        if (module instanceof ServletContextListener) {
            module = new ServletContextListenerWrapper((ServletContextListener)module);
        }
        return module;
    }

    public void enabled() {
        super.enabled();
        this.ensureCompatibleModuleType();
        this.module = this.makeModule();
        if (this.module instanceof StateAware) {
            ((StateAware)this.module).enabled();
        }
    }

    public void disabled() {
        if (this.module instanceof StateAware) {
            ((StateAware)this.module).disabled();
        }
        this.module = null;
        super.disabled();
    }

    public int getSequence() {
        return this.sequence;
    }

    @VisibleForTesting
    void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public int compareTo(LifecyclePluginModuleDescriptor o) {
        int otherSequence = o.sequence;
        return Integer.compare(this.sequence, otherSequence);
    }
}

