/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.util.Base64URL
 *  com.nimbusds.oauth2.sdk.AuthorizationGrant
 *  com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant
 *  com.nimbusds.oauth2.sdk.SAML2BearerGrant
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AbstractClientApplicationBase;
import com.microsoft.aad.msal4j.AbstractMsalAuthorizationGrant;
import com.microsoft.aad.msal4j.AuthenticationResult;
import com.microsoft.aad.msal4j.AuthenticationResultSupplier;
import com.microsoft.aad.msal4j.Authority;
import com.microsoft.aad.msal4j.AuthorityType;
import com.microsoft.aad.msal4j.IntegratedWindowsAuthorizationGrant;
import com.microsoft.aad.msal4j.InteractionRequiredCache;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.MsalInteractionRequiredException;
import com.microsoft.aad.msal4j.MsalRequest;
import com.microsoft.aad.msal4j.OAuthAuthorizationGrant;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.RefreshTokenRequest;
import com.microsoft.aad.msal4j.SAML11BearerGrant;
import com.microsoft.aad.msal4j.UserDiscoveryRequest;
import com.microsoft.aad.msal4j.UserDiscoveryResponse;
import com.microsoft.aad.msal4j.WSTrustRequest;
import com.microsoft.aad.msal4j.WSTrustResponse;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant;
import com.nimbusds.oauth2.sdk.SAML2BearerGrant;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class AcquireTokenByAuthorizationGrantSupplier
extends AuthenticationResultSupplier {
    private Authority requestAuthority;
    private MsalRequest msalRequest;

    AcquireTokenByAuthorizationGrantSupplier(AbstractClientApplicationBase clientApplication, MsalRequest msalRequest, Authority authority) {
        super(clientApplication, msalRequest);
        this.msalRequest = msalRequest;
        this.requestAuthority = authority;
    }

    @Override
    AuthenticationResult execute() throws Exception {
        MsalInteractionRequiredException cachedEx;
        AbstractMsalAuthorizationGrant authGrant = this.msalRequest.msalAuthorizationGrant();
        if (this.IsUiRequiredCacheSupported() && (cachedEx = InteractionRequiredCache.getCachedInteractionRequiredException(((RefreshTokenRequest)this.msalRequest).getFullThumbprint())) != null) {
            throw cachedEx;
        }
        if (authGrant instanceof OAuthAuthorizationGrant) {
            this.msalRequest.msalAuthorizationGrant = this.processPasswordGrant((OAuthAuthorizationGrant)authGrant);
        }
        if (authGrant instanceof IntegratedWindowsAuthorizationGrant) {
            IntegratedWindowsAuthorizationGrant integratedAuthGrant = (IntegratedWindowsAuthorizationGrant)authGrant;
            this.msalRequest.msalAuthorizationGrant = new OAuthAuthorizationGrant(this.getAuthorizationGrantIntegrated(integratedAuthGrant.getUserName()), integratedAuthGrant.getScopes(), integratedAuthGrant.getClaims());
        }
        if (this.requestAuthority == null) {
            this.requestAuthority = this.clientApplication.authenticationAuthority;
        }
        if (this.requestAuthority.authorityType == AuthorityType.AAD) {
            this.requestAuthority = this.getAuthorityWithPrefNetworkHost(this.requestAuthority.authority());
        }
        try {
            return this.clientApplication.acquireTokenCommon(this.msalRequest, this.requestAuthority);
        }
        catch (MsalInteractionRequiredException ex) {
            if (this.IsUiRequiredCacheSupported()) {
                InteractionRequiredCache.set(((RefreshTokenRequest)this.msalRequest).getFullThumbprint(), ex);
            }
            throw ex;
        }
    }

    private boolean IsUiRequiredCacheSupported() {
        return this.msalRequest instanceof RefreshTokenRequest && this.clientApplication instanceof PublicClientApplication;
    }

    private OAuthAuthorizationGrant processPasswordGrant(OAuthAuthorizationGrant authGrant) throws Exception {
        if (!(authGrant.getAuthorizationGrant() instanceof ResourceOwnerPasswordCredentialsGrant)) {
            return authGrant;
        }
        if (this.msalRequest.application().authenticationAuthority.authorityType != AuthorityType.AAD) {
            return authGrant;
        }
        ResourceOwnerPasswordCredentialsGrant grant = (ResourceOwnerPasswordCredentialsGrant)authGrant.getAuthorizationGrant();
        UserDiscoveryResponse userDiscoveryResponse = UserDiscoveryRequest.execute(this.clientApplication.authenticationAuthority.getUserRealmEndpoint(grant.getUsername()), this.msalRequest.headers().getReadonlyHeaderMap(), this.msalRequest.requestContext(), this.clientApplication.getServiceBundle());
        if (userDiscoveryResponse.isAccountFederated()) {
            WSTrustResponse response = WSTrustRequest.execute(userDiscoveryResponse.federationMetadataUrl(), grant.getUsername(), grant.getPassword().getValue(), userDiscoveryResponse.cloudAudienceUrn(), this.msalRequest.requestContext(), this.clientApplication.getServiceBundle(), this.clientApplication.logPii());
            AuthorizationGrant updatedGrant = this.getSAMLAuthorizationGrant(response);
            authGrant = new OAuthAuthorizationGrant(updatedGrant, authGrant.getParameters());
        }
        return authGrant;
    }

    private AuthorizationGrant getSAMLAuthorizationGrant(WSTrustResponse response) throws UnsupportedEncodingException {
        Object updatedGrant = response.isTokenSaml2() ? new SAML2BearerGrant(new Base64URL(Base64.getEncoder().encodeToString(response.getToken().getBytes(StandardCharsets.UTF_8)))) : new SAML11BearerGrant(new Base64URL(Base64.getEncoder().encodeToString(response.getToken().getBytes(StandardCharsets.UTF_8))));
        return updatedGrant;
    }

    private AuthorizationGrant getAuthorizationGrantIntegrated(String userName) throws Exception {
        String userRealmEndpoint = this.clientApplication.authenticationAuthority.getUserRealmEndpoint(URLEncoder.encode(userName, StandardCharsets.UTF_8.name()));
        UserDiscoveryResponse userRealmResponse = UserDiscoveryRequest.execute(userRealmEndpoint, this.msalRequest.headers().getReadonlyHeaderMap(), this.msalRequest.requestContext(), this.clientApplication.getServiceBundle());
        if (!userRealmResponse.isAccountFederated() || !"WSTrust".equalsIgnoreCase(userRealmResponse.federationProtocol())) {
            if (userRealmResponse.isAccountManaged()) {
                throw new MsalClientException("Password is required for managed user", "password_required_for_managed_user");
            }
            throw new MsalClientException("User Realm request failed", "user_realm_discovery_failed");
        }
        String mexURL = userRealmResponse.federationMetadataUrl();
        String cloudAudienceUrn = userRealmResponse.cloudAudienceUrn();
        WSTrustResponse wsTrustResponse = WSTrustRequest.execute(mexURL, cloudAudienceUrn, this.msalRequest.requestContext(), this.clientApplication.getServiceBundle(), this.clientApplication.logPii());
        AuthorizationGrant updatedGrant = this.getSAMLAuthorizationGrant(wsTrustResponse);
        return updatedGrant;
    }
}

