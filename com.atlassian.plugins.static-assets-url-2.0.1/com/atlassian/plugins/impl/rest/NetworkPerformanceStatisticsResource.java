/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.plugins.impl.rest;

import com.atlassian.plugins.impl.NetworkPerformanceStatisticsService;
import com.atlassian.plugins.impl.rest.NetworkPerformanceStatisticsEntity;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/network-statistics")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class NetworkPerformanceStatisticsResource {
    private NetworkPerformanceStatisticsService networkStatisticsService;

    public NetworkPerformanceStatisticsResource(NetworkPerformanceStatisticsService networkStatisticsService) {
        this.networkStatisticsService = networkStatisticsService;
    }

    @GET
    public Response get() {
        return Response.ok((Object)new NetworkPerformanceStatisticsEntity(this.networkStatisticsService.getRecentTransferCosts())).build();
    }
}

