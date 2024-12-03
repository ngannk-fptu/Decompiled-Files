/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider
 *  com.atlassian.applinks.spi.application.ApplicationIdUtil
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  com.atlassian.applinks.spi.link.MutableApplicationLink
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.applinks.core.v2.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider;
import com.atlassian.applinks.application.generic.GenericApplicationTypeImpl;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import com.atlassian.applinks.core.rest.model.AuthenticationProviderEntity;
import com.atlassian.applinks.core.rest.model.AuthenticationProviderEntityListEntity;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.application.ApplicationIdUtil;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.applinks.spi.link.MutableApplicationLink;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.sun.jersey.spi.resource.Singleton;
import io.swagger.annotations.Api;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Api
@Path(value="applicationlink")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@Singleton
@WebSudoRequired
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class ApplicationLinkResource
extends com.atlassian.applinks.core.v1.rest.ApplicationLinkResource {
    private final PluginAccessor pluginAccessor;
    private final AuthenticationConfigurationManager authenticationConfigurationManager;

    public ApplicationLinkResource(MutatingApplicationLinkService applicationLinkService, I18nResolver i18nResolver, InternalTypeAccessor typeAccessor, ManifestRetriever manifestRetriever, RestUrlBuilder restUrlBuilder, RequestFactory requestFactory, UserManager userManager, PluginAccessor pluginAccessor, AuthenticationConfigurationManager authenticationConfigurationManager) {
        super(applicationLinkService, i18nResolver, typeAccessor, manifestRetriever, restUrlBuilder, requestFactory, userManager);
        this.pluginAccessor = pluginAccessor;
        this.authenticationConfigurationManager = authenticationConfigurationManager;
    }

    @PUT
    public Response addApplicationLink(ApplicationLinkEntity applicationLink) throws TypeNotInstalledException {
        ApplicationType applicationType = this.typeAccessor.loadApplicationType(applicationLink.getTypeId());
        if (applicationType == null) {
            this.LOG.warn(String.format("Couldn't load type %s for application link. Type is not installed?", applicationLink.getTypeId()));
            throw new TypeNotInstalledException(applicationLink.getTypeId().get(), applicationLink.getName(), applicationLink.getRpcUrl());
        }
        ApplicationId applicationId = applicationLink.getId();
        if (applicationType instanceof GenericApplicationTypeImpl || applicationId == null) {
            applicationId = ApplicationIdUtil.generate((URI)applicationLink.getRpcUrl());
        }
        if (this.applicationLinkService.getApplicationLink(applicationId) != null) {
            return RestUtil.badRequest(this.i18nResolver.getText("applinks.error.duplicate.url", new Serializable[]{applicationLink.getRpcUrl()}));
        }
        this.applicationLinkService.addApplicationLink(applicationId, applicationType, applicationLink.getDetails());
        return RestUtil.created(this.createSelfLinkFor(applicationId));
    }

    @Override
    @POST
    @Path(value="{id}")
    public Response updateApplicationLink(@PathParam(value="id") String id, ApplicationLinkEntity applicationLink) throws TypeNotInstalledException {
        ApplicationType applicationType = this.typeAccessor.loadApplicationType(applicationLink.getTypeId());
        if (applicationType == null) {
            this.LOG.warn(String.format("Couldn't load type %s for application link. Type is not installed?", applicationLink.getTypeId()));
            throw new TypeNotInstalledException(applicationLink.getTypeId().get(), applicationLink.getName(), applicationLink.getRpcUrl());
        }
        ApplicationId applicationId = new ApplicationId(id);
        MutableApplicationLink existing = this.applicationLinkService.getApplicationLink(applicationId);
        if (existing == null) {
            return RestUtil.badRequest(this.i18nResolver.getText("applinks.notfound", new Serializable[]{applicationLink.getName()}));
        }
        if (existing.isSystem() && !this.userManager.isSystemAdmin(this.userManager.getRemoteUsername())) {
            return RestUtil.forbidden(this.i18nResolver.getText("applinks.error.only.sysadmin.operation"));
        }
        ApplicationLinkDetails linkDetails = applicationLink.getDetails();
        if (this.applicationLinkService.isNameInUse(linkDetails.getName(), applicationId)) {
            return RestUtil.badRequest(this.i18nResolver.getText("applinks.error.duplicate.name", new Serializable[]{applicationLink.getName()}));
        }
        if (!existing.getRpcUrl().equals(linkDetails.getRpcUrl())) {
            return RestUtil.badRequest(this.i18nResolver.getText("applinks.error.cannot.update.rpcurl"));
        }
        existing.update(linkDetails);
        return RestUtil.updated(this.createSelfLinkFor(applicationLink.getId()));
    }

    @GET
    @Path(value="{id}/authentication/provider")
    public Response getAuthenticationProvider(@PathParam(value="id") String id) throws TypeNotInstalledException {
        List<AuthenticationProviderEntity> configuredAuthProviders;
        ApplicationLink applicationLink = this.findApplicationLink(id);
        if (applicationLink == null) {
            return RestUtil.notFound(this.i18nResolver.getText("applinks.notfound", new Serializable[]{id}));
        }
        try {
            configuredAuthProviders = this.getConfiguredProviders(applicationLink);
        }
        catch (URISyntaxException e) {
            return RestUtil.serverError(this.i18nResolver.getText("applinks.authenticationproviders.notfound", new Serializable[]{id}));
        }
        return RestUtil.ok(new AuthenticationProviderEntityListEntity(configuredAuthProviders));
    }

    @GET
    @Path(value="{id}/authentication/provider/{provider}")
    public Response getAuthenticationProvider(@PathParam(value="id") String id, @PathParam(value="provider") String provider) throws TypeNotInstalledException {
        List<AuthenticationProviderEntity> configuredAuthProviders;
        ApplicationLink applicationLink = this.findApplicationLink(id);
        if (applicationLink == null) {
            return RestUtil.notFound(this.i18nResolver.getText("applinks.notfound", new Serializable[]{id}));
        }
        try {
            configuredAuthProviders = this.getConfiguredProviders(applicationLink, this.getProviderPredicate(provider));
        }
        catch (URISyntaxException e) {
            return RestUtil.serverError(this.i18nResolver.getText("applinks.authenticationproviders.notfound", new Serializable[]{id}));
        }
        return RestUtil.ok(new AuthenticationProviderEntityListEntity(configuredAuthProviders));
    }

    @PUT
    @Path(value="{id}/authentication/provider")
    public Response putAuthenticationProvider(@PathParam(value="id") String id, AuthenticationProviderEntity authenticationProviderEntity) throws TypeNotInstalledException, URISyntaxException {
        ApplicationLink applicationLink = this.findApplicationLink(id);
        if (applicationLink == null) {
            return RestUtil.notFound(this.i18nResolver.getText("applinks.notfound", new Serializable[]{id}));
        }
        try {
            Class<?> providerClass = Class.forName(authenticationProviderEntity.getProvider());
            if (TwoLeggedOAuthWithImpersonationAuthenticationProvider.class.equals(providerClass) && !this.userManager.isSystemAdmin(this.userManager.getRemoteUsername())) {
                return RestUtil.badRequest(this.i18nResolver.getText("applinks.authentication.provider.2loi.only.available.to.sysadmin", new Serializable[]{id, authenticationProviderEntity.getProvider()}));
            }
            this.authenticationConfigurationManager.registerProvider(applicationLink.getId(), providerClass, authenticationProviderEntity.getConfig());
            return RestUtil.created(Link.self((URI)new URI("applicationlink/" + id + "/authentication/" + authenticationProviderEntity.getProvider())));
        }
        catch (ClassNotFoundException e) {
            return RestUtil.badRequest(this.i18nResolver.getText("applinks.authentication.provider.type.not.recognized", new Serializable[]{id, authenticationProviderEntity.getProvider()}));
        }
    }

    private Predicate<AuthenticationProviderPluginModule> getProviderPredicate(final String provider) {
        return new Predicate<AuthenticationProviderPluginModule>(){

            public boolean apply(AuthenticationProviderPluginModule input) {
                return input.getAuthenticationProviderClass().getName().equals(provider);
            }
        };
    }

    private List<AuthenticationProviderEntity> getConfiguredProviders(ApplicationLink applicationLink, Predicate<AuthenticationProviderPluginModule> predicate) throws URISyntaxException {
        return this.getConfiguredProviders(applicationLink, Iterables.filter((Iterable)this.pluginAccessor.getEnabledModulesByClass(AuthenticationProviderPluginModule.class), predicate));
    }

    private List<AuthenticationProviderEntity> getConfiguredProviders(ApplicationLink applicationLink) throws URISyntaxException {
        return this.getConfiguredProviders(applicationLink, this.pluginAccessor.getEnabledModulesByClass(AuthenticationProviderPluginModule.class));
    }

    private List<AuthenticationProviderEntity> getConfiguredProviders(ApplicationLink applicationLink, Iterable<AuthenticationProviderPluginModule> pluginModules) throws URISyntaxException {
        ArrayList<AuthenticationProviderEntity> configuredAuthProviders = new ArrayList<AuthenticationProviderEntity>();
        for (AuthenticationProviderPluginModule authenticationProviderPluginModule : pluginModules) {
            AuthenticationProvider authenticationProvider = authenticationProviderPluginModule.getAuthenticationProvider(applicationLink);
            if (authenticationProvider == null) continue;
            Map config = this.authenticationConfigurationManager.getConfiguration(applicationLink.getId(), authenticationProviderPluginModule.getAuthenticationProviderClass());
            configuredAuthProviders.add(new AuthenticationProviderEntity(Link.self((URI)new URI("applicationlink/" + applicationLink.getId().toString() + "/authentication/provider")), authenticationProviderPluginModule.getClass().getName(), authenticationProviderPluginModule.getAuthenticationProviderClass().getName(), config));
        }
        return configuredAuthProviders;
    }

    private ApplicationLink findApplicationLink(String id) throws TypeNotInstalledException {
        ApplicationId applicationId;
        try {
            applicationId = new ApplicationId(id);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
        return this.applicationLinkService.getApplicationLink(applicationId);
    }
}

