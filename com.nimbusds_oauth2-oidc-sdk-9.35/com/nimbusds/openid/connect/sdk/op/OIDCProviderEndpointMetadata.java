/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerEndpointMetadata;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.op.ReadOnlyOIDCProviderEndpointMetadata;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minidev.json.JSONObject;

public class OIDCProviderEndpointMetadata
extends AuthorizationServerEndpointMetadata
implements ReadOnlyOIDCProviderEndpointMetadata {
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private URI userInfoEndpoint;
    private URI federationRegistrationEndpoint;
    private URI checkSessionIframe;
    private URI endSessionEndpoint;

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    public OIDCProviderEndpointMetadata() {
    }

    public OIDCProviderEndpointMetadata(AuthorizationServerEndpointMetadata endpointMetadata) {
        this.setAuthorizationEndpointURI(endpointMetadata.getAuthorizationEndpointURI());
        this.setTokenEndpointURI(endpointMetadata.getTokenEndpointURI());
        this.setRegistrationEndpointURI(endpointMetadata.getRegistrationEndpointURI());
        this.setIntrospectionEndpointURI(endpointMetadata.getIntrospectionEndpointURI());
        this.setRevocationEndpointURI(endpointMetadata.getRevocationEndpointURI());
        this.setDeviceAuthorizationEndpointURI(endpointMetadata.getDeviceAuthorizationEndpointURI());
        this.setBackChannelAuthenticationEndpointURI(endpointMetadata.getBackChannelAuthenticationEndpointURI());
        this.setPushedAuthorizationRequestEndpointURI(endpointMetadata.getPushedAuthorizationRequestEndpointURI());
        this.setRequestObjectEndpoint(endpointMetadata.getRequestObjectEndpoint());
    }

    @Override
    public URI getUserInfoEndpointURI() {
        return this.userInfoEndpoint;
    }

    public void setUserInfoEndpointURI(URI userInfoEndpoint) {
        this.userInfoEndpoint = userInfoEndpoint;
    }

    @Override
    public URI getCheckSessionIframeURI() {
        return this.checkSessionIframe;
    }

    public void setCheckSessionIframeURI(URI checkSessionIframe) {
        this.checkSessionIframe = checkSessionIframe;
    }

    @Override
    public URI getEndSessionEndpointURI() {
        return this.endSessionEndpoint;
    }

    public void setEndSessionEndpointURI(URI endSessionEndpoint) {
        this.endSessionEndpoint = endSessionEndpoint;
    }

    @Override
    public URI getFederationRegistrationEndpointURI() {
        return this.federationRegistrationEndpoint;
    }

    public void setFederationRegistrationEndpointURI(URI federationRegistrationEndpoint) {
        this.federationRegistrationEndpoint = federationRegistrationEndpoint;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        if (this.getUserInfoEndpointURI() != null) {
            o.put((Object)"userinfo_endpoint", (Object)this.getUserInfoEndpointURI().toString());
        }
        if (this.getCheckSessionIframeURI() != null) {
            o.put((Object)"check_session_iframe", (Object)this.getCheckSessionIframeURI().toString());
        }
        if (this.getEndSessionEndpointURI() != null) {
            o.put((Object)"end_session_endpoint", (Object)this.getEndSessionEndpointURI().toString());
        }
        if (this.getFederationRegistrationEndpointURI() != null) {
            o.put((Object)"federation_registration_endpoint", (Object)this.getFederationRegistrationEndpointURI().toString());
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
        op.setBackChannelAuthenticationEndpointURI(as.getBackChannelAuthenticationEndpointURI());
        op.setPushedAuthorizationRequestEndpointURI(as.getPushedAuthorizationRequestEndpointURI());
        op.setRequestObjectEndpoint(as.getRequestObjectEndpoint());
        op.userInfoEndpoint = JSONObjectUtils.getURI(jsonObject, "userinfo_endpoint", null);
        op.checkSessionIframe = JSONObjectUtils.getURI(jsonObject, "check_session_iframe", null);
        op.endSessionEndpoint = JSONObjectUtils.getURI(jsonObject, "end_session_endpoint", null);
        op.federationRegistrationEndpoint = JSONObjectUtils.getURI(jsonObject, "federation_registration_endpoint", null);
        return op;
    }

    static {
        HashSet<String> p = new HashSet<String>(AuthorizationServerEndpointMetadata.getRegisteredParameterNames());
        p.add("userinfo_endpoint");
        p.add("check_session_iframe");
        p.add("end_session_endpoint");
        p.add("federation_registration_endpoint");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }
}

