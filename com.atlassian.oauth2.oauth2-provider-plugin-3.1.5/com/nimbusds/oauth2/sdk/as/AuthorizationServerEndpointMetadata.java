/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.as;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.as.ReadOnlyAuthorizationServerEndpointMetadata;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.OrderedJSONObject;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.minidev.json.JSONObject;

public class AuthorizationServerEndpointMetadata
implements ReadOnlyAuthorizationServerEndpointMetadata {
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

    @Override
    public URI getAuthorizationEndpointURI() {
        return this.authzEndpoint;
    }

    public void setAuthorizationEndpointURI(URI authzEndpoint) {
        this.authzEndpoint = authzEndpoint;
    }

    @Override
    public URI getTokenEndpointURI() {
        return this.tokenEndpoint;
    }

    public void setTokenEndpointURI(URI tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    @Override
    public URI getRegistrationEndpointURI() {
        return this.regEndpoint;
    }

    public void setRegistrationEndpointURI(URI regEndpoint) {
        this.regEndpoint = regEndpoint;
    }

    @Override
    public URI getIntrospectionEndpointURI() {
        return this.introspectionEndpoint;
    }

    public void setIntrospectionEndpointURI(URI introspectionEndpoint) {
        this.introspectionEndpoint = introspectionEndpoint;
    }

    @Override
    public URI getRevocationEndpointURI() {
        return this.revocationEndpoint;
    }

    public void setRevocationEndpointURI(URI revocationEndpoint) {
        this.revocationEndpoint = revocationEndpoint;
    }

    @Override
    @Deprecated
    public URI getRequestObjectEndpoint() {
        return this.requestObjectEndpoint;
    }

    @Deprecated
    public void setRequestObjectEndpoint(URI requestObjectEndpoint) {
        this.requestObjectEndpoint = requestObjectEndpoint;
    }

    @Override
    public URI getPushedAuthorizationRequestEndpointURI() {
        return this.parEndpoint;
    }

    public void setPushedAuthorizationRequestEndpointURI(URI parEndpoint) {
        this.parEndpoint = parEndpoint;
    }

    @Override
    public URI getDeviceAuthorizationEndpointURI() {
        return this.deviceAuthzEndpoint;
    }

    public void setDeviceAuthorizationEndpointURI(URI deviceAuthzEndpoint) {
        this.deviceAuthzEndpoint = deviceAuthzEndpoint;
    }

    @Override
    public URI getBackChannelAuthenticationEndpointURI() {
        return this.backChannelAuthEndpoint;
    }

    @Override
    @Deprecated
    public URI getBackChannelAuthenticationEndpoint() {
        return this.getBackChannelAuthenticationEndpointURI();
    }

    public void setBackChannelAuthenticationEndpointURI(URI backChannelAuthEndpoint) {
        this.backChannelAuthEndpoint = backChannelAuthEndpoint;
    }

    @Deprecated
    public void setBackChannelAuthenticationEndpoint(URI backChannelAuthEndpoint) {
        this.setBackChannelAuthenticationEndpointURI(backChannelAuthEndpoint);
    }

    @Override
    public JSONObject toJSONObject() {
        OrderedJSONObject o = new OrderedJSONObject();
        if (this.getAuthorizationEndpointURI() != null) {
            ((HashMap)o).put("authorization_endpoint", this.getAuthorizationEndpointURI().toString());
        }
        if (this.getTokenEndpointURI() != null) {
            ((HashMap)o).put("token_endpoint", this.getTokenEndpointURI().toString());
        }
        if (this.getRegistrationEndpointURI() != null) {
            ((HashMap)o).put("registration_endpoint", this.getRegistrationEndpointURI().toString());
        }
        if (this.getIntrospectionEndpointURI() != null) {
            ((HashMap)o).put("introspection_endpoint", this.getIntrospectionEndpointURI().toString());
        }
        if (this.getRevocationEndpointURI() != null) {
            ((HashMap)o).put("revocation_endpoint", this.getRevocationEndpointURI().toString());
        }
        if (this.getRequestObjectEndpoint() != null) {
            ((HashMap)o).put("request_object_endpoint", this.getRequestObjectEndpoint().toString());
        }
        if (this.getPushedAuthorizationRequestEndpointURI() != null) {
            ((HashMap)o).put("pushed_authorization_request_endpoint", this.getPushedAuthorizationRequestEndpointURI().toString());
        }
        if (this.getDeviceAuthorizationEndpointURI() != null) {
            ((HashMap)o).put("device_authorization_endpoint", this.getDeviceAuthorizationEndpointURI().toString());
        }
        if (this.getBackChannelAuthenticationEndpointURI() != null) {
            ((HashMap)o).put("backchannel_authentication_endpoint", this.getBackChannelAuthenticationEndpointURI().toString());
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

