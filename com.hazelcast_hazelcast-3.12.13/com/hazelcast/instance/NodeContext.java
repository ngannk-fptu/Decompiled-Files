/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.cluster.Joiner;
import com.hazelcast.instance.AddressPicker;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.internal.networking.ServerSocketRegistry;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public interface NodeContext {
    public NodeExtension createNodeExtension(Node var1);

    public AddressPicker createAddressPicker(Node var1);

    public Joiner createJoiner(Node var1);

    public NetworkingService createNetworkingService(Node var1, ServerSocketRegistry var2);
}

