/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.nio.Address;
import java.nio.channels.ServerSocketChannel;
import java.util.Map;

public interface AddressPicker {
    public void pickAddress() throws Exception;

    @Deprecated
    public Address getBindAddress();

    public Address getBindAddress(EndpointQualifier var1);

    @Deprecated
    public Address getPublicAddress();

    public Address getPublicAddress(EndpointQualifier var1);

    public Map<EndpointQualifier, Address> getPublicAddressMap();

    @Deprecated
    public ServerSocketChannel getServerSocketChannel();

    public ServerSocketChannel getServerSocketChannel(EndpointQualifier var1);

    public Map<EndpointQualifier, ServerSocketChannel> getServerSocketChannels();
}

