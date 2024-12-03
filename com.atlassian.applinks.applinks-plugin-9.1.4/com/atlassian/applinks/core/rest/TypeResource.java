/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.applinks.core.rest;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.model.EntityTypeEntity;
import com.atlassian.applinks.core.rest.model.EntityTypeListEntity;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="type")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@Singleton
@InterceptorChain(value={ContextInterceptor.class, NoCacheHeaderInterceptor.class})
public class TypeResource {
    private final InternalTypeAccessor typeAccessor;

    public TypeResource(InternalTypeAccessor typeAccessor) {
        this.typeAccessor = typeAccessor;
    }

    @GET
    @Path(value="entity/{applicationType}")
    public Response getEntityTypesForApplicationType(@PathParam(value="applicationType") TypeId applicationTypeId) {
        ApplicationType applicationType = this.typeAccessor.loadApplicationType(applicationTypeId);
        if (applicationType == null) {
            return RestUtil.badRequest(String.format("ApplicationType with id %s not installed", applicationTypeId));
        }
        return this.response(this.typeAccessor.getEntityTypesForApplicationType(applicationTypeId));
    }

    private Response response(Iterable<? extends EntityType> types) {
        return RestUtil.ok(new EntityTypeListEntity(Lists.newArrayList((Iterable)Iterables.transform(types, (Function)new Function<EntityType, EntityTypeEntity>(){

            public EntityTypeEntity apply(EntityType from) {
                return new EntityTypeEntity(from);
            }
        }))));
    }
}

