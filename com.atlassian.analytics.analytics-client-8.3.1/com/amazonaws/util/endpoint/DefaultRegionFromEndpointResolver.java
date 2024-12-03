/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util.endpoint;

import com.amazonaws.util.AwsHostNameUtils;
import com.amazonaws.util.endpoint.RegionFromEndpointResolver;

public class DefaultRegionFromEndpointResolver
implements RegionFromEndpointResolver {
    @Override
    public String guessRegionFromEndpoint(String host, String serviceHint) {
        return AwsHostNameUtils.parseRegion(host, serviceHint);
    }
}

