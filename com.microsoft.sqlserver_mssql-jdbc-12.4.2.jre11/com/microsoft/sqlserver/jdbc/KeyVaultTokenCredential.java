/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.azure.core.credential.AccessToken
 *  com.azure.core.credential.TokenCredential
 *  com.azure.core.credential.TokenRequestContext
 *  com.microsoft.aad.msal4j.ClientCredentialFactory
 *  com.microsoft.aad.msal4j.ClientCredentialParameters
 *  com.microsoft.aad.msal4j.ConfidentialClientApplication
 *  com.microsoft.aad.msal4j.ConfidentialClientApplication$Builder
 *  com.microsoft.aad.msal4j.IAuthenticationResult
 *  com.microsoft.aad.msal4j.IClientCredential
 *  com.microsoft.aad.msal4j.IClientSecret
 *  com.microsoft.aad.msal4j.SilentParameters
 *  com.microsoft.aad.msal4j.SilentParameters$SilentParametersBuilder
 *  reactor.core.publisher.Mono
 */
package com.microsoft.sqlserver.jdbc;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCredential;
import com.microsoft.aad.msal4j.IClientSecret;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerKeyVaultAuthenticationCallback;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import reactor.core.publisher.Mono;

class KeyVaultTokenCredential
implements TokenCredential {
    private static final String NULL_VALUE = "R_NullValue";
    private final String clientId;
    private final String clientSecret;
    private final SQLServerKeyVaultAuthenticationCallback authenticationCallback;
    private String authorization;
    private ConfidentialClientApplication confidentialClientApplication;
    private String resource;
    private String scope;

    KeyVaultTokenCredential(String clientId, String clientSecret) throws SQLServerException {
        if (null == clientId || clientId.isEmpty()) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(NULL_VALUE));
            Object[] msgArgs1 = new Object[]{"Client ID"};
            throw new SQLServerException(form.format(msgArgs1), null);
        }
        if (null == clientSecret || clientSecret.isEmpty()) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(NULL_VALUE));
            Object[] msgArgs1 = new Object[]{"Client Secret"};
            throw new SQLServerException(form.format(msgArgs1), null);
        }
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authenticationCallback = null;
    }

    KeyVaultTokenCredential(SQLServerKeyVaultAuthenticationCallback authenticationCallback) {
        this.authenticationCallback = authenticationCallback;
        this.clientId = null;
        this.clientSecret = null;
    }

    public Mono<AccessToken> getToken(TokenRequestContext request) {
        if (null != this.authenticationCallback) {
            String accessToken = this.authenticationCallback.getAccessToken(this.authorization, this.resource, this.scope);
            return Mono.just((Object)new AccessToken(accessToken, OffsetDateTime.MIN));
        }
        return this.authenticateWithConfidentialClientCache(request).onErrorResume(t -> Mono.empty()).switchIfEmpty(Mono.defer(() -> this.authenticateWithConfidentialClient(request)));
    }

    KeyVaultTokenCredential setAuthorization(String authorization) {
        if (null != this.authorization && this.authorization.equals(authorization)) {
            return this;
        }
        this.authorization = authorization;
        this.confidentialClientApplication = this.getConfidentialClientApplication();
        return this;
    }

    private ConfidentialClientApplication getConfidentialClientApplication() {
        if (null == this.clientId) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(NULL_VALUE));
            Object[] msgArgs1 = new Object[]{"Client ID"};
            throw new IllegalArgumentException(form.format(msgArgs1), null);
        }
        if (null == this.authorization) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(NULL_VALUE));
            Object[] msgArgs1 = new Object[]{"Authorization"};
            throw new IllegalArgumentException(form.format(msgArgs1), null);
        }
        if (null == this.clientSecret) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(NULL_VALUE));
            Object[] msgArgs1 = new Object[]{"Client Secret"};
            throw new IllegalArgumentException(form.format(msgArgs1), null);
        }
        IClientSecret credential = ClientCredentialFactory.createFromSecret((String)this.clientSecret);
        ConfidentialClientApplication.Builder applicationBuilder = ConfidentialClientApplication.builder((String)this.clientId, (IClientCredential)credential);
        try {
            applicationBuilder = (ConfidentialClientApplication.Builder)applicationBuilder.authority(this.authorization);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return applicationBuilder.build();
    }

    private Mono<AccessToken> authenticateWithConfidentialClientCache(TokenRequestContext request) {
        return Mono.fromFuture(() -> {
            SilentParameters.SilentParametersBuilder parametersBuilder = SilentParameters.builder(new HashSet(request.getScopes()));
            try {
                return this.confidentialClientApplication.acquireTokenSilently(parametersBuilder.build());
            }
            catch (MalformedURLException e) {
                return this.getFailedCompletableFuture(new RuntimeException(e));
            }
        }).map(ar -> new AccessToken(ar.accessToken(), OffsetDateTime.ofInstant(ar.expiresOnDate().toInstant(), ZoneOffset.UTC))).filter(t -> !t.isExpired());
    }

    private CompletableFuture<IAuthenticationResult> getFailedCompletableFuture(Exception e) {
        CompletableFuture<IAuthenticationResult> completableFuture = new CompletableFuture<IAuthenticationResult>();
        completableFuture.completeExceptionally(e);
        return completableFuture;
    }

    private Mono<AccessToken> authenticateWithConfidentialClient(TokenRequestContext request) {
        return Mono.fromFuture(() -> this.confidentialClientApplication.acquireToken(ClientCredentialParameters.builder(new HashSet(request.getScopes())).build())).map(ar -> new AccessToken(ar.accessToken(), OffsetDateTime.ofInstant(ar.expiresOnDate().toInstant(), ZoneOffset.UTC)));
    }

    void setResource(String resource) {
        this.resource = resource;
    }

    void setScope(String scope) {
        this.scope = scope;
    }
}

