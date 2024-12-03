/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.AbstractOptionallyIdentifiedRequest;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.util.MapUtils;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.ResourceUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class TokenRequest
extends AbstractOptionallyIdentifiedRequest {
    private final AuthorizationGrant authzGrant;
    private final Scope scope;
    private final List<URI> resources;
    private final RefreshToken existingGrant;
    private final Map<String, List<String>> customParams;

    public TokenRequest(URI uri, ClientAuthentication clientAuth, AuthorizationGrant authzGrant, Scope scope) {
        this(uri, clientAuth, authzGrant, scope, null, null);
    }

    public TokenRequest(URI uri, ClientAuthentication clientAuth, AuthorizationGrant authzGrant, Scope scope, List<URI> resources, Map<String, List<String>> customParams) {
        super(uri, clientAuth);
        if (clientAuth == null) {
            throw new IllegalArgumentException("The client authentication must not be null");
        }
        this.authzGrant = authzGrant;
        this.scope = scope;
        if (resources != null) {
            for (URI resourceURI : resources) {
                if (ResourceUtils.isValidResourceURI(resourceURI)) continue;
                throw new IllegalArgumentException("Resource URI must be absolute and with no query or fragment: " + resourceURI);
            }
        }
        this.resources = resources;
        this.existingGrant = null;
        this.customParams = MapUtils.isNotEmpty(customParams) ? customParams : Collections.emptyMap();
    }

    public TokenRequest(URI uri, ClientAuthentication clientAuth, AuthorizationGrant authzGrant) {
        this(uri, clientAuth, authzGrant, null);
    }

    public TokenRequest(URI uri, ClientID clientID, AuthorizationGrant authzGrant, Scope scope) {
        this(uri, clientID, authzGrant, scope, null, null, null);
    }

    public TokenRequest(URI uri, ClientID clientID, AuthorizationGrant authzGrant, Scope scope, List<URI> resources, RefreshToken existingGrant, Map<String, List<String>> customParams) {
        super(uri, clientID);
        if (authzGrant.getType().requiresClientAuthentication()) {
            throw new IllegalArgumentException("The \"" + authzGrant.getType() + "\" grant type requires client authentication");
        }
        if (authzGrant.getType().requiresClientID() && clientID == null) {
            throw new IllegalArgumentException("The \"" + authzGrant.getType() + "\" grant type requires a \"client_id\" parameter");
        }
        this.authzGrant = authzGrant;
        this.scope = scope;
        if (resources != null) {
            for (URI resourceURI : resources) {
                if (ResourceUtils.isValidResourceURI(resourceURI)) continue;
                throw new IllegalArgumentException("Resource URI must be absolute and with no query or fragment: " + resourceURI);
            }
        }
        this.resources = resources;
        this.existingGrant = existingGrant;
        this.customParams = MapUtils.isNotEmpty(customParams) ? customParams : Collections.emptyMap();
    }

    public TokenRequest(URI uri, ClientID clientID, AuthorizationGrant authzGrant) {
        this(uri, clientID, authzGrant, null);
    }

    public TokenRequest(URI uri, AuthorizationGrant authzGrant, Scope scope) {
        this(uri, (ClientID)null, authzGrant, scope);
    }

    public TokenRequest(URI uri, AuthorizationGrant authzGrant) {
        this(uri, (ClientID)null, authzGrant, null);
    }

    public AuthorizationGrant getAuthorizationGrant() {
        return this.authzGrant;
    }

    public Scope getScope() {
        return this.scope;
    }

    public List<URI> getResources() {
        return this.resources;
    }

    public RefreshToken getExistingGrant() {
        return this.existingGrant;
    }

    public Map<String, List<String>> getCustomParameters() {
        return Collections.unmodifiableMap(this.customParams);
    }

    public List<String> getCustomParameter(String name) {
        return this.customParams.get(name);
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        httpRequest.setEntityContentType(ContentType.APPLICATION_URLENCODED);
        if (this.getClientAuthentication() != null) {
            this.getClientAuthentication().applyTo(httpRequest);
        }
        Map<String, List<String>> params = httpRequest.getQueryParameters();
        params.putAll(this.authzGrant.toParameters());
        if (this.scope != null && !this.scope.isEmpty()) {
            params.put("scope", Collections.singletonList(this.scope.toString()));
        }
        if (this.getClientID() != null) {
            params.put("client_id", Collections.singletonList(this.getClientID().getValue()));
        }
        if (this.getResources() != null) {
            LinkedList<String> values = new LinkedList<String>();
            for (URI uri : this.resources) {
                if (uri == null) continue;
                values.add(uri.toString());
            }
            params.put("resource", values);
        }
        if (this.getExistingGrant() != null) {
            params.put("existing_grant", Collections.singletonList(this.existingGrant.getValue()));
        }
        if (!this.getCustomParameters().isEmpty()) {
            params.putAll(this.getCustomParameters());
        }
        httpRequest.setQuery(URLUtils.serializeParameters(params));
        return httpRequest;
    }

    public static TokenRequest parse(HTTPRequest httpRequest) throws ParseException {
        String rt;
        ClientAuthentication clientAuth;
        URI uri;
        try {
            uri = httpRequest.getURL().toURI();
        }
        catch (URISyntaxException e) {
            throw new ParseException(e.getMessage(), e);
        }
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_URLENCODED);
        try {
            clientAuth = ClientAuthentication.parse(httpRequest);
        }
        catch (ParseException e) {
            throw new ParseException(e.getMessage(), OAuth2Error.INVALID_REQUEST.appendDescription(": " + e.getMessage()));
        }
        Map<String, List<String>> params = httpRequest.getQueryParameters();
        if (clientAuth instanceof ClientSecretBasic && (StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(params, "client_assertion")) || StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(params, "client_assertion_type")))) {
            String msg = "Multiple conflicting client authentication methods found: Basic and JWT assertion";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        AuthorizationGrant grant = AuthorizationGrant.parse(params);
        if (clientAuth == null && grant.getType().requiresClientAuthentication()) {
            String msg = "Missing client authentication";
            throw new ParseException(msg, OAuth2Error.INVALID_CLIENT.appendDescription(": " + msg));
        }
        ClientID clientID = null;
        if (clientAuth == null) {
            String clientIDString = MultivaluedMapUtils.getFirstValue(params, "client_id");
            if (clientIDString != null && !clientIDString.trim().isEmpty()) {
                clientID = new ClientID(clientIDString);
            }
            if (clientID == null && grant.getType().requiresClientID()) {
                String msg = "Missing required client_id parameter";
                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
            }
        }
        String scopeValue = MultivaluedMapUtils.getFirstValue(params, "scope");
        Scope scope = null;
        if (scopeValue != null) {
            scope = Scope.parse(scopeValue);
        }
        LinkedList<URI> resources = null;
        List<String> vList = params.get("resource");
        if (vList != null) {
            resources = new LinkedList<URI>();
            for (String uriValue : vList) {
                URI resourceURI;
                if (uriValue == null) continue;
                String errMsg = "Illegal resource parameter: Must be an absolute URI and with no query or fragment: " + uriValue;
                try {
                    resourceURI = new URI(uriValue);
                }
                catch (URISyntaxException e) {
                    throw new ParseException(errMsg, OAuth2Error.INVALID_RESOURCE.setDescription(errMsg));
                }
                if (!ResourceUtils.isValidResourceURI(resourceURI)) {
                    throw new ParseException(errMsg, OAuth2Error.INVALID_RESOURCE.setDescription(errMsg));
                }
                resources.add(resourceURI);
            }
        }
        RefreshToken existingGrant = StringUtils.isNotBlank(rt = MultivaluedMapUtils.getFirstValue(params, "existing_grant")) ? new RefreshToken(rt) : null;
        HashMap<String, List<String>> customParams = new HashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> p : params.entrySet()) {
            if (p.getKey().equalsIgnoreCase("grant_type") || p.getKey().equalsIgnoreCase("client_id") || p.getKey().equalsIgnoreCase("client_secret") || p.getKey().equalsIgnoreCase("client_assertion_type") || p.getKey().equalsIgnoreCase("client_assertion") || p.getKey().equalsIgnoreCase("scope") || p.getKey().equalsIgnoreCase("resource") || p.getKey().equalsIgnoreCase("existing_grant") || grant.getType().getRequestParameterNames().contains(p.getKey())) continue;
            customParams.put(p.getKey(), p.getValue());
        }
        if (clientAuth != null) {
            return new TokenRequest(uri, clientAuth, grant, scope, resources, customParams);
        }
        return new TokenRequest(uri, clientID, grant, scope, resources, existingGrant, customParams);
    }
}

