/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AbstractClientApplicationBase;
import com.microsoft.aad.msal4j.IAcquireTokenParameters;
import com.microsoft.aad.msal4j.IClientApplicationBase;
import com.microsoft.aad.msal4j.PublicApi;
import com.microsoft.aad.msal4j.StringHelper;
import com.microsoft.aad.msal4j.UserIdentifier;
import java.util.UUID;

class RequestContext {
    private String telemetryRequestId;
    private String clientId;
    private String correlationId;
    private PublicApi publicApi;
    private String applicationName;
    private String applicationVersion;
    private String authority;
    private IAcquireTokenParameters apiParameters;
    private IClientApplicationBase clientApplication;
    private UserIdentifier userIdentifier;

    public RequestContext(AbstractClientApplicationBase clientApplication, PublicApi publicApi, IAcquireTokenParameters apiParameters) {
        this.clientApplication = clientApplication;
        this.clientId = StringHelper.isBlank(clientApplication.clientId()) ? "unset_client_id" : clientApplication.clientId();
        this.correlationId = StringHelper.isBlank(clientApplication.correlationId()) ? RequestContext.generateNewCorrelationId() : clientApplication.correlationId();
        this.applicationVersion = clientApplication.applicationVersion();
        this.applicationName = clientApplication.applicationName();
        this.publicApi = publicApi;
        this.authority = clientApplication.authority();
        this.apiParameters = apiParameters;
    }

    public RequestContext(AbstractClientApplicationBase clientApplication, PublicApi publicApi, IAcquireTokenParameters apiParameters, UserIdentifier userIdentifier) {
        this(clientApplication, publicApi, apiParameters);
        this.userIdentifier = userIdentifier;
    }

    private static String generateNewCorrelationId() {
        return UUID.randomUUID().toString();
    }

    String telemetryRequestId() {
        return this.telemetryRequestId;
    }

    String clientId() {
        return this.clientId;
    }

    String correlationId() {
        return this.correlationId;
    }

    PublicApi publicApi() {
        return this.publicApi;
    }

    String applicationName() {
        return this.applicationName;
    }

    String applicationVersion() {
        return this.applicationVersion;
    }

    String authority() {
        return this.authority;
    }

    IAcquireTokenParameters apiParameters() {
        return this.apiParameters;
    }

    IClientApplicationBase clientApplication() {
        return this.clientApplication;
    }

    UserIdentifier userIdentifier() {
        return this.userIdentifier;
    }

    RequestContext telemetryRequestId(String telemetryRequestId) {
        this.telemetryRequestId = telemetryRequestId;
        return this;
    }
}

