/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 */
package com.atlassian.applinks.host.spi;

import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;

public class SupportedInboundAuthenticationModuleDescriptor
extends AbstractModuleDescriptor<AuthenticationProvider> {
    public SupportedInboundAuthenticationModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@class").withError("No supported AuthenticationProvider class specified.")});
    }

    protected final void loadClass(Plugin plugin, String clazz) throws PluginParseException {
        try {
            this.moduleClass = plugin.loadClass(this.moduleClassName, ((Object)((Object)this)).getClass());
        }
        catch (ClassNotFoundException e) {
            throw new PluginParseException(String.format("Can't find class '%s'.", this.moduleClassName), (Throwable)e);
        }
    }

    public AuthenticationProvider getModule() {
        throw new UnsupportedOperationException("Doesn't provide a module");
    }

    public Class<? extends AuthenticationProvider> getAuthenticationProviderClass() {
        return this.getModuleClass();
    }
}

