/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;

@Deprecated
public class TriggerModuleDescriptor
extends AbstractModuleDescriptor {
    public TriggerModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        throw new PluginParseException("Job/trigger module descriptors are no longer supported. Use JobConfig modules instead. If this is an Atlassian plugin, please update to the latest version.");
    }

    public Object getModule() {
        return null;
    }
}

