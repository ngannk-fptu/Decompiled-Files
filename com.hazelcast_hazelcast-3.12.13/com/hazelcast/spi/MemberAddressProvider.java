/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.instance.EndpointQualifier;
import java.net.InetSocketAddress;

public interface MemberAddressProvider {
    public InetSocketAddress getBindAddress();

    public InetSocketAddress getBindAddress(EndpointQualifier var1);

    public InetSocketAddress getPublicAddress();

    public InetSocketAddress getPublicAddress(EndpointQualifier var1);
}

