/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AbstractClientApplicationBase;
import com.microsoft.aad.msal4j.AcquireTokenByAuthorizationGrantSupplier;
import com.microsoft.aad.msal4j.AuthenticationResult;
import com.microsoft.aad.msal4j.AuthenticationResultSupplier;
import com.microsoft.aad.msal4j.Authority;
import com.microsoft.aad.msal4j.AuthorityType;
import com.microsoft.aad.msal4j.CacheTelemetry;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.MsalServiceException;
import com.microsoft.aad.msal4j.RefreshTokenParameters;
import com.microsoft.aad.msal4j.RefreshTokenRequest;
import com.microsoft.aad.msal4j.SilentRequest;
import com.microsoft.aad.msal4j.StringHelper;
import java.net.URL;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AcquireTokenSilentSupplier
extends AuthenticationResultSupplier {
    private static final Logger log = LoggerFactory.getLogger(AcquireTokenSilentSupplier.class);
    private SilentRequest silentRequest;

    AcquireTokenSilentSupplier(AbstractClientApplicationBase clientApplication, SilentRequest silentRequest) {
        super(clientApplication, silentRequest);
        this.silentRequest = silentRequest;
    }

    @Override
    AuthenticationResult execute() throws Exception {
        AuthenticationResult res;
        Authority requestAuthority = this.silentRequest.requestAuthority();
        if (requestAuthority.authorityType != AuthorityType.B2C) {
            requestAuthority = this.getAuthorityWithPrefNetworkHost(this.silentRequest.requestAuthority().authority());
        }
        if (this.silentRequest.parameters().account() == null) {
            res = this.clientApplication.tokenCache.getCachedAuthenticationResult(requestAuthority, this.silentRequest.parameters().scopes(), this.clientApplication.clientId(), this.silentRequest.assertion());
        } else {
            boolean afterRefreshOn;
            res = this.clientApplication.tokenCache.getCachedAuthenticationResult(this.silentRequest.parameters().account(), requestAuthority, this.silentRequest.parameters().scopes(), this.clientApplication.clientId());
            if (res == null) {
                throw new MsalClientException("Token not found in the cache", "cache_miss");
            }
            if (!StringHelper.isBlank(res.accessToken())) {
                this.clientApplication.getServiceBundle().getServerSideTelemetry().incrementSilentSuccessfulCount();
            }
            long currTimeStampSec = new Date().getTime() / 1000L;
            boolean bl = afterRefreshOn = res.refreshOn() != null && res.refreshOn() > 0L && res.refreshOn() < currTimeStampSec && res.expiresOn() >= currTimeStampSec;
            if (this.silentRequest.parameters().forceRefresh() || afterRefreshOn || StringHelper.isBlank(res.accessToken())) {
                if (this.silentRequest.parameters().forceRefresh()) {
                    this.clientApplication.getServiceBundle().getServerSideTelemetry().getCurrentRequest().cacheInfo(CacheTelemetry.REFRESH_FORCE_REFRESH.telemetryValue);
                } else if (afterRefreshOn) {
                    this.clientApplication.getServiceBundle().getServerSideTelemetry().getCurrentRequest().cacheInfo(CacheTelemetry.REFRESH_REFRESH_IN.telemetryValue);
                } else if (res.expiresOn() < currTimeStampSec) {
                    this.clientApplication.getServiceBundle().getServerSideTelemetry().getCurrentRequest().cacheInfo(CacheTelemetry.REFRESH_ACCESS_TOKEN_EXPIRED.telemetryValue);
                } else if (StringHelper.isBlank(res.accessToken())) {
                    this.clientApplication.getServiceBundle().getServerSideTelemetry().getCurrentRequest().cacheInfo(CacheTelemetry.REFRESH_NO_ACCESS_TOKEN.telemetryValue);
                }
                if (!StringHelper.isBlank(res.refreshToken())) {
                    if (this.silentRequest.parameters().authorityUrl() == null && !res.account().environment().equals(requestAuthority.host)) {
                        requestAuthority = Authority.createAuthority(new URL(requestAuthority.authority().replace(requestAuthority.host(), res.account().environment())));
                    }
                    RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(RefreshTokenParameters.builder(this.silentRequest.parameters().scopes(), res.refreshToken()).build(), this.silentRequest.application(), this.silentRequest.requestContext(), this.silentRequest);
                    AcquireTokenByAuthorizationGrantSupplier acquireTokenByAuthorisationGrantSupplier = new AcquireTokenByAuthorizationGrantSupplier(this.clientApplication, refreshTokenRequest, requestAuthority);
                    try {
                        res = acquireTokenByAuthorisationGrantSupplier.execute();
                    }
                    catch (MsalServiceException ex) {
                        if (afterRefreshOn && !this.silentRequest.parameters().forceRefresh() && !StringHelper.isBlank(res.accessToken())) {
                            return res;
                        }
                        throw ex;
                    }
                } else {
                    res = null;
                }
            }
        }
        if (res == null || StringHelper.isBlank(res.accessToken())) {
            throw new MsalClientException("Token not found in the cache", "cache_miss");
        }
        log.info("Returning token from cache");
        return res;
    }
}

