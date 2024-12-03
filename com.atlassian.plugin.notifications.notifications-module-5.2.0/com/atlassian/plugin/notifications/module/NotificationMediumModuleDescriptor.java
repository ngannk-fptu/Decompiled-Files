/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.util.concurrent.NotNull
 *  org.dom4j.Element
 */
package com.atlassian.plugin.notifications.module;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.util.concurrent.NotNull;
import org.dom4j.Element;

public class NotificationMediumModuleDescriptor
extends AbstractModuleDescriptor<NotificationMedium> {
    private NotificationMedium medium;
    private boolean isConfigStatic;

    public NotificationMediumModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NotNull Plugin plugin, @NotNull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.isConfigStatic = Boolean.parseBoolean(element.attributeValue("static", "false"));
    }

    public void enabled() {
        super.enabled();
        this.medium = (NotificationMedium)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
        this.medium.init((ModuleDescriptor)this);
    }

    public NotificationMedium getModule() {
        return this.medium;
    }

    public boolean isConfigStatic() {
        return this.isConfigStatic;
    }
}

