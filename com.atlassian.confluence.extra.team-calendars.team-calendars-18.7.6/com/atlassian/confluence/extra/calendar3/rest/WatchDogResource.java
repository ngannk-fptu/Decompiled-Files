/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogService;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogServiceStatus;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="watchdog")
@ReadOnlyAccessAllowed
@WebSudoRequired
@Consumes(value={"application/json"})
@InterceptorChain(value={TransactionInterceptor.class})
public class WatchDogResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(WatchDogResource.class);
    private final WatchDogService watchDogService;

    public WatchDogResource(WatchDogService watchDogService) {
        this.watchDogService = watchDogService;
    }

    @Path(value="start")
    @GET
    @Produces(value={"application/json"})
    public Response startWatchDogService() {
        WatchDogServiceStatus status = this.watchDogService.startService();
        return Response.ok((Object)status).build();
    }

    @Path(value="status")
    @GET
    @Produces(value={"application/json"})
    public Response getStatus() {
        WatchDogServiceStatus status = this.watchDogService.getStatus();
        return Response.ok((Object)status).build();
    }
}

