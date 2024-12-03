/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.trust;

import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.FederationMetadataType;
import com.nimbusds.openid.connect.sdk.federation.trust.InvalidEntityMetadataException;
import net.minidev.json.JSONObject;

public interface EntityMetadataValidator {
    public FederationMetadataType getType();

    public void validate(EntityID var1, JSONObject var2) throws InvalidEntityMetadataException;
}

