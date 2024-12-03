/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.inject.Inject
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.plugins.authentication.impl.basicauth.rest;

import com.atlassian.plugins.authentication.impl.basicauth.rest.model.BasicAuthConfigEntity;
import com.atlassian.plugins.authentication.impl.basicauth.service.BasicAuthDao;
import com.atlassian.plugins.authentication.impl.basicauth.service.CachingBasicAuthService;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/config")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class BasicAuthResource {
    private final BasicAuthDao basicAuthDao;
    private final CachingBasicAuthService cachingBasicAuthService;

    @Inject
    public BasicAuthResource(BasicAuthDao basicAuthDao, CachingBasicAuthService cachingBasicAuthService) {
        this.basicAuthDao = basicAuthDao;
        this.cachingBasicAuthService = cachingBasicAuthService;
    }

    @GET
    public BasicAuthConfigEntity get() {
        return BasicAuthConfigEntity.fromConfig(this.basicAuthDao.get());
    }

    @PUT
    public Response put(BasicAuthConfigEntity basicAuthConfig) {
        this.basicAuthDao.save(basicAuthConfig::toConfig);
        this.cachingBasicAuthService.update();
        return Response.noContent().build();
    }
}

