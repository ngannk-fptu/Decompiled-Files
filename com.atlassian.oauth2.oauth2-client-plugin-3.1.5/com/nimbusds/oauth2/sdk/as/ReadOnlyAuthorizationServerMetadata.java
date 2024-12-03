/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.as;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.langtag.LangTag;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.as.ReadOnlyAuthorizationServerEndpointMetadata;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.ciba.BackChannelTokenDeliveryMode;
import com.nimbusds.oauth2.sdk.client.ClientType;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import java.net.URI;
import java.util.List;
import net.minidev.json.JSONObject;

public interface ReadOnlyAuthorizationServerMetadata
extends ReadOnlyAuthorizationServerEndpointMetadata {
    public Issuer getIssuer();

    public URI getJWKSetURI();

    public Scope getScopes();

    public List<ResponseType> getResponseTypes();

    public List<ResponseMode> getResponseModes();

    public List<GrantType> getGrantTypes();

    public List<CodeChallengeMethod> getCodeChallengeMethods();

    public List<ClientAuthenticationMethod> getTokenEndpointAuthMethods();

    public List<JWSAlgorithm> getTokenEndpointJWSAlgs();

    public List<ClientAuthenticationMethod> getIntrospectionEndpointAuthMethods();

    public List<JWSAlgorithm> getIntrospectionEndpointJWSAlgs();

    public List<ClientAuthenticationMethod> getRevocationEndpointAuthMethods();

    public List<JWSAlgorithm> getRevocationEndpointJWSAlgs();

    public List<JWSAlgorithm> getRequestObjectJWSAlgs();

    public List<JWEAlgorithm> getRequestObjectJWEAlgs();

    public List<EncryptionMethod> getRequestObjectJWEEncs();

    public boolean supportsRequestParam();

    public boolean supportsRequestURIParam();

    public boolean requiresRequestURIRegistration();

    public boolean supportsAuthorizationResponseIssuerParam();

    public List<LangTag> getUILocales();

    public URI getServiceDocsURI();

    public URI getPolicyURI();

    public URI getTermsOfServiceURI();

    public ReadOnlyAuthorizationServerEndpointMetadata getReadOnlyMtlsEndpointAliases();

    public boolean supportsTLSClientCertificateBoundAccessTokens();

    @Deprecated
    public boolean supportsMutualTLSSenderConstrainedAccessTokens();

    public List<JWSAlgorithm> getDPoPJWSAlgs();

    public List<JWSAlgorithm> getAuthorizationJWSAlgs();

    public List<JWEAlgorithm> getAuthorizationJWEAlgs();

    public List<EncryptionMethod> getAuthorizationJWEEncs();

    public boolean requiresPushedAuthorizationRequests();

    public List<ClientType> getIncrementalAuthorizationTypes();

    public List<BackChannelTokenDeliveryMode> getBackChannelTokenDeliveryModes();

    public List<JWSAlgorithm> getBackChannelAuthenticationRequestJWSAlgs();

    public boolean supportsBackChannelUserCodeParam();

    public Object getCustomParameter(String var1);

    public URI getCustomURIParameter(String var1);

    public JSONObject getCustomParameters();

    @Override
    public JSONObject toJSONObject();
}

