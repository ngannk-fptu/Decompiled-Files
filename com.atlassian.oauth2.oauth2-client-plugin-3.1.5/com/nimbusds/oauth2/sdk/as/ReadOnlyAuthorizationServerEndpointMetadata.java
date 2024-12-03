/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.as;

import java.net.URI;
import net.minidev.json.JSONObject;

public interface ReadOnlyAuthorizationServerEndpointMetadata {
    public URI getAuthorizationEndpointURI();

    public URI getTokenEndpointURI();

    public URI getRegistrationEndpointURI();

    public URI getIntrospectionEndpointURI();

    public URI getRevocationEndpointURI();

    @Deprecated
    public URI getRequestObjectEndpoint();

    public URI getPushedAuthorizationRequestEndpointURI();

    public URI getDeviceAuthorizationEndpointURI();

    public URI getBackChannelAuthenticationEndpointURI();

    @Deprecated
    public URI getBackChannelAuthenticationEndpoint();

    public JSONObject toJSONObject();
}

