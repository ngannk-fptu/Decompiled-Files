/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider
 *  com.atlassian.applinks.api.event.ApplicationLinkAddedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkEvent
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.client.service;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.applinks.api.event.ApplicationLinkAddedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkEvent;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.mywork.client.service.ConfigService;
import com.atlassian.mywork.client.service.HostIdCache;
import com.atlassian.mywork.service.HostService;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class HostServiceImpl
implements LifecycleAware,
HostService {
    private static final Logger log = LoggerFactory.getLogger(HostServiceImpl.class);
    private final ApplicationLinkService applicationLinkService;
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final InternalHostApplication internalHostApplication;
    private final ConfigService configService;
    private final EventPublisher eventPublisher;
    private final PluginAccessor pluginAccessor;
    private final HostIdCache hostIdCache;
    private final Predicate<ApplicationLink> isAvailableHost = new Predicate<ApplicationLink>(){

        public boolean apply(ApplicationLink appLink) {
            ApplicationLinkRequestFactory authenticatedRequestFactory = appLink.createAuthenticatedRequestFactory(Anonymous.class);
            try {
                ApplicationLinkRequest request = authenticatedRequestFactory.createRequest(Request.MethodType.GET, "/plugins/servlet/login-miniview");
                return (Boolean)request.executeAndReturn((ReturningResponseHandler)new ReturningResponseHandler<Response, Boolean>(){

                    public Boolean handle(Response response) {
                        return response.isSuccessful();
                    }
                });
            }
            catch (CredentialsRequiredException e) {
                log.error("Anonymously accessible resource requires authentication", (Throwable)e);
            }
            catch (ResponseException e) {
                log.debug("Failed to connect host " + appLink.getRpcUrl(), (Throwable)e);
            }
            return false;
        }
    };
    private final Predicate<ApplicationLink> hasSupportedAuth = new Predicate<ApplicationLink>(){

        public boolean apply(ApplicationLink appLink) {
            boolean supported = HostServiceImpl.this.isConfigured(appLink.getId(), ImpersonatingAuthenticationProvider.class);
            if (!supported) {
                log.debug("Cannot use {} as notifications host: Only OAuth or Trusted Apps authentication are supported", (Object)appLink.getDisplayUrl());
            }
            return supported;
        }
    };
    private final Predicate<ApplicationLink> updateRegistration = new Predicate<ApplicationLink>(){

        public boolean apply(ApplicationLink appLink) {
            ApplicationId internalHostApplicationId = HostServiceImpl.this.internalHostApplication.getId();
            String path = "/rest/mywork/1/client";
            Request request = Anonymous.createAnonymousRequest((ApplicationLink)appLink, (Request.MethodType)Request.MethodType.POST, (String)"/rest/mywork/1/client");
            request.setHeader("Content-Type", "text/plain");
            request.setHeader("X-Atlassian-Token", "no-check");
            request.setRequestBody(internalHostApplicationId.get());
            try {
                log.debug("Updating registration of [{}] with {} on {}", new Object[]{internalHostApplicationId, appLink, "/rest/mywork/1/client"});
                request.execute();
                log.debug("Successfully registered [{}] with {} on {}", new Object[]{internalHostApplicationId, appLink, "/rest/mywork/1/client"});
                return true;
            }
            catch (ResponseException e) {
                log.debug("Failed to register [{}] with {} on {}: {}", new Object[]{internalHostApplicationId, appLink, "/rest/mywork/1/client", e.getMessage()});
                return false;
            }
        }
    };
    private Function<ApplicationLink, ApplicationId> toAppId = new Function<ApplicationLink, ApplicationId>(){

        public ApplicationId apply(ApplicationLink input) {
            return input.getId();
        }
    };
    private final Function<ApplicationId, Option<ApplicationLink>> toAppLink = new Function<ApplicationId, Option<ApplicationLink>>(){

        public Option<ApplicationLink> apply(@Nullable ApplicationId input) {
            try {
                return Option.option((Object)HostServiceImpl.this.applicationLinkService.getApplicationLink(input));
            }
            catch (TypeNotInstalledException e) {
                log.error("Failed to retrieve applink " + input, (Throwable)e);
                return Option.none();
            }
        }
    };

    public HostServiceImpl(ApplicationLinkService applicationLinkService, AuthenticationConfigurationManager authenticationConfigurationManager, InternalHostApplication internalHostApplication, ConfigService configService, EventPublisher eventPublisher, PluginAccessor pluginAccessor, CacheManager cacheFactory) {
        this(applicationLinkService, authenticationConfigurationManager, internalHostApplication, configService, eventPublisher, pluginAccessor, new HostIdCache((CacheFactory)cacheFactory, HostServiceImpl.class.getName() + ".hostIds"));
    }

    @VisibleForTesting
    HostServiceImpl(ApplicationLinkService applicationLinkService, AuthenticationConfigurationManager authenticationConfigurationManager, InternalHostApplication internalHostApplication, ConfigService configService, EventPublisher eventPublisher, PluginAccessor pluginAccessor, HostIdCache hostIdCache) {
        this.applicationLinkService = applicationLinkService;
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.internalHostApplication = internalHostApplication;
        this.configService = configService;
        this.eventPublisher = eventPublisher;
        this.pluginAccessor = pluginAccessor;
        this.hostIdCache = hostIdCache;
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    public void enable() {
        log.debug("Enabling");
        this.updateHostAvailability();
    }

    @Override
    public void resetHosts() {
        this.updateHostAvailability();
    }

    @Override
    public void disable() {
        log.debug("Disabling");
        this.setActiveHost((Option<ApplicationId>)Option.none(ApplicationId.class));
        this.unregister(this.getRegisteredHost());
        this.setRegisteredHost((Option<ApplicationId>)Option.none(ApplicationId.class));
    }

    @EventListener
    public void applicationLinksChanged(ApplicationLinkEvent event) {
        if (event instanceof ApplicationLinkAddedEvent) {
            return;
        }
        this.eventPublisher.publish((Object)new AsyncApplicationLinkEvent());
    }

    @EventListener
    public void applicationLinksChangedAsync(AsyncApplicationLinkEvent event) {
        this.updateHostAvailability();
    }

    @Override
    @Nonnull
    public Option<ApplicationLink> getActiveHost() {
        return this.lookupAppLink(HostType.ACTIVE_HOST);
    }

    @Override
    @Nonnull
    public Option<ApplicationLink> getRegisteredHost() {
        return this.lookupAppLink(HostType.REGISTERED_HOST);
    }

    private Option<ApplicationLink> lookupAppLink(HostType hostType) {
        Option<ApplicationId> hostId = this.hostIdCache.getHost(hostType.name());
        if (hostId != null) {
            log.debug("HostIdCache contained [{}] {}", (Object)hostType, hostId);
            return hostId.flatMap(this.toAppLink);
        }
        log.debug("HostIdCache didn't contain any info on [{}]; forcing a refresh of host info", (Object)hostType);
        return this.updateHostAvailability().get((Object)hostType);
    }

    @Override
    public Iterable<ApplicationLink> getAvailableHosts() {
        return Iterables.filter((Iterable)this.applicationLinkService.getApplicationLinks(), (Predicate)Predicates.and(this.hasSupportedAuth, this.isAvailableHost));
    }

    @Override
    public void setSelectedHost(@Nullable ApplicationId appId) {
        log.debug("Set selected host = {}", (Object)appId);
        this.configService.setHost(appId != null ? appId.get() : null);
        Option<ApplicationLink> oldRegisteredHost = this.getRegisteredHost();
        this.updateHostAvailability();
        if (oldRegisteredHost.isDefined() && !oldRegisteredHost.equals(this.getRegisteredHost())) {
            this.unregister(oldRegisteredHost);
        }
    }

    private void unregister(Option<ApplicationLink> oldRegisteredHost) {
        log.debug("Unregister host = {}", oldRegisteredHost);
        oldRegisteredHost.map(Functions.forPredicate(this.updateRegistration));
    }

    private Map<HostType, Option<ApplicationLink>> updateHostAvailability() {
        log.debug("updateAvailability invoked");
        Option activeHost = this.findSelectedHost().filter(this.hasSupportedAuth);
        log.debug("Local activeHost set to {}", (Object)activeHost);
        this.setActiveHost((Option<ApplicationId>)activeHost.map(this.toAppId));
        Option registeredHost = activeHost.filter(this.updateRegistration);
        this.setRegisteredHost((Option<ApplicationId>)registeredHost.map(this.toAppId));
        return ImmutableMap.of((Object)((Object)HostType.ACTIVE_HOST), (Object)activeHost, (Object)((Object)HostType.REGISTERED_HOST), (Object)registeredHost);
    }

    private void setRegisteredHost(Option<ApplicationId> hostId) {
        this.hostIdCache.setHost(HostType.REGISTERED_HOST.name(), hostId);
    }

    private void setActiveHost(Option<ApplicationId> hostId) {
        this.hostIdCache.setHost(HostType.ACTIVE_HOST.name(), hostId);
    }

    private Option<ApplicationLink> findSelectedHost() {
        String appId = this.configService.getHost();
        if (appId != null) {
            return this.findUserSelectedHost(new ApplicationId(appId));
        }
        return this.findAutoSelectedHost();
    }

    private Option<ApplicationLink> findUserSelectedHost(ApplicationId appId) {
        try {
            return Option.option((Object)this.applicationLinkService.getApplicationLink(appId));
        }
        catch (TypeNotInstalledException e) {
            throw new RuntimeException(e);
        }
    }

    public Option<ApplicationLink> findAutoSelectedHost() {
        ApplicationLink appLink = this.applicationLinkService.getPrimaryApplicationLink(ConfluenceApplicationType.class);
        if (appLink == null) {
            log.debug("Could not select notifications host automatically: No Confluence application links are available");
        }
        return Option.option((Object)appLink);
    }

    private boolean isConfigured(ApplicationId applicationId, Class<? extends AuthenticationProvider> providerClass) {
        for (AuthenticationProviderPluginModule module : this.pluginAccessor.getEnabledModulesByClass(AuthenticationProviderPluginModule.class)) {
            Class moduleProviderClass = module.getAuthenticationProviderClass();
            if (!providerClass.isAssignableFrom(moduleProviderClass) || !this.authenticationConfigurationManager.isConfigured(applicationId, moduleProviderClass)) continue;
            return true;
        }
        return false;
    }

    @AsynchronousPreferred
    private static class AsyncApplicationLinkEvent {
        private AsyncApplicationLinkEvent() {
        }
    }

    private static enum HostType {
        ACTIVE_HOST,
        REGISTERED_HOST;

    }
}

