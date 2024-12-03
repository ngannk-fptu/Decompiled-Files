/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AbstractClientApplicationBase;
import com.microsoft.aad.msal4j.Authority;
import com.microsoft.aad.msal4j.CacheTelemetry;
import com.microsoft.aad.msal4j.IUserAssertion;
import com.microsoft.aad.msal4j.MsalRequest;
import com.microsoft.aad.msal4j.RequestContext;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.aad.msal4j.StringHelper;
import java.net.MalformedURLException;
import java.net.URL;

class SilentRequest
extends MsalRequest {
    private SilentParameters parameters;
    private IUserAssertion assertion;
    private Authority requestAuthority;

    SilentRequest(SilentParameters parameters, AbstractClientApplicationBase application, RequestContext requestContext, IUserAssertion assertion) throws MalformedURLException {
        super(application, null, requestContext);
        this.parameters = parameters;
        this.assertion = assertion;
        Authority authority = this.requestAuthority = StringHelper.isBlank(parameters.authorityUrl()) ? application.authenticationAuthority : Authority.createAuthority(new URL(Authority.enforceTrailingSlash(parameters.authorityUrl())));
        if (parameters.forceRefresh()) {
            application.getServiceBundle().getServerSideTelemetry().getCurrentRequest().cacheInfo(CacheTelemetry.REFRESH_FORCE_REFRESH.telemetryValue);
        }
    }

    public SilentParameters parameters() {
        return this.parameters;
    }

    public IUserAssertion assertion() {
        return this.assertion;
    }

    public Authority requestAuthority() {
        return this.requestAuthority;
    }
}

