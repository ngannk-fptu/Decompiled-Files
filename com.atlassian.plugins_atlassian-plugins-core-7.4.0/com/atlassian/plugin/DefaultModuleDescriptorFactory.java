/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  io.atlassian.util.concurrent.CopyOnWriteMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.util.ClassLoaderUtils;
import io.atlassian.util.concurrent.CopyOnWriteMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultModuleDescriptorFactory
implements ModuleDescriptorFactory {
    private static Logger log = LoggerFactory.getLogger(DefaultModuleDescriptorFactory.class);
    private final Map<String, Class<? extends ModuleDescriptor>> moduleDescriptorClasses = CopyOnWriteMap.builder().stableViews().newHashMap();
    private final List<String> permittedModuleKeys = new ArrayList<String>();
    private final HostContainer hostContainer;

    public DefaultModuleDescriptorFactory(HostContainer hostContainer) {
        this.hostContainer = hostContainer;
    }

    public Class<? extends ModuleDescriptor> getModuleDescriptorClass(String type) {
        return this.moduleDescriptorClasses.get(type);
    }

    public ModuleDescriptor<?> getModuleDescriptor(String type) {
        if (this.shouldSkipModuleOfType(type)) {
            return null;
        }
        Class<? extends ModuleDescriptor> moduleDescriptorClazz = this.getModuleDescriptorClass(type);
        if (moduleDescriptorClazz == null) {
            throw new PluginParseException("Cannot find ModuleDescriptor class for plugin of type '" + type + "'.");
        }
        return (ModuleDescriptor)this.hostContainer.create(moduleDescriptorClazz);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean shouldSkipModuleOfType(String type) {
        List<String> list = this.permittedModuleKeys;
        synchronized (list) {
            return !this.permittedModuleKeys.isEmpty() && !this.permittedModuleKeys.contains(type);
        }
    }

    public void setModuleDescriptors(Map<String, String> moduleDescriptorClassNames) {
        for (Map.Entry<String, String> entry : moduleDescriptorClassNames.entrySet()) {
            Class descriptorClass = this.getClassFromEntry(entry);
            if (descriptorClass == null) continue;
            this.addModuleDescriptor(entry.getKey(), descriptorClass);
        }
    }

    private <D extends ModuleDescriptor<?>> Class<D> getClassFromEntry(Map.Entry<String, String> entry) {
        if (this.shouldSkipModuleOfType(entry.getKey())) {
            return null;
        }
        try {
            Class descriptorClass = ClassLoaderUtils.loadClass(entry.getValue(), this.getClass());
            if (!ModuleDescriptor.class.isAssignableFrom(descriptorClass)) {
                log.error("Configured plugin module descriptor class {} does not inherit from ModuleDescriptor", (Object)entry.getValue());
                return null;
            }
            return descriptorClass;
        }
        catch (ClassNotFoundException e) {
            log.error("Unable to add configured plugin module descriptor {}. Class not found: {}", (Object)entry.getKey(), (Object)entry.getValue());
            return null;
        }
    }

    public boolean hasModuleDescriptor(String type) {
        return this.moduleDescriptorClasses.containsKey(type);
    }

    public void addModuleDescriptor(String type, Class<? extends ModuleDescriptor> moduleDescriptorClass) {
        this.moduleDescriptorClasses.put(type, moduleDescriptorClass);
    }

    public void removeModuleDescriptorForType(String type) {
        this.moduleDescriptorClasses.remove(type);
    }

    protected final Map<String, Class<? extends ModuleDescriptor>> getDescriptorClassesMap() {
        return Collections.unmodifiableMap(this.moduleDescriptorClasses);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setPermittedModuleKeys(List<String> permittedModuleKeys) {
        if (permittedModuleKeys == null) {
            permittedModuleKeys = Collections.emptyList();
        }
        List<String> list = this.permittedModuleKeys;
        synchronized (list) {
            this.permittedModuleKeys.clear();
            this.permittedModuleKeys.addAll(permittedModuleKeys);
        }
    }
}

