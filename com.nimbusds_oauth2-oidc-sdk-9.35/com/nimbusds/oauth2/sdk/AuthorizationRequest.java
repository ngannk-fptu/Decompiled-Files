/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.JWTParser
 *  com.nimbusds.jwt.SignedJWT
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AbstractRequest;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
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
import com.nimbusds.oauth2.sdk.util.JWTClaimsSetUtils;
import com.nimbusds.oauth2.sdk.util.MapUtils;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.ResourceUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Prompt;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class AuthorizationRequest
extends AbstractRequest {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private final ResponseType rt;
    private final ClientID clientID;
    private final URI redirectURI;
    private final Scope scope;
    private final State state;
    private final ResponseMode rm;
    private final CodeChallenge codeChallenge;
    private final CodeChallengeMethod codeChallengeMethod;
    private final List<URI> resources;
    private final boolean includeGrantedScopes;
    private final JWT requestObject;
    private final URI requestURI;
    protected final Prompt prompt;
    private final Map<String, List<String>> customParams;

    public AuthorizationRequest(URI uri, ResponseType rt, ClientID clientID) {
        this(uri, rt, null, clientID, null, null, null, null, null, null, false, null, null, null, null);
    }

    public AuthorizationRequest(URI uri, ResponseType rt, ResponseMode rm, ClientID clientID, URI redirectURI, Scope scope, State state) {
        this(uri, rt, rm, clientID, redirectURI, scope, state, null, null, null, false, null, null, null, null);
    }

    public AuthorizationRequest(URI uri, ResponseType rt, ResponseMode rm, ClientID clientID, URI redirectURI, Scope scope, State state, CodeChallenge codeChallenge, CodeChallengeMethod codeChallengeMethod, List<URI> resources, boolean includeGrantedScopes, JWT requestObject, URI requestURI, Prompt prompt, Map<String, List<String>> customParams) {
        super(uri);
        if (rt == null && requestObject == null && requestURI == null) {
            throw new IllegalArgumentException("The response type must not be null");
        }
        this.rt = rt;
        this.rm = rm;
        if (clientID == null) {
            throw new IllegalArgumentException("The client ID must not be null");
        }
        this.clientID = clientID;
        this.redirectURI = redirectURI;
        this.scope = scope;
        this.state = state;
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;
        this.resources = ResourceUtils.ensureLegalResourceURIs(resources);
        this.includeGrantedScopes = includeGrantedScopes;
        if (requestObject != null && requestURI != null) {
            throw new IllegalArgumentException("Either a request object or a request URI must be specified, but not both");
        }
        this.requestObject = requestObject;
        this.requestURI = requestURI;
        if (requestObject instanceof SignedJWT) {
            JWTClaimsSet requestObjectClaims;
            try {
                requestObjectClaims = requestObject.getJWTClaimsSet();
            }
            catch (java.text.ParseException e) {
                throw new IllegalArgumentException("Illegal request parameter: " + e.getMessage(), e);
            }
            if (clientID.getValue().equals(requestObjectClaims.getSubject())) {
                throw new IllegalArgumentException("Illegal request parameter: The JWT sub (subject) claim must not equal the client_id");
            }
        }
        this.prompt = prompt;
        this.customParams = MapUtils.isNotEmpty(customParams) ? Collections.unmodifiableMap(customParams) : Collections.emptyMap();
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public ResponseType getResponseType() {
        return this.rt;
    }

    public ResponseMode getResponseMode() {
        return this.rm;
    }

    public ResponseMode impliedResponseMode() {
        return ResponseMode.resolve(this.rm, this.rt);
    }

    public ClientID getClientID() {
        return this.clientID;
    }

    public URI getRedirectionURI() {
        return this.redirectURI;
    }

    public Scope getScope() {
        return this.scope;
    }

    public State getState() {
        return this.state;
    }

    public CodeChallenge getCodeChallenge() {
        return this.codeChallenge;
    }

    public CodeChallengeMethod getCodeChallengeMethod() {
        return this.codeChallengeMethod;
    }

    public List<URI> getResources() {
        return this.resources;
    }

    public boolean includeGrantedScopes() {
        return this.includeGrantedScopes;
    }

    public JWT getRequestObject() {
        return this.requestObject;
    }

    public URI getRequestURI() {
        return this.requestURI;
    }

    public boolean specifiesRequestObject() {
        return this.requestObject != null || this.requestURI != null;
    }

    public Prompt getPrompt() {
        return this.prompt;
    }

    public Map<String, List<String>> getCustomParameters() {
        return this.customParams;
    }

    public List<String> getCustomParameter(String name) {
        return this.customParams.get(name);
    }

    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>(this.customParams);
        params.put("client_id", Collections.singletonList(this.clientID.getValue()));
        if (this.rt != null) {
            params.put("response_type", Collections.singletonList(this.rt.toString()));
        }
        if (this.rm != null) {
            params.put("response_mode", Collections.singletonList(this.rm.getValue()));
        }
        if (this.redirectURI != null) {
            params.put("redirect_uri", Collections.singletonList(this.redirectURI.toString()));
        }
        if (this.scope != null) {
            params.put("scope", Collections.singletonList(this.scope.toString()));
        }
        if (this.state != null) {
            params.put("state", Collections.singletonList(this.state.getValue()));
        }
        if (this.codeChallenge != null) {
            params.put("code_challenge", Collections.singletonList(this.codeChallenge.getValue()));
            if (this.codeChallengeMethod != null) {
                params.put("code_challenge_method", Collections.singletonList(this.codeChallengeMethod.getValue()));
            }
        }
        if (this.includeGrantedScopes) {
            params.put("include_granted_scopes", Collections.singletonList("true"));
        }
        if (this.resources != null) {
            params.put("resource", URIUtils.toStringList(this.resources));
        }
        if (this.requestObject != null) {
            try {
                params.put("request", Collections.singletonList(this.requestObject.serialize()));
            }
            catch (IllegalStateException e) {
                throw new SerializeException("Couldn't serialize request object to JWT: " + e.getMessage(), e);
            }
        }
        if (this.requestURI != null) {
            params.put("request_uri", Collections.singletonList(this.requestURI.toString()));
        }
        if (this.prompt != null) {
            params.put("prompt", Collections.singletonList(this.prompt.toString()));
        }
        return params;
    }

    public JWTClaimsSet toJWTClaimsSet() {
        if (this.specifiesRequestObject()) {
            throw new IllegalStateException("Cannot create nested JWT secured authorization request");
        }
        return JWTClaimsSetUtils.toJWTClaimsSet(this.toParameters());
    }

    public String toQueryString() {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        if (this.getEndpointURI() != null) {
            params.putAll(URLUtils.parseParameters(this.getEndpointURI().getQuery()));
        }
        params.putAll(this.toParameters());
        return URLUtils.serializeParameters(params);
    }

    public URI toURI() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The authorization endpoint URI is not specified");
        }
        StringBuilder sb = new StringBuilder(URIUtils.stripQueryString(this.getEndpointURI()).toString());
        sb.append('?');
        sb.append(this.toQueryString());
        try {
            return new URI(sb.toString());
        }
        catch (URISyntaxException e) {
            throw new SerializeException("Couldn't append query string: " + e.getMessage(), e);
        }
    }

    public HTTPRequest toHTTPRequest(HTTPRequest.Method method) {
        HTTPRequest httpRequest;
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        if (method.equals((Object)HTTPRequest.Method.GET)) {
            httpRequest = new HTTPRequest(HTTPRequest.Method.GET, this.getEndpointURI());
        } else if (method.equals((Object)HTTPRequest.Method.POST)) {
            httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        } else {
            throw new IllegalArgumentException("The HTTP request method must be GET or POST");
        }
        httpRequest.setQuery(this.toQueryString());
        return httpRequest;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        return this.toHTTPRequest(HTTPRequest.Method.GET);
    }

    public static AuthorizationRequest parse(Map<String, List<String>> params) throws ParseException {
        return AuthorizationRequest.parse(null, params);
    }

    public static AuthorizationRequest parse(URI uri, Map<String, List<String>> params) throws ParseException {
        Prompt prompt;
        List<URI> resources;
        Set<String> repeatParams = MultivaluedMapUtils.getKeysWithMoreThanOneValue(params, Collections.singleton("resource"));
        if (!repeatParams.isEmpty()) {
            String msg = "Parameter(s) present more than once: " + repeatParams;
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.setDescription(msg));
        }
        URI redirectURI = null;
        State state = State.parse(MultivaluedMapUtils.getFirstValue(params, "state"));
        ResponseMode rm = null;
        ResponseType rt = null;
        String v = MultivaluedMapUtils.getFirstValue(params, "response_mode");
        if (StringUtils.isNotBlank(v)) {
            rm = new ResponseMode(v);
        }
        if (StringUtils.isBlank(v = MultivaluedMapUtils.getFirstValue(params, "client_id"))) {
            String msg = "Missing client_id parameter";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        ClientID clientID = new ClientID(v);
        v = MultivaluedMapUtils.getFirstValue(params, "redirect_uri");
        if (StringUtils.isNotBlank(v)) {
            try {
                redirectURI = new URI(v);
            }
            catch (URISyntaxException e) {
                String msg = "Invalid redirect_uri parameter: " + e.getMessage();
                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
            }
        }
        if (StringUtils.isNotBlank(v = MultivaluedMapUtils.getFirstValue(params, "response_type"))) {
            try {
                rt = ResponseType.parse(v);
            }
            catch (ParseException e) {
                String msg = "Invalid response_type parameter";
                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), clientID, redirectURI, rm, state, e);
            }
        }
        v = MultivaluedMapUtils.getFirstValue(params, "request_uri");
        URI requestURI = null;
        if (StringUtils.isNotBlank(v)) {
            try {
                requestURI = new URI(v);
            }
            catch (URISyntaxException e) {
                String msg = "Invalid request_uri parameter: " + e.getMessage();
                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), clientID, redirectURI, ResponseMode.resolve(rm, rt), state, e);
            }
        }
        v = MultivaluedMapUtils.getFirstValue(params, "request");
        JWT requestObject = null;
        if (StringUtils.isNotBlank(v)) {
            if (requestURI != null) {
                String msg = "Invalid request: Found mutually exclusive request and request_uri parameters";
                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), clientID, redirectURI, ResponseMode.resolve(rm, rt), state, null);
            }
            try {
                requestObject = JWTParser.parse((String)v);
                if (requestObject instanceof SignedJWT) {
                    JWTClaimsSet requestObjectClaims = requestObject.getJWTClaimsSet();
                    if (clientID.getValue().equals(requestObjectClaims.getSubject())) {
                        throw new java.text.ParseException("The JWT sub (subject) claim must not equal the client_id", 0);
                    }
                }
            }
            catch (java.text.ParseException e) {
                String msg = "Invalid request parameter: " + e.getMessage();
                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), clientID, redirectURI, ResponseMode.resolve(rm, rt), state, e);
            }
        }
        if (rt == null && requestObject == null && requestURI == null) {
            String msg = "Missing response_type parameter";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), clientID, redirectURI, ResponseMode.resolve(rm, null), state, null);
        }
        v = MultivaluedMapUtils.getFirstValue(params, "scope");
        Scope scope = null;
        if (StringUtils.isNotBlank(v)) {
            scope = Scope.parse(v);
        }
        CodeChallenge codeChallenge = null;
        CodeChallengeMethod codeChallengeMethod = null;
        v = MultivaluedMapUtils.getFirstValue(params, "code_challenge");
        if (StringUtils.isNotBlank(v)) {
            codeChallenge = CodeChallenge.parse(v);
        }
        if (codeChallenge != null && StringUtils.isNotBlank(v = MultivaluedMapUtils.getFirstValue(params, "code_challenge_method"))) {
            codeChallengeMethod = CodeChallengeMethod.parse(v);
        }
        try {
            resources = ResourceUtils.parseResourceURIs(params.get("resource"));
        }
        catch (ParseException e) {
            throw new ParseException(e.getMessage(), OAuth2Error.INVALID_RESOURCE.setDescription(e.getMessage()), clientID, redirectURI, ResponseMode.resolve(rm, rt), state, e);
        }
        boolean includeGrantedScopes = false;
        v = MultivaluedMapUtils.getFirstValue(params, "include_granted_scopes");
        if ("true".equals(v)) {
            includeGrantedScopes = true;
        }
        try {
            prompt = Prompt.parse(MultivaluedMapUtils.getFirstValue(params, "prompt"));
        }
        catch (ParseException e) {
            String msg = "Invalid prompt parameter: " + e.getMessage();
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), clientID, redirectURI, ResponseMode.resolve(rm, rt), state, e);
        }
        HashMap<String, List<String>> customParams = null;
        for (Map.Entry<String, List<String>> p : params.entrySet()) {
            if (REGISTERED_PARAMETER_NAMES.contains(p.getKey())) continue;
            if (customParams == null) {
                customParams = new HashMap<String, List<String>>();
            }
            customParams.put(p.getKey(), p.getValue());
        }
        return new AuthorizationRequest(uri, rt, rm, clientID, redirectURI, scope, state, codeChallenge, codeChallengeMethod, resources, includeGrantedScopes, requestObject, requestURI, prompt, customParams);
    }

    public static AuthorizationRequest parse(String query) throws ParseException {
        return AuthorizationRequest.parse(null, URLUtils.parseParameters(query));
    }

    public static AuthorizationRequest parse(URI uri, String query) throws ParseException {
        return AuthorizationRequest.parse(uri, URLUtils.parseParameters(query));
    }

    public static AuthorizationRequest parse(URI uri) throws ParseException {
        return AuthorizationRequest.parse(URIUtils.getBaseURI(uri), URLUtils.parseParameters(uri.getRawQuery()));
    }

    public static AuthorizationRequest parse(HTTPRequest httpRequest) throws ParseException {
        String query = httpRequest.getQuery();
        if (query == null) {
            throw new ParseException("Missing URI query string");
        }
        return AuthorizationRequest.parse(URIUtils.getBaseURI(httpRequest.getURI()), query);
    }

    static {
        HashSet<String> p = new HashSet<String>();
        p.add("response_type");
        p.add("client_id");
        p.add("redirect_uri");
        p.add("scope");
        p.add("state");
        p.add("response_mode");
        p.add("code_challenge");
        p.add("code_challenge_method");
        p.add("resource");
        p.add("include_granted_scopes");
        p.add("request_uri");
        p.add("request");
        p.add("prompt");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }

    public static class Builder {
        private URI uri;
        private ResponseType rt;
        private final ClientID clientID;
        private URI redirectURI;
        private Scope scope;
        private State state;
        private ResponseMode rm;
        private CodeChallenge codeChallenge;
        private CodeChallengeMethod codeChallengeMethod;
        private boolean includeGrantedScopes;
        private List<URI> resources;
        private JWT requestObject;
        private URI requestURI;
        private Prompt prompt;
        private final Map<String, List<String>> customParams = new HashMap<String, List<String>>();

        public Builder(ResponseType rt, ClientID clientID) {
            if (rt == null) {
                throw new IllegalArgumentException("The response type must not be null");
            }
            this.rt = rt;
            if (clientID == null) {
                throw new IllegalArgumentException("The client ID must not be null");
            }
            this.clientID = clientID;
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

        public Builder(AuthorizationRequest request) {
            this.uri = request.getEndpointURI();
            this.scope = request.scope;
            this.rt = request.getResponseType();
            this.clientID = request.getClientID();
            this.redirectURI = request.getRedirectionURI();
            this.state = request.getState();
            this.rm = request.getResponseMode();
            this.codeChallenge = request.getCodeChallenge();
            this.codeChallengeMethod = request.getCodeChallengeMethod();
            this.resources = request.getResources();
            this.includeGrantedScopes = request.includeGrantedScopes();
            this.requestObject = request.requestObject;
            this.requestURI = request.requestURI;
            this.prompt = request.prompt;
            if (request instanceof AuthenticationRequest) {
                AuthenticationRequest oidcRequest = (AuthenticationRequest)request;
                for (Map.Entry<String, List<String>> oidcParam : oidcRequest.toParameters().entrySet()) {
                    if (REGISTERED_PARAMETER_NAMES.contains(oidcParam.getKey())) continue;
                    this.customParams.put(oidcParam.getKey(), oidcParam.getValue());
                }
            } else {
                this.customParams.putAll(request.getCustomParameters());
            }
        }

        public Builder responseType(ResponseType rt) {
            if (rt == null) {
                throw new IllegalArgumentException("The response type must not be null");
            }
            this.rt = rt;
            return this;
        }

        public Builder redirectionURI(URI redirectURI) {
            this.redirectURI = redirectURI;
            return this;
        }

        public Builder scope(Scope scope) {
            this.scope = scope;
            return this;
        }

        public Builder state(State state) {
            this.state = state;
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

        public Builder requestObject(JWT requestObject) {
            this.requestObject = requestObject;
            return this;
        }

        public Builder requestURI(URI requestURI) {
            this.requestURI = requestURI;
            return this;
        }

        public Builder prompt(Prompt prompt) {
            this.prompt = prompt;
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

        public AuthorizationRequest build() {
            try {
                return new AuthorizationRequest(this.uri, this.rt, this.rm, this.clientID, this.redirectURI, this.scope, this.state, this.codeChallenge, this.codeChallengeMethod, this.resources, this.includeGrantedScopes, this.requestObject, this.requestURI, this.prompt, this.customParams);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}

