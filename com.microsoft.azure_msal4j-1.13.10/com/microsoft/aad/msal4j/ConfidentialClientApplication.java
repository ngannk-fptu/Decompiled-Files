/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.ParseException
 *  com.nimbusds.oauth2.sdk.auth.ClientAuthentication
 *  com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
 *  com.nimbusds.oauth2.sdk.auth.ClientSecretPost
 *  com.nimbusds.oauth2.sdk.auth.PrivateKeyJWT
 *  com.nimbusds.oauth2.sdk.auth.Secret
 *  com.nimbusds.oauth2.sdk.id.ClientID
 *  org.slf4j.LoggerFactory
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AbstractClientApplicationBase;
import com.microsoft.aad.msal4j.AppTokenProviderParameters;
import com.microsoft.aad.msal4j.ClientAssertion;
import com.microsoft.aad.msal4j.ClientCertificate;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ClientCredentialRequest;
import com.microsoft.aad.msal4j.ClientSecret;
import com.microsoft.aad.msal4j.CustomJWTAuthentication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCredential;
import com.microsoft.aad.msal4j.IConfidentialClientApplication;
import com.microsoft.aad.msal4j.JwtHelper;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.OnBehalfOfParameters;
import com.microsoft.aad.msal4j.OnBehalfOfRequest;
import com.microsoft.aad.msal4j.ParameterValidationUtils;
import com.microsoft.aad.msal4j.PublicApi;
import com.microsoft.aad.msal4j.RequestContext;
import com.microsoft.aad.msal4j.TokenProviderResult;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.PrivateKeyJWT;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.slf4j.LoggerFactory;

public class ConfidentialClientApplication
extends AbstractClientApplicationBase
implements IConfidentialClientApplication {
    private ClientAuthentication clientAuthentication;
    private boolean clientCertAuthentication = false;
    private ClientCertificate clientCertificate;
    public Function<AppTokenProviderParameters, CompletableFuture<TokenProviderResult>> appTokenProvider;
    private boolean sendX5c;

    @Override
    public CompletableFuture<IAuthenticationResult> acquireToken(ClientCredentialParameters parameters) {
        ParameterValidationUtils.validateNotNull("parameters", parameters);
        RequestContext context = new RequestContext(this, PublicApi.ACQUIRE_TOKEN_FOR_CLIENT, parameters);
        ClientCredentialRequest clientCredentialRequest = new ClientCredentialRequest(parameters, this, context, this.appTokenProvider);
        return this.executeRequest(clientCredentialRequest);
    }

    @Override
    public CompletableFuture<IAuthenticationResult> acquireToken(OnBehalfOfParameters parameters) {
        ParameterValidationUtils.validateNotNull("parameters", parameters);
        RequestContext context = new RequestContext(this, PublicApi.ACQUIRE_TOKEN_ON_BEHALF_OF, parameters);
        OnBehalfOfRequest oboRequest = new OnBehalfOfRequest(parameters, this, context);
        return this.executeRequest(oboRequest);
    }

    private ConfidentialClientApplication(Builder builder) {
        super(builder);
        this.sendX5c = builder.sendX5c;
        this.appTokenProvider = builder.appTokenProvider;
        this.log = LoggerFactory.getLogger(ConfidentialClientApplication.class);
        this.initClientAuthentication(builder.clientCredential);
    }

    private void initClientAuthentication(IClientCredential clientCredential) {
        ParameterValidationUtils.validateNotNull("clientCredential", clientCredential);
        if (clientCredential instanceof ClientSecret) {
            this.clientAuthentication = new ClientSecretPost(new ClientID(this.clientId()), new Secret(((ClientSecret)clientCredential).clientSecret()));
        } else if (clientCredential instanceof ClientCertificate) {
            this.clientCertAuthentication = true;
            this.clientCertificate = (ClientCertificate)clientCredential;
            this.clientAuthentication = this.buildValidClientCertificateAuthority();
        } else if (clientCredential instanceof ClientAssertion) {
            this.clientAuthentication = this.createClientAuthFromClientAssertion((ClientAssertion)clientCredential);
        } else {
            throw new IllegalArgumentException("Unsupported client credential");
        }
    }

    @Override
    protected ClientAuthentication clientAuthentication() {
        if (this.clientCertAuthentication) {
            Date currentDateTime = new Date(System.currentTimeMillis());
            Date expirationTime = ((PrivateKeyJWT)this.clientAuthentication).getJWTAuthenticationClaimsSet().getExpirationTime();
            if (expirationTime.before(currentDateTime)) {
                this.clientAuthentication = this.buildValidClientCertificateAuthority();
            }
        }
        return this.clientAuthentication;
    }

    private ClientAuthentication buildValidClientCertificateAuthority() {
        ClientAssertion clientAssertion = JwtHelper.buildJwt(this.clientId(), this.clientCertificate, this.authenticationAuthority.selfSignedJwtAudience(), this.sendX5c);
        return this.createClientAuthFromClientAssertion(clientAssertion);
    }

    protected ClientAuthentication createClientAuthFromClientAssertion(ClientAssertion clientAssertion) {
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        try {
            map.put("client_assertion_type", Collections.singletonList("urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
            map.put("client_assertion", Collections.singletonList(clientAssertion.assertion()));
            return PrivateKeyJWT.parse(map);
        }
        catch (ParseException e) {
            if (e.getMessage().contains("Issuer and subject in client JWT assertion must designate the same client identifier")) {
                return new CustomJWTAuthentication(ClientAuthenticationMethod.PRIVATE_KEY_JWT, clientAssertion, new ClientID(this.clientId()));
            }
            throw new MsalClientException(e);
        }
    }

    public static Builder builder(String clientId, IClientCredential clientCredential) {
        return new Builder(clientId, clientCredential);
    }

    @Override
    public boolean sendX5c() {
        return this.sendX5c;
    }

    public static class Builder
    extends AbstractClientApplicationBase.Builder<Builder> {
        private IClientCredential clientCredential;
        private boolean sendX5c = true;
        private Function<AppTokenProviderParameters, CompletableFuture<TokenProviderResult>> appTokenProvider;

        private Builder(String clientId, IClientCredential clientCredential) {
            super(clientId);
            this.clientCredential = clientCredential;
        }

        public Builder sendX5c(boolean val) {
            this.sendX5c = val;
            return this.self();
        }

        public Builder appTokenProvider(Function<AppTokenProviderParameters, CompletableFuture<TokenProviderResult>> appTokenProvider) {
            if (appTokenProvider != null) {
                this.appTokenProvider = appTokenProvider;
                return this.self();
            }
            throw new NullPointerException("appTokenProvider is null");
        }

        @Override
        public ConfidentialClientApplication build() {
            return new ConfidentialClientApplication(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}

