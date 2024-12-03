/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.federation.config;

import com.nimbusds.oauth2.sdk.AbstractConfigurationRequest;
import com.nimbusds.oauth2.sdk.WellKnownPathComposeStrategy;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import net.jcip.annotations.Immutable;

@Immutable
public class FederationEntityConfigurationRequest
extends AbstractConfigurationRequest {
    public static final String OPENID_FEDERATION_ENTITY_WELL_KNOWN_PATH = "/.well-known/openid-federation";

    public FederationEntityConfigurationRequest(EntityID entityID) {
        this(entityID, WellKnownPathComposeStrategy.POSTFIX);
    }

    public FederationEntityConfigurationRequest(EntityID entityID, WellKnownPathComposeStrategy strategy) {
        super(entityID.toURI(), OPENID_FEDERATION_ENTITY_WELL_KNOWN_PATH, strategy);
    }
}

