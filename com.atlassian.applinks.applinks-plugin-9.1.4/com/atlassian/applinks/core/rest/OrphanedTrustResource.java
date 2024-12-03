/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.applinks.core.rest;

import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import com.atlassian.applinks.core.auth.OrphanedTrustDetector;
import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.model.OrphanedTrustEntityList;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="orphaned-trust")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@WebSudoRequired
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class OrphanedTrustResource {
    private final OrphanedTrustDetector orphanedTrustDetector;

    public OrphanedTrustResource(@Qualifier(value="delegatingOrphanedTrustDetector") OrphanedTrustDetector orphanedTrustDetector) {
        this.orphanedTrustDetector = orphanedTrustDetector;
    }

    @GET
    public Response getIds() {
        return Response.ok((Object)new OrphanedTrustEntityList(this.orphanedTrustDetector.findOrphanedTrustCertificates())).build();
    }

    @DELETE
    @Path(value="{type}/{id}")
    public Response delete(@PathParam(value="type") String typeStr, @PathParam(value="id") String id) {
        OrphanedTrustCertificate.Type type;
        try {
            type = OrphanedTrustCertificate.Type.valueOf(typeStr);
        }
        catch (IllegalArgumentException e) {
            return RestUtil.badRequest("Invalid type parameter: " + typeStr);
        }
        this.orphanedTrustDetector.deleteTrustCertificate(id, type);
        return RestUtil.ok("Deleted certificate with id: " + id);
    }
}

