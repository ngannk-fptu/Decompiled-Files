/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  com.nimbusds.jose.JWSObject$State
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.JWTParser
 *  com.nimbusds.jwt.SignedJWT
 *  com.nimbusds.langtag.LangTag
 *  com.nimbusds.langtag.LangTagException
 *  com.nimbusds.langtag.LangTagUtils
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.langtag.LangTag;
import com.nimbusds.langtag.LangTagException;
import com.nimbusds.langtag.LangTagUtils;
import com.nimbusds.oauth2.sdk.AbstractAuthenticatedRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.ciba.CIBAHintType;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JWTClaimsSetUtils;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.ResourceUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import com.nimbusds.openid.connect.sdk.OIDCClaimsRequest;
import com.nimbusds.openid.connect.sdk.claims.ACR;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import net.jcip.annotations.Immutable;

@Immutable
public class CIBARequest
extends AbstractAuthenticatedRequest {
    public static final int CLIENT_NOTIFICATION_TOKEN_MAX_LENGTH = 1024;
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private final Scope scope;
    private final BearerAccessToken clientNotificationToken;
    private final List<ACR> acrValues;
    private final String loginHintTokenString;
    private final JWT idTokenHint;
    private final String loginHint;
    private final String bindingMessage;
    private final Secret userCode;
    private final Integer requestedExpiry;
    private final OIDCClaimsRequest claims;
    private final List<LangTag> claimsLocales;
    private final String purpose;
    private final List<URI> resources;
    private final Map<String, List<String>> customParams;
    private final SignedJWT signedRequest;

    @Deprecated
    public CIBARequest(URI uri, ClientAuthentication clientAuth, Scope scope, BearerAccessToken clientNotificationToken, List<ACR> acrValues, String loginHintTokenString, JWT idTokenHint, String loginHint, String bindingMessage, Secret userCode, Integer requestedExpiry, Map<String, List<String>> customParams) {
        this(uri, clientAuth, scope, clientNotificationToken, acrValues, loginHintTokenString, idTokenHint, loginHint, bindingMessage, userCode, requestedExpiry, null, customParams);
    }

    @Deprecated
    public CIBARequest(URI uri, ClientAuthentication clientAuth, Scope scope, BearerAccessToken clientNotificationToken, List<ACR> acrValues, String loginHintTokenString, JWT idTokenHint, String loginHint, String bindingMessage, Secret userCode, Integer requestedExpiry, OIDCClaimsRequest claims, Map<String, List<String>> customParams) {
        this(uri, clientAuth, scope, clientNotificationToken, acrValues, loginHintTokenString, idTokenHint, loginHint, bindingMessage, userCode, requestedExpiry, claims, null, null, null, customParams);
    }

    public CIBARequest(URI uri, ClientAuthentication clientAuth, Scope scope, BearerAccessToken clientNotificationToken, List<ACR> acrValues, String loginHintTokenString, JWT idTokenHint, String loginHint, String bindingMessage, Secret userCode, Integer requestedExpiry, OIDCClaimsRequest claims, List<LangTag> claimsLocales, String purpose, List<URI> resources, Map<String, List<String>> customParams) {
        super(uri, clientAuth);
        this.scope = scope;
        if (clientNotificationToken != null && clientNotificationToken.getValue().length() > 1024) {
            throw new IllegalArgumentException("The client notification token must not exceed 1024 chars");
        }
        this.clientNotificationToken = clientNotificationToken;
        this.acrValues = acrValues;
        int numHints = 0;
        if (loginHintTokenString != null) {
            ++numHints;
        }
        this.loginHintTokenString = loginHintTokenString;
        if (idTokenHint != null) {
            ++numHints;
        }
        this.idTokenHint = idTokenHint;
        if (loginHint != null) {
            ++numHints;
        }
        this.loginHint = loginHint;
        if (numHints != 1) {
            throw new IllegalArgumentException("One user identity hist must be provided (login_hint_token, id_token_hint or login_hint)");
        }
        this.bindingMessage = bindingMessage;
        this.userCode = userCode;
        if (requestedExpiry != null && requestedExpiry < 1) {
            throw new IllegalArgumentException("The requested expiry must be a positive integer");
        }
        this.requestedExpiry = requestedExpiry;
        this.claims = claims;
        this.claimsLocales = claimsLocales != null ? Collections.unmodifiableList(claimsLocales) : null;
        this.purpose = purpose;
        this.resources = ResourceUtils.ensureLegalResourceURIs(resources);
        this.customParams = customParams != null ? customParams : Collections.emptyMap();
        this.signedRequest = null;
    }

    public CIBARequest(URI uri, ClientAuthentication clientAuth, SignedJWT signedRequest) {
        super(uri, clientAuth);
        if (signedRequest == null) {
            throw new IllegalArgumentException("The signed request JWT must not be null");
        }
        if (JWSObject.State.UNSIGNED.equals((Object)signedRequest.getState())) {
            throw new IllegalArgumentException("The request JWT must be in a signed state");
        }
        this.signedRequest = signedRequest;
        this.scope = null;
        this.clientNotificationToken = null;
        this.acrValues = null;
        this.loginHintTokenString = null;
        this.idTokenHint = null;
        this.loginHint = null;
        this.bindingMessage = null;
        this.userCode = null;
        this.requestedExpiry = null;
        this.claims = null;
        this.claimsLocales = null;
        this.purpose = null;
        this.resources = null;
        this.customParams = Collections.emptyMap();
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public Scope getScope() {
        return this.scope;
    }

    public BearerAccessToken getClientNotificationToken() {
        return this.clientNotificationToken;
    }

    public List<ACR> getACRValues() {
        return this.acrValues;
    }

    public CIBAHintType getHintType() {
        if (this.getLoginHintTokenString() != null) {
            return CIBAHintType.LOGIN_HINT_TOKEN;
        }
        if (this.getIDTokenHint() != null) {
            return CIBAHintType.ID_TOKEN_HINT;
        }
        return CIBAHintType.LOGIN_HINT;
    }

    public String getLoginHintTokenString() {
        return this.loginHintTokenString;
    }

    public JWT getIDTokenHint() {
        return this.idTokenHint;
    }

    public String getLoginHint() {
        return this.loginHint;
    }

    public String getBindingMessage() {
        return this.bindingMessage;
    }

    public Secret getUserCode() {
        return this.userCode;
    }

    public Integer getRequestedExpiry() {
        return this.requestedExpiry;
    }

    public OIDCClaimsRequest getOIDCClaims() {
        return this.claims;
    }

    public List<LangTag> getClaimsLocales() {
        return this.claimsLocales;
    }

    public String getPurpose() {
        return this.purpose;
    }

    public List<URI> getResources() {
        return this.resources;
    }

    public Map<String, List<String>> getCustomParameters() {
        return this.customParams;
    }

    public List<String> getCustomParameter(String name) {
        return this.customParams.get(name);
    }

    public boolean isSigned() {
        return this.signedRequest != null;
    }

    public SignedJWT getRequestJWT() {
        return this.signedRequest;
    }

    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>(this.getCustomParameters());
        if (this.isSigned()) {
            params.put("request", Collections.singletonList(this.signedRequest.serialize()));
            return params;
        }
        if (CollectionUtils.isNotEmpty(this.getScope())) {
            params.put("scope", Collections.singletonList(this.getScope().toString()));
        }
        if (this.getClientNotificationToken() != null) {
            params.put("client_notification_token", Collections.singletonList(this.getClientNotificationToken().getValue()));
        }
        if (this.getACRValues() != null) {
            params.put("acr_values", Identifier.toStringList(this.getACRValues()));
        }
        if (this.getLoginHintTokenString() != null) {
            params.put("login_hint_token", Collections.singletonList(this.getLoginHintTokenString()));
        }
        if (this.getIDTokenHint() != null) {
            params.put("id_token_hint", Collections.singletonList(this.getIDTokenHint().serialize()));
        }
        if (this.getLoginHint() != null) {
            params.put("login_hint", Collections.singletonList(this.getLoginHint()));
        }
        if (this.getBindingMessage() != null) {
            params.put("binding_message", Collections.singletonList(this.getBindingMessage()));
        }
        if (this.getUserCode() != null) {
            params.put("user_code", Collections.singletonList(this.getUserCode().getValue()));
        }
        if (this.getRequestedExpiry() != null) {
            params.put("requested_expiry", Collections.singletonList(this.getRequestedExpiry().toString()));
        }
        if (this.getOIDCClaims() != null) {
            params.put("claims", Collections.singletonList(this.getOIDCClaims().toJSONString()));
        }
        if (CollectionUtils.isNotEmpty(this.getClaimsLocales())) {
            params.put("claims_locales", Collections.singletonList(LangTagUtils.concat(this.getClaimsLocales())));
        }
        if (this.getPurpose() != null) {
            params.put("purpose", Collections.singletonList(this.purpose));
        }
        if (CollectionUtils.isNotEmpty(this.getResources())) {
            params.put("resource", URIUtils.toStringList(this.getResources(), true));
        }
        return params;
    }

    public JWTClaimsSet toJWTClaimsSet() {
        if (this.isSigned()) {
            throw new IllegalStateException();
        }
        return JWTClaimsSetUtils.toJWTClaimsSet(this.toParameters());
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        httpRequest.setEntityContentType(ContentType.APPLICATION_URLENCODED);
        this.getClientAuthentication().applyTo(httpRequest);
        Map<String, List<String>> params = httpRequest.getQueryParameters();
        params.putAll(this.toParameters());
        httpRequest.setQuery(URLUtils.serializeParameters(params));
        return httpRequest;
    }

    public static CIBARequest parse(HTTPRequest httpRequest) throws ParseException {
        URI uri = httpRequest.getURI();
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_URLENCODED);
        ClientAuthentication clientAuth = ClientAuthentication.parse(httpRequest);
        if (clientAuth == null) {
            throw new ParseException("Missing required client authentication");
        }
        Map<String, List<String>> params = httpRequest.getQueryParameters();
        if (params.containsKey("request")) {
            SignedJWT signedRequest;
            String v = MultivaluedMapUtils.getFirstValue(params, "request");
            if (StringUtils.isBlank(v)) {
                throw new ParseException("Empty request parameter");
            }
            try {
                signedRequest = SignedJWT.parse((String)v);
            }
            catch (java.text.ParseException e) {
                throw new ParseException("Invalid request JWT: " + e.getMessage(), e);
            }
            try {
                return new CIBARequest(uri, clientAuth, signedRequest);
            }
            catch (IllegalArgumentException e) {
                throw new ParseException(e.getMessage(), e);
            }
        }
        String v = MultivaluedMapUtils.getFirstValue(params, "scope");
        Scope scope = Scope.parse(v);
        v = MultivaluedMapUtils.getFirstValue(params, "client_notification_token");
        BearerAccessToken clientNotificationToken = null;
        if (StringUtils.isNotBlank(v)) {
            clientNotificationToken = new BearerAccessToken(v);
        }
        v = MultivaluedMapUtils.getFirstValue(params, "acr_values");
        LinkedList<ACR> acrValues = null;
        if (StringUtils.isNotBlank(v)) {
            acrValues = new LinkedList<ACR>();
            StringTokenizer st = new StringTokenizer(v, " ");
            while (st.hasMoreTokens()) {
                acrValues.add(new ACR(st.nextToken()));
            }
        }
        String loginHintTokenString = MultivaluedMapUtils.getFirstValue(params, "login_hint_token");
        v = MultivaluedMapUtils.getFirstValue(params, "id_token_hint");
        JWT idTokenHint = null;
        if (StringUtils.isNotBlank(v)) {
            try {
                idTokenHint = JWTParser.parse((String)v);
            }
            catch (java.text.ParseException e) {
                throw new ParseException("Invalid id_token_hint parameter: " + e.getMessage());
            }
        }
        String loginHint = MultivaluedMapUtils.getFirstValue(params, "login_hint");
        v = MultivaluedMapUtils.getFirstValue(params, "user_code");
        Secret userCode = null;
        if (StringUtils.isNotBlank(v)) {
            userCode = new Secret(v);
        }
        String bindingMessage = MultivaluedMapUtils.getFirstValue(params, "binding_message");
        v = MultivaluedMapUtils.getFirstValue(params, "requested_expiry");
        Integer requestedExpiry = null;
        if (StringUtils.isNotBlank(v)) {
            try {
                requestedExpiry = Integer.valueOf(v);
            }
            catch (NumberFormatException e) {
                throw new ParseException("The requested_expiry parameter must be an integer");
            }
        }
        v = MultivaluedMapUtils.getFirstValue(params, "claims");
        OIDCClaimsRequest claims = null;
        if (StringUtils.isNotBlank(v)) {
            try {
                claims = OIDCClaimsRequest.parse(v);
            }
            catch (ParseException e) {
                throw new ParseException("Invalid claims parameter: " + e.getMessage(), e);
            }
        }
        v = MultivaluedMapUtils.getFirstValue(params, "claims_locales");
        LinkedList<LangTag> claimsLocales = null;
        if (StringUtils.isNotBlank(v)) {
            claimsLocales = new LinkedList<LangTag>();
            StringTokenizer st = new StringTokenizer(v, " ");
            while (st.hasMoreTokens()) {
                try {
                    claimsLocales.add(LangTag.parse((String)st.nextToken()));
                }
                catch (LangTagException e) {
                    throw new ParseException("Invalid claims_locales parameter: " + e.getMessage(), e);
                }
            }
        }
        String purpose = MultivaluedMapUtils.getFirstValue(params, "purpose");
        List<URI> resources = ResourceUtils.parseResourceURIs(params.get("resource"));
        HashMap<String, List<String>> customParams = null;
        for (Map.Entry<String, List<String>> p : params.entrySet()) {
            if (REGISTERED_PARAMETER_NAMES.contains(p.getKey()) || clientAuth.getFormParameterNames().contains(p.getKey())) continue;
            if (customParams == null) {
                customParams = new HashMap<String, List<String>>();
            }
            customParams.put(p.getKey(), p.getValue());
        }
        try {
            return new CIBARequest(uri, clientAuth, scope, clientNotificationToken, acrValues, loginHintTokenString, idTokenHint, loginHint, bindingMessage, userCode, requestedExpiry, claims, claimsLocales, purpose, resources, customParams);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }

    static {
        HashSet<String> p = new HashSet<String>();
        p.add("scope");
        p.add("client_notification_token");
        p.add("acr_values");
        p.add("login_hint_token");
        p.add("id_token_hint");
        p.add("login_hint");
        p.add("binding_message");
        p.add("user_code");
        p.add("requested_expiry");
        p.add("claims");
        p.add("claims_locales");
        p.add("purpose");
        p.add("resource");
        p.add("request");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }

    public static class Builder {
        private URI uri;
        private final ClientAuthentication clientAuth;
        private final Scope scope;
        private BearerAccessToken clientNotificationToken;
        private List<ACR> acrValues;
        private String loginHintTokenString;
        private JWT idTokenHint;
        private String loginHint;
        private String bindingMessage;
        private Secret userCode;
        private Integer requestedExpiry;
        private OIDCClaimsRequest claims;
        private List<LangTag> claimsLocales;
        private String purpose;
        private List<URI> resources;
        private Map<String, List<String>> customParams = new HashMap<String, List<String>>();
        private final SignedJWT signedRequest;

        public Builder(ClientAuthentication clientAuth, Scope scope) {
            if (clientAuth == null) {
                throw new IllegalArgumentException("The client authentication must not be null");
            }
            this.clientAuth = clientAuth;
            this.scope = scope;
            this.signedRequest = null;
        }

        public Builder(ClientAuthentication clientAuth, SignedJWT signedRequest) {
            if (clientAuth == null) {
                throw new IllegalArgumentException("The client authentication must not be null");
            }
            this.clientAuth = clientAuth;
            if (signedRequest == null) {
                throw new IllegalArgumentException("The signed request JWT must not be null");
            }
            this.signedRequest = signedRequest;
            this.scope = null;
        }

        public Builder(CIBARequest request) {
            this.uri = request.getEndpointURI();
            this.clientAuth = request.getClientAuthentication();
            this.scope = request.getScope();
            this.clientNotificationToken = request.getClientNotificationToken();
            this.acrValues = request.getACRValues();
            this.loginHintTokenString = request.getLoginHintTokenString();
            this.idTokenHint = request.getIDTokenHint();
            this.loginHint = request.getLoginHint();
            this.bindingMessage = request.getBindingMessage();
            this.userCode = request.getUserCode();
            this.requestedExpiry = request.getRequestedExpiry();
            this.claims = request.getOIDCClaims();
            this.claimsLocales = request.getClaimsLocales();
            this.purpose = request.getPurpose();
            this.resources = request.getResources();
            this.customParams = request.getCustomParameters();
            this.signedRequest = request.getRequestJWT();
        }

        public Builder clientNotificationToken(BearerAccessToken token) {
            this.clientNotificationToken = token;
            return this;
        }

        public Builder acrValues(List<ACR> acrValues) {
            this.acrValues = acrValues;
            return this;
        }

        public Builder loginHintTokenString(String loginHintTokenString) {
            this.loginHintTokenString = loginHintTokenString;
            return this;
        }

        public Builder idTokenHint(JWT idTokenHint) {
            this.idTokenHint = idTokenHint;
            return this;
        }

        public Builder loginHint(String loginHint) {
            this.loginHint = loginHint;
            return this;
        }

        public Builder bindingMessage(String bindingMessage) {
            this.bindingMessage = bindingMessage;
            return this;
        }

        public Builder userCode(Secret userCode) {
            this.userCode = userCode;
            return this;
        }

        public Builder requestedExpiry(Integer requestedExpiry) {
            this.requestedExpiry = requestedExpiry;
            return this;
        }

        public Builder claims(OIDCClaimsRequest claims) {
            this.claims = claims;
            return this;
        }

        public Builder claimsLocales(List<LangTag> claimsLocales) {
            this.claimsLocales = claimsLocales;
            return this;
        }

        public Builder purpose(String purpose) {
            this.purpose = purpose;
            return this;
        }

        public Builder resource(URI resource) {
            this.resources = resource != null ? Collections.singletonList(resource) : null;
            return this;
        }

        public Builder resources(URI ... resources) {
            this.resources = resources != null ? Arrays.asList(resources) : null;
            return this;
        }

        public Builder customParameter(String name, String ... values) {
            if (values == null || values.length == 0) {
                this.customParams.remove(name);
            } else {
                this.customParams.put(name, Arrays.asList(values));
            }
            return this;
        }

        public Builder endpointURI(URI uri) {
            this.uri = uri;
            return this;
        }

        public CIBARequest build() {
            try {
                if (this.signedRequest != null) {
                    return new CIBARequest(this.uri, this.clientAuth, this.signedRequest);
                }
                return new CIBARequest(this.uri, this.clientAuth, this.scope, this.clientNotificationToken, this.acrValues, this.loginHintTokenString, this.idTokenHint, this.loginHint, this.bindingMessage, this.userCode, this.requestedExpiry, this.claims, this.claimsLocales, this.purpose, this.resources, this.customParams);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }
}

