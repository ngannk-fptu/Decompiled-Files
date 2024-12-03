/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.host.spi.EntityReference
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 *  com.atlassian.applinks.spi.link.ReciprocalActionException
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.link.DefaultEntityLinkBuilderFactory;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.model.EntityLinkEntity;
import com.atlassian.applinks.core.rest.model.ReferenceEntityList;
import com.atlassian.applinks.core.rest.model.RestEntityLinkList;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;
import com.atlassian.applinks.spi.link.ReciprocalActionException;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="entitylink")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@InterceptorChain(value={ContextInterceptor.class, NoCacheHeaderInterceptor.class})
public class EntityLinkResource {
    private final MutatingEntityLinkService entityLinkService;
    private final ApplicationLinkService applicationLinkService;
    private final InternalTypeAccessor typeAccessor;
    private final I18nResolver i18nResolver;
    private final DefaultEntityLinkBuilderFactory entityLinkFactory;
    private final InternalHostApplication internalHostApplication;
    private static final Logger log = LoggerFactory.getLogger(EntityLinkResource.class);
    private static final Comparator<EntityLinkEntity> PRIMARY_FIRST = new Comparator<EntityLinkEntity>(){

        @Override
        public int compare(EntityLinkEntity o1, EntityLinkEntity o2) {
            if (o1.isPrimary().booleanValue() && o2.isPrimary().booleanValue() || !o1.isPrimary().booleanValue() && !o2.isPrimary().booleanValue()) {
                int result = o1.getTypeId().compareTo(o2.getTypeId());
                return result != 0 ? result : o1.getKey().compareTo(o2.getKey());
            }
            return o1.isPrimary() != false ? -1 : 1;
        }
    };

    public EntityLinkResource(MutatingEntityLinkService entityLinkService, ApplicationLinkService applicationLinkService, InternalTypeAccessor typeAccessor, I18nResolver i18nResolver, DefaultEntityLinkBuilderFactory entityLinkFactory, InternalHostApplication internalHostApplication) {
        this.internalHostApplication = internalHostApplication;
        this.entityLinkService = entityLinkService;
        this.applicationLinkService = applicationLinkService;
        this.typeAccessor = typeAccessor;
        this.i18nResolver = i18nResolver;
        this.entityLinkFactory = entityLinkFactory;
    }

    @GET
    @Path(value="localEntitiesWithLinksTo/{applinkId}")
    public Response getLocalEntitiesWithLinksToApplication(@PathParam(value="applinkId") ApplicationId id) {
        HashSet<EntityReference> linkedLocalEntities = new HashSet<EntityReference>();
        for (EntityReference ref : this.internalHostApplication.getLocalEntities()) {
            for (EntityLink link : this.entityLinkService.getEntityLinksForKey(ref.getKey(), ref.getType().getClass())) {
                if (!id.equals((Object)link.getApplicationLink().getId())) continue;
                linkedLocalEntities.add(ref);
            }
        }
        return RestUtil.ok(new ReferenceEntityList(linkedLocalEntities));
    }

    @GET
    @Path(value="list/{type}/{key}")
    public Response getApplicationEntityLinks(@PathParam(value="type") TypeId localTypeId, @PathParam(value="key") String localKey, @QueryParam(value="typeId") TypeId remoteTypeId) {
        List<EntityLinkEntity> entities;
        EntityType localType = this.typeAccessor.loadEntityType(localTypeId.get());
        this.checkPermissionToManageEntityLink(localKey, localType);
        if (remoteTypeId != null) {
            EntityType remoteType = this.typeAccessor.loadEntityType(remoteTypeId.get());
            entities = EntityLinkResource.toRestApplicationEntities(this.entityLinkService.getEntityLinksForKey(localKey, localType.getClass(), remoteType.getClass()));
        } else {
            entities = EntityLinkResource.toRestApplicationEntities(this.entityLinkService.getEntityLinksForKey(localKey, localType.getClass()));
        }
        return RestUtil.ok(new RestEntityLinkList(entities));
    }

    @GET
    @Path(value="primaryLinks/{type}/{key}")
    public Response getEntityLinks(@PathParam(value="type") TypeId localTypeId, @PathParam(value="key") String localKey) {
        EntityType localType = this.typeAccessor.loadEntityType(localTypeId.get());
        this.checkPermissionToManageEntityLink(localKey, localType);
        HashMap<String, TreeSet<EntityLinkEntity>> linkMap = new HashMap<String, TreeSet<EntityLinkEntity>>();
        for (EntityLink link : this.entityLinkService.getEntityLinksForKey(localKey, localType.getClass())) {
            TreeSet<EntityLinkEntity> links = (TreeSet<EntityLinkEntity>)linkMap.get(link.getType().getI18nKey());
            if (links == null) {
                links = new TreeSet<EntityLinkEntity>(PRIMARY_FIRST);
                linkMap.put(link.getType().getI18nKey(), links);
            }
            links.add(new EntityLinkEntity(link));
        }
        return RestUtil.ok(linkMap);
    }

    @GET
    @Path(value="primary/{type}/{key}")
    public Response getPrimaryApplicationEntityLink(@PathParam(value="type") TypeId typeId, @PathParam(value="key") String localKey, @QueryParam(value="typeId") TypeId remoteTypeId) {
        Response response;
        EntityType localType = this.typeAccessor.loadEntityType(typeId.get());
        if (localType == null) {
            return RestUtil.typeNotInstalled(typeId);
        }
        this.checkPermissionToManageEntityLink(localKey, localType);
        if (remoteTypeId == null) {
            Iterable entityLinks = Iterables.filter((Iterable)this.entityLinkService.getEntityLinksForKey(localKey, localType.getClass()), (Predicate)new Predicate<EntityLink>(){

                public boolean apply(EntityLink input) {
                    return input.isPrimary();
                }
            });
            response = RestUtil.ok(EntityLinkResource.toRestApplicationEntities(entityLinks));
        } else {
            EntityType remoteType = this.typeAccessor.loadEntityType(remoteTypeId);
            EntityLink primary = this.entityLinkService.getPrimaryEntityLinkForKey(localKey, localType.getClass(), remoteType.getClass());
            response = primary != null ? RestUtil.ok(new EntityLinkEntity(primary)) : RestUtil.notFound(String.format("No primary link of type %s for local %s %s found.", remoteType, localType, localKey));
        }
        return response;
    }

    @PUT
    @Path(value="{type}/{key}")
    public Response createEntityLink(@PathParam(value="type") TypeId localTypeId, @PathParam(value="key") String localKey, @QueryParam(value="reciprocate") Boolean reciprocate, EntityLinkEntity entity) {
        EntityLink link;
        ApplicationLink applicationLink;
        RestUtil.checkParam("entity", entity);
        try {
            applicationLink = this.applicationLinkService.getApplicationLink(entity.getApplicationId());
        }
        catch (TypeNotInstalledException e) {
            return EntityLinkResource.applicationTypeNotInstalled(entity.getApplicationId(), e.getType());
        }
        if (applicationLink == null) {
            return RestUtil.notFound("No application found for id " + entity.getApplicationId());
        }
        EntityType localType = this.typeAccessor.loadEntityType(localTypeId.get());
        EntityType remoteType = this.typeAccessor.loadEntityType(entity.getTypeId().get());
        if (localType == null) {
            return RestUtil.typeNotInstalled(localTypeId);
        }
        if (remoteType == null) {
            return RestUtil.typeNotInstalled(entity.getTypeId());
        }
        this.checkPermissionToManageEntityLink(localKey, localType);
        EntityLink existingEntityLink = this.entityLinkService.getEntityLink(localKey, localType.getClass(), entity.getKey(), remoteType.getClass(), entity.getApplicationId());
        EntityLink newLink = existingEntityLink != null ? this.entityLinkFactory.builder().applicationLink(applicationLink).key(existingEntityLink.getKey()).type(remoteType).name(entity.getName()).primary(existingEntityLink.isPrimary()).build() : this.entityLinkFactory.builder().applicationLink(applicationLink).key(entity.getKey()).type(remoteType).name(entity.getName()).primary(false).build();
        if (reciprocate != null && reciprocate.booleanValue()) {
            try {
                link = this.entityLinkService.addReciprocatedEntityLink(localKey, localType.getClass(), newLink);
            }
            catch (CredentialsRequiredException e) {
                return RestUtil.credentialsRequired(this.i18nResolver);
            }
            catch (ReciprocalActionException e) {
                return RestUtil.serverError(this.i18nResolver.getText("applinks.remote.create.failed", new Serializable[]{e.getMessage()}));
            }
        } else {
            link = this.entityLinkService.addEntityLink(localKey, localType.getClass(), newLink);
        }
        return Response.status((Response.Status)Response.Status.CREATED).entity((Object)new EntityLinkEntity(link)).build();
    }

    @POST
    @Path(value="primary/{type}/{key}")
    public Response makePrimary(@PathParam(value="type") TypeId localTypeId, @PathParam(value="key") String localKey, @QueryParam(value="typeId") TypeId remoteTypeId, @QueryParam(value="key") String remoteKey, @QueryParam(value="applicationId") String applicationIdString) {
        RestUtil.checkParam("type", remoteTypeId);
        RestUtil.checkParam("key", remoteKey);
        RestUtil.checkParam("applicationId", applicationIdString);
        EntityType localType = this.typeAccessor.loadEntityType(localTypeId.get());
        this.checkPermissionToManageEntityLink(localKey, localType);
        EntityType remoteType = this.typeAccessor.loadEntityType(remoteTypeId.get());
        ApplicationId applicationId = new ApplicationId(applicationIdString);
        EntityLink link = this.entityLinkService.getEntityLink(localKey, localType.getClass(), remoteKey, remoteType.getClass(), applicationId);
        Response response = link == null ? EntityLinkResource.linkNotFound(localType.getClass(), localKey, remoteType.getClass(), remoteKey, applicationId) : RestUtil.ok(new EntityLinkEntity(this.entityLinkService.makePrimary(localKey, localType.getClass(), link)));
        return response;
    }

    @DELETE
    @Path(value="{type}/{key}")
    public Response deleteApplicationEntityLink(@PathParam(value="type") TypeId localTypeId, @PathParam(value="key") String localKey, @QueryParam(value="typeId") TypeId remoteTypeId, @QueryParam(value="key") String remoteKey, @QueryParam(value="applicationId") String applicationIdString, @QueryParam(value="reciprocate") Boolean reciprocate) {
        boolean deleteSucceeded;
        RestUtil.checkParam("type", remoteTypeId);
        RestUtil.checkParam("key", localKey);
        RestUtil.checkParam("applicationId", applicationIdString);
        ApplicationId applicationId = new ApplicationId(applicationIdString);
        EntityType localType = this.typeAccessor.loadEntityType(localTypeId.get());
        EntityType remoteType = this.typeAccessor.loadEntityType(remoteTypeId.get());
        this.checkPermissionToManageEntityLink(localKey, localType);
        EntityLink entity = this.entityLinkService.getEntityLink(localKey, localType.getClass(), remoteKey, remoteType.getClass(), applicationId);
        if (entity == null) {
            return EntityLinkResource.linkNotFound(localType.getClass(), localKey, remoteType.getClass(), remoteKey, applicationId);
        }
        if (reciprocate != null && reciprocate.booleanValue()) {
            try {
                deleteSucceeded = this.entityLinkService.deleteReciprocatedEntityLink(localKey, localType.getClass(), entity);
            }
            catch (CredentialsRequiredException e) {
                return RestUtil.credentialsRequired(this.i18nResolver);
            }
            catch (ReciprocalActionException e) {
                return RestUtil.serverError(this.i18nResolver.getText("applinks.remote.delete.failed", new Serializable[]{e.getMessage()}));
            }
        } else {
            deleteSucceeded = this.entityLinkService.deleteEntityLink(localKey, localType.getClass(), entity);
        }
        if (deleteSucceeded) {
            return RestUtil.ok();
        }
        return RestUtil.serverError("Failed to delete link " + entity);
    }

    protected void checkPermissionToManageEntityLink(String localKey, EntityType localType) {
        EntityReference localEntityReference = this.internalHostApplication.toEntityReference(localKey, localType.getClass());
        if (!this.internalHostApplication.canManageEntityLinksFor(localEntityReference)) {
            throw new WebApplicationException(RestUtil.unauthorized("You are not authorized to create a link for entity  with key '" + localKey + "' and type '" + localType.getClass() + "'"));
        }
    }

    private static Response applicationTypeNotInstalled(ApplicationId id, String type) {
        return RestUtil.badRequest(String.format("Failed to load application %s as the %s type is not installed", id, type));
    }

    private static Response linkNotFound(Class<? extends EntityType> localType, String localKey, Class<? extends EntityType> remoteType, String remoteKey, ApplicationId applicationId) {
        return RestUtil.notFound(String.format("Couldn't find link to %s:%s (%s) from local entity %s:%s", remoteType.getName(), remoteKey, applicationId.get(), localType.getName(), localKey));
    }

    private static List<EntityLinkEntity> toRestApplicationEntities(Iterable<EntityLink> entities) {
        ArrayList<EntityLinkEntity> transformed = new ArrayList<EntityLinkEntity>();
        Iterables.addAll(transformed, (Iterable)Iterables.transform(entities, (Function)new Function<EntityLink, EntityLinkEntity>(){

            public EntityLinkEntity apply(EntityLink from) {
                return new EntityLinkEntity(from);
            }
        }));
        return transformed;
    }
}

