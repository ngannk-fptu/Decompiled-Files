/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.factories;

import com.nimbusds.openid.connect.sdk.federation.policy.MetadataPolicy;
import com.nimbusds.openid.connect.sdk.federation.policy.factories.PolicyFormulationException;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;

public interface RPMetadataPolicyFactory {
    public MetadataPolicy create(OIDCClientMetadata var1, OIDCClientInformation var2) throws PolicyFormulationException;
}

