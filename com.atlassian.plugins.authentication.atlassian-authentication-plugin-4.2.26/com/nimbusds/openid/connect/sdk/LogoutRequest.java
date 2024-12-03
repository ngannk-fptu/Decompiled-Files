/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AbstractRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class LogoutRequest
extends AbstractRequest {
    private final JWT idTokenHint;
    private final URI postLogoutRedirectURI;
    private final State state;

    public LogoutRequest(URI uri, JWT idTokenHint, URI postLogoutRedirectURI, State state) {
        super(uri);
        this.idTokenHint = idTokenHint;
        this.postLogoutRedirectURI = postLogoutRedirectURI;
        if (postLogoutRedirectURI == null && state != null) {
            throw new IllegalArgumentException("The state parameter required a post-logout redirection URI");
        }
        this.state = state;
    }

    public LogoutRequest(URI uri, JWT idTokenHint) {
        this(uri, idTokenHint, null, null);
    }

    public LogoutRequest(URI uri) {
        this(uri, null, null, null);
    }

    public JWT getIDTokenHint() {
        return this.idTokenHint;
    }

    public URI getPostLogoutRedirectionURI() {
        return this.postLogoutRedirectURI;
    }

    public State getState() {
        return this.state;
    }

    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        if (this.idTokenHint != null) {
            try {
                params.put("id_token_hint", Collections.singletonList(this.idTokenHint.serialize()));
            }
            catch (IllegalStateException e) {
                throw new SerializeException("Couldn't serialize ID token: " + e.getMessage(), e);
            }
        }
        if (this.postLogoutRedirectURI != null) {
            params.put("post_logout_redirect_uri", Collections.singletonList(this.postLogoutRedirectURI.toString()));
        }
        if (this.state != null) {
            params.put("state", Collections.singletonList(this.state.getValue()));
        }
        return params;
    }

    public String toQueryString() {
        return URLUtils.serializeParameters(this.toParameters());
    }

    public URI toURI() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The end-session endpoint URI is not specified");
        }
        HashMap<String, List<String>> mergedQueryParams = new HashMap<String, List<String>>(URLUtils.parseParameters(this.getEndpointURI().getQuery()));
        mergedQueryParams.putAll(this.toParameters());
        String query = URLUtils.serializeParameters(mergedQueryParams);
        if (StringUtils.isNotBlank(query)) {
            query = '?' + query;
        }
        try {
            return new URI(URIUtils.getBaseURI(this.getEndpointURI()) + query);
        }
        catch (URISyntaxException e) {
            throw new SerializeException(e.getMessage(), e);
        }
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        URL baseURL;
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HashMap<String, List<String>> mergedQueryParams = new HashMap<String, List<String>>(URLUtils.parseParameters(this.getEndpointURI().getQuery()));
        mergedQueryParams.putAll(this.toParameters());
        try {
            baseURL = URLUtils.getBaseURL(this.getEndpointURI().toURL());
        }
        catch (MalformedURLException e) {
            throw new SerializeException(e.getMessage(), e);
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.GET, baseURL);
        httpRequest.setQuery(URLUtils.serializeParameters(mergedQueryParams));
        return httpRequest;
    }

    public static LogoutRequest parse(Map<String, List<String>> params) throws ParseException {
        return LogoutRequest.parse(null, params);
    }

    public static LogoutRequest parse(URI uri, Map<String, List<String>> params) throws ParseException {
        String v = MultivaluedMapUtils.getFirstValue(params, "id_token_hint");
        JWT idTokenHint = null;
        if (StringUtils.isNotBlank(v)) {
            try {
                idTokenHint = JWTParser.parse(v);
            }
            catch (java.text.ParseException e) {
                throw new ParseException("Invalid ID token hint: " + e.getMessage(), e);
            }
        }
        v = MultivaluedMapUtils.getFirstValue(params, "post_logout_redirect_uri");
        URI postLogoutRedirectURI = null;
        if (StringUtils.isNotBlank(v)) {
            try {
                postLogoutRedirectURI = new URI(v);
            }
            catch (URISyntaxException e) {
                throw new ParseException("Invalid \"post_logout_redirect_uri\" parameter: " + e.getMessage(), e);
            }
        }
        State state = null;
        v = MultivaluedMapUtils.getFirstValue(params, "state");
        if (postLogoutRedirectURI != null && StringUtils.isNotBlank(v)) {
            state = new State(v);
        }
        return new LogoutRequest(uri, idTokenHint, postLogoutRedirectURI, state);
    }

    public static LogoutRequest parse(String query) throws ParseException {
        return LogoutRequest.parse(null, URLUtils.parseParameters(query));
    }

    public static LogoutRequest parse(URI uri, String query) throws ParseException {
        return LogoutRequest.parse(uri, URLUtils.parseParameters(query));
    }

    public static LogoutRequest parse(URI uri) throws ParseException {
        return LogoutRequest.parse(URIUtils.getBaseURI(uri), URLUtils.parseParameters(uri.getRawQuery()));
    }

    public static LogoutRequest parse(HTTPRequest httpRequest) throws ParseException {
        String query = httpRequest.getQuery();
        if (query == null) {
            throw new ParseException("Missing URI query string");
        }
        try {
            return LogoutRequest.parse(URIUtils.getBaseURI(httpRequest.getURL().toURI()), query);
        }
        catch (URISyntaxException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}

