/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.XATransactionMessageType;
import com.hazelcast.client.impl.protocol.codec.XIDCodec;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.transaction.xa.Xid;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class XATransactionFinalizeCodec {
    public static final XATransactionMessageType REQUEST_TYPE = XATransactionMessageType.XATRANSACTION_FINALIZE;
    public static final int RESPONSE_TYPE = 100;

    public static ClientMessage encodeRequest(Xid xid, boolean isCommit) {
        int requiredDataSize = RequestParameters.calculateDataSize(xid, isCommit);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("XATransaction.finalize");
        XIDCodec.encode(xid, clientMessage);
        clientMessage.set(isCommit);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        Xid xid = null;
        parameters.xid = xid = XIDCodec.decode(clientMessage);
        boolean isCommit = false;
        parameters.isCommit = isCommit = clientMessage.getBoolean();
        return parameters;
    }

    public static ClientMessage encodeResponse() {
        int requiredDataSize = ResponseParameters.calculateDataSize();
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(100);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        return parameters;
    }

    public static class ResponseParameters {
        public static int calculateDataSize() {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final XATransactionMessageType TYPE = REQUEST_TYPE;
        public Xid xid;
        public boolean isCommit;

        public static int calculateDataSize(Xid xid, boolean isCommit) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += XIDCodec.calculateDataSize(xid);
            return ++dataSize;
        }
    }
}

