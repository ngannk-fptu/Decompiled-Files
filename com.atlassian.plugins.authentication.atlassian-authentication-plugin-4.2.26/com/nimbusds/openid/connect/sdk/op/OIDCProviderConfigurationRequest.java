/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.oauth2.sdk.AbstractConfigurationRequest;
import com.nimbusds.oauth2.sdk.WellKnownPathComposeStrategy;
import com.nimbusds.oauth2.sdk.id.Issuer;
import java.net.URI;
import net.jcip.annotations.Immutable;

@Immutable
public class OIDCProviderConfigurationRequest
extends AbstractConfigurationRequest {
    public static final String OPENID_PROVIDER_WELL_KNOWN_PATH = "/.well-known/openid-configuration";

    public OIDCProviderConfigurationRequest(Issuer issuer) {
        this(issuer, WellKnownPathComposeStrategy.POSTFIX);
    }

    public OIDCProviderConfigurationRequest(Issuer issuer, WellKnownPathComposeStrategy strategy) {
        super(URI.create(issuer.getValue()), OPENID_PROVIDER_WELL_KNOWN_PATH, strategy);
    }
}

