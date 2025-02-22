/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.nio.Address;
import java.net.UnknownHostException;

public final class AddressCodec {
    private AddressCodec() {
    }

    public static Address decode(ClientMessage clientMessage) {
        String host = clientMessage.getStringUtf8();
        int port = clientMessage.getInt();
        try {
            return new Address(host, port);
        }
        catch (UnknownHostException e) {
            throw new HazelcastException(e);
        }
    }

    public static void encode(Address address, ClientMessage clientMessage) {
        clientMessage.set(address.getHost()).set(address.getPort());
    }

    public static int calculateDataSize(Address address) {
        int dataSize = ParameterUtil.calculateDataSize(address.getHost());
        return dataSize += 4;
    }
}

