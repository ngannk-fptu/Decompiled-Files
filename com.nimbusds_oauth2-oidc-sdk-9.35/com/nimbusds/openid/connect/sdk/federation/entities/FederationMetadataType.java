/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.federation.entities;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class FederationMetadataType
extends Identifier {
    private static final long serialVersionUID = 345842707286531482L;
    public static final FederationMetadataType OPENID_RELYING_PARTY = new FederationMetadataType("openid_relying_party");
    public static final FederationMetadataType OPENID_PROVIDER = new FederationMetadataType("openid_provider");
    public static final FederationMetadataType OAUTH_AUTHORIZATION_SERVER = new FederationMetadataType("oauth_authorization_server");
    public static final FederationMetadataType OAUTH_CLIENT = new FederationMetadataType("oauth_client");
    public static final FederationMetadataType OAUTH_RESOURCE = new FederationMetadataType("oauth_resource");
    public static final FederationMetadataType FEDERATION_ENTITY = new FederationMetadataType("federation_entity");

    public FederationMetadataType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof FederationMetadataType && this.toString().equals(object.toString());
    }
}

