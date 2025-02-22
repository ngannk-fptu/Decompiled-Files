/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.MemberImpl;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AddressCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.core.Member;
import com.hazelcast.nio.Address;
import java.util.HashMap;
import java.util.Map;

public final class MemberCodec {
    private MemberCodec() {
    }

    public static Member decode(ClientMessage clientMessage) {
        Address address = AddressCodec.decode(clientMessage);
        String uuid = clientMessage.getStringUtf8();
        boolean liteMember = clientMessage.getBoolean();
        int attributeSize = clientMessage.getInt();
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        for (int i = 0; i < attributeSize; ++i) {
            String key = clientMessage.getStringUtf8();
            String value = clientMessage.getStringUtf8();
            attributes.put(key, value);
        }
        return new MemberImpl(address, uuid, attributes, liteMember);
    }

    public static void encode(Member member, ClientMessage clientMessage) {
        AddressCodec.encode(member.getAddress(), clientMessage);
        clientMessage.set(member.getUuid());
        clientMessage.set(member.isLiteMember());
        HashMap<String, Object> attributes = new HashMap<String, Object>(member.getAttributes());
        clientMessage.set(attributes.size());
        for (Map.Entry entry : attributes.entrySet()) {
            clientMessage.set((String)entry.getKey());
            Object value = entry.getValue();
            clientMessage.set(value.toString());
        }
    }

    public static int calculateDataSize(Member member) {
        int dataSize = AddressCodec.calculateDataSize(member.getAddress());
        dataSize += ParameterUtil.calculateDataSize(member.getUuid());
        ++dataSize;
        dataSize += 4;
        Map<String, Object> attributes = member.getAttributes();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            dataSize += ParameterUtil.calculateDataSize(entry.getKey());
            Object value = entry.getValue();
            dataSize += ParameterUtil.calculateDataSize(value.toString());
        }
        return dataSize;
    }
}

