/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.plugin.descriptors;

import com.atlassian.crowd.password.encoder.PasswordEncoder;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.apache.commons.lang3.Validate;

public class PasswordEncoderModuleDescriptor<T extends PasswordEncoder>
extends AbstractModuleDescriptor<T>
implements StateAware {
    private final PasswordEncoderFactory passwordEncoderFactory;

    public PasswordEncoderModuleDescriptor(PasswordEncoderFactory passwordEncoderFactory, ModuleFactory moduleFactory) {
        super(moduleFactory);
        Validate.notNull((Object)passwordEncoderFactory);
        this.passwordEncoderFactory = passwordEncoderFactory;
    }

    public T getModule() {
        return (T)((PasswordEncoder)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this));
    }

    public void enabled() {
        super.enabled();
        Object passwordEncoder = this.getModule();
        if (passwordEncoder instanceof StateAware) {
            StateAware stateAware = (StateAware)passwordEncoder;
            stateAware.enabled();
        }
        this.passwordEncoderFactory.addEncoder((PasswordEncoder)passwordEncoder);
    }

    public void disabled() {
        this.passwordEncoderFactory.removeEncoder((PasswordEncoder)this.getModule());
        super.disabled();
    }
}

