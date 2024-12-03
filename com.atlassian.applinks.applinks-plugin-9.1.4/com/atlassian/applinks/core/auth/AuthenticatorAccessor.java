/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.core.plugin.AuthenticationProviderModuleDescriptor;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatorAccessor {
    private final PluginAccessor pluginAccessor;
    private static final Logger log = LoggerFactory.getLogger(AuthenticatorAccessor.class);

    @Autowired
    public AuthenticatorAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public <T extends AuthenticationProvider> T getAuthenticationProvider(ApplicationLink applicationLink, Class<T> providerClass) {
        Objects.requireNonNull(applicationLink, "applicationLink can't be null");
        Objects.requireNonNull(providerClass, "providerClass can't be null");
        log.debug("Looking for {} for application link {}", providerClass, (Object)applicationLink.getName());
        for (AuthenticationProviderPluginModule module : this.getAllAuthenticationProviderPluginModules()) {
            AuthenticationProvider provider = module.getAuthenticationProvider(applicationLink);
            if (provider == null || !providerClass.isAssignableFrom(provider.getClass())) continue;
            return (T)provider;
        }
        return null;
    }

    public Iterable<AuthenticationProviderPluginModule> getAllAuthenticationProviderPluginModules() {
        ArrayList descriptors = Lists.newArrayList((Iterable)this.pluginAccessor.getEnabledModuleDescriptorsByClass(AuthenticationProviderModuleDescriptor.class));
        Collections.sort(descriptors, AuthenticationProviderModuleDescriptor.BY_WEIGHT);
        return Iterables.transform((Iterable)descriptors, (Function)new Function<AuthenticationProviderModuleDescriptor, AuthenticationProviderPluginModule>(){

            public AuthenticationProviderPluginModule apply(AuthenticationProviderModuleDescriptor from) {
                return from.getModule();
            }
        });
    }
}

