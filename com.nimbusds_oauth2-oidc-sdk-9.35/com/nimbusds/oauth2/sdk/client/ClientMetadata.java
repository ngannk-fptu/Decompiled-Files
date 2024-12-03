/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.EncryptionMethod
 *  com.nimbusds.jose.JWEAlgorithm
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.JWSObject$State
 *  com.nimbusds.jose.jwk.JWKSet
 *  com.nimbusds.jwt.SignedJWT
 *  com.nimbusds.langtag.LangTag
 *  com.nimbusds.langtag.LangTagUtils
 *  net.minidev.json.JSONArray
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.client;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.langtag.LangTag;
import com.nimbusds.langtag.LangTagUtils;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.ciba.BackChannelTokenDeliveryMode;
import com.nimbusds.oauth2.sdk.client.RegistrationError;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.id.SoftwareID;
import com.nimbusds.oauth2.sdk.id.SoftwareVersion;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.openid.connect.sdk.federation.registration.ClientRegistrationType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class ClientMetadata {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    public static final Set<String> PROHIBITED_REDIRECT_URI_SCHEMES;
    private Set<URI> redirectURIs;
    private Scope scope;
    private Set<ResponseType> responseTypes;
    private Set<GrantType> grantTypes;
    private List<String> contacts;
    private final Map<LangTag, String> nameEntries;
    private final Map<LangTag, URI> logoURIEntries;
    private final Map<LangTag, URI> uriEntries;
    private final Map<LangTag, URI> policyURIEntries;
    private final Map<LangTag, URI> tosURIEntries;
    private ClientAuthenticationMethod authMethod;
    private JWSAlgorithm authJWSAlg;
    private URI jwkSetURI;
    private JWKSet jwkSet;
    private Set<URI> requestObjectURIs;
    private JWSAlgorithm requestObjectJWSAlg;
    private JWEAlgorithm requestObjectJWEAlg;
    private EncryptionMethod requestObjectJWEEnc;
    private SoftwareID softwareID;
    private SoftwareVersion softwareVersion;
    private SignedJWT softwareStatement;
    private boolean tlsClientCertificateBoundAccessTokens = false;
    private String tlsClientAuthSubjectDN = null;
    private String tlsClientAuthSanDNS = null;
    private String tlsClientAuthSanURI = null;
    private String tlsClientAuthSanIP = null;
    private String tlsClientAuthSanEmail = null;
    private JWSAlgorithm authzJWSAlg;
    private JWEAlgorithm authzJWEAlg;
    private EncryptionMethod authzJWEEnc;
    private boolean requirePAR = false;
    private BackChannelTokenDeliveryMode backChannelTokenDeliveryMode;
    private URI backChannelClientNotificationEndpoint;
    private JWSAlgorithm backChannelAuthRequestJWSAlg;
    private boolean backChannelUserCodeParam = false;
    private List<ClientRegistrationType> clientRegistrationTypes;
    private String organizationName;
    private JSONObject customFields;

    public ClientMetadata() {
        this.nameEntries = new HashMap<LangTag, String>();
        this.logoURIEntries = new HashMap<LangTag, URI>();
        this.uriEntries = new HashMap<LangTag, URI>();
        this.policyURIEntries = new HashMap<LangTag, URI>();
        this.tosURIEntries = new HashMap<LangTag, URI>();
        this.customFields = new JSONObject();
    }

    public ClientMetadata(ClientMetadata metadata) {
        this.redirectURIs = metadata.getRedirectionURIs();
        this.scope = metadata.getScope();
        this.responseTypes = metadata.getResponseTypes();
        this.grantTypes = metadata.getGrantTypes();
        this.contacts = metadata.getEmailContacts();
        this.nameEntries = metadata.getNameEntries();
        this.logoURIEntries = metadata.getLogoURIEntries();
        this.uriEntries = metadata.getURIEntries();
        this.policyURIEntries = metadata.getPolicyURIEntries();
        this.tosURIEntries = metadata.getTermsOfServiceURIEntries();
        this.authMethod = metadata.getTokenEndpointAuthMethod();
        this.authJWSAlg = metadata.getTokenEndpointAuthJWSAlg();
        this.jwkSetURI = metadata.getJWKSetURI();
        this.jwkSet = metadata.getJWKSet();
        this.requestObjectURIs = metadata.getRequestObjectURIs();
        this.requestObjectJWSAlg = metadata.getRequestObjectJWSAlg();
        this.requestObjectJWEAlg = metadata.getRequestObjectJWEAlg();
        this.requestObjectJWEEnc = metadata.getRequestObjectJWEEnc();
        this.softwareID = metadata.getSoftwareID();
        this.softwareVersion = metadata.getSoftwareVersion();
        this.softwareStatement = metadata.getSoftwareStatement();
        this.tlsClientCertificateBoundAccessTokens = metadata.getTLSClientCertificateBoundAccessTokens();
        this.tlsClientAuthSubjectDN = metadata.getTLSClientAuthSubjectDN();
        this.tlsClientAuthSanDNS = metadata.getTLSClientAuthSanDNS();
        this.tlsClientAuthSanURI = metadata.getTLSClientAuthSanURI();
        this.tlsClientAuthSanIP = metadata.getTLSClientAuthSanIP();
        this.tlsClientAuthSanEmail = metadata.getTLSClientAuthSanEmail();
        this.authzJWSAlg = metadata.getAuthorizationJWSAlg();
        this.authzJWEAlg = metadata.getAuthorizationJWEAlg();
        this.authzJWEEnc = metadata.getAuthorizationJWEEnc();
        this.requirePAR = metadata.requiresPushedAuthorizationRequests();
        this.backChannelTokenDeliveryMode = metadata.getBackChannelTokenDeliveryMode();
        this.backChannelClientNotificationEndpoint = metadata.getBackChannelClientNotificationEndpoint();
        this.backChannelAuthRequestJWSAlg = metadata.getBackChannelAuthRequestJWSAlg();
        this.backChannelUserCodeParam = metadata.supportsBackChannelUserCodeParam();
        this.clientRegistrationTypes = metadata.getClientRegistrationTypes();
        this.organizationName = metadata.getOrganizationName();
        this.customFields = metadata.getCustomFields();
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public Set<URI> getRedirectionURIs() {
        return this.redirectURIs;
    }

    public URI getRedirectionURI() {
        if (this.redirectURIs != null && !this.redirectURIs.isEmpty()) {
            return this.redirectURIs.iterator().next();
        }
        return null;
    }

    public Set<String> getRedirectionURIStrings() {
        if (this.redirectURIs == null) {
            return null;
        }
        HashSet<String> uriStrings = new HashSet<String>();
        for (URI uri : this.redirectURIs) {
            uriStrings.add(uri.toString());
        }
        return uriStrings;
    }

    public void setRedirectionURIs(Set<URI> redirectURIs) {
        if (redirectURIs != null) {
            for (URI uri : redirectURIs) {
                if (uri == null) {
                    throw new IllegalArgumentException("The redirect_uri must not be null");
                }
                if (uri.getFragment() != null) {
                    throw new IllegalArgumentException("The redirect_uri must not contain fragment");
                }
                URIUtils.ensureSchemeIsNotProhibited(uri, PROHIBITED_REDIRECT_URI_SCHEMES);
            }
            this.redirectURIs = Collections.unmodifiableSet(redirectURIs);
        } else {
            this.redirectURIs = null;
        }
    }

    public void setRedirectionURI(URI redirectURI) {
        this.setRedirectionURIs(redirectURI != null ? Collections.singleton(redirectURI) : null);
    }

    public Scope getScope() {
        return this.scope;
    }

    public boolean hasScopeValue(Scope.Value scopeValue) {
        return this.scope != null && this.scope.contains(scopeValue);
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Set<ResponseType> getResponseTypes() {
        return this.responseTypes;
    }

    public void setResponseTypes(Set<ResponseType> responseTypes) {
        this.responseTypes = responseTypes;
    }

    public Set<GrantType> getGrantTypes() {
        return this.grantTypes;
    }

    public void setGrantTypes(Set<GrantType> grantTypes) {
        this.grantTypes = grantTypes;
    }

    public List<String> getEmailContacts() {
        return this.contacts;
    }

    public void setEmailContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public String getName() {
        return this.getName(null);
    }

    public String getName(LangTag langTag) {
        return this.nameEntries.get(langTag);
    }

    public Map<LangTag, String> getNameEntries() {
        return this.nameEntries;
    }

    public void setName(String name) {
        this.nameEntries.put(null, name);
    }

    public void setName(String name, LangTag langTag) {
        this.nameEntries.put(langTag, name);
    }

    public URI getLogoURI() {
        return this.getLogoURI(null);
    }

    public URI getLogoURI(LangTag langTag) {
        return this.logoURIEntries.get(langTag);
    }

    public Map<LangTag, URI> getLogoURIEntries() {
        return this.logoURIEntries;
    }

    public void setLogoURI(URI logoURI) {
        this.logoURIEntries.put(null, logoURI);
    }

    public void setLogoURI(URI logoURI, LangTag langTag) {
        this.logoURIEntries.put(langTag, logoURI);
    }

    public URI getURI() {
        return this.getURI(null);
    }

    public URI getURI(LangTag langTag) {
        return this.uriEntries.get(langTag);
    }

    public Map<LangTag, URI> getURIEntries() {
        return this.uriEntries;
    }

    public void setURI(URI uri) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(uri);
        this.uriEntries.put(null, uri);
    }

    public void setURI(URI uri, LangTag langTag) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(uri);
        this.uriEntries.put(langTag, uri);
    }

    public URI getPolicyURI() {
        return this.getPolicyURI(null);
    }

    public URI getPolicyURI(LangTag langTag) {
        return this.policyURIEntries.get(langTag);
    }

    public Map<LangTag, URI> getPolicyURIEntries() {
        return this.policyURIEntries;
    }

    public void setPolicyURI(URI policyURI) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(policyURI);
        this.policyURIEntries.put(null, policyURI);
    }

    public void setPolicyURI(URI policyURI, LangTag langTag) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(policyURI);
        this.policyURIEntries.put(langTag, policyURI);
    }

    public URI getTermsOfServiceURI() {
        return this.getTermsOfServiceURI(null);
    }

    public URI getTermsOfServiceURI(LangTag langTag) {
        return this.tosURIEntries.get(langTag);
    }

    public Map<LangTag, URI> getTermsOfServiceURIEntries() {
        return this.tosURIEntries;
    }

    public void setTermsOfServiceURI(URI tosURI) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(tosURI);
        this.tosURIEntries.put(null, tosURI);
    }

    public void setTermsOfServiceURI(URI tosURI, LangTag langTag) {
        URIUtils.ensureSchemeIsHTTPSorHTTP(tosURI);
        this.tosURIEntries.put(langTag, tosURI);
    }

    public ClientAuthenticationMethod getTokenEndpointAuthMethod() {
        return this.authMethod;
    }

    public void setTokenEndpointAuthMethod(ClientAuthenticationMethod authMethod) {
        this.authMethod = authMethod;
    }

    public JWSAlgorithm getTokenEndpointAuthJWSAlg() {
        return this.authJWSAlg;
    }

    public void setTokenEndpointAuthJWSAlg(JWSAlgorithm authJWSAlg) {
        this.authJWSAlg = authJWSAlg;
    }

    public URI getJWKSetURI() {
        return this.jwkSetURI;
    }

    public void setJWKSetURI(URI jwkSetURI) {
        this.jwkSetURI = jwkSetURI;
    }

    public JWKSet getJWKSet() {
        return this.jwkSet;
    }

    public void setJWKSet(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    public Set<URI> getRequestObjectURIs() {
        return this.requestObjectURIs;
    }

    public void setRequestObjectURIs(Set<URI> requestObjectURIs) {
        this.requestObjectURIs = requestObjectURIs;
    }

    public JWSAlgorithm getRequestObjectJWSAlg() {
        return this.requestObjectJWSAlg;
    }

    public void setRequestObjectJWSAlg(JWSAlgorithm requestObjectJWSAlg) {
        this.requestObjectJWSAlg = requestObjectJWSAlg;
    }

    public JWEAlgorithm getRequestObjectJWEAlg() {
        return this.requestObjectJWEAlg;
    }

    public void setRequestObjectJWEAlg(JWEAlgorithm requestObjectJWEAlg) {
        this.requestObjectJWEAlg = requestObjectJWEAlg;
    }

    public EncryptionMethod getRequestObjectJWEEnc() {
        return this.requestObjectJWEEnc;
    }

    public void setRequestObjectJWEEnc(EncryptionMethod requestObjectJWEEnc) {
        this.requestObjectJWEEnc = requestObjectJWEEnc;
    }

    public SoftwareID getSoftwareID() {
        return this.softwareID;
    }

    public void setSoftwareID(SoftwareID softwareID) {
        this.softwareID = softwareID;
    }

    public SoftwareVersion getSoftwareVersion() {
        return this.softwareVersion;
    }

    public void setSoftwareVersion(SoftwareVersion softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public SignedJWT getSoftwareStatement() {
        return this.softwareStatement;
    }

    public void setSoftwareStatement(SignedJWT softwareStatement) {
        if (softwareStatement != null && softwareStatement.getState().equals((Object)JWSObject.State.UNSIGNED)) {
            throw new IllegalArgumentException("The software statement must be signed");
        }
        this.softwareStatement = softwareStatement;
    }

    public boolean getTLSClientCertificateBoundAccessTokens() {
        return this.tlsClientCertificateBoundAccessTokens;
    }

    public void setTLSClientCertificateBoundAccessTokens(boolean tlsClientCertBoundTokens) {
        this.tlsClientCertificateBoundAccessTokens = tlsClientCertBoundTokens;
    }

    @Deprecated
    public boolean getMutualTLSSenderConstrainedAccessTokens() {
        return this.tlsClientCertificateBoundAccessTokens;
    }

    @Deprecated
    public void setMutualTLSSenderConstrainedAccessTokens(boolean tlsSenderAccessTokens) {
        this.tlsClientCertificateBoundAccessTokens = tlsSenderAccessTokens;
    }

    public String getTLSClientAuthSubjectDN() {
        return this.tlsClientAuthSubjectDN;
    }

    public void setTLSClientAuthSubjectDN(String subjectDN) {
        this.tlsClientAuthSubjectDN = subjectDN;
    }

    public String getTLSClientAuthSanDNS() {
        return this.tlsClientAuthSanDNS;
    }

    public void setTLSClientAuthSanDNS(String dns) {
        this.tlsClientAuthSanDNS = dns;
    }

    public String getTLSClientAuthSanURI() {
        return this.tlsClientAuthSanURI;
    }

    public void setTLSClientAuthSanURI(String uri) {
        this.tlsClientAuthSanURI = uri;
    }

    public String getTLSClientAuthSanIP() {
        return this.tlsClientAuthSanIP;
    }

    public void setTLSClientAuthSanIP(String ip) {
        this.tlsClientAuthSanIP = ip;
    }

    public String getTLSClientAuthSanEmail() {
        return this.tlsClientAuthSanEmail;
    }

    public void setTLSClientAuthSanEmail(String email) {
        this.tlsClientAuthSanEmail = email;
    }

    private void ensureExactlyOneCertSubjectFieldForTLSClientAuth() throws IllegalStateException {
        if (!ClientAuthenticationMethod.TLS_CLIENT_AUTH.equals(this.getTokenEndpointAuthMethod())) {
            return;
        }
        if (this.tlsClientAuthSubjectDN == null && this.tlsClientAuthSanDNS == null && this.tlsClientAuthSanURI == null && this.tlsClientAuthSanIP == null && this.tlsClientAuthSanEmail == null) {
            throw new IllegalStateException("A certificate field must be specified to indicate the subject in tls_client_auth: tls_client_auth_subject_dn, tls_client_auth_san_dns, tls_client_auth_san_uri, tls_client_auth_san_ip or tls_client_auth_san_email");
        }
        String exceptionMessage = "Exactly one certificate field must be specified to indicate the subject in tls_client_auth: tls_client_auth_subject_dn, tls_client_auth_san_dns, tls_client_auth_san_uri, tls_client_auth_san_ip or tls_client_auth_san_email";
        if (this.tlsClientAuthSubjectDN != null && (this.tlsClientAuthSanDNS != null || this.tlsClientAuthSanURI != null || this.tlsClientAuthSanIP != null || this.tlsClientAuthSanEmail != null)) {
            throw new IllegalStateException(exceptionMessage);
        }
        if (this.tlsClientAuthSanDNS != null && (this.tlsClientAuthSanURI != null || this.tlsClientAuthSanIP != null || this.tlsClientAuthSanEmail != null)) {
            throw new IllegalStateException(exceptionMessage);
        }
        if (this.tlsClientAuthSanURI != null && (this.tlsClientAuthSanIP != null || this.tlsClientAuthSanEmail != null)) {
            throw new IllegalStateException(exceptionMessage);
        }
        if (this.tlsClientAuthSanIP != null && this.tlsClientAuthSanEmail != null) {
            throw new IllegalStateException(exceptionMessage);
        }
    }

    public JWSAlgorithm getAuthorizationJWSAlg() {
        return this.authzJWSAlg;
    }

    public void setAuthorizationJWSAlg(JWSAlgorithm authzJWSAlg) {
        if (new JWSAlgorithm("none").equals((Object)authzJWSAlg)) {
            throw new IllegalArgumentException("The JWS algorithm must not be \"none\"");
        }
        this.authzJWSAlg = authzJWSAlg;
    }

    public JWEAlgorithm getAuthorizationJWEAlg() {
        return this.authzJWEAlg;
    }

    public void setAuthorizationJWEAlg(JWEAlgorithm authzJWEAlg) {
        this.authzJWEAlg = authzJWEAlg;
    }

    public EncryptionMethod getAuthorizationJWEEnc() {
        return this.authzJWEEnc;
    }

    public void setAuthorizationJWEEnc(EncryptionMethod authzJWEEnc) {
        this.authzJWEEnc = authzJWEEnc;
    }

    public boolean requiresPushedAuthorizationRequests() {
        return this.requirePAR;
    }

    public void requiresPushedAuthorizationRequests(boolean requirePAR) {
        this.requirePAR = requirePAR;
    }

    public BackChannelTokenDeliveryMode getBackChannelTokenDeliveryMode() {
        return this.backChannelTokenDeliveryMode;
    }

    public void setBackChannelTokenDeliveryMode(BackChannelTokenDeliveryMode backChannelTokenDeliveryMode) {
        this.backChannelTokenDeliveryMode = backChannelTokenDeliveryMode;
    }

    public URI getBackChannelClientNotificationEndpoint() {
        return this.backChannelClientNotificationEndpoint;
    }

    public void setBackChannelClientNotificationEndpoint(URI backChannelClientNotificationEndpoint) {
        this.backChannelClientNotificationEndpoint = backChannelClientNotificationEndpoint;
    }

    public JWSAlgorithm getBackChannelAuthRequestJWSAlg() {
        return this.backChannelAuthRequestJWSAlg;
    }

    public void setBackChannelAuthRequestJWSAlg(JWSAlgorithm backChannelAuthRequestJWSAlg) {
        this.backChannelAuthRequestJWSAlg = backChannelAuthRequestJWSAlg;
    }

    public boolean supportsBackChannelUserCodeParam() {
        return this.backChannelUserCodeParam;
    }

    public void setSupportsBackChannelUserCodeParam(boolean backChannelUserCodeParam) {
        this.backChannelUserCodeParam = backChannelUserCodeParam;
    }

    public List<ClientRegistrationType> getClientRegistrationTypes() {
        return this.clientRegistrationTypes;
    }

    public void setClientRegistrationTypes(List<ClientRegistrationType> regTypes) {
        this.clientRegistrationTypes = regTypes;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Object getCustomField(String name) {
        return this.customFields.get((Object)name);
    }

    public JSONObject getCustomFields() {
        return this.customFields;
    }

    public void setCustomField(String name, Object value) {
        this.customFields.put((Object)name, value);
    }

    public void setCustomFields(JSONObject customFields) {
        if (customFields == null) {
            throw new IllegalArgumentException("The custom fields JSON object must not be null");
        }
        this.customFields = customFields;
    }

    public void applyDefaults() {
        if (this.responseTypes == null) {
            this.responseTypes = new HashSet<ResponseType>();
            this.responseTypes.add(ResponseType.getDefault());
        }
        if (this.grantTypes == null) {
            this.grantTypes = new HashSet<GrantType>();
            this.grantTypes.add(GrantType.AUTHORIZATION_CODE);
        }
        if (this.authMethod == null) {
            this.authMethod = this.grantTypes.contains(GrantType.IMPLICIT) && this.grantTypes.size() == 1 ? ClientAuthenticationMethod.NONE : ClientAuthenticationMethod.getDefault();
        }
        if (this.authzJWEAlg != null && this.authzJWEEnc == null) {
            this.authzJWEEnc = EncryptionMethod.A128CBC_HS256;
        }
    }

    public JSONObject toJSONObject() {
        return this.toJSONObject(true);
    }

    public JSONObject toJSONObject(boolean includeCustomFields) {
        URI uri;
        LangTag langTag;
        this.ensureExactlyOneCertSubjectFieldForTLSClientAuth();
        JSONObject o = includeCustomFields ? new JSONObject((Map)this.customFields) : new JSONObject();
        if (this.redirectURIs != null) {
            o.put((Object)"redirect_uris", URIUtils.toStringList(this.redirectURIs));
        }
        if (this.scope != null) {
            o.put((Object)"scope", (Object)this.scope.toString());
        }
        if (this.responseTypes != null) {
            JSONArray rtList = new JSONArray();
            for (ResponseType rt : this.responseTypes) {
                rtList.add((Object)rt.toString());
            }
            o.put((Object)"response_types", (Object)rtList);
        }
        if (this.grantTypes != null) {
            JSONArray grantList = new JSONArray();
            for (GrantType grant : this.grantTypes) {
                grantList.add((Object)grant.toString());
            }
            o.put((Object)"grant_types", (Object)grantList);
        }
        if (this.contacts != null) {
            o.put((Object)"contacts", this.contacts);
        }
        if (!this.nameEntries.isEmpty()) {
            for (Map.Entry<LangTag, String> entry : this.nameEntries.entrySet()) {
                langTag = entry.getKey();
                String name = entry.getValue();
                if (name == null) continue;
                if (langTag == null) {
                    o.put((Object)"client_name", (Object)entry.getValue());
                    continue;
                }
                o.put((Object)("client_name#" + langTag), (Object)entry.getValue());
            }
        }
        if (!this.logoURIEntries.isEmpty()) {
            for (Map.Entry<LangTag, Object> entry : this.logoURIEntries.entrySet()) {
                langTag = entry.getKey();
                uri = (URI)entry.getValue();
                if (uri == null) continue;
                if (langTag == null) {
                    o.put((Object)"logo_uri", (Object)((URI)entry.getValue()).toString());
                    continue;
                }
                o.put((Object)("logo_uri#" + langTag), (Object)((URI)entry.getValue()).toString());
            }
        }
        if (!this.uriEntries.isEmpty()) {
            for (Map.Entry<LangTag, Object> entry : this.uriEntries.entrySet()) {
                langTag = entry.getKey();
                uri = (URI)entry.getValue();
                if (uri == null) continue;
                if (langTag == null) {
                    o.put((Object)"client_uri", (Object)((URI)entry.getValue()).toString());
                    continue;
                }
                o.put((Object)("client_uri#" + langTag), (Object)((URI)entry.getValue()).toString());
            }
        }
        if (!this.policyURIEntries.isEmpty()) {
            for (Map.Entry<LangTag, Object> entry : this.policyURIEntries.entrySet()) {
                langTag = entry.getKey();
                uri = (URI)entry.getValue();
                if (uri == null) continue;
                if (langTag == null) {
                    o.put((Object)"policy_uri", (Object)((URI)entry.getValue()).toString());
                    continue;
                }
                o.put((Object)("policy_uri#" + langTag), (Object)((URI)entry.getValue()).toString());
            }
        }
        if (!this.tosURIEntries.isEmpty()) {
            for (Map.Entry<LangTag, Object> entry : this.tosURIEntries.entrySet()) {
                langTag = entry.getKey();
                uri = (URI)entry.getValue();
                if (uri == null) continue;
                if (langTag == null) {
                    o.put((Object)"tos_uri", (Object)((URI)entry.getValue()).toString());
                    continue;
                }
                o.put((Object)("tos_uri#" + langTag), (Object)((URI)entry.getValue()).toString());
            }
        }
        if (this.authMethod != null) {
            o.put((Object)"token_endpoint_auth_method", (Object)this.authMethod.toString());
        }
        if (this.authJWSAlg != null) {
            o.put((Object)"token_endpoint_auth_signing_alg", (Object)this.authJWSAlg.getName());
        }
        if (this.jwkSetURI != null) {
            o.put((Object)"jwks_uri", (Object)this.jwkSetURI.toString());
        }
        if (this.jwkSet != null) {
            o.put((Object)"jwks", (Object)JSONObjectUtils.toJSONObject(this.jwkSet.toPublicJWKSet()));
        }
        if (this.requestObjectURIs != null) {
            o.put((Object)"request_uris", URIUtils.toStringList(this.requestObjectURIs));
        }
        if (this.requestObjectJWSAlg != null) {
            o.put((Object)"request_object_signing_alg", (Object)this.requestObjectJWSAlg.getName());
        }
        if (this.requestObjectJWEAlg != null) {
            o.put((Object)"request_object_encryption_alg", (Object)this.requestObjectJWEAlg.getName());
        }
        if (this.requestObjectJWEEnc != null) {
            o.put((Object)"request_object_encryption_enc", (Object)this.requestObjectJWEEnc.getName());
        }
        if (this.softwareID != null) {
            o.put((Object)"software_id", (Object)this.softwareID.getValue());
        }
        if (this.softwareVersion != null) {
            o.put((Object)"software_version", (Object)this.softwareVersion.getValue());
        }
        if (this.softwareStatement != null) {
            o.put((Object)"software_statement", (Object)this.softwareStatement.serialize());
        }
        if (this.getTLSClientCertificateBoundAccessTokens()) {
            o.put((Object)"tls_client_certificate_bound_access_tokens", (Object)this.tlsClientCertificateBoundAccessTokens);
        }
        if (this.tlsClientAuthSubjectDN != null) {
            o.put((Object)"tls_client_auth_subject_dn", (Object)this.tlsClientAuthSubjectDN);
        }
        if (this.tlsClientAuthSanDNS != null) {
            o.put((Object)"tls_client_auth_san_dns", (Object)this.tlsClientAuthSanDNS);
        }
        if (this.tlsClientAuthSanURI != null) {
            o.put((Object)"tls_client_auth_san_uri", (Object)this.tlsClientAuthSanURI);
        }
        if (this.tlsClientAuthSanIP != null) {
            o.put((Object)"tls_client_auth_san_ip", (Object)this.tlsClientAuthSanIP);
        }
        if (this.tlsClientAuthSanEmail != null) {
            o.put((Object)"tls_client_auth_san_email", (Object)this.tlsClientAuthSanEmail);
        }
        if (this.authzJWSAlg != null) {
            o.put((Object)"authorization_signed_response_alg", (Object)this.authzJWSAlg.getName());
        }
        if (this.authzJWEAlg != null) {
            o.put((Object)"authorization_encrypted_response_alg", (Object)this.authzJWEAlg.getName());
        }
        if (this.authzJWEEnc != null) {
            o.put((Object)"authorization_encrypted_response_enc", (Object)this.authzJWEEnc.getName());
        }
        if (this.requirePAR) {
            o.put((Object)"require_pushed_authorization_requests", (Object)true);
        }
        if (this.backChannelTokenDeliveryMode != null) {
            o.put((Object)"backchannel_token_delivery_mode", (Object)this.backChannelTokenDeliveryMode.getValue());
        }
        if (this.backChannelClientNotificationEndpoint != null) {
            o.put((Object)"backchannel_client_notification_endpoint", (Object)this.backChannelClientNotificationEndpoint.toString());
        }
        if (this.backChannelAuthRequestJWSAlg != null) {
            o.put((Object)"backchannel_authentication_request_signing_alg", (Object)this.backChannelAuthRequestJWSAlg.getName());
        }
        if (this.backChannelUserCodeParam) {
            o.put((Object)"backchannel_user_code_parameter", (Object)true);
        }
        if (CollectionUtils.isNotEmpty(this.clientRegistrationTypes)) {
            o.put((Object)"client_registration_types", Identifier.toStringList(this.clientRegistrationTypes));
            o.put((Object)"federation_type", Identifier.toStringList(this.clientRegistrationTypes));
        }
        if (this.organizationName != null) {
            o.put((Object)"organization_name", (Object)this.organizationName);
        }
        return o;
    }

    public String toString() {
        return this.toJSONObject().toJSONString();
    }

    public static ClientMetadata parse(JSONObject jsonObject) throws ParseException {
        return ClientMetadata.parseFromModifiableJSONObject(new JSONObject((Map)jsonObject));
    }

    private static ClientMetadata parseFromModifiableJSONObject(JSONObject jsonObject) throws ParseException {
        ClientMetadata metadata = new ClientMetadata();
        if (jsonObject.get((Object)"redirect_uris") != null) {
            LinkedHashSet<URI> redirectURIs = new LinkedHashSet<URI>();
            String[] stringArray = JSONObjectUtils.getStringArray(jsonObject, "redirect_uris");
            int n = stringArray.length;
            for (int i = 0; i < n; ++i) {
                URI uri;
                String uriString = stringArray[i];
                try {
                    uri = new URI(uriString);
                }
                catch (URISyntaxException e) {
                    throw new ParseException("Invalid redirect_uris parameter: " + e.getMessage(), RegistrationError.INVALID_REDIRECT_URI.appendDescription(": " + e.getMessage()));
                }
                redirectURIs.add(uri);
            }
            try {
                metadata.setRedirectionURIs(redirectURIs);
            }
            catch (IllegalArgumentException e) {
                throw new ParseException("Invalid redirect_uris parameter: " + e.getMessage(), RegistrationError.INVALID_REDIRECT_URI.appendDescription(": " + e.getMessage()));
            }
            jsonObject.remove((Object)"redirect_uris");
        }
        try {
            LinkedList<ClientRegistrationType> types;
            if (jsonObject.get((Object)"scope") != null) {
                metadata.setScope(Scope.parse(JSONObjectUtils.getString(jsonObject, "scope")));
                jsonObject.remove((Object)"scope");
            }
            if (jsonObject.get((Object)"response_types") != null) {
                LinkedHashSet<ResponseType> responseTypes = new LinkedHashSet<ResponseType>();
                for (String rt : JSONObjectUtils.getStringArray(jsonObject, "response_types")) {
                    responseTypes.add(ResponseType.parse(rt));
                }
                metadata.setResponseTypes(responseTypes);
                jsonObject.remove((Object)"response_types");
            }
            if (jsonObject.get((Object)"grant_types") != null) {
                LinkedHashSet<GrantType> grantTypes = new LinkedHashSet<GrantType>();
                for (String grant : JSONObjectUtils.getStringArray(jsonObject, "grant_types")) {
                    grantTypes.add(GrantType.parse(grant));
                }
                metadata.setGrantTypes(grantTypes);
                jsonObject.remove((Object)"grant_types");
            }
            if (jsonObject.get((Object)"contacts") != null) {
                metadata.setEmailContacts(JSONObjectUtils.getStringList(jsonObject, "contacts"));
                jsonObject.remove((Object)"contacts");
            }
            Map matches = LangTagUtils.find((String)"client_name", (Map)jsonObject);
            for (Map.Entry entry : matches.entrySet()) {
                try {
                    metadata.setName((String)entry.getValue(), (LangTag)entry.getKey());
                }
                catch (ClassCastException e) {
                    throw new ParseException("Invalid client_name (language tag) parameter");
                }
                ClientMetadata.removeMember(jsonObject, "client_name", (LangTag)entry.getKey());
            }
            matches = LangTagUtils.find((String)"logo_uri", (Map)jsonObject);
            for (Map.Entry entry : matches.entrySet()) {
                if (entry.getValue() == null) continue;
                try {
                    metadata.setLogoURI(new URI((String)entry.getValue()), (LangTag)entry.getKey());
                }
                catch (Exception e) {
                    throw new ParseException("Invalid logo_uri (language tag) parameter");
                }
                ClientMetadata.removeMember(jsonObject, "logo_uri", (LangTag)entry.getKey());
            }
            matches = LangTagUtils.find((String)"client_uri", (Map)jsonObject);
            for (Map.Entry entry : matches.entrySet()) {
                if (entry.getValue() == null) continue;
                try {
                    metadata.setURI(new URI((String)entry.getValue()), (LangTag)entry.getKey());
                }
                catch (Exception e) {
                    throw new ParseException("Invalid client_uri (language tag) parameter: " + e.getMessage());
                }
                ClientMetadata.removeMember(jsonObject, "client_uri", (LangTag)entry.getKey());
            }
            matches = LangTagUtils.find((String)"policy_uri", (Map)jsonObject);
            for (Map.Entry entry : matches.entrySet()) {
                if (entry.getValue() == null) continue;
                try {
                    metadata.setPolicyURI(new URI((String)entry.getValue()), (LangTag)entry.getKey());
                }
                catch (Exception e) {
                    throw new ParseException("Invalid policy_uri (language tag) parameter: " + e.getMessage());
                }
                ClientMetadata.removeMember(jsonObject, "policy_uri", (LangTag)entry.getKey());
            }
            matches = LangTagUtils.find((String)"tos_uri", (Map)jsonObject);
            for (Map.Entry entry : matches.entrySet()) {
                if (entry.getValue() == null) continue;
                try {
                    metadata.setTermsOfServiceURI(new URI((String)entry.getValue()), (LangTag)entry.getKey());
                }
                catch (Exception e) {
                    throw new ParseException("Invalid tos_uri (language tag) parameter: " + e.getMessage());
                }
                ClientMetadata.removeMember(jsonObject, "tos_uri", (LangTag)entry.getKey());
            }
            if (jsonObject.get((Object)"token_endpoint_auth_method") != null) {
                metadata.setTokenEndpointAuthMethod(ClientAuthenticationMethod.parse(JSONObjectUtils.getString(jsonObject, "token_endpoint_auth_method")));
                jsonObject.remove((Object)"token_endpoint_auth_method");
            }
            if (jsonObject.get((Object)"token_endpoint_auth_signing_alg") != null) {
                metadata.setTokenEndpointAuthJWSAlg(JWSAlgorithm.parse((String)JSONObjectUtils.getString(jsonObject, "token_endpoint_auth_signing_alg")));
                jsonObject.remove((Object)"token_endpoint_auth_signing_alg");
            }
            if (jsonObject.get((Object)"jwks_uri") != null) {
                metadata.setJWKSetURI(JSONObjectUtils.getURI(jsonObject, "jwks_uri"));
                jsonObject.remove((Object)"jwks_uri");
            }
            if (jsonObject.get((Object)"jwks") != null) {
                try {
                    metadata.setJWKSet(JWKSet.parse((Map)JSONObjectUtils.getJSONObject(jsonObject, "jwks")));
                }
                catch (java.text.ParseException e) {
                    throw new ParseException(e.getMessage(), e);
                }
                jsonObject.remove((Object)"jwks");
            }
            if (jsonObject.get((Object)"request_uris") != null) {
                LinkedHashSet<URI> requestURIs = new LinkedHashSet<URI>();
                for (String uriString : JSONObjectUtils.getStringArray(jsonObject, "request_uris")) {
                    try {
                        requestURIs.add(new URI(uriString));
                    }
                    catch (URISyntaxException e) {
                        throw new ParseException("Invalid request_uris parameter");
                    }
                }
                metadata.setRequestObjectURIs(requestURIs);
                jsonObject.remove((Object)"request_uris");
            }
            if (jsonObject.get((Object)"request_object_signing_alg") != null) {
                metadata.setRequestObjectJWSAlg(JWSAlgorithm.parse((String)JSONObjectUtils.getString(jsonObject, "request_object_signing_alg")));
                jsonObject.remove((Object)"request_object_signing_alg");
            }
            if (jsonObject.get((Object)"request_object_encryption_alg") != null) {
                metadata.setRequestObjectJWEAlg(JWEAlgorithm.parse((String)JSONObjectUtils.getString(jsonObject, "request_object_encryption_alg")));
                jsonObject.remove((Object)"request_object_encryption_alg");
            }
            if (jsonObject.get((Object)"request_object_encryption_enc") != null) {
                metadata.setRequestObjectJWEEnc(EncryptionMethod.parse((String)JSONObjectUtils.getString(jsonObject, "request_object_encryption_enc")));
                jsonObject.remove((Object)"request_object_encryption_enc");
            }
            if (jsonObject.get((Object)"software_id") != null) {
                metadata.setSoftwareID(new SoftwareID(JSONObjectUtils.getString(jsonObject, "software_id")));
                jsonObject.remove((Object)"software_id");
            }
            if (jsonObject.get((Object)"software_version") != null) {
                metadata.setSoftwareVersion(new SoftwareVersion(jsonObject.get((Object)"software_version").toString()));
                jsonObject.remove((Object)"software_version");
            }
            if (jsonObject.get((Object)"software_statement") != null) {
                try {
                    metadata.setSoftwareStatement(SignedJWT.parse((String)JSONObjectUtils.getString(jsonObject, "software_statement")));
                }
                catch (java.text.ParseException e) {
                    throw new ParseException("Invalid software_statement JWT: " + e.getMessage());
                }
                jsonObject.remove((Object)"software_statement");
            }
            if (jsonObject.get((Object)"tls_client_certificate_bound_access_tokens") != null) {
                metadata.setTLSClientCertificateBoundAccessTokens(JSONObjectUtils.getBoolean(jsonObject, "tls_client_certificate_bound_access_tokens"));
                jsonObject.remove((Object)"tls_client_certificate_bound_access_tokens");
            }
            if (jsonObject.get((Object)"tls_client_auth_subject_dn") != null) {
                metadata.setTLSClientAuthSubjectDN(JSONObjectUtils.getString(jsonObject, "tls_client_auth_subject_dn"));
                jsonObject.remove((Object)"tls_client_auth_subject_dn");
            }
            if (jsonObject.get((Object)"tls_client_auth_san_dns") != null) {
                metadata.setTLSClientAuthSanDNS(JSONObjectUtils.getString(jsonObject, "tls_client_auth_san_dns"));
                jsonObject.remove((Object)"tls_client_auth_san_dns");
            }
            if (jsonObject.get((Object)"tls_client_auth_san_uri") != null) {
                metadata.setTLSClientAuthSanURI(JSONObjectUtils.getString(jsonObject, "tls_client_auth_san_uri"));
                jsonObject.remove((Object)"tls_client_auth_san_uri");
            }
            if (jsonObject.get((Object)"tls_client_auth_san_ip") != null) {
                metadata.setTLSClientAuthSanIP(JSONObjectUtils.getString(jsonObject, "tls_client_auth_san_ip"));
                jsonObject.remove((Object)"tls_client_auth_san_ip");
            }
            if (jsonObject.get((Object)"tls_client_auth_san_email") != null) {
                metadata.setTLSClientAuthSanEmail(JSONObjectUtils.getString(jsonObject, "tls_client_auth_san_email"));
                jsonObject.remove((Object)"tls_client_auth_san_email");
            }
            metadata.ensureExactlyOneCertSubjectFieldForTLSClientAuth();
            if (jsonObject.get((Object)"authorization_signed_response_alg") != null) {
                metadata.setAuthorizationJWSAlg(JWSAlgorithm.parse((String)JSONObjectUtils.getString(jsonObject, "authorization_signed_response_alg")));
                jsonObject.remove((Object)"authorization_signed_response_alg");
            }
            if (jsonObject.get((Object)"authorization_encrypted_response_alg") != null) {
                metadata.setAuthorizationJWEAlg(JWEAlgorithm.parse((String)JSONObjectUtils.getString(jsonObject, "authorization_encrypted_response_alg")));
                jsonObject.remove((Object)"authorization_encrypted_response_alg");
            }
            if (jsonObject.get((Object)"authorization_encrypted_response_enc") != null) {
                metadata.setAuthorizationJWEEnc(EncryptionMethod.parse((String)JSONObjectUtils.getString(jsonObject, "authorization_encrypted_response_enc")));
                jsonObject.remove((Object)"authorization_encrypted_response_enc");
            }
            if (jsonObject.get((Object)"require_pushed_authorization_requests") != null) {
                metadata.requiresPushedAuthorizationRequests(JSONObjectUtils.getBoolean(jsonObject, "require_pushed_authorization_requests"));
                jsonObject.remove((Object)"require_pushed_authorization_requests");
            }
            if (jsonObject.get((Object)"backchannel_token_delivery_mode") != null) {
                metadata.setBackChannelTokenDeliveryMode(BackChannelTokenDeliveryMode.parse(JSONObjectUtils.getString(jsonObject, "backchannel_token_delivery_mode")));
                jsonObject.remove((Object)"backchannel_token_delivery_mode");
            }
            if (jsonObject.get((Object)"backchannel_client_notification_endpoint") != null) {
                metadata.setBackChannelClientNotificationEndpoint(JSONObjectUtils.getURI(jsonObject, "backchannel_client_notification_endpoint"));
                jsonObject.remove((Object)"backchannel_client_notification_endpoint");
            }
            if (jsonObject.get((Object)"backchannel_authentication_request_signing_alg") != null) {
                metadata.setBackChannelAuthRequestJWSAlg(JWSAlgorithm.parse((String)JSONObjectUtils.getString(jsonObject, "backchannel_authentication_request_signing_alg")));
                jsonObject.remove((Object)"backchannel_authentication_request_signing_alg");
            }
            if (jsonObject.get((Object)"backchannel_user_code_parameter") != null) {
                metadata.setSupportsBackChannelUserCodeParam(JSONObjectUtils.getBoolean(jsonObject, "backchannel_user_code_parameter"));
                jsonObject.remove((Object)"backchannel_user_code_parameter");
            }
            if (jsonObject.get((Object)"client_registration_types") != null) {
                types = new LinkedList<ClientRegistrationType>();
                for (String v : JSONObjectUtils.getStringList(jsonObject, "client_registration_types")) {
                    types.add(new ClientRegistrationType(v));
                }
                metadata.setClientRegistrationTypes(types);
                jsonObject.remove((Object)"client_registration_types");
            } else if (jsonObject.get((Object)"federation_type") != null) {
                types = new LinkedList();
                for (String v : JSONObjectUtils.getStringList(jsonObject, "federation_type")) {
                    types.add(new ClientRegistrationType(v));
                }
                metadata.setClientRegistrationTypes(types);
                jsonObject.remove((Object)"federation_type");
            }
            if (jsonObject.get((Object)"organization_name") != null) {
                metadata.setOrganizationName(JSONObjectUtils.getString(jsonObject, "organization_name"));
                jsonObject.remove((Object)"organization_name");
            }
        }
        catch (ParseException | IllegalStateException e) {
            throw new ParseException(e.getMessage(), RegistrationError.INVALID_CLIENT_METADATA.appendDescription(": " + e.getMessage()), e.getCause());
        }
        metadata.customFields = jsonObject;
        return metadata;
    }

    private static void removeMember(JSONObject jsonObject, String name, LangTag langTag) {
        if (langTag == null) {
            jsonObject.remove((Object)name);
        } else {
            jsonObject.remove((Object)(name + "#" + langTag));
        }
    }

    static {
        PROHIBITED_REDIRECT_URI_SCHEMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("data", "javascript", "vbscript")));
        HashSet<String> p = new HashSet<String>();
        p.add("redirect_uris");
        p.add("scope");
        p.add("response_types");
        p.add("grant_types");
        p.add("contacts");
        p.add("client_name");
        p.add("logo_uri");
        p.add("client_uri");
        p.add("policy_uri");
        p.add("tos_uri");
        p.add("token_endpoint_auth_method");
        p.add("token_endpoint_auth_signing_alg");
        p.add("jwks_uri");
        p.add("jwks");
        p.add("request_uris");
        p.add("request_object_signing_alg");
        p.add("request_object_encryption_alg");
        p.add("request_object_encryption_enc");
        p.add("require_pushed_authorization_requests");
        p.add("software_id");
        p.add("software_version");
        p.add("software_statement");
        p.add("tls_client_certificate_bound_access_tokens");
        p.add("tls_client_auth_subject_dn");
        p.add("tls_client_auth_san_dns");
        p.add("tls_client_auth_san_uri");
        p.add("tls_client_auth_san_ip");
        p.add("tls_client_auth_san_email");
        p.add("authorization_signed_response_alg");
        p.add("authorization_encrypted_response_alg");
        p.add("authorization_encrypted_response_enc");
        p.add("backchannel_token_delivery_mode");
        p.add("backchannel_client_notification_endpoint");
        p.add("backchannel_authentication_request_signing_alg");
        p.add("backchannel_user_code_parameter");
        p.add("client_registration_types");
        p.add("organization_name");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }
}

