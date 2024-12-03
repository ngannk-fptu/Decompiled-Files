/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.DependsOn
 *  com.atlassian.applinks.api.event.ApplicationLinkAuthConfigChangedEvent
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.DependsOn;
import com.atlassian.applinks.api.event.ApplicationLinkAuthConfigChangedEvent;
import com.atlassian.applinks.core.auth.AuthenticatorAccessor;
import com.atlassian.applinks.core.property.ApplicationLinkProperties;
import com.atlassian.applinks.core.property.PropertyService;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.event.api.EventPublisher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationConfigurationManagerImpl
implements AuthenticationConfigurationManager {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationConfigurationManagerImpl.class);
    private final ApplicationLinkService applicationLinkService;
    private final AuthenticatorAccessor authenticatorAccessor;
    private final PropertyService propertyService;
    private final EventPublisher eventPublisher;

    @Autowired
    public AuthenticationConfigurationManagerImpl(ApplicationLinkService applicationLinkService, AuthenticatorAccessor authenticatorAccessor, PropertyService propertyService, EventPublisher eventPublisher) {
        this.applicationLinkService = applicationLinkService;
        this.authenticatorAccessor = authenticatorAccessor;
        this.propertyService = propertyService;
        this.eventPublisher = eventPublisher;
    }

    public Map<String, String> getConfiguration(ApplicationId id, Class<? extends AuthenticationProvider> provider) {
        this.assertApplicationLinkPresence(id);
        return this.propertyService.getApplicationLinkProperties(id).getProviderConfig(this.getPrefixForProvider(provider));
    }

    public boolean isConfigured(ApplicationId id, Class<? extends AuthenticationProvider> provider) {
        return this.propertyService.getApplicationLinkProperties(id).authProviderIsConfigured(this.getPrefixForProvider(provider));
    }

    public void registerProvider(ApplicationId id, Class<? extends AuthenticationProvider> provider, Map<String, String> config) {
        this.assertApplicationLinkPresence(id);
        ApplicationLinkProperties props = this.propertyService.getApplicationLinkProperties(id);
        props.setProviderConfig(this.getPrefixForProvider(provider), config);
        this.publishChangeEvent(id);
    }

    public void unregisterProvider(ApplicationId id, Class<? extends AuthenticationProvider> provider) {
        ApplicationLink applicationLink = this.assertApplicationLinkPresence(id);
        ApplicationLinkProperties props = this.propertyService.getApplicationLinkProperties(id);
        props.removeProviderConfig(this.getPrefixForProvider(provider));
        for (Class<? extends AuthenticationProvider> dependentProviderClass : this.findDependentProviders(provider, applicationLink)) {
            props.removeProviderConfig(this.getPrefixForProvider(dependentProviderClass));
        }
        this.publishChangeEvent(id);
    }

    private Set<Class<? extends AuthenticationProvider>> findDependentProviders(Class<? extends AuthenticationProvider> providerClass, ApplicationLink applicationLink) {
        Iterable<AuthenticationProviderPluginModule> allAuthenticationProviderPluginModules = this.authenticatorAccessor.getAllAuthenticationProviderPluginModules();
        HashSet allDependentProviders = Sets.newHashSet();
        return this.findDependentProviders((Collection<Class<? extends AuthenticationProvider>>)ImmutableList.of(providerClass), applicationLink, allAuthenticationProviderPluginModules, allDependentProviders);
    }

    private Set<Class<? extends AuthenticationProvider>> findDependentProviders(Collection<Class<? extends AuthenticationProvider>> providerClasses, ApplicationLink applicationLink, Iterable<AuthenticationProviderPluginModule> allAuthenticationProviderPluginModules, Set<Class<? extends AuthenticationProvider>> allDependentProviders) {
        HashSet newDependentProviders = Sets.newHashSet();
        for (AuthenticationProviderPluginModule module : allAuthenticationProviderPluginModules) {
            AuthenticationProvider authenticationProvider = module.getAuthenticationProvider(applicationLink);
            if (authenticationProvider == null) continue;
            Class dependentProviderClass = module.getAuthenticationProviderClass();
            try {
                DependsOn dependsOn = dependentProviderClass.getAnnotation(DependsOn.class);
                if (dependsOn == null) continue;
                for (Class dependedProviderClass : dependsOn.value()) {
                    if (!providerClasses.contains(dependedProviderClass) || allDependentProviders.contains(dependentProviderClass)) continue;
                    newDependentProviders.add(dependentProviderClass);
                }
            }
            catch (NoClassDefFoundError ncdfe) {
                log.error("Unable to determine authentication provider dependents", (Throwable)ncdfe);
            }
        }
        if (newDependentProviders.isEmpty()) {
            return allDependentProviders;
        }
        allDependentProviders.addAll(newDependentProviders);
        return this.findDependentProviders(newDependentProviders, applicationLink, allAuthenticationProviderPluginModules, allDependentProviders);
    }

    private ApplicationLink assertApplicationLinkPresence(ApplicationId id) {
        ApplicationLink applicationLink = this.getApplicationLink(id);
        if (applicationLink == null) {
            throw new IllegalArgumentException(String.format("Application Link \"%s\" not found.", id));
        }
        return applicationLink;
    }

    private ApplicationLink getApplicationLink(ApplicationId id) {
        ApplicationLink applicationLink;
        try {
            applicationLink = this.applicationLinkService.getApplicationLink(id);
        }
        catch (TypeNotInstalledException e) {
            throw new IllegalStateException(String.format("Failed to load application link %s as type %s is no longer installed.", id, e.getType()));
        }
        return applicationLink;
    }

    private String getPrefixForProvider(Class<? extends AuthenticationProvider> provider) {
        return Objects.requireNonNull(provider, "AuthenticationProvider").getName();
    }

    private void publishChangeEvent(ApplicationId id) {
        ApplicationLink link = this.getApplicationLink(id);
        if (link != null) {
            this.eventPublisher.publish((Object)new ApplicationLinkAuthConfigChangedEvent(link));
        }
    }
}

