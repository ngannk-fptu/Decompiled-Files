/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.Signer;
import com.amazonaws.util.endpoint.RegionFromEndpointResolver;

public interface RegionFromEndpointResolverAwareSigner
extends Signer {
    public void setRegionFromEndpointResolver(RegionFromEndpointResolver var1);
}

