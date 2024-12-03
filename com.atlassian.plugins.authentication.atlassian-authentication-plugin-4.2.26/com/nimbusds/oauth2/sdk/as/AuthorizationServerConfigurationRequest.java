/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.as;

import com.nimbusds.oauth2.sdk.AbstractConfigurationRequest;
import com.nimbusds.oauth2.sdk.WellKnownPathComposeStrategy;
import com.nimbusds.oauth2.sdk.id.Issuer;
import java.net.URI;
import net.jcip.annotations.Immutable;

@Immutable
public class AuthorizationServerConfigurationRequest
extends AbstractConfigurationRequest {
    public static final String OAUTH_SERVER_WELL_KNOWN_PATH = "/.well-known/oauth-authorization-server";

    public AuthorizationServerConfigurationRequest(Issuer issuer) {
        this(issuer, WellKnownPathComposeStrategy.POSTFIX);
    }

    public AuthorizationServerConfigurationRequest(Issuer issuer, WellKnownPathComposeStrategy strategy) {
        super(URI.create(issuer.getValue()), OAUTH_SERVER_WELL_KNOWN_PATH, strategy);
    }
}

