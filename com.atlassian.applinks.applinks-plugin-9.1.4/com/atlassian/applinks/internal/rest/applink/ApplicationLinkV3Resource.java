/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.oauth2.ApplinkOAuth2Service
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  com.atlassian.applinks.spi.link.MutableApplicationLink
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.annotation.Nonnull
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.rest.applink;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.oauth2.ApplinkOAuth2Service;
import com.atlassian.applinks.internal.applink.ApplinkHelper;
import com.atlassian.applinks.internal.applink.ApplinkValidationService;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.rest.interceptor.RestRepresentationInterceptor;
import com.atlassian.applinks.internal.common.rest.model.applink.RestApplicationLink;
import com.atlassian.applinks.internal.common.rest.util.RestApplicationIdParser;
import com.atlassian.applinks.internal.feature.ApplinksFeatureService;
import com.atlassian.applinks.internal.feature.ApplinksFeatures;
import com.atlassian.applinks.internal.permission.PermissionValidationService;
import com.atlassian.applinks.internal.rest.applink.data.RestApplinkDataProvider;
import com.atlassian.applinks.internal.rest.applink.data.RestApplinkDataProviders;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.internal.rest.interceptor.ServiceExceptionInterceptor;
import com.atlassian.applinks.internal.rest.model.applink.RestExtendedApplicationLink;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.applinks.spi.link.MutableApplicationLink;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="applinks")
@Singleton
@InterceptorChain(value={ServiceExceptionInterceptor.class, RestRepresentationInterceptor.class, NoCacheHeaderInterceptor.class})
public class ApplicationLinkV3Resource {
    private static final Logger log = LoggerFactory.getLogger(ApplicationLinkV3Resource.class);
    public static final String CONTEXT = "applinks";
    private final MutatingApplicationLinkService applicationLinkService;
    private final ApplinkHelper applinkHelper;
    private final ApplinkValidationService applinkValidationService;
    private final PermissionValidationService permissionValidationService;
    private final RestApplinkDataProviders dataProviders;
    private final RestApplicationIdParser restApplicationIdParser;
    private final ApplinkOAuth2Service applinkOAuth2Service;
    private final ApplinksFeatureService applinksFeatureService;

    public ApplicationLinkV3Resource(MutatingApplicationLinkService applicationLinkService, ApplinkHelper applinkHelper, ApplinkValidationService applinkValidationService, PermissionValidationService permissionValidationService, RestApplinkDataProviders dataProviders, RestApplicationIdParser restApplicationIdParser, ApplinkOAuth2Service applinkOAuth2Service, ApplinksFeatureService applinksFeatureService) {
        this.applicationLinkService = applicationLinkService;
        this.applinkHelper = applinkHelper;
        this.applinkValidationService = applinkValidationService;
        this.permissionValidationService = permissionValidationService;
        this.dataProviders = dataProviders;
        this.restApplicationIdParser = restApplicationIdParser;
        this.applinkOAuth2Service = applinkOAuth2Service;
        this.applinksFeatureService = applinksFeatureService;
    }

    @GET
    public Response getAll(@QueryParam(value="property") Set<String> propertyKeys, @QueryParam(value="data") Set<String> dataKeys) throws ServiceException {
        this.permissionValidationService.validateAdmin();
        Iterable applicationLinks = this.applicationLinkService.getApplicationLinks();
        List oauth2Clients = this.applinksFeatureService.isEnabled(ApplinksFeatures.V4_UI) ? this.applinkOAuth2Service.getApplicationLinksForOAuth2Clients() : Collections.emptyList();
        List oauth2Providers = this.applinksFeatureService.isEnabled(ApplinksFeatures.V4_UI) ? this.applinkOAuth2Service.getApplicationLinksForOAuth2Provider() : Collections.emptyList();
        Iterable allApplicationLinks = Iterables.concat((Iterable)applicationLinks, oauth2Clients, oauth2Providers);
        ImmutableList.Builder restApplinks = ImmutableList.builder();
        for (ApplicationLink applink : allApplicationLinks) {
            restApplinks.add((Object)new RestExtendedApplicationLink(applink, propertyKeys, this.getData(applink, dataKeys)));
        }
        return Response.ok((Object)restApplinks.build()).build();
    }

    @GET
    @Path(value="{applinkid}")
    public Response get(@PathParam(value="applinkid") String applinkId, @QueryParam(value="property") Set<String> propertyKeys, @QueryParam(value="data") Set<String> dataKeys) throws ServiceException {
        this.permissionValidationService.validateAdmin();
        ApplicationLink applink = this.applinkHelper.getApplicationLink(this.restApplicationIdParser.parse(applinkId));
        return Response.ok((Object)new RestExtendedApplicationLink(applink, propertyKeys, this.getData(applink, dataKeys))).build();
    }

    @PUT
    @Path(value="{applinkid}")
    public Response update(@PathParam(value="applinkid") String applinkId, RestApplicationLink restApplink) throws ServiceException {
        this.permissionValidationService.validateAdmin();
        ApplicationId id = this.restApplicationIdParser.parse(applinkId);
        ApplicationLinkDetails details = restApplink.toDetails();
        this.applinkValidationService.validateUpdate(id, details);
        if (details.isPrimary()) {
            this.applinkHelper.makePrimary(id);
        }
        MutableApplicationLink applink = this.applinkHelper.getMutableApplicationLink(id);
        applink.update(details);
        return Response.ok((Object)new RestApplicationLink((ApplicationLink)applink)).build();
    }

    @DELETE
    @Path(value="{applinkid}")
    public Response delete(@PathParam(value="applinkid") String applinkId) throws ServiceException {
        this.permissionValidationService.validateAdmin();
        ApplicationLink applink = this.applinkHelper.getApplicationLink(this.restApplicationIdParser.parse(applinkId));
        this.applicationLinkService.deleteApplicationLink(applink);
        return Response.noContent().build();
    }

    @Nonnull
    private Map<String, Object> getData(ApplicationLink applink, Set<String> dataKeys) throws ServiceException {
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        for (String key : dataKeys) {
            RestApplinkDataProvider provider = this.dataProviders.getProvider(key);
            if (provider != null) {
                data.put(key, provider.provide(key, applink));
                continue;
            }
            log.debug("RestApplinkDataProvider for requested key {} not found", (Object)key);
            data.put(key, null);
        }
        return data;
    }
}

