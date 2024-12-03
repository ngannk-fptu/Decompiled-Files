/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.ParseException
 *  com.nimbusds.oauth2.sdk.SerializeException
 *  com.nimbusds.oauth2.sdk.http.HTTPRequest
 *  com.nimbusds.oauth2.sdk.http.HTTPRequest$Method
 *  com.nimbusds.oauth2.sdk.http.HTTPResponse
 *  com.nimbusds.oauth2.sdk.util.URLUtils
 *  com.nimbusds.openid.connect.sdk.token.OIDCTokens
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AccountCacheEntity;
import com.microsoft.aad.msal4j.AuthenticationResult;
import com.microsoft.aad.msal4j.Authority;
import com.microsoft.aad.msal4j.AuthorityType;
import com.microsoft.aad.msal4j.B2CAuthority;
import com.microsoft.aad.msal4j.ClientAssertion;
import com.microsoft.aad.msal4j.ClientCredentialRequest;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.HTTPContentType;
import com.microsoft.aad.msal4j.IdToken;
import com.microsoft.aad.msal4j.JsonHelper;
import com.microsoft.aad.msal4j.MsalRequest;
import com.microsoft.aad.msal4j.MsalServiceExceptionFactory;
import com.microsoft.aad.msal4j.OAuthHttpRequest;
import com.microsoft.aad.msal4j.ServiceBundle;
import com.microsoft.aad.msal4j.StringHelper;
import com.microsoft.aad.msal4j.TokenResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TokenRequestExecutor {
    Logger log = LoggerFactory.getLogger(TokenRequestExecutor.class);
    final Authority requestAuthority;
    private final MsalRequest msalRequest;
    private final ServiceBundle serviceBundle;

    TokenRequestExecutor(Authority requestAuthority, MsalRequest msalRequest, ServiceBundle serviceBundle) {
        this.requestAuthority = requestAuthority;
        this.serviceBundle = serviceBundle;
        this.msalRequest = msalRequest;
    }

    AuthenticationResult executeTokenRequest() throws ParseException, IOException {
        this.log.debug("Sending token request to: {}", (Object)this.requestAuthority.canonicalAuthorityUrl());
        OAuthHttpRequest oAuthHttpRequest = this.createOauthHttpRequest();
        HTTPResponse oauthHttpResponse = oAuthHttpRequest.send();
        return this.createAuthenticationResultFromOauthHttpResponse(oauthHttpResponse);
    }

    OAuthHttpRequest createOauthHttpRequest() throws SerializeException, MalformedURLException, ParseException {
        if (this.requestAuthority.tokenEndpointUrl() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        OAuthHttpRequest oauthHttpRequest = new OAuthHttpRequest(HTTPRequest.Method.POST, this.requestAuthority.tokenEndpointUrl(), this.msalRequest.headers().getReadonlyHeaderMap(), this.msalRequest.requestContext(), this.serviceBundle);
        oauthHttpRequest.setContentType(HTTPContentType.ApplicationURLEncoded.contentType);
        HashMap<String, List<String>> params = new HashMap<String, List<String>>(this.msalRequest.msalAuthorizationGrant().toParameters());
        if (this.msalRequest.application().clientCapabilities() != null) {
            params.put("claims", Collections.singletonList(this.msalRequest.application().clientCapabilities()));
        }
        if (this.msalRequest.msalAuthorizationGrant.getClaims() != null) {
            Object claimsRequest = this.msalRequest.msalAuthorizationGrant.getClaims().formatAsJSONString();
            if (params.get("claims") != null) {
                claimsRequest = JsonHelper.mergeJSONString((String)((List)params.get("claims")).get(0), (String)claimsRequest);
            }
            params.put("claims", Collections.singletonList(claimsRequest));
        }
        if (this.msalRequest.requestContext().apiParameters().extraQueryParameters() != null) {
            for (String key : this.msalRequest.requestContext().apiParameters().extraQueryParameters().keySet()) {
                if (params.containsKey(key)) {
                    this.log.warn("A query parameter {} has been provided with values multiple times.", (Object)key);
                }
                params.put(key, Collections.singletonList(this.msalRequest.requestContext().apiParameters().extraQueryParameters().get(key)));
            }
        }
        oauthHttpRequest.setQuery(URLUtils.serializeParameters(params));
        if (this.msalRequest.application().clientAuthentication() != null) {
            Map queryParameters = oauthHttpRequest.getQueryParameters();
            String clientID = this.msalRequest.application().clientId();
            queryParameters.put("client_id", Arrays.asList(clientID));
            oauthHttpRequest.setQuery(URLUtils.serializeParameters((Map)queryParameters));
            if (this.msalRequest instanceof ClientCredentialRequest && ((ClientCredentialRequest)this.msalRequest).parameters.clientCredential() != null) {
                ((ConfidentialClientApplication)this.msalRequest.application()).createClientAuthFromClientAssertion((ClientAssertion)((ClientCredentialRequest)this.msalRequest).parameters.clientCredential()).applyTo((HTTPRequest)oauthHttpRequest);
            } else {
                this.msalRequest.application().clientAuthentication().applyTo((HTTPRequest)oauthHttpRequest);
            }
        }
        return oauthHttpRequest;
    }

    private AuthenticationResult createAuthenticationResultFromOauthHttpResponse(HTTPResponse oauthHttpResponse) throws ParseException {
        AccountCacheEntity accountCacheEntity;
        String refreshToken;
        OIDCTokens tokens;
        TokenResponse response;
        if (oauthHttpResponse.getStatusCode() == 200) {
            response = TokenResponse.parseHttpResponse(oauthHttpResponse);
            tokens = response.getOIDCTokens();
            refreshToken = null;
            if (tokens.getRefreshToken() != null) {
                refreshToken = tokens.getRefreshToken().getValue();
            }
            accountCacheEntity = null;
            if (tokens.getIDToken() != null) {
                String idTokenJson = tokens.getIDToken().getParsedParts()[1].decodeToString();
                IdToken idToken = JsonHelper.convertJsonToObject(idTokenJson, IdToken.class);
                AuthorityType type = this.msalRequest.application().authenticationAuthority.authorityType;
                if (!StringHelper.isBlank(response.getClientInfo())) {
                    if (type == AuthorityType.B2C) {
                        B2CAuthority authority = (B2CAuthority)this.msalRequest.application().authenticationAuthority;
                        accountCacheEntity = AccountCacheEntity.create(response.getClientInfo(), this.requestAuthority, idToken, authority.policy());
                    } else {
                        accountCacheEntity = AccountCacheEntity.create(response.getClientInfo(), this.requestAuthority, idToken);
                    }
                } else if (type == AuthorityType.ADFS) {
                    accountCacheEntity = AccountCacheEntity.createADFSAccount(this.requestAuthority, idToken);
                }
            }
        } else {
            if (oauthHttpResponse.getStatusCode() == 429 || oauthHttpResponse.getStatusCode() >= 500) {
                this.serviceBundle.getServerSideTelemetry().previousRequests.putAll(this.serviceBundle.getServerSideTelemetry().previousRequestInProgress);
            }
            throw MsalServiceExceptionFactory.fromHttpResponse(oauthHttpResponse);
        }
        long currTimestampSec = new Date().getTime() / 1000L;
        AuthenticationResult result = AuthenticationResult.builder().accessToken(tokens.getAccessToken().getValue()).refreshToken(refreshToken).familyId(response.getFoci()).idToken(tokens.getIDTokenString()).environment(this.requestAuthority.host()).expiresOn(currTimestampSec + response.getExpiresIn()).extExpiresOn(response.getExtExpiresIn() > 0L ? currTimestampSec + response.getExtExpiresIn() : 0L).refreshOn(response.getRefreshIn() > 0L ? currTimestampSec + response.getRefreshIn() : 0L).accountCacheEntity(accountCacheEntity).scopes(response.getScope()).build();
        return result;
    }

    Logger getLog() {
        return this.log;
    }

    Authority getRequestAuthority() {
        return this.requestAuthority;
    }

    MsalRequest getMsalRequest() {
        return this.msalRequest;
    }

    ServiceBundle getServiceBundle() {
        return this.serviceBundle;
    }
}

