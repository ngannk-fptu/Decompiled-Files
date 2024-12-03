/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.applinks.spi.auth.AuthenticationScenario
 *  com.atlassian.applinks.spi.auth.AutoConfiguringAuthenticatorProviderPluginModule
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.plugin.AuthenticationProviderModuleDescriptor;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.applinks.spi.auth.AuthenticationScenario;
import com.atlassian.applinks.spi.auth.AutoConfiguringAuthenticatorProviderPluginModule;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.net.RequestFactory;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationConfigurator {
    private final PluginAccessor pluginAccessor;
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationConfigurator.class);

    @Autowired
    public AuthenticationConfigurator(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public boolean configureAuthenticationForApplicationLink(ApplicationLink applicationLink, AuthenticationScenario scenario, RequestFactory requestFactory) throws AuthenticationConfigurationException {
        ArrayList descriptors = Lists.newArrayList((Iterable)this.pluginAccessor.getEnabledModuleDescriptorsByClass(AuthenticationProviderModuleDescriptor.class));
        Collections.sort(descriptors, AuthenticationProviderModuleDescriptor.BY_WEIGHT);
        AuthenticationConfigurationException exception = null;
        for (AuthenticationProviderModuleDescriptor descriptor : descriptors) {
            AutoConfiguringAuthenticatorProviderPluginModule configurableModule;
            AuthenticationProviderPluginModule module = descriptor.getModule();
            if (!(module instanceof AutoConfiguringAuthenticatorProviderPluginModule) || !(configurableModule = (AutoConfiguringAuthenticatorProviderPluginModule)module).isApplicable(scenario, applicationLink)) continue;
            try {
                configurableModule.enable(requestFactory, applicationLink);
                LOG.debug("Configured authentication provider '{}' for application link '{}'", (Object)configurableModule.getClass().getName(), (Object)applicationLink.getId().toString());
                return true;
            }
            catch (AuthenticationConfigurationException e) {
                LOG.warn("Failed to initialize authentication provider '" + configurableModule.getAuthenticationProviderClass().getName() + "'. Trying to use another one.", (Throwable)e);
                exception = e;
            }
        }
        LOG.debug("No authentication provider auto-configured for the new application link '{}'.", (Object)applicationLink.getId().toString());
        if (exception != null) {
            throw new AuthenticationConfigurationException("No authentication provider configured and one or more authentication provider threw an exception during auto-configuration.", exception);
        }
        return false;
    }
}

