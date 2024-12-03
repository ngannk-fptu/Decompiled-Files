/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.federation.api.TrustNegotiationResponse;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class TrustNegotiationSuccessResponse
extends TrustNegotiationResponse {
    private final JSONObject metadata;

    public TrustNegotiationSuccessResponse(JSONObject metadata) {
        if (metadata == null) {
            throw new IllegalArgumentException("The metadata JSON object must not be null");
        }
        this.metadata = metadata;
    }

    public JSONObject getMetadataJSONObject() {
        return this.metadata;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        HTTPResponse httpResponse = new HTTPResponse(200);
        httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
        httpResponse.setContent(this.getMetadataJSONObject().toJSONString());
        return httpResponse;
    }

    public static TrustNegotiationSuccessResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(200);
        JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        return new TrustNegotiationSuccessResponse(jsonObject);
    }
}

