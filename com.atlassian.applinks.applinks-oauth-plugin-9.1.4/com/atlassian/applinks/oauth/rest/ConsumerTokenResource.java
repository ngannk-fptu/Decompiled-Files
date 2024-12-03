/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor
 *  com.atlassian.applinks.core.rest.context.ContextInterceptor
 *  com.atlassian.applinks.core.rest.util.RestUtil
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.applinks.internal.common.rest.util.RestApplicationIdParser
 *  com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.sal.api.user.UserManager
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.applinks.oauth.rest;

import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.internal.common.rest.util.RestApplicationIdParser;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="consumer-token")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Singleton
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class ConsumerTokenResource {
    private final ConsumerTokenStoreService consumerTokenStoreService;
    private final UserManager userManager;

    public ConsumerTokenResource(ConsumerTokenStoreService consumerTokenStoreService, UserManager userManager) {
        this.consumerTokenStoreService = consumerTokenStoreService;
        this.userManager = userManager;
    }

    @DELETE
    @Path(value="{id}")
    public Response removeConsumerToken(@PathParam(value="id") String id) {
        if (this.userManager.getRemoteUserKey() != null) {
            this.consumerTokenStoreService.removeConsumerToken(RestApplicationIdParser.parseApplicationId((String)id), this.userManager.getRemoteUser().getUsername());
            return RestUtil.noContent();
        }
        return RestUtil.unauthorized((String)"User is not authorized");
    }
}

