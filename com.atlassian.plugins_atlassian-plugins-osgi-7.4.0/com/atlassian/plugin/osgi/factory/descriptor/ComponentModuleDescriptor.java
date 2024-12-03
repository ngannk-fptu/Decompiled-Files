/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.RequirePermission
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.descriptors.CannotDisable
 *  com.atlassian.plugin.module.ModuleFactory
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.plugin.osgi.factory.descriptor;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.RequirePermission;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.descriptors.CannotDisable;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.module.BeanPrefixModuleFactory;
import javax.annotation.Nonnull;
import org.dom4j.Element;

@CannotDisable
@RequirePermission(value={"execute_java"})
public class ComponentModuleDescriptor
extends AbstractModuleDescriptor<Object> {
    public ComponentModuleDescriptor() {
        super(ModuleFactory.LEGACY_MODULE_FACTORY);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        super.init(plugin, element);
        this.checkPermissions();
    }

    protected void loadClass(Plugin plugin, String clazz) {
        try {
            this.moduleClass = plugin.loadClass(clazz, null);
        }
        catch (ClassNotFoundException e) {
            throw new PluginParseException("cannot load component class", (Throwable)e);
        }
    }

    public Object getModule() {
        return new BeanPrefixModuleFactory().createModule(this.getKey(), this);
    }
}

