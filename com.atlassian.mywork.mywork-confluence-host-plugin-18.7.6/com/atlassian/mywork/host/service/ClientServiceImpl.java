/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.api.event.ApplicationLinkAddedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkEvent
 *  com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mywork.model.Registration
 *  com.atlassian.mywork.service.LocalNotificationService
 *  com.atlassian.mywork.service.SystemStatusService
 *  com.atlassian.mywork.util.Executors
 *  com.atlassian.mywork.util.GlobalIdFactory
 *  com.atlassian.oauth.event.AccessTokenRemovedEvent
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.navlink.consumer.menu.services.IgnoreRemotePluginApplicationLinkPredicate
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.api.event.ApplicationLinkAddedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkEvent;
import com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.usercompatibility.UserCompatibilityHelper;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.host.dao.UserApplicationLinkDao;
import com.atlassian.mywork.host.event.BeforeCountNewNotificationsEvent;
import com.atlassian.mywork.host.event.ClientRegistrationEvent;
import com.atlassian.mywork.host.model.UserApplicationLink;
import com.atlassian.mywork.host.service.ActiveClientsCache;
import com.atlassian.mywork.host.service.AppLinkHelper;
import com.atlassian.mywork.host.service.LocalClientService;
import com.atlassian.mywork.model.Registration;
import com.atlassian.mywork.service.LocalNotificationService;
import com.atlassian.mywork.service.SystemStatusService;
import com.atlassian.mywork.util.Executors;
import com.atlassian.mywork.util.GlobalIdFactory;
import com.atlassian.oauth.event.AccessTokenRemovedEvent;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.navlink.consumer.menu.services.IgnoreRemotePluginApplicationLinkPredicate;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.usercompatibility.UserKey;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@ParametersAreNonnullByDefault
@Component
public class ClientServiceImpl
implements LocalClientService,
LifecycleAware {
    private static final Logger LOG = LoggerFactory.getLogger(ClientServiceImpl.class);
    protected static final String APP_ID_KEY = "appId";
    protected static final String CATEGORY_KEY = "category";
    protected static final String CATEGORY_VALUE = "Authorisation";
    protected static final List<String> GLOBAL_ID_KEYS = ImmutableList.of((Object)"appId", (Object)"category");
    private final ApplicationLinkService applicationLinkService;
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final LocalNotificationService notificationService;
    private final UserApplicationLinkDao userApplicationLinkDao;
    private final AppLinkHelper appLinkHelper;
    private final InternalHostApplication internalHostApplication;
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;
    private final PluginAccessor pluginAccessor;
    private final SystemStatusService systemStatusService;
    private final Predicate<ReadOnlyApplicationLink> isAtlassianProduct;
    private final ActiveClientsCache activeClients;
    private final Lock registrationLock;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ExecutorService clientChecker = Executors.newSingleThreadExecutor((String)"myWorkClientChecker");

    public ClientServiceImpl(ApplicationLinkService applicationLinkService, @ComponentImport AuthenticationConfigurationManager authenticationConfigurationManager, LocalNotificationService notificationService, UserApplicationLinkDao userApplicationLinkDao, AppLinkHelper appLinkHelper, @ComponentImport @Qualifier(value="internalHostApplication") InternalHostApplication internalHostApplication, EventPublisher eventPublisher, TransactionTemplate transactionTemplate, PluginAccessor pluginAccessor, @ComponentImport SystemStatusService systemStatusService, @ComponentImport ClusterLockService clusterLockService, CacheManager cacheManager) {
        this.applicationLinkService = applicationLinkService;
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.notificationService = notificationService;
        this.userApplicationLinkDao = userApplicationLinkDao;
        this.appLinkHelper = appLinkHelper;
        this.internalHostApplication = internalHostApplication;
        this.eventPublisher = eventPublisher;
        this.transactionTemplate = transactionTemplate;
        this.pluginAccessor = pluginAccessor;
        this.systemStatusService = systemStatusService;
        this.isAtlassianProduct = Predicates.not((Predicate)new IgnoreRemotePluginApplicationLinkPredicate(applicationLinkService));
        this.activeClients = new ActiveClientsCache((Cache<String, Set<String>>)cacheManager.getCache("active-clients-shared-data"));
        this.registrationLock = clusterLockService.getLockForName(this.getClass().getName());
    }

    @EventListener
    public void verifyAuth(BeforeCountNewNotificationsEvent beforeCountNewNotificationsEvent) {
        this.verifyAuth(beforeCountNewNotificationsEvent.getUsername());
    }

    @Override
    public void verifyAuth(String username) {
        Iterable appIdsToCheck = Iterables.filter((Iterable)this.activeClients, appId -> this.authenticationConfigurationManager.isConfigured(appId, OAuthAuthenticationProvider.class));
        UserKey userKey = UserCompatibilityHelper.getKeyForUsername(username);
        Map<ApplicationId, UserApplicationLink> userAppLinksByAppLink = this.join(appIdsToCheck, this.userApplicationLinkDao.findAllByApplicationId(userKey));
        for (Map.Entry<ApplicationId, UserApplicationLink> entry : userAppLinksByAppLink.entrySet()) {
            UserApplicationLink userAppLink = entry.getValue();
            if (userAppLink != null && userAppLink.isAuthVerified()) continue;
            ApplicationLink appLink = this.getApplicationLink(entry.getKey());
            if (appLink == null) {
                this.removeActiveClient(entry.getKey());
                continue;
            }
            try {
                this.appLinkHelper.execute(username, appLink, "/rest/mywork-client/1/host/verifyAuth", response -> {
                    block6: {
                        if (response.isSuccessful()) {
                            try {
                                if (new ObjectMapper().readTree(response.getResponseBodyAsStream()).getBooleanValue()) {
                                    this.userApplicationLinkDao.setPingCompleted(username, appLink.getId().get());
                                    break block6;
                                }
                                this.appLinkHelper.createNotification(username, appLink, (AuthorisationURIGenerator)appLink.createAuthenticatedRequestFactory(ImpersonatingAuthenticationProvider.class));
                            }
                            catch (IOException e) {
                                LOG.debug("Failed to check authentication for " + username + " on " + appLink.getName() + ": " + e.getMessage(), (Throwable)e);
                            }
                            catch (ResponseException e) {
                                LOG.debug("Failed to check authentication for " + username + " on " + appLink.getName() + ": " + e.getMessage(), (Throwable)e);
                            }
                        } else {
                            LOG.debug("Failed to check authentication for " + username + " on " + appLink.getName() + ": " + response.getStatusText());
                        }
                    }
                    return null;
                });
            }
            catch (ResponseException e) {
                LOG.debug("Failed to check authentication for " + username + " on " + appLink.getName() + ": " + e.getMessage(), (Throwable)e);
            }
        }
    }

    private Map<ApplicationId, UserApplicationLink> join(Iterable<ApplicationId> appIds, Map<String, UserApplicationLink> userAppLinksByAppId) {
        HashMap<String, UserApplicationLink> userAppLinksByAppLinkId = new HashMap<String, UserApplicationLink>(userAppLinksByAppId);
        HashMap<ApplicationId, UserApplicationLink> result = new HashMap<ApplicationId, UserApplicationLink>();
        for (ApplicationId appId : appIds) {
            result.put(appId, (UserApplicationLink)userAppLinksByAppLinkId.remove(appId.get()));
        }
        for (UserApplicationLink userAppLink : userAppLinksByAppLinkId.values()) {
            this.userApplicationLinkDao.delete(userAppLink.getId());
        }
        return result;
    }

    private ApplicationLink getApplicationLink(ApplicationId appId) {
        try {
            return this.applicationLinkService.getApplicationLink(appId);
        }
        catch (TypeNotInstalledException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clientPong(String username, String appId) {
        String globalId = ClientServiceImpl.generateGlobalId(appId);
        this.notificationService.deleteByGlobalId(username, globalId);
    }

    public void onStart() {
        LOG.debug("Initializing component");
        this.eventPublisher.register((Object)this);
        LOG.debug("Starting component");
        this.systemStatusService.runWhenCompletelyUp(() -> {
            if (!this.activeClients.isInitialized()) {
                LOG.debug("ActiveClients is not initialized. Initializing now.");
                this.lockAndInitializeActiveClients();
            }
        }, (Executor)this.clientChecker);
    }

    private void lockAndInitializeActiveClients() {
        if (this.registrationLock.tryLock()) {
            LOG.debug("Obtained lock to perform active clients initialization");
            try {
                if (!this.activeClients.isInitialized()) {
                    this.transactionTemplate.execute(() -> {
                        this.doInitActiveClients();
                        return null;
                    });
                }
            }
            finally {
                this.registrationLock.unlock();
            }
        }
    }

    private void doInitActiveClients() {
        LOG.debug("Initializing active clients");
        for (ApplicationLink appLink : Iterables.filter((Iterable)this.applicationLinkService.getApplicationLinks(), this.isAtlassianProduct)) {
            this.updatePotentialClient(appLink);
        }
        this.notificationService.invalidateCachedCounts();
        LOG.debug("Active clients initialization complete");
    }

    public void onStop() {
        LOG.debug("Stopping component");
        this.eventPublisher.unregister((Object)this);
        if (!this.clientChecker.isShutdown()) {
            this.clientChecker.shutdownNow();
        }
    }

    @EventListener
    public void applicationLinkChangedEvent(ApplicationLinkEvent event) {
        if (event instanceof ApplicationLinkAddedEvent || !this.isAtlassianProduct.apply((Object)event.getApplicationLink())) {
            return;
        }
        this.systemStatusService.runWhenCompletelyUp(() -> this.transactionTemplate.execute(() -> {
            this.doHandleAppLinkChangedEvent(event);
            return null;
        }), (Executor)this.clientChecker);
    }

    private void doHandleAppLinkChangedEvent(ApplicationLinkEvent event) {
        this.userApplicationLinkDao.clearPingCompleted(event.getApplicationId().get());
        this.notificationService.deleteByGlobalId(ClientServiceImpl.generateGlobalId(event.getApplicationId().get()));
        if (event instanceof ApplicationLinkDeletedEvent) {
            this.removeActiveClient(event.getApplicationLink().getId());
        } else {
            if (event instanceof ApplicationLinksIDChangedEvent) {
                ApplicationLinksIDChangedEvent idEvent = (ApplicationLinksIDChangedEvent)event;
                this.removeActiveClient(idEvent.getOldApplicationId());
            }
            this.updatePotentialClient(event.getApplicationLink());
        }
        this.notificationService.invalidateCachedCounts();
    }

    @EventListener
    public void onAccessTokenRemovedEvent(AccessTokenRemovedEvent event) {
        UserKey userKey = UserCompatibilityHelper.getKeyForUsername(event.getUsername());
        for (UserApplicationLink link : this.userApplicationLinkDao.findAllByApplicationId(userKey).values()) {
            this.userApplicationLinkDao.delete(link.getId());
        }
    }

    @Override
    public void updatePotentialClient(String appId) {
        ApplicationLink appLink = null;
        try {
            LOG.debug("Fetching applink details for [{}]", (Object)appId);
            appLink = this.applicationLinkService.getApplicationLink(new ApplicationId(appId));
            LOG.debug("Successfully fetched {}", (Object)appLink);
        }
        catch (TypeNotInstalledException e) {
            LOG.warn("Failed to update potential client", (Throwable)e);
        }
        if (appLink == null) {
            LOG.debug("Application link for client '" + appId + "' was not found");
        } else if (!this.isAtlassianProduct.apply((Object)appLink)) {
            LOG.debug("Ignoring remote app link for '" + appId + "'");
        } else {
            LOG.debug("Applink [{}] is valid, updating potential client", (Object)appId);
            this.updatePotentialClient(appLink);
            LOG.debug("Successfully updated potential client [{}]", (Object)appId);
            this.notificationService.invalidateCachedCounts();
        }
    }

    public Iterable<ApplicationLink> getActiveClients() {
        HashSet clientIds = Sets.newHashSet((Iterable)this.activeClients);
        ArrayList result = Lists.newArrayList((Iterable)Iterables.filter((Iterable)this.applicationLinkService.getApplicationLinks(), appLink -> clientIds.contains(appLink.getId())));
        for (ApplicationLink appLink2 : result) {
            clientIds.remove(appLink2.getId());
        }
        for (ApplicationId appId : clientIds) {
            this.removeActiveClient(appId);
        }
        return result;
    }

    private void updatePotentialClient(ApplicationLink appLink) {
        block8: {
            try {
                LOG.debug("Obtaining lock for updating client registrations");
                if (this.registrationLock.tryLock(5000L, TimeUnit.MILLISECONDS)) {
                    LOG.debug("Successfully obtained lock for updating registrations for {}", (Object)appLink.getId());
                    try {
                        Iterable<Registration> registrations = this.getRegistrations(appLink);
                        if (registrations != null) {
                            LOG.debug("Publishing ClientRegistrationEvent");
                            this.eventPublisher.publish((Object)new ClientRegistrationEvent(this, registrations));
                            LOG.info("Adding {} as active client", (Object)appLink.getId());
                            this.activeClients.add(appLink.getId());
                        } else {
                            LOG.info(appLink + " is not an active client, removing it");
                            this.removeActiveClient(appLink.getId());
                        }
                        LOG.debug("Finished updating potential client {}", (Object)appLink.getId());
                        break block8;
                    }
                    finally {
                        LOG.debug("Releasing lock for updating registrations");
                        this.registrationLock.unlock();
                    }
                }
                throw new IllegalStateException("Failed to obtain client registration lock, cannot update potential client " + appLink.getId());
            }
            catch (InterruptedException e) {
                LOG.error("Interrupted while waiting for lock for updating client {}", (Object)appLink);
                Thread.currentThread().interrupt();
            }
        }
    }

    private Iterable<Registration> getRegistrations(ApplicationLink appLink) {
        if (!this.isConfigured(appLink.getId(), ImpersonatingAuthenticationProvider.class) || appLink.getRpcUrl().getHost() == null) {
            return null;
        }
        ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory(Anonymous.class);
        String path = "/rest/mywork-client/1/registration?appid=" + this.internalHostApplication.getId().get();
        try {
            ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, path);
            LOG.debug("Fetching registration of [{}] from [{}] on [{}]", new Object[]{this.internalHostApplication.getId(), appLink, path});
            return (Iterable)request.executeAndReturn(response -> {
                if (response.isSuccessful()) {
                    LOG.debug("Successfully retrieved registration of [{}] from [{}]", (Object)this.internalHostApplication.getId(), (Object)appLink);
                    return this.createRegistrations(appLink, (JsonNode)response.getEntity(JsonNode.class));
                }
                if (response.getStatusCode() == Response.Status.NOT_FOUND.getStatusCode()) {
                    LOG.debug("Registration not found on request of [{}] from [{}]", (Object)this.internalHostApplication.getId(), (Object)appLink);
                    return null;
                }
                LOG.debug("Failed to retrieve registration of [{}] from [{}]: {}", new Object[]{this.internalHostApplication.getId(), appLink, response.getStatusText()});
                throw new ResponseException(response.getStatusText());
            });
        }
        catch (CredentialsRequiredException e) {
            LOG.error("Anonymously accessible resource requires authentication", (Throwable)e);
        }
        catch (ResponseException e) {
            LOG.debug("Failed to fetch registration for [{}] from {} on path [{}]: {}", new Object[]{appLink, appLink.getId(), path, e.getMessage()});
        }
        return null;
    }

    private Iterable<Registration> createRegistrations(ApplicationLink appLink, JsonNode jsonNode) {
        try {
            if (jsonNode.isArray()) {
                ArrayList<Registration> result = new ArrayList<Registration>(jsonNode.size());
                for (JsonNode registrationNode : jsonNode) {
                    result.add(this.createRegistration(appLink, registrationNode));
                }
                return result;
            }
            return ImmutableList.of((Object)this.createRegistration(appLink, jsonNode));
        }
        catch (IOException e) {
            throw new RuntimeException("Could not parse response: " + e.getMessage(), e);
        }
    }

    private Registration createRegistration(ApplicationLink appLink, JsonNode registrationNode) throws IOException {
        Registration registration = (Registration)this.mapper.treeToValue(registrationNode, Registration.class);
        return new Registration(registration.getApplication(), appLink.getId().get(), appLink.getDisplayUrl().toString(), registration.getI18n(), registration.getActions(), registration.getProperties(), registration.getTemplates());
    }

    private boolean isConfigured(ApplicationId applicationId, Class<? extends AuthenticationProvider> providerClass) {
        for (AuthenticationProviderPluginModule module : this.pluginAccessor.getEnabledModulesByClass(AuthenticationProviderPluginModule.class)) {
            Class moduleProviderClass = module.getAuthenticationProviderClass();
            if (!providerClass.isAssignableFrom(moduleProviderClass) || !this.authenticationConfigurationManager.isConfigured(applicationId, moduleProviderClass)) continue;
            return true;
        }
        return false;
    }

    private void removeActiveClient(ApplicationId appId) {
        this.activeClients.remove(appId);
        this.notificationService.deleteByGlobalId(ClientServiceImpl.generateGlobalId(appId.get()));
    }

    public static String generateGlobalId(String appId) {
        return GlobalIdFactory.encode(GLOBAL_ID_KEYS, (Map)ImmutableMap.of((Object)APP_ID_KEY, (Object)appId, (Object)CATEGORY_KEY, (Object)CATEGORY_VALUE));
    }
}

