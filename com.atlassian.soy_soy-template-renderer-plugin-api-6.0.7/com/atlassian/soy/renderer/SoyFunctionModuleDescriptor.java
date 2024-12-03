/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  com.atlassian.soy.renderer.SoyFunction
 */
package com.atlassian.soy.renderer;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.atlassian.soy.renderer.SoyFunction;

@PublicApi
public class SoyFunctionModuleDescriptor
extends AbstractModuleDescriptor<SoyFunction> {
    public static final String XML_ELEMENT_NAME = "soy-function";
    private SoyFunction module;

    @Internal
    public SoyFunctionModuleDescriptor(ModuleFactory factory) {
        super(factory);
    }

    public void enabled() {
        super.enabled();
        this.module = (SoyFunction)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public void disabled() {
        super.disabled();
        this.module = null;
    }

    public SoyFunction getModule() {
        return this.module;
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@class").withError("The class is required")});
    }
}

