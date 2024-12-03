/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerEndpointMetadata;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minidev.json.JSONObject;

public class OIDCProviderEndpointMetadata
extends AuthorizationServerEndpointMetadata {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private URI userInfoEndpoint;
    private URI federationRegistrationEndpoint;

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public OIDCProviderEndpointMetadata() {
    }

    public OIDCProviderEndpointMetadata(AuthorizationServerEndpointMetadata mtlsEndpointAliases) {
        this.setAuthorizationEndpointURI(mtlsEndpointAliases.getAuthorizationEndpointURI());
        this.setTokenEndpointURI(mtlsEndpointAliases.getTokenEndpointURI());
        this.setRegistrationEndpointURI(mtlsEndpointAliases.getRegistrationEndpointURI());
        this.setIntrospectionEndpointURI(mtlsEndpointAliases.getIntrospectionEndpointURI());
        this.setRevocationEndpointURI(mtlsEndpointAliases.getRevocationEndpointURI());
        this.setDeviceAuthorizationEndpointURI(mtlsEndpointAliases.getDeviceAuthorizationEndpointURI());
        this.setPushedAuthorizationRequestEndpointURI(mtlsEndpointAliases.getPushedAuthorizationRequestEndpointURI());
        this.setRequestObjectEndpoint(mtlsEndpointAliases.getRequestObjectEndpoint());
    }

    public URI getUserInfoEndpointURI() {
        return this.userInfoEndpoint;
    }

    public void setUserInfoEndpointURI(URI userInfoEndpoint) {
        this.userInfoEndpoint = userInfoEndpoint;
    }

    public URI getFederationRegistrationEndpointURI() {
        return this.federationRegistrationEndpoint;
    }

    public void setFederationRegistrationEndpointURI(URI federationRegistrationEndpoint) {
        this.federationRegistrationEndpoint = federationRegistrationEndpoint;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        if (this.userInfoEndpoint != null) {
            o.put("userinfo_endpoint", this.userInfoEndpoint.toString());
        }
        if (this.federationRegistrationEndpoint != null) {
            o.put("federation_registration_endpoint", this.federationRegistrationEndpoint.toString());
        }
        return o;
    }

    public static OIDCProviderEndpointMetadata parse(JSONObject jsonObject) throws ParseException {
        AuthorizationServerEndpointMetadata as = AuthorizationServerEndpointMetadata.parse(jsonObject);
        OIDCProviderEndpointMetadata op = new OIDCProviderEndpointMetadata();
        op.setAuthorizationEndpointURI(as.getAuthorizationEndpointURI());
        op.setTokenEndpointURI(as.getTokenEndpointURI());
        op.setRegistrationEndpointURI(as.getRegistrationEndpointURI());
        op.setIntrospectionEndpointURI(as.getIntrospectionEndpointURI());
        op.setRevocationEndpointURI(as.getRevocationEndpointURI());
        op.setDeviceAuthorizationEndpointURI(as.getDeviceAuthorizationEndpointURI());
        op.setPushedAuthorizationRequestEndpointURI(as.getPushedAuthorizationRequestEndpointURI());
        op.setRequestObjectEndpoint(as.getRequestObjectEndpoint());
        op.userInfoEndpoint = JSONObjectUtils.getURI(jsonObject, "userinfo_endpoint", null);
        op.federationRegistrationEndpoint = JSONObjectUtils.getURI(jsonObject, "federation_registration_endpoint", null);
        return op;
    }

    static {
        HashSet<String> p = new HashSet<String>(AuthorizationServerEndpointMetadata.getRegisteredParameterNames());
        p.add("userinfo_endpoint");
        p.add("federation_registration_endpoint");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }
}

