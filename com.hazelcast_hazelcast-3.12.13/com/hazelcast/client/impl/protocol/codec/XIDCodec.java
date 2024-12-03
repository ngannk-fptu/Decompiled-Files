/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.transaction.impl.xa.SerializableXID;
import javax.transaction.xa.Xid;

public final class XIDCodec {
    private XIDCodec() {
    }

    public static Xid decode(ClientMessage clientMessage) {
        int formatId = clientMessage.getInt();
        byte[] globalTransactionId = clientMessage.getByteArray();
        byte[] branchQualifier = clientMessage.getByteArray();
        return new SerializableXID(formatId, globalTransactionId, branchQualifier);
    }

    public static void encode(Xid xid, ClientMessage clientMessage) {
        clientMessage.set(xid.getFormatId());
        clientMessage.set(xid.getGlobalTransactionId());
        clientMessage.set(xid.getBranchQualifier());
    }

    public static int calculateDataSize(Xid xid) {
        int dataSize = 0;
        dataSize += 4;
        dataSize += ParameterUtil.calculateDataSize(xid.getGlobalTransactionId());
        return dataSize += ParameterUtil.calculateDataSize(xid.getBranchQualifier());
    }
}

