/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.PropertySet
 *  com.atlassian.applinks.api.SubvertedEntityLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.event.ApplicationLinkAddedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkMadePrimaryEvent
 *  com.atlassian.applinks.api.event.EntityLinkAddedEvent
 *  com.atlassian.applinks.api.event.EntityLinkDeletedEvent
 *  com.atlassian.applinks.host.spi.EntityReference
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.applinks.spi.auth.AuthenticationScenario
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  com.atlassian.applinks.spi.link.AuthenticationResponseException
 *  com.atlassian.applinks.spi.link.EntityLinkBuilderFactory
 *  com.atlassian.applinks.spi.link.LinkCreationResponseException
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.link.NotAdministratorException
 *  com.atlassian.applinks.spi.link.ReciprocalActionException
 *  com.atlassian.applinks.spi.link.RemoteErrorListException
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.NotificationException
 *  com.atlassian.plugin.util.ChainingClassLoader
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.ws.rs.core.Response
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.PropertySet;
import com.atlassian.applinks.api.SubvertedEntityLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.event.ApplicationLinkAddedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkMadePrimaryEvent;
import com.atlassian.applinks.api.event.EntityLinkAddedEvent;
import com.atlassian.applinks.api.event.EntityLinkDeletedEvent;
import com.atlassian.applinks.core.ImmutableApplicationLink;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.auth.ApplicationLinkRequestFactoryFactory;
import com.atlassian.applinks.core.auth.AuthenticationConfigurator;
import com.atlassian.applinks.core.event.BeforeApplicationLinkDeletedEvent;
import com.atlassian.applinks.core.link.DefaultApplicationLink;
import com.atlassian.applinks.core.link.InternalApplicationLink;
import com.atlassian.applinks.core.link.InternalEntityLinkService;
import com.atlassian.applinks.core.property.ApplicationLinkProperties;
import com.atlassian.applinks.core.property.EntityLinkProperties;
import com.atlassian.applinks.core.property.PropertyService;
import com.atlassian.applinks.core.rest.client.ApplicationLinkClient;
import com.atlassian.applinks.core.rest.client.EntityLinkClient;
import com.atlassian.applinks.core.rest.context.CurrentContext;
import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import com.atlassian.applinks.core.rest.model.ErrorListEntity;
import com.atlassian.applinks.core.rest.ui.AuthenticationResource;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.core.v1.rest.ApplicationLinkResource;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.application.IconUriResolver;
import com.atlassian.applinks.internal.common.net.BasicHttpAuthRequestFactory;
import com.atlassian.applinks.internal.common.net.Uris;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.auth.AuthenticationScenario;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.applinks.spi.link.AuthenticationResponseException;
import com.atlassian.applinks.spi.link.EntityLinkBuilderFactory;
import com.atlassian.applinks.spi.link.LinkCreationResponseException;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.link.NotAdministratorException;
import com.atlassian.applinks.spi.link.ReciprocalActionException;
import com.atlassian.applinks.spi.link.RemoteErrorListException;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.NotificationException;
import com.atlassian.plugin.util.ChainingClassLoader;
import com.atlassian.plugin.util.ClassLoaderUtils;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultApplicationLinkService
implements InitializingBean,
MutatingApplicationLinkService,
SubvertedEntityLinkService,
InternalEntityLinkService {
    @VisibleForTesting
    static final String APPLICATION_IDS = "application.ids";
    private static final Logger LOG = LoggerFactory.getLogger(DefaultApplicationLinkService.class);
    private static final int CREATE_APPLICATION_LINK_SOCKET_TIMEOUT = 60000;
    private final ApplicationLinkRequestFactoryFactory requestFactoryFactory;
    private final PropertyService propertyService;
    private final InternalTypeAccessor typeAccessor;
    private final ApplicationLinkClient applicationLinkClient;
    private final EventPublisher eventPublisher;
    private final InternalHostApplication internalHostApplication;
    private final RequestFactory<Request<Request<?, Response>, Response>> requestFactory;
    private final RestUrlBuilder restUrlBuilder;
    private final ManifestRetriever manifestRetriever;
    private final AuthenticationConfigurator authenticationConfigurator;
    private final EntityLinkServiceApi entityLinkService;
    private final Lock applicationIdsLock = new ReentrantLock();

    @Autowired
    public DefaultApplicationLinkService(PropertyService propertyService, ApplicationLinkRequestFactoryFactory requestFactoryFactory, InternalTypeAccessor typeAccessor, ApplicationLinkClient applicationLinkClient, EventPublisher eventPublisher, InternalHostApplication internalHostApplication, RequestFactory<Request<Request<?, Response>, Response>> requestFactory, RestUrlBuilder restUrlBuilder, ManifestRetriever manifestRetriever, AuthenticationConfigurator authenticationConfigurator, EntityLinkBuilderFactory entityLinkBuilderFactory, EntityLinkClient entityLinkClient) {
        this(propertyService, requestFactoryFactory, typeAccessor, applicationLinkClient, eventPublisher, internalHostApplication, requestFactory, restUrlBuilder, manifestRetriever, authenticationConfigurator, new DefaultEntityLinkService(propertyService, entityLinkBuilderFactory, internalHostApplication, typeAccessor, entityLinkClient, eventPublisher));
    }

    @VisibleForTesting
    DefaultApplicationLinkService(PropertyService propertyService, ApplicationLinkRequestFactoryFactory requestFactoryFactory, InternalTypeAccessor typeAccessor, ApplicationLinkClient applicationLinkClient, EventPublisher eventPublisher, InternalHostApplication internalHostApplication, RequestFactory<Request<Request<?, Response>, Response>> requestFactory, RestUrlBuilder restUrlBuilder, ManifestRetriever manifestRetriever, AuthenticationConfigurator authenticationConfigurator, EntityLinkServiceApi entityLinkService) {
        this.requestFactoryFactory = requestFactoryFactory;
        this.propertyService = propertyService;
        this.typeAccessor = typeAccessor;
        this.applicationLinkClient = applicationLinkClient;
        this.eventPublisher = eventPublisher;
        this.internalHostApplication = internalHostApplication;
        this.requestFactory = requestFactory;
        this.restUrlBuilder = restUrlBuilder;
        this.manifestRetriever = manifestRetriever;
        this.authenticationConfigurator = authenticationConfigurator;
        this.entityLinkService = entityLinkService;
    }

    public void afterPropertiesSet() throws Exception {
        if (this.entityLinkService instanceof DefaultEntityLinkService) {
            ((DefaultEntityLinkService)DefaultEntityLinkService.class.cast(this.entityLinkService)).setApplicationLinkService((ApplicationLinkService)this);
        }
    }

    public InternalApplicationLink getApplicationLink(ApplicationId id) throws TypeNotInstalledException {
        if (!this.getApplicationIds().contains(id)) {
            return null;
        }
        return this.retrieveApplicationLink(id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void changeApplicationId(ApplicationId oldId, ApplicationId newId) throws TypeNotInstalledException {
        this.applicationIdsLock.lock();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Changing application link id from [{}] to [{}]", (Object)oldId, (Object)newId);
        }
        try {
            List<ApplicationId> applicationIds = this.getApplicationIds();
            if (!applicationIds.contains(Objects.requireNonNull(oldId))) {
                throw new IllegalArgumentException("Application with server ID " + oldId.toString() + " does not exist.");
            }
            ApplicationLinkProperties oldProperties = this.propertyService.getApplicationLinkProperties(oldId);
            ApplicationLinkProperties newProperties = this.propertyService.getApplicationLinkProperties(Objects.requireNonNull(newId));
            newProperties.setProperties(oldProperties);
            if (!applicationIds.contains(newId)) {
                applicationIds.add(newId);
            } else {
                LOG.warn("There is already an Application Link registered with the ID '{}'. We are merging the upgraded NON-UAL Application Link with this existing Application Link.", (Object)newId);
            }
            this.setApplicationIds(applicationIds);
            InternalApplicationLink from = this.retrieveApplicationLink(oldId);
            InternalApplicationLink to = this.retrieveApplicationLink(newId);
            this.entityLinkService.migrateEntityLinks((ApplicationLink)from, (ApplicationLink)to);
            oldProperties.remove();
            applicationIds.remove(oldId);
            this.setApplicationIds(applicationIds);
        }
        finally {
            this.applicationIdsLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void makePrimary(ApplicationId id) throws TypeNotInstalledException {
        this.applicationIdsLock.lock();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Making application link id [{}] primary", (Object)id.get());
        }
        try {
            InternalApplicationLink internalApplicationLink = this.getApplicationLink(id);
            if (internalApplicationLink == null) {
                throw new IllegalArgumentException("No application link with ID=" + id);
            }
            Iterable<InternalApplicationLink> applicationLinksOfType = this.getInternalApplicationLinks(internalApplicationLink.getType().getClass());
            for (InternalApplicationLink link : applicationLinksOfType) {
                link.setPrimaryFlag(link.getId().equals((Object)id));
            }
            this.eventPublisher.publish((Object)new ApplicationLinkMadePrimaryEvent((ApplicationLink)internalApplicationLink));
        }
        finally {
            this.applicationIdsLock.unlock();
        }
    }

    public void setSystem(ApplicationId id, boolean isSystem) throws TypeNotInstalledException {
        InternalApplicationLink internalApplicationLink = this.getApplicationLink(id);
        internalApplicationLink.setSystem(isSystem);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InternalApplicationLink addApplicationLink(ApplicationId id, ApplicationType type, ApplicationLinkDetails details) {
        try {
            List<ApplicationId> applicationIds;
            this.applicationIdsLock.lock();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding application link id [{}]", (Object)id.get());
            }
            if ((applicationIds = this.getApplicationIds()).contains(id)) {
                throw new IllegalArgumentException("Application with server ID " + id + " is already configured");
            }
            boolean onlyLinkOfItsType = Iterables.isEmpty(this.getApplicationLinks(type.getClass()));
            ApplicationLinkProperties applicationLinkProperties = this.propertyService.getApplicationLinkProperties(id);
            applicationLinkProperties.setType(TypeId.getTypeId((ApplicationType)type));
            applicationLinkProperties.setName(this.findSuitableName(details.getName()));
            applicationLinkProperties.setDisplayUrl(details.getDisplayUrl());
            applicationLinkProperties.setRpcUrl(details.getRpcUrl());
            applicationIds.add(id);
            this.setApplicationIds(applicationIds);
            DefaultApplicationLink addedAppLink = new DefaultApplicationLink(id, type, applicationLinkProperties, this.requestFactoryFactory, this.eventPublisher);
            if (details.isPrimary() || onlyLinkOfItsType) {
                try {
                    this.makePrimary(id);
                }
                catch (TypeNotInstalledException e) {
                    LOG.warn("Failed to make new application link the primary application link", (Throwable)e);
                }
            }
            this.eventPublisher.publish((Object)new ApplicationLinkAddedEvent((ApplicationLink)addedAppLink));
            DefaultApplicationLink defaultApplicationLink = addedAppLink;
            return defaultApplicationLink;
        }
        finally {
            this.applicationIdsLock.unlock();
        }
    }

    private String findSuitableName(String name) {
        String proposedName;
        Iterable<ApplicationLink> allApplicationLinks = this.getApplicationLinks();
        if (!this.isNameInUse(name, null, allApplicationLinks)) {
            return name;
        }
        String root = name.replace(" - [0-9]+$", "");
        int i = 2;
        do {
            proposedName = String.format("%s - %d", root, i);
            ++i;
        } while (this.isNameInUse(proposedName, null, allApplicationLinks));
        return proposedName;
    }

    private boolean isNameInUse(final String name, final ApplicationId id, Iterable<? extends ApplicationLink> allApplicationLinks) {
        try {
            Iterables.find(allApplicationLinks, (Predicate)new Predicate<ApplicationLink>(){

                public boolean apply(ApplicationLink appLink) {
                    return appLink.getName().equals(name) && !appLink.getId().equals((Object)id);
                }
            });
        }
        catch (NoSuchElementException nsee) {
            return false;
        }
        return true;
    }

    public boolean isNameInUse(String name, ApplicationId id) {
        Iterable<InternalApplicationLink> allApplicationLinks = this.getInternalApplicationLinks();
        return this.isNameInUse(name, id, allApplicationLinks);
    }

    public void deleteReciprocatedApplicationLink(ApplicationLink link) throws ReciprocalActionException, CredentialsRequiredException {
        this.applicationLinkClient.deleteReciprocalLinkFrom(link);
        this.deleteApplicationLink(link);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteApplicationLink(ApplicationLink link) {
        block12: {
            ImmutableApplicationLink originalLink = new ImmutableApplicationLink(link, this.requestFactoryFactory);
            try {
                this.applicationIdsLock.lock();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Deleting application link id [{}]", (Object)link.getId());
                }
                List<ApplicationId> applicationIds = this.getApplicationIds();
                this.entityLinkService.deleteEntityLinksFor(link);
                if (!applicationIds.remove(link.getId())) break block12;
                ApplicationLinkProperties appLinkProperties = this.propertyService.getApplicationLinkProperties(link.getId());
                TypeId typeId = appLinkProperties.getType();
                boolean wasPrimary = link.isPrimary();
                try {
                    this.eventPublisher.publish((Object)new BeforeApplicationLinkDeletedEvent(link));
                }
                catch (NotificationException e) {
                    LOG.error("An error occurred when broadcasting event {} for application link with id '{}' and name '{}'", new Object[]{BeforeApplicationLinkDeletedEvent.class.getName(), link.getId(), link.getName()});
                }
                this.setApplicationIds(applicationIds);
                appLinkProperties.remove();
                if (wasPrimary) {
                    if (typeId == null) {
                        LOG.warn("Failed to make new application link the primary application link to replace link with id '{}' and name '{}': Could not find type", (Object)link.getId(), (Object)link.getName());
                    } else {
                        ApplicationType deletedType = this.typeAccessor.loadApplicationType(typeId);
                        Iterator<InternalApplicationLink> linkIterator = this.getInternalApplicationLinks(deletedType.getClass()).iterator();
                        if (linkIterator.hasNext()) {
                            ApplicationLink newPrimaryApplicationLink = (ApplicationLink)linkIterator.next();
                            try {
                                this.makePrimary(newPrimaryApplicationLink.getId());
                            }
                            catch (TypeNotInstalledException ex) {
                                LOG.warn("Failed to make new application link the primary application link", (Throwable)ex);
                            }
                        }
                    }
                }
                this.eventPublisher.publish((Object)new ApplicationLinkDeletedEvent((ApplicationLink)originalLink));
            }
            finally {
                this.applicationIdsLock.unlock();
            }
        }
    }

    private InternalApplicationLink retrieveApplicationLink(ApplicationId id) throws TypeNotInstalledException {
        ApplicationLinkProperties properties = this.propertyService.getApplicationLinkProperties(Objects.requireNonNull(id));
        TypeId typeId = properties.getType();
        if (typeId == null) {
            LOG.warn("Couldn't find type id for application link with id {}. Link is corrupted", (Object)id.get());
            throw new TypeNotInstalledException("unknown", properties.getName(), properties.getRpcUrl());
        }
        ApplicationType type = this.typeAccessor.loadApplicationType(typeId);
        if (type == null) {
            LOG.debug("Couldn't load type {} for application link with id {}, name {}, rpc.url {} . The type may not be installed.", new Object[]{typeId, id.get(), properties.getName(), properties.getRpcUrl()});
            throw new TypeNotInstalledException(typeId.get(), properties.getName(), properties.getRpcUrl());
        }
        return new DefaultApplicationLink(id, type, properties, this.requestFactoryFactory, this.eventPublisher);
    }

    public Iterable<ApplicationLink> getApplicationLinks() {
        return Iterables.filter(this.getInternalApplicationLinks(), ApplicationLink.class);
    }

    public Iterable<InternalApplicationLink> getInternalApplicationLinks() {
        ArrayList<InternalApplicationLink> links = new ArrayList<InternalApplicationLink>();
        for (ApplicationId id : this.getApplicationIds()) {
            try {
                links.add(this.retrieveApplicationLink(id));
            }
            catch (TypeNotInstalledException typeNotInstalledException) {}
        }
        return links;
    }

    public Iterable<ApplicationLink> getApplicationLinks(Class<? extends ApplicationType> type) {
        return StreamSupport.stream(this.getInternalApplicationLinks(type).spliterator(), false).filter(Objects::nonNull).sorted((applicationLink, applicationLink1) -> {
            if (applicationLink.isPrimary() == applicationLink1.isPrimary()) {
                return 0;
            }
            if (applicationLink.isPrimary()) {
                return -1;
            }
            return 1;
        }).collect(Collectors.toList());
    }

    public Iterable<InternalApplicationLink> getInternalApplicationLinks(Class<? extends ApplicationType> type) {
        Objects.requireNonNull(type);
        return StreamSupport.stream(this.getInternalApplicationLinks().spliterator(), false).filter(i -> type.isAssignableFrom(i.getType().getClass())).collect(Collectors.toList());
    }

    public ApplicationLink getPrimaryApplicationLink(Class<? extends ApplicationType> type) {
        Iterator<ApplicationLink> iterator = this.getApplicationLinks(type).iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        while (iterator.hasNext()) {
            ApplicationLink application = iterator.next();
            if (!application.isPrimary()) continue;
            return application;
        }
        throw new IllegalStateException("There are application links of type " + type + " configured, but none are marked as primary");
    }

    private List<ApplicationId> getApplicationIds() {
        List list = (List)this.propertyService.getGlobalAdminProperties().getProperty(APPLICATION_IDS);
        if (list == null) {
            return new ArrayList<ApplicationId>();
        }
        return list.stream().map(ApplicationId::new).collect(Collectors.toList());
    }

    private void setApplicationIds(List<ApplicationId> applicationIds) {
        if (LOG.isDebugEnabled()) {
            String message = String.format("Setting application link ids [%s]", applicationIds);
            LOG.debug(message);
        }
        this.propertyService.getGlobalAdminProperties().putProperty(APPLICATION_IDS, new ArrayList(applicationIds.stream().map(ApplicationId::get).collect(Collectors.toList())));
    }

    public void createReciprocalLink(URI remoteRpcUrl, URI customLocalRpcUrl, String username, String password) throws ReciprocalActionException {
        ErrorListEntity errorListEntity;
        String url;
        URI localRpcUrl = customLocalRpcUrl != null ? customLocalRpcUrl : this.internalHostApplication.getBaseUrl();
        try {
            boolean adminUser = this.isAdminUserInRemoteApplication(remoteRpcUrl, username, password);
            if (!adminUser) {
                throw new NotAdministratorException();
            }
        }
        catch (ResponseException ex) {
            throw new AuthenticationResponseException();
        }
        ApplicationLinkEntity linkBackToMyself = new ApplicationLinkEntity(this.internalHostApplication.getId(), TypeId.getTypeId((ApplicationType)this.internalHostApplication.getType()), this.internalHostApplication.getName(), this.internalHostApplication.getBaseUrl(), this.internalHostApplication.getType().getIconUrl(), IconUriResolver.resolveIconUri(this.internalHostApplication.getType()), localRpcUrl, false, false, Link.self((URI)this.createSelfLinkFor(this.internalHostApplication.getId())));
        try {
            ApplicationLinkResource resource = (ApplicationLinkResource)this.restUrlBuilder.getUrlFor(RestUtil.getBaseRestUri(remoteRpcUrl), ApplicationLinkResource.class);
            url = resource.updateApplicationLink(this.internalHostApplication.getId().toString(), null).toString();
        }
        catch (TypeNotInstalledException e) {
            throw new AssertionError((Object)(RestUrlBuilder.class.getName() + " must never throw " + TypeNotInstalledException.class.getName()));
        }
        Request<Request<?, Response>, Response> request = new BasicHttpAuthRequestFactory(this.requestFactory, username, password).createRequest(Request.MethodType.PUT, url);
        request.setSoTimeout(60000);
        ClassLoader currentContextClassloader = Thread.currentThread().getContextClassLoader();
        ChainingClassLoader chainingClassLoader = new ChainingClassLoader(new ClassLoader[]{currentContextClassloader, ClassLoaderUtils.class.getClassLoader(), ClassLoader.getSystemClassLoader()});
        Thread.currentThread().setContextClassLoader((ClassLoader)chainingClassLoader);
        try {
            errorListEntity = (ErrorListEntity)request.setEntity((Object)linkBackToMyself).executeAndReturn(response -> !response.isSuccessful() ? (ErrorListEntity)response.getEntity(ErrorListEntity.class) : null);
        }
        catch (ResponseException ex) {
            String message = "After creating the 2-Way link an error occurred when reading the response from the remote application. {}";
            throw new LinkCreationResponseException("After creating the 2-Way link an error occurred when reading the response from the remote application. {}", (Throwable)ex);
        }
        catch (RuntimeException ex) {
            String message = "An error occurred when trying to create the application link in the remote application.";
            throw new ReciprocalActionException("An error occurred when trying to create the application link in the remote application.", (Throwable)ex);
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentContextClassloader);
        }
        if (errorListEntity != null) {
            throw new RemoteErrorListException(errorListEntity.getErrors());
        }
    }

    public boolean isAdminUserInRemoteApplication(URI url, String username, String password) throws ResponseException {
        URI uri = Uris.uncheckedConcatenate(url, "/rest/applinks/1.0/");
        AuthenticationResource restUrl = (AuthenticationResource)this.restUrlBuilder.getUrlFor(uri, AuthenticationResource.class);
        return (Boolean)this.requestFactory.createRequest(Request.MethodType.GET, restUrl.getIsAdminUser().toString()).addBasicAuthentication(url.getHost(), username, password).executeAndReturn(Response::isSuccessful);
    }

    public URI createSelfLinkFor(ApplicationId id) {
        try {
            URI baseUri = Optional.ofNullable(CurrentContext.getContext()).map(context -> context.getUriInfo().getBaseUri()).orElseGet(() -> ((InternalHostApplication)this.internalHostApplication).getBaseUrl());
            ApplicationLinkResource applicationLinkResource = (ApplicationLinkResource)this.restUrlBuilder.getUrlFor(baseUri, ApplicationLinkResource.class);
            String idString = id.get();
            javax.ws.rs.core.Response applicationLink = applicationLinkResource.getApplicationLink(idString);
            return this.restUrlBuilder.getURI(applicationLink);
        }
        catch (TypeNotInstalledException e) {
            throw new IllegalStateException(String.format("Failed to load application %s as the %s type is not installed", id.get(), e.getType()));
        }
    }

    public ApplicationLink createApplicationLink(ApplicationType type, ApplicationLinkDetails linkDetails) throws ManifestNotFoundException {
        Manifest manifest = this.manifestRetriever.getManifest(linkDetails.getRpcUrl(), type);
        return this.addApplicationLink(manifest.getId(), type, linkDetails);
    }

    public void configureAuthenticationForApplicationLink(ApplicationLink applicationLink, AuthenticationScenario authenticationScenario, String username, String password) throws AuthenticationConfigurationException {
        this.authenticationConfigurator.configureAuthenticationForApplicationLink(applicationLink, authenticationScenario, new BasicHttpAuthRequestFactory(this.requestFactory, username, password));
    }

    public EntityLink addEntityLink(String localKey, Class<? extends EntityType> localType, EntityLink entityLink) {
        return this.entityLinkService.addEntityLink(localKey, localType, entityLink);
    }

    public EntityLink addReciprocatedEntityLink(String localKey, Class<? extends EntityType> localType, EntityLink entityLink) throws ReciprocalActionException, CredentialsRequiredException {
        return this.entityLinkService.addReciprocatedEntityLink(localKey, localType, entityLink);
    }

    public boolean deleteEntityLink(String localKey, Class<? extends EntityType> localType, EntityLink entityLink) {
        return this.entityLinkService.deleteEntityLink(localKey, localType, entityLink);
    }

    public boolean deleteReciprocatedEntityLink(String localKey, Class<? extends EntityType> localType, EntityLink entityLink) throws ReciprocalActionException, CredentialsRequiredException {
        return this.entityLinkService.deleteReciprocatedEntityLink(localKey, localType, entityLink);
    }

    public void deleteEntityLinksFor(ApplicationLink link) {
        this.entityLinkService.deleteEntityLinksFor(link);
    }

    public EntityLink makePrimary(String localKey, Class<? extends EntityType> localType, EntityLink entityLink) {
        return this.entityLinkService.makePrimary(localKey, localType, entityLink);
    }

    public EntityLink getEntityLink(String localKey, Class<? extends EntityType> localType, String remoteKey, Class<? extends EntityType> remoteType, ApplicationId applicationId) {
        return this.entityLinkService.getEntityLink(localKey, localType, remoteKey, remoteType, applicationId);
    }

    public Iterable<EntityLink> getEntityLinksForApplicationLink(ApplicationLink applicationLink) throws TypeNotInstalledException {
        return this.entityLinkService.getEntityLinksForApplicationLink(applicationLink);
    }

    public Iterable<EntityLink> getEntityLinksForKey(String localKey, Class<? extends EntityType> localType, Class<? extends EntityType> type) {
        return this.entityLinkService.getEntityLinksForKey(localKey, localType, type);
    }

    public Iterable<EntityLink> getEntityLinksForKey(String localKey, Class<? extends EntityType> localType) {
        return this.entityLinkService.getEntityLinksForKey(localKey, localType);
    }

    public EntityLink getPrimaryEntityLinkForKey(String localKey, Class<? extends EntityType> localType, Class<? extends EntityType> type) {
        return this.entityLinkService.getPrimaryEntityLinkForKey(localKey, localType, type);
    }

    public EntityLinkBuilderFactory getEntityLinkBuilderFactory() {
        return this.entityLinkService.getEntityLinkBuilderFactory();
    }

    public Iterable<EntityLink> getEntityLinksNoPermissionCheck(Object entity, Class<? extends EntityType> type) {
        return this.entityLinkService.getEntityLinksNoPermissionCheck(entity, type);
    }

    public Iterable<EntityLink> getEntityLinksNoPermissionCheck(Object entity) {
        return this.entityLinkService.getEntityLinksNoPermissionCheck(entity);
    }

    public Iterable<EntityLink> getEntityLinks(Object entity, Class<? extends EntityType> type) {
        return this.entityLinkService.getEntityLinks(entity, type);
    }

    public Iterable<EntityLink> getEntityLinks(Object entity) {
        return this.entityLinkService.getEntityLinks(entity);
    }

    public EntityLink getPrimaryEntityLink(Object entity, Class<? extends EntityType> type) {
        return this.entityLinkService.getPrimaryEntityLink(entity, type);
    }

    @Override
    public void migrateEntityLinks(ApplicationLink from, ApplicationLink to) {
        this.entityLinkService.migrateEntityLinks(from, to);
    }

    @VisibleForTesting
    static class DefaultEntityLinkService
    implements EntityLinkServiceApi,
    InternalEntityLinkService,
    SubvertedEntityLinkService {
        private static final Logger LOG = LoggerFactory.getLogger((String)DefaultEntityLinkService.class.getName());
        private static final String LINKED_ENTITIES = "linked.entities";
        private static final String PRIMARY_FMT = "primary.%s";
        private static final String TYPE = "type";
        private static final String TYPE_I18N = "typeI18n";
        private static final String APPLICATION_ID = "applicationId";
        private static final String KEY = "key";
        private static final String NAME = "name";
        private final PropertyService propertyService;
        private final EntityLinkBuilderFactory entityLinkBuilderFactory;
        private final InternalHostApplication internalHostApplication;
        private final InternalTypeAccessor typeAccessor;
        private final EntityLinkClient entityLinkClient;
        private final EventPublisher eventPublisher;
        private ApplicationLinkService applicationLinkService;

        public DefaultEntityLinkService(PropertyService propertyService, EntityLinkBuilderFactory entityLinkBuilderFactory, InternalHostApplication internalHostApplication, InternalTypeAccessor typeAccessor, EntityLinkClient entityLinkClient, EventPublisher eventPublisher) {
            this.propertyService = propertyService;
            this.entityLinkBuilderFactory = entityLinkBuilderFactory;
            this.internalHostApplication = internalHostApplication;
            this.typeAccessor = typeAccessor;
            this.entityLinkClient = entityLinkClient;
            this.eventPublisher = eventPublisher;
        }

        void setApplicationLinkService(ApplicationLinkService applicationLinkService) {
            this.applicationLinkService = applicationLinkService;
        }

        public EntityLinkBuilderFactory getEntityLinkBuilderFactory() {
            return this.entityLinkBuilderFactory;
        }

        public EntityLink addReciprocatedEntityLink(String localKey, Class<? extends EntityType> localTypeClass, EntityLink entityLink) throws ReciprocalActionException, CredentialsRequiredException {
            this.entityLinkClient.createEntityLinkFrom(entityLink, this.loadTypeFromClass(localTypeClass), localKey);
            return this.addEntityLink(localKey, localTypeClass, entityLink);
        }

        private EntityType loadTypeFromClass(Class<? extends EntityType> localTypeClass) {
            return Objects.requireNonNull(this.typeAccessor.getEntityType(localTypeClass), String.format("%s class available, but type not installed?", localTypeClass));
        }

        @Override
        public void migrateEntityLinks(ApplicationLink from, ApplicationLink to) {
            if (LOG.isDebugEnabled()) {
                String message = String.format("Migrating Entity Links from Application Link [%s] to [%s]", from.getId().get(), to.getId().get());
                LOG.debug(message);
            }
            for (EntityReference localEntity : this.internalHostApplication.getLocalEntities()) {
                ArrayList entityLinks = Lists.newArrayList((Iterable)this.getStoredEntityLinks(localEntity.getKey(), localEntity.getType().getClass()).stream().map(oldEntityLink -> {
                    if (oldEntityLink.getApplicationLink().getId().equals((Object)from.getId())) {
                        EntityLink newEntityLink = this.entityLinkBuilderFactory.builder().applicationLink(to).type(oldEntityLink.getType()).key(oldEntityLink.getKey()).name(oldEntityLink.getName()).primary(oldEntityLink.isPrimary()).build();
                        EntityLinkProperties oldLinkProperties = this.propertyService.getProperties((EntityLink)oldEntityLink);
                        EntityLinkProperties newLinkProperties = this.propertyService.getProperties(newEntityLink);
                        newLinkProperties.setProperties(oldLinkProperties);
                        oldLinkProperties.removeAll();
                        String primaryPropertyKey = DefaultEntityLinkService.primaryPropertyKey(TypeId.getTypeId((EntityType)newEntityLink.getType()));
                        PropertySet props = this.propertyService.getLocalEntityProperties(localEntity.getKey(), TypeId.getTypeId((EntityType)localEntity.getType()));
                        Object value = props.getProperty(primaryPropertyKey);
                        if (value != null) {
                            Properties primary = (Properties)value;
                            if (from.getId().get().equals(primary.get(APPLICATION_ID))) {
                                primary.put(APPLICATION_ID, to.getId().get());
                                props.putProperty(primaryPropertyKey, (Object)primary);
                            }
                        }
                        return newEntityLink;
                    }
                    return oldEntityLink;
                }).collect(Collectors.toList()));
                this.setStoredEntityLinks(localEntity.getKey(), localEntity.getType().getClass(), entityLinks);
            }
        }

        public EntityLink addEntityLink(String localKey, Class<? extends EntityType> localType, EntityLink entityLink) {
            List<EntityLink> entities = this.getStoredEntityLinks(localKey, localType);
            boolean isUpdate = false;
            Iterator<EntityLink> iterator = entities.iterator();
            while (iterator.hasNext()) {
                EntityLink storedEntity = iterator.next();
                if (!DefaultEntityLinkService.equivalent(storedEntity, entityLink)) continue;
                iterator.remove();
                isUpdate = true;
                break;
            }
            if (LOG.isDebugEnabled()) {
                String message = isUpdate ? String.format("Updating Entity Link for [%s] [%s] as [%s]", localType, localKey, entityLink) : String.format("Adding Entity Link for [%s] [%s] as [%s]", localType, localKey, entityLink);
                LOG.debug(message);
            }
            entities.add(entityLink);
            this.setStoredEntityLinks(localKey, localType, entities);
            EntityLink newLink = entityLink;
            if (entityLink.isPrimary() || this.getPrimaryRef(localKey, this.lookUpTypeId(localType), TypeId.getTypeId((EntityType)entityLink.getType())) == null) {
                newLink = this.makePrimaryImpl(localKey, localType, entityLink);
            }
            this.eventPublisher.publish((Object)new EntityLinkAddedEvent(newLink, localKey, localType));
            return newLink;
        }

        private TypeId lookUpTypeId(Class<? extends EntityType> localType) {
            EntityType type = this.typeAccessor.getEntityType(localType);
            if (type == null) {
                throw new IllegalStateException("Couldn't load " + localType.getName() + ", type not installed?");
            }
            return TypeId.getTypeId((EntityType)type);
        }

        public boolean deleteReciprocatedEntityLink(String localKey, Class<? extends EntityType> localType, EntityLink entityToDelete) throws ReciprocalActionException, CredentialsRequiredException {
            if (LOG.isDebugEnabled()) {
                String message = String.format("Deleting Reciprocated Entity Link for [%s] [%s] was [%s]", this.loadTypeFromClass(localType), localKey, entityToDelete);
                LOG.debug(message);
            }
            this.entityLinkClient.deleteEntityLinkFrom(entityToDelete, this.loadTypeFromClass(localType), localKey);
            return this.deleteEntityLink(localKey, localType, entityToDelete);
        }

        public boolean deleteEntityLink(String localKey, Class<? extends EntityType> localType, EntityLink entityToDelete) {
            List<EntityLink> entities = this.getStoredEntityLinks(localKey, localType);
            boolean deleted = false;
            Iterator<EntityLink> iterator = entities.iterator();
            while (iterator.hasNext()) {
                EntityLink entity = iterator.next();
                if (!DefaultEntityLinkService.equivalent(entity, entityToDelete)) continue;
                iterator.remove();
                deleted = true;
                break;
            }
            if (deleted) {
                PrimaryRef primary;
                if (LOG.isDebugEnabled()) {
                    String message = String.format("Deleting Entity Link for [%s] [%s] was [%s]", this.loadTypeFromClass(localType), localKey, entityToDelete);
                    LOG.debug(message);
                }
                if ((primary = this.getPrimaryRef(localKey, this.lookUpTypeId(localType), TypeId.getTypeId((EntityType)entityToDelete.getType()))) == null || primary.refersTo(entityToDelete)) {
                    this.selectNewPrimary(localKey, localType, entityToDelete.getType().getClass(), entities);
                }
                this.propertyService.getProperties(entityToDelete).removeAll();
                this.setStoredEntityLinks(localKey, localType, entities);
                this.eventPublisher.publish((Object)new EntityLinkDeletedEvent(entityToDelete, localKey, localType));
            }
            return deleted;
        }

        private void selectNewPrimary(String localKey, Class<? extends EntityType> localType, Class<? extends EntityType> type, Iterable<? extends EntityLink> entities) {
            Iterator<? extends EntityLink> it = entities.iterator();
            if (!it.hasNext()) {
                String primaryPropertyKey = DefaultEntityLinkService.primaryPropertyKey(this.lookUpTypeId(type));
                this.propertyService.getLocalEntityProperties(localKey, this.lookUpTypeId(localType)).removeProperty(primaryPropertyKey);
            } else {
                this.makePrimaryImpl(localKey, localType, it.next());
            }
        }

        public void deleteEntityLinksFor(ApplicationLink link) {
            Objects.requireNonNull(link);
            if (LOG.isDebugEnabled()) {
                String message = String.format("Deleting Entity Links for Application Link [%s]", link.getId().get());
                LOG.debug(message);
            }
            for (EntityReference localEntity : this.internalHostApplication.getLocalEntities()) {
                HashSet typesForWhichToReassignPrimaries = new HashSet();
                HashSet removedEntityLinks = new HashSet();
                List updatedLinks = this.getStoredEntityLinks(localEntity.getKey(), localEntity.getType().getClass()).stream().filter(input -> {
                    if (link.getId().equals((Object)input.getApplicationLink().getId())) {
                        PrimaryRef primary;
                        if (!typesForWhichToReassignPrimaries.contains(input.getType().getClass()) && (primary = this.getPrimaryRef(localEntity.getKey(), TypeId.getTypeId((EntityType)localEntity.getType()), TypeId.getTypeId((EntityType)input.getType()))).refersTo((EntityLink)input)) {
                            typesForWhichToReassignPrimaries.add(input.getType().getClass());
                        }
                        removedEntityLinks.add(input);
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());
                for (Class type : typesForWhichToReassignPrimaries) {
                    this.selectNewPrimary(localEntity.getKey(), localEntity.getType().getClass(), type, updatedLinks);
                }
                for (EntityLink removedLink : removedEntityLinks) {
                    this.propertyService.getProperties(removedLink).removeAll();
                }
                this.setStoredEntityLinks(localEntity.getKey(), localEntity.getType().getClass(), updatedLinks);
                for (EntityLink removedLink : removedEntityLinks) {
                    this.eventPublisher.publish((Object)new EntityLinkDeletedEvent(removedLink, localEntity.getKey(), localEntity.getType().getClass()));
                }
            }
        }

        private List<EntityLink> getStoredEntityLinks(String localKey, Class<? extends EntityType> localType) {
            return this.getStoredEntityLinks(localKey, localType, PermissionMode.CHECK);
        }

        private List<EntityLink> getStoredEntityLinks(String localKey, Class<? extends EntityType> localType, PermissionMode permissionMode) {
            List<String> encodedLinks;
            Objects.requireNonNull(localKey, "localKey can't be null");
            Objects.requireNonNull(localType, "localType can't be null");
            switch (permissionMode) {
                case CHECK: {
                    if (this.internalHostApplication.doesEntityExist(localKey, localType)) break;
                    LOG.error(String.format("No local entity with key '%s' and type '%s' exists", localKey, localType));
                    return Lists.newArrayList();
                }
                case NO_CHECK: {
                    if (this.internalHostApplication.doesEntityExistNoPermissionCheck(localKey, localType)) break;
                    LOG.error(String.format("No local entity with key '%s' and type '%s' exists", localKey, localType));
                    return Lists.newArrayList();
                }
                default: {
                    LOG.error("Unknown permission mode: " + (Object)((Object)permissionMode));
                    return Lists.newArrayList();
                }
            }
            if ((encodedLinks = this.getEncodedLinks(localKey, localType)) == null) {
                encodedLinks = new ArrayList<String>();
            }
            ArrayList<EntityLink> entityLinks = new ArrayList<EntityLink>();
            for (String from : encodedLinks) {
                ApplicationLink applicationLink;
                ApplicationId applicationId;
                JSONObject obj;
                try {
                    obj = new JSONObject(from);
                    applicationId = new ApplicationId(this.getRequiredJSONString(obj, APPLICATION_ID));
                }
                catch (JSONException e) {
                    throw new RuntimeException("Failed to decode stored entity link to JSON for local entity with key '" + localKey + "' and of type '" + localType + "'. Encoded string is: '" + from + "'", e);
                }
                try {
                    applicationLink = this.applicationLinkService.getApplicationLink(applicationId);
                }
                catch (TypeNotInstalledException e) {
                    LOG.warn(String.format("Couldn't load application link with id %s, type %s is not installed. All child entity links will be inaccessible.", applicationId, e.getType()));
                    continue;
                }
                if (applicationLink == null) {
                    LOG.debug("Skipping EntityLink [" + from + "] for [" + localKey + "." + this.lookUpTypeId(localType) + "." + LINKED_ENTITIES + "] because ApplicationLink with id [" + applicationId + "] was not found. It should be removed.");
                    continue;
                }
                TypeId typeId = new TypeId(this.getRequiredJSONString(obj, TYPE));
                EntityType type = this.typeAccessor.loadEntityType(typeId);
                if (type == null) {
                    LOG.warn(String.format("Couldn't load type %s for entity link (child of application link with id %s). Type is not installed? ", typeId, applicationLink.getId()));
                    continue;
                }
                String key = this.getRequiredJSONString(obj, KEY);
                PrimaryRef primaryRef = this.getPrimaryRef(localKey, this.lookUpTypeId(localType), TypeId.getTypeId((EntityType)type));
                boolean isPrimary = primaryRef != null ? primaryRef.refersTo(key, TypeId.getTypeId((EntityType)type), applicationLink.getId()) : false;
                entityLinks.add(this.entityLinkBuilderFactory.builder().key(key).type(type).name(this.getRequiredJSONString(obj, NAME)).applicationLink(applicationLink).primary(isPrimary).build());
            }
            return entityLinks;
        }

        private List<String> getEncodedLinks(String localKey, Class<? extends EntityType> localType) {
            return (List)this.propertyService.getLocalEntityProperties(localKey, this.lookUpTypeId(localType)).getProperty(LINKED_ENTITIES);
        }

        private void setStoredEntityLinks(String localKey, Class<? extends EntityType> localType, List<? extends EntityLink> entities) {
            Objects.requireNonNull(localKey, "localKey can't be null");
            Objects.requireNonNull(localType, "localType can't be null");
            if (entities == null || entities.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    String message = String.format("Removing stored entity links for [%s] [%s] was [%s]", localKey, this.lookUpTypeId(localType), this.propertyService.getLocalEntityProperties(localKey, this.lookUpTypeId(localType)).getProperty(LINKED_ENTITIES));
                    LOG.debug(message);
                }
                this.propertyService.getLocalEntityProperties(localKey, this.lookUpTypeId(localType)).removeProperty(LINKED_ENTITIES);
                return;
            }
            List encodedEntities = entities.stream().map(from -> {
                HashMap<String, String> propertyMap = new HashMap<String, String>();
                propertyMap.put(KEY, from.getKey());
                propertyMap.put(NAME, from.getName());
                propertyMap.put(TYPE, TypeId.getTypeId((EntityType)from.getType()).get());
                propertyMap.put(TYPE_I18N, from.getType().getI18nKey());
                propertyMap.put(APPLICATION_ID, from.getApplicationLink().getId().get());
                StringWriter sw = new StringWriter();
                try {
                    new JSONObject(propertyMap).write((Writer)sw);
                }
                catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                return sw.getBuffer().toString();
            }).collect(Collectors.toList());
            if (LOG.isDebugEnabled()) {
                String message = String.format("Setting stored entity links for [%s] [%s] as [%s]", localKey, this.lookUpTypeId(localType), entities);
                LOG.debug(message);
            }
            this.propertyService.getLocalEntityProperties(localKey, this.lookUpTypeId(localType)).putProperty(LINKED_ENTITIES, encodedEntities);
        }

        private String getJSONString(JSONObject obj, String propertyKey) {
            try {
                return obj.isNull(propertyKey) ? null : (String)obj.get(propertyKey);
            }
            catch (JSONException je) {
                throw new RuntimeException(je);
            }
        }

        private String getRequiredJSONString(JSONObject obj, String propertyKey) throws NullPointerException {
            return this.assertNotNull(this.getJSONString(obj, propertyKey), propertyKey);
        }

        private <T> T assertNotNull(T value, String propertyKey) {
            return Objects.requireNonNull(value, EntityLink.class.getSimpleName() + " property '" + propertyKey + "' should not be null!");
        }

        private String getRequiredString(Map map, String propertyKey) {
            return this.assertNotNull((String)map.get(propertyKey), propertyKey);
        }

        public Iterable<EntityLink> getEntityLinksForKey(String localKey, Class<? extends EntityType> localType, Class<? extends EntityType> typeOfRemoteEntities) {
            return this.getEntityLinksForKey(localKey, localType, typeOfRemoteEntities, PermissionMode.CHECK);
        }

        private Iterable<EntityLink> getEntityLinksForKey(String localKey, Class<? extends EntityType> localType, final Class<? extends EntityType> typeOfRemoteEntities, PermissionMode permissionMode) {
            Objects.requireNonNull(localKey, "localKey can't be null");
            Objects.requireNonNull(localType, "localType can't be null");
            Objects.requireNonNull(typeOfRemoteEntities, "typeOfRemoteEntities can't be null");
            return Iterables.filter(this.getStoredEntityLinks(localKey, localType, permissionMode), (Predicate)new Predicate<EntityLink>(){

                public boolean apply(EntityLink input) {
                    return typeOfRemoteEntities.isAssignableFrom(input.getType().getClass());
                }
            });
        }

        public Iterable<EntityLink> getEntityLinks(Object entity, Class<? extends EntityType> type) {
            Objects.requireNonNull(entity);
            EntityReference entityRef = this.internalHostApplication.toEntityReference(entity);
            return this.getEntityLinksForKey(entityRef.getKey(), entityRef.getType().getClass(), type);
        }

        public Iterable<EntityLink> getEntityLinksForKey(String localKey, Class<? extends EntityType> localType) {
            return this.getEntityLinksForKey(localKey, localType, PermissionMode.CHECK);
        }

        private Iterable<EntityLink> getEntityLinksForKey(String localKey, Class<? extends EntityType> localType, PermissionMode permissionMode) {
            Objects.requireNonNull(localKey, "localKey can't be null");
            Objects.requireNonNull(localType, "localType can't be null");
            return this.getStoredEntityLinks(localKey, localType, permissionMode);
        }

        public Iterable<EntityLink> getEntityLinks(Object domainObject) {
            Objects.requireNonNull(domainObject);
            EntityReference entityRef = this.internalHostApplication.toEntityReference(domainObject);
            return this.getEntityLinksForKey(entityRef.getKey(), entityRef.getType().getClass());
        }

        public Iterable<EntityLink> getEntityLinksNoPermissionCheck(Object entity, Class<? extends EntityType> type) {
            Objects.requireNonNull(entity);
            EntityReference entityRef = this.internalHostApplication.toEntityReference(entity);
            return this.getEntityLinksForKey(entityRef.getKey(), entityRef.getType().getClass(), type, PermissionMode.NO_CHECK);
        }

        public Iterable<EntityLink> getEntityLinksNoPermissionCheck(Object domainObject) {
            Objects.requireNonNull(domainObject);
            EntityReference entityRef = this.internalHostApplication.toEntityReference(domainObject);
            return this.getEntityLinksForKey(entityRef.getKey(), entityRef.getType().getClass(), PermissionMode.NO_CHECK);
        }

        public EntityLink getPrimaryEntityLinkForKey(String localKey, Class<? extends EntityType> localType, Class<? extends EntityType> typeOfRemoteEntity) {
            Objects.requireNonNull(localKey, "localKey can't be null`");
            Objects.requireNonNull(localType, "localType can't be null");
            Objects.requireNonNull(typeOfRemoteEntity, "typeOfRemoteEntity can't be null");
            EntityLink primary = null;
            PrimaryRef primaryRef = this.getPrimaryRef(localKey, this.lookUpTypeId(localType), this.lookUpTypeId(typeOfRemoteEntity));
            if (primaryRef != null) {
                for (EntityLink entity : this.getEntityLinksForKey(localKey, localType)) {
                    if (!primaryRef.refersTo(entity)) continue;
                    primary = entity;
                    break;
                }
            }
            return primary;
        }

        public EntityLink getPrimaryEntityLink(Object domainObject, Class<? extends EntityType> type) {
            Objects.requireNonNull(domainObject);
            EntityReference entityRef = this.internalHostApplication.toEntityReference(domainObject);
            return this.getPrimaryEntityLinkForKey(entityRef.getKey(), entityRef.getType().getClass(), type);
        }

        public EntityLink getEntityLink(String localKey, Class<? extends EntityType> localType, String remoteKey, Class<? extends EntityType> remoteType, ApplicationId applicationId) {
            EntityLink link = null;
            for (EntityLink storedLink : this.getStoredEntityLinks(localKey, localType)) {
                if (!DefaultEntityLinkService.equivalent(storedLink, remoteKey, remoteType, applicationId)) continue;
                link = storedLink;
                break;
            }
            return link;
        }

        public Iterable<EntityLink> getEntityLinksForApplicationLink(final ApplicationLink applicationLink) throws TypeNotInstalledException {
            Objects.requireNonNull(applicationLink);
            ArrayList<EntityLink> entityLinks = new ArrayList<EntityLink>();
            for (EntityReference localEntity : this.internalHostApplication.getLocalEntities()) {
                ArrayList list = Lists.newArrayList((Iterable)Iterables.filter(this.getStoredEntityLinks(localEntity.getKey(), localEntity.getType().getClass()), (Predicate)new Predicate<EntityLink>(){

                    public boolean apply(EntityLink input) {
                        return applicationLink.getId().equals((Object)input.getApplicationLink().getId());
                    }
                }));
                entityLinks.addAll(list);
            }
            return entityLinks;
        }

        public EntityLink makePrimary(String localKey, Class<? extends EntityType> localType, EntityLink newPrimary) {
            Objects.requireNonNull(localKey, "localKey can't be null");
            Objects.requireNonNull(localType, "localType can't be null");
            Objects.requireNonNull(newPrimary, "newPrimary can't be null");
            if (this.getEntityLink(localKey, localType, newPrimary.getKey(), newPrimary.getType().getClass(), newPrimary.getApplicationLink().getId()) == null) {
                throw new IllegalArgumentException(String.format("Can not make %s the new primary, not linked to from local entity %s:%s", newPrimary, localType, localKey));
            }
            return this.makePrimaryImpl(localKey, localType, newPrimary);
        }

        private static boolean equivalent(EntityLink a, EntityLink b) {
            return DefaultEntityLinkService.equivalent(a, b.getKey(), b.getType().getClass(), b.getApplicationLink().getId());
        }

        private static boolean equivalent(EntityLink a, String key, Class<? extends EntityType> type, ApplicationId applicationId) {
            return a.getKey().equals(key) && a.getType().getClass().equals(type) && a.getApplicationLink().getId().equals((Object)applicationId);
        }

        private EntityLink makePrimaryImpl(String localKey, Class<? extends EntityType> localType, EntityLink newEntity) {
            String primaryPropertyKey = DefaultEntityLinkService.primaryPropertyKey(TypeId.getTypeId((EntityType)newEntity.getType()));
            Properties primary = new Properties();
            primary.put(KEY, newEntity.getKey());
            primary.put(APPLICATION_ID, newEntity.getApplicationLink().getId().get());
            if (LOG.isDebugEnabled()) {
                String message = String.format("Set primary link for [%s] [%s] as [%s]", localKey, this.lookUpTypeId(localType), primary);
                LOG.debug(message);
            }
            this.propertyService.getLocalEntityProperties(localKey, this.lookUpTypeId(localType)).putProperty(primaryPropertyKey, (Object)primary);
            if (!newEntity.isPrimary()) {
                return this.entityLinkBuilderFactory.builder().applicationLink(newEntity.getApplicationLink()).key(newEntity.getKey()).type(newEntity.getType()).name(newEntity.getName()).primary(true).build();
            }
            return newEntity;
        }

        private PrimaryRef getPrimaryRef(String key, TypeId typeId, TypeId typeOfRemoteEntity) {
            Objects.requireNonNull(key, "key can't be null");
            Objects.requireNonNull(typeId, "typeId can't be null");
            Objects.requireNonNull(typeOfRemoteEntity, "typeOfRemoteEntity can't be null");
            Properties primaryProps = (Properties)this.propertyService.getLocalEntityProperties(key, typeId).getProperty(DefaultEntityLinkService.primaryPropertyKey(typeOfRemoteEntity));
            PrimaryRef primaryRef = null;
            if (primaryProps != null) {
                primaryRef = new PrimaryRef(this.getRequiredString(primaryProps, KEY), typeOfRemoteEntity, new ApplicationId(this.getRequiredString(primaryProps, APPLICATION_ID)));
            }
            return primaryRef;
        }

        private static String primaryPropertyKey(TypeId remoteType) {
            return String.format(PRIMARY_FMT, remoteType.get());
        }

        private static enum PermissionMode {
            CHECK,
            NO_CHECK;

        }

        private static class PrimaryRef {
            private final String key;
            private final TypeId type;
            private final ApplicationId applicationId;

            private PrimaryRef(String key, TypeId type, ApplicationId applicationId) {
                this.key = Objects.requireNonNull(key, "key can't be null");
                this.type = Objects.requireNonNull(type, "type can't be null");
                this.applicationId = Objects.requireNonNull(applicationId, "applicationId can't be null");
            }

            public String getKey() {
                return this.key;
            }

            public TypeId getType() {
                return this.type;
            }

            public ApplicationId getApplicationId() {
                return this.applicationId;
            }

            public boolean refersTo(String key, TypeId type, ApplicationId applicationId) {
                return this.key.equals(key) && this.type.equals((Object)type) && this.applicationId.equals((Object)applicationId);
            }

            public boolean refersTo(EntityLink link) {
                return this.refersTo(link.getKey(), TypeId.getTypeId((EntityType)link.getType()), link.getApplicationLink().getId());
            }
        }
    }

    @VisibleForTesting
    static interface EntityLinkServiceApi
    extends InternalEntityLinkService,
    SubvertedEntityLinkService {
    }
}

