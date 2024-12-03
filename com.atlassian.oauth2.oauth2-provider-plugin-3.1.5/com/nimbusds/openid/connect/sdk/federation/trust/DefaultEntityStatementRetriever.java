/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.trust;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.WellKnownPathComposeStrategy;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.federation.api.FederationAPIError;
import com.nimbusds.openid.connect.sdk.federation.api.FetchEntityStatementRequest;
import com.nimbusds.openid.connect.sdk.federation.api.FetchEntityStatementResponse;
import com.nimbusds.openid.connect.sdk.federation.config.FederationEntityConfigurationRequest;
import com.nimbusds.openid.connect.sdk.federation.config.FederationEntityConfigurationResponse;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import com.nimbusds.openid.connect.sdk.federation.trust.EntityStatementRetriever;
import com.nimbusds.openid.connect.sdk.federation.trust.ResolveException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class DefaultEntityStatementRetriever
implements EntityStatementRetriever {
    private final int httpConnectTimeoutMs;
    private final int httpReadTimeoutMs;
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT_MS = 1000;
    public static final int DEFAULT_HTTP_READ_TIMEOUT_MS = 1000;
    private final List<URI> recordedRequests = new LinkedList<URI>();

    public DefaultEntityStatementRetriever() {
        this(1000, 1000);
    }

    public DefaultEntityStatementRetriever(int httpConnectTimeoutMs, int httpReadTimeoutMs) {
        this.httpConnectTimeoutMs = httpConnectTimeoutMs;
        this.httpReadTimeoutMs = httpReadTimeoutMs;
    }

    public int getHTTPConnectTimeout() {
        return this.httpConnectTimeoutMs;
    }

    public int getHTTPReadTimeout() {
        return this.httpReadTimeoutMs;
    }

    void applyTimeouts(HTTPRequest httpRequest) {
        httpRequest.setConnectTimeout(this.httpConnectTimeoutMs);
        httpRequest.setReadTimeout(this.httpReadTimeoutMs);
    }

    @Override
    public EntityStatement fetchSelfIssuedEntityStatement(EntityID target) throws ResolveException {
        FederationEntityConfigurationResponse response;
        HTTPResponse httpResponse;
        FederationEntityConfigurationRequest request = new FederationEntityConfigurationRequest(target);
        HTTPRequest httpRequest = request.toHTTPRequest();
        this.applyTimeouts(httpRequest);
        this.record(httpRequest);
        try {
            httpResponse = httpRequest.send();
        }
        catch (IOException e) {
            throw new ResolveException("Couldn't retrieve entity configuration for " + httpRequest.getURL() + ": " + e.getMessage(), e);
        }
        if (StringUtils.isNotBlank(target.toURI().getPath()) && 404 == httpResponse.getStatusCode()) {
            request = new FederationEntityConfigurationRequest(target, WellKnownPathComposeStrategy.INFIX);
            httpRequest = request.toHTTPRequest();
            this.applyTimeouts(httpRequest);
            this.record(httpRequest);
            try {
                httpResponse = httpRequest.send();
            }
            catch (IOException e) {
                throw new ResolveException("Couldn't retrieve entity configuration for " + httpRequest.getURL() + ": " + e.getMessage(), e);
            }
        }
        try {
            response = FederationEntityConfigurationResponse.parse(httpResponse);
        }
        catch (ParseException e) {
            throw new ResolveException("Error parsing entity configuration response from " + httpRequest.getURL() + ": " + e.getMessage(), e);
        }
        if (!response.indicatesSuccess()) {
            ErrorObject errorObject = response.toErrorResponse().getErrorObject();
            throw new ResolveException("Entity configuration error response from " + httpRequest.getURL() + ": " + errorObject.getHTTPStatusCode() + (errorObject.getCode() != null ? " " + errorObject.getCode() : ""), errorObject);
        }
        return response.toSuccessResponse().getEntityStatement();
    }

    @Override
    public EntityStatement fetchEntityStatement(URI federationAPIEndpoint, EntityID issuer, EntityID subject) throws ResolveException {
        FetchEntityStatementResponse response;
        HTTPResponse httpResponse;
        FetchEntityStatementRequest request = new FetchEntityStatementRequest(federationAPIEndpoint, issuer, subject, null);
        HTTPRequest httpRequest = request.toHTTPRequest();
        this.applyTimeouts(httpRequest);
        this.record(httpRequest);
        try {
            httpResponse = httpRequest.send();
        }
        catch (IOException e) {
            throw new ResolveException("Couldn't fetch entity statement from " + issuer + " at " + federationAPIEndpoint + ": " + e.getMessage(), e);
        }
        try {
            response = FetchEntityStatementResponse.parse(httpResponse);
        }
        catch (ParseException e) {
            throw new ResolveException("Error parsing entity statement response from " + issuer + " at " + federationAPIEndpoint + ": " + e.getMessage(), e);
        }
        if (!response.indicatesSuccess()) {
            FederationAPIError errorObject = response.toErrorResponse().getErrorObject();
            throw new ResolveException("Entity statement error response from " + issuer + " at " + federationAPIEndpoint + ": " + errorObject.getHTTPStatusCode() + (errorObject.getCode() != null ? " " + errorObject.getCode() : ""), errorObject);
        }
        return response.toSuccessResponse().getEntityStatement();
    }

    private void record(HTTPRequest httpRequest) {
        URI uri = null;
        if (httpRequest.getQuery() == null) {
            uri = httpRequest.getURI();
        } else {
            try {
                uri = new URI(httpRequest.getURL() + "?" + httpRequest.getQuery());
            }
            catch (URISyntaxException uRISyntaxException) {
                // empty catch block
            }
        }
        this.recordedRequests.add(uri);
    }

    public List<URI> getRecordedRequests() {
        return this.recordedRequests;
    }
}

