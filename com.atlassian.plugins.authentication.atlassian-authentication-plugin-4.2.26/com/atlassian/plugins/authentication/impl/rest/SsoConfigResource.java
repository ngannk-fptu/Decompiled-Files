/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.inject.Inject
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 */
package com.atlassian.plugins.authentication.impl.rest;

import com.atlassian.plugins.authentication.impl.rest.SsoConfigResourceService;
import com.atlassian.plugins.authentication.impl.rest.model.PATCH;
import com.atlassian.plugins.authentication.impl.rest.model.SsoConfigEntity;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path(value="/sso")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class SsoConfigResource {
    private final SsoConfigResourceService ssoConfigResourceService;

    @Inject
    public SsoConfigResource(SsoConfigResourceService ssoConfigResourceService) {
        this.ssoConfigResourceService = ssoConfigResourceService;
    }

    @GET
    public SsoConfigEntity getConfig() {
        return this.ssoConfigResourceService.getConfig();
    }

    @PATCH
    public SsoConfigEntity updateConfig(SsoConfigEntity ssoConfigEntity) {
        return this.ssoConfigResourceService.updateConfig(ssoConfigEntity);
    }
}

