/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.regions;

import com.amazonaws.annotation.SdkInternalApi;
import java.util.Collection;

@SdkInternalApi
public interface RegionImpl {
    public String getName();

    public String getDomain();

    public String getPartition();

    public boolean isServiceSupported(String var1);

    public String getServiceEndpoint(String var1);

    public boolean hasHttpEndpoint(String var1);

    public boolean hasHttpsEndpoint(String var1);

    public Collection<String> getAvailableEndpoints();
}

