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
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.transaction.xa.Xid;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class XATransactionCreateCodec {
    public static final XATransactionMessageType REQUEST_TYPE = XATransactionMessageType.XATRANSACTION_CREATE;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(Xid xid, long timeout) {
        int requiredDataSize = RequestParameters.calculateDataSize(xid, timeout);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(true);
        clientMessage.setOperationName("XATransaction.create");
        XIDCodec.encode(xid, clientMessage);
        clientMessage.set(timeout);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        Xid xid = null;
        parameters.xid = xid = XIDCodec.decode(clientMessage);
        long timeout = 0L;
        parameters.timeout = timeout = clientMessage.getLong();
        return parameters;
    }

    public static ClientMessage encodeResponse(String response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(104);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        String response = null;
        parameters.response = response = clientMessage.getStringUtf8();
        return parameters;
    }

    public static class ResponseParameters {
        public String response;

        public static int calculateDataSize(String response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(response);
        }
    }

    public static class RequestParameters {
        public static final XATransactionMessageType TYPE = REQUEST_TYPE;
        public Xid xid;
        public long timeout;

        public static int calculateDataSize(Xid xid, long timeout) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += XIDCodec.calculateDataSize(xid);
            return dataSize += 8;
        }
    }
}

