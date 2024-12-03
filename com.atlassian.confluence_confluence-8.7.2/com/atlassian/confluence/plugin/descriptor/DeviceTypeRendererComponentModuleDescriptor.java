/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.module.BeanPrefixModuleFactory
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.module.BeanPrefixModuleFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;

public class DeviceTypeRendererComponentModuleDescriptor
extends AbstractModuleDescriptor<Renderer> {
    private static final String DEVICE_TYPE_ELEMENT_NAME = "device-type";
    private final Set<String> deviceTypes = new HashSet<String>();

    public DeviceTypeRendererComponentModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        List elements = element.elements(DEVICE_TYPE_ELEMENT_NAME);
        for (Element el : elements) {
            this.deviceTypes.add(el.getText());
        }
    }

    public Renderer getModule() {
        return (Renderer)new BeanPrefixModuleFactory().createModule(this.getKey(), (ModuleDescriptor)this);
    }

    public Set<String> getDeviceTypes() {
        return Collections.unmodifiableSet(this.deviceTypes);
    }
}

