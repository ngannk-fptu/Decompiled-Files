/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 *  com.atlassian.streams.api.StreamsException
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.streams.internal.rest.resources;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.atlassian.streams.api.StreamsException;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.internal.ActivityProviders;
import com.atlassian.streams.internal.AppLinksActivityProvider;
import com.atlassian.streams.internal.rest.resources.whitelist.Whitelist;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="url-proxy")
public class UrlProxyResource {
    private final ActivityProviders activityProviders;
    private final Whitelist whitelist;

    public UrlProxyResource(ActivityProviders activityProviders, Whitelist whitelist) {
        this.activityProviders = (ActivityProviders)Preconditions.checkNotNull((Object)activityProviders, (Object)"activityProviders");
        this.whitelist = (Whitelist)Preconditions.checkNotNull((Object)whitelist, (Object)"whitelist");
    }

    @GET
    public Response get(@QueryParam(value="url") String url) {
        return this.execute(url, Request.MethodType.GET, null);
    }

    @POST
    @Consumes(value={"application/json"})
    public Response post(@QueryParam(value="url") String url) {
        return this.execute(url, Request.MethodType.POST, "application/json");
    }

    @PUT
    @Consumes(value={"application/json"})
    public Response put(@QueryParam(value="url") String url) {
        return this.execute(url, Request.MethodType.PUT, "application/json");
    }

    private Response execute(String url, Request.MethodType methodType, @Nullable String contentType) {
        Uri uri = Uri.parse((String)url);
        if (!this.whitelist.allows(uri.toJavaUri())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)"").build();
        }
        Option<AppLinksActivityProvider> provider = this.activityProviders.getRemoteProviderForUri(uri);
        try {
            Response response;
            if (!provider.isDefined()) {
                return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)"").build();
            }
            Request<?, com.atlassian.sal.api.net.Response> request = ((AppLinksActivityProvider)provider.get()).createRequest(url, methodType);
            request.setFollowRedirects(false);
            if (contentType != null) {
                request.addHeader("Content-Type", contentType);
            }
            if (401 == (response = this.executeRequest(request)).getStatus()) {
                return this.retryRequestAsAnonymous((AppLinksActivityProvider)provider.get(), url, methodType);
            }
            return response;
        }
        catch (CredentialsRequiredException cre) {
            return this.retryRequestAsAnonymous((AppLinksActivityProvider)provider.get(), url, methodType);
        }
    }

    private Response retryRequestAsAnonymous(AppLinksActivityProvider provider, String url, Request.MethodType methodType) {
        try {
            return this.executeRequest(provider.createAnonymousRequest(url, methodType));
        }
        catch (CredentialsRequiredException cre) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)"").build();
        }
    }

    private Response executeRequest(Request<?, com.atlassian.sal.api.net.Response> request) {
        try {
            return Response.status((int)((com.atlassian.sal.api.net.Response)request.executeAndReturn((ReturningResponseHandler)new ProxyResponseHandler())).getStatusCode()).entity((Object)"").build();
        }
        catch (ResponseException e) {
            throw new StreamsException((Throwable)e);
        }
    }

    private final class ProxyResponseHandler
    implements ReturningResponseHandler<com.atlassian.sal.api.net.Response, com.atlassian.sal.api.net.Response> {
        private ProxyResponseHandler() {
        }

        public com.atlassian.sal.api.net.Response handle(com.atlassian.sal.api.net.Response response) throws ResponseException {
            return response;
        }
    }
}

