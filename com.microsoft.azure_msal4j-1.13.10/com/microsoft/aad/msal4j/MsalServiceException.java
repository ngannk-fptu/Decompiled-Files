/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AadInstanceDiscoveryResponse;
import com.microsoft.aad.msal4j.ErrorResponse;
import com.microsoft.aad.msal4j.MsalException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MsalServiceException
extends MsalException {
    private Integer statusCode;
    private String statusMessage;
    private String correlationId;
    private String claims;
    private Map<String, List<String>> headers;
    private String subError;

    public MsalServiceException(String message, String error) {
        super(message, error);
    }

    public MsalServiceException(ErrorResponse errorResponse, Map<String, List<String>> httpHeaders) {
        super(errorResponse.errorDescription, errorResponse.error());
        this.statusCode = errorResponse.statusCode();
        this.statusMessage = errorResponse.statusMessage();
        this.subError = errorResponse.subError();
        this.correlationId = errorResponse.correlation_id();
        this.claims = errorResponse.claims();
        this.headers = Collections.unmodifiableMap(httpHeaders);
    }

    public MsalServiceException(AadInstanceDiscoveryResponse discoveryResponse) {
        super(discoveryResponse.errorDescription(), discoveryResponse.error());
        this.correlationId = discoveryResponse.correlationId();
    }

    public Integer statusCode() {
        return this.statusCode;
    }

    public String statusMessage() {
        return this.statusMessage;
    }

    public String correlationId() {
        return this.correlationId;
    }

    public String claims() {
        return this.claims;
    }

    public Map<String, List<String>> headers() {
        return this.headers;
    }

    String subError() {
        return this.subError;
    }
}

