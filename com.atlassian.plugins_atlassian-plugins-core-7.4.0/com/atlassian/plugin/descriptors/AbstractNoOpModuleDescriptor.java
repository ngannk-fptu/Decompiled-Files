/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.plugin.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

abstract class AbstractNoOpModuleDescriptor<T>
extends AbstractModuleDescriptor<T> {
    private String errorText;

    protected AbstractNoOpModuleDescriptor() {
        super(new ModuleFactory(){

            public <T> T createModule(String name, ModuleDescriptor<T> moduleDescriptor) {
                throw new IllegalStateException("The module is either unloadable or unrecognised, in any case this shouldn't be called!");
            }
        });
    }

    public final String getErrorText() {
        return this.errorText;
    }

    public final void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    @Override
    public final T getModule() {
        return null;
    }

    @Override
    public final boolean isEnabledByDefault() {
        return false;
    }

    public final void setKey(String key) {
        this.key = key;
    }

    public final void setName(String name) {
        this.name = name;
    }
}

