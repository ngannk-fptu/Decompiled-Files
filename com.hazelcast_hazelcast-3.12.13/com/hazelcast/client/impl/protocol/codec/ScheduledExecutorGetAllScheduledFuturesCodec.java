/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MemberCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorMessageType;
import com.hazelcast.client.impl.protocol.codec.ScheduledTaskHandlerCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.core.Member;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ScheduledExecutorGetAllScheduledFuturesCodec {
    public static final ScheduledExecutorMessageType REQUEST_TYPE = ScheduledExecutorMessageType.SCHEDULEDEXECUTOR_GETALLSCHEDULEDFUTURES;
    public static final int RESPONSE_TYPE = 121;

    public static ClientMessage encodeRequest(String schedulerName) {
        int requiredDataSize = RequestParameters.calculateDataSize(schedulerName);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("ScheduledExecutor.getAllScheduledFutures");
        clientMessage.set(schedulerName);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String schedulerName = null;
        parameters.schedulerName = schedulerName = clientMessage.getStringUtf8();
        return parameters;
    }

    public static ClientMessage encodeResponse(Collection<Map.Entry<Member, List<ScheduledTaskHandler>>> handlers) {
        int requiredDataSize = ResponseParameters.calculateDataSize(handlers);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(121);
        clientMessage.set(handlers.size());
        for (Map.Entry<Member, List<ScheduledTaskHandler>> handlers_item : handlers) {
            Member handlers_itemKey = handlers_item.getKey();
            List<ScheduledTaskHandler> handlers_itemVal = handlers_item.getValue();
            MemberCodec.encode(handlers_itemKey, clientMessage);
            clientMessage.set(handlers_itemVal.size());
            for (ScheduledTaskHandler handlers_itemVal_item : handlers_itemVal) {
                ScheduledTaskHandlerCodec.encode(handlers_itemVal_item, clientMessage);
            }
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        ArrayList<Map.Entry<Member, List<ScheduledTaskHandler>>> handlers = null;
        int handlers_size = clientMessage.getInt();
        handlers = new ArrayList<Map.Entry<Member, List<ScheduledTaskHandler>>>(handlers_size);
        for (int handlers_index = 0; handlers_index < handlers_size; ++handlers_index) {
            Member handlers_item_key = MemberCodec.decode(clientMessage);
            int handlers_item_val_size = clientMessage.getInt();
            ArrayList<ScheduledTaskHandler> handlers_item_val = new ArrayList<ScheduledTaskHandler>(handlers_item_val_size);
            for (int handlers_item_val_index = 0; handlers_item_val_index < handlers_item_val_size; ++handlers_item_val_index) {
                ScheduledTaskHandler handlers_item_val_item = ScheduledTaskHandlerCodec.decode(clientMessage);
                handlers_item_val.add(handlers_item_val_item);
            }
            AbstractMap.SimpleEntry handlers_item = new AbstractMap.SimpleEntry(handlers_item_key, handlers_item_val);
            handlers.add(handlers_item);
        }
        parameters.handlers = handlers;
        return parameters;
    }

    public static class ResponseParameters {
        public List<Map.Entry<Member, List<ScheduledTaskHandler>>> handlers;

        public static int calculateDataSize(Collection<Map.Entry<Member, List<ScheduledTaskHandler>>> handlers) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += 4;
            for (Map.Entry<Member, List<ScheduledTaskHandler>> handlers_item : handlers) {
                Member handlers_itemKey = handlers_item.getKey();
                List<ScheduledTaskHandler> handlers_itemVal = handlers_item.getValue();
                dataSize += MemberCodec.calculateDataSize(handlers_itemKey);
                dataSize += 4;
                for (ScheduledTaskHandler handlers_itemVal_item : handlers_itemVal) {
                    dataSize += ScheduledTaskHandlerCodec.calculateDataSize(handlers_itemVal_item);
                }
            }
            return dataSize;
        }
    }

    public static class RequestParameters {
        public static final ScheduledExecutorMessageType TYPE = REQUEST_TYPE;
        public String schedulerName;

        public static int calculateDataSize(String schedulerName) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(schedulerName);
        }
    }
}

