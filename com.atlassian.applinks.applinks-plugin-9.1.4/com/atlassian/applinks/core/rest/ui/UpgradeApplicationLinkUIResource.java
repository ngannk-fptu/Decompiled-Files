/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent
 *  com.atlassian.applinks.host.spi.EntityReference
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.auth.AuthenticationScenario
 *  com.atlassian.applinks.spi.auth.AutoConfiguringAuthenticatorProviderPluginModule
 *  com.atlassian.applinks.spi.link.MutableApplicationLink
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 *  com.atlassian.applinks.spi.link.ReciprocalActionException
 *  com.atlassian.applinks.spi.manifest.ApplicationStatus
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.ObjectUtils
 */
package com.atlassian.applinks.core.rest.ui;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.auth.ApplicationLinkRequestAdaptor;
import com.atlassian.applinks.core.auth.AuthenticationConfigurator;
import com.atlassian.applinks.core.rest.AbstractResource;
import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.client.EntityLinkClient;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import com.atlassian.applinks.core.rest.model.ErrorListEntity;
import com.atlassian.applinks.core.rest.model.UpgradeApplicationLinkRequestEntity;
import com.atlassian.applinks.core.rest.model.UpgradeApplicationLinkResponseEntity;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.application.IconUriResolver;
import com.atlassian.applinks.internal.common.net.BasicHttpAuthRequestFactory;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.auth.AuthenticationScenario;
import com.atlassian.applinks.spi.auth.AutoConfiguringAuthenticatorProviderPluginModule;
import com.atlassian.applinks.spi.link.MutableApplicationLink;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;
import com.atlassian.applinks.spi.link.ReciprocalActionException;
import com.atlassian.applinks.spi.manifest.ApplicationStatus;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.resource.Singleton;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.ObjectUtils;

@Path(value="upgrade")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@Singleton
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class UpgradeApplicationLinkUIResource
extends AbstractResource {
    private final MutatingApplicationLinkService applicationLinkService;
    private final MutatingEntityLinkService entityLinkService;
    private final ManifestRetriever manifestRetriever;
    private final I18nResolver i18nResolver;
    private final PluginAccessor pluginAccessor;
    private final EventPublisher eventPublisher;
    private final InternalHostApplication internalHostApplication;
    private final AuthenticationConfigurator authenticationConfigurator;
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final EntityLinkClient entityLinkClient;
    private final UserManager userManager;

    public UpgradeApplicationLinkUIResource(RestUrlBuilder restUrlBuilder, RequestFactory<Request<Request<?, com.atlassian.sal.api.net.Response>, com.atlassian.sal.api.net.Response>> requestFactory, MutatingApplicationLinkService applicationLinkService, MutatingEntityLinkService entityLinkService, AuthenticationConfigurator authenticationConfigurator, AuthenticationConfigurationManager authenticationConfigurationManager, EventPublisher eventPublisher, I18nResolver i18nResolver, InternalHostApplication internalHostApplication, ManifestRetriever manifestRetriever, PluginAccessor pluginAccessor, EntityLinkClient entityLinkClient, InternalTypeAccessor typeAccessor, UserManager userManager) {
        super(restUrlBuilder, typeAccessor, requestFactory, applicationLinkService);
        this.applicationLinkService = applicationLinkService;
        this.entityLinkService = entityLinkService;
        this.authenticationConfigurator = authenticationConfigurator;
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.eventPublisher = eventPublisher;
        this.i18nResolver = i18nResolver;
        this.internalHostApplication = internalHostApplication;
        this.manifestRetriever = manifestRetriever;
        this.pluginAccessor = pluginAccessor;
        this.entityLinkClient = entityLinkClient;
        this.userManager = userManager;
    }

    @POST
    @Path(value="ual/{applinkId}")
    public Response upgrade(@PathParam(value="applinkId") String id, UpgradeApplicationLinkRequestEntity upgradeApplicationLinkRequestEntity) throws TypeNotInstalledException {
        String error;
        ApplicationId applicationId = new ApplicationId(id);
        MutableApplicationLink applicationLink = this.applicationLinkService.getApplicationLink(applicationId);
        if (applicationLink == null) {
            return RestUtil.notFound(this.i18nResolver.getText("applinks.notfound", new Serializable[]{id}));
        }
        if (this.manifestRetriever.getApplicationStatus(applicationLink.getRpcUrl(), applicationLink.getType()) == ApplicationStatus.UNAVAILABLE) {
            error = this.i18nResolver.getText("applinks.legacy.upgrade.error.offline");
        } else {
            try {
                Manifest manifest = this.manifestRetriever.getManifest(applicationLink.getRpcUrl(), applicationLink.getType());
                if (!applicationLink.getId().equals((Object)manifest.getId())) {
                    if (manifest.getAppLinksVersion() != null && manifest.getAppLinksVersion().getMajor() >= 3) {
                        return this.performUalUpgrade(applicationLink, upgradeApplicationLinkRequestEntity, manifest);
                    }
                } else {
                    this.LOG.info("The application id '" + applicationLink.getId() + "' of the application link stored and the remote application are equal, no upgrade required.");
                    return Response.ok().build();
                }
                error = this.i18nResolver.getText("applinks.legacy.upgrade.error.legacy");
            }
            catch (ManifestNotFoundException e) {
                error = this.i18nResolver.getText("applinks.legacy.upgrade.error.manifest", new Serializable[]{TypeId.getTypeId((ApplicationType)applicationLink.getType()).toString(), applicationLink.getId().toString()});
            }
        }
        return RestUtil.badRequest(error);
    }

    private Response performUalUpgrade(final MutableApplicationLink oldApplicationLink, UpgradeApplicationLinkRequestEntity upgradeApplicationLinkRequestEntity, Manifest manifest) throws TypeNotInstalledException {
        final ArrayList<String> warnings = new ArrayList<String>();
        BasicHttpAuthRequestFactory authenticatedRequestFactory = null;
        if (upgradeApplicationLinkRequestEntity.isCreateTwoWayLink()) {
            try {
                if (!this.applicationLinkService.isAdminUserInRemoteApplication(oldApplicationLink.getRpcUrl(), upgradeApplicationLinkRequestEntity.getUsername(), upgradeApplicationLinkRequestEntity.getPassword())) {
                    return RestUtil.badFormRequest(Lists.newArrayList((Object[])new String[]{this.i18nResolver.getText("applinks.error.unauthorized")}), Lists.newArrayList((Object[])new String[]{"authorization"}));
                }
                authenticatedRequestFactory = new BasicHttpAuthRequestFactory(this.requestFactory, upgradeApplicationLinkRequestEntity.getUsername(), upgradeApplicationLinkRequestEntity.getPassword());
            }
            catch (ResponseException ex) {
                return RestUtil.serverError(this.i18nResolver.getText("applinks.error.authorization.response"));
            }
        }
        this.applicationLinkService.changeApplicationId(oldApplicationLink.getId(), manifest.getId());
        MutableApplicationLink newApplicationLink = this.applicationLinkService.getApplicationLink(manifest.getId());
        if (upgradeApplicationLinkRequestEntity.isCreateTwoWayLink() && authenticatedRequestFactory != null) {
            URI localRpcUrl = (URI)ObjectUtils.defaultIfNull((Object)upgradeApplicationLinkRequestEntity.getRpcUrl(), (Object)this.internalHostApplication.getBaseUrl());
            Request createTwoWayLinkRequest = authenticatedRequestFactory.createRequest(Request.MethodType.PUT, URIUtil.uncheckedConcatenate(newApplicationLink.getRpcUrl(), "/rest/applinks/1.0/", "applicationlink", this.internalHostApplication.getId().toString()).toString());
            ApplicationLinkEntity linkBackToMyself = new ApplicationLinkEntity(this.internalHostApplication.getId(), TypeId.getTypeId((ApplicationType)this.internalHostApplication.getType()), this.internalHostApplication.getName(), this.internalHostApplication.getBaseUrl(), this.internalHostApplication.getType().getIconUrl(), IconUriResolver.resolveIconUri(this.internalHostApplication.getType()), localRpcUrl, false, false, this.createSelfLinkFor(this.internalHostApplication.getId()));
            createTwoWayLinkRequest.setEntity((Object)linkBackToMyself);
            try {
                createTwoWayLinkRequest.execute((ResponseHandler)new ResponseHandler<com.atlassian.sal.api.net.Response>(){

                    public void handle(com.atlassian.sal.api.net.Response response) throws ResponseException {
                        if (!response.isSuccessful()) {
                            try {
                                ErrorListEntity listEntity = (ErrorListEntity)response.getEntity(ErrorListEntity.class);
                                warnings.addAll(listEntity.getErrors());
                            }
                            catch (RuntimeException re) {
                                UpgradeApplicationLinkUIResource.this.LOG.warn("Could not parse the peer's response to upgrade application link \"" + oldApplicationLink.getName() + "\" to a bi-directional link. Status code: " + response.getStatusCode() + ".");
                                throw re;
                            }
                        }
                    }
                });
            }
            catch (ResponseException ex) {
                this.LOG.debug("After creating the 2-Way link an error occurred when reading the response from the remote application.", (Throwable)ex);
                warnings.add(this.i18nResolver.getText("applinks.error.response"));
            }
            catch (RuntimeException ex) {
                this.LOG.debug("An error occurred when trying to create the application link in the remote application.", (Throwable)ex);
                warnings.add(this.i18nResolver.getText("applinks.error.general"));
            }
            Response upgradeAuthenResponse = this.upgradeAuthentication(upgradeApplicationLinkRequestEntity, warnings, authenticatedRequestFactory, (ApplicationLink)newApplicationLink);
            if (upgradeAuthenResponse != null) {
                return upgradeAuthenResponse;
            }
        }
        if (upgradeApplicationLinkRequestEntity.isReciprocateEntityLinks()) {
            this.reciprocateEntityLinks((ApplicationLink)newApplicationLink, authenticatedRequestFactory, warnings);
        }
        this.eventPublisher.publish((Object)new ApplicationLinksIDChangedEvent((ApplicationLink)newApplicationLink, oldApplicationLink.getId()));
        this.LOG.info("Successfully upgraded Application Link {} (old application id: {} to new application id: {})", new Object[]{newApplicationLink.getName(), oldApplicationLink.getId(), newApplicationLink.getId()});
        return Response.ok((Object)new UpgradeApplicationLinkResponseEntity(this.toApplicationLinkEntity((ApplicationLink)newApplicationLink), warnings)).build();
    }

    @VisibleForTesting
    protected Response upgradeAuthentication(UpgradeApplicationLinkRequestEntity upgradeApplicationLinkRequestEntity, List<String> warnings, RequestFactory authenticatedRequestFactory, ApplicationLink newApplicationLink) {
        try {
            this.disableAutoConfigurableAuthenticationProviders(newApplicationLink, authenticatedRequestFactory);
        }
        catch (AuthenticationConfigurationException e) {
            this.LOG.warn("Unable to reset existing authentication configuration: " + e.getMessage());
            warnings.add(this.i18nResolver.getText("applinks.ual.upgrade.autoconfiguration.delete.failed", new Serializable[]{e.getMessage()}));
        }
        final boolean shareUserBase = upgradeApplicationLinkRequestEntity.getConfigFormValues().shareUserbase();
        final boolean trustEachOther = upgradeApplicationLinkRequestEntity.getConfigFormValues().trustEachOther();
        if (!this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey()) && shareUserBase) {
            return RestUtil.badFormRequest(Lists.newArrayList((Object[])new String[]{this.i18nResolver.getText("applinks.error.only.sysadmin.operation")}), Lists.newArrayList((Object[])new String[]{"same-userbase"}));
        }
        try {
            this.authenticationConfigurator.configureAuthenticationForApplicationLink(newApplicationLink, new AuthenticationScenario(){

                public boolean isCommonUserBase() {
                    return shareUserBase;
                }

                public boolean isTrusted() {
                    return trustEachOther;
                }
            }, authenticatedRequestFactory);
        }
        catch (AuthenticationConfigurationException e) {
            this.LOG.warn("Could not configure authentication providers for application link '" + newApplicationLink.getName() + "' ", (Throwable)e);
            warnings.add(this.i18nResolver.getText("applinks.link.create.autoconfiguration.failed"));
        }
        return null;
    }

    private void reciprocateEntityLinks(ApplicationLink applicationLink, final RequestFactory authenticatedRequestFactory, List<String> warnings) throws TypeNotInstalledException {
        ApplicationLinkRequestFactory applicationLinkRequestFactory = new ApplicationLinkRequestFactory(){

            public ApplicationLinkRequest createRequest(Request.MethodType methodType, String url) throws CredentialsRequiredException {
                return new ApplicationLinkRequestAdaptor(authenticatedRequestFactory.createRequest(methodType, url));
            }

            public URI getAuthorisationURI(URI callback) {
                return null;
            }

            public URI getAuthorisationURI() {
                return null;
            }
        };
        for (EntityReference entityReference : this.internalHostApplication.getLocalEntities()) {
            for (EntityLink entityLink : this.entityLinkService.getEntityLinksForKey(entityReference.getKey(), entityReference.getType().getClass())) {
                if (!applicationLink.equals(entityLink.getApplicationLink())) continue;
                try {
                    this.entityLinkClient.createEntityLinkFrom(entityLink, entityReference.getType(), entityReference.getKey(), applicationLinkRequestFactory);
                }
                catch (CredentialsRequiredException e) {
                    throw new RuntimeException("Unexpected CredentialsRequiredException", e);
                }
                catch (ReciprocalActionException e) {
                    String warning = this.i18nResolver.getText("applinks.ual.upgrade.reciprocate.entitylinks.failed", new Serializable[]{this.i18nResolver.getText(entityLink.getType().getI18nKey()), entityLink.getKey(), applicationLink.getName(), this.i18nResolver.getText(entityReference.getType().getI18nKey()), entityReference.getKey()});
                    warnings.add(warning);
                    this.LOG.error(warning, (Throwable)e);
                }
            }
        }
    }

    protected void disableAutoConfigurableAuthenticationProviders(ApplicationLink applicationLink, RequestFactory requestFactory) throws AuthenticationConfigurationException {
        for (AutoConfiguringAuthenticatorProviderPluginModule module : this.pluginAccessor.getEnabledModulesByClass(AutoConfiguringAuthenticatorProviderPluginModule.class)) {
            if (!this.authenticationConfigurationManager.isConfigured(applicationLink.getId(), module.getAuthenticationProviderClass())) continue;
            module.disable(requestFactory, applicationLink);
        }
    }

    @POST
    @Path(value="legacy/{applinkId}")
    public Response upgrade(@PathParam(value="applinkId") String id) throws TypeNotInstalledException {
        String error;
        ApplicationId applicationId = new ApplicationId(id);
        MutableApplicationLink applicationLink = this.applicationLinkService.getApplicationLink(applicationId);
        if (applicationLink == null) {
            return RestUtil.notFound(this.i18nResolver.getText("applinks.notfound", new Serializable[]{id}));
        }
        if (this.manifestRetriever.getApplicationStatus(applicationLink.getRpcUrl(), applicationLink.getType()) == ApplicationStatus.UNAVAILABLE) {
            error = this.i18nResolver.getText("applinks.legacy.upgrade.error.offline");
        } else {
            try {
                Manifest manifest = this.manifestRetriever.getManifest(applicationLink.getRpcUrl(), applicationLink.getType());
                if (!applicationLink.getId().equals((Object)manifest.getId())) {
                    if (manifest.getAppLinksVersion() == null || manifest.getAppLinksVersion().getMajor() < 3) {
                        this.applicationLinkService.changeApplicationId(applicationId, manifest.getId());
                        this.eventPublisher.publish((Object)new ApplicationLinksIDChangedEvent((ApplicationLink)this.applicationLinkService.getApplicationLink(manifest.getId()), applicationId));
                        this.LOG.info("Successfully upgraded Application Link to non-UAL peer {} (old application id: {} to new application id: {})", new Object[]{applicationLink.getName(), applicationId, manifest.getId()});
                        return Response.ok((Object)new UpgradeApplicationLinkResponseEntity(this.toApplicationLinkEntity((ApplicationLink)this.applicationLinkService.getApplicationLink(manifest.getId())), Collections.emptyList())).build();
                    }
                } else {
                    return Response.ok().build();
                }
                error = this.i18nResolver.getText("applinks.legacy.upgrade.error.ual");
            }
            catch (ManifestNotFoundException e) {
                error = this.i18nResolver.getText("applinks.legacy.upgrade.error.manifest", new Serializable[]{TypeId.getTypeId((ApplicationType)applicationLink.getType()).toString(), applicationLink.getId().toString()});
            }
        }
        return RestUtil.badRequest(error);
    }
}

