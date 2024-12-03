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

import com.atlassian.plugins.authentication.impl.rest.JitProvisionedUsersResourceService;
import com.atlassian.plugins.authentication.impl.rest.model.JitUserEntity;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path(value="/jit-users")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class JitProvisionedUsersResource {
    private final JitProvisionedUsersResourceService jitProvisionedUsersResourceService;

    @Inject
    public JitProvisionedUsersResource(JitProvisionedUsersResourceService jitProvisionedUsersResourceService) {
        this.jitProvisionedUsersResourceService = jitProvisionedUsersResourceService;
    }

    @GET
    public List<JitUserEntity> getJitProvisionedUsers() {
        return this.jitProvisionedUsersResourceService.findJitProvisionedUsers();
    }
}

