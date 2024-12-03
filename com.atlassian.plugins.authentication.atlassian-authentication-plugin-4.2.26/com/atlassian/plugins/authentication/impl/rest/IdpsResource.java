/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.google.common.base.Preconditions
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.inject.Inject
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 */
package com.atlassian.plugins.authentication.impl.rest;

import com.atlassian.plugins.authentication.impl.rest.IdpsResourceService;
import com.atlassian.plugins.authentication.impl.rest.model.IdpConfigEntity;
import com.atlassian.plugins.authentication.impl.rest.model.PATCH;
import com.atlassian.plugins.authentication.impl.rest.model.RestPage;
import com.atlassian.plugins.authentication.impl.rest.model.RestPageRequest;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.google.common.base.Preconditions;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Objects;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path(value="/idps")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class IdpsResource {
    private final IdpsResourceService idpsResourceService;

    @Inject
    public IdpsResource(IdpsResourceService idpsResourceService) {
        this.idpsResourceService = idpsResourceService;
    }

    @GET
    public RestPage<IdpConfigEntity> getIdps(@Context RestPageRequest pageRequest) {
        return RestPage.fromListPlusOne(this.idpsResourceService.getConfigs(pageRequest), pageRequest);
    }

    @POST
    public IdpConfigEntity addIdp(IdpConfigEntity idp) {
        return this.idpsResourceService.addConfig(idp);
    }

    @GET
    @Path(value="/{id}")
    public IdpConfigEntity getIdp(@PathParam(value="id") Long id) {
        return this.idpsResourceService.getConfig(id);
    }

    @PATCH
    @Path(value="/{id}")
    public IdpConfigEntity updateIdp(@PathParam(value="id") Long id, IdpConfigEntity idpConfigEntity) {
        Preconditions.checkNotNull((Object)id, (Object)"Id must not be null");
        Preconditions.checkArgument((idpConfigEntity.getId() == null || Objects.equals(id, idpConfigEntity.getId()) ? 1 : 0) != 0, (Object)"The ID in the request body must either be null or equal to one in the path");
        return this.idpsResourceService.updateConfig(id, idpConfigEntity);
    }

    @DELETE
    @Path(value="/{id}")
    public IdpConfigEntity removeIdp(@PathParam(value="id") Long id) {
        return this.idpsResourceService.removeConfig(id);
    }
}

