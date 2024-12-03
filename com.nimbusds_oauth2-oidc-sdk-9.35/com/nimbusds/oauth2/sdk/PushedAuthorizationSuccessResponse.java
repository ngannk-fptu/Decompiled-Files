/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.PushedAuthorizationResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class PushedAuthorizationSuccessResponse
extends PushedAuthorizationResponse {
    private final URI requestURI;
    private final long lifetime;

    public PushedAuthorizationSuccessResponse(URI requestURI, long lifetime) {
        if (requestURI == null) {
            throw new IllegalArgumentException("The request URI must not be null");
        }
        this.requestURI = requestURI;
        if (lifetime <= 0L) {
            throw new IllegalArgumentException("The request lifetime must be a positive integer");
        }
        this.lifetime = lifetime;
    }

    public URI getRequestURI() {
        return this.requestURI;
    }

    public long getLifetime() {
        return this.lifetime;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put((Object)"request_uri", (Object)this.getRequestURI().toString());
        o.put((Object)"expires_in", (Object)this.getLifetime());
        return o;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        HTTPResponse httpResponse = new HTTPResponse(201);
        httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
        httpResponse.setContent(this.toJSONObject().toString());
        return httpResponse;
    }

    public static PushedAuthorizationSuccessResponse parse(JSONObject jsonObject) throws ParseException {
        URI requestURI = JSONObjectUtils.getURI(jsonObject, "request_uri");
        long lifetime = JSONObjectUtils.getLong(jsonObject, "expires_in");
        return new PushedAuthorizationSuccessResponse(requestURI, lifetime);
    }

    public static PushedAuthorizationSuccessResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(201, 200);
        JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        return PushedAuthorizationSuccessResponse.parse(jsonObject);
    }
}

