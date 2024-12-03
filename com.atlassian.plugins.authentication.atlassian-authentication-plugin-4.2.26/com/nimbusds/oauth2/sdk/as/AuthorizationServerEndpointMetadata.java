/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.as;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.OrderedJSONObject;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.minidev.json.JSONObject;

public class AuthorizationServerEndpointMetadata {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private URI authzEndpoint;
    private URI tokenEndpoint;
    private URI regEndpoint;
    private URI introspectionEndpoint;
    private URI revocationEndpoint;
    private URI requestObjectEndpoint;
    private URI parEndpoint;
    private URI deviceAuthzEndpoint;
    private URI backChannelAuthEndpoint;

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public URI getAuthorizationEndpointURI() {
        return this.authzEndpoint;
    }

    public void setAuthorizationEndpointURI(URI authzEndpoint) {
        this.authzEndpoint = authzEndpoint;
    }

    public URI getTokenEndpointURI() {
        return this.tokenEndpoint;
    }

    public void setTokenEndpointURI(URI tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public URI getRegistrationEndpointURI() {
        return this.regEndpoint;
    }

    public void setRegistrationEndpointURI(URI regEndpoint) {
        this.regEndpoint = regEndpoint;
    }

    public URI getIntrospectionEndpointURI() {
        return this.introspectionEndpoint;
    }

    public void setIntrospectionEndpointURI(URI introspectionEndpoint) {
        this.introspectionEndpoint = introspectionEndpoint;
    }

    public URI getRevocationEndpointURI() {
        return this.revocationEndpoint;
    }

    public void setRevocationEndpointURI(URI revocationEndpoint) {
        this.revocationEndpoint = revocationEndpoint;
    }

    @Deprecated
    public URI getRequestObjectEndpoint() {
        return this.requestObjectEndpoint;
    }

    @Deprecated
    public void setRequestObjectEndpoint(URI requestObjectEndpoint) {
        this.requestObjectEndpoint = requestObjectEndpoint;
    }

    public URI getPushedAuthorizationRequestEndpointURI() {
        return this.parEndpoint;
    }

    public void setPushedAuthorizationRequestEndpointURI(URI parEndpoint) {
        this.parEndpoint = parEndpoint;
    }

    public URI getDeviceAuthorizationEndpointURI() {
        return this.deviceAuthzEndpoint;
    }

    public void setDeviceAuthorizationEndpointURI(URI deviceAuthzEndpoint) {
        this.deviceAuthzEndpoint = deviceAuthzEndpoint;
    }

    public URI getBackChannelAuthenticationEndpoint() {
        return this.backChannelAuthEndpoint;
    }

    public void setBackChannelAuthenticationEndpoint(URI backChannelAuthEndpoint) {
        this.backChannelAuthEndpoint = backChannelAuthEndpoint;
    }

    public JSONObject toJSONObject() {
        OrderedJSONObject o = new OrderedJSONObject();
        if (this.authzEndpoint != null) {
            ((HashMap)o).put("authorization_endpoint", this.authzEndpoint.toString());
        }
        if (this.tokenEndpoint != null) {
            ((HashMap)o).put("token_endpoint", this.tokenEndpoint.toString());
        }
        if (this.regEndpoint != null) {
            ((HashMap)o).put("registration_endpoint", this.regEndpoint.toString());
        }
        if (this.introspectionEndpoint != null) {
            ((HashMap)o).put("introspection_endpoint", this.introspectionEndpoint.toString());
        }
        if (this.revocationEndpoint != null) {
            ((HashMap)o).put("revocation_endpoint", this.revocationEndpoint.toString());
        }
        if (this.requestObjectEndpoint != null) {
            ((HashMap)o).put("request_object_endpoint", this.requestObjectEndpoint.toString());
        }
        if (this.parEndpoint != null) {
            ((HashMap)o).put("pushed_authorization_request_endpoint", this.parEndpoint.toString());
        }
        if (this.deviceAuthzEndpoint != null) {
            ((HashMap)o).put("device_authorization_endpoint", this.deviceAuthzEndpoint.toString());
        }
        if (this.backChannelAuthEndpoint != null) {
            ((HashMap)o).put("backchannel_authentication_endpoint", this.backChannelAuthEndpoint.toString());
        }
        return o;
    }

    public String toString() {
        return this.toJSONObject().toJSONString();
    }

    public static AuthorizationServerEndpointMetadata parse(JSONObject jsonObject) throws ParseException {
        AuthorizationServerEndpointMetadata as = new AuthorizationServerEndpointMetadata();
        as.authzEndpoint = JSONObjectUtils.getURI(jsonObject, "authorization_endpoint", null);
        as.tokenEndpoint = JSONObjectUtils.getURI(jsonObject, "token_endpoint", null);
        as.regEndpoint = JSONObjectUtils.getURI(jsonObject, "registration_endpoint", null);
        as.introspectionEndpoint = JSONObjectUtils.getURI(jsonObject, "introspection_endpoint", null);
        as.revocationEndpoint = JSONObjectUtils.getURI(jsonObject, "revocation_endpoint", null);
        as.requestObjectEndpoint = JSONObjectUtils.getURI(jsonObject, "request_object_endpoint", null);
        as.parEndpoint = JSONObjectUtils.getURI(jsonObject, "pushed_authorization_request_endpoint", null);
        as.deviceAuthzEndpoint = JSONObjectUtils.getURI(jsonObject, "device_authorization_endpoint", null);
        as.backChannelAuthEndpoint = JSONObjectUtils.getURI(jsonObject, "backchannel_authentication_endpoint", null);
        return as;
    }

    static {
        HashSet<String> p = new HashSet<String>();
        p.add("authorization_endpoint");
        p.add("token_endpoint");
        p.add("registration_endpoint");
        p.add("introspection_endpoint");
        p.add("revocation_endpoint");
        p.add("request_object_endpoint");
        p.add("pushed_authorization_request_endpoint");
        p.add("device_authorization_endpoint");
        p.add("backchannel_authentication_endpoint");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }
}

