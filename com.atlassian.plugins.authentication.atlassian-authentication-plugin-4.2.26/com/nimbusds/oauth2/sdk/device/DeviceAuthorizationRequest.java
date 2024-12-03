/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.device;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.AbstractOptionallyIdentifiedRequest;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.MapUtils;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class DeviceAuthorizationRequest
extends AbstractOptionallyIdentifiedRequest {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private final Scope scope;
    private final Map<String, List<String>> customParams;

    public DeviceAuthorizationRequest(URI uri, ClientID clientID) {
        this(uri, clientID, null, null);
    }

    public DeviceAuthorizationRequest(URI uri, ClientID clientID, Scope scope) {
        this(uri, clientID, scope, null);
    }

    public DeviceAuthorizationRequest(URI uri, ClientID clientID, Scope scope, Map<String, List<String>> customParams) {
        super(uri, clientID);
        if (clientID == null) {
            throw new IllegalArgumentException("The client ID must not be null");
        }
        this.scope = scope;
        this.customParams = MapUtils.isNotEmpty(customParams) ? Collections.unmodifiableMap(customParams) : Collections.emptyMap();
    }

    public DeviceAuthorizationRequest(URI uri, ClientAuthentication clientAuth, Scope scope, Map<String, List<String>> customParams) {
        super(uri, clientAuth);
        if (clientAuth == null) {
            throw new IllegalArgumentException("The client authentication must not be null");
        }
        this.scope = scope;
        this.customParams = MapUtils.isNotEmpty(customParams) ? Collections.unmodifiableMap(customParams) : Collections.emptyMap();
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public Scope getScope() {
        return this.scope;
    }

    public Map<String, List<String>> getCustomParameters() {
        return this.customParams;
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
        if (this.scope != null && !this.scope.isEmpty()) {
            params.put("scope", Collections.singletonList(this.scope.toString()));
        }
        if (this.getClientID() != null) {
            params.put("client_id", Collections.singletonList(this.getClientID().getValue()));
        }
        if (!this.getCustomParameters().isEmpty()) {
            params.putAll(this.getCustomParameters());
        }
        httpRequest.setQuery(URLUtils.serializeParameters(params));
        return httpRequest;
    }

    public static DeviceAuthorizationRequest parse(HTTPRequest httpRequest) throws ParseException {
        ClientID clientID;
        String v;
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
        if (clientAuth == null) {
            v = MultivaluedMapUtils.getFirstValue(params, "client_id");
            if (StringUtils.isBlank(v)) {
                String msg = "Missing client_id parameter";
                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
            }
            clientID = new ClientID(v);
        } else {
            clientID = null;
        }
        v = MultivaluedMapUtils.getFirstValue(params, "scope");
        Scope scope = null;
        if (StringUtils.isNotBlank(v)) {
            scope = Scope.parse(v);
        }
        HashMap<String, List<String>> customParams = null;
        for (Map.Entry<String, List<String>> p : params.entrySet()) {
            if (REGISTERED_PARAMETER_NAMES.contains(p.getKey())) continue;
            if (customParams == null) {
                customParams = new HashMap<String, List<String>>();
            }
            customParams.put(p.getKey(), p.getValue());
        }
        if (clientAuth == null) {
            return new DeviceAuthorizationRequest(uri, clientID, scope, customParams);
        }
        return new DeviceAuthorizationRequest(uri, clientAuth, scope, customParams);
    }

    static {
        HashSet<String> p = new HashSet<String>();
        p.add("client_id");
        p.add("scope");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }

    public static class Builder {
        private URI uri;
        private final ClientAuthentication clientAuth;
        private final ClientID clientID;
        private Scope scope;
        private final Map<String, List<String>> customParams = new HashMap<String, List<String>>();

        public Builder(ClientID clientID) {
            if (clientID == null) {
                throw new IllegalArgumentException("The client ID must not be null");
            }
            this.clientID = clientID;
            this.clientAuth = null;
        }

        public Builder(ClientAuthentication clientAuth) {
            if (clientAuth == null) {
                throw new IllegalArgumentException("The client authentication must not be null");
            }
            this.clientID = null;
            this.clientAuth = clientAuth;
        }

        public Builder(DeviceAuthorizationRequest request) {
            this.uri = request.getEndpointURI();
            this.clientAuth = request.getClientAuthentication();
            this.scope = request.scope;
            this.clientID = request.getClientID();
            this.customParams.putAll(request.getCustomParameters());
        }

        public Builder scope(Scope scope) {
            this.scope = scope;
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

        public DeviceAuthorizationRequest build() {
            try {
                if (this.clientAuth == null) {
                    return new DeviceAuthorizationRequest(this.uri, this.clientID, this.scope, this.customParams);
                }
                return new DeviceAuthorizationRequest(this.uri, this.clientAuth, this.scope, this.customParams);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}

