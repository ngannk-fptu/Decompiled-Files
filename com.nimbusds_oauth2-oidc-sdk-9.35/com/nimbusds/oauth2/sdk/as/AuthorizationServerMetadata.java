/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.Algorithm
 *  com.nimbusds.jose.EncryptionMethod
 *  com.nimbusds.jose.JWEAlgorithm
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.langtag.LangTag
 *  com.nimbusds.langtag.LangTagException
 *  net.minidev.json.JSONObject
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
import com.nimbusds.oauth2.sdk.as.ReadOnlyAuthorizationServerEndpointMetadata;
import com.nimbusds.oauth2.sdk.as.ReadOnlyAuthorizationServerMetadata;
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
import com.nimbusds.openid.connect.sdk.Prompt;
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
extends AuthorizationServerEndpointMetadata
implements ReadOnlyAuthorizationServerMetadata {
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
    private List<Prompt.Type> promptTypes;
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

    @Override
    public Issuer getIssuer() {
        return this.issuer;
    }

    @Override
    public URI getJWKSetURI() {
        return this.jwkSetURI;
    }

    public void setJWKSetURI(URI jwkSetURI) {
        this.jwkSetURI = jwkSetURI;
    }

    @Override
    public Scope getScopes() {
        return this.scope;
    }

    public void setScopes(Scope scope) {
        this.scope = scope;
    }

    @Override
    public List<ResponseType> getResponseTypes() {
        return this.rts;
    }

    public void setResponseTypes(List<ResponseType> rts) {
        this.rts = rts;
    }

    @Override
    public List<ResponseMode> getResponseModes() {
        return this.rms;
    }

    public void setResponseModes(List<ResponseMode> rms) {
        this.rms = rms;
    }

    @Override
    public List<GrantType> getGrantTypes() {
        return this.gts;
    }

    public void setGrantTypes(List<GrantType> gts) {
        this.gts = gts;
    }

    @Override
    public List<CodeChallengeMethod> getCodeChallengeMethods() {
        return this.codeChallengeMethods;
    }

    public void setCodeChallengeMethods(List<CodeChallengeMethod> codeChallengeMethods) {
        this.codeChallengeMethods = codeChallengeMethods;
    }

    @Override
    public List<ClientAuthenticationMethod> getTokenEndpointAuthMethods() {
        return this.tokenEndpointAuthMethods;
    }

    public void setTokenEndpointAuthMethods(List<ClientAuthenticationMethod> authMethods) {
        this.tokenEndpointAuthMethods = authMethods;
    }

    @Override
    public List<JWSAlgorithm> getTokenEndpointJWSAlgs() {
        return this.tokenEndpointJWSAlgs;
    }

    public void setTokenEndpointJWSAlgs(List<JWSAlgorithm> jwsAlgs) {
        if (jwsAlgs != null && jwsAlgs.contains(Algorithm.NONE)) {
            throw new IllegalArgumentException("The \"none\" algorithm is not accepted");
        }
        this.tokenEndpointJWSAlgs = jwsAlgs;
    }

    @Override
    public List<ClientAuthenticationMethod> getIntrospectionEndpointAuthMethods() {
        return this.introspectionEndpointAuthMethods;
    }

    public void setIntrospectionEndpointAuthMethods(List<ClientAuthenticationMethod> authMethods) {
        this.introspectionEndpointAuthMethods = authMethods;
    }

    @Override
    public List<JWSAlgorithm> getIntrospectionEndpointJWSAlgs() {
        return this.introspectionEndpointJWSAlgs;
    }

    public void setIntrospectionEndpointJWSAlgs(List<JWSAlgorithm> jwsAlgs) {
        if (jwsAlgs != null && jwsAlgs.contains(Algorithm.NONE)) {
            throw new IllegalArgumentException("The \"none\" algorithm is not accepted");
        }
        this.introspectionEndpointJWSAlgs = jwsAlgs;
    }

    @Override
    public List<ClientAuthenticationMethod> getRevocationEndpointAuthMethods() {
        return this.revocationEndpointAuthMethods;
    }

    public void setRevocationEndpointAuthMethods(List<ClientAuthenticationMethod> authMethods) {
        this.revocationEndpointAuthMethods = authMethods;
    }

    @Override
    public List<JWSAlgorithm> getRevocationEndpointJWSAlgs() {
        return this.revocationEndpointJWSAlgs;
    }

    public void setRevocationEndpointJWSAlgs(List<JWSAlgorithm> jwsAlgs) {
        if (jwsAlgs != null && jwsAlgs.contains(Algorithm.NONE)) {
            throw new IllegalArgumentException("The \"none\" algorithm is not accepted");
        }
        this.revocationEndpointJWSAlgs = jwsAlgs;
    }

    @Override
    public List<JWSAlgorithm> getRequestObjectJWSAlgs() {
        return this.requestObjectJWSAlgs;
    }

    public void setRequestObjectJWSAlgs(List<JWSAlgorithm> requestObjectJWSAlgs) {
        this.requestObjectJWSAlgs = requestObjectJWSAlgs;
    }

    @Override
    public List<JWEAlgorithm> getRequestObjectJWEAlgs() {
        return this.requestObjectJWEAlgs;
    }

    public void setRequestObjectJWEAlgs(List<JWEAlgorithm> requestObjectJWEAlgs) {
        this.requestObjectJWEAlgs = requestObjectJWEAlgs;
    }

    @Override
    public List<EncryptionMethod> getRequestObjectJWEEncs() {
        return this.requestObjectJWEEncs;
    }

    public void setRequestObjectJWEEncs(List<EncryptionMethod> requestObjectJWEEncs) {
        this.requestObjectJWEEncs = requestObjectJWEEncs;
    }

    @Override
    public boolean supportsRequestParam() {
        return this.requestParamSupported;
    }

    public void setSupportsRequestParam(boolean requestParamSupported) {
        this.requestParamSupported = requestParamSupported;
    }

    @Override
    public boolean supportsRequestURIParam() {
        return this.requestURIParamSupported;
    }

    public void setSupportsRequestURIParam(boolean requestURIParamSupported) {
        this.requestURIParamSupported = requestURIParamSupported;
    }

    @Override
    public boolean requiresRequestURIRegistration() {
        return this.requireRequestURIReg;
    }

    public void setRequiresRequestURIRegistration(boolean requireRequestURIReg) {
        this.requireRequestURIReg = requireRequestURIReg;
    }

    @Override
    public boolean supportsAuthorizationResponseIssuerParam() {
        return this.authzResponseIssParameterSupported;
    }

    public void setSupportsAuthorizationResponseIssuerParam(boolean authzResponseIssParameterSupported) {
        this.authzResponseIssParameterSupported = authzResponseIssParameterSupported;
    }

    @Override
    public List<LangTag> getUILocales() {
        return this.uiLocales;
    }

    public void setUILocales(List<LangTag> uiLocales) {
        this.uiLocales = uiLocales;
    }

    @Override
    public URI getServiceDocsURI() {
        return this.serviceDocsURI;
    }

    public void setServiceDocsURI(URI serviceDocsURI) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(serviceDocsURI);
        this.serviceDocsURI = serviceDocsURI;
    }

    @Override
    public URI getPolicyURI() {
        return this.policyURI;
    }

    public void setPolicyURI(URI policyURI) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(policyURI);
        this.policyURI = policyURI;
    }

    @Override
    public URI getTermsOfServiceURI() {
        return this.tosURI;
    }

    public void setTermsOfServiceURI(URI tosURI) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(tosURI);
        this.tosURI = tosURI;
    }

    @Override
    public ReadOnlyAuthorizationServerEndpointMetadata getReadOnlyMtlsEndpointAliases() {
        return this.getMtlsEndpointAliases();
    }

    public AuthorizationServerEndpointMetadata getMtlsEndpointAliases() {
        return this.mtlsEndpointAliases;
    }

    public void setMtlsEndpointAliases(AuthorizationServerEndpointMetadata mtlsEndpointAliases) {
        this.mtlsEndpointAliases = mtlsEndpointAliases;
    }

    @Override
    public boolean supportsTLSClientCertificateBoundAccessTokens() {
        return this.tlsClientCertificateBoundAccessTokens;
    }

    public void setSupportsTLSClientCertificateBoundAccessTokens(boolean tlsClientCertBoundTokens) {
        this.tlsClientCertificateBoundAccessTokens = tlsClientCertBoundTokens;
    }

    @Override
    @Deprecated
    public boolean supportsMutualTLSSenderConstrainedAccessTokens() {
        return this.supportsTLSClientCertificateBoundAccessTokens();
    }

    @Deprecated
    public void setSupportsMutualTLSSenderConstrainedAccessTokens(boolean mutualTLSSenderConstrainedAccessTokens) {
        this.setSupportsTLSClientCertificateBoundAccessTokens(mutualTLSSenderConstrainedAccessTokens);
    }

    @Override
    public List<JWSAlgorithm> getDPoPJWSAlgs() {
        return this.dPoPJWSAlgs;
    }

    public void setDPoPJWSAlgs(List<JWSAlgorithm> dPoPJWSAlgs) {
        this.dPoPJWSAlgs = dPoPJWSAlgs;
    }

    @Override
    public List<JWSAlgorithm> getAuthorizationJWSAlgs() {
        return this.authzJWSAlgs;
    }

    public void setAuthorizationJWSAlgs(List<JWSAlgorithm> authzJWSAlgs) {
        this.authzJWSAlgs = authzJWSAlgs;
    }

    @Override
    public List<JWEAlgorithm> getAuthorizationJWEAlgs() {
        return this.authzJWEAlgs;
    }

    public void setAuthorizationJWEAlgs(List<JWEAlgorithm> authzJWEAlgs) {
        this.authzJWEAlgs = authzJWEAlgs;
    }

    @Override
    public List<EncryptionMethod> getAuthorizationJWEEncs() {
        return this.authzJWEEncs;
    }

    public void setAuthorizationJWEEncs(List<EncryptionMethod> authzJWEEncs) {
        this.authzJWEEncs = authzJWEEncs;
    }

    @Override
    public boolean requiresPushedAuthorizationRequests() {
        return this.requirePAR;
    }

    public void requiresPushedAuthorizationRequests(boolean requirePAR) {
        this.requirePAR = requirePAR;
    }

    @Override
    public List<ClientType> getIncrementalAuthorizationTypes() {
        return this.incrementalAuthzTypes;
    }

    public void setIncrementalAuthorizationTypes(List<ClientType> incrementalAuthzTypes) {
        this.incrementalAuthzTypes = incrementalAuthzTypes;
    }

    @Override
    public List<BackChannelTokenDeliveryMode> getBackChannelTokenDeliveryModes() {
        return this.backChannelTokenDeliveryModes;
    }

    public void setBackChannelTokenDeliveryModes(List<BackChannelTokenDeliveryMode> backChannelTokenDeliveryModes) {
        this.backChannelTokenDeliveryModes = backChannelTokenDeliveryModes;
    }

    @Override
    public List<JWSAlgorithm> getBackChannelAuthenticationRequestJWSAlgs() {
        return this.backChannelAuthRequestJWSAlgs;
    }

    public void setBackChannelAuthenticationRequestJWSAlgs(List<JWSAlgorithm> backChannelAuthRequestJWSAlgs) {
        this.backChannelAuthRequestJWSAlgs = backChannelAuthRequestJWSAlgs;
    }

    @Override
    public boolean supportsBackChannelUserCodeParam() {
        return this.backChannelUserCodeSupported;
    }

    public void setSupportsBackChannelUserCodeParam(boolean backChannelUserCodeSupported) {
        this.backChannelUserCodeSupported = backChannelUserCodeSupported;
    }

    @Override
    public List<Prompt.Type> getPromptTypes() {
        return this.promptTypes;
    }

    public void setPromptTypes(List<Prompt.Type> promptTypes) {
        this.promptTypes = promptTypes;
    }

    @Override
    public Object getCustomParameter(String name) {
        return this.customParameters.get((Object)name);
    }

    @Override
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
        this.customParameters.put((Object)name, value);
    }

    @Override
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
        o.put((Object)"issuer", (Object)this.issuer.getValue());
        if (this.jwkSetURI != null) {
            o.put((Object)"jwks_uri", (Object)this.jwkSetURI.toString());
        }
        if (this.scope != null) {
            o.put((Object)"scopes_supported", this.scope.toStringList());
        }
        if (this.rts != null) {
            stringList = new ArrayList<String>(this.rts.size());
            for (ResponseType responseType : this.rts) {
                stringList.add(responseType.toString());
            }
            o.put((Object)"response_types_supported", stringList);
        }
        if (this.rms != null) {
            stringList = new ArrayList(this.rms.size());
            for (ResponseMode responseMode : this.rms) {
                stringList.add(responseMode.getValue());
            }
            o.put((Object)"response_modes_supported", stringList);
        }
        if (this.gts != null) {
            stringList = new ArrayList(this.gts.size());
            for (GrantType grantType : this.gts) {
                stringList.add(grantType.toString());
            }
            o.put((Object)"grant_types_supported", stringList);
        }
        if (this.codeChallengeMethods != null) {
            stringList = new ArrayList(this.codeChallengeMethods.size());
            for (CodeChallengeMethod codeChallengeMethod : this.codeChallengeMethods) {
                stringList.add(codeChallengeMethod.getValue());
            }
            o.put((Object)"code_challenge_methods_supported", stringList);
        }
        if (this.tokenEndpointAuthMethods != null) {
            stringList = new ArrayList(this.tokenEndpointAuthMethods.size());
            for (ClientAuthenticationMethod clientAuthenticationMethod : this.tokenEndpointAuthMethods) {
                stringList.add(clientAuthenticationMethod.getValue());
            }
            o.put((Object)"token_endpoint_auth_methods_supported", stringList);
        }
        if (this.tokenEndpointJWSAlgs != null) {
            stringList = new ArrayList(this.tokenEndpointJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.tokenEndpointJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put((Object)"token_endpoint_auth_signing_alg_values_supported", stringList);
        }
        if (this.introspectionEndpointAuthMethods != null) {
            stringList = new ArrayList(this.introspectionEndpointAuthMethods.size());
            for (ClientAuthenticationMethod clientAuthenticationMethod : this.introspectionEndpointAuthMethods) {
                stringList.add(clientAuthenticationMethod.getValue());
            }
            o.put((Object)"introspection_endpoint_auth_methods_supported", stringList);
        }
        if (this.introspectionEndpointJWSAlgs != null) {
            stringList = new ArrayList(this.introspectionEndpointJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.introspectionEndpointJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put((Object)"introspection_endpoint_auth_signing_alg_values_supported", stringList);
        }
        if (this.revocationEndpointAuthMethods != null) {
            stringList = new ArrayList(this.revocationEndpointAuthMethods.size());
            for (ClientAuthenticationMethod clientAuthenticationMethod : this.revocationEndpointAuthMethods) {
                stringList.add(clientAuthenticationMethod.getValue());
            }
            o.put((Object)"revocation_endpoint_auth_methods_supported", stringList);
        }
        if (this.revocationEndpointJWSAlgs != null) {
            stringList = new ArrayList(this.revocationEndpointJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.revocationEndpointJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put((Object)"revocation_endpoint_auth_signing_alg_values_supported", stringList);
        }
        if (this.requestObjectJWSAlgs != null) {
            stringList = new ArrayList(this.requestObjectJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.requestObjectJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put((Object)"request_object_signing_alg_values_supported", stringList);
        }
        if (this.requestObjectJWEAlgs != null) {
            stringList = new ArrayList(this.requestObjectJWEAlgs.size());
            for (JWEAlgorithm jWEAlgorithm : this.requestObjectJWEAlgs) {
                stringList.add(jWEAlgorithm.getName());
            }
            o.put((Object)"request_object_encryption_alg_values_supported", stringList);
        }
        if (this.requestObjectJWEEncs != null) {
            stringList = new ArrayList(this.requestObjectJWEEncs.size());
            for (EncryptionMethod encryptionMethod : this.requestObjectJWEEncs) {
                stringList.add(encryptionMethod.getName());
            }
            o.put((Object)"request_object_encryption_enc_values_supported", stringList);
        }
        if (this.uiLocales != null) {
            stringList = new ArrayList(this.uiLocales.size());
            for (LangTag langTag : this.uiLocales) {
                stringList.add(langTag.toString());
            }
            o.put((Object)"ui_locales_supported", stringList);
        }
        if (this.serviceDocsURI != null) {
            o.put((Object)"service_documentation", (Object)this.serviceDocsURI.toString());
        }
        if (this.policyURI != null) {
            o.put((Object)"op_policy_uri", (Object)this.policyURI.toString());
        }
        if (this.tosURI != null) {
            o.put((Object)"op_tos_uri", (Object)this.tosURI.toString());
        }
        if (this.requestParamSupported) {
            o.put((Object)"request_parameter_supported", (Object)true);
        }
        if (this.requestURIParamSupported) {
            o.put((Object)"request_uri_parameter_supported", (Object)true);
        }
        if (this.requireRequestURIReg) {
            o.put((Object)"require_request_uri_registration", (Object)true);
        }
        if (this.authzResponseIssParameterSupported) {
            o.put((Object)"authorization_response_iss_parameter_supported", (Object)true);
        }
        if (this.mtlsEndpointAliases != null) {
            o.put((Object)"mtls_endpoint_aliases", (Object)this.mtlsEndpointAliases.toJSONObject());
        }
        if (this.tlsClientCertificateBoundAccessTokens) {
            o.put((Object)"tls_client_certificate_bound_access_tokens", (Object)true);
        }
        if (this.dPoPJWSAlgs != null) {
            stringList = new ArrayList(this.dPoPJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.dPoPJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put((Object)"dpop_signing_alg_values_supported", stringList);
        }
        if (this.authzJWSAlgs != null) {
            stringList = new ArrayList(this.authzJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.authzJWSAlgs) {
                stringList.add(jWSAlgorithm.getName());
            }
            o.put((Object)"authorization_signing_alg_values_supported", stringList);
        }
        if (this.authzJWEAlgs != null) {
            stringList = new ArrayList(this.authzJWEAlgs.size());
            for (JWEAlgorithm jWEAlgorithm : this.authzJWEAlgs) {
                stringList.add(jWEAlgorithm.getName());
            }
            o.put((Object)"authorization_encryption_alg_values_supported", stringList);
        }
        if (this.authzJWEEncs != null) {
            stringList = new ArrayList(this.authzJWEEncs.size());
            for (EncryptionMethod encryptionMethod : this.authzJWEEncs) {
                stringList.add(encryptionMethod.getName());
            }
            o.put((Object)"authorization_encryption_enc_values_supported", stringList);
        }
        if (this.requirePAR) {
            o.put((Object)"require_pushed_authorization_requests", (Object)true);
        }
        if (CollectionUtils.isNotEmpty(this.incrementalAuthzTypes)) {
            stringList = new ArrayList(this.incrementalAuthzTypes.size());
            for (ClientType clientType : this.incrementalAuthzTypes) {
                if (clientType == null) continue;
                stringList.add(clientType.name().toLowerCase());
            }
            o.put((Object)"incremental_authz_types_supported", stringList);
        }
        if (this.backChannelTokenDeliveryModes != null) {
            stringList = new ArrayList(this.backChannelTokenDeliveryModes.size());
            for (BackChannelTokenDeliveryMode backChannelTokenDeliveryMode : this.backChannelTokenDeliveryModes) {
                if (backChannelTokenDeliveryMode == null) continue;
                stringList.add(backChannelTokenDeliveryMode.getValue());
            }
            o.put((Object)"backchannel_token_delivery_modes_supported", stringList);
        }
        if (this.backChannelAuthRequestJWSAlgs != null) {
            stringList = new ArrayList(this.backChannelAuthRequestJWSAlgs.size());
            for (JWSAlgorithm jWSAlgorithm : this.backChannelAuthRequestJWSAlgs) {
                if (jWSAlgorithm == null) continue;
                stringList.add(jWSAlgorithm.getName());
            }
            o.put((Object)"backchannel_authentication_request_signing_alg_values_supported", stringList);
        }
        if (this.backChannelUserCodeSupported) {
            o.put((Object)"backchannel_user_code_parameter_supported", (Object)true);
        }
        if (this.promptTypes != null) {
            stringList = new ArrayList(this.promptTypes.size());
            for (Prompt.Type type : this.promptTypes) {
                stringList.add(type.toString());
            }
            o.put((Object)"prompt_values_supported", stringList);
        }
        o.putAll((Map)this.customParameters);
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
        as.setBackChannelAuthenticationEndpointURI(asEndpoints.getBackChannelAuthenticationEndpointURI());
        as.jwkSetURI = JSONObjectUtils.getURI(jsonObject, "jwks_uri", null);
        if (jsonObject.get((Object)"scopes_supported") != null) {
            as.scope = new Scope();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "scopes_supported")) {
                if (v == null) continue;
                as.scope.add(new Scope.Value(v));
            }
        }
        if (jsonObject.get((Object)"response_types_supported") != null) {
            as.rts = new ArrayList<ResponseType>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "response_types_supported")) {
                if (v == null) continue;
                as.rts.add(ResponseType.parse(v));
            }
        }
        if (jsonObject.get((Object)"response_modes_supported") != null) {
            as.rms = new ArrayList<ResponseMode>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "response_modes_supported")) {
                if (v == null) continue;
                as.rms.add(new ResponseMode(v));
            }
        }
        if (jsonObject.get((Object)"grant_types_supported") != null) {
            as.gts = new ArrayList<GrantType>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "grant_types_supported")) {
                if (v == null) continue;
                as.gts.add(GrantType.parse(v));
            }
        }
        if (jsonObject.get((Object)"code_challenge_methods_supported") != null) {
            as.codeChallengeMethods = new ArrayList<CodeChallengeMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "code_challenge_methods_supported")) {
                if (v == null) continue;
                as.codeChallengeMethods.add(CodeChallengeMethod.parse(v));
            }
        }
        if (jsonObject.get((Object)"token_endpoint_auth_methods_supported") != null) {
            as.tokenEndpointAuthMethods = new ArrayList<ClientAuthenticationMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "token_endpoint_auth_methods_supported")) {
                if (v == null) continue;
                as.tokenEndpointAuthMethods.add(ClientAuthenticationMethod.parse(v));
            }
        }
        if (jsonObject.get((Object)"token_endpoint_auth_signing_alg_values_supported") != null) {
            as.tokenEndpointJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "token_endpoint_auth_signing_alg_values_supported")) {
                if (v != null && v.equals(Algorithm.NONE.getName())) {
                    throw new ParseException("The none algorithm is not accepted");
                }
                if (v == null) continue;
                as.tokenEndpointJWSAlgs.add(JWSAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"introspection_endpoint_auth_methods_supported") != null) {
            as.introspectionEndpointAuthMethods = new ArrayList<ClientAuthenticationMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "introspection_endpoint_auth_methods_supported")) {
                if (v == null) continue;
                as.introspectionEndpointAuthMethods.add(ClientAuthenticationMethod.parse(v));
            }
        }
        if (jsonObject.get((Object)"introspection_endpoint_auth_signing_alg_values_supported") != null) {
            as.introspectionEndpointJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "introspection_endpoint_auth_signing_alg_values_supported")) {
                if (v != null && v.equals(Algorithm.NONE.getName())) {
                    throw new ParseException("The none algorithm is not accepted");
                }
                if (v == null) continue;
                as.introspectionEndpointJWSAlgs.add(JWSAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"revocation_endpoint_auth_methods_supported") != null) {
            as.revocationEndpointAuthMethods = new ArrayList<ClientAuthenticationMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "revocation_endpoint_auth_methods_supported")) {
                if (v == null) continue;
                as.revocationEndpointAuthMethods.add(ClientAuthenticationMethod.parse(v));
            }
        }
        if (jsonObject.get((Object)"revocation_endpoint_auth_signing_alg_values_supported") != null) {
            as.revocationEndpointJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "revocation_endpoint_auth_signing_alg_values_supported")) {
                if (v != null && v.equals(Algorithm.NONE.getName())) {
                    throw new ParseException("The none algorithm is not accepted");
                }
                if (v == null) continue;
                as.revocationEndpointJWSAlgs.add(JWSAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"request_object_signing_alg_values_supported") != null) {
            as.requestObjectJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "request_object_signing_alg_values_supported")) {
                if (v == null) continue;
                as.requestObjectJWSAlgs.add(JWSAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"request_object_encryption_alg_values_supported") != null) {
            as.requestObjectJWEAlgs = new ArrayList<JWEAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "request_object_encryption_alg_values_supported")) {
                if (v == null) continue;
                as.requestObjectJWEAlgs.add(JWEAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"request_object_encryption_enc_values_supported") != null) {
            as.requestObjectJWEEncs = new ArrayList<EncryptionMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "request_object_encryption_enc_values_supported")) {
                if (v == null) continue;
                as.requestObjectJWEEncs.add(EncryptionMethod.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"ui_locales_supported") != null) {
            as.uiLocales = new ArrayList<LangTag>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "ui_locales_supported")) {
                if (v == null) continue;
                try {
                    as.uiLocales.add(LangTag.parse((String)v));
                }
                catch (LangTagException e) {
                    throw new ParseException("Invalid ui_locales_supported field: " + e.getMessage(), e);
                }
            }
        }
        if (jsonObject.get((Object)"service_documentation") != null) {
            try {
                as.setServiceDocsURI(JSONObjectUtils.getURI(jsonObject, "service_documentation"));
            }
            catch (IllegalArgumentException e) {
                throw new ParseException("Illegal service_documentation parameter: " + e.getMessage());
            }
        }
        if (jsonObject.get((Object)"op_policy_uri") != null) {
            try {
                as.setPolicyURI(JSONObjectUtils.getURI(jsonObject, "op_policy_uri"));
            }
            catch (IllegalArgumentException e) {
                throw new ParseException("Illegal op_policy_uri parameter: " + e.getMessage());
            }
        }
        if (jsonObject.get((Object)"op_tos_uri") != null) {
            try {
                as.setTermsOfServiceURI(JSONObjectUtils.getURI(jsonObject, "op_tos_uri"));
            }
            catch (IllegalArgumentException e) {
                throw new ParseException("Illegal op_tos_uri parameter: " + e.getMessage());
            }
        }
        if (jsonObject.get((Object)"request_parameter_supported") != null) {
            as.requestParamSupported = JSONObjectUtils.getBoolean(jsonObject, "request_parameter_supported");
        }
        if (jsonObject.get((Object)"request_uri_parameter_supported") != null) {
            as.requestURIParamSupported = JSONObjectUtils.getBoolean(jsonObject, "request_uri_parameter_supported");
        }
        if (jsonObject.get((Object)"require_request_uri_registration") != null) {
            as.requireRequestURIReg = JSONObjectUtils.getBoolean(jsonObject, "require_request_uri_registration");
        }
        if (jsonObject.get((Object)"authorization_response_iss_parameter_supported") != null) {
            as.authzResponseIssParameterSupported = JSONObjectUtils.getBoolean(jsonObject, "authorization_response_iss_parameter_supported");
        }
        if (jsonObject.get((Object)"mtls_endpoint_aliases") != null) {
            as.mtlsEndpointAliases = AuthorizationServerEndpointMetadata.parse(JSONObjectUtils.getJSONObject(jsonObject, "mtls_endpoint_aliases"));
        }
        if (jsonObject.get((Object)"tls_client_certificate_bound_access_tokens") != null) {
            as.tlsClientCertificateBoundAccessTokens = JSONObjectUtils.getBoolean(jsonObject, "tls_client_certificate_bound_access_tokens");
        }
        if (jsonObject.get((Object)"dpop_signing_alg_values_supported") != null) {
            as.dPoPJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "dpop_signing_alg_values_supported")) {
                if (v == null) continue;
                as.dPoPJWSAlgs.add(JWSAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"authorization_signing_alg_values_supported") != null) {
            as.authzJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "authorization_signing_alg_values_supported")) {
                if (v == null) continue;
                as.authzJWSAlgs.add(JWSAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"authorization_encryption_alg_values_supported") != null) {
            as.authzJWEAlgs = new ArrayList<JWEAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "authorization_encryption_alg_values_supported")) {
                if (v == null) continue;
                as.authzJWEAlgs.add(JWEAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"authorization_encryption_enc_values_supported") != null) {
            as.authzJWEEncs = new ArrayList<EncryptionMethod>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "authorization_encryption_enc_values_supported")) {
                if (v == null) continue;
                as.authzJWEEncs.add(EncryptionMethod.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"require_pushed_authorization_requests") != null) {
            as.requiresPushedAuthorizationRequests(JSONObjectUtils.getBoolean(jsonObject, "require_pushed_authorization_requests"));
        }
        if (jsonObject.get((Object)"incremental_authz_types_supported") != null) {
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
        if (jsonObject.get((Object)"backchannel_token_delivery_modes_supported") != null) {
            as.backChannelTokenDeliveryModes = new ArrayList<BackChannelTokenDeliveryMode>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "backchannel_token_delivery_modes_supported")) {
                if (v == null) continue;
                as.backChannelTokenDeliveryModes.add(BackChannelTokenDeliveryMode.parse(v));
            }
        }
        if (jsonObject.get((Object)"backchannel_authentication_request_signing_alg_values_supported") != null) {
            as.backChannelAuthRequestJWSAlgs = new ArrayList<JWSAlgorithm>();
            for (String v : JSONObjectUtils.getStringArray(jsonObject, "backchannel_authentication_request_signing_alg_values_supported")) {
                if (v == null) continue;
                as.backChannelAuthRequestJWSAlgs.add(JWSAlgorithm.parse((String)v));
            }
        }
        if (jsonObject.get((Object)"backchannel_user_code_parameter_supported") != null) {
            as.backChannelUserCodeSupported = JSONObjectUtils.getBoolean(jsonObject, "backchannel_user_code_parameter_supported");
        }
        if (jsonObject.get((Object)"prompt_values_supported") != null) {
            as.promptTypes = new ArrayList<Prompt.Type>();
            for (String v : JSONObjectUtils.getStringList(jsonObject, "prompt_values_supported")) {
                if (v == null) continue;
                as.promptTypes.add(Prompt.Type.parse(v));
            }
        }
        JSONObject customParams = new JSONObject((Map)jsonObject);
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
        p.add("prompt_values_supported");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }
}

