/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.CannotDisable
 *  com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.impl;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.CannotDisable;
import com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.core.Plugin;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginModuleImpl
implements Plugin.Module {
    private static final Logger log = LoggerFactory.getLogger(PluginModuleImpl.class);
    private final ModuleDescriptor<?> module;
    private final I18nResolver i18nResolver;
    private final Plugin plugin;
    private static final Method isBrokenMethod;

    PluginModuleImpl(ModuleDescriptor<?> module, I18nResolver i18nResolver, Plugin plugin) {
        this.module = Objects.requireNonNull(module, "module");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public ModuleDescriptor<?> getModuleDescriptor() {
        return this.module;
    }

    @Override
    public String getCompleteKey() {
        return this.module.getCompleteKey();
    }

    @Override
    public String getDescription() {
        return this.module.getDescription();
    }

    @Override
    public String getKey() {
        return this.module.getKey();
    }

    @Override
    public String getName() {
        String i18nNameKey = this.module.getI18nNameKey();
        if (i18nNameKey != null && this.i18nResolver.getText(i18nNameKey) != null && !this.i18nResolver.getText(i18nNameKey).equals(i18nNameKey)) {
            return this.i18nResolver.getText(i18nNameKey);
        }
        return this.module.getName();
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getPluginKey() {
        return this.module.getPluginKey();
    }

    public String toString() {
        return this.getCompleteKey();
    }

    @Override
    public boolean canNotBeDisabled() {
        return this.getModuleDescriptor().getClass().isAnnotationPresent(CannotDisable.class);
    }

    @Override
    public boolean hasRecognisableType() {
        return !(this.module instanceof UnrecognisedModuleDescriptor);
    }

    @Override
    public boolean isBroken() {
        try {
            return isBrokenMethod != null && (Boolean)isBrokenMethod.invoke(this.module, new Object[0]) != false;
        }
        catch (AbstractMethodError e) {
            return false;
        }
        catch (InvocationTargetException e) {
            log.error("Unexpected error while calling isBroken()", (Throwable)e);
            return false;
        }
        catch (IllegalAccessException e) {
            log.error("Unexpected error while calling isBroken()", (Throwable)e);
            return false;
        }
    }

    static {
        Method method = null;
        try {
            method = ModuleDescriptor.class.getMethod("isBroken", new Class[0]);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        isBrokenMethod = method;
    }
}

