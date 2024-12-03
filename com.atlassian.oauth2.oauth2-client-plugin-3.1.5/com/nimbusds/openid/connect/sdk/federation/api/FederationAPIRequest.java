/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.oauth2.sdk.AbstractRequest;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import com.nimbusds.openid.connect.sdk.federation.api.OperationType;
import java.net.URI;
import java.util.List;
import java.util.Map;

public abstract class FederationAPIRequest
extends AbstractRequest {
    private final OperationType operationType;

    public FederationAPIRequest(URI endpoint, OperationType operationType) {
        super(endpoint);
        if (operationType == null) {
            throw new IllegalArgumentException("The operation type must not be null");
        }
        this.operationType = operationType;
    }

    public OperationType getOperationType() {
        return this.operationType;
    }

    public abstract Map<String, List<String>> toParameters();

    @Override
    public HTTPRequest toHTTPRequest() {
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.GET, this.getEndpointURI());
        httpRequest.setQuery(URLUtils.serializeParameters(this.toParameters()));
        return httpRequest;
    }
}

