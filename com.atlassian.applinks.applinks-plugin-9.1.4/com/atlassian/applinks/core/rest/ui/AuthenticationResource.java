/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.applinks.core.rest.ui;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.core.manifest.AppLinksManifestDownloader;
import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="authenticationinfo")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class AuthenticationResource {
    private final AppLinksManifestDownloader downloader;

    public AuthenticationResource(AppLinksManifestDownloader downloader) {
        this.downloader = downloader;
    }

    @GET
    public Response getIsAdminUser() {
        return RestUtil.ok();
    }

    @GET
    @Path(value="id/{applinkId}/url/{url:.*$}")
    public Response rpcUrlIsReachable(@PathParam(value="applinkId") String applicationId, @PathParam(value="url") URI uri, @QueryParam(value="url") URI qUrl) {
        if (qUrl != null) {
            uri = qUrl;
        }
        try {
            Manifest manifest = this.downloader.download(uri);
            if (manifest.getId().equals((Object)new ApplicationId(applicationId))) {
                return RestUtil.ok();
            }
            return RestUtil.notFound("");
        }
        catch (ManifestNotFoundException e) {
            return RestUtil.notFound("");
        }
    }
}

