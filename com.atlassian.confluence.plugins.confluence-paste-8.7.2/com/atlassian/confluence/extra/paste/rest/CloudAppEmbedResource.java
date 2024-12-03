/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.NonMarshallingRequestFactory
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseStatusException
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.extra.paste.rest;

import com.atlassian.sal.api.net.NonMarshallingRequestFactory;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseStatusException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/cloudappembed")
public class CloudAppEmbedResource {
    private static final String OEMBED_URL = "http://cl.ly/";
    private NonMarshallingRequestFactory requestFactory;

    public CloudAppEmbedResource(NonMarshallingRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    @GET
    @Produces(value={"application/json"})
    public Response getOEmbed(@QueryParam(value="resource") String resource) throws IOException {
        String url = OEMBED_URL + URLEncoder.encode(resource, StandardCharsets.UTF_8.name());
        Request request = this.requestFactory.createRequest(Request.MethodType.GET, url);
        request.setHeader("Accept", "application/json");
        try {
            return Response.ok((Object)request.execute()).build();
        }
        catch (ResponseStatusException e) {
            return Response.status((int)e.getResponse().getStatusCode()).build();
        }
        catch (Exception e) {
            return Response.serverError().build();
        }
    }
}

