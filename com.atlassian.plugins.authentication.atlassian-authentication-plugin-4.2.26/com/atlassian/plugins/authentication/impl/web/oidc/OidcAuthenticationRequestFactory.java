/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.oidc;

import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.impl.web.oidc.OidcAuthenticationRequest;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.Prompt;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class OidcAuthenticationRequestFactory {
    private static final Logger log = LoggerFactory.getLogger(OidcAuthenticationRequestFactory.class);
    private static final String OPENID_CONNECT_DEFAULT_SCOPE = "openid";

    public OidcAuthenticationRequest prepareOidcAuthenticationRequest(String callbackUri, String loginHint, boolean forceReAuthentication, OidcConfig oidcConfig) {
        AuthenticationRequest authRequest = this.prepareAuthenticationRequest(callbackUri, loginHint, forceReAuthentication, oidcConfig);
        log.debug("Prepared OpenID Authentication request: {}", (Object)authRequest.toQueryString());
        return new OidcAuthenticationRequest(authRequest.getState().getValue(), authRequest.getNonce().getValue(), UUID.randomUUID().toString(), authRequest.toURI().toString());
    }

    public AuthenticationRequest prepareAuthenticationRequest(String callbackUri, String loginHint, boolean forceReAuthentication, OidcConfig oidcConfig) {
        ArrayList additionalScopes = Lists.newArrayList(oidcConfig.getAdditionalScopes());
        JustInTimeConfig justInTimeConfig = oidcConfig.getJustInTimeConfig();
        if (justInTimeConfig.isEnabled().orElse(false).booleanValue()) {
            additionalScopes.addAll(justInTimeConfig.getAdditionalJitScopes());
        }
        List<String> combinedScopes = Stream.concat(Stream.of(OPENID_CONNECT_DEFAULT_SCOPE), additionalScopes.stream()).filter(scope -> !Strings.isNullOrEmpty((String)scope)).distinct().collect(Collectors.toList());
        log.trace("Effective OIDC scopes for auth request to IdP [{}] are: [{}]", (Object)oidcConfig.getId(), combinedScopes);
        AuthenticationRequest.Builder authRequestBuilder = new AuthenticationRequest.Builder(new ResponseType(ResponseType.Value.CODE), Scope.parse(combinedScopes), new ClientID(oidcConfig.getClientId()), URI.create(callbackUri));
        return authRequestBuilder.state(new State()).nonce(new Nonce()).endpointURI(URI.create(oidcConfig.getAuthorizationEndpoint())).prompt(forceReAuthentication ? new Prompt(Prompt.Type.LOGIN) : null).loginHint(loginHint).build();
    }
}

