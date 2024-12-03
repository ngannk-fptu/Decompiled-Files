/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.applinks.internal.rest.migration;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.internal.common.capabilities.RemoteApplicationCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteCapabilitiesService;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.rest.util.RestApplicationIdParser;
import com.atlassian.applinks.internal.migration.AuthenticationMigrationService;
import com.atlassian.applinks.internal.migration.AuthenticationStatus;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.internal.rest.interceptor.ServiceExceptionInterceptor;
import com.atlassian.applinks.internal.rest.model.migration.RestAuthenticationStatus;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="migration")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Singleton
@AnonymousAllowed
@WebSudoRequired
@InterceptorChain(value={ContextInterceptor.class, ServiceExceptionInterceptor.class, NoCacheHeaderInterceptor.class})
public class MigrateAuthenticationResource {
    public static final String CONTEXT = "migration";
    private final AuthenticationMigrationService migrationService;
    private final RemoteCapabilitiesService remoteCapabilitiesService;
    private final RestApplicationIdParser applicationIdParser;

    public MigrateAuthenticationResource(AuthenticationMigrationService authenticationMigrationService, RemoteCapabilitiesService remoteCapabilitiesService, RestApplicationIdParser applicationIdParser) {
        this.migrationService = authenticationMigrationService;
        this.applicationIdParser = applicationIdParser;
        this.remoteCapabilitiesService = remoteCapabilitiesService;
    }

    @POST
    @Path(value="{id}")
    public Response migrate(@PathParam(value="id") String id) throws ServiceException {
        ApplicationId applicationId = this.applicationIdParser.parse(id);
        AuthenticationStatus configs = this.migrationService.migrateToOAuth(applicationId);
        RemoteApplicationCapabilities remoteCapabilities = this.remoteCapabilitiesService.getCapabilities(applicationId);
        return RestUtil.ok(new RestAuthenticationStatus(configs, remoteCapabilities));
    }
}

