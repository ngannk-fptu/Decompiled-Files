/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.applinks.core.rest.ui;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.model.ApplicationLinkInfoEntity;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.sal.api.message.I18nResolver;
import com.sun.jersey.spi.resource.Singleton;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="applicationlinkInfo")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@Singleton
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class ApplicationLinkInfoResource {
    private final PluginAccessor pluginAccessor;
    private final ApplicationLinkService applicationLinkService;
    private final I18nResolver i18nResolver;
    private final MutatingEntityLinkService entityLinkService;
    private final InternalHostApplication internalHostApplication;
    private final TypeAccessor typeAccessor;

    public ApplicationLinkInfoResource(PluginAccessor pluginAccessor, ApplicationLinkService applicationLinkService, I18nResolver i18nResolver, MutatingEntityLinkService entityLinkService, InternalHostApplication internalHostApplication, TypeAccessor typeAccessor) {
        this.pluginAccessor = pluginAccessor;
        this.applicationLinkService = applicationLinkService;
        this.i18nResolver = i18nResolver;
        this.entityLinkService = entityLinkService;
        this.internalHostApplication = internalHostApplication;
        this.typeAccessor = typeAccessor;
    }

    @GET
    @Path(value="id/{id}")
    public Response getConfiguredAuthenticationTypesAndEntityLinksForApplicationLink(@PathParam(value="id") ApplicationId id) {
        int entityCount;
        ApplicationLink applicationLink;
        try {
            applicationLink = this.applicationLinkService.getApplicationLink(id);
            entityCount = (int)StreamSupport.stream(this.entityLinkService.getEntityLinksForApplicationLink(applicationLink).spliterator(), false).count();
        }
        catch (TypeNotInstalledException e) {
            return RestUtil.notFound(this.i18nResolver.getText("applinks.type.not.installed", new Serializable[]{e.getType()}));
        }
        if (applicationLink == null) {
            return RestUtil.notFound(this.i18nResolver.getText("applinks.notfound", new Serializable[]{id.get()}));
        }
        List<String> configuredAuthProviders = this.pluginAccessor.getEnabledModulesByClass(AuthenticationProviderPluginModule.class).stream().filter(Objects::nonNull).map(provider -> provider.getAuthenticationProviderClass().getName()).collect(Collectors.toList());
        List entityTypes = this.pluginAccessor.getEnabledModulesByClass(EntityType.class);
        List<String> hostAppEntityTypesAsString = this.filterEntityTypes(entityTypes, this.internalHostApplication.getType());
        ArrayList<String> remoteEntityTypesAsString = new ArrayList<String>(this.filterEntityTypes(entityTypes, this.internalHostApplication.getType()));
        return RestUtil.ok(new ApplicationLinkInfoEntity(configuredAuthProviders, entityCount, hostAppEntityTypesAsString, remoteEntityTypesAsString));
    }

    private List<String> filterEntityTypes(List<EntityType> entityTypes, ApplicationType type) {
        return entityTypes.stream().filter(entityType -> TypeId.getTypeId((ApplicationType)type).equals((Object)TypeId.getTypeId((ApplicationType)this.typeAccessor.getApplicationType(entityType.getApplicationType())))).map(from -> TypeId.getTypeId((EntityType)from).get()).collect(Collectors.toList());
    }
}

