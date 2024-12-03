/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.JWTClaimsSet$Builder
 *  com.nimbusds.jwt.JWTParser
 *  com.nimbusds.langtag.LangTag
 *  com.nimbusds.langtag.LangTagException
 *  com.nimbusds.langtag.LangTagUtils
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.langtag.LangTag;
import com.nimbusds.langtag.LangTagException;
import com.nimbusds.langtag.LangTagUtils;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallenge;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import com.nimbusds.openid.connect.sdk.ClaimsRequest;
import com.nimbusds.openid.connect.sdk.Display;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCClaimsRequest;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValidator;
import com.nimbusds.openid.connect.sdk.OIDCScopeValue;
import com.nimbusds.openid.connect.sdk.Prompt;
import com.nimbusds.openid.connect.sdk.claims.ACR;
import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import net.jcip.annotations.Immutable;

@Immutable
public class AuthenticationRequest
extends AuthorizationRequest {
    public static final int PURPOSE_MIN_LENGTH = 3;
    public static final int PURPOSE_MAX_LENGTH = 300;
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private final Nonce nonce;
    private final Display display;
    private final int maxAge;
    private final List<LangTag> uiLocales;
    private final List<LangTag> claimsLocales;
    private final JWT idTokenHint;
    private final String loginHint;
    private final List<ACR> acrValues;
    private final OIDCClaimsRequest claims;
    private final String purpose;

    public AuthenticationRequest(URI uri, ResponseType rt, Scope scope, ClientID clientID, URI redirectURI, State state, Nonce nonce) {
        this(uri, rt, null, scope, clientID, redirectURI, state, nonce, null, null, -1, null, null, null, null, null, (OIDCClaimsRequest)null, null, null, null, null, null, null, false, null);
    }

    @Deprecated
    public AuthenticationRequest(URI uri, ResponseType rt, ResponseMode rm, Scope scope, ClientID clientID, URI redirectURI, State state, Nonce nonce, Display display, Prompt prompt, int maxAge, List<LangTag> uiLocales, List<LangTag> claimsLocales, JWT idTokenHint, String loginHint, List<ACR> acrValues, ClaimsRequest claims, String purpose, JWT requestObject, URI requestURI, CodeChallenge codeChallenge, CodeChallengeMethod codeChallengeMethod, List<URI> resources, boolean includeGrantedScopes, Map<String, List<String>> customParams) {
        this(uri, rt, rm, scope, clientID, redirectURI, state, nonce, display, prompt, maxAge, uiLocales, claimsLocales, idTokenHint, loginHint, acrValues, AuthenticationRequest.toOIDCClaimsRequestWithSilentFail(claims), purpose, requestObject, requestURI, codeChallenge, codeChallengeMethod, resources, includeGrantedScopes, customParams);
    }

    public AuthenticationRequest(URI uri, ResponseType rt, ResponseMode rm, Scope scope, ClientID clientID, URI redirectURI, State state, Nonce nonce, Display display, Prompt prompt, int maxAge, List<LangTag> uiLocales, List<LangTag> claimsLocales, JWT idTokenHint, String loginHint, List<ACR> acrValues, OIDCClaimsRequest claims, String purpose, JWT requestObject, URI requestURI, CodeChallenge codeChallenge, CodeChallengeMethod codeChallengeMethod, List<URI> resources, boolean includeGrantedScopes, Map<String, List<String>> customParams) {
        super(uri, rt, rm, clientID, redirectURI, scope, state, codeChallenge, codeChallengeMethod, resources, includeGrantedScopes, requestObject, requestURI, prompt, customParams);
        if (!this.specifiesRequestObject()) {
            if (redirectURI == null) {
                throw new IllegalArgumentException("The redirection URI must not be null");
            }
            OIDCResponseTypeValidator.validate(rt);
            if (scope == null) {
                throw new IllegalArgumentException("The scope must not be null");
            }
            if (!scope.contains(OIDCScopeValue.OPENID)) {
                throw new IllegalArgumentException("The scope must include an \"openid\" value");
            }
            if (nonce == null && Nonce.isRequired(rt)) {
                throw new IllegalArgumentException("Nonce required for response_type=" + rt);
            }
        }
        this.nonce = nonce;
        this.display = display;
        this.maxAge = maxAge;
        this.uiLocales = uiLocales != null ? Collections.unmodifiableList(uiLocales) : null;
        this.claimsLocales = claimsLocales != null ? Collections.unmodifiableList(claimsLocales) : null;
        this.idTokenHint = idTokenHint;
        this.loginHint = loginHint;
        this.acrValues = acrValues != null ? Collections.unmodifiableList(acrValues) : null;
        this.claims = claims;
        if (purpose != null) {
            if (purpose.length() < 3) {
                throw new IllegalArgumentException("The purpose must not be shorter than 3 characters");
            }
            if (purpose.length() > 300) {
                throw new IllegalArgumentException("The purpose must not be longer than 300 characters");
            }
        }
        this.purpose = purpose;
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public Nonce getNonce() {
        return this.nonce;
    }

    public Display getDisplay() {
        return this.display;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public List<LangTag> getUILocales() {
        return this.uiLocales;
    }

    public List<LangTag> getClaimsLocales() {
        return this.claimsLocales;
    }

    public JWT getIDTokenHint() {
        return this.idTokenHint;
    }

    public String getLoginHint() {
        return this.loginHint;
    }

    public List<ACR> getACRValues() {
        return this.acrValues;
    }

    @Deprecated
    public ClaimsRequest getClaims() {
        return AuthenticationRequest.toClaimsRequestWithSilentFail(this.claims);
    }

    private static OIDCClaimsRequest toOIDCClaimsRequestWithSilentFail(ClaimsRequest claims) {
        if (claims == null) {
            return null;
        }
        try {
            return OIDCClaimsRequest.parse(claims.toJSONObject());
        }
        catch (com.nimbusds.oauth2.sdk.ParseException e) {
            return null;
        }
    }

    private static ClaimsRequest toClaimsRequestWithSilentFail(OIDCClaimsRequest claims) {
        if (claims == null) {
            return null;
        }
        try {
            return ClaimsRequest.parse(claims.toJSONObject());
        }
        catch (com.nimbusds.oauth2.sdk.ParseException e) {
            return null;
        }
    }

    public OIDCClaimsRequest getOIDCClaims() {
        return this.claims;
    }

    public String getPurpose() {
        return this.purpose;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        StringBuilder sb;
        Map<String, List<String>> params = super.toParameters();
        if (this.nonce != null) {
            params.put("nonce", Collections.singletonList(this.nonce.toString()));
        }
        if (this.display != null) {
            params.put("display", Collections.singletonList(this.display.toString()));
        }
        if (this.maxAge >= 0) {
            params.put("max_age", Collections.singletonList("" + this.maxAge));
        }
        if (this.uiLocales != null) {
            sb = new StringBuilder();
            for (LangTag locale : this.uiLocales) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(locale.toString());
            }
            params.put("ui_locales", Collections.singletonList(sb.toString()));
        }
        if (CollectionUtils.isNotEmpty(this.claimsLocales)) {
            params.put("claims_locales", Collections.singletonList(LangTagUtils.concat(this.claimsLocales)));
        }
        if (this.idTokenHint != null) {
            try {
                params.put("id_token_hint", Collections.singletonList(this.idTokenHint.serialize()));
            }
            catch (IllegalStateException e) {
                throw new SerializeException("Couldn't serialize ID token hint: " + e.getMessage(), e);
            }
        }
        if (this.loginHint != null) {
            params.put("login_hint", Collections.singletonList(this.loginHint));
        }
        if (this.acrValues != null) {
            sb = new StringBuilder();
            for (ACR acr : this.acrValues) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(acr.toString());
            }
            params.put("acr_values", Collections.singletonList(sb.toString()));
        }
        if (this.claims != null) {
            params.put("claims", Collections.singletonList(this.claims.toJSONObject().toString()));
        }
        if (this.purpose != null) {
            params.put("purpose", Collections.singletonList(this.purpose));
        }
        return params;
    }

    @Override
    public JWTClaimsSet toJWTClaimsSet() {
        JWTClaimsSet jwtClaimsSet = super.toJWTClaimsSet();
        if (jwtClaimsSet.getClaim("max_age") != null) {
            try {
                String maxAgeString = jwtClaimsSet.getStringClaim("max_age");
                JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder(jwtClaimsSet);
                builder.claim("max_age", (Object)Integer.parseInt(maxAgeString));
                return builder.build();
            }
            catch (ParseException e) {
                throw new SerializeException(e.getMessage());
            }
        }
        return jwtClaimsSet;
    }

    public static AuthenticationRequest parse(Map<String, List<String>> params) throws com.nimbusds.oauth2.sdk.ParseException {
        return AuthenticationRequest.parse(null, params);
    }

    public static AuthenticationRequest parse(URI uri, Map<String, List<String>> params) throws com.nimbusds.oauth2.sdk.ParseException {
        String purpose;
        AuthorizationRequest ar = AuthorizationRequest.parse(uri, params);
        Nonce nonce = Nonce.parse(MultivaluedMapUtils.getFirstValue(params, "nonce"));
        if (!ar.specifiesRequestObject()) {
            if (ar.getRedirectionURI() == null) {
                String msg = "Missing redirect_uri parameter";
                throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), ar.getClientID(), null, ar.impliedResponseMode(), ar.getState());
            }
            if (ar.getScope() == null) {
                String msg = "Missing scope parameter";
                throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), ar.getClientID(), ar.getRedirectionURI(), ar.impliedResponseMode(), ar.getState());
            }
            if (nonce == null && Nonce.isRequired(ar.getResponseType())) {
                String msg = "Missing nonce parameter: Required for response_type=" + ar.getResponseType();
                throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), ar.getClientID(), ar.getRedirectionURI(), ar.impliedResponseMode(), ar.getState());
            }
        }
        if (ar.getResponseType() != null) {
            try {
                OIDCResponseTypeValidator.validate(ar.getResponseType());
            }
            catch (IllegalArgumentException e) {
                String msg = "Unsupported response_type parameter: " + e.getMessage();
                throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.UNSUPPORTED_RESPONSE_TYPE.appendDescription(": " + msg), ar.getClientID(), ar.getRedirectionURI(), ar.impliedResponseMode(), ar.getState());
            }
        }
        if (ar.getScope() != null && !ar.getScope().contains(OIDCScopeValue.OPENID)) {
            String msg = "The scope must include an openid value";
            throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), ar.getClientID(), ar.getRedirectionURI(), ar.impliedResponseMode(), ar.getState());
        }
        Display display = null;
        if (params.containsKey("display")) {
            try {
                display = Display.parse(MultivaluedMapUtils.getFirstValue(params, "display"));
            }
            catch (com.nimbusds.oauth2.sdk.ParseException e) {
                String msg = "Invalid display parameter: " + e.getMessage();
                throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), ar.getClientID(), ar.getRedirectionURI(), ar.impliedResponseMode(), ar.getState(), e);
            }
        }
        String v = MultivaluedMapUtils.getFirstValue(params, "max_age");
        int maxAge = -1;
        if (StringUtils.isNotBlank(v)) {
            try {
                maxAge = Integer.parseInt(v);
            }
            catch (NumberFormatException e) {
                String msg = "Invalid max_age parameter: " + v;
                throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), ar.getClientID(), ar.getRedirectionURI(), ar.impliedResponseMode(), ar.getState(), e);
            }
        }
        v = MultivaluedMapUtils.getFirstValue(params, "ui_locales");
        LinkedList<LangTag> uiLocales = null;
        if (StringUtils.isNotBlank(v)) {
            uiLocales = new LinkedList<LangTag>();
            StringTokenizer st = new StringTokenizer(v, " ");
            while (st.hasMoreTokens()) {
                try {
                    uiLocales.add(LangTag.parse((String)st.nextToken()));
                }
                catch (LangTagException e) {
                    String msg = "Invalid ui_locales parameter: " + e.getMessage();
                    throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), ar.getClientID(), ar.getRedirectionURI(), ar.impliedResponseMode(), ar.getState(), e);
                }
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
                    String msg = "Invalid claims_locales parameter: " + e.getMessage();
                    throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), ar.getClientID(), ar.getRedirectionURI(), ar.impliedResponseMode(), ar.getState(), e);
                }
            }
        }
        v = MultivaluedMapUtils.getFirstValue(params, "id_token_hint");
        JWT idTokenHint = null;
        if (StringUtils.isNotBlank(v)) {
            try {
                idTokenHint = JWTParser.parse((String)v);
            }
            catch (ParseException e) {
                String msg = "Invalid id_token_hint parameter: " + e.getMessage();
                throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), ar.getClientID(), ar.getRedirectionURI(), ar.impliedResponseMode(), ar.getState(), e);
            }
        }
        String loginHint = MultivaluedMapUtils.getFirstValue(params, "login_hint");
        v = MultivaluedMapUtils.getFirstValue(params, "acr_values");
        LinkedList<ACR> acrValues = null;
        if (StringUtils.isNotBlank(v)) {
            acrValues = new LinkedList<ACR>();
            StringTokenizer st = new StringTokenizer(v, " ");
            while (st.hasMoreTokens()) {
                acrValues.add(new ACR(st.nextToken()));
            }
        }
        v = MultivaluedMapUtils.getFirstValue(params, "claims");
        OIDCClaimsRequest claims = null;
        if (StringUtils.isNotBlank(v)) {
            try {
                claims = OIDCClaimsRequest.parse(v);
            }
            catch (com.nimbusds.oauth2.sdk.ParseException e) {
                String msg = "Invalid claims parameter: " + e.getMessage();
                throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), ar.getClientID(), ar.getRedirectionURI(), ar.impliedResponseMode(), ar.getState(), e);
            }
        }
        if ((purpose = MultivaluedMapUtils.getFirstValue(params, "purpose")) != null && (purpose.length() < 3 || purpose.length() > 300)) {
            String msg = "Invalid purpose parameter: Must not be shorter than 3 and longer than 300 characters";
            throw new com.nimbusds.oauth2.sdk.ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), ar.getClientID(), ar.getRedirectionURI(), ar.impliedResponseMode(), ar.getState());
        }
        HashMap<String, List<String>> customParams = null;
        for (Map.Entry<String, List<String>> p : params.entrySet()) {
            if (REGISTERED_PARAMETER_NAMES.contains(p.getKey())) continue;
            if (customParams == null) {
                customParams = new HashMap<String, List<String>>();
            }
            customParams.put(p.getKey(), p.getValue());
        }
        return new AuthenticationRequest(uri, ar.getResponseType(), ar.getResponseMode(), ar.getScope(), ar.getClientID(), ar.getRedirectionURI(), ar.getState(), nonce, display, ar.getPrompt(), maxAge, uiLocales, claimsLocales, idTokenHint, loginHint, acrValues, claims, purpose, ar.getRequestObject(), ar.getRequestURI(), ar.getCodeChallenge(), ar.getCodeChallengeMethod(), ar.getResources(), ar.includeGrantedScopes(), customParams);
    }

    public static AuthenticationRequest parse(String query) throws com.nimbusds.oauth2.sdk.ParseException {
        return AuthenticationRequest.parse(null, URLUtils.parseParameters(query));
    }

    public static AuthenticationRequest parse(URI uri, String query) throws com.nimbusds.oauth2.sdk.ParseException {
        return AuthenticationRequest.parse(uri, URLUtils.parseParameters(query));
    }

    public static AuthenticationRequest parse(URI uri) throws com.nimbusds.oauth2.sdk.ParseException {
        return AuthenticationRequest.parse(URIUtils.getBaseURI(uri), URLUtils.parseParameters(uri.getRawQuery()));
    }

    public static AuthenticationRequest parse(HTTPRequest httpRequest) throws com.nimbusds.oauth2.sdk.ParseException {
        String query = httpRequest.getQuery();
        if (query == null) {
            throw new com.nimbusds.oauth2.sdk.ParseException("Missing URI query string");
        }
        URI endpointURI = httpRequest.getURI();
        return AuthenticationRequest.parse(endpointURI, query);
    }

    static {
        HashSet<String> p = new HashSet<String>(AuthorizationRequest.getRegisteredParameterNames());
        p.add("nonce");
        p.add("display");
        p.add("max_age");
        p.add("ui_locales");
        p.add("claims_locales");
        p.add("id_token_hint");
        p.add("login_hint");
        p.add("acr_values");
        p.add("claims");
        p.add("purpose");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }

    public static class Builder {
        private URI uri;
        private ResponseType rt;
        private final ClientID clientID;
        private URI redirectURI;
        private Scope scope;
        private State state;
        private Nonce nonce;
        private Display display;
        private Prompt prompt;
        private int maxAge = -1;
        private List<LangTag> uiLocales;
        private List<LangTag> claimsLocales;
        private JWT idTokenHint;
        private String loginHint;
        private List<ACR> acrValues;
        private OIDCClaimsRequest claims;
        private String purpose;
        private JWT requestObject;
        private URI requestURI;
        private ResponseMode rm;
        private CodeChallenge codeChallenge;
        private CodeChallengeMethod codeChallengeMethod;
        private List<URI> resources;
        private boolean includeGrantedScopes;
        private final Map<String, List<String>> customParams = new HashMap<String, List<String>>();

        public Builder(ResponseType rt, Scope scope, ClientID clientID, URI redirectURI) {
            if (rt == null) {
                throw new IllegalArgumentException("The response type must not be null");
            }
            OIDCResponseTypeValidator.validate(rt);
            this.rt = rt;
            if (scope == null) {
                throw new IllegalArgumentException("The scope must not be null");
            }
            if (!scope.contains(OIDCScopeValue.OPENID)) {
                throw new IllegalArgumentException("The scope must include an \"openid\" value");
            }
            this.scope = scope;
            if (clientID == null) {
                throw new IllegalArgumentException("The client ID must not be null");
            }
            this.clientID = clientID;
            this.redirectURI = redirectURI;
        }

        public Builder(JWT requestObject, ClientID clientID) {
            if (requestObject == null) {
                throw new IllegalArgumentException("The request object must not be null");
            }
            this.requestObject = requestObject;
            if (clientID == null) {
                throw new IllegalArgumentException("The client ID must not be null");
            }
            this.clientID = clientID;
        }

        public Builder(URI requestURI, ClientID clientID) {
            if (requestURI == null) {
                throw new IllegalArgumentException("The request URI must not be null");
            }
            this.requestURI = requestURI;
            if (clientID == null) {
                throw new IllegalArgumentException("The client ID must not be null");
            }
            this.clientID = clientID;
        }

        public Builder(AuthenticationRequest request) {
            this.uri = request.getEndpointURI();
            this.rt = request.getResponseType();
            this.clientID = request.getClientID();
            this.redirectURI = request.getRedirectionURI();
            this.scope = request.getScope();
            this.state = request.getState();
            this.nonce = request.getNonce();
            this.display = request.getDisplay();
            this.prompt = request.getPrompt();
            this.maxAge = request.getMaxAge();
            this.uiLocales = request.getUILocales();
            this.claimsLocales = request.getClaimsLocales();
            this.idTokenHint = request.getIDTokenHint();
            this.loginHint = request.getLoginHint();
            this.acrValues = request.getACRValues();
            this.claims = request.getOIDCClaims();
            this.purpose = request.getPurpose();
            this.requestObject = request.getRequestObject();
            this.requestURI = request.getRequestURI();
            this.rm = request.getResponseMode();
            this.codeChallenge = request.getCodeChallenge();
            this.codeChallengeMethod = request.getCodeChallengeMethod();
            this.resources = request.getResources();
            this.includeGrantedScopes = request.includeGrantedScopes();
            this.customParams.putAll(request.getCustomParameters());
        }

        public Builder responseType(ResponseType rt) {
            if (rt == null) {
                throw new IllegalArgumentException("The response type must not be null");
            }
            this.rt = rt;
            return this;
        }

        public Builder scope(Scope scope) {
            if (scope == null) {
                throw new IllegalArgumentException("The scope must not be null");
            }
            if (!scope.contains(OIDCScopeValue.OPENID)) {
                throw new IllegalArgumentException("The scope must include an openid value");
            }
            this.scope = scope;
            return this;
        }

        public Builder redirectionURI(URI redirectURI) {
            if (redirectURI == null) {
                throw new IllegalArgumentException("The redirection URI must not be null");
            }
            this.redirectURI = redirectURI;
            return this;
        }

        public Builder state(State state) {
            this.state = state;
            return this;
        }

        public Builder endpointURI(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder nonce(Nonce nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder display(Display display) {
            this.display = display;
            return this;
        }

        public Builder prompt(Prompt prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder maxAge(int maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public Builder uiLocales(List<LangTag> uiLocales) {
            this.uiLocales = uiLocales;
            return this;
        }

        public Builder claimsLocales(List<LangTag> claimsLocales) {
            this.claimsLocales = claimsLocales;
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

        public Builder acrValues(List<ACR> acrValues) {
            this.acrValues = acrValues;
            return this;
        }

        @Deprecated
        public Builder claims(ClaimsRequest claims) {
            if (claims == null) {
                this.claims = null;
            } else {
                try {
                    this.claims = OIDCClaimsRequest.parse(claims.toJSONObject());
                }
                catch (com.nimbusds.oauth2.sdk.ParseException e) {
                    throw new IllegalArgumentException("Invalid claims: " + e.getMessage(), e);
                }
            }
            return this;
        }

        public Builder claims(OIDCClaimsRequest claims) {
            this.claims = claims;
            return this;
        }

        public Builder purpose(String purpose) {
            this.purpose = purpose;
            return this;
        }

        public Builder requestObject(JWT requestObject) {
            this.requestObject = requestObject;
            return this;
        }

        public Builder requestURI(URI requestURI) {
            this.requestURI = requestURI;
            return this;
        }

        public Builder responseMode(ResponseMode rm) {
            this.rm = rm;
            return this;
        }

        @Deprecated
        public Builder codeChallenge(CodeChallenge codeChallenge, CodeChallengeMethod codeChallengeMethod) {
            this.codeChallenge = codeChallenge;
            this.codeChallengeMethod = codeChallengeMethod;
            return this;
        }

        public Builder codeChallenge(CodeVerifier codeVerifier, CodeChallengeMethod codeChallengeMethod) {
            if (codeVerifier != null) {
                CodeChallengeMethod method = codeChallengeMethod != null ? codeChallengeMethod : CodeChallengeMethod.getDefault();
                this.codeChallenge = CodeChallenge.compute(method, codeVerifier);
                this.codeChallengeMethod = method;
            } else {
                this.codeChallenge = null;
                this.codeChallengeMethod = null;
            }
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

        public Builder includeGrantedScopes(boolean includeGrantedScopes) {
            this.includeGrantedScopes = includeGrantedScopes;
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

        public AuthenticationRequest build() {
            try {
                return new AuthenticationRequest(this.uri, this.rt, this.rm, this.scope, this.clientID, this.redirectURI, this.state, this.nonce, this.display, this.prompt, this.maxAge, this.uiLocales, this.claimsLocales, this.idTokenHint, this.loginHint, this.acrValues, this.claims, this.purpose, this.requestObject, this.requestURI, this.codeChallenge, this.codeChallengeMethod, this.resources, this.includeGrantedScopes, this.customParams);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}

