/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 */
package com.atlassian.applinks.host.spi;

import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.host.spi.SupportedInboundAuthenticationModuleDescriptor;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public abstract class AbstractInternalHostApplication
implements InternalHostApplication {
    protected final PluginAccessor pluginAccessor;

    protected AbstractInternalHostApplication(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public Iterable<Class<? extends AuthenticationProvider>> getSupportedInboundAuthenticationTypes() {
        return Iterables.transform((Iterable)this.pluginAccessor.getEnabledModuleDescriptorsByClass(SupportedInboundAuthenticationModuleDescriptor.class), (Function)new Function<SupportedInboundAuthenticationModuleDescriptor, Class<? extends AuthenticationProvider>>(){

            public Class<? extends AuthenticationProvider> apply(SupportedInboundAuthenticationModuleDescriptor from) {
                return from.getAuthenticationProviderClass();
            }
        });
    }

    @Override
    public Iterable<Class<? extends AuthenticationProvider>> getSupportedOutboundAuthenticationTypes() {
        return Iterables.transform((Iterable)this.pluginAccessor.getEnabledModulesByClass(AuthenticationProviderPluginModule.class), (Function)new Function<AuthenticationProviderPluginModule, Class<? extends AuthenticationProvider>>(){

            public Class<? extends AuthenticationProvider> apply(AuthenticationProviderPluginModule from) {
                return from.getAuthenticationProviderClass();
            }
        });
    }
}

