/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.oauth2.sdk.as.ReadOnlyAuthorizationServerEndpointMetadata;
import java.net.URI;

public interface ReadOnlyOIDCProviderEndpointMetadata
extends ReadOnlyAuthorizationServerEndpointMetadata {
    public URI getUserInfoEndpointURI();

    public URI getCheckSessionIframeURI();

    public URI getEndSessionEndpointURI();

    public URI getFederationRegistrationEndpointURI();
}

