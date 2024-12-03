/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.as;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.langtag.LangTag;
import com.nimbusds.langtag.LangTagException;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerEndpointMetadata;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.ciba.BackChannelTokenDeliveryMode;
import com.nimbusds.oauth2.sdk.client.ClientType;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONObject;

public class AuthorizationServerMetadata
extends AuthorizationServerEndpointMetadata {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private final Issuer issuer;
    private URI jwkSetURI;
    private Scope scope;
    private List<ResponseType> rts;
    private List<ResponseMode> rms;
    private List<GrantType> gts;
    private List<CodeChallengeMethod> codeChallengeMethods;
    private List<ClientAuthenticationMethod> tokenEndpointAuthMethods;
    private List<JWSAlgorithm> tokenEndpointJWSAlgs;
    private List<ClientAuthenticationMethod> introspectionEndpointAuthMethods;
    private List<JWSAlgorithm> introspectionEndpointJWSAlgs;
    private List<ClientAuthenticationMethod> revocationEndpointAuthMethods;
    private List<JWSAlgorithm> revocationEndpointJWSAlgs;
    private List<JWSAlgorithm> requestObjectJWSAlgs;
    private List<JWEAlgorithm> requestObjectJWEAlgs;
    private List<EncryptionMethod> requestObjectJWEEncs;
    private boolean requestParamSupported = false;
    private boolean requestURIParamSupported = false;
    private boolean requireRequestURIReg = false;
    private boolean authzResponseIssParameterSupported = false;
    private List<LangTag> uiLocales;
    private URI serviceDocsURI;
    private URI policyURI;
    private URI tosURI;
    private AuthorizationServerEndpointMetadata mtlsEndpointAliases;
    private boolean tlsClientCertificateBoundAccessTokens = false;
    private List<JWSAlgorithm> dPoPJWSAlgs;
    private List<JWSAlgorithm> authzJWSAlgs;
    private List<JWEAlgorithm> authzJWEAlgs;
    private List<EncryptionMethod> authzJWEEncs;
    private boolean requirePAR = false;
    private List<ClientType> incrementalAuthzTypes;
    private List<BackChannelTokenDeliveryMode> backChannelTokenDeliveryModes;
    private List<JWSAlgorithm> backChannelAuthRequestJWSAlgs;
    private boolean backChannelUserCodeSupported = false;
    private final JSONObject customParameters = new JSONObject();

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public AuthorizationServerMetadata(Issuer issuer) {
        URI uri;
        try {
            uri = new URI(issuer.getValue());
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("The issuer identifier must be a URI: " + e.getMessage(), e);
        }
        if (uri.getRawQuery() != null) {
            throw new IllegalArgumentException("The issuer URI must be without a query component");
        }
        if (uri.getRawFragment() != null) {
            throw new IllegalArgumentException("The issuer URI must be without a fragment component");
        }
        this.issuer = issuer;
    }

    public Issuer getIssuer() {
        return this.issuer;
    }

    public URI getJWKSetURI() {
        return this.jwkSetURI;
    }

    public void setJWKSetURI(URI jwkSetURI) {
        this.jwkSetURI = jwkSetURI;
    }

    public Scope getScopes() {
        return this.scope;
    }

    public void setScopes(Scope scope) {
        this.scope = scope;
    }

    public List<ResponseType> getResponseTypes() {
        return this.rts;
    }

    public void setResponseTypes(List<ResponseType> rts) {
        this.rts = rts;
    }

    public List<ResponseMode> getResponseModes() {
        return this.rms;
    }

    public void setResponseModes(List<ResponseMode> rms) {
        this.rms = rms;
    }

    public List<GrantType> getGrantTypes() {
        return this.gts;
    }

    public void setGrantTypes(List<GrantType> gts) {
        this.gts = gts;
    }

    public List<CodeChallengeMethod> getCodeChallengeMethods() {
        return this.codeChallengeMethods;
    }

    public void setCodeChallengeMethods(List<CodeChallengeMethod> codeChallengeMethods) {
        this.codeChallengeMethods = codeChallengeMethods;
    }

    public List<ClientAuthenticationMethod> getTokenEndpointAuthMethods() {
        return this.tokenEndpointAuthMethods;
    }

    public void setTokenEndpointAuthMethods(List<ClientAuthenticationMethod> authMethods) {
        this.tokenEndpointAuthMethods = authMethods;
    }

    public List<JWSAlgorithm> getTokenEndpointJWSAlgs() {
        return this.tokenEndpointJWSAlgs;
    }

    public void setTokenEndpointJWSAlgs(List<JWSAlgorithm> jwsAlgs) {
        if (jwsAlgs != null && jwsAlgs.contains(Algorithm.NONE)) {
            throw new IllegalArgumentException("The \"none\" algorithm is not accepted");
        }
        this.tokenEndpointJWSAlgs = jwsAlgs;
    }

    public List<ClientAuthenticationMethod> getIntrospectionEndpointAuthMethods() {
        return this.introspectionEndpointAuthMethods;
    }

    public void setIntrospectionEndpointAuthMethods(List<ClientAuthenticationMethod> authMethods) {
        this.introspectionEndpointAuthMethods = authMethods;
    }

    public List<JWSAlgorithm> getIntrospectionEndpointJWSAlgs() {
        return this.introspectionEndpointJWSAlgs;
    }

    public void setIntrospectionEndpointJWSAlgs(List<JWSAlgorithm> jwsAlgs) {
        if (jwsAlgs != null && jwsAlgs.contains(Algorithm.NONE)) {
            throw new IllegalArgumentException("The \"none\" algorithm is not accepted");
        }
        this.introspectionEndpointJWSAlgs = jwsAlgs;
    }

    public List<ClientAuthenticationMethod> getRevocationEndpointAuthMethods() {
        return this.revocationEndpointAuthMethods;
    }

    public void setRevocationEndpointAuthMethods(List<ClientAuthenticationMethod> authMethods) {
        this.revocationEndpointAuthMethods = authMethods;
    }

    public List<JWSAlgorithm> getRevocationEndpointJWSAlgs() {
        return this.revocationEndpointJWSAlgs;
    }

    public void setRevocationEndpointJWSAlgs(List<JWSAlgorithm> jwsAlgs) {
        if (jwsAlgs != null && jwsAlgs.contains(Algorithm.NONE)) {
            throw new IllegalArgumentException("The \"none\" algorithm is not accepted");
        }
        this.revocationEndpointJWSAlgs = jwsAlgs;
    }

    public List<JWSAlgorithm> getRequestObjectJWSAlgs() {
        return this.requestObjectJWSAlgs;
    }

    public void setRequestObjectJWSAlgs(List<JWSAlgorithm> requestObjectJWSAlgs) {
        this.requestObjectJWSAlgs = requestObjectJWSAlgs;
    }

    public List<JWEAlgorithm> getRequestObjectJWEAlgs() {
        return this.requestObjectJWEAlgs;
    }

    public void setRequestObjectJWEAlgs(List<JWEAlgorithm> requestObjectJWEAlgs) {
        this.requestObjectJWEAlgs = requestObjectJWEAlgs;
    }

    public List<EncryptionMethod> getRequestObjectJWEEncs() {
        return this.requestObjectJWEEncs;
    }

    public void setRequestObjectJWEEncs(List<EncryptionMethod> requestObjectJWEEncs) {
        this.requestObjectJWEEncs = requestObjectJWEEncs;
    }

    public boolean supportsRequestParam() {
        return this.requestParamSupported;
    }

    public void setSupportsRequestParam(boolean requestParamSupported) {
        this.requestParamSupported = requestParamSupported;
    }

    public boolean supportsRequestURIParam() {
        return this.requestURIParamSupported;
    }

    public void setSupportsRequestURIParam(boolean requestURIParamSupported) {
        this.requestURIParamSupported = requestURIParamSupported;
    }

    public boolean requiresRequestURIRegistration() {
        return this.requireRequestURIReg;
    }

    public void setRequiresRequestURIRegistration(boolean requireRequestURIReg) {
        this.requireRequestURIReg = requireRequestURIReg;
    }

    public boolean supportsAuthorizationResponseIssuerParam() {
        return this.authzResponseIssParameterSupported;
    }

    public void setSupportsAuthorizationResponseIssuerParam(boolean authzResponseIssParameterSupported) {
        this.authzResponseIssParameterSupported = authzResponseIssParameterSupported;
    }

    public List<LangTag> getUILocales() {
        return this.uiLocales;
    }

    public void setUILocales(List<LangTag> uiLocales) {
        this.uiLocales = uiLocales;
    }

    public URI getServiceDocsURI() {
        return this.serviceDocsURI;
    }

    public void setServiceDocsURI(URI serviceDocsURI) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(serviceDocsURI);
        this.serviceDocsURI = serviceDocsURI;
    }

    public URI getPolicyURI() {
        return this.policyURI;
    }

    public void setPolicyURI(URI policyURI) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(policyURI);
        this.policyURI = policyURI;
    }

    public URI getTermsOfServiceURI() {
        return this.tosURI;
    }

    public void setTermsOfServiceURI(URI tosURI) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(tosURI);
        this.tosURI = tosURI;
    }

    public AuthorizationServerEndpointMetadata getMtlsEndpointAliases() {
        return this.mtlsEndpointAliases;
    }

    public void setMtlsEndpointAliases(AuthorizationServerEndpointMetadata mtlsEndpointAliases) {
        this.mtlsEndpointAliases = mtlsEndpointAliases;
    }

    public boolean supportsTLSClientCertificateBoundAccessTokens() {
        return this.tlsClientCertificateBoundAccessTokens;
    }

    public void setSupportsTLSClientCertificateBoundAccessTokens(boolean tlsClientCertBoundTokens) {
        this.tlsClientCertificateBoundAccessTokens = tlsClientCertBoundTokens;
    }

    @Deprecated
    public boolean supportsMutualTLSSenderConstrainedAccessTokens() {
        return this.supportsTLSClientCertificateBoundAccessTokens();
    }

    @Deprecated
    public void setSupportsMutualTLSSenderConstrainedAccessTokens(boolean mutualTLSSenderConstrainedAccessTokens) {
        this.setSupportsTLSClientCertificateBoundAccessTokens(mutualTLSSenderConstrainedAccessTokens);
    }

    public List<JWSAlgorithm> getDPoPJWSAlgs() {
        return this.dPoPJWSAlgs;
    }

    public void setDPoPJWSAlgs(List<JWSAlgorithm> dPoPJWSAlgs) {
        this.dPoPJWSAlgs = dPoPJWSAlgs;
    }

    public List<JWSAlgorithm> getAuthorizationJWSAlgs() {
        return this.authzJWSAlgs;
    }

    public void setAuthorizationJWSAlgs(List<JWSAlgorithm> authzJWSAlgs) {
        this.authzJWSAlgs = authzJWSAlgs;
    }

    public List<JWEAlgorithm> getAuthorizationJWEAlgs() {
        return this.authzJWEAlgs;
    }

    public void setAuthorizationJWEAlgs(List<JWEAlgorithm> authzJWEAlgs) {
        this.authzJWEAlgs = authzJWEAlgs;
    }

    public List<EncryptionMethod> getAuthorizationJWEEncs() {
        return this.authzJWEEncs;
    }

    public void setAuthorizationJWEEncs(List<EncryptionMethod> authzJWEEncs) {
        this.authzJWEEncs = authzJWEEncs;
    }

    public boolean requiresPushedAuthorizationRequests() {
        return this.requirePAR;
    }

    public void requiresPushedAuthorizationRequests(boolean requirePAR) {
        this.requirePAR = requirePAR;
    }

    public List<ClientType> getIncrementalAuthorizationTypes() {
        return this.incrementalAuthzTypes;
    }

    public void setIncrementalAuthorizationTypes(List<ClientType> incrementalAuthzTypes) {
        this.incrementalAuthzTypes = incrementalAuthzTypes;
    }

    public List<BackChannelTokenDeliveryMode> getBackChannelTokenDeliveryModes() {
        return this.backChannelTokenDeliveryModes;
    }

    public void setBackChannelTokenDeliveryModes(List<BackChannelTokenDeliveryMode> backChannelTokenDeliveryModes) {
        this.backChannelTokenDeliveryModes = backChannelTokenDeliveryModes;
    }

    public List<JWSAlgorithm> getBackChannelAuthenticationRequestJWSAlgs() {
        return this.backChannelAuthRequestJWSAlgs;
    }

    public void setBackChannelAuthenticationRequestJWSAlgs(List<JWSAlgorithm> backChannelAuthRequestJWSAlgs) {
        this.backChannelAuthRequestJWSAlgs = backChannelAuthRequestJWSAlgs;
    }

    public boolean supportsBackChannelUserCodeParam() {
        return this.backChannelUserCodeSupported;
    }

    public void setSupportsBackChannelUserCodeParam(boolean backChannelUserCodeSupported) {
        this.backChannelUserCodeSupported = backChannelUserCodeSupported;
    }

    public Object getCustomParameter(String name) {
        return this.customParameters.get(name);
    }

    public URI getCustomURIParameter(String name) {
        try {
            return JSONObjectUtils.getURI(this.customParameters, name, null);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setCustomParameter(String name, Object value) {
        if (REGISTERED_PARAMETER_NAMES.contains(name)) {
            throw new IllegalArgumentException("The " + name + " parameter is registered");
        }
        this.customParameters.put(name, value);
    }

    public JSONObject getCustomParameters() {
        return this.customParameters;
    }

    public void applyDefaults() {
        if (this.rms == null) {
            this.rms = new ArrayList<ResponseMode>(2);
            this.rms.add(ResponseMode.QUERY);
            this.rms.add(ResponseMode.FRAGMENT);
        }
        if (this.gts == null) {
            this.gts = new ArrayList<GrantType>(2);
            this.gts.add(GrantType.AUTHORIZATION_CODE);
            this.gts.add(GrantType.IMPLICIT);
        }
        if (this.tokenEndpointAuthMethods == null) {
            this.tokenEndpointAuthMethods = new ArrayList<ClientAuthenticationMethod>();
            this.tokenEndpointAuthMethods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        }
    }

    @Override
    public JSONObject toJSONObject() {
        ArrayList<String> stringList;
        JSONObject o = super.toJSONObject();
        o.put("issuer", this.issuer.getValue());
        if (this.jwkSetURI != null) {
            o.put("jwks_uri", this.jwkSetURI.toString());
        }
        if (this.scope != null) {
            o.put("scopes_supported", this.scope.toStringList());
        }
        if (this.rts != null) {
            stringList = new ArrayList<String>(this.rts.size());
            for (ResponseType responseType : this.rts) {
                stringList.add(responseType.toString());
            }
            o.put("response_types_supported", stringList);
        }
        if (this.rms != null) {
            stringList = new ArrayList(this.rms.size());
            for (ResponseMode responseMode : this.rms) {
                stringList.add(responseMode.getValue());
            }
            o.put("response_modes_supported", stringList);
        }
        if (this.gts != null) {
            stringList = new ArrayList(this.gts.size());
            for (GrantType grantType : this.gts) {
                stringList.add(grantType.toString());
            }
            o.put("grant_types_supported", stringList);
        }
        if (this.codeChallengeMethods != null) {
            stringList = new ArrayList(this.codeChallengeMethods.size());
            for (CodeChallengeMethod codeChallengeMethod : this.codeChallengeMethods) {
                stringList.add(codeChallengeMethod.getValue());
            }
            o.put("code_challenge_methods_supported", stringList);
        }
        if (this.tokenEndpointAuthMethods != null) {
            stringList = new ArrayList(this.tokenEndpointAuthMethods.size());
            for (ClientAuthenticationMethod clientAuthenticationMethod : this.tokenEndpointAuthMethods) {
                stringList.add(clientAuthenticationMethod.getValue());
            }
            o.put("token_endpoint_auth_methods_supported", stringList);
        }
        if (this.tokenEndpointJWSAlgs != null) {
            stringList = new ArrayList(this.tokenEndpointJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.tokenEndpointJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put("token_endpoint_auth_signing_alg_values_supported", stringList);
        }
        if (this.introspectionEndpointAuthMethods != null) {
            stringList = new ArrayList(this.introspectionEndpointAuthMethods.size());
            for (ClientAuthenticationMethod clientAuthenticationMethod : this.introspectionEndpointAuthMethods) {
                stringList.add(clientAuthenticationMethod.getValue());
            }
            o.put("introspection_endpoint_auth_methods_supported", stringList);
        }
        if (this.introspectionEndpointJWSAlgs != null) {
            stringList = new ArrayList(this.introspectionEndpointJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.introspectionEndpointJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put("introspection_endpoint_auth_signing_alg_values_supported", stringList);
        }
        if (this.revocationEndpointAuthMethods != null) {
            stringList = new ArrayList(this.revocationEndpointAuthMethods.size());
            for (ClientAuthenticationMethod clientAuthenticationMethod : this.revocationEndpointAuthMethods) {
                stringList.add(clientAuthenticationMethod.getValue());
            }
            o.put("revocation_endpoint_auth_methods_supported", stringList);
        }
        if (this.revocationEndpointJWSAlgs != null) {
            stringList = new ArrayList(this.revocationEndpointJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.revocationEndpointJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put("revocation_endpoint_auth_signing_alg_values_supported", stringList);
        }
        if (this.requestObjectJWSAlgs != null) {
            stringList = new ArrayList(this.requestObjectJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.requestObjectJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put("request_object_signing_alg_values_supported", stringList);
        }
        if (this.requestObjectJWEAlgs != null) {
            stringList = new ArrayList(this.requestObjectJWEAlgs.size());
            for (JWEAlgorithm jWEAlgorithm : this.requestObjectJWEAlgs) {
                stringList.add(jWEAlgorithm.getName());
            }
            o.put("request_object_encryption_alg_values_supported", stringList);
        }
        if (this.requestObjectJWEEncs != null) {
            stringList = new ArrayList(this.requestObjectJWEEncs.size());
            for (EncryptionMethod encryptionMethod : this.requestObjectJWEEncs) {
                stringList.add(encryptionMethod.getName());
            }
            o.put("request_object_encryption_enc_values_supported", stringList);
        }
        if (this.uiLocales != null) {
            stringList = new ArrayList(this.uiLocales.size());
            for (LangTag langTag : this.uiLocales) {
                stringList.add(langTag.toString());
            }
            o.put("ui_locales_supported", stringList);
        }
        if (this.serviceDocsURI != null) {
            o.put("service_documentation", this.serviceDocsURI.toString());
        }
        if (this.policyURI != null) {
            o.put("op_policy_uri", this.policyURI.toString());
        }
        if (this.tosURI != null) {
            o.put("op_tos_uri", this.tosURI.toString());
        }
        if (this.requestParamSupported) {
            o.put("request_parameter_supported", true);
        }
        if (this.requestURIParamSupported) {
            o.put("request_uri_parameter_supported", true);
        }
        if (this.requireRequestURIReg) {
            o.put("require_request_uri_registration", true);
        }
        if (this.authzResponseIssParameterSupported) {
            o.put("authorization_response_iss_parameter_supported", true);
        }
        if (this.mtlsEndpointAliases != null) {
            o.put("mtls_endpoint_aliases", this.mtlsEndpointAliases.toJSONObject());
        }
        if (this.tlsClientCertificateBoundAccessTokens) {
            o.put("tls_client_certificate_bound_access_tokens", true);
        }
        if (this.dPoPJWSAlgs != null) {
            stringList = new ArrayList(this.dPoPJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.dPoPJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put("dpop_signing_alg_values_supported", stringList);
        }
        if (this.authzJWSAlgs != null) {
            stringList = new ArrayList(this.authzJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.authzJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put("authorization_signing_alg_values_supported", stringList);
        }
        if (this.authzJWEAlgs != null) {
            stringList = new ArrayList(this.authzJWEAlgs.size());
            for (JWEAlgorithm jWEAlgorithm : this.authzJWEAlgs) {
                stringList.add(jWEAlgorithm.getName());
            }
            o.put("authorization_encryption_alg_values_supported", stringList);
        }
        if (this.authzJWEEncs != null) {
            stringList = new ArrayList(this.authzJWEEncs.size());
            for (EncryptionMethod encryptionMethod : this.authzJWEEncs) {
                stringList.add(encryptionMethod.getName());
            }
            o.put("authorization_encryption_enc_values_supported", stringList);
        }
        if (this.requirePAR) {
            o.put("require_pushed_authorization_requests", true);
        }
        if (CollectionUtils.isNotEmpty(this.incrementalAuthzTypes)) {
            stringList = new ArrayList(this.incrementalAuthzTypes.size());
            for (ClientType clientType : this.incrementalAuthzTypes) {
                if (clientType == null) continue;
                stringList.add(clientType.name().toLowerCase());
            }
            o.put("incremental_authz_types_supported", stringList);
        }
        if (this.backChannelTokenDeliveryModes != null) {
            stringList = new ArrayList(this.backChannelTokenDeliveryModes.size());
            for (BackChannelTokenDeliveryMode backChannelTokenDeliveryMode : this.backChannelTokenDeliveryModes) {
                if (backChannelTokenDeliveryMode == null) continue;
                stringList.add(backChannelTokenDeliveryMode.getValue());
            }
            o.put("backchannel_token_delivery_modes_supported", stringList);
        }
        if (this.backChannelAuthRequestJWSAlgs != null) {
            stringList = new ArrayList(this.backChannelAuthRequestJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.backChannelAuthRequestJWSAlgs) {
                if (jWSAlgorithm == null) continue;
                stringList.add(jWSAlgorithm.getName());
            }
            o.put("backchannel_authentication_request_signing_alg_values_supported", stringList);
        }
        if (this.backChannelUserCodeSupported) {
            o.put("backchannel_user_code_parameter_supported", true);
        }
        o.putAll(this.customParameters);
        return o;
    }

    public static AuthorizationServerMetadata parse(JSONObject jsonObject) throws ParseException {
        AuthorizationServerMetadata as;
        Issuer issuer = new Issuer(JSONObjectUtils.getURI(jsonObject, "issuer").toString());
        AuthorizationServerEndpointMetadata asEndpoints = AuthorizationServerEndpointMetadata.parse(jsonObject);
        try {
            as = new AuthorizationServerMetadata(issuer);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
        as.setAuthorizationEndpointURI(asEndpoints.getAuthorizationEndpointURI());
        as.setTokenEndpointURI(asEndpoints.getTokenEndpointURI());
        as.setRegistrationEndpointURI(asEndpoints.getRegistrationEndpointURI());
        as.setIntrospectionEndpointURI(asEndpoints.getIntrospectionEndpointURI());
        as.setRevocationEndpointURI(asEndpoints.getRevocationEndpointURI());
        as.setRequestObjectEndpoint(asEndpoints.getRequestObjectEndpoint());
        as.setPushedAuthorizationRequestEndpointURI(asEndpoints.getPushedAuthorizationRequestEndpointURI());
        as.setDeviceAuthorizationEndpointURI(asEndpoints.getDeviceAuthorizationEndpointURI());
        as.setBackChannelAuthenticationEndpoint(asEndpoints.getBackChannelAuthenticationEndpoint());
        as.jwkSetURI = JSONObjectUtils.getURI(jsonObject, "jwks_uri", null);
        if (jsonObject.get("scopes_supported") != null) {
            as.scope = new Scope();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "scopes_supported")) {
                if (v == null) continue;
                as.scope.add(new Scope.Value(v));
            }
        }
        if (jsonObject.get("response_types_supported") != null) {
            as.rts = new ArrayList<ResponseType>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "response_types_supported")) {
                if (v == null) continue;
                as.rts.add(ResponseType.parse(v));
            }
        }
        if (jsonObject.get("response_modes_supported") != null) {
            as.rms = new ArrayList<ResponseMode>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "response_modes_supported")) {
                if (v == null) continue;
                as.rms.add(new ResponseMode(v));
            }
        }
        if (jsonObject.get("grant_types_supported") != null) {
            as.gts = new ArrayList<GrantType>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "grant_types_supported")) {
                if (v == null) continue;
                as.gts.add(GrantType.parse(v));
            }
        }
        if (jsonObject.get("code_challenge_methods_supported") != null) {
            as.codeChallengeMethods = new ArrayList<CodeChallengeMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "code_challenge_methods_supported")) {
                if (v == null) continue;
                as.codeChallengeMethods.add(CodeChallengeMethod.parse(v));
            }
        }
        if (jsonObject.get("token_endpoint_auth_methods_supported") != null) {
            as.tokenEndpointAuthMethods = new ArrayList<ClientAuthenticationMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "token_endpoint_auth_methods_supported")) {
                if (v == null) continue;
                as.tokenEndpointAuthMethods.add(ClientAuthenticationMethod.parse(v));
            }
        }
        if (jsonObject.get("token_endpoint_auth_signing_alg_values_supported") != null) {
            as.tokenEndpointJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "token_endpoint_auth_signing_alg_values_supported")) {
                if (v != null && v.equals(Algorithm.NONE.getName())) {
                    throw new ParseException("The none algorithm is not accepted");
                }
                if (v == null) continue;
                as.tokenEndpointJWSAlgs.add(JWSAlgorithm.parse(v));
            }
        }
        if (jsonObject.get("introspection_endpoint_auth_methods_supported") != null) {
            as.introspectionEndpointAuthMethods = new ArrayList<ClientAuthenticationMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "introspection_endpoint_auth_methods_supported")) {
                if (v == null) continue;
                as.introspectionEndpointAuthMethods.add(ClientAuthenticationMethod.parse(v));
            }
        }
        if (jsonObject.get("introspection_endpoint_auth_signing_alg_values_supported") != null) {
            as.introspectionEndpointJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "introspection_endpoint_auth_signing_alg_values_supported")) {
                if (v != null && v.equals(Algorithm.NONE.getName())) {
                    throw new ParseException("The none algorithm is not accepted");
                }
                if (v == null) continue;
                as.introspectionEndpointJWSAlgs.add(JWSAlgorithm.parse(v));
            }
        }
        if (jsonObject.get("revocation_endpoint_auth_methods_supported") != null) {
            as.revocationEndpointAuthMethods = new ArrayList<ClientAuthenticationMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "revocation_endpoint_auth_methods_supported")) {
                if (v == null) continue;
                as.revocationEndpointAuthMethods.add(ClientAuthenticationMethod.parse(v));
            }
        }
        if (jsonObject.get("revocation_endpoint_auth_signing_alg_values_supported") != null) {
            as.revocationEndpointJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "revocation_endpoint_auth_signing_alg_values_supported")) {
                if (v != null && v.equals(Algorithm.NONE.getName())) {
                    throw new ParseException("The none algorithm is not accepted");
                }
                if (v == null) continue;
                as.revocationEndpointJWSAlgs.add(JWSAlgorithm.parse(v));
            }
        }
        if (jsonObject.get("request_object_signing_alg_values_supported") != null) {
            as.requestObjectJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "request_object_signing_alg_values_supported")) {
                if (v == null) continue;
                as.requestObjectJWSAlgs.add(JWSAlgorithm.parse(v));
            }
        }
        if (jsonObject.get("request_object_encryption_alg_values_supported") != null) {
            as.requestObjectJWEAlgs = new ArrayList<JWEAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "request_object_encryption_alg_values_supported")) {
                if (v == null) continue;
                as.requestObjectJWEAlgs.add(JWEAlgorithm.parse(v));
            }
        }
        if (jsonObject.get("request_object_encryption_enc_values_supported") != null) {
            as.requestObjectJWEEncs = new ArrayList<EncryptionMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "request_object_encryption_enc_values_supported")) {
                if (v == null) continue;
                as.requestObjectJWEEncs.add(EncryptionMethod.parse(v));
            }
        }
        if (jsonObject.get("ui_locales_supported") != null) {
            as.uiLocales = new ArrayList<LangTag>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "ui_locales_supported")) {
                if (v == null) continue;
                try {
                    as.uiLocales.add(LangTag.parse(v));
                }
                catch (LangTagException e) {
                    throw new ParseException("Invalid ui_locales_supported field: " + e.getMessage(), e);
                }
            }
        }
        if (jsonObject.get("service_documentation") != null) {
            try {
                as.setServiceDocsURI(JSONObjectUtils.getURI(jsonObject, "service_documentation"));
            }
            catch (IllegalArgumentException e) {
                throw new ParseException("Illegal service_documentation parameter: " + e.getMessage());
            }
        }
        if (jsonObject.get("op_policy_uri") != null) {
            try {
                as.setPolicyURI(JSONObjectUtils.getURI(jsonObject, "op_policy_uri"));
            }
            catch (IllegalArgumentException e) {
                throw new ParseException("Illegal op_policy_uri parameter: " + e.getMessage());
            }
        }
        if (jsonObject.get("op_tos_uri") != null) {
            try {
                as.setTermsOfServiceURI(JSONObjectUtils.getURI(jsonObject, "op_tos_uri"));
            }
            catch (IllegalArgumentException e) {
                throw new ParseException("Illegal op_tos_uri parameter: " + e.getMessage());
            }
        }
        if (jsonObject.get("request_parameter_supported") != null) {
            as.requestParamSupported = JSONObjectUtils.getBoolean(jsonObject, "request_parameter_supported");
        }
        if (jsonObject.get("request_uri_parameter_supported") != null) {
            as.requestURIParamSupported = JSONObjectUtils.getBoolean(jsonObject, "request_uri_parameter_supported");
        }
        if (jsonObject.get("require_request_uri_registration") != null) {
            as.requireRequestURIReg = JSONObjectUtils.getBoolean(jsonObject, "require_request_uri_registration");
        }
        if (jsonObject.get("authorization_response_iss_parameter_supported") != null) {
            as.authzResponseIssParameterSupported = JSONObjectUtils.getBoolean(jsonObject, "authorization_response_iss_parameter_supported");
        }
        if (jsonObject.get("mtls_endpoint_aliases") != null) {
            as.mtlsEndpointAliases = AuthorizationServerEndpointMetadata.parse(JSONObjectUtils.getJSONObject(jsonObject, "mtls_endpoint_aliases"));
        }
        if (jsonObject.get("tls_client_certificate_bound_access_tokens") != null) {
            as.tlsClientCertificateBoundAccessTokens = JSONObjectUtils.getBoolean(jsonObject, "tls_client_certificate_bound_access_tokens");
        }
        if (jsonObject.get("dpop_signing_alg_values_supported") != null) {
            as.dPoPJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "dpop_signing_alg_values_supported")) {
                if (v == null) continue;
                as.dPoPJWSAlgs.add(JWSAlgorithm.parse(v));
            }
        }
        if (jsonObject.get("authorization_signing_alg_values_supported") != null) {
            as.authzJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "authorization_signing_alg_values_supported")) {
                if (v == null) continue;
                as.authzJWSAlgs.add(JWSAlgorithm.parse(v));
            }
        }
        if (jsonObject.get("authorization_encryption_alg_values_supported") != null) {
            as.authzJWEAlgs = new ArrayList<JWEAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "authorization_encryption_alg_values_supported")) {
                if (v == null) continue;
                as.authzJWEAlgs.add(JWEAlgorithm.parse(v));
            }
        }
        if (jsonObject.get("authorization_encryption_enc_values_supported") != null) {
            as.authzJWEEncs = new ArrayList<EncryptionMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "authorization_encryption_enc_values_supported")) {
                if (v == null) continue;
                as.authzJWEEncs.add(EncryptionMethod.parse(v));
            }
        }
        if (jsonObject.get("require_pushed_authorization_requests") != null) {
            as.requiresPushedAuthorizationRequests(JSONObjectUtils.getBoolean(jsonObject, "require_pushed_authorization_requests"));
        }
        if (jsonObject.get("incremental_authz_types_supported") != null) {
            as.incrementalAuthzTypes = new ArrayList<ClientType>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "incremental_authz_types_supported")) {
                ClientType clientType;
                if (v == null) continue;
                try {
                    clientType = ClientType.valueOf(v.toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    throw new ParseException("Illegal client type in incremental_authz_types_supported field: " + v);
                }
                as.incrementalAuthzTypes.add(clientType);
            }
        }
        if (jsonObject.get("backchannel_token_delivery_modes_supported") != null) {
            as.backChannelTokenDeliveryModes = new ArrayList<BackChannelTokenDeliveryMode>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "backchannel_token_delivery_modes_supported")) {
                if (v == null) continue;
                as.backChannelTokenDeliveryModes.add(BackChannelTokenDeliveryMode.parse(v));
            }
        }
        if (jsonObject.get("backchannel_authentication_request_signing_alg_values_supported") != null) {
            as.backChannelAuthRequestJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "backchannel_authentication_request_signing_alg_values_supported")) {
                if (v == null) continue;
                as.backChannelAuthRequestJWSAlgs.add(JWSAlgorithm.parse(v));
            }
        }
        if (jsonObject.get("backchannel_user_code_parameter_supported") != null) {
            as.backChannelUserCodeSupported = JSONObjectUtils.getBoolean(jsonObject, "backchannel_user_code_parameter_supported");
        }
        JSONObject customParams = new JSONObject(jsonObject);
        customParams.keySet().removeAll(REGISTERED_PARAMETER_NAMES);
        for (Map.Entry customEntry : customParams.entrySet()) {
            as.setCustomParameter((String)customEntry.getKey(), customEntry.getValue());
        }
        return as;
    }

    public static AuthorizationServerMetadata parse(String s) throws ParseException {
        return AuthorizationServerMetadata.parse(JSONObjectUtils.parse(s));
    }

    public static URL resolveURL(Issuer issuer) throws GeneralException {
        try {
            URL issuerURL = new URL(issuer.getValue());
            if (issuerURL.getQuery() != null && !issuerURL.getQuery().trim().isEmpty()) {
                throw new GeneralException("The issuer identifier must not contain a query component");
            }
            if (issuerURL.getPath() != null && issuerURL.getPath().endsWith("/")) {
                return new URL(issuerURL + ".well-known/oauth-authorization-server");
            }
            return new URL(issuerURL + "/.well-known/oauth-authorization-server");
        }
        catch (MalformedURLException e) {
            throw new GeneralException("The issuer identifier doesn't represent a valid URL: " + e.getMessage(), e);
        }
    }

    public static AuthorizationServerMetadata resolve(Issuer issuer) throws GeneralException, IOException {
        return AuthorizationServerMetadata.resolve(issuer, 0, 0);
    }

    public static AuthorizationServerMetadata resolve(Issuer issuer, int connectTimeout, int readTimeout) throws GeneralException, IOException {
        URL configURL = AuthorizationServerMetadata.resolveURL(issuer);
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.GET, configURL);
        httpRequest.setConnectTimeout(connectTimeout);
        httpRequest.setReadTimeout(readTimeout);
        HTTPResponse httpResponse = httpRequest.send();
        if (httpResponse.getStatusCode() != 200) {
            throw new IOException("Couldn't download OAuth 2.0 Authorization Server metadata from " + configURL + ": Status code " + httpResponse.getStatusCode());
        }
        JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        AuthorizationServerMetadata as = AuthorizationServerMetadata.parse(jsonObject);
        if (!issuer.equals(as.issuer)) {
            throw new GeneralException("The returned issuer doesn't match the expected: " + as.getIssuer());
        }
        return as;
    }

    static {
        HashSet<String> p = new HashSet<String>(AuthorizationServerEndpointMetadata.getRegisteredParameterNames());
        p.add("issuer");
        p.add("jwks_uri");
        p.add("scopes_supported");
        p.add("response_types_supported");
        p.add("response_modes_supported");
        p.add("grant_types_supported");
        p.add("code_challenge_methods_supported");
        p.add("token_endpoint_auth_methods_supported");
        p.add("token_endpoint_auth_signing_alg_values_supported");
        p.add("request_parameter_supported");
        p.add("request_uri_parameter_supported");
        p.add("require_request_uri_registration");
        p.add("request_object_signing_alg_values_supported");
        p.add("request_object_encryption_alg_values_supported");
        p.add("request_object_encryption_enc_values_supported");
        p.add("ui_locales_supported");
        p.add("service_documentation");
        p.add("op_policy_uri");
        p.add("op_tos_uri");
        p.add("introspection_endpoint_auth_methods_supported");
        p.add("introspection_endpoint_auth_signing_alg_values_supported");
        p.add("revocation_endpoint_auth_methods_supported");
        p.add("revocation_endpoint_auth_signing_alg_values_supported");
        p.add("mtls_endpoint_aliases");
        p.add("tls_client_certificate_bound_access_tokens");
        p.add("dpop_signing_alg_values_supported");
        p.add("authorization_signing_alg_values_supported");
        p.add("authorization_encryption_alg_values_supported");
        p.add("authorization_encryption_enc_values_supported");
        p.add("require_pushed_authorization_requests");
        p.add("incremental_authz_types_supported");
        p.add("authorization_response_iss_parameter_supported");
        p.add("backchannel_token_delivery_modes_supported");
        p.add("backchannel_authentication_request_signing_alg_values_supported");
        p.add("backchannel_user_code_parameter_supported");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }
}

