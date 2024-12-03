/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.auth.ClientAuthentication
 *  com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
 *  com.nimbusds.oauth2.sdk.id.ClientID
 *  org.slf4j.LoggerFactory
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AbstractClientApplicationBase;
import com.microsoft.aad.msal4j.AuthorityType;
import com.microsoft.aad.msal4j.ClientAuthenticationPost;
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters;
import com.microsoft.aad.msal4j.DeviceCodeFlowRequest;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IPublicClientApplication;
import com.microsoft.aad.msal4j.IntegratedWindowsAuthenticationParameters;
import com.microsoft.aad.msal4j.IntegratedWindowsAuthenticationRequest;
import com.microsoft.aad.msal4j.InteractiveRequest;
import com.microsoft.aad.msal4j.InteractiveRequestParameters;
import com.microsoft.aad.msal4j.ParameterValidationUtils;
import com.microsoft.aad.msal4j.PublicApi;
import com.microsoft.aad.msal4j.RequestContext;
import com.microsoft.aad.msal4j.UserIdentifier;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import com.microsoft.aad.msal4j.UserNamePasswordRequest;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.id.ClientID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.LoggerFactory;

public class PublicClientApplication
extends AbstractClientApplicationBase
implements IPublicClientApplication {
    private final ClientAuthenticationPost clientAuthentication;

    @Override
    public CompletableFuture<IAuthenticationResult> acquireToken(UserNamePasswordParameters parameters) {
        ParameterValidationUtils.validateNotNull("parameters", parameters);
        RequestContext context = new RequestContext(this, PublicApi.ACQUIRE_TOKEN_BY_USERNAME_PASSWORD, parameters, UserIdentifier.fromUpn(parameters.username()));
        UserNamePasswordRequest userNamePasswordRequest = new UserNamePasswordRequest(parameters, this, context);
        return this.executeRequest(userNamePasswordRequest);
    }

    @Override
    public CompletableFuture<IAuthenticationResult> acquireToken(IntegratedWindowsAuthenticationParameters parameters) {
        ParameterValidationUtils.validateNotNull("parameters", parameters);
        RequestContext context = new RequestContext(this, PublicApi.ACQUIRE_TOKEN_BY_INTEGRATED_WINDOWS_AUTH, parameters, UserIdentifier.fromUpn(parameters.username()));
        IntegratedWindowsAuthenticationRequest integratedWindowsAuthenticationRequest = new IntegratedWindowsAuthenticationRequest(parameters, this, context);
        return this.executeRequest(integratedWindowsAuthenticationRequest);
    }

    @Override
    public CompletableFuture<IAuthenticationResult> acquireToken(DeviceCodeFlowParameters parameters) {
        if (AuthorityType.B2C.equals((Object)this.authenticationAuthority.authorityType())) {
            throw new IllegalArgumentException("Invalid authority type. Device Flow is not supported by B2C authority.");
        }
        ParameterValidationUtils.validateNotNull("parameters", parameters);
        RequestContext context = new RequestContext(this, PublicApi.ACQUIRE_TOKEN_BY_DEVICE_CODE_FLOW, parameters);
        AtomicReference<CompletableFuture<IAuthenticationResult>> futureReference = new AtomicReference<CompletableFuture<IAuthenticationResult>>();
        DeviceCodeFlowRequest deviceCodeRequest = new DeviceCodeFlowRequest(parameters, futureReference, this, context);
        CompletableFuture<IAuthenticationResult> future = this.executeRequest(deviceCodeRequest);
        futureReference.set(future);
        return future;
    }

    @Override
    public CompletableFuture<IAuthenticationResult> acquireToken(InteractiveRequestParameters parameters) {
        ParameterValidationUtils.validateNotNull("parameters", parameters);
        AtomicReference<CompletableFuture<IAuthenticationResult>> futureReference = new AtomicReference<CompletableFuture<IAuthenticationResult>>();
        RequestContext context = new RequestContext(this, PublicApi.ACQUIRE_TOKEN_INTERACTIVE, parameters, UserIdentifier.fromUpn(parameters.loginHint()));
        InteractiveRequest interactiveRequest = new InteractiveRequest(parameters, futureReference, this, context);
        CompletableFuture<IAuthenticationResult> future = this.executeRequest(interactiveRequest);
        futureReference.set(future);
        return future;
    }

    private PublicClientApplication(Builder builder) {
        super(builder);
        ParameterValidationUtils.validateNotBlank("clientId", this.clientId());
        this.log = LoggerFactory.getLogger(PublicClientApplication.class);
        this.clientAuthentication = new ClientAuthenticationPost(ClientAuthenticationMethod.NONE, new ClientID(this.clientId()));
    }

    @Override
    protected ClientAuthentication clientAuthentication() {
        return this.clientAuthentication;
    }

    public static Builder builder(String clientId) {
        return new Builder(clientId);
    }

    public static class Builder
    extends AbstractClientApplicationBase.Builder<Builder> {
        private Builder(String clientId) {
            super(clientId);
        }

        @Override
        public PublicClientApplication build() {
            return new PublicClientApplication(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}

